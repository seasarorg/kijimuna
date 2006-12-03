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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.binding.IComponentModel;
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentNotFoundRtti;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.AutoBindingUtil;
import org.seasar.kijimuna.core.util.BindingTypeUtil;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

public class ComponentModel implements IComponentModel, ConstCore {

	private static final Pattern PATTERN_ANY = Pattern.compile(".*");
	
	private IRtti rtti;
	private IComponentElement component;
	private Map propElementMap; 
	private long latestStamp;
	private Map propModelCache = new HashMap();
	
	public ComponentModel(IRtti rtti, IComponentElement component) {
		if (rtti == null || component == null) {
			throw new IllegalArgumentException();
		}
		this.rtti = rtti;
		this.component = component;
	}
	
	public IPropertyModel[] getPropertyModels() {
		boolean modified = isModified();
		if (modified) {
			clearCache();
		}
		if (propModelCache.size() == getPropertyElementMap().size()) {
			return (IPropertyModel[]) propModelCache.values().toArray(
					new IPropertyModel[propModelCache.size()]);
		}
		IRttiPropertyDescriptor[] propDescs = rtti.getProperties(PATTERN_ANY);
		IPropertyModel[] propModels = new IPropertyModel[propDescs.length];
		for (int i = 0; i < propDescs.length; i++) {
			propModels[i] = internalGetPropertyModel(propDescs[i].getName());
		}
		if (modified) {
			tryToUpdateStamp();
		}
		return propModels;
	}
	
	public IPropertyModel getPropertyModel(String propName) {
		if (StringUtils.noneValue(propName)) {
			return null;
		}
		boolean modified = isModified();
		if (modified) {
			clearCache();
		}
		if (propModelCache.containsKey(propName)) {
			return (IPropertyModel) propModelCache.get(propName);
		}
		IPropertyModel propModel = internalGetPropertyModel(propName);
		if (modified) {
			tryToUpdateStamp();
		}
		return propModel;
	}
	
	public Object getAdapter(Class adapter) {
		if (IRtti.class.equals(adapter)) {
			return rtti;
		} else if (IComponentElement.class.equals(adapter)) {
			return component;
		}
		return null;
	}
	
	private IPropertyModel internalGetPropertyModel(String propName) {
		IRttiPropertyDescriptor propDesc = rtti.getProperty(propName);
		if (propDesc == null) {
			return null;
		}
		processAutoBinding(propDesc);
		IPropertyModel propModel = new PropertyModel(propDesc,
				getPropertyElement(propName));
		propModelCache.put(propModel.getPropertyName(), propModel);
		return propModel;
	}
	
	private boolean isModified() {
		return canGetStamp() ? latestStamp != getStamp() : true;
	}
	
	private void clearCache() {
		propModelCache.clear();
	}
	
	private void tryToUpdateStamp() {
		if (canGetStamp()) {
			latestStamp = getStamp();
		}
	}
	
	private boolean canGetStamp() {
		return rtti.getType() != null && rtti.getType().getResource() != null;
	}
	
	private long getStamp() {
		return canGetStamp() ? rtti.getType().getResource()
				.getModificationStamp() : -1L;
	}
	
	private void processAutoBinding(IRttiPropertyDescriptor propDesc) {
		if (AutoBindingUtil.isRequiredPropertyAutoBinding(component
				.getAutoBindingMode())) {
			if (ModelUtils.doDesignTimeAutoBinding(propDesc.getType())) {
				createPropertyInjector(propDesc).inject();
			}
		}
	}
	
	private IInjector createPropertyInjector(IRttiPropertyDescriptor propDesc) {
		IPropertyElement prop = getPropertyElement(propDesc.getName());
		return prop != null ? new ElementInjector(prop, propDesc) :
				(IInjector) new NonElementInjector(propDesc);
	}
	
	private IPropertyElement getPropertyElement(String propName) {
		return (IPropertyElement) getPropertyElementMap().get(propName);	
	}
	
	private Map getPropertyElementMap() {
		if (propElementMap == null) {
			List propList = component.getPropertyList();
			propElementMap = new HashMap(propList.size());
			for (int i = 0; i < propList.size(); i++) {
				IPropertyElement prop = (IPropertyElement) propList.get(i);
				propElementMap.put(prop.getPropertyName(), prop);
			}
		}
		return propElementMap;
	}
	
	
	private interface IInjector {
		void inject();
	}
	
	private abstract class AbstractPropertyInjector implements IInjector {
		
		protected final IRttiPropertyDescriptor propDesc;
		
		public AbstractPropertyInjector(IRttiPropertyDescriptor propDesc) {
			if (propDesc == null) {
				throw new IllegalArgumentException();
			}
			this.propDesc = propDesc;
		}
		
		public final void inject() {
			if (canInject()) {
				if (!injectByComponentName()) {
					injectByPropertyType();
				}
			}
		}
		
		protected abstract boolean canInject();
		
		protected boolean injectByComponentName() {
			IRtti inject = getComponentRttiFromKeySource(propDesc.getName());
			boolean assignable = propDesc.getType().isAssignableFrom(inject);
			if (assignable || inject instanceof HasErrorRtti) {
				propDesc.setValue(inject);
			} else {
				propDesc.setValue(createComponentNotFoundRtti(propDesc.getName()));
			}
			return assignable;
		}
		
		protected void injectByPropertyType() {
			if (propDesc.getType().isInterface() && propDesc.isWritable()) {
				propDesc.setValue(getComponentRttiFromKeySource(propDesc.getType()));
			}
		}
		
		protected IRtti getComponentRttiFromKeySource(Object keySource) {
			return component.getContainerElement().getComponent(
					createComponentKey(keySource));
		}
		
		protected IComponentKey createComponentKey(Object keySource) {
			return component.getContainerElement().createComponentKey(keySource);
		}
		
		protected IComponentNotFound createComponentNotFoundRtti(Object key) {
			return new ComponentNotFoundRtti(createComponentKey(key));
		}
	}
	
	private class ElementInjector extends AbstractPropertyInjector {
		
		private IPropertyElement prop;
		
		public ElementInjector(IPropertyElement prop,
				IRttiPropertyDescriptor propDesc) {
			super(propDesc);
			if (prop == null) {
				throw new IllegalArgumentException();
			}
			this.prop = prop;
		}

		protected boolean canInject() {
			return BindingTypeUtil.needsAutoBinding(prop);
		}
	}
	
	private class NonElementInjector extends AbstractPropertyInjector {
		
		public NonElementInjector(IRttiPropertyDescriptor propDesc) {
			super(propDesc);
		}
		
		protected boolean canInject() {
			return BindingTypeUtil.needsAutoBinding(propDesc);
		}
		
		protected boolean injectByComponentName() {
			IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
					IBindingAnnotation.class);
			if (injectByAnnotation(propDesc, ba)) {
				return true;
			}
			return super.injectByComponentName();
		}
		
		private boolean injectByAnnotation(
				IRttiPropertyDescriptor propDesc, IBindingAnnotation ba) {
			if (ba == null) {
				return false;
			}
			IRtti inject = null;
			String propertyName = ba.getPropertyName();
			if (!BindingTypeUtil.isAvailable(ba.getBindingType())) {
				inject = createComponentNotFoundRtti(propDesc.getName());
			} else if (StringUtils.noneValue(propertyName)) {
				return false;
			} else {
				inject = getComponentRttiFromKeySource(propertyName);
			}
			propDesc.setValue(inject);
			return true;
		}
	}

}
