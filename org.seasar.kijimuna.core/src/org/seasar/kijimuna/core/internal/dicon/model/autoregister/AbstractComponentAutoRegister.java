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

import java.util.HashMap;
import java.util.Map;

import org.seasar.kijimuna.core.util.ClassHandler;

public abstract class AbstractComponentAutoRegister extends AbstractAutoRegister
		implements ClassHandler {

	private Map componentMap = new HashMap();

	public Map getComponentMap() {
		return componentMap;
	}

	public void setComponentMap(Map componentMap) {
		this.componentMap = componentMap;
	}

	/**
	 * 引数で指定したクラスを処理します。
	 * <p>
	 * 登録するクラスパターンにマッチしていれば登録し、 そうでなければ何も行いません。
	 * 
	 * @param packageName
	 *            パッケージ名
	 * @param shortClassName
	 *            クラス名
	 */
	public void processClass(String packageName, String shortClassName) {
		if (isIgnore(packageName, shortClassName)) {
			return;
		}
		if (isMatch(packageName, shortClassName)) {
			String clazz = packageName + "." + shortClassName;
			String name = getAutoNaming().defineName(packageName, shortClassName);
			getComponentMap().put(name, clazz);
		}
	}

}
