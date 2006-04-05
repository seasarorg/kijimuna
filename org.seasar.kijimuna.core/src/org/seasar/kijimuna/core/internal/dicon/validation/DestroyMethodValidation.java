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
import org.seasar.kijimuna.core.dicon.model.IDestroyMethodElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DestroyMethodValidation implements IValidation, ConstCore {

	public void validation(IDiconElement element) {
		if (element instanceof IDestroyMethodElement) {
			validDestroy((IDestroyMethodElement) element);
		}
	}

	private void validDestroy(IDestroyMethodElement destroy) {
		IComponentElement component = (IComponentElement) destroy.getParent();
		String instance = component.getInstanceMode();
		if (!instance.equals(DICON_VAL_INSTANCE_SINGLETON)) {
			MarkerSetting.createDiconMarker("dicon.validation.DestroyMethodValidation.1",
					destroy, new Object[] {
						instance
					});
		}
	}

}
