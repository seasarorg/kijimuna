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
import org.seasar.kijimuna.core.dicon.info.IAspectInfo;
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AspectValidation implements IValidation, ConstCore {

	private static IRtti interceptor;
    
    public void validation(IDiconElement element) {
        if(element instanceof IAspectElement) {
            IAspectElement aspect = (IAspectElement)element;
            validAspect(aspect);
            validAOP(aspect);
        }
    }
    
    private boolean hasClassName(IAspectElement aspect) {
        IComponentElement parent = (IComponentElement)aspect.getParent();
		String className = parent.getComponentClassName();
		return StringUtils.existValue(className);
    }
    
    private IRtti getInterceptorRtti(IAspectElement aspect) {
        if(interceptor == null) {
        	interceptor = aspect.getRttiLoader().loadRtti(
        	        MODEL_INTERFACE_INTERCEPTOR);
        	if(interceptor instanceof HasErrorRtti) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.AspectValidation.2", 
			            aspect);
        	}
        }
        return interceptor;
    }
    
    private void validAspect(IAspectElement aspect) {
        IRtti rtti = (IRtti)aspect.getAdapter(IRtti.class);
		if(rtti != null) {
		    IRtti interceptor = getInterceptorRtti(aspect);
			if(!(interceptor instanceof HasErrorRtti) &&
					!interceptor.isAssignableFrom(rtti)) {
			    MarkerSetting.createDiconMarker(
			            "dicon.validation.AspectValidation.1", 
			            aspect);
			}
		}
		if(!hasClassName(aspect)) {
		    MarkerSetting.createDiconMarker(
		            "dicon.validation.AspectValidation.3", 
		            aspect);
		}
    }

    private void validApplyMethod(
            IAspectElement aspect, IRttiMethodDesctiptor[] methods) {
        for(int i = 0; i < methods.length; i++) {
            if(methods[i] != null) {
	            if(methods[i].isFinal() || methods[i].isStatic()) {
					String display = ModelUtils.getMethodDisplay(methods[i], true);
					String errorID;
					if(methods[i].isFinal()) {
					    errorID = "dicon.validation.AspectValidation.6"; 
					} else {
					    errorID = "dicon.validation.AspectValidation.7"; 
					}
				    MarkerSetting.createDiconMarker(
				            errorID, aspect, new Object[] { display }); 
	            }
            }
        }
    }
    
    private void validAOP(IAspectElement aspect) {
        IRtti rtti = ModelUtils.getComponentRtti(aspect);
        IAspectInfo info = (IAspectInfo)aspect.getAdapter(IAspectInfo.class);
        if(info != null) {
	        IPointcut[] pointcuts = info.getPointcuts();
	        if((pointcuts.length == 1) && pointcuts[0].isAutoApply()) {
                IRtti[] implementings = pointcuts[0].getImplementings();
	            if(implementings.length == 0) {
				    MarkerSetting.createDiconMarker(
				            "dicon.validation.AspectValidation.9" , 
				            aspect, new Object[] { rtti.getQualifiedName() }); 
	            } else {
	               for(int i = 0; i < implementings.length; i++) {
	                   IRttiMethodDesctiptor[] methods = 
	                       ModelUtils.getImplementMethods(rtti, implementings[i]);
	                   validApplyMethod(aspect, methods);
	               }
	            }
	        } else {
		        for(int i = 0; i < pointcuts.length; i++) {
			        if(pointcuts[i].hasError()) {
			            MarkerSetting.createDiconMarker(
					            "dicon.validation.AspectValidation.5",
					            aspect, new Object[] { pointcuts[i].getErrorMessage() });
			        } else if(hasClassName(aspect)) {
				        IRttiMethodDesctiptor[] methods = pointcuts[i].getApplyMethods();
				        validApplyMethod(aspect, methods);
			        }
		        }
	        }
		}
        if((rtti != null) && rtti.isFinal()) {
		    MarkerSetting.createDiconMarker(
		            "dicon.validation.AspectValidation.8",
		            aspect, new Object[] { rtti.getQualifiedName() });
        }
    }
    
}
