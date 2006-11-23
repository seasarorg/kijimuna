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
package org.seasar.kijimuna.core.dicon;

import junit.framework.TestCase;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class DiconNatureTest extends TestCase implements ConstCore {

	private TestProject testProject;

	public DiconNatureTest(String arg) {
		super(arg);
	}

	protected void setUp() throws Exception {
		testProject = new TestProject();
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	private int countBuilder(ICommand[] builders) {
		int num = 0;
		for (int i = 0; i < builders.length; i++) {
			String name = builders[i].getBuilderName();
			if (name.equals(ID_PROCESSOR_DICON_BUILDER)) {
				num++;
			}
		}
		return num;
	}

	public void testConfigureDeconfigure() throws Exception {
		IProject project = testProject.getProject();
		DiconNature nature = new DiconNature();
		nature.setProject(project);
		nature.configure();
		ICommand[] builders = project.getDescription().getBuildSpec();
		assertEquals(countBuilder(builders), 1);
		nature.deconfigure();
		builders = project.getDescription().getBuildSpec();
		assertEquals(countBuilder(builders), 0);
	}

}
