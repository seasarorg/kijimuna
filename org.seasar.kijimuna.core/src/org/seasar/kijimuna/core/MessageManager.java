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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MessageManager {

	private Plugin plugin;
	private String resourceBaseName;
	private String pluginID;
	private ResourceBundle bundle;

	public MessageManager(Plugin plugin, String resourceBaseName) {
		this.plugin = plugin;
		this.resourceBaseName = resourceBaseName;
	}

	public void reportInfo(String message) {
		try {
			IStatus status = new Status(IStatus.INFO, pluginID, IStatus.OK, message, null);
			plugin.getLog().log(status);
		} catch (RuntimeException e1) {
		}
	}

	public void reportException(Exception e) {
		try {
			IStatus status;
			if (e instanceof CoreException) {
				status = ((CoreException) e).getStatus();
			} else {
				status = new Status(IStatus.ERROR, pluginID, IStatus.OK, e.getMessage(),
						e);
			}
			plugin.getLog().log(status);
		} catch (RuntimeException e1) {
		}
	}

	public String getResourceString(String key) {
		try {
			if (bundle == null) {
				getResourceBundle();
			}
			return bundle.getString(key);
		} catch (Exception e) {
			return "!" + key + "!";
		}
	}

	public String getResourceString(String key, Object[] args) {
		if (args == null) {
			args = new Object[0];
		}
		return MessageFormat.format(getResourceString(key), args);
	}

	public ResourceBundle getResourceBundle() {
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle(resourceBaseName, Locale.getDefault(),
						plugin.getClass().getClassLoader());
			}
			return bundle;
		} catch (Exception e) {
			KijimunaCore.reportException(e);
			return null;
		}
	}

}
