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
package org.seasar.kijimuna.ui.internal.provider.dicon.walker;

import org.eclipse.ui.views.properties.IPropertySource;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public abstract class AbstractContentWalker implements IContentWalker {

    private IPropertySource source;
    private IContentWalker parent;

    public AbstractContentWalker(IContentWalker parent) {
        this.parent = parent;
    }
    
    protected IPropertySource createProperty() {
        return null;
    }
    
    public Object getParent() {
        return parent;
    }
    
    public Object[] getChildren() {
        return new Object[0];
    }
    
    public Object getAdapter(Class adapter) {
        if(adapter.equals(IPropertySource.class)) {
            if(source == null) {
                source = createProperty();
            }
            return source;
        }
        return null;
    }

    public boolean isDefaultExpandedState() {
        return false;
    }

}
