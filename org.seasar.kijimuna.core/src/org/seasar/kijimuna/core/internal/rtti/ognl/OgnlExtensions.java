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
package org.seasar.kijimuna.core.internal.rtti.ognl;

import org.ognl.el.ExecutionEnvironment;
import org.ognl.el.Extensions;
import org.ognl.el.MethodFailedException;
import org.ognl.el.OgnlException;
import org.ognl.el.PropertyAccessor;
import org.ognl.util.ClassRegistry;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiFieldDescriptor;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class OgnlExtensions extends Extensions {

	private RttiLoader loader;
	private ClassRegistry accessors = new ClassRegistry();

	public OgnlExtensions(RttiLoader loader) {
		this.loader = loader;
	}

	private IRtti convert(RttiLoader localLoader, Object obj)
			throws OgnlRttiUnprocessable {
		if (obj instanceof IRtti) {
			if (obj instanceof HasErrorRtti) {
				String message = ((HasErrorRtti) obj).getErrorMessage();
				throw new OgnlRttiUnprocessable(message);
			}
			return (IRtti) obj;
		}
		return localLoader.loadRtti(obj.getClass());
	}

	private IRtti[] convertArgs(RttiLoader localLoader, Object[] args)
			throws OgnlRttiUnprocessable {
		if (args != null) {
			IRtti[] rttiArgs = new IRtti[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					rttiArgs[i] = convert(localLoader, args[i]);
				}
			}
			return rttiArgs;
		}
		return new IRtti[0];
	}

	private IRtti invoke(IRtti rtti, String methodName, Object[] args,
			boolean staticAccess) throws OgnlRttiUnprocessable {
		RttiLoader localLoader = rtti.getRttiLoader();
		IRtti[] rttiArgs = convertArgs(localLoader, args);
		IRttiMethodDesctiptor descriptor = rtti.getMethod(methodName, rttiArgs,
				staticAccess);
		if (descriptor != null) {
			return descriptor.getReturnType();
		}
		return loader.loadHasErrorRtti(null, KijimunaCore.getResourceString(
				"rtti.ognl.OgnlExtensions.1", new Object[] {
						rtti.getQualifiedName(), methodName, rttiArgs
				}));
	}

	private PropertyAccessor getPropertyAccessor(Class forClass) {
		return (PropertyAccessor) accessors.get(forClass);
	}

	public void setPropertyAccessor(Class forClass, PropertyAccessor accessor) {
		accessors.put(forClass, accessor);
	}

	public Object callArrayConstructor(ExecutionEnvironment environment,
			String componentClassName, Object[] args) throws OgnlException {
		return loader.loadRtti(componentClassName + "[]");
	}

	public Object callConstructor(ExecutionEnvironment environment,
			String targetClassName, Object[] args) throws OgnlException {
		IRtti rtti = loader.loadRtti(targetClassName);
		if (rtti instanceof HasErrorRtti) {
			return rtti;
		}
		IRtti[] rttiArgs = convertArgs(loader, args);
		IRttiConstructorDesctiptor descriptor = rtti.getConstructor(rttiArgs);
		if (descriptor != null) {
			return rtti;
		}
		return loader.loadHasErrorRtti(null, KijimunaCore.getResourceString(
				"rtti.ognl.OgnlExtensions.2", new Object[] {
						targetClassName, rtti.getShortName(), rttiArgs
				}));
	}

	public Object callMethod(ExecutionEnvironment environment, Object target,
			String methodName, Object[] args) throws MethodFailedException, OgnlException {
		IRtti rtti = convert(loader, target);
		if (rtti instanceof HasErrorRtti) {
			return rtti;
		}
		return invoke(rtti, methodName, args, false);
	}

	public Object callStaticMethod(ExecutionEnvironment environment,
			String targetClassName, String methodName, Object[] args)
			throws MethodFailedException, OgnlException {
		IRtti rtti = loader.loadRtti(targetClassName);
		if (rtti instanceof HasErrorRtti) {
			return rtti;
		}
		return invoke(rtti, methodName, args, true);
	}

	public Object getIndexedPropertyValue(ExecutionEnvironment environment,
			Object source, Object index) throws OgnlException {
		throw new OgnlRttiUnsupportedOperationException();
	}

	public Object getNamedIndexedPropertyValue(ExecutionEnvironment environment,
			Object source, String propertyName, Object index) throws OgnlException {
		throw new OgnlRttiUnsupportedOperationException();
	}

	public Object getPropertyValue(ExecutionEnvironment environment, Object source,
			Object property) throws OgnlException {
		PropertyAccessor accessor = getPropertyAccessor(source.getClass());
		if (accessor != null) {
			return accessor.getPropertyValue(environment, source, property);
		}
		IRtti rtti = convert(loader, source);
		if (rtti instanceof HasErrorRtti) {
			return rtti;
		}
		String propertyName = property.toString();
		IRttiPropertyDescriptor prop = rtti.getProperty(propertyName);
		if ((prop != null) && prop.isReadable()) {
			return prop.getType();
		}
		IRttiFieldDescriptor field = rtti.getField(propertyName, false);
		if (field != null) {
			return field.getType();
		}
		return loader.loadHasErrorRtti(null, KijimunaCore.getResourceString(
				"rtti.ognl.OgnlExtensions.3", new Object[] {
						rtti.getQualifiedName(), propertyName
				}));
	}

	public Object getStaticFieldValue(ExecutionEnvironment environment,
			String targetClassName, String fieldName) throws OgnlException {
		IRtti rtti = loader.loadRtti(targetClassName);
		if (rtti instanceof HasErrorRtti) {
			return rtti;
		}
		IRttiFieldDescriptor field = rtti.getField(fieldName, true);
		if (field != null) {
			return field.getType();
		}
		return loader.loadHasErrorRtti(null, KijimunaCore.getResourceString(
				"rtti.ognl.OgnlExtensions.4", new Object[] {
						rtti.getQualifiedName(), fieldName
				}));
	}

	public void setIndexedPropertyValue(ExecutionEnvironment environment, Object target,
			Object index, Object value) throws OgnlException {
		throw new OgnlRttiUnsupportedOperationException();
	}

	public void setNamedIndexedPropertyValue(ExecutionEnvironment environment,
			Object target, String propertyName, Object index, Object value)
			throws OgnlException {
		throw new OgnlRttiUnsupportedOperationException();
	}

	public void setPropertyValue(ExecutionEnvironment environment, Object target,
			Object property, Object value) throws OgnlException {
		PropertyAccessor accessor = getPropertyAccessor(target.getClass());
		if (accessor != null) {
			accessor.setPropertyValue(environment, target, property, value);
		} else {
			IRtti rtti = convert(loader, target);
			if (rtti instanceof HasErrorRtti) {
				return;
			}
			String propertyName = property.toString();
			IRtti rttiValue = convert(loader, value);
			IRttiPropertyDescriptor prop = rtti.getProperty(propertyName);
			if ((prop == null) || !prop.isWritable()
					|| prop.getType().isAssignableFrom(rttiValue)) {
				IRttiFieldDescriptor field = rtti.getField(propertyName, false);
				if ((field == null) || (field.isFinal())
						|| !field.getType().isAssignableFrom(rttiValue)) {
					throw new OgnlRttiUnprocessable(KijimunaCore.getResourceString(
							"rtti.ognl.OgnlExtensions.5", new Object[] {
									rtti.getQualifiedName(), propertyName
							}));
				}
			}
		}
	}

}
