/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.kijimuna.core.internal.dicon.model.autoregister;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

import org.seasar.kijimuna.core.KijimunaCore;

/**
 * <code>ComponentAutoRegister</code>の動作をエミュレートするIAutoRegisterの実装。
 */
public class ComponentAutoRegister extends AbstractComponentAutoRegister {

	private List referenceClasses = new ArrayList();
	private AbstractComponentAutoRegister register;

	public void addReferenceClass(String className) {
		referenceClasses.add(className);
	}

	public void registerAll() {
		try {
			for (int i = 0; i < referenceClasses.size(); i++) {
				String referenceClass = (String) referenceClasses.get(i);
				IType type = getProject().findType(referenceClass);
				if (!type.exists()) {
					return;
				}
				IJavaElement parent = type.getPackageFragment().getParent();
				if (parent.getElementName().endsWith(".jar")) {
					// JARの場合
					register = new JarComponentAutoRegister();
					((JarComponentAutoRegister) register)
							.setReferenceClass(referenceClass);
				} else {
					// JARじゃない場合
					register = new FileSystemComponentAutoRegister();
				}

				register.setProject(getProject());

				List classPatterns = getClassPatterns();
				for (int j = 0; j < classPatterns.size(); j++) {
					ClassPattern pattern = (ClassPattern) classPatterns.get(j);
					register.addClassPattern(pattern.getPackageName(), pattern
							.getShortClassNames());
				}

				List ignorePatterns = getIgnoreClassPatterns();
				for (int j = 0; j < ignorePatterns.size(); j++) {
					ClassPattern pattern = (ClassPattern) ignorePatterns.get(j);
					register.addIgnoreClassPattern(pattern.getPackageName(), pattern
							.getShortClassNames());
				}

				register.registerAll();
				setComponentMap(register.getComponentMap());
			}
		} catch (Exception ex) {
			KijimunaCore.reportException(ex);
		}
	}

}
