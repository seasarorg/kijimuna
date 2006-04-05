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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.seasar.kijimuna.core.dtd.IAttributeDef;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.dtd.IElementDef;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.editor.contentassist.ProposalComparator;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 */
public class XmlAttributeAssistant extends AbstractXmlAssistant {
	
	public XmlAttributeAssistant(
	        XmlAssistProcessor processor, IDtd dtd, XmlRegion xmlRegion) {
	    super(processor, dtd, xmlRegion);
	}

	private String getDisplayText(IAttributeDef attribute) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(attribute.getName());
		String[] items = attribute.getItems();
		if (items.length != 0) {
			buffer.append(" - (");
			for (int i = 0; i < items.length; i++) {
				String item = items[i];
				buffer.append(item);
				if (i < items.length - 1) {
					buffer.append(" | ");
				} else {
					buffer.append(")");
				}
			}
		}
		return buffer.toString();
	}
	
	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		IDtd dtd = getDtd();
		if(dtd != null) {
			XmlRegion xmlRegion = getXmlRegion();
			String elementName = xmlRegion.getElementName();
			if(StringUtils.existValue(elementName)) {
				IElementDef elementDef = dtd.getElement(elementName);
				if(elementDef != null) {
					IAttributeDef[] attributes = elementDef.getAttributes();
					List proposals = new ArrayList();
					String pre = "";
					char c = xmlRegion.getText().charAt(xmlRegion.getCursorOffset() - 1); 
					if(c == '"' || c == '\'') {
						pre = " ";
					}
					String post = "";
					c = xmlRegion.getText().charAt(xmlRegion.getCursorOffset());
					if(c != ' ' && c != '\t' && c != '\r' && c != '\n') {
						post = " ";
					}
					for (int i = 0; i < attributes.length; i++) {
						IAttributeDef attribute = attributes[i];
						String attrName = attribute.getName();
						if (isMatch(attrName, prefix)) {
							String replaceStr = pre + attrName + "=\"\"" + post;
							String displayText = getDisplayText(attribute);
							ICompletionProposal proposal = createProposal(
							        replaceStr, displayText, prefix, offset, 
									replaceStr.length() - 1 - post.length(), IMAGE_ICON_XML_ATTR);
							proposals.add(proposal);
						}
					}
					Collections.sort(proposals, new ProposalComparator());
					return (ICompletionProposal[]) proposals.toArray(
					        new ICompletionProposal[proposals.size()]);
				}
			}
		}
		return NO_PROPOSALS;
	}

}
