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

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

import junit.framework.TestCase;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectUtilsTest extends TestCase implements ConstCore {

	private TestProject testProject;
    
    public ProjectUtilsTest(String arg) {
		super(arg);
	}
	
	protected void setUp() throws Exception {
		testProject = new TestProject();

		// TODO: not implement
//		testProject.addJar(ID_PLUGIN_CORE, "test.jar");
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}
    
	public void testFindDiconStorage() throws Exception {
		// TODO: not implement
//	    IStorage storage = ProjectUtils.findDiconStorage(
//	            testProject.getProject(), "/org/seasar/kijimuna/test/injar.dicon");
//	    assertNotNull(storage);
//	    assertEquals(storage.getName(), "injar.dicon");
	}
	
}
