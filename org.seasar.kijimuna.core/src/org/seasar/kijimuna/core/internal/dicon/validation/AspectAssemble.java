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
import org.seasar.kijimuna.core.rtti.IRtti;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AspectAssemble implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IComponentElement) {
			assemble((IComponentElement) element);
		}
	}

	private void assemble(IComponentElement component) {
		IRtti rtti = (IRtti) component.getAdapter(IRtti.class);
		if (rtti != null) {
			if (rtti.isInterface()) {
				List aspects = component.getAspectList();
				if (aspects.size() == 0) {
					MarkerSetting.createDiconMarker("dicon.validation.AspectAssemble.1",
							component, new Object[] {
								rtti.getQualifiedName()
							});
				}
			}
		}
	}

}
