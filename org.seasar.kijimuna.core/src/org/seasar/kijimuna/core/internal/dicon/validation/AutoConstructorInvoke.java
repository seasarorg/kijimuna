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
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoConstructorInvoke implements IValidation, ConstCore {

    public void validation(IDiconElement element) {
        if(element instanceof IComponentElement) {
            IComponentElement component = (IComponentElement)element;
            String el = component.getExpression();
            if((component.getArgList().size() == 0) && StringUtils.noneValue(el)) {
                String autoBinding = component.getAutoBindingMode();
                if(autoBinding.equals(DICON_VAL_AUTO_BINDING_AUTO) || 
                        autoBinding.equals(DICON_VAL_AUTO_BINDING_CONSTRUCTOR)) {
                    autoAssembler(component);
                } else {
                    defaultAssembler(component);
                }
            }
        }
    }
    
	private void reportInjection(IComponentElement component,
	        IRtti[] suitableArgs, IRtti[] injectedArgs, String display) {
		for(int i = 0; i < injectedArgs.length; i++) {
            if(injectedArgs[i] instanceof IComponentNotFound) {
				MarkerSetting.createDiconMarker(
				        "dicon.validation.AutoConstructorInvoke.3",
				        component, new Object[] { display, new Integer(i + 1) });
            } else if(injectedArgs[i] instanceof ITooManyRegisted) {
				MarkerSetting.createDiconMarker(
				        "dicon.validation.AutoConstructorInvoke.5",
				        component, new Object[] { display, new Integer(i + 1) });
            } else if(injectedArgs[i] != null) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.AutoConstructorInvoke.2",
				        component, new Object[] {
                        	display, new Integer(i + 1),
                        	ModelUtils.getInjectedElementName(injectedArgs[i]) });
            }
		}
	}
	
	private boolean checkCyclicReference(
	        IComponentElement component, IRtti injectedArg, int num, String display) {
	    if(DICON_VAL_INSTANCE_SINGLETON.equals(component.getInstanceMode())) {
	        IComponentElement injectedElement = 
            	(IComponentElement)injectedArg.getAdapter(IComponentElement.class);
            if(component.equals(injectedElement)) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.AutoConstructorInvoke.4",
				        component, new Object[] { display, new Integer(num + 1) });
                return true;
            }
	    }
	    return false;
	}
	
	// 自動にコンストラクタを探すアッセンブル
	private void autoAssembler(IComponentElement component) {
	    IComponentInfo info = 
	        (IComponentInfo)component.getAdapter(IComponentInfo.class);
	    if(info != null) {
	        IRttiConstructorDesctiptor suitable = info.getAutoInjectedConstructor();
	        if(suitable != null) {
			    IRtti[] suitableArgs = suitable.getArgs();
	            IRtti[] injectedArgs = suitable.getValues();
	            if(injectedArgs != null) {
					String display = ModelUtils.getConstructorDisplay(suitable, true);
					for(int i = 0; i < suitableArgs.length; i++) {
					    if(injectedArgs[i] != null) {
					        checkCyclicReference(component, injectedArgs[i], i, display);
					    }
				    }
				    reportInjection(component, suitableArgs, injectedArgs, display);
	            }
	        }
	    }
	}

	// デフォルトコンストラクタで行うアッセンブル
	private void defaultAssembler(IComponentElement component) {
	    IRttiConstructorDesctiptor suitable = (IRttiConstructorDesctiptor)
	    		component.getAdapter(IRttiConstructorDesctiptor.class);
		if(suitable == null) {
			int aspectSize = component.getAspectList().size();
			if(aspectSize == 0) {
				String qualifiedName = component.getComponentClassName();
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.AutoConstructorInvoke.1",
			            component, new Object[] { qualifiedName });
			}
		}
	}
	
}
