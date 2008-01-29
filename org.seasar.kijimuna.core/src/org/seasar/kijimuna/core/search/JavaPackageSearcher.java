/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.kijimuna.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameRequestor;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.search.DefaultPackageRequestor;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class JavaPackageSearcher {

	private IProject project;

	public JavaPackageSearcher(IProject project) {
		this.project = project;
	}

	public void searchPackagesAndTypes(String prefix, IPackageRequestor requestor) {
		int lastDot = prefix.lastIndexOf('.');
		String packageName;
		String trimedPrefix = "";
		if (lastDot > 0) {
			packageName = prefix.substring(0, lastDot);
			trimedPrefix = prefix.substring(lastDot + 1);
		} else if (lastDot == -1) {
			packageName = prefix;
		} else {
			return;
		}
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();

			// search packages and types
			for (int i = 0; i < roots.length; i++) {
				IJavaElement[] elements = roots[i].getChildren();
				for (int k = 0; k < elements.length; k++) {
					if (elements[k] instanceof IPackageFragment) {
						IPackageFragment pack = (IPackageFragment) elements[k];
						String currentName = pack.getElementName();
						if (StringUtils.existValue(currentName)
								&& currentName.toLowerCase().startsWith(
										prefix.toLowerCase())) {
							requestor.acceptPackage(pack, roots[i].isArchive());
						} else if (currentName.equals(packageName)) {
							if (roots[i].isArchive()) {
								handleClassFiles(pack, requestor, trimedPrefix);
							} else {
								handleCompilationUnits(pack, requestor, trimedPrefix);
							}
						}
					}
				}
			}

			// search inner type
			if (lastDot > 0) {
				int lastDollar = prefix.lastIndexOf('$');
				String typeName;
				if (lastDollar > lastDot) {
					typeName = prefix.substring(0, lastDollar);
				} else {
					typeName = packageName;
				}
				IType type = javaProject.findType(typeName);
				if (type != null && Flags.isPublic(type.getFlags())) {
					IJavaElement[] children = type.getChildren();
					for (int i = 0; i < children.length; i++) {
						if (children[i] instanceof IType) {
							IType innerType = (IType) children[i];
							int flag = innerType.getFlags();
							if (Flags.isPublic(flag)) {
								requestor.acceptType(innerType);
							}
						}
					}
				}
			}

			// direct search types
			if (lastDot == -1 && prefix.length() > 0) {
				IPackageFragmentRoot[] pkgs = javaProject.getAllPackageFragmentRoots();
				char[][] typeNames = {prefix.toCharArray()};
				
				final ArrayList res = new ArrayList();
				TypeNameRequestor typeNameRequestor = new TypeNameRequestor(){
					public void acceptType(
							int modifiers, 
							char[] packageName,
							char[] simpleTypeName, 
							char[][] enclosingTypeNames,
							String path) {
						
						if (enclosingTypeNames.length == 0 && Flags.isPublic(modifiers)) {
							StringBuffer fqcn = new StringBuffer();
							if(packageName.length > 0){
								fqcn.append(packageName).append(".");
							}
							fqcn.append(simpleTypeName);
							res.add(fqcn.toString());
						}
					}
				};
				//TODO:3.2との互換性をとるため,deprecatedメソッドを使用した。
				new SearchEngine().searchAllTypeNames(
						null, 
						prefix.toCharArray(),
						SearchPattern.R_PREFIX_MATCH,
						IJavaSearchConstants.CLASS,
						SearchEngine.createJavaSearchScope(pkgs),
						typeNameRequestor,
						IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						null);
				
				for (Iterator iterator = res.iterator(); iterator.hasNext();) {
					String fqcn = (String) iterator.next();
					IType type = javaProject.findType(fqcn);
					requestor.acceptType(type);
				}
			}
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
	}

	private void handleClassFiles(IPackageFragment pack, IPackageRequestor requestor,
			String trimedPrefix) {
		try {
			IClassFile[] classes = pack.getClassFiles();
			for (int i = 0; i < classes.length; i++) {
				IType type = classes[i].getType();
				String fullName = type.getFullyQualifiedName();
				if (fullName.indexOf('$') == -1) {
					String currentName = type.getElementName();
					if (currentName.toLowerCase().startsWith(trimedPrefix.toLowerCase())) {
						int flag = type.getFlags();
						if (Flags.isPublic(flag)) {
							requestor.acceptType(type);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
	}

	private void handleCompilationUnits(IPackageFragment pack,
			IPackageRequestor requestor, String trimedPrefix) {
		try {
			ICompilationUnit[] units = pack.getCompilationUnits();
			for (int i = 0; i < units.length; i++) {
				IType[] types = units[i].getAllTypes();
				for (int k = 0; k < types.length; k++) {
					String fullName = types[k].getFullyQualifiedName();
					if (fullName.indexOf('$') == -1) {
						String currentName = types[k].getElementName();
						if (currentName.toLowerCase().startsWith(
								trimedPrefix.toLowerCase())) {
							int flag = types[k].getFlags();
							if (Flags.isPublic(flag)) {
								requestor.acceptType(types[k]);
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
	}

	public IPackageRequestor createDefaultRequestor(Collection collection) {
		return new DefaultPackageRequestor(collection);
	}

}
