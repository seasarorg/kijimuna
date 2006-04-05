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
package org.seasar.kijimuna.ui;

import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.seasar.kijimuna.core.MessageManager;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class KijimunaUI extends Plugin implements ConstUI {

	//	 static
	// -------------------------------------------------------------------

	private static KijimunaUI kijimunaUI;
	
	
	public static void reportInfo(String message) {
		kijimunaUI.messageManager.reportInfo(message);
	}
	
	public static void reportException(Exception e) {
		kijimunaUI.messageManager.reportException(e);
	}

	public static String getResourceString(String key) {
		return kijimunaUI.messageManager.getResourceString(key);
	}
	
	public static String getResourceString(String key, Object[] args) {
		return kijimunaUI.messageManager.getResourceString(key, args);
	}
	
	public static ResourceBundle getResourceBundle() {
	    return kijimunaUI.messageManager.getResourceBundle();
	}

	public static URL getEntry(String name) {
	    return kijimunaUI.getBundle().getEntry(name);
	}
	
	//	 instance
	// -------------------------------------------------------------------
	
	private MessageManager messageManager; 
	
	public KijimunaUI() {
		kijimunaUI = this;
		messageManager = new MessageManager(this, PATH_RESOURCES);
	}
	
    public void stop(BundleContext context) throws Exception {
        kijimunaUI = null;
        super.stop(context);
    }

}
