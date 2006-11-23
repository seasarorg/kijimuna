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
package org.seasar.kijimuna.ui.internal.editor.dicon.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.editor.contentassist.xml.AbstractXmlAssistant;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconXmlDeclAssistant extends AbstractXmlAssistant {

	public DiconXmlDeclAssistant(XmlAssistProcessor processor, XmlRegion xmlRegion) {
		super(processor, null, xmlRegion);
	}

	private ICompletionProposal createDeclProposal(String prefix, int offset, String enc) {
		String decl = KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.10",
				new Object[] {
					enc
				});
		if (isMatch(decl, prefix)) {
			return createProposal(decl, enc, prefix, offset, decl.length(), null);
		}
		return null;
	}

	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		List proposals = new ArrayList();
		String regionText = getXmlRegion().getText();
		int cursorOffset = getXmlRegion().getCursorOffset();
		String xmlPrefix = regionText.substring(0, cursorOffset);
		String[] enc = WorkbenchUtils.getAllWorkbenchEncodings();
		for (int i = 0; i < enc.length; i++) {
			ICompletionProposal proposal = createDeclProposal(xmlPrefix, offset, enc[i]);
			if (proposal != null) {
				proposals.add(proposal);
			}
		}
		return (ICompletionProposal[]) proposals
				.toArray(new ICompletionProposal[proposals.size()]);
	}

}
