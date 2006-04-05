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
package org.seasar.kijimuna.ui.internal.provider.dicon.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentKeyRootProperty extends NullProperty {

    private static final String COUNT_ID = "key num";  
    private static final String TOOMANY_ID = "too many";  
    private static final String NORMAL_ID = "normal";  
    
    private static IPropertyDescriptor[] descriptors;
    
    static {
        descriptors = new IPropertyDescriptor[3];
        PropertyDescriptor d = new PropertyDescriptor(
		        COUNT_ID, KijimunaUI.getResourceString(
		                "dicon.provider.property.ComponentKeyRootProperty.1"));
        d.setCategory(KijimunaUI.getResourceString(
                "dicon.provider.property.ContentProperty.4"));
        descriptors[0] = d;        
        d = new PropertyDescriptor(
                TOOMANY_ID, KijimunaUI.getResourceString(
		                "dicon.provider.property.ComponentKeyRootProperty.2"));
        d.setCategory(KijimunaUI.getResourceString(
                "dicon.provider.property.ContentProperty.4"));
        descriptors[1] = d;        
        d = new PropertyDescriptor(
                NORMAL_ID, KijimunaUI.getResourceString(
		                "dicon.provider.property.ComponentKeyRootProperty.3"));
        d.setCategory(KijimunaUI.getResourceString(
                "dicon.provider.property.ContentProperty.4"));
        descriptors[2] = d;        
    }
    
    private IComponentKey[] keys;
    
    public ComponentKeyRootProperty(IComponentKey[] keys) {
        this.keys = keys;
    }

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	private Integer countKey(boolean tooMany) {
	    int count = 0;
	    for(int i = 0; i < keys.length; i++) {
	        if((keys[i].getTooMany() != IComponentKey.NOT_TOO_MANY) == tooMany) {
	            count++;
	        }
	    }
	    return new Integer(count);
	}
	
	public Object getPropertyValue(Object id) {
		if(COUNT_ID.equals(id)) {
	        return new Integer(keys.length);
		} else if(TOOMANY_ID.equals(id)){
		    return countKey(true);
		} else if(NORMAL_ID.equals(id)) {
		    return countKey(false);
		}
		return null;
	}
    
    
}
