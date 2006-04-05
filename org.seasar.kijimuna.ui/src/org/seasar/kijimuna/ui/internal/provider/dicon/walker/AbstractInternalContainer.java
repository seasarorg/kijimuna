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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public abstract class AbstractInternalContainer extends AbstractContentWalker
		implements IInternalContainer, ConstUI {

    private IDiconElement element;
    
    public AbstractInternalContainer(IInternalContainer parent) {
        this(parent.getElement(), parent);
    }
    
    public AbstractInternalContainer(IDiconElement element, IContentWalker parent) {
        super(parent);
        this.element = element;
    }
    
    public IContainerElement getContainer() {
    	IProject project = (IProject)element.getAdapter(IProject.class);
    	IStorage storage = (IStorage)element.getAdapter(IStorage.class);
    	if((project != null) && (storage != null)) {
    		return ModelUtils.getContainer(project, storage);
    	} 
    	return null;
    }

    public IDiconElement getElement() {
        return element;
    }

    public int getMarkerSeverity() {
        return MARKER_SEVERITY_NONE;
    }
    
    public boolean isOGNL() {
        return false;
    }

    public Object getAdapter(Class adapter) {
        if(IDiconElement.class.equals(adapter)) {
            return element;
        }
        return super.getAdapter(adapter);
    }
}
