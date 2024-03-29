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

import org.eclipse.jdt.core.IJavaProject;

/**
 * IAutoRegisterの実装クラスのための基底クラス。�̂��߂̊��N���X�B
 */
public abstract class AbstractAutoRegister implements IAutoRegister {

	private IJavaProject project;
	private List classPatterns = new ArrayList();
	private List ignoreClassPatterns = new ArrayList();
	private AutoNaming naming = new DefaultAutoNaming();

	public void setProject(IJavaProject project) {
		this.project = project;
	}

	public IJavaProject getProject() {
		return this.project;
	}

	public void setAutoNaming(AutoNaming naming) {
		if (naming != null) {
			this.naming = naming;
		}
	}

	public AutoNaming getAutoNaming() {
		return this.naming;
	}

	public void addClassPattern(String packageName, String shortClassNames) {
		classPatterns.add(new ClassPattern(packageName, shortClassNames));
	}

	public List getClassPatterns() {
		return this.classPatterns;
	}

	public void addIgnoreClassPattern(String packageName, String shortClassNames) {
		ignoreClassPatterns.add(new ClassPattern(packageName, shortClassNames));
	}

	public List getIgnoreClassPatterns() {
		return this.ignoreClassPatterns;
	}

	/**
	 * 追加されているClassPatternの数を返します。
	 * 
	 * @return
	 */
	public int getClassPatternSize() {
		return classPatterns.size();
	}

	/**
	 * ClassPatternを返します。
	 * 
	 * @param index
	 * @return
	 */
	public ClassPattern getClassPattern(int index) {
		return (ClassPattern) classPatterns.get(index);
	}

	/**
	 * 引数で指定したクラスが登録するクラスパターンにマッチするかどうかを調べます。
	 */
	protected boolean isMatch(String packageName, String shortClassName) {
		if (classPatterns.isEmpty()) {
			return false;
		}

		for (int i = 0; i < classPatterns.size(); ++i) {
			ClassPattern cp = (ClassPattern) classPatterns.get(i);
			if (cp.isAppliedPackageName(packageName)
					&& cp.isAppliedShortClassName(shortClassName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 引数で指定したクラスが無視するクラスパターンにマッチするかどうかを調べます。
	 */
	protected boolean isIgnore(String packageName, String shortClassName) {
		if (ignoreClassPatterns.isEmpty()) {
			return false;
		}
		for (int i = 0; i < ignoreClassPatterns.size(); ++i) {
			ClassPattern cp = (ClassPattern) ignoreClassPatterns.get(i);
			if (!cp.isAppliedPackageName(packageName)) {
				continue;
			}
			if (cp.isAppliedShortClassName(shortClassName)) {
				return true;
			}
		}
		return false;
	}

}
