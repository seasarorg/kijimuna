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

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ModelUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoSetterInjection implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentElement) {
			IComponentElement component = (IComponentElement) element;
			if (isAutoBinding(component)) {
				autoInjection(component);
			}
		} else if (element instanceof IPropertyElement) {
			IPropertyElement prop = (IPropertyElement) element;
			if (isAutoBinding(prop) &&
					ModelUtils.isAutoBindingProperty(prop)) {
				validateAutoBinding(prop);
			}
		}
	}
	
	private void validateAutoBinding(IPropertyElement prop) {
		IRtti rtti = (IRtti) prop.getParent().getAdapter(IRtti.class);
		validate(prop, (IRtti) prop.getAdapter(IRtti.class),
				rtti.getQualifiedName(), prop.getPropertyName());
	}

	// 自動セッターインジェクション�C���W�F�N�V����
	private void autoInjection(IComponentElement component) {
		IComponentInfo info = (IComponentInfo) component.getAdapter(IComponentInfo.class);
		if (info != null) {
			IRttiPropertyDescriptor[] autoInjected = info.getAutoInjectedProperties();
			for (int i = 0; i < autoInjected.length; i++) {
				IRtti value = autoInjected[i].getValue();
				validate(component, value, autoInjected[i].getParent()
						.getQualifiedName(), autoInjected[i].getName());
			}
		}
	}
	
	private boolean isAutoBinding(IComponentElement component) {
		return isAutoBindingComponent(component.getAutoBindingMode());
	}
	
	private boolean isAutoBinding(IPropertyElement prop) {
		IElement element = prop.getParent();
		if (element instanceof IComponentElement) {
			return isAutoBindingComponent(((IComponentElement) element)
					.getAutoBindingMode());
		}
		return false;
	}
	
	private boolean isAutoBindingComponent(String autoBindingMode) {
		return DICON_VAL_AUTO_BINDING_AUTO.equals(autoBindingMode) ||
				DICON_VAL_AUTO_BINDING_PROPERTY.equals(autoBindingMode);
	}
	
	private void validate(IDiconElement element, IRtti value,
			String parentName, String propName) {
		if (value instanceof IComponentNotFound) {
			// mustならエラー。mayなら警告なし。
			if (element instanceof IPropertyElement) {
				String bindingType = ((IPropertyElement) element).getBindingType();
				if (DICON_VAL_BINDING_TYPE_MAY.equals(bindingType)) {
					return;
				} else if (DICON_VAL_BINDING_TYPE_MUST.equals(bindingType)) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.AutoSetterInjection.4", element,
							((HasErrorRtti) value).getErrorMessage());
					return;
				}
			}
			MarkerSetting.createDiconMarker(
					"dicon.validation.AutoSetterInjection.2", element,
					new Object[] {parentName, propName});
		} else if (value instanceof ITooManyRegisted) {
			MarkerSetting.createDiconMarker(
					"dicon.validation.AutoSetterInjection.3", element,
					new Object[] {parentName, propName,
							ModelUtils.getInjectedElementName(value)
					});
		} else if (value != null) {
			MarkerSetting.createDiconMarker(
					"dicon.validation.AutoSetterInjection.1", element,
					new Object[] {parentName, propName,
							ModelUtils.getInjectedElementName(value)
					});
		}
	}

}
