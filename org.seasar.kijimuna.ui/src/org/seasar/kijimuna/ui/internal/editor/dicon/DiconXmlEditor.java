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
package org.seasar.kijimuna.ui.internal.editor.dicon;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.editor.configuration.ColorManager;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlDocumentProvider;
import org.seasar.kijimuna.ui.internal.editor.dicon.configuration.DiconConfiguration;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 * @author Toshitaka Agata (Nulab, Inc.)
 */
public class DiconXmlEditor extends TextEditor implements IPropertyChangeListener,
		ConstUI {

	private ColorManager colorManager;

	public DiconXmlEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new DiconConfiguration(this, colorManager));
		setDocumentProvider(new XmlDocumentProvider());
		PreferencesUtil.getPreferenceStoreOfWorkspace().addPropertyChangeListener(this);
	}

	public void dispose() {
		PreferencesUtil.getPreferenceStoreOfWorkspace()
				.removePropertyChangeListener(this);
		colorManager.dispose();
		super.dispose();
	}

	protected void createActions() {
		super.createActions();
		IAction action = new ContentAssistAction(KijimunaUI.getResourceBundle(),
				ACTION_CONTENTASSIST_PROPOSAL, this);
		action
				.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction(ACTION_CONTENTASSIST_PROPOSAL, action);
	}

	public void propertyChange(PropertyChangeEvent event) {
		DiconConfiguration config = (DiconConfiguration) getSourceViewerConfiguration();
		if (config != null) {
			config.updatePreferences();
			getSourceViewer().invalidateTextPresentation();
		}
	}
}
