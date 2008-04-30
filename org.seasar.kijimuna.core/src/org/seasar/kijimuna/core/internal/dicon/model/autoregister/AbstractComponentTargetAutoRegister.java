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
package org.seasar.kijimuna.core.internal.dicon.model.autoregister;

import java.util.Iterator;
import java.util.List;

import org.seasar.kijimuna.core.internal.dicon.model.ComponentElement;
import org.seasar.kijimuna.core.internal.dicon.model.ContainerElement;
import org.seasar.kijimuna.core.util.ClassUtil;

/**
 * コンポーネントを対象にした自動登録を行うための抽象クラスです。
 * 
 */
public abstract class AbstractComponentTargetAutoRegister extends AbstractAutoRegister {

	protected ContainerElement containerElement;

	public void setContainerElement(ContainerElement containerElement) {
		this.containerElement = containerElement;
	}

	public void registerAll() {
		List componentList = containerElement.getComponentList();
		for (Iterator componentListIterator = componentList.iterator(); componentListIterator
				.hasNext();) {
			ComponentElement componentElement = (ComponentElement) componentListIterator
					.next();
			if (isAppliedComponent(componentElement)) {
				register(componentElement);
			}
		}
	}

	/**
	 * {@link ComponentElement}を登録します。
	 * 
	 * @param componentElement
	 */
	protected abstract void register(ComponentElement componentElement);

	/**
	 * 処理対象のコンポーネントかどうか返します。
	 * 
	 * @param cd
	 * @return 処理対象のコンポーネントかどうか
	 */
	protected boolean isAppliedComponent(final ComponentElement cd) {
		final String componentClass = cd.getComponentClassName();
		if (componentClass == null) {
			return false;
		}

		final String packageName = ClassUtil.getPackageName(componentClass);
		final String shortClassName = ClassUtil.getShortClassName(componentClass);
		for (int i = 0; i < getClassPatternSize(); ++i) {
			final ClassPattern cp = getClassPattern(i);
			if (isIgnore(packageName, shortClassName)) {
				return false;
			}
			if (cp.isAppliedPackageName(packageName)
					&& cp.isAppliedShortClassName(shortClassName)) {
				return true;
			}
		}
		return false;
	}

}
