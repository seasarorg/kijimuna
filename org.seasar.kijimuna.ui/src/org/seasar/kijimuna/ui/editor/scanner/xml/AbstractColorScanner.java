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
package org.seasar.kijimuna.ui.editor.scanner.xml;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.editor.configuration.ColorManager;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlConsts;

public abstract class AbstractColorScanner extends RuleBasedScanner implements XmlConsts {

	private ColorManager manager;

	public AbstractColorScanner(ColorManager manager) {
		this.manager = manager;
	}

	protected ColorManager getColorManager() {
		return manager;
	}

	protected IToken getColorToken(String prefKey) {
		return getColorToken(getRGB(prefKey));
	}

	protected IToken getColorToken(RGB rgb) {
		return new Token(new TextAttribute(getColorManager().getColor(rgb)));
	}

	protected RGB getRGB(String prefKey) {
		IPreferenceStore pref = PreferencesUtil.getPreferenceStoreOfWorkspace();
		try {
			return StringConverter.asRGB(pref.getString(prefKey));
		} catch (DataFormatException e) {
			return COLOR_DEFAULT;
		}
	}

}
