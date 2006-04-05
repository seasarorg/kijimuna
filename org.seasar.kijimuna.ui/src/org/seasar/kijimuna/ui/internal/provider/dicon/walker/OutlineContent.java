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
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentRoot;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class OutlineContent implements IContentRoot { 

	private IProject project;
    private IStorage storage;
    
    public OutlineContent(IProject project, IStorage storage) {
        this.project = project;
    	this.storage = storage;
    }
    
    public Object[] getTopLevelItems() {
        IContainerElement container = ModelUtils.getContainer(project, storage); 
        if(container != null) {
            Object[] ret = new Object[2];
            ret[0] = new RelatedContainerRoot(container);
            ret[1] = new ContentItem(container, null, true);
            return ret;
        }
        return new Object[0];
    }
    
}
