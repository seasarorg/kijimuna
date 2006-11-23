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

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class IncludeProperty extends NullProperty implements ConstCore {

	private static IPropertyDescriptor[] descriptors;

	static {
		descriptors = new IPropertyDescriptor[1];
		PropertyDescriptor d = new PropertyDescriptor(DICON_ATTR_PATH, DICON_ATTR_PATH);
		d.setCategory(KijimunaUI
				.getResourceString("dicon.provider.property.ContentProperty.3"));
		descriptors[0] = d;
	}

	private IIncludeElement include;

	public IncludeProperty(IIncludeElement include) {
		this.include = include;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if (DICON_ATTR_PATH.equals(id)) {
			return include.getPath();
		}
		return null;
	}

}
