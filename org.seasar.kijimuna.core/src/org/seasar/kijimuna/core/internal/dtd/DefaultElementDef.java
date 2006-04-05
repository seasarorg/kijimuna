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

import java.util.LinkedHashMap;

import org.seasar.kijimuna.core.dtd.IAttributeDef;
import org.seasar.kijimuna.core.dtd.IElementDef;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultElementDef implements IElementDef {
	
	private String name;
	private LinkedHashMap elementMap = new LinkedHashMap();
	private LinkedHashMap attributeMap = new LinkedHashMap();
	private boolean empty;
	private boolean pcdata;

	public DefaultElementDef(String name, boolean empty, boolean pcdata) {
		this.name = name;
		this.empty = empty;
		this.pcdata = pcdata;
	}

	public String getName() {
		return name;
	}

	public IElementDef[] getElements() {
		return (IElementDef[]) elementMap.values().toArray(new IElementDef[elementMap.size()]);
	}

	public void addElement(IElementDef childElement) {
		elementMap.put(childElement.getName(), childElement);
	}

	public IAttributeDef[] getAttributes() {
		return (IAttributeDef[]) attributeMap.values().toArray(new IAttributeDef[attributeMap.size()]);
	}

	public void addAttribute(IAttributeDef attribute) {
		attributeMap.put(attribute.getName(), attribute);
	}

	public IAttributeDef getAttribute(String name) {
		return (IAttributeDef) attributeMap.get(name);
	}

    public boolean hasPCData() {
        return pcdata;
    }

    public boolean isEmpty() {
        return empty;
    }

}
