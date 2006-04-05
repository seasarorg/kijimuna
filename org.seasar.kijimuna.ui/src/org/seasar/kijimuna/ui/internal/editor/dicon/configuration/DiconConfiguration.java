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
package org.seasar.kijimuna.ui.internal.editor.dicon.configuration;

import org.eclipse.ui.IEditorPart;

import org.seasar.kijimuna.ui.editor.configuration.ColorManager;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlConfiguration;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.internal.editor.dicon.contentassist.DiconAssistProcessor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconConfiguration extends XmlConfiguration {

	public DiconConfiguration(IEditorPart editor, ColorManager colorManager) {
		super(editor, colorManager);
	}

	protected XmlAssistProcessor createAssistProcessor() {
		return new DiconAssistProcessor(getFile());
	}

}
