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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import org.seasar.kijimuna.core.annotation.IAnnotationReader;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.rtti.IRttiFieldDescriptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;

public class ConstantAnnotationReader implements IAnnotationReader {

	public IBindingAnnotation getBindingAnnotation(IRttiPropertyDescriptor prop) {
		return BindingAnnotationCreator.create(prop);
	}
	
	
	private static class BindingAnnotationCreator {

		private static final String BINDING = "_BINDING";
		
		private static final Pattern PATTERN_BINDING_TYPE = Pattern.compile(
				"^bindingType=([^\\s]+)$");
		
		static IBindingAnnotation create(IRttiPropertyDescriptor prop) {
			IRttiFieldDescriptor fieldDesc = prop.getParent().getField(
					prop.getName() + BINDING, true);
			if (fieldDesc == null) {
				return null;
			}
			try {
				IType type = fieldDesc.getParent().getType();
				if (!type.exists()) {
					return null;
				}
				Object value = type.getField(fieldDesc.getName()).getConstant();
				if (!(value instanceof String)) {
					return null;
				}
				String source = (String) value;
				// nullのとき
				if ("null".equals(source)) {
					return null;
				}
				source = source.replaceAll("\"", "");
				Matcher matcher = PATTERN_BINDING_TYPE.matcher(source);
				// bindinTypeのとき
				if (matcher.matches()) {
					return new ConstantBindingAnnotation(fieldDesc.getName(), null,
							matcher.group(1));
				}
				// nullという文字列のとき
				else if ("null".equals(source)) {
					return new ConstantBindingAnnotation(fieldDesc.getName(), "null",
							DICON_VAL_BINDING_TYPE_NONE);
				}
				// それ以外はプロパティ名が指定されている
				else {
					return new ConstantBindingAnnotation(fieldDesc.getName(), source,
							DICON_VAL_BINDING_TYPE_MUST);
				}
			} catch (JavaModelException ignore) {
				return null;
			}
		}
	}

}
