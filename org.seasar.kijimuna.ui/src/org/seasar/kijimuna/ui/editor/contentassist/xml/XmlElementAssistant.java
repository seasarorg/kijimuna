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
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.util.DtdUtils;
import org.seasar.kijimuna.ui.editor.contentassist.ProposalComparator;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class XmlElementAssistant extends AbstractXmlAssistant {

	private String rootElementName;

	public XmlElementAssistant(XmlAssistProcessor processor, IDtd dtd,
			XmlRegion xmlRegion, String rootElementName) {
		super(processor, dtd, xmlRegion);
		this.rootElementName = rootElementName;
	}

	private int getCursolPos(int offset, String requiredAttributes, int additional) {
		int index = requiredAttributes.indexOf('"');
		if (index > 0) {
			return offset + index + 1;
		}
		return offset + additional;
	}

	private String getRequiredAttributesStr(IElementDef element) {
		IAttributeDef[] requiredAttributes = DtdUtils.getRequiredAttributes(element);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < requiredAttributes.length; i++) {
			IAttributeDef attribute = requiredAttributes[i];
			buffer.append(" ");
			buffer.append(attribute.getName());
			buffer.append("=\"");
			if (attribute.getDefaultValue() != null) {
				buffer.append(attribute.getDefaultValue());
			}
			buffer.append("\"");
		}
		return buffer.toString();
	}

	private String getDTDDisplayText(IElementDef element) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(element.getName());
		return buffer.toString();
	}

	private void addDtdProposals(List proposals, String prefix, int offset,
			IElementDef[] elementDefs) {
		XmlRegion xmlRegion = getXmlRegion();
		for (int i = 0; i < elementDefs.length; i++) {
			IElementDef element = elementDefs[i];
			String elementName = element.getName();
			if (isMatch(elementName, prefix)) {
				String replaceStr = "";
				int cursolPos = 0;
				if (xmlRegion.getText().startsWith("<")
						&& xmlRegion.getCursorOffset() != 0) {
					String text = xmlRegion.getText().substring(prefix.length()).trim();
					if (xmlRegion.getText().endsWith(">") && text.indexOf("<") == -1) {
						replaceStr = elementName;
						cursolPos = elementName.length();
					} else {
						if (element.isEmpty()) {
							String required = getRequiredAttributesStr(element);
							replaceStr = elementName + required + "/>";
							cursolPos = getCursolPos(elementName.length(), required, 2);
						} else {
							String required = getRequiredAttributesStr(element);
							replaceStr = elementName + required + "></" + elementName
									+ ">";
							cursolPos = getCursolPos(elementName.length(), required, 1);
						}
					}
				} else {
					String required = getRequiredAttributesStr(element);
					if (element.isEmpty()) {
						replaceStr = "<" + elementName + required + "/>";
						cursolPos = getCursolPos(1 + elementName.length(), required, 0);
					} else {
						replaceStr = "<" + elementName + required + "></" + elementName
								+ ">";
						cursolPos = getCursolPos(1 + elementName.length(), required, 1);
					}
				}
				String displayText = getDTDDisplayText(element);
				ICompletionProposal proposal = createProposal(replaceStr, displayText,
						prefix, offset, cursolPos, IMAGE_ICON_XML_TAG);
				proposals.add(proposal);
			}
		}
	}

	protected String getRootElementName() {
		return rootElementName;
	}

	protected void addBodyProposals(List proposals, String prefix, int offset) {
	}

	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		IDtd dtd = getDtd();
		if (dtd != null) {
			IElement parent = getParentElement();
			if (parent != null) {
				IElementDef parentElementDef = dtd.getElement(parent.getElementName());
				if (parentElementDef != null) {
					IElementDef[] elementDefs = parentElementDef.getElements();
					List proposals = new ArrayList();
					addDtdProposals(proposals, prefix, offset, elementDefs);
					Collections.sort(proposals, new ProposalComparator());
					if (parentElementDef.hasPCData()) {
						addBodyProposals(proposals, prefix, offset);
					}
					return (ICompletionProposal[]) proposals
							.toArray(new ICompletionProposal[proposals.size()]);
				}
			} else {
				String rootName = getRootElementName();
				IElementDef root = dtd.getElement(rootName);
				if (root != null) {
					List proposals = new ArrayList();
					addDtdProposals(proposals, prefix, offset, new IElementDef[] {
						root
					});
					return (ICompletionProposal[]) proposals
							.toArray(new ICompletionProposal[proposals.size()]);
				}
			}
		}
		return NO_PROPOSALS;
	}

}
