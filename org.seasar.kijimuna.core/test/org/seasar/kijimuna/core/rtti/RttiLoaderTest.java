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
package org.seasar.kijimuna.core.rtti;

import java.util.TreeMap;

import org.eclipse.jdt.core.IJavaProject;
import org.seasar.kijimuna.core.test.TestProject;

import junit.framework.TestCase;


/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class RttiLoaderTest extends TestCase {
	
	public RttiLoaderTest(String arg) {
		super(arg);
	}

	
	private TestProject project;

	protected void setUp() throws Exception {
		project = new TestProject();
	}
	

	protected void tearDown() throws Exception {
		project.dispose();
	}

	public void testGetProject() {
		IJavaProject jp = project.getJavaProject();
		RttiLoader loader = new RttiLoader(jp.getElementName(), false);
		assertEquals(loader.getProject(), jp);
	}
	
	public void testIsAutoConvert() {
		IJavaProject jp = project.getJavaProject();
		RttiLoader loader = new RttiLoader(jp.getElementName(), false);
		assertFalse(loader.isAutoConvert());
		loader = new RttiLoader(jp.getElementName(), true);
		assertTrue(loader.isAutoConvert());
	}
	
	public void testLoadRtti1() throws Exception {
		IJavaProject jp = project.getJavaProject();
		RttiLoader loader = new RttiLoader(jp.getElementName(), false);
		IRtti rtti = loader.loadRtti(Integer.TYPE);
		assertEquals(rtti.getQualifiedName(), "int");
	}
	
	public void testLoadRtti2() throws Exception {
		IJavaProject jp = project.getJavaProject();
		RttiLoader loader = new RttiLoader(jp.getElementName(), false);
		IRtti rtti = loader.loadRtti("java.lang.Integer");
		assertEquals(rtti.getQualifiedName(), "java.lang.Integer");
	}

	public void testLoadRtti3() throws Exception {
		IJavaProject jp = project.getJavaProject();
		RttiLoader loader = new RttiLoader(jp.getElementName(), false);
		IRtti rtti = loader.loadRtti(TreeMap.class);
		IRtti[] interfaces = rtti.getInterfaces();
		assertEquals(interfaces.length,4);
	}

}
