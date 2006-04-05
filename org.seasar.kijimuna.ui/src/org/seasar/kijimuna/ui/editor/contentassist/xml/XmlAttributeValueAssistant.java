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
import org.seasar.kijimuna.ui.editor.contentassist.xml.AbstractXmlAssistant;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class XmlAttributeValueAssistant extends AbstractXmlAssistant {

	public XmlAttributeValueAssistant(XmlAssistProcessor processor,
	        IDtd dtd, XmlRegion xmlRegion) {
	    super(processor, dtd, xmlRegion);
	}
	
	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		return getDefaultCompletionProposal(prefix, offset);
	}

	private ICompletionProposal[] getDefaultCompletionProposal(String prefix, int offset) {
		String elementName = getXmlRegion().getElementName();
		if(StringUtils.existValue(elementName)) {
			IElementDef element = getDtd().getElement(elementName);
			if(element != null) {
				String attributeName = getXmlRegion().getAttributeName();
				if(StringUtils.existValue(attributeName)) {
					IAttributeDef attribute = element.getAttribute(attributeName);
					if(attribute != null) {
						List proposals = new ArrayList();
						String[] items = attribute.getItems();
						for (int i = 0; i < items.length; i++) {
							String item = items[i];
							if (isMatch(item, prefix)) {
							    ICompletionProposal proposal = createProposal(item, item, prefix, 
								            offset, item.length(), IMAGE_ICON_XML_ITEM);
								proposals.add(proposal);
							}
						}
						Collections.sort(proposals, new ProposalComparator());
						return (ICompletionProposal[]) proposals.toArray(
						        new ICompletionProposal[proposals.size()]);
					}
				}
			}
		}
		return NO_PROPOSALS;
	}

}
