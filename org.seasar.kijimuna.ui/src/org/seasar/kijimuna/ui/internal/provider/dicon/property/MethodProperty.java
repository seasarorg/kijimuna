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

import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MethodProperty extends NullProperty {

	private static IPropertyDescriptor[] descriptors;

	static {
		// info category
		String category = KijimunaUI
				.getResourceString("dicon.provider.property.ContentProperty.4");
		descriptors = new IPropertyDescriptor[6];
		// args
		String id = "dicon.provider.property.MethodProperty.1";
		PropertyDescriptor d = new PropertyDescriptor(id, KijimunaUI
				.getResourceString(id));
		d.setCategory(category);
		descriptors[0] = d;
		// returnType
		id = "dicon.provider.property.MethodProperty.2";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[1] = d;
		// methodName
		id = "dicon.provider.property.MethodProperty.3";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[2] = d;
		// final
		id = "dicon.provider.property.MethodProperty.4";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[3] = d;
		// static
		id = "dicon.provider.property.MethodProperty.5";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[4] = d;
		// parent
		id = "dicon.provider.property.MethodProperty.6";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[5] = d;
	}

	private IRttiMethodDesctiptor method;

	public MethodProperty(IRttiMethodDesctiptor method) {
		this.method = method;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	private String getArgDisplay() {
		IRtti[] args = method.getArgs();
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			if (i != 0) {
				ret.append(", ");
			}
			ret.append(args[i].getQualifiedName());
		}
		return ret.toString();
	}

	public Object getPropertyValue(Object id) {
		if ("dicon.provider.property.MethodProperty.1".equals(id)) {
			return getArgDisplay();
		} else if ("dicon.provider.property.MethodProperty.2".equals(id)) {
			return method.getReturnType();
		} else if ("dicon.provider.property.MethodProperty.3".equals(id)) {
			return method.getMethodName();
		} else if ("dicon.provider.property.MethodProperty.4".equals(id)) {
			return new Boolean(method.isFinal());
		} else if ("dicon.provider.property.MethodProperty.5".equals(id)) {
			return new Boolean(method.isStatic());
		} else if ("dicon.provider.property.MethodProperty.6".equals(id)) {
			return method.getParent();
		} else {
			return null;
		}
	}

}
