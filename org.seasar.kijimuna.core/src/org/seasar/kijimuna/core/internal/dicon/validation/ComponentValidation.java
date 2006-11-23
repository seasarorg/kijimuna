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
import org.seasar.kijimuna.core.dicon.DiconOgnlRtti;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentValidation implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentElement) {
			IComponentElement component = (IComponentElement) element;
			checkError(component);
			validComponent(component);
		}
	}

	private void checkError(IComponentElement component) {
		IRtti rtti = (IRtti) component.getAdapter(IRtti.class);
		if (rtti instanceof HasErrorRtti) {
			MarkerSetting.createDiconMarker("dicon.validation.ComponentValidation.1",
					component, ((HasErrorRtti) rtti).getErrorMessage());
		}
	}

	private void validComponent(IComponentElement component) {
		String el = component.getExpression();
		String className = component.getComponentClassName();
		String instance = component.getInstanceMode();
		if (StringUtils.noneValue(className) && StringUtils.noneValue(el)
				&& !instance.equals(DICON_VAL_INSTANCE_OUTER)) {
			MarkerSetting.createDiconMarker("dicon.validation.ComponentValidation.2",
					component);
		}
		if (StringUtils.existValue(className) && StringUtils.existValue(el)) {
			RttiLoader rttiLoader = component.getRttiLoader();
			IRtti elRtti = new DiconOgnlRtti(rttiLoader).getValue(component
					.getContainerElement(), el);
			IRtti classRtti = rttiLoader.loadRtti(className);
			if (!classRtti.isAssignableFrom(elRtti)) {
				MarkerSetting.createDiconMarker("dicon.validation.ComponentValidation.3",
						component);
			}
		}
		IComponentKey[] keys = component
				.getTooManyComponentKeyArray(IComponentKey.TOO_MANY_FETAL);
		if (keys != null && keys.length > 0) {
			for (int i = 0; i < keys.length; i++) {
				MarkerSetting.createDiconMarker("dicon.validation.ComponentValidation.4",
						component, new IComponentKey[] {
							keys[i]
						});
			}
		}
	}

}
