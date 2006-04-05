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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.preference.design.ProjectPropertyGUI;
import org.seasar.kijimuna.ui.util.WidgetUtils;

import com.sun.corba.se.spi.ior.MakeImmutable;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectPreferencePage extends PropertyPage implements ConstCore {

	private ProjectPropertyGUI view;
	private Button natureCheck;
	private TabFolder tabFolder;
	private Combo xmlErrorCombo;
	private Combo xmlWarningCombo;
	private Button validationCheck;
	private Combo nullInjectionCombo;
	private Combo autoInjectionCombo;
	private Combo javaFetalCombo;
	private Combo diconFetalCombo;
	private Combo diconProblemCombo;

	private IProject getProject() {
		return (IProject)getElement();
	}

	protected Control createContents(Composite parent) {
		view = new ProjectPropertyGUI(parent, SWT.NULL);
		natureCheck = view.getNatureCheck();
		tabFolder = view.getTabFolder();
		xmlErrorCombo = view.getXmlErrorCombo();
		xmlWarningCombo = view.getXmlWarningCombo();
		validationCheck = view.getValidationCheck();
		nullInjectionCombo = view.getNullInjectionCombo();
		autoInjectionCombo = view.getAutoInjectionCombo();
		javaFetalCombo = view.getJavaFetalCombo();
		diconFetalCombo = view.getDiconFetalCombo();
		diconProblemCombo = view.getDiconProblemCombo();
		IProject project = getProject();
		try {
			boolean isNature = project.hasNature(ID_NATURE_DICON);
			natureCheck.setSelection(isNature);
			view.natureCheckWidgetSelected(isNature);
			tabFolder.setEnabled(isNature);
			settingCombos(false);
			boolean isVallidation = MarkerSetting.isDiconValidation(project);
			validationCheck.setSelection(isVallidation);
			view.validationCheckWidgetSelected(isNature && isVallidation);
		} catch(CoreException e) {
			KijimunaUI.reportException(e);
		}
		return view;
	}

	private void settingCombos(boolean isDefault) {
	    IProject project = getProject();
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, xmlErrorCombo, MARKER_CATEGORY_XML_ERROR, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, xmlWarningCombo, MARKER_CATEGORY_XML_WARNING, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, nullInjectionCombo, MARKER_CATEGORY_NULL_INJECTION, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, autoInjectionCombo, MARKER_CATEGORY_AUTO_INJECTION, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, javaFetalCombo, MARKER_CATEGORY_JAVA_FETAL, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, diconFetalCombo, MARKER_CATEGORY_DICON_FETAL, isDefault);
		WidgetUtils.setDiconMarkerSettingCombo(
		        project, diconProblemCombo, MARKER_CATEGORY_DICON_PROBLEM, isDefault);
	}
	
	public boolean performOk() {
		try {
			if(natureCheck.getSelection()) {
			    final IProject project = getProject();
				boolean alreadyHasNature = ProjectUtils.hasNature(
				        project, ID_NATURE_DICON); 
				if(!alreadyHasNature) {
				    ProjectUtils.addNature(project, ID_NATURE_DICON);
				}
				MarkerSetting.setDiconMarkerPreference(project, 
				        MARKER_CATEGORY_XML_ERROR, 
				        xmlErrorCombo.getSelectionIndex());
				MarkerSetting.setDiconMarkerPreference(project, 
				        MARKER_CATEGORY_XML_WARNING, 
				        xmlWarningCombo.getSelectionIndex());
				MarkerSetting.setDiconValidationPreference(project, validationCheck.getSelection());
				MarkerSetting.setDiconMarkerPreference(project, 
				        MARKER_CATEGORY_NULL_INJECTION,
				        nullInjectionCombo.getSelectionIndex());
				MarkerSetting.setDiconMarkerPreference(project,
				        MARKER_CATEGORY_AUTO_INJECTION,
				        autoInjectionCombo.getSelectionIndex());
				MarkerSetting.setDiconMarkerPreference(project,
				        MARKER_CATEGORY_JAVA_FETAL,
				        javaFetalCombo.getSelectionIndex());
				MarkerSetting.setDiconMarkerPreference(project,
				        MARKER_CATEGORY_DICON_FETAL, 
				        diconFetalCombo.getSelectionIndex());
				MarkerSetting.setDiconMarkerPreference(project,
				        MARKER_CATEGORY_DICON_PROBLEM, 
				        diconProblemCombo.getSelectionIndex());
				if(alreadyHasNature) {
					IWorkbenchWindow window = 
						PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					try {
						window.run(true, true, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
							    KijimunaCore.getProjectRecorder().cleanup(project, monitor);
							}
						});
					} catch (InvocationTargetException e) {
					} catch (InterruptedException e) {
					}
				}
			} else {
			    ProjectUtils.removeNature(getProject(), ID_NATURE_DICON);
			}
		} catch (CoreException e) {
			KijimunaUI.reportException(e);
			return false;
		}
		return true;
	}

    protected void performDefaults() {
		if(natureCheck.getSelection()) {
			settingCombos(true);
		    IProject project = getProject();
		    boolean defaultValidation = MarkerSetting.getDiconValidationPreference(project, true);
		    validationCheck.setSelection(defaultValidation);
			view.validationCheckWidgetSelected(defaultValidation);
		}
    }
}