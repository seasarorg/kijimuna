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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlConsts;
import org.seasar.kijimuna.ui.editor.contentassist.IContentAssistant;
import org.seasar.kijimuna.ui.util.WidgetUtils;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class XmlAssistProcessor 
		implements IContentAssistProcessor, ConstUI, XmlConsts {

	private IFile file;
	private ImageRegistry registry;
	
	public XmlAssistProcessor(IFile file) {
		super();
		this.file = file;
		registry = new ImageRegistry();
	}
    
	public Image getImage(String name) {
	    Image image = registry.get(name);
	    if(image == null) {
	        ImageDescriptor descriptor = WidgetUtils.getImageDescriptor(name);
	        image = descriptor.createImage();
	        registry.put(name, image);
	    }
	    return image;
	}
	
	protected String getRootElementName() {
	    return null;
	}
	
	protected IDtd getDtd(IDocument doc, int offset) {
		return null;
	}
    
	protected boolean isAttributeChar(char c) {
	    return false;
	}

	protected boolean isBodyChar(char c) {
		return false;
	}
	
	protected IContentAssistant getXmlDeclContentAssistant(XmlRegion xmlRegion) {
	    return null;
	}
	
	protected IContentAssistant getDocDeclContentAssistant(XmlRegion xmlRegion) {
		return null;
	}
	
	protected IContentAssistant getElementContentAssistant(
	        IDtd dtd, XmlRegion xmlRegion, String rootElementName) {
	    return new XmlElementAssistant(this, dtd, xmlRegion, rootElementName);
	}
	
	protected IContentAssistant getElementClosingContentAssistant(
			IDtd dtd, XmlRegion xmlRegion) {
	    return new XmlElementClosingAssistant(this, dtd, xmlRegion);
	}
	
	protected IContentAssistant getAttributeContentAssistant(IDtd dtd, XmlRegion xmlRegion) {
	    return new XmlAttributeAssistant(this, dtd, xmlRegion);
	}
	
	protected IContentAssistant getAttributeValueContentAssistant(
			IDtd dtd, XmlRegion xmlRegion) {
	    return new XmlAttributeValueAssistant(this, dtd, xmlRegion);
	}
	
    private String getElementName(String regionText, int cursorOffset) {
    	StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < cursorOffset; i++) {
			char c = regionText.charAt(i);
			if(StringUtils.isWhitespace(c)) {
				return buffer.toString();
			} else if(Character.isJavaIdentifierPart(c)){
				buffer.append(c);
			} else if(c != '<' && c != '/') {
				break;
			}
		}
    	return null;
    }
    
    private String getAttributeName(String regionText, int cursorOffset) {
    	StringBuffer ret = null;
        for(int pos = cursorOffset - 1; pos > -1; pos--) {
        	char c = regionText.charAt(pos);
        	if(ret != null) {
            	if(Character.isJavaIdentifierPart(c)) {
            		ret.insert(0, c);
            	} else {
            		return ret.toString();
            	}
        	} else {
        		if(c == '"' || c == '\'') {
        			for(pos--; pos > -1; pos--) {
        				c = regionText.charAt(pos);
        				if(c == '=' ) {
    	        			ret = new StringBuffer();
    	        			break;
        				} else if(!StringUtils.isWhitespace(c)) {
        					return null;
        				}
        			}
        		}
        	}
        }
    	return null;
    }
    
    private IRegion getAttributeValueRegion(String regionText, int regionOffset, int cursorOffset) {
        for(int pos = cursorOffset - 1; pos > -1; pos--) {
        	char c = regionText.charAt(pos);
    		if(c == '"' || c == '\'') {
    			int offset = pos + 1;
    			for(pos = offset; pos < regionText.length(); pos++) {
    				c = regionText.charAt(pos);
    				if(c == '"' || c == '\'') {
    					return new Region(offset + regionOffset, pos - offset);
    				}
    			}
    			return null;
        	}
        }
    	return null;
    }
    
    public XmlRegion getXmlRegiton(IDocument doc, int offset) 
    		throws BadLocationException {
	    String stringToOffset = doc.get(0, offset);
		ITypedRegion region = doc.getPartition(offset);
		String regionText = doc.get(region.getOffset(), region.getLength());
		int cursorOffset = offset - region.getOffset();
        int proposalMode = PROPOSAL_MODE_NONE;
        String regionType = region.getType();
        int regionOffset = region.getOffset();
        String elementName = null;
        String attributeName = null;
        String hyperlinkText = null;
        IRegion hyperlinkRegion = null;
        if(regionType.equals(TYPE_XML_DECL)) {
		    proposalMode = PROPOSAL_MODE_XML_DECL;
		} else if(regionType.equals(TYPE_DOC_DECL)) {
		    proposalMode = PROPOSAL_MODE_DOC_DECL;
		} else if(regionType.equals(TYPE_TAG)) {
			if(cursorOffset == 0) {
				// special pattern
				proposalMode = PROPOSAL_MODE_ELEMENT;
				int lastGt = stringToOffset.lastIndexOf('>');
				if(lastGt != -1) {
					regionOffset = lastGt + 1;
					regionText = stringToOffset.substring(regionOffset, stringToOffset.length());
					cursorOffset = regionText.length();
				}
			} else {
				elementName = getElementName(regionText, cursorOffset);
				if(StringUtils.existValue(elementName)) {
					attributeName = getAttributeName(regionText, cursorOffset);
					if(StringUtils.existValue(attributeName)) {
					    proposalMode = PROPOSAL_MODE_ATTRIBUTE_VALUE;
				    	hyperlinkRegion = getAttributeValueRegion(regionText, regionOffset, cursorOffset);
				    	hyperlinkText = doc.get(hyperlinkRegion.getOffset(), hyperlinkRegion.getLength());
					} else {
						proposalMode = PROPOSAL_MODE_ATTRIBUTE;
					}
				} else {
					if(regionText.length() > 2 && regionText.charAt(1) == '/') {
						proposalMode = PROPOSAL_MODE_ELEMENT_CLOSING;
					} else {
						proposalMode = PROPOSAL_MODE_ELEMENT;
					}
				}
			}
		} else if(regionType.equals(IDocument.DEFAULT_CONTENT_TYPE)) {
			proposalMode = PROPOSAL_MODE_ELEMENT;
			hyperlinkRegion = new Region(regionOffset, regionText.length());
			hyperlinkText = regionText;
		}
		return new XmlRegion(file, stringToOffset, regionOffset, cursorOffset, 
				proposalMode, regionText, elementName, attributeName, hyperlinkText, hyperlinkRegion);
    }

	private String getPrefixFromRegion(XmlRegion xmlRegion) {
		String text = xmlRegion.getText();
		boolean attributeValue = 
			(xmlRegion.getProposalMode() == PROPOSAL_MODE_ATTRIBUTE_VALUE);
		boolean body = 
			(xmlRegion.getProposalMode() == PROPOSAL_MODE_ELEMENT);
 		StringBuffer buffer = new StringBuffer();
        for(int pos = xmlRegion.getCursorOffset() - 1; pos > -1; pos--) {
        	char c = text.charAt(pos);
            if(Character.isJavaIdentifierPart(c) || (body && isBodyChar(c)) ||
            		(attributeValue && isAttributeChar(c))) {
                buffer.insert(0, c);
            } else {
            	break;
            }
        }
		return buffer.toString();
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
	    if(offset == -1) {
	        return NO_PROPOSALS;
	    }
		IDocument doc = viewer.getDocument();
		XmlRegion xmlRegion;
		try {
			xmlRegion = getXmlRegiton(doc, offset);
		} catch (BadLocationException e) {
			return NO_PROPOSALS;
		}		
		IContentAssistant assistant = null;
		IDtd dtd = getDtd(doc, offset);
		String rootElementName = getRootElementName();
		if(xmlRegion.getProposalMode() == PROPOSAL_MODE_XML_DECL) {
			assistant = getXmlDeclContentAssistant(xmlRegion);
		} else if(xmlRegion.getProposalMode() == PROPOSAL_MODE_DOC_DECL) {
			assistant = getDocDeclContentAssistant(xmlRegion);
		} else if(xmlRegion.getProposalMode() == PROPOSAL_MODE_ELEMENT) {
		    assistant = getElementContentAssistant(dtd, xmlRegion, rootElementName);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ELEMENT_CLOSING) {
			assistant = getElementClosingContentAssistant(dtd, xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ATTRIBUTE) {
            assistant = getAttributeContentAssistant(dtd, xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ATTRIBUTE_VALUE) {
			assistant = getAttributeValueContentAssistant(dtd, xmlRegion);
		}
		String prefix = getPrefixFromRegion(xmlRegion);
		if(assistant != null) {
			ICompletionProposal[] proposals = assistant.getCompletionProposal(prefix, offset);
			return proposals;
		}
		return NO_PROPOSALS;
	}
	
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
