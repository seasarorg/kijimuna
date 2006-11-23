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
package org.seasar.kijimuna.core.annotation;

import org.seasar.kijimuna.core.ConstCore;

public interface IBindingAnnotation {

	int BINDING_TYPE_UNKNOWN = -1;
	
	int BINDING_TYPE_MUST = 0;
	
	int BINDING_TYPE_SHOULD = 1;
	
	int BINDING_TYPE_MAY = 2;
	
	int BINDING_TYPE_NONE = 3;
	
	String getPropertyName();
	
	int getBindingType();
	
	String getIntactBindingType();
	
	
	public static class Converter implements ConstCore {
		
		public static int convert(String bindingTypeName) {
			if (DICON_VAL_BINDING_TYPE_MUST.equals(bindingTypeName)) {
				return BINDING_TYPE_MUST;
			} else if (DICON_VAL_BINDING_TYPE_SHOULD.equals(bindingTypeName)) {
				return BINDING_TYPE_SHOULD;
			} else if (DICON_VAL_BINDING_TYPE_MAY.equals(bindingTypeName)) {
				return BINDING_TYPE_MAY;
			} else if (DICON_VAL_BINDING_TYPE_NONE.equals(bindingTypeName)) {
				return BINDING_TYPE_NONE;
			} else {
				return BINDING_TYPE_UNKNOWN;
			}
		}
		
		public static String convert(int bindingType) {
			switch (bindingType) {
			case BINDING_TYPE_MUST:
				return DICON_VAL_BINDING_TYPE_MUST;
			case BINDING_TYPE_SHOULD:
				return DICON_VAL_BINDING_TYPE_SHOULD;
			case BINDING_TYPE_MAY:
				return DICON_VAL_BINDING_TYPE_MAY;
			case BINDING_TYPE_NONE:
				return DICON_VAL_BINDING_TYPE_NONE;
			default:
				return "UNKNOWN";
			}
		}
	}

}
