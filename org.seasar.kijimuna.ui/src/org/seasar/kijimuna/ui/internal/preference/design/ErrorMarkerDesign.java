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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.seasar.kijimuna.core.ConstCore;

/**
 * plugin/projectの設定ページ->エラーマーカー設定のUI
 */
public class ErrorMarkerDesign extends Composite implements ConstCore {

	private Button diconValidationCheck;
	private List xmlValidationLabels = new ArrayList();
	private List xmlValidationCombos = new ArrayList();
	private List diconValidationLabels = new ArrayList();
	private List diconValidationCombos = new ArrayList();

	private IPreferenceStore store;

	public ErrorMarkerDesign(Composite parent, int style, IPreferenceStore store) {
		super(parent, style);
		this.store = store;
		buildDesign();
	}

	private void buildDesign() {
		setFont(getParent().getFont());
		setLayout(new GridLayout(2, false));

		// parser title
		createMarkerLabel("ErrorMarkerDesign.2");
		createLabelSpace();

		// xml error
		createMarkerLabel("ErrorMarkerDesign.3");
		createMarkerCombo(MARKER_SEVERITY_XML_ERROR);

		// xml warning
		createMarkerLabel("ErrorMarkerDesign.4");
		createMarkerCombo(MARKER_SEVERITY_XML_WARNING);

		// dummy line
		createLabelSpace();
		createLabelSpace();

		// validation check
		diconValidationCheck = createCheck("ErrorMarkerDesign.11");
		diconValidationCheck.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent evt) {
				handleDiconValidationCheck();
			}
		});
		createLabelSpace();

		// validation title
		createValidationLabel("ErrorMarkerDesign.10");
		createLabelSpace();

		// null injection
		createValidationLabel("ErrorMarkerDesign.5");
		createValidationCombo(MARKER_SEVERITY_NULL_INJECTION);

		// auto injection
		createValidationLabel("ErrorMarkerDesign.6");
		createValidationCombo(MARKER_SEVERITY_AUTO_INJECTION);

		// java fetal
		createValidationLabel("ErrorMarkerDesign.7");
		createValidationCombo(MARKER_SEVERITY_JAVA_FETAL);

		// dicon fetail
		createValidationLabel("ErrorMarkerDesign.8");
		createValidationCombo(MARKER_SEVERITY_DICON_FETAL);

		// problem dicon
		createValidationLabel("ErrorMarkerDesign.9");
		createValidationCombo(MARKER_SEVERITY_DICON_PROBLEM);

		// dicon checkbox 初期化
		boolean diconCheck = store.getBoolean(MARKER_SEVERITY_ENABLE_DICON_VALIDATION);
		diconValidationCheck.setSelection(diconCheck);
		handleDiconValidationCheck();
	}

	private void handleDiconValidationCheck() {
		boolean sel = diconValidationCheck.getSelection();
		setControlsEnable(diconValidationLabels, sel);
		setControlsEnable(diconValidationCombos, sel);
	}

	private List getAllCombos() {
		List list = new ArrayList(xmlValidationCombos);
		list.addAll(diconValidationCombos);
		return list;
	}

	private Button createCheck(String labelKey) {
		Button check = new Button(this, SWT.CHECK | SWT.LEFT);
		check.setText(Messages.getString(labelKey));
		check.setFont(getParent().getFont());
		return check;
	}

	private Label createMarkerLabel(String labelKey) {
		return createLabel(labelKey, xmlValidationLabels);
	}

	private Label createValidationLabel(String labelKey) {
		return createLabel(labelKey, diconValidationLabels);
	}

	private Label createLabel(String labelKey, List controls) {
		Label label = new Label(this, SWT.NULL);
		label.setText(Messages.getString(labelKey));
		label.setFont(getParent().getFont());
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		label.setLayoutData(data);
		controls.add(label);
		return label;
	}

	private Label createLabelSpace() {
		return new Label(this, SWT.NULL);
	}

	private Combo createMarkerCombo(String key) {
		return createCombo(key, xmlValidationCombos);
	}

	private Combo createValidationCombo(String key) {
		return createCombo(key, diconValidationCombos);
	}

	private Combo createCombo(String key, List combos) {
		Combo combo = new Combo(this, SWT.READ_ONLY);
		combo.add("Error");
		combo.add("Warning");
		combo.add("Info");
		combo.add("Ignore");
		combo.setData(key);

		int severity = store.getInt(key);
		combo.select(severity);

		combo.setFont(getParent().getFont());
		combos.add(combo);
		return combo;
	}

	private void setControlsEnable(List controls, boolean enabled) {
		for (Iterator iterator = controls.iterator(); iterator.hasNext();) {
			Control control = (Control) iterator.next();
			control.setEnabled(enabled);
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setControlsEnable(xmlValidationLabels, enabled);
		setControlsEnable(xmlValidationCombos, enabled);
		diconValidationCheck.setEnabled(enabled);
		setControlsEnable(diconValidationLabels, enabled);
		if (enabled) {
			setControlsEnable(diconValidationCombos, diconValidationCheck.getSelection());
		} else {
			setControlsEnable(diconValidationCombos, false);
		}
	}

	public void store() {
		store.setValue(MARKER_SEVERITY_ENABLE_DICON_VALIDATION, diconValidationCheck
				.getSelection());
		List allCombos = getAllCombos();
		for (Iterator iterator = allCombos.iterator(); iterator.hasNext();) {
			Combo combo = (Combo) iterator.next();
			String key = (String) combo.getData();
			int sel = combo.getSelectionIndex();
			store.setValue(key, sel);
		}
	}

	public void loadDefault() {
		boolean check = store.getDefaultBoolean(MARKER_SEVERITY_ENABLE_DICON_VALIDATION);
		this.diconValidationCheck.setSelection(check);
		handleDiconValidationCheck();

		List combos = getAllCombos();
		for (Iterator iterator = combos.iterator(); iterator.hasNext();) {
			Combo combo = (Combo) iterator.next();
			String key = (String) combo.getData();
			combo.select(store.getDefaultInt(key));
		}
	}
}
