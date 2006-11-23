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
package org.seasar.kijimuna.ui.internal.preference.design;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.ui.util.WidgetUtils;

public class ErrorMarkerDesign extends Composite implements ConstCore,
		IStorableDesigin {

	private IProject project;
	private Button validationCheck;
	private List markerLabels = new ArrayList();
	private List markerCombos = new ArrayList();
	private List validationLabels = new ArrayList();
	private List validationCombos = new ArrayList();
	
	public ErrorMarkerDesign(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public ErrorMarkerDesign(Composite parent, int style, IProject project) {
		super(parent, style);
		setProject(project);
		buildDesign();
	}
	
	public void store() {
		MarkerSetting.setDiconValidationPreference(getProject(),
				validationCheck.getSelection());
		List combos = addList(markerCombos, validationCombos);
		for (int i = 0; i < combos.size(); i++) {
			Combo combo = (Combo) combos.get(i);
			MarkerSetting.setDiconMarkerPreference(getProject(),
					((Integer) combo.getData()).intValue(),
					combo.getSelectionIndex());
		}
	}
	
	public void loadDefault() {
		setValidationCheck(true);
		List combos = addList(markerCombos, validationCombos);
		for (int i = 0; i < combos.size(); i++) {
			setComboSelection((Combo) combos.get(i), true);
		}
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void buildDesign() {
		setFont(getParent().getFont());
		setLayout(new GridLayout(2, false));
		
		// parser title
		createMarkerLabel("ErrorMarkerDesign.2");
		createLabelSpace();
		
		// xml error
		createMarkerLabel("ErrorMarkerDesign.3");
		createMarkerCombo(MARKER_CATEGORY_XML_ERROR);
		
		// xml warning
		createMarkerLabel("ErrorMarkerDesign.4");
		createMarkerCombo(MARKER_CATEGORY_XML_WARNING);
		
		// dummy line
		createLabelSpace();
		createLabelSpace();
		
		// validation check
		validationCheck = createCheck("ErrorMarkerDesign.11");
		validationCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				validationCheckWidgetSelected(validationCheck.getSelection());
			}
		});
		createLabelSpace();
		
		// validation title
		createValidationLabel("ErrorMarkerDesign.10");
		createLabelSpace();
		
		// null injection
		createValidationLabel("ErrorMarkerDesign.5");
		createValidationCombo(MARKER_CATEGORY_NULL_INJECTION);
		
		// auto injection
		createValidationLabel("ErrorMarkerDesign.6");
		createValidationCombo(MARKER_CATEGORY_AUTO_INJECTION);
		
		// java fetal
		createValidationLabel("ErrorMarkerDesign.7");
		createValidationCombo(MARKER_CATEGORY_JAVA_FETAL);
		
		// dicon fetail
		createValidationLabel("ErrorMarkerDesign.8");
		createValidationCombo(MARKER_CATEGORY_DICON_FETAL);
		
		// problem dicon
		createValidationLabel("ErrorMarkerDesign.9");
		createValidationCombo(MARKER_CATEGORY_DICON_PROBLEM);
		
		setValidationCheck(false);
	}
	
	private IProject getProject() {
		return project;
	}
	
	private Button createCheck(String labelKey) {
		Button check = new Button(this, SWT.CHECK | SWT.LEFT);
		check.setText(Messages.getString(labelKey));
		check.setFont(getParent().getFont());
		return check;
	}
	
	private Label createMarkerLabel(String labelKey) {
		return createLabel(labelKey, markerLabels);
	}
	
	private Label createValidationLabel(String labelKey) {
		return createLabel(labelKey, validationLabels);
	}
	
	private Label createLabel(String labelKey, List controls) {
		Label label = new Label(this, SWT.NULL);
		label.setText(Messages.getString(labelKey));
		label.setFont(getParent().getFont());
		controls.add(label);
		return label;
	}
	
	private Label createLabelSpace() {
		return new Label(this, SWT.NULL);
	}
	
	private Combo createMarkerCombo(int category) {
		return createCombo(category, markerCombos);
	}
	
	private Combo createValidationCombo(int category) {
		return createCombo(category, validationCombos);
	}
	
	private Combo createCombo(int category, List combos) {
		Combo combo = new Combo(this, SWT.READ_ONLY);
		setComboSelection(combo, false, category);
		combo.setFont(getParent().getFont());
		combos.add(combo);
		return combo;
	}
	
	private void setComboSelection(Combo combo, boolean isDefault) {
		setComboSelection(combo, isDefault, ((Integer) combo.getData()).intValue());
	}
	
	private void setComboSelection(Combo combo, boolean isDefault, int selection) {
		WidgetUtils.setDiconMarkerSettingCombo(getProject(), combo, selection,
				isDefault);
	}
	
	private void setValidationCheck(boolean isDefault) {
		boolean isValidation = MarkerSetting.getDiconValidationPreference(
				getProject(), isDefault);
		validationCheck.setSelection(isValidation);
		validationCheckWidgetSelected(isValidation);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setControlsEnable(addList(markerCombos, markerLabels), enabled);
		validationCheck.setEnabled(enabled);
		validationCheckWidgetSelected(enabled && validationCheck.getSelection());
	}

	public void validationCheckWidgetSelected(boolean flg) {
		setControlsEnable(addList(validationCombos, validationLabels), flg);
	}
	
	private void setControlsEnable(List controls, boolean enabled) {
		for (int i = 0; i < controls.size(); i++) {
			((Control) controls.get(i)).setEnabled(enabled);
		}
	}
	
	private List addList(List list1, List list2) {
		List ret = new ArrayList(list1);
		ret.addAll(list2);
		return ret;
	}

}
