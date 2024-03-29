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
package org.seasar.kijimuna.ui.editor.configuration.xml;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.scanner.xml.XmlPartitionScanner;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class XmlDocumentProvider extends FileDocumentProvider implements XmlConsts,
		ConstUI {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = new DefaultPartitioner(
					new XmlPartitionScanner(), new String[] {
							TYPE_COMMENT, TYPE_XML_DECL, TYPE_DOC_DECL, TYPE_TAG,
							IDocument.DEFAULT_CONTENT_TYPE
					});
			document.setDocumentPartitioner(partitioner);
			partitioner.connect(document);
		}
		return document;
	}

}
