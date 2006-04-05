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
			String autoBinding = component.getAutoBindingMode();
			if (autoBinding.equals(DICON_VAL_AUTO_BINDING_AUTO)
					|| autoBinding.equals(DICON_VAL_AUTO_BINDING_PROPERTY)) {
				autoInjection(component);
			}
		}
	}

	// 自動セッターインジェクション�C���W�F�N�V����
	private void autoInjection(IComponentElement component) {
		IComponentInfo info = (IComponentInfo) component.getAdapter(IComponentInfo.class);
		if (info != null) {
			IRttiPropertyDescriptor[] autoInjected = info.getAutoInjectedProperties();
			for (int i = 0; i < autoInjected.length; i++) {
				IRtti value = autoInjected[i].getValue();
				if (value instanceof IComponentNotFound) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.AutoSetterInjection.2", component,
							new Object[] {
									autoInjected[i].getParent().getQualifiedName(),
									autoInjected[i].getName()
							});
				} else if (value instanceof ITooManyRegisted) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.AutoSetterInjection.3", component,
							new Object[] {
									autoInjected[i].getParent().getQualifiedName(),
									autoInjected[i].getName(),
									ModelUtils.getInjectedElementName(value)
							});
				} else if (value != null) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.AutoSetterInjection.1", component,
							new Object[] {
									autoInjected[i].getParent().getQualifiedName(),
									autoInjected[i].getName(),
									ModelUtils.getInjectedElementName(value)
							});
				}
			}
		}
	}

}
