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
package org.seasar.kijimuna.core.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.preference.PluginScope;

import junit.framework.TestCase;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PluginPreferencesTest extends TestCase {

	public void testLoad() throws Exception {
		KijimunaCore kijimuna = KijimunaCore.getInstance();
	    IPath path = kijimuna.getStateLocation().append(
	    		".settings").append("testproject1.prefs");
	    File file = path.toFile();
	    file.getParentFile().mkdirs();
	    file.createNewFile();
	    OutputStreamWriter writer = 
	        new OutputStreamWriter(new FileOutputStream(file));
	    writer.write("#" + new Date() + "\r\n"); 
	    writer.write("eclipse.preferences.version=1\r\n");
	    writer.write("key1=value1\r\n");
	    writer.flush();
	    IScopeContext scope = new PluginScope(kijimuna);
	    IEclipsePreferences pref = scope.getNode("testproject1");
	    assertEquals(pref.get("key1", "no val"), "value1");
	}
	
	public void testFlash() throws Exception {
		KijimunaCore kijimuna = KijimunaCore.getInstance();
	    IScopeContext scope = new PluginScope(kijimuna);
	    IEclipsePreferences pref = scope.getNode("testproject2");
	    pref.put("key2", "value2");
	    pref.flush();
	    IPath path = kijimuna.getStateLocation().append(
	    	".settings").append("testproject2.prefs");
	    File file = path.toFile();
	    assertTrue(file.exists());
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	            new FileInputStream(file)));
	    String line1 = reader.readLine();
	    assertTrue(line1.startsWith("#"));
	    String line2 = reader.readLine();
	    assertTrue(line2.length() > 0);
	    String line3 = reader.readLine();
	    assertEquals(line3, "key2=value2");
	}
	
}
