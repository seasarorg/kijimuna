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

import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.IExternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.IncludeProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class IncludeItem extends AbstractInternalContainer implements IExternalContainer,
		ConstUI {

	public IncludeItem(IIncludeElement element, IContentWalker parent) {
		super(element, parent);
	}

	private IIncludeElement getIncludeElement() {
		return (IIncludeElement) getElement();
	}

	protected IPropertySource createProperty() {
		return new IncludeProperty(getIncludeElement());
	}

	public int getMarkerSeverity() {
		return getIncludeElement().getMarkerSeverity();
	}

	public IContainerElement getExternalContainer() {
		return getIncludeElement().getChildContainer();
	}

	public String getDisplayName() {
		IIncludeElement include = getIncludeElement();
		IContainerElement container = include.getChildContainer();
		if (container != null) {
			return container.getPath();
		}
		return include.getPath();
	}

	public String getImageName() {
		return IMAGE_ICON_INCLUDE;
	}

}
