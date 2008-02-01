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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.internal.preference.design.DesignPane;
import org.seasar.kijimuna.ui.internal.preference.design.DiconEditorColoringDesign;
import org.seasar.kijimuna.ui.internal.preference.design.ErrorMarkerDesign;
import org.seasar.kijimuna.ui.internal.preference.design.Messages;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PluginPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, ConstCore {

	private ErrorMarkerDesign markerDesign;
	private DiconEditorColoringDesign coloringDesign;

	public void init(IWorkbench workbench) {
		IPreferenceStore store = PreferencesUtil.getPreferenceStoreOfWorkspace();
		setPreferenceStore(store);
	}
	
	protected Control createContents(Composite parent) {
		IPreferenceStore store = getPreferenceStore();

		DesignPane pane = new DesignPane(parent, SWT.NULL);
		TabFolder folder = pane.getTabFolder();
		
		// error marker tab
		markerDesign = new ErrorMarkerDesign(folder, SWT.NULL, store);
		TabItem item = new TabItem(folder, SWT.NULL);
		item.setText(Messages.getString("ErrorMarkerDesign.1"));
		item.setControl(markerDesign);
		
		// editor coloring tab
		coloringDesign = new DiconEditorColoringDesign(folder, SWT.NULL, store);
		item = new TabItem(folder, SWT.NULL);
		item.setText(Messages.getString("DiconEditorColoringDesign.1"));
		item.setControl(coloringDesign);
		
		return pane;
	}
	
	public boolean performOk() {
		applyModification();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws
					InvocationTargetException, InterruptedException {
				KijimunaCore.getProjectRecorder().cleanup(monitor);
			}
		};
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			if (w != null) {
				w.run(true, true, runnable);
			}
		} catch (InvocationTargetException e) {
			KijimunaCore.reportException(e);
		} catch (InterruptedException e) {
			KijimunaCore.reportException(e);
		}
		return true;
	}
	
	protected void performApply() {
		applyModification();
	}
	
	protected void performDefaults() {
		markerDesign.loadDefault();
		coloringDesign.loadDefault();
	}
	
	private void applyModification() {
		markerDesign.store();
		coloringDesign.store();
	}

}
