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
package org.seasar.kijimuna.ui.internal.preference;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferencePage;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.ui.internal.preference.design.ProjectPropertyGUI;
import org.seasar.kijimuna.ui.util.WidgetUtils;

import com.sun.corba.se.spi.ior.MakeImmutable;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PluginPreferencePage extends WorkbenchPreferencePage
		implements ConstCore {

	private ProjectPropertyGUI view;
	private Combo xmlErrorCombo;
	private Combo xmlWarningCombo;
	private Button validationCheck;
	private Combo nullInjectionCombo;
	private Combo autoInjectionCombo;
	private Combo javaFetalCombo;
	private Combo diconFetalCombo;
	private Combo diconProblemCombo;
	
	protected Control createContents(Composite parent) {
		view = new ProjectPropertyGUI(parent, SWT.NULL);
		Button natureCheck = view.getNatureCheck();
		natureCheck.setVisible(false);
		xmlErrorCombo = view.getXmlErrorCombo();
		xmlWarningCombo = view.getXmlWarningCombo();
		validationCheck = view.getValidationCheck();
		nullInjectionCombo = view.getNullInjectionCombo();
		autoInjectionCombo = view.getAutoInjectionCombo();
		javaFetalCombo = view.getJavaFetalCombo();
		diconFetalCombo = view.getDiconFetalCombo();
		diconProblemCombo = view.getDiconProblemCombo();
		
		settingCombos(false);
		boolean isValidation = MarkerSetting.getDiconValidationPreference(null, false);
		validationCheck.setSelection(isValidation);
		view.validationCheckWidgetSelected(isValidation);
		return view;
	}

	private void settingCombos(boolean isDefault) {
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, xmlErrorCombo, MARKER_CATEGORY_XML_ERROR, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, xmlWarningCombo, MARKER_CATEGORY_XML_WARNING, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, nullInjectionCombo, MARKER_CATEGORY_NULL_INJECTION, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, autoInjectionCombo, MARKER_CATEGORY_AUTO_INJECTION, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, javaFetalCombo, MARKER_CATEGORY_JAVA_FETAL, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, diconFetalCombo, MARKER_CATEGORY_DICON_FETAL, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        null, diconProblemCombo, MARKER_CATEGORY_DICON_PROBLEM, isDefault);
	}	

	public boolean performOk() {
		MarkerSetting.setDiconMarkerPreference(null, 
		        MARKER_CATEGORY_XML_ERROR, 
		        xmlErrorCombo.getSelectionIndex());
		MarkerSetting.setDiconMarkerPreference(null, 
		        MARKER_CATEGORY_XML_WARNING, 
		        xmlWarningCombo.getSelectionIndex());
		MarkerSetting.setDiconValidationPreference(null, validationCheck.getSelection());
		MarkerSetting.setDiconMarkerPreference(null, 
		        MARKER_CATEGORY_NULL_INJECTION,
		        nullInjectionCombo.getSelectionIndex());
		MarkerSetting.setDiconMarkerPreference(null,
		        MARKER_CATEGORY_AUTO_INJECTION,
		        autoInjectionCombo.getSelectionIndex());
		MarkerSetting.setDiconMarkerPreference(null,
		        MARKER_CATEGORY_JAVA_FETAL,
		        javaFetalCombo.getSelectionIndex());
		MarkerSetting.setDiconMarkerPreference(null,
		        MARKER_CATEGORY_DICON_FETAL, 
		        diconFetalCombo.getSelectionIndex());
		MarkerSetting.setDiconMarkerPreference(null,
		        MARKER_CATEGORY_DICON_PROBLEM, 
		        diconProblemCombo.getSelectionIndex());
		IWorkbenchWindow window = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			window.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
				    KijimunaCore.getProjectRecorder().cleanup(monitor);
				}
			});
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}
		return true;
	}

    protected void performDefaults() {
		settingCombos(true);
	    validationCheck.setSelection(true);
		view.validationCheckWidgetSelected(true);
    }	
}
