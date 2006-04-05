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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconOgnlRtti;
import org.seasar.kijimuna.core.dicon.ModelManager;
import org.seasar.kijimuna.core.dicon.info.IDirectAccessed;
import org.seasar.kijimuna.core.dicon.model.IExpressionElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
abstract class AbstractExpressionElement
		extends DiconElement implements IExpressionElement {

	protected IRtti rtti;
    private boolean locking;

	
	public boolean isLocking() {
	    return locking;
	}
	
	public void setLocking(boolean validating) {
	    this.locking = validating;
	}
	
	protected AbstractExpressionElement(
	        IProject project, IStorage storage, String elementName) {
		super(project, storage, elementName);
	}
	
	public String getExpression() {
		return StringUtils.replaceIgnorableChars(getBody());
	}
	
	protected abstract IRtti getNonExpressionValue();

	protected IRtti getExpressionValue(String el) {
		DiconOgnlRtti ognlRtti = new DiconOgnlRtti(getRttiLoader());
		return ognlRtti.getValue(getContainerElement(), el);
	}
	
    public Object getAdapter(Class adapter) {
        if(IRtti.class.equals(adapter)) {
            if((rtti == null) || !isLocking()) {
	    		String el = getExpression();
    		    if(StringUtils.existValue(el)) {
    				rtti = getExpressionValue(el);
    			} else {
    			    rtti = getNonExpressionValue();
    			}
    		    if(rtti != null) {
	    		    IStorage storage = (IStorage)rtti.getAdapter(IStorage.class);
	    		    if(storage != null) {
	    		        ModelManager model = getNature().getModel();
	    		        model.addContainerAndRelatedFile(getContainerElement(), storage);
	    		    }
    		    }
            }
    		return rtti;
        }
        return super.getAdapter(adapter);
    }

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		if(isOGNL()) {
			String expression = getExpression();
			buffer.append("[").append(expression).append("]");
		} else {
			IRtti value = (IRtti)getAdapter(IRtti.class);
			if(value != null) {
			    buffer.append(value.getQualifiedName());
			} else {
			    buffer.append(KijimunaCore.getResourceString(
			            "dicon.model.AbstractExpressionElement.1"));
			}
		}
		return buffer.toString();
	}

	public boolean isOGNL() {
		String expression = getExpression();
	    if(StringUtils.existValue(expression)) {
			IRtti value = (IRtti)getAdapter(IRtti.class);
			if(value instanceof HasErrorRtti) {
	            return false;
	        } else if(!(value instanceof IDirectAccessed)) {
	            return true;
	        }
	    }
	    return false;	
	}
	
}
