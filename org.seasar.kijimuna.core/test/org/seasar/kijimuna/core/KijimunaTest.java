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

import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.core.test.TestProject;


/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class KijimunaTest extends TestCase  implements ConstCore {

	private TestProject project;
	
	public KijimunaTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		project = new TestProject();
	}

	protected void tearDown() throws Exception {
		project.dispose();
	}
	
	public void testOperateDiconNature() throws Exception {
		IProject proj = project.getProject();
		ProjectUtils.addNature(proj, ID_NATURE_DICON);
		assertTrue(ProjectUtils.getNature(proj, ID_NATURE_DICON) != null);
		ProjectUtils.removeNature(proj, ID_NATURE_DICON);
		assertTrue(ProjectUtils.getNature(proj, ID_NATURE_DICON) == null);
	}
	
	public void testGetEntry() throws Exception {
		URL url = KijimunaCore.getEntry("/components.dtd");
		InputStream in = url.openStream();
		int i = in.read();
		assertEquals((char)i, '<');
	}
	
}
