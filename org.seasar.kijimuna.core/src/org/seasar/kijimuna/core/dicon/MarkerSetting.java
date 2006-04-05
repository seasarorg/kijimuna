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
package org.seasar.kijimuna.core.dicon;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.preference.IPreferences;
import org.seasar.kijimuna.core.util.MarkerUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MarkerSetting implements ConstCore {

	private static String convertKey(int category) {
		return MARKER_SEVERITY_ALL[category];
	}

	private static boolean hasSetting(String[] settings, String key) {
		for (int i = 0; i < settings.length; i++) {
			if (settings[i].equals(key)) {
				return true;
			}
		}
		return false;
	}

	private static int getCategoryID(String key) {
		if (hasSetting(MARKER_SET_XML_ERROR, key)) {
			return MARKER_CATEGORY_XML_ERROR;
		} else if (hasSetting(MARKER_SET_XML_WARNING, key)) {
			return MARKER_CATEGORY_XML_WARNING;
		} else if (hasSetting(MARKER_SET_NULL_INJECTION, key)) {
			return MARKER_CATEGORY_NULL_INJECTION;
		} else if (hasSetting(MARKER_SET_AUTO_INJECTION, key)) {
			return MARKER_CATEGORY_AUTO_INJECTION;
		} else if (hasSetting(MARKER_SET_JAVA_FETAL, key)) {
			return MARKER_CATEGORY_JAVA_FETAL;
		} else if (hasSetting(MARKER_SET_DICON_FETAL, key)) {
			return MARKER_CATEGORY_DICON_FETAL;
		} else if (hasSetting(MARKER_SET_DICON_PROBLEM, key)) {
			return MARKER_CATEGORY_DICON_PROBLEM;
		} else {
			return MARKER_CATEGORY_UNKNOWN;
		}
	}

	private static String getCategory(String key) {
		int category = getCategoryID(key);
		if (category == MARKER_CATEGORY_UNKNOWN) {
			return "";
		}
		return convertKey(category);
	}

	private static int getSeveritySetting(IProject project, String key) {
		int category = getCategoryID(key);
		if (category == MARKER_CATEGORY_UNKNOWN) {
			KijimunaCore.reportInfo("unknown marker id [" + key + "]");
			return MARKER_SEVERITY_IGNORE;
		}
		return getDiconMarkerPreference(project, category, false);
	}

	public static void setDiconMarkerPreference(IProject project, int category,
			int severity) {
		IPreferences pref;
		if (project == null) {
			pref = KijimunaCore.getPreferences();
		} else {
			DiconNature nature = DiconNature.getInstance(project);
			if (nature != null) {
				pref = nature.getPreferences();
			} else {
				return;
			}
		}
		String categoryKey = convertKey(category);
		pref.putInt(categoryKey, severity);
	}

	public static int getDiconMarkerPreference(IProject project, int category,
			boolean isDefault) {
		IPreferences pref;
		if (project == null) {
			pref = KijimunaCore.getPreferences();
		} else {
			DiconNature nature = DiconNature.getInstance(project);
			if (nature != null) {
				pref = nature.getPreferences();
			} else {
				pref = KijimunaCore.getPreferences();
			}
		}
		String categoryKey = convertKey(category);
		if (isDefault) {
			return pref.getDefaultInt(categoryKey);
		}
		return pref.getInt(categoryKey);
	}

	public static void setDiconValidationPreference(IProject project, boolean isValidation) {
		IPreferences pref;
		if (project == null) {
			pref = KijimunaCore.getPreferences();
		} else {
			DiconNature nature = DiconNature.getInstance(project);
			if (nature != null) {
				pref = nature.getPreferences();
			} else {
				return;
			}
		}
		pref.putBoolean(MARKER_SEVERITY_NOT_VALIDATION, !isValidation);
	}

	public static boolean getDiconValidationPreference(IProject project, boolean isDefault) {
		IPreferences pref;
		if (project == null) {
			pref = KijimunaCore.getPreferences();
		} else {
			DiconNature nature = DiconNature.getInstance(project);
			if (nature != null) {
				pref = nature.getPreferences();
			} else {
				pref = KijimunaCore.getPreferences();
			}
		}
		boolean notValidation;
		if (isDefault) {
			notValidation = pref.getDefaultBoolean(MARKER_SEVERITY_NOT_VALIDATION);
		} else {
			notValidation = pref.getBoolean(MARKER_SEVERITY_NOT_VALIDATION);
		}
		return !notValidation;
	}

	public static boolean isDiconValidation(IProject project) {
		return getDiconValidationPreference(project, false);
	}

	public static void createDiconMarker(String id, IDiconElement element, String message) {
		IStorage storage = element.getStorage();
		if ((storage != null) && (storage instanceof IFile)) {
			IFile file = (IFile) storage;
			int markerSeverity = getSeveritySetting(file.getProject(), id);
			element.setMarkerServerity(markerSeverity);
			MarkerUtils.createMarker(ID_MARKER_DICONVALIDAION, getCategory(id),
					markerSeverity, file, element.getStartLine(), "["
							+ element.getElementName() + "] " + message);
		}
	}

	public static void createDiconMarker(String id, IDiconElement element, Object[] info) {
		createDiconMarker(id, element, KijimunaCore.getResourceString(id, info));
	}

	public static void createDiconMarker(String id, IDiconElement element) {
		createDiconMarker(id, element, KijimunaCore.getResourceString(id));
	}

	public static void createProjectMarker(String id, IProject project, String message) {
		MarkerUtils.createMarker(ID_MARKER_DICONVALIDAION, getCategory(id),
				getSeveritySetting(project, id), project, 0, "[project] " + message);
	}

}
