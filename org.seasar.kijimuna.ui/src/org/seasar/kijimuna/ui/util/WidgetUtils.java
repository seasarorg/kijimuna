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
package org.seasar.kijimuna.ui.util;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.preference.IPreferences;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class WidgetUtils implements ConstUI {

	public static ImageDescriptor getImageDescriptor(String name) {
	    ImageDescriptor descriptor;
        URL url = KijimunaUI.getEntry(PATH_IMAGES + name);
      	if(url != null) {
      	    descriptor = ImageDescriptor.createFromURL(url);
      	} else {
      	    descriptor = ImageDescriptor.getMissingImageDescriptor();
      	}
	    return descriptor;
	}
    
    public static void setDiconMarkerSettingCombo(
            IProject project, Combo combo, int category, boolean isDefault) {
        combo.setData(new Integer(category));
        if(combo.getItemCount() == 0) { 
	        combo.add("Error");
	        combo.add("Warning");
	        combo.add("Info");
	        combo.add("Ignore");
        }
        int severity = MarkerSetting.getDiconMarkerPreference(
        		project, category, isDefault);
        combo.select(severity);
    }
    
    public static void setDiconValidationSetting(
    		 IProject project, Button button, boolean isDefault) {
        boolean isValidation = MarkerSetting.getDiconValidationPreference(
        		project, isDefault);
        button.setSelection(isValidation);
    }

}
