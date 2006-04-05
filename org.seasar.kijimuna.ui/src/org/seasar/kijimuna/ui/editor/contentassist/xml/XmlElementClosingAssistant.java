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
package org.seasar.kijimuna.ui.editor.contentassist.xml;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.dtd.IElementDef;
import org.seasar.kijimuna.core.parser.IElement;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 */
public class XmlElementClosingAssistant extends AbstractXmlAssistant {

	public XmlElementClosingAssistant(XmlAssistProcessor processor, IDtd dtd,
			XmlRegion xmlRegion) {
		super(processor, dtd, xmlRegion);
	}

	private String getDisplayText(IElementDef element) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(element.getName());
		return buffer.toString();
	}

	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		IDtd dtd = getDtd();
		if (dtd != null) {
			XmlRegion xmlRegion = getXmlRegion();
			IElement parent = getParentElement();
			if (parent != null) {
				String parentElementName = parent.getElementName();
				IElementDef element = dtd.getElement(parentElementName);
				if (element != null) {
					String elementName = parentElementName;
					if (isMatch(elementName, prefix)) {
						String replaceStr = "";
						if (xmlRegion.getText().startsWith("</")) {
							replaceStr = elementName + ">";
						} else {
							replaceStr = "</" + elementName + ">";
						}
						String displayText = getDisplayText(element);
						return new ICompletionProposal[] {
							createProposal(replaceStr, displayText, prefix, offset,
									replaceStr.length(), IMAGE_ICON_XML_TAG)
						};
					}
				}
			}
		}
		return NO_PROPOSALS;
	}

}
