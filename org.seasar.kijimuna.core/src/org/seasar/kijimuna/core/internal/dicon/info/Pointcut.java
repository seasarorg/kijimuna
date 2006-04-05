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
import java.util.regex.PatternSyntaxException;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class Pointcut implements IPointcut {
    
    private IAspectElement aspect;
    private String regexp;
    private String message;
    private Pattern pattern;
    private IRtti[] implementings;
    private IRttiMethodDesctiptor[] methods;
    private boolean autoApply;
    
    public Pointcut(IAspectElement aspect, String regexp) {
        this.aspect = aspect;
        if(regexp != null) {
	        this.regexp = regexp;
        } else {
            autoApply = true;
        }
        getPattern();
    }
    
    private Pattern getPattern() {
        if(pattern == null) {
	        try {
	            String r = regexp;
	            if(autoApply) {
	                r = ".*";
	            }
	            pattern = Pattern.compile(r);
	        } catch (PatternSyntaxException e) {
	            message = e.getMessage();
	        }
        }
        return pattern;
    }
    
    public boolean isAutoApply() {
        return autoApply;
    }
    
    public IRtti getInstanceRtti() {
    	return ModelUtils.getComponentRtti(aspect);
    }
    
    public IRtti[] getImplementings() {
        if(implementings == null) {
            implementings = new IRtti[0];
            if(autoApply) {
                methods = new IRttiMethodDesctiptor[0];
	            IRtti rtti = ModelUtils.getComponentRtti(aspect);
	            if(rtti != null) {
	                implementings = rtti.getInterfaces();
	            }
            }
        }
        return implementings;
    }
    
    public IRttiMethodDesctiptor[] getApplyMethods() {
        if(methods == null) {
            methods = new IRttiMethodDesctiptor[0];
            if(!autoApply) {
                implementings = new IRtti[0];
	            Pattern p = getPattern();
	            if(p != null) {
	            	IRtti rtti = getInstanceRtti();
	                if(rtti != null) {
	                	methods = rtti.getMethods(p);
	                }
	            }
            }
        }
        return methods;
    }

    public IRttiMethodDesctiptor[] getApplyMethods(IRtti implementing) {
        if(autoApply) {
        	IRtti rtti = getInstanceRtti();
            return ModelUtils.getImplementMethods(rtti, implementing);
        }
        return new IRttiMethodDesctiptor[0];
    }
    
    public String getRegexp() {
        if(regexp != null) {
            return regexp;
        }
        return KijimunaCore.getResourceString("dicon.tools.Pointcut.1");
    }

    public int compareTo(Object test) {
        if(test instanceof IPointcut) {
            IPointcut pointcut = (IPointcut)test;
            return getRegexp().compareTo(pointcut.getRegexp());
        }
        return 0;
    }
    
    public String getErrorMessage() {
        return message;
    }

    public boolean hasError() {
        return (message != null);
    }

}
