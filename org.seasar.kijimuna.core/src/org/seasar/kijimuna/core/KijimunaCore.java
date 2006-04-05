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
package org.seasar.kijimuna.core;

import java.net.URL;

import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.seasar.kijimuna.core.preference.IPreferences;
import org.seasar.kijimuna.core.project.ProjectRecorder;
import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.MarkerUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class KijimunaCore extends Plugin implements ConstCore {

	// static
	// -------------------------------------------------------------------

	private static KijimunaCore kijimuna;
	private static IPreferences preferences;

	public static KijimunaCore getInstance() {
		return kijimuna;
	}

	public static void reportInfo(String message) {
		kijimuna.messageManager.reportInfo(message);
	}

	public static void reportException(Exception e) {
		kijimuna.messageManager.reportException(e);
	}

	public static String getResourceString(String key) {
		return kijimuna.messageManager.getResourceString(key);
	}

	public static String getResourceString(String key, Object[] args) {
		return kijimuna.messageManager.getResourceString(key, args);
	}

	public static URL getEntry(String path) {
		return kijimuna.getBundle().getEntry(path);
	}

	public static ProjectRecorder getProjectRecorder() {
		return kijimuna.recorder;
	}

	public static String getVersion() {
		return kijimuna.getBundle().getHeaders().get("Bundle-Version").toString();
	}

	public static IPreferences getPreferences() {
		if (preferences == null) {
			preferences = new KijimunaPreferences();
			String version = preferences.get(PREFERENCES_KEY_VERSION);
			if (!version.equals(KijimunaCore.getVersion())) {
				preferences.clear();
				preferences.put(PREFERENCES_KEY_VERSION, KijimunaCore.getVersion());
				initPluginData();
			}
		}
		return preferences;
	}

	private static void initPluginData() {
		MarkerUtils.removeAllMarker(ID_MARKER);
		FileUtils.deleteAllFiles(kijimuna.getStateLocation());
	}

	// instance
	// -------------------------------------------------------------------

	private ProjectRecorder recorder;
	private MessageManager messageManager;

	public KijimunaCore() {
		super();
		kijimuna = this;
		messageManager = new MessageManager(this, PATH_RESOURCE);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferences();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		recorder = new ProjectRecorder(this);
		recorder.inithialize(null);
		workspace.addSaveParticipant(this, recorder);
	}

	public void stop(BundleContext context) throws Exception {
		IScopeContext scope = new InstanceScope();
		Preferences pref = scope.getNode(ID_PLUGIN_CORE);
		pref.flush();
		Preferences pluginPref = Platform.getPreferencesService().getRootNode().node(
				"plugin");
		pluginPref.flush();
		recorder = null;
		messageManager = null;
		kijimuna = null;
		super.stop(context);
	}

}
