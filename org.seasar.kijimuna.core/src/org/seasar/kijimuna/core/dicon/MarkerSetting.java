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

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.preference.IPreferenceStore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.util.MarkerUtils;
import org.seasar.kijimuna.core.util.PreferencesUtil;

/**
 * @author kenmaz (http://d.hatena.ne.jp/kenmaz)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MarkerSetting implements ConstCore {
	
	public static void createDiconMarker(String id, IDiconElement element, String message) {
		IStorage storage = element.getStorage();
		if ((storage != null) && (storage instanceof IFile)) {
			IFile file = (IFile) storage;
			IPreferenceStore store = PreferencesUtil.getPreferenceStore(file.getProject());
			int markerSeverity = store.getInt(convertToMarkerSeverityKey(id));
			element.setMarkerServerity(markerSeverity);
			String msg = "[" + element.getElementName() + "] " + message;
			
			MarkerUtils.createMarker(ID_MARKER_DICONVALIDAION, id, 
					markerSeverity, file, element.getStartLine(), msg);
		}
	}

	public static void createDiconMarker(String id, IDiconElement element, Object[] info) {
		createDiconMarker(id, element, KijimunaCore.getResourceString(id, info));
	}

	public static void createDiconMarker(String id, IDiconElement element) {
		createDiconMarker(id, element, KijimunaCore.getResourceString(id));
	}

	public static void createProjectMarker(String id, IProject project, String message) {
		IPreferenceStore pref = PreferencesUtil.getPreferenceStore(project);
		int markerSeverity = pref.getInt(id);
		
		MarkerUtils.createMarker(ID_MARKER_DICONVALIDAION, id, markerSeverity, project, 0, "[project] " + message);
	}


	private static String convertToMarkerSeverityKey(String id){
		if(Arrays.asList(MARKER_SET_AUTO_INJECTION).contains(id)){
			return MARKER_SEVERITY_AUTO_INJECTION;
		}else if(Arrays.asList(MARKER_SET_DICON_FETAL).contains(id)){
			return MARKER_SEVERITY_DICON_FETAL;
		}else if(Arrays.asList(MARKER_SET_DICON_PROBLEM).contains(id)){
			return MARKER_SEVERITY_DICON_PROBLEM;
		}else if(Arrays.asList(MARKER_SET_JAVA_FETAL).contains(id)){
			return MARKER_SEVERITY_JAVA_FETAL;
		}else if(Arrays.asList(MARKER_SET_NULL_INJECTION).contains(id)){
			return MARKER_SEVERITY_NULL_INJECTION;
		}else if(Arrays.asList(MARKER_SET_XML_ERROR).contains(id)){
			return MARKER_SEVERITY_XML_ERROR;
		}else if(Arrays.asList(MARKER_SET_XML_WARNING).contains(id)){
			return MARKER_SEVERITY_XML_WARNING;
		}
		return "";//TODO:error log 
	}
}
