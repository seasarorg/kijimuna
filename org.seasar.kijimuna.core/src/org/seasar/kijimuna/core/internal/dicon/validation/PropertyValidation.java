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
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ModelUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PropertyValidation implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentElement) {
			validate((IComponentElement) element);
		}
	}
	
	protected void validate(IComponentElement component) {
		IPropertyModel[] propModels = (IPropertyModel[]) component.getAdapter(
				IPropertyModel[].class);
		for (int i = 0; i < propModels.length; i++) {
			IPropertyElement prop = (IPropertyElement) propModels[i].getAdapter(
					IPropertyElement.class);
			IDiconElement element = prop != null ? (IDiconElement) prop : component;
			validateBindingType(element, propModels[i]);
			if (propModels[i].requiresAutoBinding()) {
				String autoBinding = component.getAutoBindingMode();
				if (DICON_VAL_AUTO_BINDING_AUTO.equals(autoBinding) ||
						DICON_VAL_AUTO_BINDING_PROPERTY.equals(autoBinding)) {
					validateAutoBinding(element, propModels[i]);
				}
			}
		}
	}
	
	protected void validateBindingType(IDiconElement element,
			IPropertyModel propModel) {
		String bt = propModel.getBindingType();
		if (!(DICON_VAL_BINDING_TYPE_MAY.equals(bt) ||
				DICON_VAL_BINDING_TYPE_SHOULD.equals(bt) ||
				DICON_VAL_BINDING_TYPE_MUST.equals(bt) ||
				DICON_VAL_BINDING_TYPE_NONE.equals(bt))) {
			MarkerSetting.createDiconMarker(
					"dicon.validation.AutoSetterInjection.5", element,
					new Object[] {
							propModel.getPropertyName(),
							bt
					});
		}
	}
	
	protected void validateAutoBinding(IDiconElement element,
			IPropertyModel propModel) {
		IRtti propValue = (IRtti) propModel.getAdapter(IRtti.class);
		if (propValue instanceof ITooManyRegisted) {
			markTooManyRegistered(propModel, element);
		} else if (propModel.wasDoneAutoBinding()) {
			markAutoBindingDone(propModel, element);
		} else if (propModel.requiresAutoBinding()) {
			if (propValue instanceof IComponentNotFound) {
				if (propModel.isAutoBindingType()) {
					markComponentNotFound(propModel, element);
				} else {
					// mustのときだけエラーでそれ以外は何もしない（S2.3.x）
					if (DICON_VAL_BINDING_TYPE_MUST.equals(propModel
							.getBindingType())) {
						createMarkerOfBindingTypeMust(propModel, element);
					}
				}
			}
		}
	}
	
	protected void markComponentNotFound(IPropertyModel propModel,
			IDiconElement element) {
		String bt = propModel.getBindingType();
		if (DICON_VAL_BINDING_TYPE_MAY.equals(bt) ||
				DICON_VAL_BINDING_TYPE_NONE.equals(bt)) {
			return;
		} else if (DICON_VAL_BINDING_TYPE_MUST.equals(bt)) {
			createMarkerOfBindingTypeMust(propModel, element);
		} else if (DICON_VAL_BINDING_TYPE_SHOULD.equals(bt)) {
			MarkerSetting.createDiconMarker(
					"dicon.validation.AutoSetterInjection.2", element,
					new Object[] {
							getParentRtti(propModel).getQualifiedName(),
							propModel.getPropertyName()
					});
		}
	}
	
	protected void markTooManyRegistered(IPropertyModel propModel,
			IDiconElement element) {
		MarkerSetting.createDiconMarker(
				"dicon.validation.AutoSetterInjection.3", element,
				new Object[] {
						getParentRtti(propModel).getQualifiedName(),
						propModel.getPropertyName(),
						ModelUtils.getInjectedElementName(getPropDesc(
								propModel).getValue())
				});
	}
	
	protected void markAutoBindingDone(IPropertyModel propModel,
			IDiconElement element) {
		MarkerSetting.createDiconMarker(
				"dicon.validation.AutoSetterInjection.1", element,
				new Object[] {
						getParentRtti(propModel).getQualifiedName(),
						propModel.getPropertyName(),
						ModelUtils.getInjectedElementName(getPropDesc(
								propModel).getValue())
				});
	}
	
	private void createMarkerOfBindingTypeMust(IPropertyModel propModel,
			IDiconElement element) {
		MarkerSetting.createDiconMarker(
				"dicon.validation.AutoSetterInjection.4", element,
				new Object[] {
						getParentRtti(propModel).getQualifiedName(),
						propModel.getPropertyName()
				});
	}
	
	private IRtti getParentRtti(IPropertyModel propModel) {
		IRttiPropertyDescriptor propDesc = (IRttiPropertyDescriptor) propModel
				.getAdapter(IRttiPropertyDescriptor.class);
		return propDesc.getParent();
	}
	
	private IRttiPropertyDescriptor getPropDesc(IPropertyModel propModel) {
		return (IRttiPropertyDescriptor) propModel.getAdapter(
				IRttiPropertyDescriptor.class);
	}

}

