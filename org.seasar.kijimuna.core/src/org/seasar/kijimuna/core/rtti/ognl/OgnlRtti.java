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
package org.seasar.kijimuna.core.rtti.ognl;

import org.ognl.el.Expression;
import org.ognl.el.ExpressionSyntaxException;
import org.ognl.el.OgnlException;
import org.ognl.el.PropertyAccessor;
import org.ognl.el.extensions.DefaultExecutionEnvironment;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.rtti.ognl.OgnlExtensions;
import org.seasar.kijimuna.core.internal.rtti.ognl.OgnlRttiUnprocessable;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class OgnlRtti {

	private DefaultExecutionEnvironment environment;
	private OgnlExtensions extensions;
	private RttiLoader rootLoader;
	
	public OgnlRtti(RttiLoader rootLoader) {
		this.rootLoader = rootLoader;
		extensions = new OgnlExtensions(rootLoader);
		environment = new DefaultExecutionEnvironment();
		environment.setExtensions(extensions);
	}
	
	public void setVariableValue(String key, Object value) {
		environment.setVariableValue(key, value);
	}

	public void setPropertyAccessor(
			Class clazz, PropertyAccessor accessor) {
		extensions.setPropertyAccessor(clazz, accessor);
	}
	
	public RttiLoader getRttiLoader() {
	    return rootLoader;
	}
	
	public IRtti getValue(Object root, String el) {
		try {
		    Expression expression = environment.parseExpression(el);
			Object ret = environment.getValue(expression, root);
			if(ret != null) {
				if(ret instanceof IRtti) {
					return (IRtti)ret;
				}
				return rootLoader.loadRtti(ret.getClass());
			}
		} catch(OgnlRttiUnprocessable e) {
		} catch(ExpressionSyntaxException e) {
			return rootLoader.loadHasErrorRtti(null, KijimunaCore.getResourceString(
			        "rtti.ognl.OgnlRtti.1", new Object[] { el }));
		} catch(OgnlException e) {
		    return rootLoader.loadHasErrorRtti(null, e.getMessage());
		}
	    return null;
	}

}