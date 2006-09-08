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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PluginPreferences extends EclipsePreferences {

	private static Set loadedNodes = new HashSet();

	private String pluginID;
	private String qualifier;
	private IEclipsePreferences loadLevel;
	private int segmentCount;

	public PluginPreferences() {
		super(null, null);
	}

	private PluginPreferences(IEclipsePreferences parent, String name) {
		super((EclipsePreferences) parent, name);
		String path = absolutePath();
		segmentCount = getSegmentCount(path);
		if (segmentCount < 2) {
			return;
		}
		pluginID = getSegment(path, 1);
		if (segmentCount > 2) {
			qualifier = getSegment(path, 2);
		}
	}

	protected EclipsePreferences internalCreate(EclipsePreferences nodeParent,
			String nodeName, Plugin context) {
		return new PluginPreferences(nodeParent, nodeName);
	}
	
	protected IPath getLocation() {
		if (pluginID == null || qualifier == null) {
			return null;
		}
		IPath path = ResourcesPlugin.getPlugin().getStateLocation();
		path = path.removeLastSegments(1).append(pluginID);
		return computeLocation(path, qualifier);
	}

	protected IEclipsePreferences getLoadLevel() {
		if (loadLevel == null) {
			if (pluginID == null || qualifier == null) {
				return null;
			}
			IEclipsePreferences node = this;
			for (int i = 3; i < segmentCount; i++) {
				node = (IEclipsePreferences) node.parent();
			}
			loadLevel = node;
		}
		return loadLevel;
	}

	protected void loaded() {
		loadedNodes.add(name());
	}

	protected boolean isAlreadyLoaded(IEclipsePreferences node) {
		return loadedNodes.contains(node.name());
	}

}
