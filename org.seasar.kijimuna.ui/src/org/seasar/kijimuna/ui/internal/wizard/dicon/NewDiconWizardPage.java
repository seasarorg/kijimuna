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
package org.seasar.kijimuna.ui.internal.wizard.dicon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class NewDiconWizardPage extends NewTypeWizardPage implements ModifyListener,
		ConstUI {

	private Text nameText;
	private Combo encodingCombo;
	private Combo versionCombo;

	public NewDiconWizardPage(String pageName) {
		super(false, pageName);
		setTitle(KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.1"));
		setDescription(KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.2"));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		final int nColumns = 4;
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);

		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText(KijimunaUI
				.getResourceString("dicon.wizard.NewDiconWizardPage.3"));

		nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData text1LData1 = new GridData();
		text1LData1.horizontalAlignment = GridData.FILL;
		text1LData1.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(text1LData1);
		nameText.addModifyListener(this);

		Label nullLabel1 = new Label(composite, SWT.NONE);
		GridData nullLabelLData1 = new GridData();
		nullLabelLData1.horizontalSpan = 2;
		nullLabelLData1.horizontalAlignment = GridData.FILL;
		nullLabel1.setLayoutData(nullLabelLData1);

		Label encodingLabel = new Label(composite, SWT.NONE);
		encodingLabel.setText(KijimunaUI
				.getResourceString("dicon.wizard.NewDiconWizardPage.4"));

		encodingCombo = new Combo(composite, SWT.NONE);
		initEncodingCombo(encodingCombo);
		encodingCombo.addModifyListener(this);

		Label nullLabel2 = new Label(composite, SWT.NONE);
		GridData nullLabelLData2 = new GridData();
		nullLabelLData2.horizontalSpan = 2;
		nullLabelLData2.horizontalAlignment = GridData.FILL;
		nullLabel2.setLayoutData(nullLabelLData2);

		Label versionLabel = new Label(composite, SWT.NONE);
		versionLabel.setText(KijimunaUI
				.getResourceString("dicon.wizard.NewDiconWizardPage.5"));

		versionCombo = new Combo(composite, SWT.NONE);
		initVersionCombo(versionCombo);
		versionCombo.addModifyListener(this);

		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private void initEncodingCombo(Combo combo) {
		combo.setText("UTF-8");
		if (combo.getItemCount() == 0) {
			String[] encodings = WorkbenchUtils.getAllWorkbenchEncodings();
			for (int i = 0; i < encodings.length; i++) {
				combo.add(encodings[i]);
			}
		}
	}

	private void initVersionCombo(Combo combo) {
		combo.setText(DTD_DISPLAY_23);
		if (combo.getItemCount() == 0) {
			combo.add(DTD_DISPLAY_20);
			combo.add(DTD_DISPLAY_21);
			combo.add(DTD_DISPLAY_23);
			combo.add(DTD_DISPLAY_24);
		}
	}

	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
	}

	private IStatus createStatus(int severity, String message) {
		return new Status(severity, ID_PLUGIN_UI, IStatus.OK, message, null);
	}

	private IStatus createNameStatus() {
		int severity = IStatus.ERROR;
		String message = "";
		if (nameText != null) {
			String name = nameText.getText();
			if (StringUtils.noneValue(name)) {
				message = KijimunaUI
						.getResourceString("dicon.wizard.NewDiconWizardPage.6");
			} else {
				IPackageFragment pack = getPackageFragment();
				if (!name.endsWith("." + EXT_DICON)) {
					name = name + "." + EXT_DICON;
				}
				try {
					IContainer folder = (IContainer) pack.getUnderlyingResource();
					IFile file = folder.getFile(new Path(name));
					if (file.exists()) {
						message = KijimunaUI
								.getResourceString("dicon.wizard.NewDiconWizardPage.13");
					} else {
						severity = IStatus.OK;
					}
				} catch (JavaModelException e) {
					KijimunaCore.reportException(e);
				}
			}
		}
		return createStatus(severity, message);
	}

	private IStatus createEncodingStatus() {
		int severity = IStatus.ERROR;
		String message = "";
		if (encodingCombo != null) {
			String enc = encodingCombo.getText();
			if (StringUtils.noneValue(enc)) {
				message = KijimunaUI
						.getResourceString("dicon.wizard.NewDiconWizardPage.7");
			} else {
				try {
					String test = "enc test string";
					test.getBytes(enc);
					severity = IStatus.OK;
				} catch (UnsupportedEncodingException e) {
					message = KijimunaUI
							.getResourceString("dicon.wizard.NewDiconWizardPage.8");
				}
			}
		}
		return createStatus(severity, message);
	}

	private IStatus createPackageExistStatus() {
		int severity = IStatus.ERROR;
		String message = "";
		IPackageFragment pack = getPackageFragment();
		if (pack.exists()) {
			severity = IStatus.OK;
		} else {
			message = KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.9");
		}
		return createStatus(severity, message);
	}

	private void doStatusUpdate() {
		IStatus nameStatus = createNameStatus();
		IStatus encodingStatus = createEncodingStatus();
		IStatus packageExist = createPackageExistStatus();
		IStatus[] status = new IStatus[] {
				fContainerStatus, fPackageStatus, nameStatus, encodingStatus,
				packageExist
		};
		updateStatus(status);
	}

	public void modifyText(ModifyEvent e) {
		doStatusUpdate();
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			nameText.setFocus();
		}
	}

	private String getPublicId(String selected) {
		if (DTD_DISPLAY_20.equals(selected)) {
			return PUBLIC_ID_DICON_20;
		}
		if (DTD_DISPLAY_21.equals(selected)) {
			return PUBLIC_ID_DICON_21;
		}
		if (DTD_DISPLAY_24.equals(selected)) {
			return PUBLIC_ID_DICON_24;
		}
		return PUBLIC_ID_DICON_23;
	}

	private String getSystemId(String selected) {
		if (DTD_DISPLAY_20.equals(selected)) {
			return SYSTEM_ID_DICON_20;
		}
		if (DTD_DISPLAY_21.equals(selected)) {
			return SYSTEM_ID_DICON_21;
		}
		if (DTD_DISPLAY_24.equals(selected)) {
			return SYSTEM_ID_DICON_24;
		}
		return SYSTEM_ID_DICON_23;
	}

	public boolean createDiconFile() {
		IPackageFragment pack = getPackageFragment();
		String name = nameText.getText();
		if (!name.endsWith("." + EXT_DICON)) {
			name = name + "." + EXT_DICON;
		}
		String enc = encodingCombo.getText();
		String publicId = getPublicId(versionCombo.getText());
		String systemId = getSystemId(versionCombo.getText());
		String dicon = KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.10",
				new Object[] {
					enc
				})
				+ KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.11",
						new Object[] {
								publicId, systemId
						})
				+ KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.12");
		try {
			InputStream contents = new ByteArrayInputStream(dicon.getBytes(enc));
			IFile file = FileUtils.createFile(pack, name, contents);
			if (file != null) {
				file.setCharset(enc, null);
				WorkbenchUtils.openDiconEditor(file);
				return true;
			}
		} catch (CoreException e) {
			KijimunaUI.reportException(e);
		} catch (UnsupportedEncodingException e) {
			KijimunaUI.reportException(e);
		}
		return false;
	}

}
