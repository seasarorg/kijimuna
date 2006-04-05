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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.contentassist.IContentAssistant;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.util.CoreUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconAssistProcessor extends XmlAssistProcessor implements ConstUI {

    public DiconAssistProcessor(IFile file) {
        super(file);
    }

    protected boolean isBodyChar(char c) {
    	String test = "#@.";
    	return (test.indexOf(c) != -1);
    }
    
    protected boolean isAttributeChar(char c) {
    	String test = "/."; 
        return (test.indexOf(c) != -1);
    }

    protected String getRootElementName() {
        return DICON_TAG_CONTAINER;
    }
    
	protected IDtd getDtd(IDocument doc, int offset) {
		try {
			String stringToOffset = doc.get(0, offset);
			return CoreUtils.loadDtd(CoreUtils.getPublicId(stringToOffset));
		} catch (BadLocationException e) {
			return null;
		}
	}

	protected IContentAssistant getAttributeValueContentAssistant(
    		IDtd dtd, XmlRegion xmlRegion) {
        return new DiconAttributeValueAssistant(this, dtd, xmlRegion);
    }
    
    protected IContentAssistant getElementContentAssistant(
            IDtd dtd, XmlRegion xmlRegion, String rootElementName) {
        return new DiconElementAssistant(this, dtd, xmlRegion, rootElementName);
    }
    
    protected IContentAssistant getXmlDeclContentAssistant(XmlRegion xmlRegion) {
        return new DiconXmlDeclAssistant(this, xmlRegion);
    }
    
    protected IContentAssistant getDocDeclContentAssistant(XmlRegion xmlRegion) {
    	return new DiconDocDeclAssistant(this, xmlRegion);
    }

}
