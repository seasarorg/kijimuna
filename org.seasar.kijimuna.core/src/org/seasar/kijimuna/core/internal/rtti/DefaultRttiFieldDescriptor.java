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

import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiFieldDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultRttiFieldDescriptor extends AbstractRttiValuableDescriptor implements
		IRttiFieldDescriptor {

	private boolean fFinal;
	private boolean fStatic;
	private boolean fEnum;

	public DefaultRttiFieldDescriptor(IRtti parent, String name, IRtti type,
			boolean fFinal, boolean fStatic, boolean fEnum) {
		super(parent, name, type);
		this.fFinal = fFinal;
		this.fStatic = fStatic;
		this.fEnum = fEnum;
	}

	public boolean equals(Object test) {
		if (test instanceof IRttiFieldDescriptor) {
			IRttiFieldDescriptor desc = (IRttiFieldDescriptor) test;
			return getParent().equals(desc.getParent())
					&& getName().equals(desc.getName());
		}
		return false;
	}

	public boolean isFinal() {
		return fFinal;
	}

	public boolean isStatic() {
		return fStatic;
	}

	public boolean isEnum() {
		return fEnum;
	}

}
