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

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public final class ComponentKeyString
	implements IComponentKey, ConstCore {

    private String componentKey;
    private int tooMany;
    
    public ComponentKeyString(String componentKey) {
        this.componentKey = componentKey;
    }

    public void setTooMany(int tooMany) {
        this.tooMany = tooMany;
    }
    
    public int getTooMany() {
        return tooMany;
    }
    
    public String getDisplayName() {
        return componentKey;
    }
    
    public int getKeyType() {
        return STRING;
    }
    
    public Object getAdapter(Class adapter) {
        return null;
    }
    
    public int compareTo(Object test) {
    	if(test instanceof IComponentKey) {
    		IComponentKey testKey = (IComponentKey)test;
    		if(testKey.getKeyType() == INTERFACE) {
    			return -1;
    		}
    		return getDisplayName().compareTo(testKey.getDisplayName());
    	}
    	return 0;
    }    

    public boolean equals(Object test) {
    	if(test instanceof ComponentKeyString) {
    		ComponentKeyString testKey = (ComponentKeyString)test;
   			return componentKey.equals(testKey.componentKey);
    	}
    	return false;
    }
    
    public int hashCode() {
        return componentKey.hashCode();
    }
    
    public String toString() {
        return componentKey;
    }

}
