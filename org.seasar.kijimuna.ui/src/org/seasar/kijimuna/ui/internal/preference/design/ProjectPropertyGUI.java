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

/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
package org.seasar.kijimuna.ui.internal.preference.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ProjectPropertyGUI extends Composite {

	private Label parserMarkerLabel;
	private Label validationMarkerLabel;
	private Button natureCheck;
	private Button validationCheck;
	private TabFolder tabFolder;
	private TabItem markerTabItem;
	private Composite markerComposite;

	private Label xmlErrorLabel;
	private Label xmlWarningLabel;
	private Label nullInjectionLabel;
	private Label autoInjectionLabel;
	private Label javaFetalLabel;
	private Label diconFetalLabel;
	private Label problemDiconLabel;

	private Combo xmlErrorCombo;
	private Combo xmlWarningCombo;
	private Combo nullInjectionCombo;
	private Combo autoInjectionCombo;
	private Combo javaFetalCombo;
	private Combo diconFetalCombo;
	private Combo diconProblemCombo;

	public ProjectPropertyGUI(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	public void initGUI() {
		try {
			preInitGUI();
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			this.setLayout(layout);

			// nature check
			natureCheck = new Button(this, SWT.CHECK | SWT.LEFT);
			natureCheck.setText(Messages.getString("ProjectPropertyGUI.0")); //$NON-NLS-1$
			natureCheck.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent evt) {
					natureCheckWidgetSelected(natureCheck.getSelection());
					validationCheckWidgetSelected(natureCheck.getSelection()
							&& validationCheck.getSelection());
				}
			});
			GridData natureCheckData = new GridData();
			natureCheckData.horizontalAlignment = GridData.FILL;
			natureCheckData.grabExcessHorizontalSpace = true;
			natureCheck.setLayoutData(natureCheckData);

			// tab forlder
			tabFolder = new TabFolder(this, SWT.NULL);
			GridData tabFolderLData = new GridData();
			tabFolderLData.horizontalAlignment = GridData.FILL;
			tabFolder.setLayoutData(tabFolderLData);

			// tab item
			markerComposite = new Composite(tabFolder, SWT.NULL);
			markerComposite.setLayout(new GridLayout(2, false));
			markerTabItem = new TabItem(tabFolder, SWT.NULL);
			markerTabItem.setText(Messages.getString("ProjectPropertyGUI.1")); //$NON-NLS-1$
			markerTabItem.setControl(markerComposite);

			// parser title
			parserMarkerLabel = new Label(markerComposite, SWT.NULL);
			parserMarkerLabel.setText(Messages.getString("ProjectPropertyGUI.2")); //$NON-NLS-1$
			new Label(markerComposite, SWT.NULL);

			// xml error
			xmlErrorLabel = new Label(markerComposite, SWT.NULL);
			xmlErrorLabel.setText(Messages.getString("ProjectPropertyGUI.3")); //$NON-NLS-1$
			xmlErrorCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// xml warning
			xmlWarningLabel = new Label(markerComposite, SWT.NULL);
			xmlWarningLabel.setText(Messages.getString("ProjectPropertyGUI.4")); //$NON-NLS-1$
			xmlWarningCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// dummy line
			new Label(markerComposite, SWT.NULL);
			new Label(markerComposite, SWT.NULL);

			// validation check
			validationCheck = new Button(markerComposite, SWT.CHECK | SWT.LEFT);
			validationCheck.setText(Messages.getString("ProjectPropertyGUI.11")); //$NON-NLS-1$
			validationCheck.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent evt) {
					validationCheckWidgetSelected(validationCheck.getSelection());
				}
			});
			new Label(markerComposite, SWT.NULL);

			// validation title
			validationMarkerLabel = new Label(markerComposite, SWT.NULL);
			validationMarkerLabel.setText(Messages.getString("ProjectPropertyGUI.10")); //$NON-NLS-1$
			new Label(markerComposite, SWT.NULL);

			// null injection
			nullInjectionLabel = new Label(markerComposite, SWT.NULL);
			nullInjectionLabel.setText(Messages.getString("ProjectPropertyGUI.5")); //$NON-NLS-1$
			nullInjectionCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// auto injection
			autoInjectionLabel = new Label(markerComposite, SWT.NULL);
			autoInjectionLabel.setText(Messages.getString("ProjectPropertyGUI.6")); //$NON-NLS-1$
			autoInjectionCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// java fetal
			javaFetalLabel = new Label(markerComposite, SWT.NULL);
			javaFetalLabel.setText(Messages.getString("ProjectPropertyGUI.7")); //$NON-NLS-1$
			javaFetalCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// dicon fetail
			diconFetalLabel = new Label(markerComposite, SWT.NULL);
			diconFetalLabel.setText(Messages.getString("ProjectPropertyGUI.8")); //$NON-NLS-1$
			diconFetalCombo = new Combo(markerComposite, SWT.READ_ONLY);

			// problem dicon
			problemDiconLabel = new Label(markerComposite, SWT.NULL);
			problemDiconLabel.setText(Messages.getString("ProjectPropertyGUI.9")); //$NON-NLS-1$
			diconProblemCombo = new Combo(markerComposite, SWT.READ_ONLY);

			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void preInitGUI() {
	}

	public void postInitGUI() {
	}

	public Button getNatureCheck() {
		return natureCheck;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public Combo getXmlErrorCombo() {
		return xmlErrorCombo;
	}

	public Combo getXmlWarningCombo() {
		return xmlWarningCombo;
	}

	public Button getValidationCheck() {
		return validationCheck;
	}

	public Combo getNullInjectionCombo() {
		return nullInjectionCombo;
	}

	public Combo getAutoInjectionCombo() {
		return autoInjectionCombo;
	}

	public Combo getJavaFetalCombo() {
		return javaFetalCombo;
	}

	public Combo getDiconFetalCombo() {
		return diconFetalCombo;
	}

	public Combo getDiconProblemCombo() {
		return diconProblemCombo;
	}

	public void natureCheckWidgetSelected(boolean flg) {
		tabFolder.setEnabled(flg);
		parserMarkerLabel.setEnabled(flg);
		xmlErrorLabel.setEnabled(flg);
		xmlErrorCombo.setEnabled(flg);
		xmlWarningLabel.setEnabled(flg);
		xmlWarningCombo.setEnabled(flg);
		validationCheck.setEnabled(flg);
	}

	public void validationCheckWidgetSelected(boolean flg) {
		validationMarkerLabel.setEnabled(flg);
		nullInjectionLabel.setEnabled(flg);
		nullInjectionCombo.setEnabled(flg);
		autoInjectionLabel.setEnabled(flg);
		autoInjectionCombo.setEnabled(flg);
		javaFetalLabel.setEnabled(flg);
		javaFetalCombo.setEnabled(flg);
		diconFetalLabel.setEnabled(flg);
		diconFetalCombo.setEnabled(flg);
		problemDiconLabel.setEnabled(flg);
		diconProblemCombo.setEnabled(flg);
	}

}
