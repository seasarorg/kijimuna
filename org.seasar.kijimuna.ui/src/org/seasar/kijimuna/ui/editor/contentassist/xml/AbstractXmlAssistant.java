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
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.contentassist.IContentAssistant;
import org.seasar.kijimuna.ui.util.CoreUtils;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public abstract class AbstractXmlAssistant implements IContentAssistant, ConstUI {

    private XmlAssistProcessor processor;
    private IDtd dtd;
	private XmlRegion xmlRegion;
    private IElement parentElement;
    
    protected boolean isMatch(String proposalName, String prefix) {
        return proposalName.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    protected AbstractXmlAssistant(
    		XmlAssistProcessor processor, IDtd dtd, XmlRegion xmlRegion) {
        this.processor = processor;
        this.dtd = dtd;
        this.xmlRegion = xmlRegion;
    }

    protected IDtd getDtd() {
        return dtd;
    }
    
    protected XmlRegion getXmlRegion() {
    	return xmlRegion;
    }
	
	protected IElement getParentElement() {
	    if(parentElement == null) {
		    XmlRegion xmlRegion = getXmlRegion();
		    String stringToOffset = xmlRegion.getStringToOffset();
		    IFile file = xmlRegion.getFile();
		    IParseResult result = CoreUtils.parse(stringToOffset, file);
		    parentElement = result.getLastStackElement();
	    }
	    return parentElement;
	}
    
    public ICompletionProposal createProposal(String replaceStr, String displayStr,
            String prefix, int offset, int cursorPosition, String imageName) {
        Image image = null;
        if(StringUtils.existValue(imageName)) {
            image = getImage(imageName);
        }
        return new CompletionProposal(
                replaceStr, offset - prefix.length(), prefix.length(),  
                cursorPosition, image, displayStr, null, null);
    }
    
    private Image getImage(String name) {
        return processor.getImage(name);
    }

}
