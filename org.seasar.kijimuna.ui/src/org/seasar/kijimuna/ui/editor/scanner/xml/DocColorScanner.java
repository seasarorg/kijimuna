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
import org.eclipse.jface.text.rules.WhitespaceRule;

import org.seasar.kijimuna.ui.editor.configuration.ColorManager;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DocColorScanner extends AbstractColorScanner {

	public DocColorScanner(ColorManager manager) {
		super(manager);
		configure();
	}
	
	public void configure() {
		IRule[] rules = new IRule[4];
		rules[0] = new CommentRule(getColorToken(PREF_COLOR_COMMENT));
		rules[1] = new XmlDeclRule(getColorToken(PREF_COLOR_XML_DECL));
		rules[2] = new DocDeclRule(getColorToken(PREF_COLOR_DOC_DECL));
		rules[3] = new WhitespaceRule(new WhitespaceDetector());
		setRules(rules);
		setDefaultReturnToken(getColorToken(PREF_COLOR_DEFAULT));
	}

}
