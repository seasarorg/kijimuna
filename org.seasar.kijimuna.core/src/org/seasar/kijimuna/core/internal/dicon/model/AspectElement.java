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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.info.IAspectInfo;
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.internal.dicon.info.Pointcut;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AspectElement extends ComponentHolderElement
		implements IAspectElement, ConstCore {
    
    private IPointcut[] pointcuts;
    
	public AspectElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_ASPECT);
	}
	
	public String getPointcut() {
		return getAttribute(DICON_ATTR_POINTCUT);
	}
	
	private IPointcut[] createPointcuts() {
	    if(pointcuts == null) {
	        String attribute = getPointcut();
	        if(StringUtils.existValue(attribute)) {
		        List list = new ArrayList();
		        String[] regexps = attribute.split(",");
		        for(int i = 0; i < regexps.length; i++) {
		            list.add(new Pointcut(this, regexps[i].trim()));
		        }
			    Collections.sort(list);
			    pointcuts = (IPointcut[])list.toArray(new IPointcut[list.size()]);
	        } else {
	            pointcuts = new IPointcut[] { new Pointcut(this, null) };
	        }
	    }
	    return pointcuts;
	}
	
	public Object getAdapter(Class adapter) {
	    if(IAspectInfo.class.equals(adapter)) {
	        return new IAspectInfo() {
	            public IPointcut[] getPointcuts() {
	                return createPointcuts();
	            }
	        };
	    }
	    return super.getAdapter(adapter);
	}
	
}
