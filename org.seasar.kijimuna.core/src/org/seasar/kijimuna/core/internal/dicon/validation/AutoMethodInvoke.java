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

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.IApplyMethodInfo;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IMethodElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoMethodInvoke implements IValidation, ConstCore {
	
    public void validation(IDiconElement element) {
        if(element instanceof IMethodElement) {
            try {
            	invoke((IMethodElement)element);
            } catch(Exception e) {
            	e.printStackTrace();
            }
        }
    }

	private void invoke(IMethodElement method) {
		String methodName = method.getMethodName();
		if((StringUtils.noneValue(methodName)) ||
		        (method.getArgList().size() > 0)) {
			return;
		}
		IRtti component = ModelUtils.getComponentRtti(method);
		IApplyMethodInfo info = (IApplyMethodInfo)method.getAdapter(IApplyMethodInfo.class);
		if(info != null) {
		    IRttiMethodDesctiptor suitable = info.getAutoInjectedMethod();
		    if(suitable != null) {
			    IRtti[] suitableArgs = suitable.getArgs();
			    IRtti[] injectedArgs = suitable.getValues();
				String display = ModelUtils.getMethodDisplay(suitable, true);
		        if(injectedArgs != null) {
		            for(int i = 0; i < suitableArgs.length; i++) {
		                if(injectedArgs[i] instanceof IComponentNotFound) {
							MarkerSetting.createDiconMarker(
							        "dicon.validation.AutoMethodInvoke.2",
							        method, new Object[] { display, new Integer(i + 1) });
		                } else if(injectedArgs[i] instanceof ITooManyRegisted) {
							MarkerSetting.createDiconMarker(
							        "dicon.validation.AutoMethodInvoke.4",
							        method, new Object[] { display, new Integer(i + 1) });
		                } else if(injectedArgs[i] != null) {
							MarkerSetting.createDiconMarker(
		                        "dicon.validation.AutoMethodInvoke.1",
		                        method, new Object[] {
		                        	display, new Integer(i + 1),
		                        	ModelUtils.getInjectedElementName(injectedArgs[i]) });
		                }
		            }
		        }
		    } else {
				String display = ModelUtils.getMethodDisplay(
						component, methodName, new IRtti[0], true);
				MarkerSetting.createDiconMarker(
				        "dicon.validation.AutoMethodInvoke.3",
				        method, new Object[] { display });
		    }
		}
	}
}
