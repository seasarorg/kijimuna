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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jdt.core.IPackageFragment;

import org.seasar.kijimuna.core.test.TestProject;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultRttiTest extends TestCase {

	public DefaultRttiTest(String arg) {
		super(arg);
	}

	private TestProject project;
	private RttiLoader loader;

	protected void setUp() throws Exception {
		project = new TestProject();
		IPackageFragment pack = project.createPackage("test");
		project.createType(pack,
				"IBase.java", "public interface IBase {" +
				"  public static final int BASE = 999;"+
				"}");
		project.createType(pack,
			"IPerson.java", "public interface IPerson extends IBase {" +
			"  public static final String CONST = \"static string\";"+
			"}");
		project.createType(pack, "AbstractPerson.java",
			"public abstract class AbstractPerson {"+
			"	public int integer;" +
			"	public int getInteger() {" +
			"		return integer;" +
			"	}" +
			"	public void setInteger(int integer) {" +
			"		this.integer = integer;" +
			"	}" +
			"	public Person createPerson(String name) {" +
			"		return new Person(name, -1);" +
			"	}" +
			"	public Person createPerson(String name, int age) {" +
			"		return new Person(name, age);" +
			"	}" +
			"}");
		project.createType(pack, "Person.java", 
			"public class Person extends AbstractPerson implements IPerson" +
			"	public Person parent;" +
			"	public java.util.List list;" +
			"	public static final char[ ] array = new char[0];" +
			"	public Person() {" +
			"	}" +
			"	public Person(String name, int age) {" +
			"	}" +
			"	public Person createPerson() {" +
			"		return new Person();" +
			"	}" +
			"	public Person createPerson(String name, int age) {" +
			"		return new Person(name, age);" +
			"	}" +
			"	public java.util.Map createMap() {" +
			"		return new java.util.HashMap();" +
			"	}" +
			"	public long createLong() {" +
			"		return 100L;" +
			"	}" +
			"	public Person getParent() {" +
			"		return parent;" +
			"	}" +
			"	public void setList(java.util.List list) {" +
			"		this.list = list;" +
			"	}" +
			"	public static void main(String[] args) {" +
			"		System.out.println(\"hello world\");" +
			"	}" +
			"}"
		);
		project.createType(pack, "DefaultPerson.java",
				"public class DefaultPerson extends Person {" + "}");
		project.createType(pack,
				"Sample.java", "public class Sample {" +
				"  public String str = \"string\";"+
				"}");
		loader = new RttiLoader(project.getJavaProject().getElementName(), false);
	}

	protected void tearDown() throws Exception {
		project.dispose();
	}

	public void testGetFile1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IStorage storage = (IStorage) person.getAdapter(IStorage.class);
		assertEquals(storage.getFullPath().toString(),
				"/TestProject/src/test/Person.java");
	}

	public void testGetFile2() throws Exception {
		IRtti string = loader.loadRtti("java.lang.String");
		IFile file = (IFile) string.getAdapter(IFile.class);
		assertNull(file);
	}

	public void testGetFile3() throws Exception {
		project.addJar("org.seasar.kijimuna.core", "/ognl-3.0.0-pre-2.jar");
		IRtti extensions = loader.loadRtti("org.ognl.el.Extensions");
		IFile file = (IFile) extensions.getAdapter(IFile.class);
		assertNull(file);
	}

	public void testGetShortName1() throws Exception {
		IRtti primitive = loader.loadRtti("boolean");
		assertEquals(primitive.getShortName(), "boolean");
	}

	public void testGetShortName2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		assertEquals(person.getShortName(), "Person");
	}

	public void testGetShortName3() throws Exception {
		IRtti array = loader.loadRtti("test.Person[]");
		assertEquals(array.getShortName(), "Person[]");
	}

	public void testIsInterface1() throws Exception {
		IRtti i_person = loader.loadRtti("test.IPerson");
		assertTrue(i_person.isInterface());
	}

	public void testIsInterface2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		assertFalse(person.isInterface());
	}

	public void testGetSuperClass1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti clazz = person.getSuperClass();
		assertEquals(clazz.getQualifiedName(), "test.AbstractPerson");
	}

	public void testGetSuperClass2() throws Exception {
		IRtti a_person = loader.loadRtti("test.AbstractPerson");
		IRtti clazz = a_person.getSuperClass();
		assertEquals(clazz.getQualifiedName(), "java.lang.Object");
	}

	public void testGetSuperClass3() throws Exception {
		IRtti i_person = loader.loadRtti("test.IPerson");
		IRtti clazz = i_person.getSuperClass();
		assertNull(clazz);
	}

	public void testGetSuperInterfaces1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti i_person = loader.loadRtti("test.IPerson");
		IRtti[] interfaces = person.getInterfaces();
		assertEquals(2, interfaces.length);
		assertTrue(i_person.equals(interfaces[1]));
	}

	public void testGetSuperInterfaces2() throws Exception {
		IRtti a_person = loader.loadRtti("test.AbstractPerson");
		IRtti[] interfaces = a_person.getInterfaces();
		assertEquals(interfaces.length, 0);
	}

	public void testIsAssignableFrom1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		assertTrue(person.isAssignableFrom(person));
	}

	public void testIsAssignableFrom2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti a_person = loader.loadRtti("test.AbstractPerson");
		assertTrue(a_person.isAssignableFrom(person));
	}

	public void testIsAssignableFrom3() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti i_person = loader.loadRtti("test.IPerson");
		assertTrue(i_person.isAssignableFrom(person));
	}

	public void testIsAssignableFrom4() throws Exception {
		IRtti i_person = loader.loadRtti("test.IPerson");
		IRtti person = loader.loadRtti("test.Person");
		assertFalse(person.isAssignableFrom(i_person));
	}

	public void testIsAssignableFrom5() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti a_person = loader.loadRtti("test.AbstractPerson");
		assertFalse(person.isAssignableFrom(a_person));
	}

	public void testIsAssignableFrom6() throws Exception {
		IRtti longVal = loader.loadRtti("long");
		IRtti intVal = loader.loadRtti("int");
		assertTrue(longVal.isAssignableFrom(intVal));
		assertFalse(intVal.isAssignableFrom(longVal));
	}

	public void testIsAssignableFrom7() throws Exception {
		IRtti obj = loader.loadRtti("java.lang.Object");
		IRtti array = loader.loadRtti("int[]");
		assertTrue(obj.isAssignableFrom(array));
		assertFalse(array.isAssignableFrom(obj));
	}

	public void testIsAssignableFrom8() throws Exception {
		IRtti obj = loader.loadRtti("java.lang.Object");
		IRtti interf = loader.loadRtti("test.IPerson");
		assertTrue(obj.isAssignableFrom(interf));
		assertFalse(interf.isAssignableFrom(obj));
	}

	public void testGetField1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("parent", false);
		assertEquals(member.getType().getQualifiedName(), "test.Person");
	}

	public void testGetField2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("list", false);
		assertEquals(member.getType().getQualifiedName(), "java.util.List");
	}

	public void testGetField3() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("integer", false);
		assertEquals(member.getType().getQualifiedName(), "int");
	}

	public void testGetField4() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("array", false);
		assertEquals(member.getType().getQualifiedName(), "char[]");
	}

	public void testGetField5() throws Exception {
		IRtti personArray = loader.loadRtti("test.Person[]");
		IRttiFieldDescriptor member = personArray.getField("length", false);
		assertEquals(member.getType().getQualifiedName(), "int");
	}

	public void testGetField6() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("CONST", true);
		assertEquals(member.getType().getQualifiedName(), "java.lang.String");
	}

	public void testGetField7() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiFieldDescriptor member = person.getField("BASE", true);
		assertEquals(member.getType().getQualifiedName(), "int");
	}

	public void testGetProperties() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiPropertyDescriptor[] props = person.getProperties(Pattern.compile(".*"));
		assertEquals(props.length, 4);
		Set set = new HashSet();
		for (int i = 0; i < props.length; i++) {
			set.add(props[i].getName());
		}
		assertTrue(set.contains("parent"));
		assertTrue(set.contains("list"));
		assertTrue(set.contains("integer"));
		assertTrue(set.contains("class"));
	}

	public void testGetProperty1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti type = loader.loadRtti("java.util.List");
		IRttiPropertyDescriptor prop = person.getProperty("list");
		assertNotNull(prop);
		assertEquals(prop.getType(), type);
		assertTrue(prop.isReadable());
		assertTrue(prop.isWritable());
	}

	public void testGetProperty2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti type = loader.loadRtti("test.Person");
		IRttiPropertyDescriptor prop = person.getProperty("parent");
		assertNotNull(prop);
		assertEquals(prop.getType(), type);
		assertTrue(prop.isReadable());
		assertTrue(prop.isWritable());
	}

	public void testGetProperty3() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti type = loader.loadRtti("int");
		IRttiPropertyDescriptor prop = person.getProperty("integer");
		assertNotNull(prop);
		assertEquals(prop.getType(), type);
		assertTrue(prop.isWritable());
		assertTrue(prop.isReadable());
	}
	
	public void testGetProperty4() throws Exception {
		IRtti person = loader.loadRtti("test.Sample");
		IRtti type = loader.loadRtti("java.lang.String");
		IRttiPropertyDescriptor prop = person.getProperty("str");
		assertNotNull(prop);
		assertEquals(prop.getType(), type);
		assertTrue(prop.isWritable());
		assertTrue(prop.isReadable());
	}

	public void testHasConstructor1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti[] args = new IRtti[0];
		assertNotNull(person.getConstructor(args));
	}

	public void testHasConstructor2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti[] args = new IRtti[] {
				loader.loadRtti("java.lang.String"), loader.loadRtti("int")
		};
		assertNotNull(person.getConstructor(args));
	}

	public void testHasConstructor3() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti[] args = new IRtti[] {
			loader.loadRtti("java.lang.String"),
		};
		assertNull(person.getConstructor(args));
	}

	public void testHasConstructor4() throws Exception {
		IRtti defaultPerson = loader.loadRtti("test.DefaultPerson");
		assertNotNull(defaultPerson.getConstructor(null));
	}

	public void testGetMethods() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiMethodDesctiptor[] methods = person.getMethods(Pattern
				.compile("createPerson"));
		assertEquals(methods.length, 3);
		assertEquals(methods[0].getMethodName(), "createPerson");
		assertEquals(methods[0].getReturnType().getQualifiedName(), "test.Person");
	}

	public void testGetMethod1() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiMethodDesctiptor ret1 = person.getMethod("createMap", new IRtti[0], false);
		assertEquals(ret1.getReturnType().getQualifiedName(), "java.util.Map");
	}

	public void testGetMethod2() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiMethodDesctiptor ret2 = person.getMethod("createLong", new IRtti[0], false);
		assertEquals(ret2.getReturnType().getQualifiedName(), "long");
	}

	public void testGetMethod3() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiMethodDesctiptor ret3 = person
				.getMethod("createPerson", new IRtti[0], false);
		assertEquals(ret3.getReturnType().getQualifiedName(), "test.Person");
	}

	public void testGetMethod4() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti[] args = new IRtti[] {
			loader.loadRtti("java.lang.String[]")
		};
		IRttiMethodDesctiptor ret4 = person.getMethod("main", args, false);
		assertEquals(ret4.getReturnType().getQualifiedName(), "void");
	}

	public void testGetMethod5() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRttiMethodDesctiptor ret5 = person.getMethod("createPerson", new IRtti[0], true);
		assertNull(ret5);
	}

	public void testGetMethod6() throws Exception {
		IRtti person = loader.loadRtti("test.Person");
		IRtti[] args = new IRtti[] {
			loader.loadRtti("java.lang.String[]")
		};
		IRttiMethodDesctiptor ret6 = person.getMethod("main", args, true);
		assertEquals(ret6.getReturnType().getQualifiedName(), "void");
	}

}
