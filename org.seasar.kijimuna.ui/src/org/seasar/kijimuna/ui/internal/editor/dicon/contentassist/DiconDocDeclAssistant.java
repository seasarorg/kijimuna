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

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconDocDeclAssistant extends AbstractXmlAssistant {

	public DiconDocDeclAssistant(XmlAssistProcessor processor, XmlRegion xmlRegion) {
		super(processor, null, xmlRegion);
	}

	private ICompletionProposal createDeclProposal(String prefix, int offset,
			String version, String publicId, String systemId) {
		String decl = KijimunaUI.getResourceString("dicon.wizard.NewDiconWizardPage.11",
				new Object[] {
						publicId, systemId
				});
		String display = KijimunaUI.getResourceString(
				"dicon.editor.contentassist.DiconDocDeclAssistant.1", new Object[] {
					version
				});
		if (isMatch(decl, prefix)) {
			return createProposal(decl, display, prefix, offset, decl.length(), null);
		}
		return null;
	}

	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		List proposals = new ArrayList();
		String regionText = getXmlRegion().getText();
		int cursorOffset = getXmlRegion().getCursorOffset();
		String docPrefix = regionText.substring(0, cursorOffset);
		ICompletionProposal proposal1 = createDeclProposal(docPrefix, offset,
				DTD_DISPLAY_20, PUBLIC_ID_DICON_20, SYSTEM_ID_DICON_20);
		if (proposal1 != null) {
			proposals.add(proposal1);
		}
		ICompletionProposal proposal2 = createDeclProposal(docPrefix, offset,
				DTD_DISPLAY_21, PUBLIC_ID_DICON_21, SYSTEM_ID_DICON_21);
		if (proposal2 != null) {
			proposals.add(proposal2);
		}
		ICompletionProposal proposal3 = createDeclProposal(docPrefix, offset,
				DTD_DISPLAY_23, PUBLIC_ID_DICON_23, SYSTEM_ID_DICON_23);
		if (proposal3 != null) {
			proposals.add(proposal3);
		}
		ICompletionProposal proposal4 = createDeclProposal(docPrefix, offset,
				DTD_DISPLAY_24, PUBLIC_ID_DICON_24, SYSTEM_ID_DICON_24);
		if (proposal4 != null) {
			proposals.add(proposal4);
		}
		return (ICompletionProposal[]) proposals
				.toArray(new ICompletionProposal[proposals.size()]);
	}

}
