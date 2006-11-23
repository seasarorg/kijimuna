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

import org.eclipse.jdt.core.IMember;

import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultRttiConstructorDescriptor extends AbstractRttiInvokableDescriptor
		implements IRttiConstructorDesctiptor {

	private boolean defaultConstructor;

	public DefaultRttiConstructorDescriptor(IMember member, IRtti parent,
			boolean defaultConstructor) {
		super(member, parent);
		this.defaultConstructor = defaultConstructor;
	}

	public IMember getMember() {
		if (defaultConstructor) {
			return getParent().getType();
		}
		return super.getMember();
	}

	public boolean isDefaultConstructor() {
		return defaultConstructor;
	}

}
