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
package org.seasar.kijimuna.core.internal.dicon.info;

import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiWrapper;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public final class ComponentKeyRtti extends RttiWrapper implements IComponentKey {

	private int tooMany;

	public ComponentKeyRtti(IRtti rtti) {
		super(rtti);
	}

	public void setTooMany(int tooMany) {
		this.tooMany = tooMany;
	}

	public int getTooMany() {
		return tooMany;
	}

	public String getDisplayName() {
		return getQualifiedName();
	}

	public int getKeyType() {
		return INTERFACE;
	}

	public int compareTo(Object test) {
		if (test instanceof IComponentKey) {
			IComponentKey testKey = (IComponentKey) test;
			if (testKey.getKeyType() == STRING) {
				return 1;
			}
			return getDisplayName().compareTo(testKey.getDisplayName());
		}
		return 0;
	}

}
