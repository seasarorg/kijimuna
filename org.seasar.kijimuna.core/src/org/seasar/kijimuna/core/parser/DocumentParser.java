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
package org.seasar.kijimuna.core.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kijimuna.core.KijimunaCore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DocumentParser {

	private static SAXParserFactory factory;
	
	private IParseResult parse(InputStream stream, String systemID, 
			IProgressMonitor monitor, DocumentHandler handler) {
	    if((stream == null) || (handler == null)) {
	        return null;
	    }
		handler.setProgressMonitor(monitor);
		try {
			InputSource source = new InputSource(stream);
			if(systemID != null) {
			    source.setSystemId(systemID);
			}
			getParser().parse(source, handler);
		} catch (Exception e) {
			KijimunaCore.reportException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				KijimunaCore.reportException(e);
			}
		}
		return handler.getResult();
	}

	public IParseResult parse(String text, IFile file, DocumentHandler handler) {
		String systemID = file.getFullPath().toString();
		InputStream in;
		try {
			in = new ByteArrayInputStream(text.getBytes(file.getCharset()));
		} catch (UnsupportedEncodingException e) {
			in = new ByteArrayInputStream(text.getBytes());
		} catch (CoreException e) {
			in = new ByteArrayInputStream(text.getBytes());
		}
		return parse(in, systemID, null, handler);
	}
	
	public IParseResult parse(IProject project, IStorage storage, IProgressMonitor monitor, 
			DocumentHandler handler) {
		if (storage == null) {
			return null;
		}
		if (handler != null) {
			handler.setStorage(project, storage);
		} else {
			return null;
		}
		try {
			InputStream stream = storage.getContents();
			String systemID = storage.getFullPath().toString(); 
			return parse(stream, systemID, monitor, handler);
		} catch (Exception e) {
			KijimunaCore.reportException(e);
			return null;
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (factory == null) {
			factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
		}
		return factory.newSAXParser();
	}

}