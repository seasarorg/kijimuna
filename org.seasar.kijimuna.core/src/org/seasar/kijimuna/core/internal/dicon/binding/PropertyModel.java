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
package org.seasar.kijimuna.core.internal.dicon.binding;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.BindingTypeUtil;
import org.seasar.kijimuna.core.util.StringUtils;

public class PropertyModel implements IPropertyModel, ConstCore {

	private IRttiPropertyDescriptor propDesc;
	private IPropertyElement prop;
	private IStrategy strategy;
	
	public PropertyModel(IRttiPropertyDescriptor propDesc, IPropertyElement
			prop) {
		if (propDesc == null) {
			throw new IllegalArgumentException();
		}
		this.propDesc = propDesc;
		this.prop = prop;
		this.strategy = createStrategy();
	}

	public String getPropertyName() {
		return strategy.getPropertyName();
	}

	public String getBindingType() {
		return strategy.getBindingType();
	}

	public boolean wasDoneAutoBinding() {
		IRtti rtti = propDesc.getValue();
		return rtti != null && !(rtti instanceof HasErrorRtti);
	}
	
	public boolean isAutoBindingType() {
		return propDesc.getType().isInterface() && propDesc.isWritable();
	}
	
	public boolean requiresAutoBinding() {
		return propDesc.isWritable() && strategy.requiresAutoBinding();
	}

	public Object getAdapter(Class adapter) {
		if (IRtti.class.equals(adapter)) {
			// FIXME IPropertyElementのRTTIと自動バインディングされた
			// IRttiPropertyDescriptor#getValueのRTTIが一致していないための回避作...
			// PropertyElement#getAdapterはcomponentModel#getPropertyModelを呼び出す？
			if (requiresAutoBinding()) {
				return propDesc.getValue();
			} else if (prop != null) {
				return prop.getAdapter(IRtti.class);
			}
		} else if (IPropertyElement.class.equals(adapter)) {
			return prop;
		} else if (IRttiPropertyDescriptor.class.equals(adapter)) {
			return propDesc;
		} else if (IBindingAnnotation.class.equals(adapter)) {
			return propDesc.getAdapter(IBindingAnnotation.class);
		}
		return null;
	}
	
	private IStrategy createStrategy() {
		if (prop != null) {
			return new ElementStrategy();
		}
		IBindingAnnotation annotation = (IBindingAnnotation) propDesc
				.getAdapter(IBindingAnnotation.class);
		if (annotation != null) {
			return new AnnotationStrategy(annotation);
		}
		return new DefaultStrategy();
	}
	
	
	private interface IStrategy {
		String getPropertyName();
		String getBindingType();
		boolean requiresAutoBinding();
	}
	
	private class ElementStrategy implements IStrategy {

		public String getPropertyName() {
			return prop.getPropertyName();
		}

		public String getBindingType() {
			return prop.getBindingType();
		}
		
		public boolean requiresAutoBinding() {
			return BindingTypeUtil.needsAutoBinding(prop);
		}
	}
	
	private class AnnotationStrategy implements IStrategy {
		
		private IBindingAnnotation annotation;
		
		public AnnotationStrategy(IBindingAnnotation annotation) {
			this.annotation = annotation;
		}
		
		public String getPropertyName() {
			String propName = annotation.getPropertyName();
			return StringUtils.existValue(propName) ? propName : propDesc
					.getName();
		}
		
		public String getBindingType() {
			return annotation.getBindingType();
		}
		
		public boolean requiresAutoBinding() {
			return BindingTypeUtil.isRequiredAutoBinding(getBindingType());
		}
	}
	
	private class DefaultStrategy implements IStrategy {

		public String getPropertyName() {
			return propDesc.getName();
		}

		public String getBindingType() {
			return DICON_VAL_BINDING_TYPE_SHOULD;
		}

		public boolean requiresAutoBinding() {
			return true;
		}
	}

}
