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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

import org.seasar.kijimuna.core.project.ProjectRecorder;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class KijimunaCore extends Plugin implements IResourceChangeListener, ConstCore {

	// static
	// -------------------------------------------------------------------

	private static KijimunaCore kijimuna;

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

	// instance
	// -------------------------------------------------------------------

	private ProjectRecorder recorder;
	private MessageManager messageManager;

	public KijimunaCore() {
		super();
		kijimuna = this;
		messageManager = new MessageManager(this, PATH_RESOURCE);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_DELETE);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		recorder = new ProjectRecorder(this);
		recorder.inithialize(null);
		workspace.addSaveParticipant(this, recorder);
	}

	public void stop(BundleContext context) throws Exception {
		Preferences pluginPref = Platform.getPreferencesService().getRootNode().node(
				"plugin");
		pluginPref.flush();
		recorder = null;
		messageManager = null;
		kijimuna = null;
		super.stop(context);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getResource() instanceof IProject) {
			ProjectUtils.clearDiconStorageWithCash((IProject) event.getResource());
		}
	}

}
