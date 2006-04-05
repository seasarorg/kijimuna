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

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconOgnlRtti;
import org.seasar.kijimuna.core.dicon.info.IApplyMethodInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IArgElement;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IMethodElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MethodElement extends AbstractExpressionElement implements IMethodElement,
		ConstCore {

	private IApplyMethodInfo info;
	private IRttiMethodDesctiptor suitableMethod;

	public MethodElement(IProject project, IStorage storage, String elementName) {
		super(project, storage, elementName);
	}

	public String getMethodName() {
		return getAttribute(DICON_ATTR_NAME);
	}

	public List getArgList() {
		return getChildren(DICON_TAG_ARG);
	}

	private IRttiMethodDesctiptor getSuitableMethod(IRtti component, String methodName) {
		if (component != null) {
			IRttiMethodDesctiptor[] methods = component.getMethods(Pattern
					.compile(methodName));
			IRttiMethodDesctiptor suitable = null;
			int size = -1;
			for (int i = 0; i < methods.length; i++) {
				IRtti[] rttiArgs = methods[i].getArgs();
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
						suitable = methods[i];
					}
				}
			}
			return suitable;
		}
		return null;
	}

	private IRttiMethodDesctiptor findAutoInjectedMethod() {
		String methodName = getMethodName();
		if (StringUtils.existValue(methodName) && (getArgList().size() == 0)) {
			if (suitableMethod == null) {
				IComponentElement component = (IComponentElement) getParent();
				if (StringUtils.existValue(component.getComponentClassName())) {
					IRtti rtti = (IRtti) component.getAdapter(IRtti.class);
					if (rtti != null) {
						IRttiMethodDesctiptor suitable = getSuitableMethod(rtti,
								getMethodName());
						if (suitable != null) {
							IRtti[] suitableArgs = suitable.getArgs();
							IRtti[] injectedArgs = new IRtti[suitableArgs.length];
							for (int i = 0; i < suitableArgs.length; i++) {
								IContainerElement container = getContainerElement();
								IComponentKey key = container
										.createComponentKey(suitableArgs[i]);
								if (ModelUtils.doDesignTimeAutoBinding(suitableArgs[i])) {
									IRtti inject = container.getComponent(key);
									injectedArgs[i] = inject;
								}
							}
							suitable.setValues(injectedArgs);
							suitableMethod = suitable;
						}
					}
				}
			}
			return suitableMethod;
		}
		return null;
	}

	private IRttiMethodDesctiptor getMethodDescriptor() {
		String methodName = getMethodName();
		if (StringUtils.existValue(methodName)) {
			if (getArgList().size() == 0) {
				suitableMethod = findAutoInjectedMethod();
			} else {
				IRtti component = ModelUtils.getComponentRtti(getParent());
				if (!(component instanceof HasErrorRtti)) {
					List args = getArgList();
					IRtti[] rttiArgs = new IRtti[args.size()];
					boolean flag = true;
					for (int i = 0; i < rttiArgs.length; i++) {
						IRtti rtti = (IRtti) ((IArgElement) args.get(i))
								.getAdapter(IRtti.class);
						if (rtti instanceof HasErrorRtti) {
							flag = false;
							break;
						}
						rttiArgs[i] = rtti;
					}
					if (flag) {
						suitableMethod = component.getMethod(methodName, rttiArgs, false);
					}
				}
			}
		}
		return suitableMethod;
	}

	protected IRtti getNonExpressionValue() {
		return null;
	}

	protected IRtti getExpressionValue(String el) {
		RttiLoader loader = getRttiLoader();
		DiconOgnlRtti ognlRtti = new DiconOgnlRtti(loader);
		ognlRtti.setComponent((IComponentElement) getParent());
		return ognlRtti.getValue(getContainerElement(), el);
	}

	public boolean isOGNL() {
		return StringUtils.noneValue(getMethodName());
	}

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		String method = getMethodName();
		if (isOGNL()) {
			String expression = getExpression();
			if (StringUtils.existValue(expression)) {
				buffer.append("[").append(expression).append("]");
			} else {
				buffer.append("[...]");
			}
		} else {
			buffer.append(method);
		}
		return buffer.toString();
	}

	public Object getAdapter(Class adapter) {
		if (IApplyMethodInfo.class.equals(adapter)) {
			if (info == null) {
				info = new IApplyMethodInfo() {

					public IRttiMethodDesctiptor getAutoInjectedMethod() {
						return findAutoInjectedMethod();
					}
				};
			}
			return info;
		} else if (IRttiMethodDesctiptor.class.equals(adapter)) {
			return getMethodDescriptor();
		}
		return super.getAdapter(adapter);
	}

}
