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

import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.IExternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.RelatedContainerProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class RelatedContainerItem extends AbstractContentWalker implements
		IExternalContainer, ConstUI {

	private IContainerElement relatedContainer;

	public RelatedContainerItem(IContainerElement relatedContainer, IContentWalker parent) {
		super(parent);
		this.relatedContainer = relatedContainer;
	}

	protected IPropertySource createProperty() {
		return new RelatedContainerProperty(relatedContainer);
	}

	public IContainerElement getExternalContainer() {
		return relatedContainer;
	}

	public Object[] getChildren() {
		List parents = ModelUtils.getParentContaienrs(relatedContainer);
		Object[] ret = new Object[parents.size()];
		for (int i = 0; i < parents.size(); i++) {
			ret[i] = new RelatedContainerItem((IContainerElement) parents.get(i), this);
		}
		return ret;
	}

	public String getDisplayName() {
		return relatedContainer.getDisplayName();
	}

	public String getImageName() {
		return IMAGE_ICON_PARENT;
	}

}
