package org.seasar.kijimuna.core.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.PreferencesUtil;

/**
 * Kijimunaのデフォルト設定値を設定するクラス
 * @author kenmaz (http://d.hatena.ne.jp/kenmaz)
 */
public class KijimunaPreferenceInitializer extends AbstractPreferenceInitializer 
	implements ConstCore{

	/**
	 * 設定ストアの初期値を定義します。
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferencesUtil.getPreferenceStoreOfWorkspace();

		store.setDefault(PREFERENCES_KEY_VERSION, KijimunaCore.getVersion());
		store.setDefault(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM, false);
		store.setDefault(MARKER_SEVERITY_XML_ERROR, MARKER_SEVERITY_ERROR);
		store.setDefault(MARKER_SEVERITY_XML_WARNING, MARKER_SEVERITY_WARNING);
		store.setDefault(MARKER_SEVERITY_ENABLE_DICON_VALIDATION, true);
		store.setDefault(MARKER_SEVERITY_NULL_INJECTION, MARKER_SEVERITY_WARNING);
		store.setDefault(MARKER_SEVERITY_AUTO_INJECTION, MARKER_SEVERITY_IGNORE);
		store.setDefault(MARKER_SEVERITY_JAVA_FETAL, MARKER_SEVERITY_ERROR);
		store.setDefault(MARKER_SEVERITY_DICON_FETAL, MARKER_SEVERITY_ERROR);
		store.setDefault(MARKER_SEVERITY_DICON_PROBLEM, MARKER_SEVERITY_WARNING);
		store.setDefault(EDITOR_COLOR_COMMENT,"128,0,0");
		store.setDefault(EDITOR_COLOR_XMLDECL,"128,128,128");
		store.setDefault(EDITOR_COLOR_DOCDECL,"64,128,128");
		store.setDefault(EDITOR_COLOR_TAG,"0,0,128");
		store.setDefault(EDITOR_COLOR_ATTRIBUTE,"0,128,0");
		store.setDefault(EDITOR_COLOR_DEFALUT,"0,0,0");
	}
	
	/**
	 * 設定ストアを初期化します。
	 * @param store
	 */
	public static void setToDefalutAll(IPreferenceStore store){
		store.setToDefault(PREFERENCES_KEY_VERSION);
		store.setToDefault(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM);
		store.setToDefault(MARKER_SEVERITY_XML_ERROR);
		store.setToDefault(MARKER_SEVERITY_XML_WARNING);
		store.setToDefault(MARKER_SEVERITY_ENABLE_DICON_VALIDATION);
		store.setToDefault(MARKER_SEVERITY_NULL_INJECTION);
		store.setToDefault(MARKER_SEVERITY_AUTO_INJECTION);
		store.setToDefault(MARKER_SEVERITY_JAVA_FETAL);
		store.setToDefault(MARKER_SEVERITY_DICON_FETAL);
		store.setToDefault(MARKER_SEVERITY_DICON_PROBLEM);
		store.setToDefault(EDITOR_COLOR_COMMENT);
		store.setToDefault(EDITOR_COLOR_XMLDECL);
		store.setToDefault(EDITOR_COLOR_DOCDECL);
		store.setToDefault(EDITOR_COLOR_TAG);
		store.setToDefault(EDITOR_COLOR_ATTRIBUTE);
		store.setToDefault(EDITOR_COLOR_DEFALUT);		
	}
}
