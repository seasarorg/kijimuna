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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class FileUtilsTest extends TestCase implements ConstCore {

	private TestProject testProject;
	private IPackageFragment pack;

	public FileUtilsTest(String arg) {
		super(arg);
	}

	protected void setUp() throws Exception {
		testProject = new TestProject();
		pack = testProject.createPackage("test");
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	private String contents =
		"<?xml=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
		"<conponents>\r\n" +
		"	<component class=\"java.util.ArrayList\"/>\r\n" +
		"</components>";

	public void testCreateFile() throws Exception {
		InputStream stream = new ByteArrayInputStream(contents.getBytes());
		FileUtils.createFile(pack, "test.dicon", stream);
		IPath path = pack.getPath().append("test.dicon");
		IFile file = testProject.getProject().getFile(path);

		// TODO: not implement
		// assertTrue(file.exists());
	}

}
