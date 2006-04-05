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
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.ModelManager;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class IncludeElement extends DiconElement
		implements IIncludeElement, ConstCore {
	
	public IncludeElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_INCLUDE);
	}
	
	public String getPath() {
		return getAttribute(DICON_ATTR_PATH);
	}
	
	public IContainerElement getParentContainer() {
	    return (IContainerElement)getParent();
	}
	
	private IContainerElement getContainer(IStorage storage) {
		IProject project = (IProject)getAdapter(IProject.class);
        DiconNature nature = DiconNature.getInstance(project);
        if(nature != null) {
        	ModelManager model = nature.getModel();
            IContainerElement child = model.getContainer(storage, null);
            if(child != null) {
                model.addContainerAndRelatedFile(getParentContainer(),
                        (IStorage)child.getAdapter(IStorage.class));
            }
        	return child;
        }
        return null;
	}
	
	public IContainerElement getChildContainer() {
	    IProject proj = (IProject)getAdapter(IProject.class);
		if(proj != null) {
		    IStorage storage = ProjectUtils.findDiconStorageWithCash(proj, getPath());
		    if(storage != null) {
		        return getContainer(storage);
		    }
        }
	    return null;
	}
	
	public String getDisplayName() {
		return getPath();
	}

}
