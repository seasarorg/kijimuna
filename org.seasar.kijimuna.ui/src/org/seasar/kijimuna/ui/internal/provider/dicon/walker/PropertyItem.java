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
package org.seasar.kijimuna.ui.internal.provider.dicon.walker;

import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;

public class PropertyItem extends AbstractInternalContainer {

	public PropertyItem(IPropertyElement prop, IContentWalker walker) {
		super(prop, walker);
	}

	public String getDisplayName() {
		return getElement().getDisplayName();
	}

	public String getImageName() {
		return IMAGE_ICON_PROPERTY;
	}
	
	public int getMarkerSeverity() {
		IPropertyElement prop = (IPropertyElement) getElement();
		if (prop.getAdapter(IRtti.class) instanceof HasErrorRtti) {
			String bt = prop.getBindingType();
			if (DICON_VAL_BINDING_TYPE_MUST.equals(bt)) {
				return MARKER_SEVERITY_ERROR;
			} else if (DICON_VAL_BINDING_TYPE_SHOULD.equals(bt)) {
				if (StringUtils.existValue(prop.getExpression())) {
					return MARKER_SEVERITY_ERROR;
				} else {
					return MARKER_SEVERITY_WARNING;
				}
			}
		}
		return MARKER_SEVERITY_NONE;
	}

}
