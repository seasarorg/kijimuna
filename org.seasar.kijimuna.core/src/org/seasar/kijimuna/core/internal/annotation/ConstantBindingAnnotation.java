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
package org.seasar.kijimuna.core.internal.annotation;

import org.seasar.kijimuna.core.annotation.IBindingAnnotation;

public class ConstantBindingAnnotation implements IBindingAnnotation {

	private String fieldName;
	private String propertyName;
	private int bindingType = BINDING_TYPE_SHOULD;
	private String intactBindingType;
	
	public ConstantBindingAnnotation(String fieldName, String propertyName,
			int bindingType, String intactBindingType) {
		this.fieldName = fieldName;
		this.propertyName = propertyName;
		this.bindingType = bindingType;
		this.intactBindingType = intactBindingType;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public int getBindingType() {
		return bindingType;
	}
	
	public String getIntactBindingType() {
		return intactBindingType;
	}
	
	public String toString() {
		return fieldName + " = \"" + (propertyName != null ? propertyName :
			"bindingType=" + IBindingAnnotation.Converter.convert(
					bindingType)) + "\"";
	}

}
