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
package org.seasar.kijimuna.ui.internal.provider.dicon.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedPropertyProperty extends NullProperty {

	private static IPropertyDescriptor[] descriptors;

	static {
		// info category
		String category = KijimunaUI
				.getResourceString("dicon.provider.property.ContentProperty.4");
		descriptors = new IPropertyDescriptor[4];
		// class type
		String id = "dicon.provider.property.AutoInjectedPropertyProperty.1";
		PropertyDescriptor d = new PropertyDescriptor(id, KijimunaUI
				.getResourceString(id));
		d.setCategory(category);
		descriptors[0] = d;
		// property name
		id = "dicon.provider.property.AutoInjectedPropertyProperty.2";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[1] = d;
		// property type
		id = "dicon.provider.property.AutoInjectedPropertyProperty.3";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[2] = d;
		// injected component
		id = "dicon.provider.property.AutoInjectedPropertyProperty.4";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[3] = d;
	}

	private IRttiPropertyDescriptor property;
	private IPropertyElement prop;

	public AutoInjectedPropertyProperty(IRttiPropertyDescriptor property) {
		this.property = property;
	}

	public AutoInjectedPropertyProperty(IPropertyElement prop) {
		this.prop = prop;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if ("dicon.provider.property.AutoInjectedPropertyProperty.1".equals(id)) {
			return getQualifiedClassNameOfParent();
		} else if ("dicon.provider.property.AutoInjectedPropertyProperty.2".equals(id)) {
			return getPropertyName();
		} else if ("dicon.provider.property.AutoInjectedPropertyProperty.3".equals(id)) {
			return getQualifiedClassNameOfProperty();
		} else if ("dicon.provider.property.AutoInjectedPropertyProperty.4".equals(id)) {
			IRtti value = getPropertyValue();
			if (value instanceof IComponentNotFound) {
				return KijimunaUI.getResourceString(
						"dicon.provider.property.AutoInjectedPropertyProperty.5",
						new Object[] {
							value.getAdapter(IComponentKey.class)
						});
			} else if (value instanceof ITooManyRegisted) {
				return ((ITooManyRegisted) value).getErrorMessage();
			} else {
				return ModelUtils.getInjectedElementName(value);
			}
		}
		return null;
	}

	private String getQualifiedClassNameOfParent() {
		if (prop != null) {
			IRtti parent = (IRtti) prop.getParent().getAdapter(IRtti.class);
			return parent != null ? parent.getQualifiedName() : "";
		}
		return property.getParent().getQualifiedName();
	}

	private String getPropertyName() {
		return prop != null ? prop.getPropertyName() : property.getName();
	}

	private String getQualifiedClassNameOfProperty() {
		if (prop != null) {
			IRtti parent = (IRtti) prop.getParent().getAdapter(IRtti.class);
			IRttiPropertyDescriptor propDesc = parent != null ? parent.getProperty(prop
					.getPropertyName()) : null;
			return propDesc != null ? propDesc.getType().getQualifiedName() : "";
		}
		return property.getType().getQualifiedName();
	}

	private IRtti getPropertyValue() {
		return prop != null ? (IRtti) prop.getAdapter(IRtti.class) : property.getValue();
	}

}
