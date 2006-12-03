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
package org.seasar.kijimuna.core.util;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;

public class BindingTypeUtil implements ConstCore {

	public static boolean isAvailable(String bindingType) {
		return isRequiredAutoBinding(bindingType) ||
				DICON_VAL_BINDING_TYPE_NONE.equals(bindingType);
	}
	
	public static boolean isRequiredAutoBinding(String bindingType) {
		return DICON_VAL_BINDING_TYPE_MAY.equals(bindingType) ||
				DICON_VAL_BINDING_TYPE_SHOULD.equals(bindingType) ||
				DICON_VAL_BINDING_TYPE_MUST.equals(bindingType);
	}
	
	public static boolean needsAutoBinding(IPropertyElement prop) {
		return prop != null &&
				isRequiredAutoBinding(prop.getBindingType()) &&
				prop.getChildren().isEmpty() &&
				StringUtils.noneValue(prop.getExpression());
	}
	
	public static boolean needsAutoBinding(IRttiPropertyDescriptor propDesc) {
		if (propDesc == null) {
			return false;
		}
		IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
				IBindingAnnotation.class);
		return ba == null || (ba != null && isRequiredAutoBinding(ba
				.getBindingType()));
	}

}
