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
package org.seasar.kijimuna.core.internal.dtd;

import org.seasar.kijimuna.core.dtd.IAttributeDef;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 */
public class DefaultAttributeDef implements IAttributeDef {

	private String name;
	private String decl;
	private String defaultValue;
	private String[] items;

	public DefaultAttributeDef(String name, String decl, String defaultValue,
			String[] items) {
		this.name = name;
		this.decl = decl;
		this.defaultValue = defaultValue;
		this.items = items;
	}

	public String getDecl() {
		return decl;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String[] getItems() {
		return items;
	}

	public String getName() {
		return name;
	}

}
