package org.seasar.kijimuna.core.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.test.TestProject;

import junit.framework.TestCase;

public class PreferencesUtilTest extends TestCase {

	private TestProject testProject;
	private IProject project;

	protected void setUp() throws Exception {
		testProject = new TestProject();
		project = testProject.getProject();
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	public void testGetPreferenceStore_GetStoreOfProject() {
		IPreferenceStore storeOfProject = PreferencesUtil
				.getPreferenceStoreOfProject(project);
		storeOfProject.setValue(ConstCore.MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM, true);
		storeOfProject.setValue("test", "OK");
		IPreferenceStore store = PreferencesUtil.getPreferenceStore(project);
		assertEquals("OK", store.getString("test"));
	}

	public void testGetPreferenceStore_GetStoreOfWorkspace() {
		IPreferenceStore storeOfWorkspace = PreferencesUtil
				.getPreferenceStoreOfWorkspace();
		storeOfWorkspace.setValue("test", "OK");
		IPreferenceStore store = PreferencesUtil.getPreferenceStore(project);
		assertEquals("OK", store.getString("test"));
	}

}
