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
import org.eclipse.jface.text.IRegion;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara(Gluegent, Inc.)
 */
public class XmlRegion {

	private IFile file;
	private String stringToOffset;
	private int regionOffset;
	private int cursorOffset;
	private int proposalMode;
	private String regionText;
	private String elementName;
	private String attributeName;
	private String hyperlinkText;
	private IRegion hyperlinkRegion;

	public XmlRegion(IFile file, String stringToOffset, int regionOffset,
			int cursorOffset, int proposalMode, String regionText, String elementName,
			String attributeName, String hyperlinkText, IRegion hyperlinkRegion) {
		this.file = file;
		this.stringToOffset = stringToOffset;
		this.regionOffset = regionOffset;
		this.cursorOffset = cursorOffset;
		this.proposalMode = proposalMode;
		this.regionText = regionText;
		this.elementName = elementName;
		this.attributeName = attributeName;
		this.hyperlinkText = hyperlinkText;
		this.hyperlinkRegion = hyperlinkRegion;
	}

	public IFile getFile() {
		return file;
	}

	public String getStringToOffset() {
		return stringToOffset;
	}

	public int getCursorOffset() {
		return cursorOffset;
	}

	public int getOffset() {
		return regionOffset;
	}

	public String getText() {
		return regionText;
	}

	public int getProposalMode() {
		return proposalMode;
	}

	public String getElementName() {
		return elementName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getHyperlinkText() {
		return hyperlinkText;
	}

	public IRegion getHyperlinkRegion() {
		return hyperlinkRegion;
	}

}
