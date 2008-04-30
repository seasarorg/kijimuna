package org.seasar.kijimuna.core.preference;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.test.TestProject;
import org.seasar.kijimuna.core.util.PreferencesUtil;

public class KijimunaPreferenceInitializerTest extends TestCase implements ConstCore {

	private TestProject testProject;

	protected void setUp() throws Exception {
		testProject = new TestProject();
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	public void testInitializeDefaultPreferences() {
		IPreferenceStore store = PreferencesUtil.getPreferenceStoreOfWorkspace();
		assertEquals(KijimunaCore.getVersion(), store.getString(PREFERENCES_KEY_VERSION));
		assertEquals(false, store.getBoolean(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_XML_ERROR));
		assertEquals(MARKER_SEVERITY_WARNING, store.getInt(MARKER_SEVERITY_XML_WARNING));
		assertEquals(true, store.getBoolean(MARKER_SEVERITY_ENABLE_DICON_VALIDATION));
		assertEquals(MARKER_SEVERITY_WARNING, store
				.getInt(MARKER_SEVERITY_NULL_INJECTION));
		assertEquals(MARKER_SEVERITY_IGNORE, store.getInt(MARKER_SEVERITY_AUTO_INJECTION));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_JAVA_FETAL));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_DICON_FETAL));
		assertEquals(MARKER_SEVERITY_WARNING, store.getInt(MARKER_SEVERITY_DICON_PROBLEM));

		assertEquals("128,0,0", store.getString(EDITOR_COLOR_COMMENT));
		assertEquals("128,128,128", store.getString(EDITOR_COLOR_XMLDECL));
		assertEquals("64,128,128", store.getString(EDITOR_COLOR_DOCDECL));
		assertEquals("0,0,128", store.getString(EDITOR_COLOR_TAG));
		assertEquals("0,128,0", store.getString(EDITOR_COLOR_ATTRIBUTE));
		assertEquals("0,0,0", store.getString(EDITOR_COLOR_DEFALUT));
	}

	public void testSetToDefalutAll() {
		IPreferenceStore store = PreferencesUtil.getPreferenceStoreOfWorkspace();
		store.setValue(PREFERENCES_KEY_VERSION, "0.0.1");
		store.setValue(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM, true);
		store.setValue(MARKER_SEVERITY_XML_ERROR, 5);
		store.setValue(store.getString(EDITOR_COLOR_COMMENT), "dummy");

		KijimunaPreferenceInitializer.setToDefalutAll(store);

		assertEquals(KijimunaCore.getVersion(), store.getString(PREFERENCES_KEY_VERSION));
		assertEquals(false, store.getBoolean(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_XML_ERROR));
		assertEquals(MARKER_SEVERITY_WARNING, store.getInt(MARKER_SEVERITY_XML_WARNING));
		assertEquals(true, store.getBoolean(MARKER_SEVERITY_ENABLE_DICON_VALIDATION));
		assertEquals(MARKER_SEVERITY_WARNING, store
				.getInt(MARKER_SEVERITY_NULL_INJECTION));
		assertEquals(MARKER_SEVERITY_IGNORE, store.getInt(MARKER_SEVERITY_AUTO_INJECTION));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_JAVA_FETAL));
		assertEquals(MARKER_SEVERITY_ERROR, store.getInt(MARKER_SEVERITY_DICON_FETAL));
		assertEquals(MARKER_SEVERITY_WARNING, store.getInt(MARKER_SEVERITY_DICON_PROBLEM));

		assertEquals("128,0,0", store.getString(EDITOR_COLOR_COMMENT));
		assertEquals("128,128,128", store.getString(EDITOR_COLOR_XMLDECL));
		assertEquals("64,128,128", store.getString(EDITOR_COLOR_DOCDECL));
		assertEquals("0,0,128", store.getString(EDITOR_COLOR_TAG));
		assertEquals("0,128,0", store.getString(EDITOR_COLOR_ATTRIBUTE));
		assertEquals("0,0,0", store.getString(EDITOR_COLOR_DEFALUT));
	}
}
