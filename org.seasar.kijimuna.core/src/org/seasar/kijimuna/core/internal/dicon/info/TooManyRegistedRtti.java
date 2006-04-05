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

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TooManyRegistedRtti extends HasErrorRtti
		implements ITooManyRegisted {
    
    private IComponentKey key;
    private IDiconElement[] components;
    
    private static String getKeysDisplay(
            IComponentKey key, IDiconElement[] components) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < components.length; i++) {
            if(i != 0) {
                buffer.append(", ");
            }
            buffer.append(components[i].getDisplayName());
        }
        return buffer.toString();
    }
    
    public TooManyRegistedRtti(IComponentKey key, IDiconElement[] components) {
        super(key.getDisplayName(),
                KijimunaCore.getResourceString("dicon.tools.TooManyRegistedRtti.1", 
                new Object[] { key, getKeysDisplay(key, components) }));
        this.key = key;
        this.components = components;
    }
    
    public IDiconElement[] getRegistedComponents() {
        return components;
    }
    
    protected IRtti getWrappedRtti() {
        if(key instanceof IRtti) {
           return (IRtti)key; 
        }
        return null;
    }
    
    public Object getAdapter(Class adapter) {
        if(IComponentKey.class.equals(adapter)) {
            return key;
        }
        return super.getAdapter(adapter);
    }
}
