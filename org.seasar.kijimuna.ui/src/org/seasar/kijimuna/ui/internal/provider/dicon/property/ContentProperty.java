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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.info.IDirectAccessed;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ContentProperty extends NullProperty
		implements ConstCore {

    private static Map descriptorsMap;

    private static void descSetting(Map map, String tag, String[] id) {
        IPropertyDescriptor[] desc = new IPropertyDescriptor[id.length];
        for(int i = 0; i < id.length; i++) {
            String display;
            String category;
            if(DICON_BODY.equals(id[i])) {
                // expression
                display = KijimunaUI.getResourceString(
                        "dicon.provider.property.ContentProperty.1");
                category = KijimunaUI.getResourceString(
                	"dicon.provider.property.ContentProperty.4");
            } else if(DICON_ATTR_PATH.equals(id[i])) {
                // path
                display = KijimunaUI.getResourceString(
                	"dicon.provider.property.ContentProperty.2");
                category = KijimunaUI.getResourceString(
                	"dicon.provider.property.ContentProperty.4");
            } else if(DICON_INJECTED_VALUE.equals(id[i])) {
            	// injected value
                display = KijimunaUI.getResourceString(
            		"dicon.provider.property.ContentProperty.5");
                category = KijimunaUI.getResourceString(
                	"dicon.provider.property.ContentProperty.4");
            } else if(DICON_DESCRIPTION.equals(id[i])) {
            	// injected value
                display = KijimunaUI.getResourceString(
            		"dicon.provider.property.ContentProperty.6");
                category = KijimunaUI.getResourceString(
                	"dicon.provider.property.ContentProperty.4");
            } else {
                // xml attributes
                display = id[i];
                category = KijimunaUI.getResourceString(
                "dicon.provider.property.ContentProperty.3");
            }
            PropertyDescriptor d = new PropertyDescriptor(id[i], display);
            d.setCategory(category);
            desc[i] = d;
        }
        map.put(tag, desc);
    }
    
    static {
        descriptorsMap = new HashMap();
        descSetting(descriptorsMap, DICON_TAG_ARG, DICON_ATTRS_ARG); 
        descSetting(descriptorsMap, DICON_TAG_ASPECT, DICON_ATTRS_ASPECT); 
        descSetting(descriptorsMap, DICON_TAG_COMPONENT, DICON_ATTRS_COMPONENT); 
        descSetting(descriptorsMap, DICON_TAG_CONTAINER, DICON_ATTRS_CONTAINER); 
        descSetting(descriptorsMap, DICON_TAG_DESCRIPTION, DICON_ATTRS_DESCRIPTION); 
        descSetting(descriptorsMap, DICON_TAG_DESTROYMETHOD, DICON_ATTRS_DESTROYMETHOD); 
        descSetting(descriptorsMap, DICON_TAG_INITMETHOD, DICON_ATTRS_INITMETHOD); 
        descSetting(descriptorsMap, DICON_TAG_META, DICON_ATTRS_META); 
        descSetting(descriptorsMap, DICON_TAG_PROPERTY, DICON_ATTRS_PROPERTY); 
    }
    
    private IDiconElement element;
    
	public ContentProperty(IDiconElement element) {
	    this.element = element;
	}
    
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return (IPropertyDescriptor[])descriptorsMap.get(element.getElementName());
	}
	
	public Object getPropertyValue(Object id) {
	    if(DICON_BODY.equals(id)) {
	        return element.getBody(); 
	    } else if((element instanceof IContainerElement) &&
	    		DICON_ATTR_PATH.equals(id)) {
	    	return ((IContainerElement)element).getPath();
	    } else if(DICON_INJECTED_VALUE.equals(id)) {
	        IRtti rtti = (IRtti)element.getAdapter(IRtti.class);
	        if(rtti != null) {
	            if(rtti instanceof HasErrorRtti) {
	                return ((HasErrorRtti)rtti).getErrorMessage();
	            } else if(rtti instanceof IDirectAccessed) {
	                return ModelUtils.getInjectedElementName(rtti);
	            }
	        }
	        return null;
	    } else if(DICON_DESCRIPTION.equals(id)) {
	        String ret = element.getBody();
	        if(ret == null) {
	            ret = "";
	        }
	        return ret;
	    }
		return element.getAttribute(id.toString());
	}
	
}
