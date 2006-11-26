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

import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IExpressionElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.ContentProperty;

public class PropertyItem extends AbstractInternalContainer {

	private IPropertyModel propModel;
	
	public PropertyItem(IPropertyModel propModel, ContentItem parent) {
		super(parent);
		this.propModel = propModel;
	}
	
	public PropertyItem(IPropertyModel propModel, IPropertyElement prop,
			ContentItem parent) {
		super(prop, parent);
		this.propModel = propModel;
	}
	
	public String getImageName() {
		return IMAGE_ICON_PROPERTY;
	}
	
	public String getDisplayName() {
		return propModel.getPropertyName();
	}
	
	public Object[] getChildren() {
		IDiconElement element = getElement();
		if (element == null) {
			return super.getChildren();
		}
		List child = element.getChildren();
		if (child.size() != 1) {
			return super.getChildren();
		}
		return new Object[] {new ContentItem((IDiconElement) child.get(0),
				this, true)};
	}
	
	public boolean isOGNL() {
		IDiconElement element = getElement();
		return element instanceof IExpressionElement ? ((IExpressionElement)
				element).isOGNL() : false;
	}
	
	protected IPropertySource createProperty() {
		IDiconElement element = getElement();
		return element != null ? new ContentProperty(element) : null;
	}

}
