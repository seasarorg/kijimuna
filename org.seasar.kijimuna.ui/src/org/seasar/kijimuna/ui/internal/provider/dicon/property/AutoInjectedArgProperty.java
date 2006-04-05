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
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiInvokableDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedArgProperty extends NullProperty {

    private static IPropertyDescriptor[] descriptors;
    
    static {
        // info category
        String category = KijimunaUI.getResourceString(
                "dicon.provider.property.ContentProperty.4");
        descriptors = new IPropertyDescriptor[5];
        // method name
        String id = "dicon.provider.property.AutoInjectedArgProperty.1";
        PropertyDescriptor d = 
            new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
        d.setCategory(category);
        descriptors[0] = d;
        // return type
        id = "dicon.provider.property.AutoInjectedArgProperty.2";
        d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
        d.setCategory(category);
        descriptors[1] = d;
        // arg index
        id = "dicon.provider.property.AutoInjectedArgProperty.3";
        d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
        d.setCategory(category);
        descriptors[2] = d;
        // arg type
        id = "dicon.provider.property.AutoInjectedArgProperty.4";
        d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
        d.setCategory(category);
        descriptors[3] = d;
        // injected component
        id = "dicon.provider.property.AutoInjectedArgProperty.5";
        d = new PropertyDescriptor(id, KijimunaUI.getResourceString(id));
        d.setCategory(category);
        descriptors[4] = d;
    }
	
    private IRttiInvokableDesctiptor method;
    private int index;
	
	public AutoInjectedArgProperty(IRttiInvokableDesctiptor method, int index) {
		this.method = method;
		this.index = index;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		if("dicon.provider.property.AutoInjectedArgProperty.1".equals(id)) {
		    return ModelUtils.getMethodDisplay(method, true);
		} else if("dicon.provider.property.AutoInjectedArgProperty.2".equals(id)) {
		    return method.getReturnType().getQualifiedName();
		} else if("dicon.provider.property.AutoInjectedArgProperty.3".equals(id)) {
		    return new Integer(index);
		} else if("dicon.provider.property.AutoInjectedArgProperty.4".equals(id)) {
		    return method.getArgs()[index].getQualifiedName();
		} else if("dicon.provider.property.AutoInjectedArgProperty.5".equals(id)) {
		    IRtti[] values = method.getValues();
		    if(values != null) {
			    if(values[index] instanceof IComponentNotFound) {
			        return KijimunaUI.getResourceString(
			                "dicon.provider.property.AutoInjectedArgProperty.6",
			                new Object[] { values[index].getAdapter(IComponentKey.class) });
			    } else if(values[index] instanceof ITooManyRegisted) {
			        return ((ITooManyRegisted)values[index]).getErrorMessage();
			    } else {
		            return ModelUtils.getInjectedElementName(values[index]);
			    }
		    }
		} 
		return null;
	}

}
