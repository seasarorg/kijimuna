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
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ManualSetterInjection implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IPropertyElement) {
			manualInject((IPropertyElement) element);
		}
	}

	private void manualInject(IPropertyElement property) {
		IComponentElement componentElement = (IComponentElement) property.getParent();
		IRtti component = (IRtti) componentElement.getAdapter(IRtti.class);
		if (component != null) {
			IRtti value = (IRtti) property.getAdapter(IRtti.class);
			if (value instanceof HasErrorRtti) {
				return;
			}
			String propertyName = property.getPropertyName();
			if (StringUtils.existValue(propertyName) && (value != null)) {
				IRttiPropertyDescriptor desc = component.getProperty(propertyName);
				if ((desc == null)
						|| !desc.isWritable()
						|| (!desc.getType().isAssignableFrom(value) && (desc.getType()
								.getConstructor(new IRtti[] {
									value
								}) == null))) {
					MarkerSetting.createDiconMarker(
							"dicon.validation.ManualSetterInjection.1", property,
							new Object[] {
									component.getQualifiedName(), propertyName,
									value.getQualifiedName()
							});
				}
			}
		}
	}
}
