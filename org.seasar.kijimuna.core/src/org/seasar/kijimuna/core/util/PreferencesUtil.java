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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.preference.KijimunaPreferenceInitializer;

/**
 * 設定ストアを取得するためのユーティリティクラスです
 * 
 * @author kenmaz (http://d.hatena.ne.jp/kenmaz)
 * 
 */
public class PreferencesUtil implements ConstCore {

	/**
	 * プロジェクト固有の設定ストアを取得します。<br>
	 * プロジェクト固有設定が行われていない場合はワークスペースの設定ストアを取得します。
	 */
	public static IPreferenceStore getPreferenceStore(IProject project) {
		IPreferenceStore store = getPreferenceStoreOfProject(project);
		boolean isPrjCustom = store.getBoolean(MARKER_SEVERITY_ENABLE_PROJECT_CUSTOM);
		if (!isPrjCustom) {
			return getPreferenceStoreOfWorkspace();
		} else {
			return store;
		}
	}

	/**
	 * プロジェクト固有の設定ストアを取得します。
	 */
	public static IPreferenceStore getPreferenceStoreOfProject(IProject project) {
		ProjectScope scope = new ProjectScope(project);
		String id = KijimunaCore.getInstance().getBundle().getSymbolicName();
		IPreferenceStore store = getStore(scope, id);
		return store;
	}

	/**
	 * ワークスペースの設定ストアを取得します。
	 */
	public static IPreferenceStore getPreferenceStoreOfWorkspace() {
		String id = KijimunaCore.getInstance().getBundle().getSymbolicName();
		return getStore(new InstanceScope(), id);
	}

	private static IPreferenceStore getStore(IScopeContext scope, String id) {
		ScopedPreferenceStore store = new ScopedPreferenceStore(scope, id);
		String version = store.getString(PREFERENCES_KEY_VERSION);

		// 以前のバージョンの設定がのこっている場合
		if (!version.equals(KijimunaCore.getVersion())) {
			// ストアをクリア
			KijimunaPreferenceInitializer.setToDefalutAll(store);
			// マーカーをクリア
			MarkerUtils.removeAllMarker(ConstCore.ID_MARKER);
			// modelデータを削除
			IPath stateLoc = KijimunaCore.getInstance().getStateLocation(); // TODO:what?
			FileUtils.deleteAllFiles(stateLoc);
		}
		return store;
	}
}
