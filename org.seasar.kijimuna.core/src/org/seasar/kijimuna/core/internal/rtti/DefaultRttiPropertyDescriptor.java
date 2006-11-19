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
package org.seasar.kijimuna.core.internal.rtti;

import java.util.HashSet;
import java.util.Set;

import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultRttiPropertyDescriptor extends AbstractRttiValuableDescriptor
		implements IRttiPropertyDescriptor {

	private Set writableType;
	private boolean readable;
	private boolean writable;

	public DefaultRttiPropertyDescriptor(IRtti parent, String name, IRtti type,
			boolean isReadable) {
		super(parent, name, type);
		writableType = new HashSet();
		if (isReadable) {
			readable = true;
		} else {
			writable = true;
			writableType.add(type);
		}
	}

	public void doReadable(IRtti type) {
		if (!readable || type.getQualifiedName().equals("boolean")) {
			setType(type);
		}
		readable = true;
		writable = writableType.contains(type);
	}

	public void doWritable(IRtti type) {
		if ((readable) && getType().equals(type)) {
			writable = true;
		}
		writableType.add(type);
	}
	
	public int hashCode() {
		int ret = 17;
		ret = 37 * ret + getParent().hashCode();
		ret = 37 * ret + getName().hashCode();
		return ret;
	}
	
	public boolean equals(Object test) {
		if (test instanceof IRttiPropertyDescriptor) {
			IRttiPropertyDescriptor desc = (IRttiPropertyDescriptor) test;
			return getParent().equals(desc.getParent())
					&& getName().equals(desc.getName());
		}
		return false;
	}

	public boolean isReadable() {
		return readable;
	}

	public boolean isWritable() {
		return writable;
	}

	public int compareTo(Object test) {
		if (test instanceof IRttiPropertyDescriptor) {
			IRttiPropertyDescriptor prop = (IRttiPropertyDescriptor) test;
			return getName().compareTo(prop.getName());
		}
		return 1;
	}

}
