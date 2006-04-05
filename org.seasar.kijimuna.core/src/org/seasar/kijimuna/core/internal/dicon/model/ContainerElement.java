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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentKeyRtti;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentKeyString;
import org.seasar.kijimuna.core.internal.dicon.info.ComponentNotFoundRtti;
import org.seasar.kijimuna.core.internal.dicon.info.ContainerRtti;
import org.seasar.kijimuna.core.internal.dicon.info.InjectedRtti;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ContainerElement extends DiconElement implements IContainerElement,
		ConstCore, Comparable {

	private IRtti s2ContainerRtti;
	private Map componentDefMap = new HashMap();
	private Map componentKeyMap = new HashMap();
	private Set containerKeySet = new TreeSet();
	private IComponentInfo info;

	public ContainerElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_CONTAINER);

		IComponentKey key = createComponentKey(MODEL_NAME_CONTAINER);
		componentDefMap.put(key, this);
		containerKeySet.add(key);
		IRtti rtti = getS2ContainerRtti();
		IComponentKey interfaceKey = createComponentKey(rtti);
		componentDefMap.put(interfaceKey, this);
		containerKeySet.add(interfaceKey);

		addMagicComponent(project, storage, MODEL_NAME_REQUEST, MODEL_INTERFACE_REQUEST);
		addMagicComponent(project, storage, MODEL_NAME_RESPONSE, MODEL_INTERFACE_RESPONSE);
		addMagicComponent(project, storage, MODEL_NAME_SESSION, MODEL_INTERFACE_SESSION);
		addMagicComponent(project, storage, MODEL_NAME_SERVLETCONTEXT,
				MODEL_INTERFACE_SERVLETCONTEXT);
	}

	private IRtti getS2ContainerRtti() {
		if (s2ContainerRtti == null) {
			s2ContainerRtti = getRttiLoader().loadRtti(MODEL_INTERFACE_S2CONTAINER);
		}
		return s2ContainerRtti;
	}

	private void addMagicComponent(IProject project, IStorage storage, String name,
			String clazz) {
		ComponentElement element = new ComponentElement(project, storage);
		HashMap property = new HashMap();
		property.put("name", name);
		property.put("class", clazz);
		element.setAttributes(property);
		// element.setStartLocation(2, 0, 0);
		element.setRootElement(this);
		// servletContext.setParent(this);
		IComponentKey servletContextName = createComponentKey(name);
		componentDefMap.put(servletContextName, element);
		containerKeySet.add(servletContextName);
		IRtti rtti = getRttiLoader().loadRtti(clazz);
		IComponentKey interfaceKey = createComponentKey(rtti);
		componentDefMap.put(interfaceKey, element);
		containerKeySet.add(interfaceKey);
	}

	public void setAttributes(Map properties) {
		super.setAttributes(properties);
		String namespace = getNamespace();
		if (StringUtils.existValue(namespace)) {
			IComponentKey key = createComponentKey(namespace);
			componentDefMap.put(key, this);
			containerKeySet.add(key);
		}
	}

	public void addChild(IElement child) {
		super.addChild(child);
		if (child instanceof IComponentElement) {
			registComponentElement((IComponentElement) child);
		}
	}

	public List getComponentList() {
		return getChildren(DICON_TAG_COMPONENT);
	}

	public List getIncludeList() {
		return getChildren(DICON_TAG_INCLUDE);
	}

	public String getNamespace() {
		return super.getAttribute(DICON_ATTR_NAMESPACE);
	}

	public String getPath() {
		IStorage storage = (IStorage) getAdapter(IStorage.class);
		if (storage != null) {
			return ProjectUtils.getResourceLoaderPath(storage);
		}
		return null;
	}

	public IComponentKey createComponentKey(Object key) {
		String keyString = key.toString();
		IComponentKey ret = (IComponentKey) componentKeyMap.get(keyString);
		if (ret == null) {
			if (key instanceof IRtti) {
				ret = new ComponentKeyRtti((IRtti) key);
			} else {
				ret = new ComponentKeyString(keyString);
			}
			componentKeyMap.put(keyString, ret);
		}
		return ret;
	}

	private void registComponentElement(IComponentElement component) {
		String instance = component.getInstanceMode();
		if (!instance.equals(DICON_VAL_INSTANCE_OUTER)) {
			IComponentInfo keyInfo = (IComponentInfo) component
					.getAdapter(IComponentInfo.class);
			if (keyInfo != null) {
				IComponentKey[] keys = keyInfo.getComponentKeys();
				for (int i = 0; i < keys.length; i++) {
					registComponentElementByKey(keys[i], component);
				}
			}
		}
	}

	private void registComponentElementByKey(IComponentKey key,
			IComponentElement component) {
		if (key != null) {
			Object registed = componentDefMap.get(key);
			if (registed == null) {
				componentDefMap.put(key, component);
			} else {
				if (key instanceof ComponentKeyRtti) {
					key.setTooMany(IComponentKey.TOO_MANY_PROBLEM);
				} else {
					key.setTooMany(IComponentKey.TOO_MANY_FETAL);
				}
				if (registed instanceof TooManyRegistrationHolder) {
					((TooManyRegistrationHolder) registed).addComponentElement(component);
				} else {
					IProject project = (IProject) getAdapter(IProject.class);
					IStorage storage = (IStorage) getAdapter(IStorage.class);
					if ((project != null) && (storage != null)) {
						TooManyRegistrationHolder holder = new TooManyRegistrationHolder(
								project, storage, key);
						holder.addComponentElement((IDiconElement) registed);
						holder.addComponentElement(component);
						componentDefMap.put(key, holder);
					}
				}
			}
		}
	}

	public IDiconElement findDefinition(IComponentKey componentKey, Stack stack) {
		IDiconElement element = (IDiconElement) componentDefMap.get(componentKey);
		if (element != null) {
			return element;
		}
		for (Iterator it = getIncludeList().iterator(); it.hasNext();) {
			IIncludeElement incl = (IIncludeElement) it.next();
			IContainerElement childContainer = incl.getChildContainer();
			if (childContainer != null && !stack.contains(childContainer)) {
				stack.push(childContainer);
				element = childContainer.findDefinition(componentKey, stack);
				stack.pop();
				if (element != null) {
					return element;
				}
			}
		}
		return null;
	}

	public IRtti getComponent(IComponentKey componentKey) {
		Stack stack = new Stack();
		IDiconElement element = findDefinition(componentKey, stack);
		if (element instanceof IContainerElement) {
			IContainerElement container = (IContainerElement) element;
			return new ContainerRtti(getS2ContainerRtti(), container, componentKey);
		} else if (element instanceof IComponentElement) {
			IRtti rtti = (IRtti) element.getAdapter(IRtti.class);
			if (rtti != null) {
				if (rtti instanceof ITooManyRegisted) {
					return rtti;
				}
				IComponentElement component = (IComponentElement) element;
				return new InjectedRtti(rtti, component, componentKey);
			}
		}
		return new ComponentNotFoundRtti(componentKey);
	}

	public Object getAdapter(Class adapter) {
		if (IRtti.class.equals(adapter)) {
			return getS2ContainerRtti();
		} else if (IComponentInfo.class.equals(adapter)) {
			if (info == null) {
				info = new IComponentInfo() {

					public IComponentKey[] getComponentKeys() {
						return (IComponentKey[]) containerKeySet
								.toArray(new IComponentKey[containerKeySet.size()]);
					}

					public IRttiConstructorDesctiptor getAutoInjectedConstructor() {
						return null;
					}

					public IRttiPropertyDescriptor[] getAutoInjectedProperties() {
						return new IRttiPropertyDescriptor[0];
					}
				};
			}
			return info;
		}
		return super.getAdapter(adapter);
	}

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getPath());
		String namespace = getNamespace();
		if (StringUtils.existValue(namespace)) {
			buffer.append("<").append(namespace).append(">");
		}
		return buffer.toString();
	}

	public int compareTo(Object test) {
		if (test instanceof IContainerElement) {
			IContainerElement container = (IContainerElement) test;
			return getPath().compareTo(container.getPath());
		}
		return 0;
	}

}
