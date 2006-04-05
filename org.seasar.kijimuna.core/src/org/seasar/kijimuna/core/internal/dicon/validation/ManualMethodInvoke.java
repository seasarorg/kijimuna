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

import java.util.List;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IMethodElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ManualMethodInvoke implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IMethodElement) {
			invoke((IMethodElement) element);
		}
	}

	private void invoke(IMethodElement method) {
		String methodName = method.getMethodName();
		List argList = method.getArgList();
		if (StringUtils.existValue(methodName) && (argList.size() > 0)) {
			IRtti component = ModelUtils.getComponentRtti(method);
			if ((component != null) && !(component instanceof HasErrorRtti)) {
				IRtti[] rttiArgs = ModelUtils.convertArray(method.getArgList().toArray());
				for (int i = 0; i < rttiArgs.length; i++) {
					if (rttiArgs[i] instanceof HasErrorRtti) {
						return;
					}
				}
				IRttiMethodDesctiptor descriptor = (IRttiMethodDesctiptor) method
						.getAdapter(IRttiMethodDesctiptor.class);
				if (descriptor == null) {
					String display = ModelUtils.getMethodDisplay(component, methodName,
							rttiArgs, true);
					MarkerSetting.createDiconMarker(
							"dicon.validation.ManualMethodInvoke.1", method,
							new Object[] {
								display
							});
				}
			}
		}
	}

}
