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

import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;

public class PropertyItemFactory {

	public static IContentWalker createPropertyItem(IPropertyModel propModel,
			ContentItem item) {
		IPropertyElement prop = (IPropertyElement) propModel.getAdapter(
				IPropertyElement.class);
		boolean bound = propModel.requiresAutoBinding();
		if (prop != null) {
			return bound ? new AutoInjectedPropertyItem(propModel, prop, item) :
				(IContentWalker) new PropertyItem(propModel, prop, item);
		} else {
			return bound ? new AutoInjectedPropertyItem(propModel, item) :
				(IContentWalker) new PropertyItem(propModel, item);	
		}
	}

}
