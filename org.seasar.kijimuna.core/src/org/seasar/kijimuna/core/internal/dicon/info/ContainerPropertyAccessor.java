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
package org.seasar.kijimuna.core.internal.dicon.info;

import org.ognl.el.ExecutionEnvironment;
import org.ognl.el.OgnlException;
import org.ognl.el.PropertyAccessor;

import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.internal.rtti.ognl.OgnlRttiUnprocessable;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiWrapper;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class ContainerPropertyAccessor implements PropertyAccessor {

	public Object getPropertyValue(ExecutionEnvironment environment, Object target,
			Object property) throws OgnlException {
		if (target instanceof ContainerRtti) {
			Object adapter = ((RttiWrapper) target).getAdapter(IContainerElement.class);
			if (adapter != null) {
				target = adapter;
			}
		}
		IContainerElement container = (IContainerElement) target;
		IComponentKey key = container.createComponentKey(property);
		IRtti rtti = container.getComponent(key);
		if ((rtti instanceof ContainerRtti) || (rtti instanceof HasErrorRtti)) {
			return rtti;
		}
		return new DirectAccessedRtti(rtti);
	}

	public Object getIndexedPropertyValue(ExecutionEnvironment environment,
			Object target, Object index) throws OgnlException {
		throw new OgnlRttiUnprocessable();
	}

	public Object getNamedIndexedPropertyValue(ExecutionEnvironment environment,
			Object target, String propertyName, Object index) throws OgnlException {
		throw new OgnlRttiUnprocessable();
	}

	public void setIndexedPropertyValue(ExecutionEnvironment environment, Object target,
			Object index, Object value) throws OgnlException {
		throw new OgnlRttiUnprocessable();
	}

	public void setNamedIndexedPropertyValue(ExecutionEnvironment environment,
			Object target, String propertyName, Object index, Object value)
			throws OgnlException {
		throw new OgnlRttiUnprocessable();
	}

	public void setPropertyValue(ExecutionEnvironment environment, Object target,
			Object property, Object value) throws OgnlException {
		throw new OgnlRttiUnprocessable();
	}

}
