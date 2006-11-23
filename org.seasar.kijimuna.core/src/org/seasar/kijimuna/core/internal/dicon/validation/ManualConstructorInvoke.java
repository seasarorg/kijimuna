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
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ManualConstructorInvoke implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentElement) {
			manualAssembler((IComponentElement) element);
		}
	}

	// 引数のあるコンストラクタでのアッセンブル
	private void manualAssembler(IComponentElement component) {
		List args = component.getArgList();
		if (args.size() == 0) {
			return;
		}
		IRtti rtti = (IRtti) component.getAdapter(IRtti.class);
		if ((rtti != null) && !(rtti instanceof HasErrorRtti)) {
			int aspectSize = component.getAspectList().size();
			if ((aspectSize == 0) && (rtti.isInterface())) {
				MarkerSetting.createDiconMarker(
						"dicon.validation.ManualConstructorInvoke.1", component,
						new Object[] {
							rtti.getQualifiedName()
						});
			} else {
				IRtti[] rttiArgs = ModelUtils.convertArray(component.getArgList()
						.toArray());
				for (int i = 0; i < rttiArgs.length; i++) {
					if (rttiArgs[i] instanceof HasErrorRtti) {
						return;
					}
				}
				IRttiConstructorDesctiptor descriptor = (IRttiConstructorDesctiptor) component
						.getAdapter(IRttiConstructorDesctiptor.class);
				if (descriptor == null) {
					String display = ModelUtils.getMethodDisplay(rtti, rtti
							.getShortName(), rttiArgs, true);
					MarkerSetting.createDiconMarker(
							"dicon.validation.ManualConstructorInvoke.2", component,
							new Object[] {
								display
							});
				}
			}
		}
	}

}
