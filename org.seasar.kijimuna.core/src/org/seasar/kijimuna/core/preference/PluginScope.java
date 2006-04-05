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
package org.seasar.kijimuna.core.preference;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PluginScope implements IScopeContext {

	public static final String SCOPE = "plugin";
	private Plugin plugin;
    
	public PluginScope(Plugin plugin) {
	    super();
	    if(plugin == null) {
			throw new IllegalArgumentException();
	    }
	    this.plugin = plugin;
	}
	
    public IPath getLocation() {
    	IPath path = ResourcesPlugin.getPlugin().getStateLocation();
    	String pluginID = plugin.getBundle().getSymbolicName();
    	path = path.removeLastSegments(1).append(pluginID);
    	return path;
    }

    public String getName() {
        return SCOPE;
    }
    
	public IEclipsePreferences getNode(String qualifier) {
		if (qualifier == null) {
			throw new IllegalArgumentException();
		}
		if (plugin == null) {
			return null;
		}
		IEclipsePreferences root = Platform.getPreferencesService().getRootNode();
    	String pluginID = plugin.getBundle().getSymbolicName();
		return (IEclipsePreferences)root.node(SCOPE).node(pluginID).node(qualifier);
	}
    
}
