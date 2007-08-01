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
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.internal.dicon.autobinding.AutoBindingComponentProvider;
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

	private IRttiPropertyDescriptor[] autoInjectedProperties() {
		String autoBinding = getAutoBindingMode();
		if (autoBinding.equals(DICON_VAL_AUTO_BINDING_AUTO)
				|| autoBinding.equals(DICON_VAL_AUTO_BINDING_PROPERTY)) {
			IRtti rtti = (IRtti) getAdapter(IRtti.class);
			if (rtti != null) {
				List list = new ArrayList();
				IRttiPropertyDescriptor[] propDescs = rtti.getProperties(PATTERN_ANY);
				for (int i = 0; i < propDescs.length; i++) {
					IRttiPropertyDescriptor propDesc = propDescs[i];
					if (!propDesc.isWritable()) {
						continue;
					}
					if (ModelUtils.hasPropertyElement(this, propDesc.getName())) {
						continue;
					}
					if (ModelUtils.doDesignTimeAutoBinding(propDesc.getType())) {
						processAutoBinding(propDesc);
					}
					if (propDesc.getValue() != null) { 
						list.add(propDesc);
					}
				}
				return (IRttiPropertyDescriptor[]) list
						.toArray(new IRttiPropertyDescriptor[list.size()]);
			}
		}
		return new IRttiPropertyDescriptor[0];
	}
	
	private void processAutoBinding(IRttiPropertyDescriptor propDesc) {
		AutoBindingComponentProvider provider =
			new AutoBindingComponentProvider(getContainerElement());
		IRtti rtti = provider.getAutoBindingComponentRtti(propDesc);
		propDesc.setValue(rtti);
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
						return autoInjectedProperties();
					}
				};
			}
			return info;
		} else if (IRttiConstructorDesctiptor.class.equals(adapter)) {
			getAdapter(IRtti.class);
			return getConstructorDescriptor();
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

}
