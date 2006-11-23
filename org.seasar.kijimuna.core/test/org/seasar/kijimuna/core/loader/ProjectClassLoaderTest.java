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
package org.seasar.kijimuna.core.loader;

import java.net.URL;

import junit.framework.TestCase;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectClassLoaderTest extends TestCase implements ConstCore {

	private TestProject project;
	private ProjectClassLoader loader;

	public ProjectClassLoaderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		project = new TestProject();
		project.addJar(ID_PLUGIN_CORE, "/dtdparser-1.2.1.jar");
		loader = new ProjectClassLoader(project.getJavaProject());
	}

	protected void tearDown() throws Exception {
		project.dispose();
	}

	public void testGetURLs() {
		URL[] urls = loader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i].toString());
		}
	}

	public void testLoadClass() throws Exception {
		loader.loadClass("com.wutka.dtd.DTD");
		assertTrue(true);
	}

}
