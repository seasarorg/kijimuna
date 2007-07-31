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
package org.seasar.kijimuna.core.internal.dicon.autobinding;

import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentNotFoundRtti;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;

public class AutoBindingComponentProvider {

	private final IContainerElement container;

	public AutoBindingComponentProvider(IContainerElement container) {
		if (container == null) {
			throw new IllegalArgumentException();
		}
		this.container = container;
	}
	
	public IRtti getAutoBindingComponentRtti(IRttiPropertyDescriptor propDesc) {
		IRtti rtti = findByComponentName(propDesc);
		return rtti != null ? rtti : findByPropertyType(propDesc);
	}
	
	private IRtti findByComponentName(IRttiPropertyDescriptor propDesc) {
		IRtti inject = getComponentRttiFromKeySource(propDesc.getName());
		if (!propDesc.getType().isInterface() &&
				inject instanceof HasErrorRtti) {
			return null;
		}
		return propDesc.getType().isAssignableFrom(inject) ? inject : null;
	}
	
	private IRtti findByPropertyType(IRttiPropertyDescriptor propDesc) {
		IRtti type = propDesc.getType();
		return type.isInterface() ? getComponentRttiFromKeySource(type) :
				new ComponentNotFoundRtti(createComponentKey(type));
	}
	
	private IRtti getComponentRttiFromKeySource(Object key) {
		return container.getComponent(createComponentKey(key));
	}
	
	private IComponentKey createComponentKey(Object key) {
		return container.createComponentKey(key);
	}

}
