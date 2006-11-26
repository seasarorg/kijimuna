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
package org.seasar.kijimuna.core.internal.dicon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.internal.dicon.binding.PropertyModel;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentNotFoundRtti;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentElement extends AbstractExpressionElement implements
		IComponentElement, ConstCore {

	private static final Pattern PATTERN_ANY = Pattern.compile(".*");
	
	private Set componentKeySet;
	private IComponentInfo info;
	private IRttiConstructorDesctiptor suitableConstructor;

	public ComponentElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_COMPONENT);
	}

	private IRttiConstructorDesctiptor getConstructorDescriptor() {
		String className = getComponentClassName();
		if (StringUtils.existValue(className) && (suitableConstructor == null)) {
			IRtti rtti = (IRtti) getAdapter(IRtti.class);
			if ((rtti != null) && !(rtti instanceof HasErrorRtti)) {
				if (getArgList().size() == 0) {
					suitableConstructor = findAutoInjectedConstructor();
				} else {
					suitableConstructor = rtti.getConstructor(ModelUtils
							.convertArray(getArgList().toArray()));
				}
			}
		}
		return suitableConstructor;
	}

	protected IRtti getExpressionValue(String el) {
		String className = getComponentClassName();
		return StringUtils.existValue(className) ? getRttiLoader().loadRtti(
				className) : super.getExpressionValue(el);
	}

	protected IRtti getNonExpressionValue() {
		String className = getComponentClassName();
		RttiLoader loader = getRttiLoader();
		if (StringUtils.existValue(className)) {
			return loader.loadRtti(className);
		} else if (isOuterInjection()) {
			return null;
		} else {
			return loader.loadHasErrorRtti(null, KijimunaCore
					.getResourceString("dicon.model.ComponentElement.1"));
		}
	}

	public String getComponentName() {
		return getAttribute(DICON_ATTR_NAME);
	}

	public String getComponentClassName() {
		return getAttribute(DICON_ATTR_CLASS);
	}

	public String getAutoBindingMode() {
		String autoBinding = getAttribute(DICON_ATTR_AUTOBINDING);
		if (StringUtils.noneValue(autoBinding)) {
			autoBinding = DICON_VAL_AUTO_BINDING_AUTO;
		}
		return autoBinding;
	}

	public String getInstanceMode() {
		String instance = getAttribute(DICON_ATTR_INSTANCE);
		if (instance == null) {
			instance = DICON_VAL_INSTANCE_SINGLETON;
		}
		return instance;
	}

	private synchronized Set createKeys() {
		if ((componentKeySet == null) || !isLocking()) {
			IContainerElement container = getContainerElement();
			componentKeySet = new TreeSet();
			String componentName = getComponentName();
			if (StringUtils.existValue(componentName)) {
				IComponentKey nameKey = container.createComponentKey(componentName);
				componentKeySet.add(nameKey);
			}
			if (StringUtils.existValue(getComponentClassName())) {
				String instance = getInstanceMode();
				if (!DICON_VAL_INSTANCE_OUTER.equals(instance)) {
					setLocking(true);
					IRtti rtti = (IRtti) getAdapter(IRtti.class);
					setLocking(false);
					if (rtti != null
							&& "java.lang.Object".equals(rtti.getQualifiedName()) == false) {
						do {
							IComponentKey classKey = container.createComponentKey(rtti);
							componentKeySet.add(classKey);
							IRtti interfaces[] = rtti.getInterfaces();
							for (int i = 0; i < interfaces.length; i++) {
								IComponentKey interfaceKey = container
										.createComponentKey(interfaces[i]);
								componentKeySet.add(interfaceKey);
							}
							rtti = rtti.getSuperClass();
						} while (rtti != null
								&& "java.lang.Object".equals(rtti.getQualifiedName()) == false);
					}
				}
			}
		}
		return componentKeySet;
	}

	public List getArgList() {
		return getChildren(DICON_TAG_ARG);
	}

	public List getAspectList() {
		return getChildren(DICON_TAG_ASPECT);
	}

	public List getPropertyList() {
		return getChildren(DICON_TAG_PROPERTY);
	}

	public List getInitMethodList() {
		return getChildren(DICON_TAG_INITMETHOD);
	}

	public List getDestroyMethodList() {
		return getChildren(DICON_TAG_DESTROYMETHOD);
	}

	private boolean isOuterInjection() {
		return DICON_VAL_INSTANCE_OUTER.equals(getInstanceMode());
	}

	private IRttiConstructorDesctiptor getSuitableConstructor(IRtti rtti) {
		int size = -1;
		IRttiConstructorDesctiptor suitable = null;
		IRttiConstructorDesctiptor[] constructors = rtti.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			IRtti[] rttiArgs = constructors[i].getArgs();
			if (rttiArgs.length == 0) {
				return constructors[i];
			}
			if (size < rttiArgs.length) {
				boolean flag = true;
				for (int k = 0; k < rttiArgs.length; k++) {
					if (!rttiArgs[k].isInterface()) {
						flag = false;
						break;
					}
				}
				if (flag) {
					size = rttiArgs.length;
					suitable = constructors[i];
				}
			}
		}
		return suitable;
	}

	private IRttiConstructorDesctiptor findAutoInjectedConstructor() {
		String el = getExpression();
		if ((getArgList().size() == 0) && StringUtils.noneValue(el)) {
			if (suitableConstructor == null) {
				String autoBinding = getAutoBindingMode();
				IRtti rtti = getRttiLoader().loadRtti(getComponentClassName());
				if ((rtti != null) && !(rtti instanceof HasErrorRtti)) {
					if (autoBinding.equals(DICON_VAL_AUTO_BINDING_AUTO)
							|| autoBinding.equals(DICON_VAL_AUTO_BINDING_CONSTRUCTOR)) {
						IRttiConstructorDesctiptor suitable = getSuitableConstructor(rtti);
						if (suitable != null) {
							IRtti[] suitableArgs = suitable.getArgs();
							IRtti[] injectedArgs = new IRtti[suitableArgs.length];
							for (int i = 0; i < suitableArgs.length; i++) {
								IContainerElement container = getContainerElement();
								IComponentKey key = container
										.createComponentKey(suitableArgs[i]);
								if (ModelUtils.doDesignTimeAutoBinding(suitableArgs[i])) {
									injectedArgs[i] = container.getComponent(key);
								}
							}
							suitable.setValues(injectedArgs);
							suitableConstructor = suitable;
						}
					} else {
						suitableConstructor = rtti.getConstructor(new IRtti[0]);
					}
				}
			}
			return suitableConstructor;
		}
		return null;
	}
	
	private IPropertyModel[] getPropertyModels() {
		IRtti rtti = (IRtti) getAdapter(IRtti.class);
		if (rtti == null) {
			return new IPropertyModel[0];
		}
		IRttiPropertyDescriptor[] propDescs = rtti.getProperties(PATTERN_ANY);
		processAutoBinding(propDescs);
		IPropertyModel[] propModels = new IPropertyModel[propDescs.length];
		for (int i = 0; i < propDescs.length; i++) {
			propModels[i] = new PropertyModel(propDescs[i],
					getPropertyElementFrom(propDescs[i]));
		}
		return propModels;
	}
	
	private void processAutoBinding(IRttiPropertyDescriptor[] propDescs) {
		String autoBinding = getAutoBindingMode();
		if (DICON_VAL_AUTO_BINDING_AUTO.equals(autoBinding) ||
				DICON_VAL_AUTO_BINDING_PROPERTY.equals(autoBinding)) {
			for (int i = 0; i < propDescs.length; i++) {
				IRttiPropertyDescriptor propDesc = propDescs[i];
				if (ModelUtils.doDesignTimeAutoBinding(propDesc.getType())) {
					createPropertyInjector(propDesc).inject();
				}
			}
		}
	}
	
	private IInjector createPropertyInjector(IRttiPropertyDescriptor propDesc) {
		IPropertyElement prop = getPropertyElementFrom(propDesc);
		return prop != null ? new ElementInjector(prop, propDesc) :
				(IInjector) new NonElementInjector(propDesc);
	}
	
	private IPropertyElement getPropertyElementFrom(IRttiPropertyDescriptor
			propDesc) {
		List props = getPropertyList();
		for (int i = 0; i < props.size(); i++) {
			IPropertyElement prop = (IPropertyElement) props.get(i);
			if (propDesc.getName().equals(prop.getPropertyName())) {
				return prop;
			}
		}
		return null;		
	}
	
	public Object getAdapter(Class adapter) {
		if (IComponentInfo.class.equals(adapter)) {
			if (info == null) {
				info = new IComponentInfo() {

					public IComponentKey[] getComponentKeys() {
						if (getDepth() == 2) {
							Set keys = createKeys();
							return (IComponentKey[]) keys.toArray(new IComponentKey[keys
									.size()]);
						}
						return new IComponentKey[0];
					}

					public IRttiConstructorDesctiptor getAutoInjectedConstructor() {
						return findAutoInjectedConstructor();
					}

					public IRttiPropertyDescriptor[] getAutoInjectedProperties() {
						return new IRttiPropertyDescriptor[0];
					}
				};
			}
			return info;
		} else if (IRttiConstructorDesctiptor.class.equals(adapter)) {
			getAdapter(IRtti.class);
			return getConstructorDescriptor();
		}
		// FIXME IPropertyModel[]でなくてIComponentInfoに入れるかどうか
		else if (IPropertyModel[].class.equals(adapter)) {
			return getPropertyModels();
		}
		return super.getAdapter(adapter);
	}

	public boolean isOGNL() {
		if (isOuterInjection()) {
			return false;
		} else if (StringUtils.noneValue(getComponentClassName())) {
			Object rtti = getAdapter(IRtti.class);
			if (rtti != null) {
				IComponentElement component = (IComponentElement) ((IAdaptable) rtti)
						.getAdapter(IComponentElement.class);
				if (component != null) {
					return component.isOGNL();
				}
			}
			return true;
		} else {
			return super.isOGNL();
		}
	}

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		if (isOuterInjection() && StringUtils.noneValue(getComponentClassName())) {
			buffer.append(KijimunaCore
					.getResourceString("dicon.model.ComponentElement.2"));
		} else {
			buffer.append(super.getDisplayName());
		}
		String name = getComponentName();
		if (StringUtils.existValue(name)) {
			buffer.append("<").append(name).append(">");
		}
		return buffer.toString();
	}

	public IComponentKey[] getTooManyComponentKeyArray(int tooMany) {
		List tooManyKey = new ArrayList();
		if (componentKeySet != null && componentKeySet.isEmpty() == false) {
			for (Iterator iterator = componentKeySet.iterator(); iterator.hasNext();) {
				IComponentKey key = (IComponentKey) iterator.next();
				if (key.getTooMany() == tooMany) {
					tooManyKey.add(key);
				}
			}
		}
		return (IComponentKey[]) tooManyKey.toArray(new IComponentKey[tooManyKey.size()]);
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
			return getContainerElement().getComponent(createComponentKey(keySource));
		}
		
		protected IComponentKey createComponentKey(Object keySource) {
			return getContainerElement().createComponentKey(keySource);
		}
		
		protected IComponentNotFound createComponentNotFoundRtti(Object key) {
			return new ComponentNotFoundRtti(createComponentKey(key));
		}
		
		protected boolean isAvailableBindingType(String bindingType) {
			return isAutoBindingRequired(bindingType) ||
					DICON_VAL_BINDING_TYPE_NONE.equals(bindingType);
		}
		
		protected boolean isAutoBindingRequired(String bindingType) {
			return DICON_VAL_BINDING_TYPE_MAY.equals(bindingType) ||
					DICON_VAL_BINDING_TYPE_SHOULD.equals(bindingType) ||
					DICON_VAL_BINDING_TYPE_MUST.equals(bindingType);
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
			String bt = prop.getBindingType();
			return prop.getChildren().size() == 0 &&
					StringUtils.noneValue(prop.getExpression()) &&
					isAutoBindingRequired(bt);
		}
	}
	
	
	private class NonElementInjector extends AbstractPropertyInjector {
		
		public NonElementInjector(IRttiPropertyDescriptor propDesc) {
			super(propDesc);
		}
		
		protected boolean canInject() {
			IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
					IBindingAnnotation.class);
			return ba == null || (ba != null && isAutoBindingRequired(ba
					.getBindingType()));
		}
		
		protected boolean injectByComponentName() {
			IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
					IBindingAnnotation.class);
			if (injectByComponentNameUsingAnnotation(propDesc, ba)) {
				return true;
			}
			return super.injectByComponentName();
		}
		
		private boolean injectByComponentNameUsingAnnotation(
				IRttiPropertyDescriptor propDesc, IBindingAnnotation ba) {
			if (ba == null) {
				return false;
			}
			IRtti inject = null;
			String propertyName = ba.getPropertyName();
			if (!isAvailableBindingType(ba.getBindingType())) {
				inject = createComponentNotFoundRtti(propDesc.getName());
			} else if (propertyName == null) {
				return false;
			} else {
				inject = getComponentRttiFromKeySource(propertyName);
			}
			propDesc.setValue(inject);
			return true;
		}
	}


}
