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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class KijimunaRenameParticipant extends RenameParticipant {

	private IFile[] dicons;
	private IJavaElement targetElement;
	private DiconNature nature;

	public String getName() {
		return KijimunaCore.getResourceString("dicon.refactor.TypeRenameParticipant.1");
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	protected boolean initialize(Object element) {
		try{
			targetElement = (IJavaElement) element;
			IFile file = null;
			
			if (targetElement instanceof IPackageFragment) {
				IPackageFragment pkgFragment = (IPackageFragment) element;
				ICompilationUnit[] cUnits = pkgFragment.getCompilationUnits();
				if(cUnits.length == 0){
					return false;
				}else{
					file = (IFile) cUnits[0].getUnderlyingResource();
				}
			}else{
				file = (IFile) targetElement.getUnderlyingResource();	
			}
			IProject prj = file.getProject();
			nature = DiconNature.getInstance(prj);
			dicons = nature.getModel().getRelatedFiles(file, true);
			return dicons.length > 0;
			
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
		return false;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		if (!getArguments().getUpdateReferences()){
			return null;
		}
		List renameChanges = null;
		
		//TODO: xxxChange()内リファクタリング
		if (targetElement instanceof IPackageFragment) {
			renameChanges = createPacageRenameChange(pm);
		}else if (targetElement instanceof IType) {
			renameChanges = createTypeRenameChange(pm);
		}else if (targetElement instanceof IMethod) {
			renameChanges = createMethodRenameChange(pm);
		}else if (targetElement instanceof IField){
			renameChanges = createFieldRenameChange(pm);
		}else{
			//ignore
		}
		
		if(renameChanges != null && renameChanges.size() > 0){
			CompositeChange changes = new CompositeChange("Dicon Files");
			changes.markAsSynthetic();
			
			for (Iterator iterator = renameChanges.iterator(); iterator.hasNext();) {
				changes.add((Change) iterator.next());
			}
			return changes;
		}
		return null;
	}

	private List createTypeRenameChange(IProgressMonitor pm) {
		IType targetType = (IType) targetElement;
		String oldFQCN = targetType.getFullyQualifiedName();
		String newName = getArguments().getNewName();
		String pkgName = targetType.getPackageFragment().getElementName();
		String newFQCN = ClassUtil.concatName(pkgName, newName);
		return createFQCNRenameChange(newFQCN, oldFQCN, false, pm);
	}

	private List createPacageRenameChange(IProgressMonitor pm) {
		IPackageFragment pkg = (IPackageFragment) targetElement;
		String oldPkgName = pkg.getElementName();
		String newPkgName = getArguments().getNewName();
		return createFQCNRenameChange(newPkgName, oldPkgName, true, pm);
	}
	
	private List createFQCNRenameChange(String newValue, String oldValue, 
			boolean changePkgName,IProgressMonitor pm) {
	
		List changes = new ArrayList();
		
		for (int i = 0; i < dicons.length; i++) {
			IFile dicon = dicons[i];
			ModelManager manager = nature.getModel();
			IContainerElement model = manager.getContainer(dicon, pm);
	
			MultiTextEdit edit = new MultiTextEdit();
			
			List components = model.getComponentList();
			for (Iterator iterator = components.iterator(); iterator.hasNext();) {
				IComponentElement component = (IComponentElement) iterator.next();
				String fqcn = component.getComponentClassName();
				if(fqcn == null){
					continue;
				}
				if(changePkgName){
					//パッケージ名変更
					String[] result = ClassUtil.splitFQCN(fqcn);
					String pkgName = result[0];
					String typeName = result[1];
					
					if(pkgName.equals(oldValue)){
						String newFQCN = ClassUtil.concatName(newValue, typeName);
						ReplaceEdit replaceEdit = createClassAttrReplaceEdit(component, newFQCN);
						edit.addChild(replaceEdit);
					}
				}else{
					//クラス名変更
					if(fqcn.equals(oldValue)){
						ReplaceEdit replaceEdit = createClassAttrReplaceEdit(component, newValue);
						edit.addChild(replaceEdit);
					}
				}
			}
			if(edit.getChildrenSize() > 0){
				TextFileChange change = new TextFileChange("", dicon);
				change.setEdit(edit);
				changes.add(change);
			}
		}
		return changes;
	}

	private List createMethodRenameChange(IProgressMonitor pm) {
		IMethod targetMethod = (IMethod) targetElement;
		String newName = getArguments().getNewName();
		String oldName = targetMethod.getElementName();
		String typeFQCN = targetMethod.getDeclaringType().getFullyQualifiedName();
		
		List changes = new ArrayList();
		
		for (int i = 0; i < dicons.length; i++) {
			IFile dicon = dicons[i];
			ModelManager manager = nature.getModel();
			IContainerElement model = manager.getContainer(dicon, pm);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			List components = model.getComponentList();
			for (Iterator componentsItr = components.iterator(); componentsItr.hasNext();) {
				IComponentElement component = (IComponentElement) componentsItr.next();
				String className = component.getComponentClassName();
				
				if(className != null && className.equals(typeFQCN)){
					createSetterInjectionEdit(newName, oldName, edit, component);
					createMethodInjectionEdit(newName, oldName, edit, component);
				}
			}
			if(edit.getChildrenSize() > 0){
				TextFileChange change = new TextFileChange("", dicon);
				change.setEdit(edit);
				changes.add(change);
			}
		}
		return changes;
	}

	private List createFieldRenameChange(IProgressMonitor pm) {
		IField targetField = (IField) targetElement;
		String oldName = targetField.getElementName();
		String fqcn = targetField.getDeclaringType().getFullyQualifiedName();
		String newName = getArguments().getNewName();
		
		List changes = new ArrayList();
		for (int i = 0; i < dicons.length; i++) {
			IFile dicon = dicons[i];
			ModelManager manager = nature.getModel();
			IContainerElement model = manager.getContainer(dicon, pm);
			
			MultiTextEdit edit = new MultiTextEdit();
			List components = model.getComponentList();
			
			for (Iterator componentsItr = components.iterator(); componentsItr.hasNext();) {
				IComponentElement component = (IComponentElement) componentsItr.next();
				String className = component.getComponentClassName();
				
				if(className != null && className.equals(fqcn)){
					List props = component.getPropertyList();
					
					for (Iterator propsItr = props.iterator(); propsItr.hasNext();) {
						IPropertyElement prop = (IPropertyElement) propsItr.next();
						String propName = prop.getPropertyName();
						
						if(propName.equals(oldName)){
							ReplaceEdit replaceEdit = createPropNameReplaceEdit(prop, newName); 
							edit.addChild(replaceEdit);
						}
					}
				}
			}
			if(edit.getChildrenSize() > 0){
				TextFileChange change = new TextFileChange("", dicon);
				change.setEdit(edit);
				changes.add(change);
			}
		}
		return changes;
	}

	private void createSetterInjectionEdit(String newName, String oldName,
			MultiTextEdit edit, IComponentElement component) {
	
		if(!newName.startsWith("set")){
			return;
		}
		List props = component.getPropertyList();
		
		for (Iterator propsItr = props.iterator(); propsItr.hasNext();) {
			IPropertyElement prop = (IPropertyElement) propsItr.next();
			String propName = prop.getPropertyName();
			String setterName = "set" + StringUtils.capitalize(propName);
			
			if(setterName.equals(oldName)){
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
			
			if(methodName.equals(oldName)){
				ReplaceEdit replaceEdit = createInitMethodNameReplaceEdit(initMethod, newName); 
				edit.addChild(replaceEdit);
			}
		}
	}

	private ReplaceEdit createClassAttrReplaceEdit(IComponentElement component, String newClassName) {
		Attribute classAttr = component.getAttributeObject("class");
		int offset = classAttr.getOffset();
		int length = classAttr.getLength();
		String newAttrDef = "class=\"" + newClassName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}
	
	private ReplaceEdit createPropNameReplaceEdit(IPropertyElement prop, String newPropName) {
		Attribute nameAttr = prop.getAttributeObject("name");
		int offset = nameAttr.getOffset();
		int length = nameAttr.getLength();
		String newAttrDef = "name=\"" + newPropName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}

	private ReplaceEdit createInitMethodNameReplaceEdit(InitMethodElement initMethod, String newName) {
		Attribute nameAttr = initMethod.getAttributeObject("name");
		int offset = nameAttr.getOffset();
		int length = nameAttr.getLength();
		String newAttrDef = "name=\"" + newName + "\"";
		return new ReplaceEdit(offset, length, newAttrDef);
	}
}
