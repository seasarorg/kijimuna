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
package org.seasar.kijimuna.core.internal.dicon.validation;

import java.util.Iterator;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.model.IArgElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IMethodElement;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MethodValidation implements IValidation, ConstCore {

    public void validation(IDiconElement element) {
        if(element instanceof IMethodElement) {
            validMethod((IMethodElement)element);
        }
    }

    private void validMethod(IMethodElement method) {
		String methodName = method.getMethodName();
		String el = method.getExpression();
		if(StringUtils.existValue(methodName)) {
			if(StringUtils.existValue(el)) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.MethodValidation.1",
			            method); 
			}
		} else {
			if(StringUtils.noneValue(el)) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.MethodValidation.2",
			            method); 
			}
			for(Iterator it = method.getArgList().iterator(); it.hasNext();) {
			    IArgElement arg = (IArgElement)it.next(); 
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.MethodValidation.3",
			            arg); 
			}
		}
        
    }
    
}
