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
package org.seasar.kijimuna.core.rtti.ognl;

import junit.framework.TestCase;

import org.ognl.el.ExecutionEnvironment;
import org.ognl.el.extensions.DefaultExecutionEnvironment;

import org.eclipse.jdt.core.IPackageFragment;

import org.seasar.kijimuna.core.internal.rtti.ognl.OgnlExtensions;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class OgnlExtensionsTest extends TestCase {

	public OgnlExtensionsTest(String arg) {
		super(arg);
	}

	private TestProject project;
	private RttiLoader loader;
	private IRtti person;
	private OgnlExtensions ext;
	private ExecutionEnvironment env;

	protected void setUp() throws Exception {
		project = new TestProject();
		IPackageFragment pack = project.createPackage("test");
		project.createType(pack, "Person.java", 
				"import java.util.List;" +
				"public class Person {" +
				"	public List list;" +
				"	public static String STATIC = \"satic value\";" +
				"	public Person getProperty() {" +
				"		return null;" +
				"	}" +
				"	public List listFamily() {" +
				"		return null;" +
				"	}" +
				"	public Person[] findFamily(String name, int age) {" +
				"		return new Person[0];" +
				"	}" +
				"	public static Person newInstance() {" +
				"		return new Person();" +
				"	}" +
				"}"
			);
		loader = new RttiLoader(project.getJavaProject().getElementName(), true);
		person = loader.loadRtti("test.Person");
		ext = new OgnlExtensions(loader);
		env = new DefaultExecutionEnvironment();
	}

	protected void tearDown() throws Exception {
		project.dispose();
	}

	public void testCallArrayConstructor() throws Exception {
		IRtti rtti = (IRtti) ext.callArrayConstructor(env, "test.Person", null);
		assertEquals(rtti.getQualifiedName(), "test.Person[]");
	}

	public void testCallConstructor() throws Exception {
		IRtti rtti = (IRtti) ext.callConstructor(env, "test.Person", null);
		assertEquals(rtti.getQualifiedName(), "test.Person");
	}

	public void testCallMethod1() throws Exception {
		IRtti rtti = (IRtti) ext.callMethod(env, person, "listFamily", null);
		assertEquals(rtti.getQualifiedName(), "java.util.List");
	}

	public void testCallMethod2() throws Exception {
		IRtti rtti = (IRtti) ext.callMethod(env, person, "newInstance", null);
		assertEquals(rtti.getQualifiedName(), "test.Person");
	}

	public void testCallMethod4() throws Exception {
		IRtti arg[] = new IRtti[] {
				loader.loadRtti("java.lang.String"), loader.loadRtti("int")
		};
		IRtti rtti = (IRtti) ext.callMethod(env, person, "findFamily", arg);
		assertEquals(rtti.getQualifiedName(), "test.Person[]");
	}

	public void testCallMethod5() throws Exception {
		IRtti rtti = (IRtti) ext.callMethod(env, person, "hashCode", null);
		assertEquals(rtti.getQualifiedName(), "int");
	}

	public void testCallStaticMethod() throws Exception {
		IRtti rtti = (IRtti) ext
				.callStaticMethod(env, "test.Person", "newInstance", null);
		assertEquals(rtti.getQualifiedName(), "test.Person");
	}

	public void testGetPropertyValue1() throws Exception {
		IRtti rtti = (IRtti) ext.getPropertyValue(env, person, "property");
		assertEquals(rtti.getQualifiedName(), "test.Person");
	}

	public void testGetPropertyValue2() throws Exception {
		IRtti rtti = (IRtti) ext.getPropertyValue(env, person, "list");
		assertEquals(rtti.getQualifiedName(), "java.util.List");
	}

	public void testGetStaticFieldValue1() throws Exception {
		IRtti rtti = (IRtti) ext.getStaticFieldValue(env, "test.Person", "STATIC");
		assertEquals(rtti.getQualifiedName(), "java.lang.String");
	}

	public void testGetStaticFieldValue2() throws Exception {
		IRtti rtti = (IRtti) ext.getStaticFieldValue(env, "test.Person", "class");
		assertEquals(rtti.getQualifiedName(), "java.lang.Class");
	}

	public void testGetStaticFieldValue3() throws Exception {
		IRtti rtti = (IRtti) ext.getStaticFieldValue(env, "java.lang.Boolean", "TRUE");
		assertEquals(rtti.getQualifiedName(), "java.lang.Boolean");
	}

}
