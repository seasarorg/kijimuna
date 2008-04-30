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

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;

import org.seasar.kijimuna.ui.editor.configuration.ColorManager;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TagColorScanner extends AbstractColorScanner {

	public TagColorScanner(ColorManager manager) {
		super(manager);
		configure();
	}

	public void configure() {
		IToken attribute = getColorToken(PREF_COLOR_ATTRIBUTE);
		IRule[] rules = new IRule[3];
		rules[0] = new SingleLineRule("\"", "\"", attribute, '\\');
		rules[1] = new SingleLineRule("'", "'", attribute, '\\');
		rules[2] = new WhitespaceRule(new WhitespaceDetector());
		setRules(rules);
		setDefaultReturnToken(getColorToken(PREF_COLOR_TAG));
	}

}
