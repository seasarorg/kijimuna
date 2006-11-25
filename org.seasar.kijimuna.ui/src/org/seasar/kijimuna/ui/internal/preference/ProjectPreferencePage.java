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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.preference.design.DesignPane;
import org.seasar.kijimuna.ui.internal.preference.design.ErrorMarkerDesign;
import org.seasar.kijimuna.ui.internal.preference.design.Messages;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectPreferencePage extends PropertyPage implements ConstCore {

	private Button natureCheck;
	private ErrorMarkerDesign markerDesign;
	
	private IProject getProject() {
		return (IProject) getElement();
	}

	protected Control createContents(Composite parent) {
		DesignPane pane = new DesignPane(parent, SWT.NULL);
		
		// nature checkbox
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		natureCheck = new Button(pane, SWT.CHECK | SWT.LEFT);
		natureCheck.setText(Messages.getString("ProjectPreferencePage.1")); //$NON-NLS-1$
		natureCheck.setLayoutData(gd);
		natureCheck.setFont(parent.getFont());
		natureCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				markerDesign.setEnabled(natureCheck.getSelection());
			}
		});
		
		// error marker tab
		TabFolder folder = pane.getTabFolder();
		markerDesign = new ErrorMarkerDesign(folder, SWT.NULL, getProject());
		TabItem item = new TabItem(folder, SWT.NULL);
		item.setText(Messages.getString("ErrorMarkerDesign.1"));
		item.setControl(markerDesign);
		
		try {
			boolean hasNature = getProject().hasNature(ID_NATURE_DICON);
			natureCheck.setSelection(hasNature);
			markerDesign.setEnabled(hasNature);
		} catch (CoreException e) {
			KijimunaUI.reportException(e);
		}
		return pane;
	}
	
	public boolean performOk() {
		applyModification();
		cleanupDiconModel();
		return true;
	}
	
	protected void performApply() {
		applyModification();
	}
	
	private void applyModification() {
		try {
			IProject project = getProject();
			if (natureCheck.getSelection()) {
				if (!project.hasNature(ID_NATURE_DICON)) {
					ProjectUtils.addNature(project, ID_NATURE_DICON);
				}
				markerDesign.store();
			} else {
				markerDesign.store();
				ProjectUtils.removeNature(project, ID_NATURE_DICON);
			}
		} catch (CoreException e) {
			KijimunaUI.reportException(e);
		}
	}

	protected void performDefaults() {
		if (natureCheck.getSelection()) {
			markerDesign.loadDefault();
		}
	}
	
	private void cleanupDiconModel() {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws
					InvocationTargetException, InterruptedException {
				KijimunaCore.getProjectRecorder().cleanup(getProject(),
						monitor);
			}
		};
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			if (w != null) {
				w.run(true, true, runnable);
			}
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}
	}

}
