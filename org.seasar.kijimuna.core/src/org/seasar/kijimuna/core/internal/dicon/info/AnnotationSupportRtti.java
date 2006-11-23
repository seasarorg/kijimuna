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

import java.util.regex.Pattern;

import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiWrapper;

public class AnnotationSupportRtti extends RttiWrapper {

	public AnnotationSupportRtti(IRtti rtti) {
		super(rtti);
	}
	
	public IRttiPropertyDescriptor getProperty(String name) {
		IRttiPropertyDescriptor propDesc = super.getProperty(name);
		return propDesc != null ? new AnnotationSupportRttiPropertyDescriptor(
				propDesc) : null;
	}
	
	public IRttiPropertyDescriptor[] getProperties(Pattern pattern) {
		IRttiPropertyDescriptor[] propDescs = super.getProperties(pattern);
		for (int i = 0; i < propDescs.length; i++) {
			propDescs[i] = new AnnotationSupportRttiPropertyDescriptor(
					propDescs[i]);
		}
		return propDescs;
	}

}
