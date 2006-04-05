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
package org.seasar.kijimuna.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.ModelManager;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiInvokableDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ModelUtils implements ConstCore {

	public static String getConstructorDisplay(
			IRttiConstructorDesctiptor constructor, boolean fullDisplay) {
		IRtti rtti = constructor.getParent();
	    return getMethodDisplay(rtti,
	    		rtti.getQualifiedName(), constructor.getArgs(), fullDisplay);
	}
	
	public static String getMethodDisplay(
	        IRttiInvokableDesctiptor method, boolean fullDisplay) {
	    return getMethodDisplay(method.getParent(),
	            method.getMethodName(), method.getArgs(), fullDisplay);
	}
	
	public static String getMethodDisplay(
		        IRtti component, String methodName, IRtti[] args, boolean fullDisplay) {
		StringBuffer buffer = new StringBuffer();
		if(fullDisplay) {
		    buffer.append(component.getQualifiedName()).append("#");
		}
		buffer.append(methodName).append("(");
        for(int i = 0; i < args.length; i++) {
        	if(i != 0) {
        		buffer.append(", ");
        	}
        	if(args[i] != null) {
        	    if(fullDisplay) {
        	        buffer.append(args[i].getQualifiedName());
        	    } else {
        	        buffer.append(args[i].getShortName());
        	    }
        	} else {
				buffer.append("null");
        	}
        }
        buffer.append(")");
		return buffer.toString();
	}
	
	public static IRtti[] convertArray(Object[] objs) {
		IRtti[] rttiArgs = new IRtti[objs.length];
		for(int i = 0; i < objs.length; i++) {
			Object obj = objs[i];
			if(obj instanceof IRtti) {
				rttiArgs[i] = (IRtti)obj;
			} else if(objs[i] instanceof IAdaptable){
				rttiArgs[i] = (IRtti)((IAdaptable)objs[i]).getAdapter(IRtti.class);
			} else {
				rttiArgs[i] = null;
			}
		}
		return rttiArgs;
	}

	private static IComponentElement getParentComponent(IElement element) {
        while (element != null){
            if(element instanceof IComponentElement) {
    		    return (IComponentElement)element;
            }
            element = element.getParent();
        } 
	    return null;
	}
	
    public static IRtti getComponentRtti(IElement element) {
        IComponentElement component = getParentComponent(element);
        if(component != null) {
   		    return (IRtti)component.getAdapter(IRtti.class);
        } 
	    return null;
    }
    
    public static boolean doDesignTimeAutoBinding(IRtti rtti) {
    	String qname = rtti.getQualifiedName();
    	if(MODEL_INTERFACE_REQUEST.equals(qname) ||
	    	MODEL_INTERFACE_RESPONSE.equals(qname) ||
	    	MODEL_INTERFACE_SESSION.equals(qname)) {
    		return false; 
        }
        return true;
    }

	public static boolean hasPropertyElement(
	        IComponentElement component, String name) {
	    if(name != null) {
	        List list = component.getPropertyList();
	        for(Iterator it = list.iterator(); it.hasNext();) {
	            IPropertyElement element = (IPropertyElement)it.next();
	            if(name.equals(element.getPropertyName())) {
	                return true;
	            }
	        }
	    }
        return false;
	}
	
	public static String getInjectedElementName(IRtti component) {
	    if(component == null) {
	        return "null";
	    }
    	IDiconElement element = 
    		(IDiconElement)component.getAdapter(IComponentElement.class);
    	if(element == null) {
    		element = (IDiconElement)component.getAdapter(IContainerElement.class);
    	}
        if(element != null) {
            return element.getDisplayName() + 
            	"(@" + element.getContainerElement().getDisplayName() + ")";
        }
        return component.getQualifiedName();
	}
	
    public static List getParentContaienrs(IContainerElement container) {
        List list = new ArrayList();
        IProject project = (IProject)container.getAdapter(IProject.class);
        IStorage storage = (IStorage)container.getAdapter(IStorage.class);
        if((project != null) && (storage != null)) {
	        DiconNature nature = DiconNature.getInstance(project);
	        if(nature != null) {
	        	ModelManager model = nature.getModel();
	        	IStorage[] relateds = model.getRelatedFiles(storage, false);
	            for(int i = 0; i < relateds.length; i++) {
	                IContainerElement parent = model.getContainer(relateds[i], null);
	                list.add(parent);
	            }
	        }
        }
        return list;
    }

    public static IContainerElement getContainer(IProject project, IStorage storage) {
    	DiconNature nature = DiconNature.getInstance(project);
    	if(nature != null) {
    		return nature.getModel().getContainer(storage, null);
    	}
    	return null;
    }
    
    public static IRttiMethodDesctiptor[] getImplementMethods(
            IRtti rtti, IRtti implementing) {
        if((rtti != null) && (implementing != null)) {
	        Set set = new TreeSet();
	        IRttiMethodDesctiptor[] desc = implementing.getMethods(Pattern.compile(".*"));
	        for(int k = 0; k < desc.length; k++) {
	            IRttiMethodDesctiptor method = rtti.getMethod(
	                    desc[k].getMethodName(), desc[k].getArgs(), false);
	            set.add(method);
	        }
	        return (IRttiMethodDesctiptor[])set.toArray(
	                new IRttiMethodDesctiptor[set.size()]);
        }
        return new IRttiMethodDesctiptor[0];
    }

}