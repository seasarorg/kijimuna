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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MarkerUtils implements ConstCore {
    
    private static int getSystemSeverity(int severity) {
        switch(severity) {
        	case MARKER_SEVERITY_ERROR:
        	    return IMarker.SEVERITY_ERROR;
        	case MARKER_SEVERITY_WARNING:
        	    return IMarker.SEVERITY_WARNING;
        	case MARKER_SEVERITY_INFO:
        	    return IMarker.SEVERITY_INFO;
        	default:
        	    return -1;
        }
    }
    
    public static void createMarker(String type, String category, int severity,
            IResource resource, int line, String message) {
        if(resource != null) {
	        if(		(severity == MARKER_SEVERITY_ERROR) ||
	                (severity == MARKER_SEVERITY_WARNING) ||
	                (severity == MARKER_SEVERITY_INFO)) {
		        try {
		    		IMarker marker = resource.createMarker(type);
		    		Map map = new HashMap(4);
		    		int systemSeverity = getSystemSeverity(severity);
		    		map.put(IMarker.SEVERITY, new Integer(systemSeverity));
		    		if (line < 1) {
		    		    line = 1;
		    		}
	    			map.put(IMarker.LINE_NUMBER, new Integer(line));
		    		map.put(MARKER_ATTR_CATEGORY, category);
		    		map.put(IMarker.MESSAGE, message);
		    		marker.setAttributes(map);
		    	} catch (CoreException e) {
		    		KijimunaCore.reportException(e);
		    	}
	    	}
        }
    }
    
	public static void deleteMarker(IResource resource, String type) {
        if(resource != null) {
	    	try {
	    		resource.deleteMarkers(
	    		        type, true, IResource.DEPTH_INFINITE);
	    	} catch (CoreException e) {
	    		KijimunaCore.reportException(e);
	    	}
        }
    }

	public static void removeAllMarker(String type) {
	    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		deleteMarker(root, type);
	}

    public static IMarker[] findMarker(IResource resource, String type) {
        if(resource != null) {
	        try {
	    		return resource.findMarkers(
	    		        type, true, IResource.DEPTH_INFINITE);
	    	} catch (CoreException e) {
	    		KijimunaCore.reportException(e);
	    	}
        }
		return new IMarker[0];
    }

}
