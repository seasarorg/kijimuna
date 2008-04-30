/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.kijimuna.core.internal.dicon.refactor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ISharableParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.ModelManager;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.internal.dicon.model.InitMethodElement;
import org.seasar.kijimuna.core.parser.Attribute;
import org.seasar.kijimuna.core.util.ClassUtil;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 * @author kentaro Matsumae
 */
public class KijimunaRenameParticipant extends RenameParticipant implements
		ISharableParticipant {

	private DiconNature nature;

	private Set diconList = new HashSet();

	// key=element, value=newName
	private Map renameElements = new HashMap();

	public String getName() {
		return KijimunaCore.getResourceString("dicon.refactor.TypeRenameParticipant.1");
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	protected boolean initialize(Object element) {
		try {
			IJavaElement targetElement = (IJavaElement) element;
			IProject prj = targetElement.getUnderlyingResource().getProject();
			nature = DiconNature.getInstance(prj);

			addElement(element, (RenameArguments) getArguments());
			return true;

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addElement(Object element, RefactoringArguments arguments) {
		IJavaElement javaElement = (IJavaElement) element;
		String newName = ((RenameArguments) arguments).getNewName();
		renameElements.put(javaElement, newName);

		try {
			if (javaElement instanceof IPackageFragment) {
				// 名前変更対象パッケージのクラスに関連するdiconファイルをすべて取得
				IPackageFragment pkgFragment = (IPackageFragment) javaElement;
				ICompilationUnit[] cUnits = pkgFragment.getCompilationUnits();

				for (int i = 0; i < cUnits.length; i++) {
					ICompilationUnit cUnit = cUnits[i];
					IFile file = (IFile) cUnit.getUnderlyingResource();
					IFile[] dicon = nature.getModel().getRelatedFiles(file, true);
					diconList.addAll(Arrays.asList(dicon));
				}
			} else {
				// 名前変更対象クラスに関連するdiconファイルをすべて取得
				IFile file = (IFile) javaElement.getUnderlyingResource();
				IFile[] dicon = nature.getModel().getRelatedFiles(file, true);
				diconList.addAll(Arrays.asList(dicon));
			}
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		if (!getArguments().getUpdateReferences()) {
			return null;
		}
		CompositeChange compChanges = new CompositeChange("Dicon Files");
		compChanges.markAsSynthetic();

		for (Iterator diconItr = diconList.iterator(); diconItr.hasNext();) {
			IFile dicon = (IFile) diconItr.next();
			ModelManager manager = nature.getModel();
			IContainerElement model = manager.getContainer(dicon, pm);

			MultiTextEdit multiEdit = new MultiTextEdit();

			for (Iterator renameItr = renameElements.keySet().iterator(); renameItr
					.hasNext();) {
				IJavaElement element = (IJavaElement) renameItr.next();
				String newName = (String) renameElements.get(element);

				if (element instanceof IPackageFragment) {
					createPacageRenameChange((IPackageFragment) element, newName, model,
							multiEdit, pm);
				} else if (element instanceof IType) {
					createTypeRenameChange((IType) element, newName, model, multiEdit, pm);
				} else if (element instanceof IMethod) {
					createMethodRenameChange((IMethod) element, newName, model,
							multiEdit, pm);
				} else if (element instanceof IField) {
					createFieldRenameChange((IField) element, newName, model, multiEdit,
							pm);
				} else {
					// ignore
				}
			}
			TextFileChange change = new TextFileChange("", dicon);
			change.setEdit(multiEdit);
			compChanges.add(change);

		}
		return compChanges;
	}

	private void createTypeRenameChange(IType targetType, String newName,
			IContainerElement model, MultiTextEdit multiEdit, IProgressMonitor pm) {

		String oldFQCN = targetType.getFullyQualifiedName();
		String pkgName = targetType.getPackageFragment().getElementName();
		String newFQCN = ClassUtil.concatName(pkgName, newName);
		createFQCNRenameChange(newFQCN, oldFQCN, false, multiEdit, model, pm);

	}

	private void createPacageRenameChange(IPackageFragment pkg, String newPkgName,
			IContainerElement model, MultiTextEdit multiEdit, IProgressMonitor pm) {

		String oldPkgName = pkg.getElementName();
		createFQCNRenameChange(newPkgName, oldPkgName, true, multiEdit, model, pm);
	}

	private void createFQCNRenameChange(String newValue, String oldValue,
			boolean changePkgName, MultiTextEdit multiEdit, IContainerElement model,
			IProgressMonitor pm) {

		List components = model.getComponentList();

		for (Iterator iterator = components.iterator(); iterator.hasNext();) {
			IComponentElement component = (IComponentElement) iterator.next();
			String fqcn = component.getComponentClassName();

			if (fqcn == null) {
				continue;
			}
			if (changePkgName) {
				// パッケージ名変更
				String[] result = ClassUtil.splitFQCN(fqcn);
				String pkgName = result[0];
				String typeName = result[1];

				if (pkgName.equals(oldValue)) {
					String newFQCN = ClassUtil.concatName(newValue, typeName);
					ReplaceEdit replaceEdit = createClassAttrReplaceEdit(component,
							newFQCN);
					multiEdit.addChild(replaceEdit);
				}
			} else {
				// クラス名変更
				if (fqcn.equals(oldValue)) {
					ReplaceEdit replaceEdit = createClassAttrReplaceEdit(component,
							newValue);
					multiEdit.addChild(replaceEdit);
				}
			}
		}
	}

	private void createMethodRenameChange(IMethod targetMethod, String newName,
			IContainerElement model, MultiTextEdit multiEdit, IProgressMonitor pm) {

		String oldName = targetMethod.getElementName();
		String typeFQCN = targetMethod.getDeclaringType().getFullyQualifiedName();

		List components = model.getComponentList();
		for (Iterator componentsItr = components.iterator(); componentsItr.hasNext();) {
			IComponentElement component = (IComponentElement) componentsItr.next();
			String className = component.getComponentClassName();

			if (className != null && className.equals(typeFQCN)) {
				createSetterInjectionEdit(newName, oldName, multiEdit, component);
				createMethodInjectionEdit(newName, oldName, multiEdit, component);
			}
		}
	}

	private void createSetterInjectionEdit(String newName, String oldName,
			MultiTextEdit edit, IComponentElement component) {

		if (!newName.startsWith("set")) {
			return;
		}
		List props = component.getPropertyList();

		for (Iterator propsItr = props.iterator(); propsItr.hasNext();) {
			IPropertyElement prop = (IPropertyElement) propsItr.next();
			String propName = prop.getPropertyName();
			String setterName = "set" + StringUtils.capitalize(propName);

			if (setterName.equals(oldName)) {
				String newPropName = newName.substring(3);
				newPropName = StringUtils.decapitalize(newPropName);

				ReplaceEdit replaceEdit = createPropNameReplaceEdit(prop, newPropName);
				edit.addChild(replaceEdit);
			}
		}
	}

	private void createMethodInjectionEdit(String newName, String oldName,
			MultiTextEdit edit, IComponentElement component) {

		List initMethods = component.getInitMethodList();
		for (Iterator iterator = initMethods.iterator(); iterator.hasNext();) {
			InitMethodElement initMethod = (InitMethodElement) iterator.next();
			String methodName = initMethod.getMethodName();

			if (methodName.equals(oldName)) {
				ReplaceEdit replaceEdit = createInitMethodNameReplaceEdit(initMethod,
						newName);
				edit.addChild(replaceEdit);
			}
		}
	}

	private void createFieldRenameChange(IField targetField, String newName,
			IContainerElement model, MultiTextEdit multiEdit, IProgressMonitor pm) {

		String oldName = targetField.getElementName();
		String fqcn = targetField.getDeclaringType().getFullyQualifiedName();

		List components = model.getComponentList();

		for (Iterator componentsItr = components.iterator(); componentsItr.hasNext();) {
			IComponentElement component = (IComponentElement) componentsItr.next();
			String className = component.getComponentClassName();

			if (className != null && className.equals(fqcn)) {
				List props = component.getPropertyList();

				for (Iterator propsItr = props.iterator(); propsItr.hasNext();) {
					IPropertyElement prop = (IPropertyElement) propsItr.next();
					String propName = prop.getPropertyName();

					if (propName.equals(oldName)) {
						ReplaceEdit replaceEdit = createPropNameReplaceEdit(prop, newName);
						multiEdit.addChild(replaceEdit);
					}
				}
			}
		}
	}

	private ReplaceEdit createClassAttrReplaceEdit(IComponentElement component,
			String newClassName) {
		Attribute classAttr = component.getAttributeObject("class");
		int offset = classAttr.getOffset();
		int length = classAttr.getLength();
		String newAttrDef = "class=\"" + newClassName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}

	private ReplaceEdit createPropNameReplaceEdit(IPropertyElement prop,
			String newPropName) {
		Attribute nameAttr = prop.getAttributeObject("name");
		int offset = nameAttr.getOffset();
		int length = nameAttr.getLength();
		String newAttrDef = "name=\"" + newPropName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}

	private ReplaceEdit createInitMethodNameReplaceEdit(InitMethodElement initMethod,
			String newName) {
		Attribute nameAttr = initMethod.getAttributeObject("name");
		int offset = nameAttr.getOffset();
		int length = nameAttr.getLength();
		String newAttrDef = "name=\"" + newName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}

}
