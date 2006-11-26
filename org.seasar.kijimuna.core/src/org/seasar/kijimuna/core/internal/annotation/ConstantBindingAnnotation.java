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
	private String bindingType;
	
	public ConstantBindingAnnotation(String fieldName, String propertyName,
			String bindingType) {
		this.fieldName = fieldName;
		this.propertyName = propertyName;
		this.bindingType = bindingType;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public String getBindingType() {
		return bindingType;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(fieldName).append(" = \"");
		if (propertyName != null) {
			sb.append(propertyName);
		} else {
			sb.append("bindingType=")
				.append("\"").append(bindingType).append("\"");
		}
		return sb.append("\"").toString();
	}

}
