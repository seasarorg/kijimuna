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

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.preference.IPreferences;

public class PreferencesUtil {

	public static IPreferences getPreferencesExactly(IProject project) {
		IPreferences pref = getProjectPreferences(project);
		return pref != null ? pref : getWorkbenchPreferences();
	}
	
	public static IPreferences getPreferences(IProject project) {
		return project != null ? getProjectPreferences(project) :
			getWorkbenchPreferences();
	}
	
	public static IPreferences getProjectPreferences(IProject project) {
		if (project == null) {
			return null;
		}
		DiconNature nature = DiconNature.getInstance(project);
		return nature != null ? nature.getPreferences() : null;
	}
	
	public static IPreferences getWorkbenchPreferences() {
		return KijimunaCore.getPreferences();
	}

}
