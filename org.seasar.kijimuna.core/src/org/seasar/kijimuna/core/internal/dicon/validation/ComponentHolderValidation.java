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
package org.seasar.kijimuna.core.internal.dicon.validation;

import java.util.Iterator;
import java.util.List;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.model.IComponentHolderElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.internal.dicon.model.MetaElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentHolderValidation implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentHolderElement) {
			IComponentHolderElement arg = (IComponentHolderElement) element;
			if (arg instanceof IPropertyElement) {
				IPropertyElement prop = (IPropertyElement) arg;
				if (!DICON_VAL_BINDING_TYPE_MUST.equals(prop.getBindingType())) {
					return;
				}
			}
			checkError(arg);
			validComponentHolder(arg);
		}
	}

	private void checkError(IComponentHolderElement componentHolder) {
		IRtti rtti = (IRtti) componentHolder.getAdapter(IRtti.class);
		if (rtti instanceof HasErrorRtti) {
			MarkerSetting.createDiconMarker(
					"dicon.validation.ComponentHolderValidation.1", componentHolder,
					((HasErrorRtti) rtti).getErrorMessage());
		}
	}

	private void validComponentHolder(IComponentHolderElement componentHolder) {
		String el = componentHolder.getExpression();
		List children = componentHolder.getChildren();
		int size = children.size();
		if (size > 1) {
			for (Iterator it = children.iterator(); it.hasNext();) {
				IDiconElement element = (IDiconElement) it.next();
				MarkerSetting.createDiconMarker(
						"dicon.validation.ComponentHolderValidation.4", element);
			}
		}
		if (StringUtils.existValue(el)) {
			if (size > 0) {
				IDiconElement element = (IDiconElement) children.get(size - 1);
				MarkerSetting.createDiconMarker(
						"dicon.validation.ComponentHolderValidation.2", element);
			}
		} else {
			if (size == 0 && componentHolder instanceof MetaElement == false) {
				if (!isAutoBindingProperty(componentHolder)) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.ComponentHolderValidation.3", componentHolder);
				}
			}
		}
	}
	
	private boolean isAutoBindingProperty(IComponentHolderElement holder) {
		return holder instanceof IPropertyElement &&
				!(holder.getAdapter(IRtti.class) instanceof HasErrorRtti);
	}

}
