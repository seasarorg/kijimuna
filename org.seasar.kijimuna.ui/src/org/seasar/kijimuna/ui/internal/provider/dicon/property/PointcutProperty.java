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

import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PointcutProperty extends NullProperty {

	private static IPropertyDescriptor[] descriptors;

	static {
		// info category
		String category = KijimunaUI
				.getResourceString("dicon.provider.property.ContentProperty.4");
		descriptors = new IPropertyDescriptor[2];
		// errorMessage
		String id = "dicon.provider.property.PointcutProperty.1";
		PropertyDescriptor d = new PropertyDescriptor(id, KijimunaUI
				.getResourceString(id));
		d.setCategory(category);
		descriptors[0] = d;
		// regexp
		id = "dicon.provider.property.PointcutProperty.2";
		d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
		d.setCategory(category);
		descriptors[1] = d;
	}

	private IPointcut pointcut;

	public PointcutProperty(IPointcut pointcut) {
		this.pointcut = pointcut;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if ("dicon.provider.property.PointcutProperty.1".equals(id)) {
			return pointcut.getErrorMessage();
		} else if ("dicon.provider.property.PointcutProperty.2".equals(id)) {
			return pointcut.getRegexp();
		} else {
			return null;
		}
	}

}
