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
package org.seasar.kijimuna.core.annotation;

import org.eclipse.jdt.core.IPackageFragment;

import org.seasar.kijimuna.core.internal.dicon.info.AnnotationSupportRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.test.TestProject;

import junit.framework.TestCase;

public class ConstantBindingAnnotationTest extends TestCase {

	private static final String testType =
		"public class TestType {" +
		"  public static final String hoge_BINDING = \"bindingType=may\";" +
		"  public static final String fuga_BINDING = \"propName\";" +
		"  public static final String foo_BINDING = \"bindingType=aaa\";" +
		"  public static final String bar_BINDING = null;" +
		"  public String getHoge() {}" +
		"  public String getFuga() {}" +
		"  public String getFoo() {}" +
		"  public String getBar() {}" +
		"}";
	
	private TestProject project;
	private IRtti rtti;
	
	protected void setUp() throws Exception {
		project = new TestProject();
		IPackageFragment pack = project.createPackage("test");
		project.createType(pack, "TestType.java", testType);
		RttiLoader loader = new RttiLoader(project.getJavaProject()
				.getElementName(), false);
		rtti = new AnnotationSupportRtti(loader.loadRtti("test.TestType"));
	}
	
	protected void tearDown() throws Exception {
		project.dispose();
	}
	
	public void testCorrectBindingType() {
		IRttiPropertyDescriptor propDesc = rtti.getProperty("hoge");
		IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
				IBindingAnnotation.class);
		
		assertNull(ba.getPropertyName());
		assertEquals(IBindingAnnotation.BINDING_TYPE_MAY, ba.getBindingType());
		assertEquals("may", ba.getIntactBindingType());
	}
	
	public void testPropertyName() {
		IRttiPropertyDescriptor propDesc = rtti.getProperty("fuga");
		IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
				IBindingAnnotation.class);
		
		assertEquals("propName", ba.getPropertyName());
		assertEquals(IBindingAnnotation.BINDING_TYPE_MUST, ba.getBindingType());
		assertNull(ba.getIntactBindingType());
	}
	
	public void testIncorrectBindingType() {
		IRttiPropertyDescriptor propDesc = rtti.getProperty("foo");
		IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
				IBindingAnnotation.class);
		
		assertNull(ba.getPropertyName());
		assertEquals(IBindingAnnotation.BINDING_TYPE_UNKNOWN, ba.getBindingType());
		assertEquals("aaa", ba.getIntactBindingType());
	}
	
	public void testNoExisting() {
		IRttiPropertyDescriptor propDesc = rtti.getProperty("bar");
		IBindingAnnotation ba = (IBindingAnnotation) propDesc.getAdapter(
				IBindingAnnotation.class);
		
		assertNull(ba);
	}

}
