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
package org.seasar.kijimuna.core.internal.dicon.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.internal.dicon.autobinding.AutoBindingComponentProvider;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PropertyElement extends ComponentHolderElement implements IPropertyElement,
		ConstCore {

	public PropertyElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_PROPERTY);
	}

	public String getBindingType() {
		String bindingType = getAttribute(DICON_ATTR_BINDINGTYPE);
		if (StringUtils.noneValue(bindingType)) {
			bindingType = DICON_VAL_BINDING_TYPE_SHOULD;
		}
		return bindingType;
	}

	public String getPropertyName() {
		return getAttribute(DICON_ATTR_NAME);
	}

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getPropertyName());
		String superDisplay = super.getDisplayName();
		if (!isNotDisplay() && StringUtils.existValue(superDisplay)) {
			buffer.append(" ").append(superDisplay);
		}
		return buffer.toString();
	}
	
	protected IRtti getNonExpressionValue() {
		if (ModelUtils.isAutoBindingProperty(this)) {
			IElement element = getParent();
			if (!(element instanceof IComponentElement)) {
				return loadChildElementNotFoundRtti();
			}
			IRtti rtti = (IRtti) element.getAdapter(IRtti.class);
			if (rtti == null) {
				return loadChildElementNotFoundRtti();
			}
			if (rtti instanceof HasErrorRtti) {
				return rtti;
			}
			IRttiPropertyDescriptor propDesc = rtti.getProperty(getPropertyName());
			AutoBindingComponentProvider provider =
				new AutoBindingComponentProvider(getContainerElement());
			return provider.getAutoBindingComponentRtti(propDesc);
		}
		return super.getNonExpressionValue();
	}
	
	private boolean isNotDisplay() {
		return !DICON_VAL_BINDING_TYPE_MUST.equals(getBindingType()) &&
				getAdapter(IRtti.class) instanceof HasErrorRtti;
	}

}
