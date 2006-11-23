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
package org.seasar.kijimuna.core.search;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class JavaPackageSeacherTest extends TestCase implements ConstCore {

	private TestProject testProject;
	private IProject project;

	public JavaPackageSeacherTest(String arg) {
		super(arg);
	}

	protected void setUp() throws Exception {
		testProject = new TestProject();
		project = testProject.getProject();
		testProject.addJar(ID_PLUGIN_CORE, "/dtdparser-1.2.1.jar");
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	public void testSearchPackagesAndTypes1() throws Exception {
		JavaPackageSearcher searcher = new JavaPackageSearcher(project);
		List list = new ArrayList();
		IPackageRequestor requestor = searcher.createDefaultRequestor(list);
		searcher.searchPackagesAndTypes("com.wutka.d", requestor);
		assertEquals(list.size(), 1);
		IPackageFragment pack = (IPackageFragment) list.get(0);
		assertEquals(pack.getElementName(), "com.wutka.dtd");
	}

	public void testSearchPackagesAndTypes2() throws Exception {
		JavaPackageSearcher searcher = new JavaPackageSearcher(project);
		List list = new ArrayList();
		IPackageRequestor requestor = searcher.createDefaultRequestor(list);
		searcher.searchPackagesAndTypes("com.wutka.dtd.DTDEl", requestor);
		assertEquals(list.size(), 1);
		IType type = (IType) list.get(0);
		assertEquals(type.getFullyQualifiedName(), "com.wutka.dtd.DTDElement");
	}

}
