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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.MarkerUtils;
import org.seasar.kijimuna.core.test.TestProject;

import junit.framework.TestCase;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MarkerUtilsTest extends TestCase implements ConstCore {

	private TestProject testProject;
	private IResource file;
	
	public MarkerUtilsTest(String arg) {
		super(arg);
	}
	
	protected void setUp() throws Exception {
		testProject = new TestProject();
		IPackageFragment pack = testProject.createPackage("test");
		IType type = testProject.createType(pack,
				"Test.java", "public class Test {" +
				"  public int getInt() { return 999; }"+
				"}");
		file = type.getUnderlyingResource();
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}
 	
	public void testCreateErrorMarker() throws Exception {
		MarkerUtils.createMarker(ID_MARKER, MARKER_SEVERITY_DICON_FETAL,
				MARKER_SEVERITY_ERROR, file, 1, "test error");
		IMarker[] markers = MarkerUtils.findMarker(file, ID_MARKER);
		assertEquals(markers.length, 1);
		assertEquals(((Integer)markers[0].getAttribute(IMarker.SEVERITY)).intValue(), IMarker.SEVERITY_ERROR);
		assertEquals(markers[0].getAttribute(IMarker.MESSAGE), "test error");
		assertEquals(((Integer)markers[0].getAttribute(IMarker.LINE_NUMBER)).intValue(), 1);
	}
	
	public void testDeleteProblemMarker() throws Exception {
		MarkerUtils.createMarker(ID_MARKER, MARKER_SEVERITY_DICON_FETAL,
				MARKER_SEVERITY_ERROR, file, 1, "test error");
		IMarker[] markers = MarkerUtils.findMarker(file, ID_MARKER);
		assertEquals(markers.length, 1);
		MarkerUtils.deleteMarker(file, ID_MARKER);
		markers = MarkerUtils.findMarker(file, ID_MARKER);
		assertEquals(markers.length, 0);
	}
	
	public void testDumpMessageDescription() {
	    dumpMessageDescriptor("XML�p�[�T�[���ʒm����G���[", MARKER_SET_XML_ERROR);
	    dumpMessageDescriptor("XML�p�[�T�[���ʒm����x��", MARKER_SET_XML_WARNING);
	    dumpMessageDescriptor("�����C���W�F�N�V������null���ݒ肳���ꍇ", MARKER_SET_NULL_INJECTION);
	    dumpMessageDescriptor("�����C���W�F�N�V���������R���|�[�l���g��", MARKER_SET_AUTO_INJECTION);
	    dumpMessageDescriptor("Java�^�Ɋ֘A����v���I�Ȗ��", MARKER_SET_JAVA_FETAL);
	    dumpMessageDescriptor("XML�ݒ�ɂ�����v���I�Ȗ��", MARKER_SET_DICON_FETAL);
	    dumpMessageDescriptor("XML�ݒ�ɂ������r�I�y���Ȗ��", MARKER_SET_DICON_PROBLEM);
	}

	private void dumpMessageDescriptor(String category, String[] idArray) {
	    for(int i = 0; i < idArray.length; i++) {
	        System.out.print(idArray[i]);
	        System.out.print(",");
	        System.out.print(category);
	        System.out.print(",");
	        System.out.println(KijimunaCore.getResourceString(idArray[i]));
	    }
	}
}
