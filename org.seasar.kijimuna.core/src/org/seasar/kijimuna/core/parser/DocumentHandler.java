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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.dicon.model.ArgElement;
import org.seasar.kijimuna.core.internal.dicon.model.ComponentElement;
import org.seasar.kijimuna.core.internal.dicon.model.ContainerElement;
import org.seasar.kijimuna.core.internal.dicon.model.InitMethodElement;
import org.seasar.kijimuna.core.internal.dicon.model.PropertyElement;
import org.seasar.kijimuna.core.internal.dicon.model.autoregister.AutoRegisterFactory;
import org.seasar.kijimuna.core.internal.dicon.model.autoregister.ComponentAutoRegister;
import org.seasar.kijimuna.core.internal.dicon.model.autoregister.IAutoRegister;
import org.seasar.kijimuna.core.internal.dicon.model.autoregister.JarComponentAutoRegister;
import org.seasar.kijimuna.core.internal.parser.DefaultParseResult;
import org.seasar.kijimuna.core.util.MarkerUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DocumentHandler extends DefaultHandler implements ConstCore {

	private IProject project;
	private IStorage storage;
	private IProgressMonitor monitor;
	private Stack stack;
	private Locator locator;
	private Map dtdMap;
	private ElementFactory factory;
	private IElement result;
	private String markerType;
	private int errorSeverity;
	private int warningSeverity;
	private String publicId;

	public DocumentHandler(ElementFactory factory) {
		this(factory, null, MARKER_SEVERITY_IGNORE, MARKER_SEVERITY_IGNORE);
	}

	public DocumentHandler(ElementFactory factory, String markerType, int errorSeverity,
			int warningSeverity) {
		stack = new Stack();
		setDocumentLocator(locator);
		dtdMap = new HashMap();
		this.factory = factory;
		this.markerType = markerType;
		this.errorSeverity = errorSeverity;
		this.warningSeverity = warningSeverity;
	}

	public void setStorage(IProject project, IStorage storage) {
		this.project = project;
		this.storage = storage;
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void putDtdPath(String publicId, String path) {
		dtdMap.put(publicId, path);
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException {
		if (publicId != null) {
			String dtdPath = (String) dtdMap.get(publicId);
			if (dtdPath != null) {
				try {
					InputSource source = new InputSource(KijimunaCore.getEntry(dtdPath)
							.openStream());
					this.publicId = publicId;
					return source;
				} catch (IOException ignore) {
				}
			}
		}
		return null;
	}

	public void startDocument() throws SAXException {
		if (factory == null) {
			factory = new ElementFactory();
		}
	}

	public void startElement(String namespaceURI, String localName, String qName,
			Attributes attributes) {
		if (monitor != null) {
			monitor.worked(1);
		}
		int depth = stack.size() + 1;
		Map property = new HashMap();
		for (int i = 0; i < attributes.getLength(); i++) {
			property.put(attributes.getQName(i), attributes.getValue(i));
		}
		IElement element = factory.createElement(project, storage, qName);
		element.setStartLocation(depth, locator.getLineNumber(), locator
				.getColumnNumber());
		element.setAttributes(property);
		if (depth == 1) {
			result = element;
			element.setRootElement(element);
		} else {
			element.setRootElement(result);
			IElement parent = (IElement) stack.peek();
			element.setParent(parent);
		}
		stack.push(element);
	}

	public void characters(char[] buffer, int start, int length) {
		if (monitor != null) {
			monitor.worked(1);
		}
		IElement element = (IElement) stack.peek();
		StringBuffer body = new StringBuffer();
		String old = element.getBody();
		if (old != null) {
			body.append(old);
		}
		body.append(new String(buffer, start, length));
		element.setBody(body.toString());
	}

	public void endElement(String namespaceURI, String localName, String qName) {
		if (monitor != null) {
			monitor.worked(1);
		}
		IElement element = (IElement) stack.pop();
		element.setEndLocation(locator.getLineNumber(), locator.getColumnNumber());
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		KijimunaCore.reportException(exception);
		error(exception);
	}

	public void error(SAXParseException exception) throws SAXException {
		if ((storage != null) && (storage instanceof IFile) && (markerType != null)) {
			MarkerUtils.createMarker(markerType, MARKER_SEVERITY_XML_ERROR,
					errorSeverity, (IFile) storage, exception.getLineNumber(), "[XML]"
							+ exception.getMessage());
		}
	}

	public void warning(SAXParseException exception) throws SAXException {
		if ((storage != null) && (storage instanceof IFile) && (markerType != null)) {
			MarkerUtils.createMarker(markerType, MARKER_SEVERITY_XML_WARNING,
					warningSeverity, (IFile) storage, exception.getLineNumber(), "[XML]"
							+ exception.getMessage());
		}
	}

	public IParseResult getResult() {
		IElement lastStack = null;
		if (!stack.isEmpty()) {
			lastStack = (IElement) stack.peek();
		}

		if (result instanceof ContainerElement) {
			ContainerElement containerElement = (ContainerElement) result;
			registerAutoComponent(containerElement);
		}

		return new DefaultParseResult(publicId, result, lastStack);
	}

	private void registerAutoComponent(ContainerElement containerElement) {
		List componentList = containerElement.getComponentList();
		for (Iterator componentListIterator = componentList.iterator(); componentListIterator
				.hasNext();) {
			ComponentElement componentElement = (ComponentElement) componentListIterator
					.next();
			String autoRegisterClassName = componentElement.getComponentClassName();
			if (AutoRegisterFactory.isAutoRegister(autoRegisterClassName)) {
				doAutoRegister(componentElement, autoRegisterClassName);
			}
		}
	}

	private void doAutoRegister(ComponentElement componentElement,
			String autoRegisterClassName) {
		IAutoRegister register = AutoRegisterFactory
				.getAutoRegister(autoRegisterClassName);
		register.setProject(JavaCore.create(project));
		List propertyList = componentElement.getPropertyList();
		processProperty(register, propertyList);
		List initMethodList = componentElement.getInitMethodList();
		processInitMethod(register, initMethodList);
		register.registerAll();
		Map componentMap = register.getComponentMap();
		for (Iterator it = componentMap.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			((ContainerElement) componentElement.getRootElement())
					.addAutoRegisterComponent((String) entry.getKey(), (String) entry
							.getValue(), componentElement.getDepth(), componentElement
							.getStartLine(), componentElement.getStartColumn());
		}
	}

	private void processProperty(IAutoRegister register, List propertyList) {
		for (Iterator propertyListIterator = propertyList.iterator(); propertyListIterator
				.hasNext();) {
			PropertyElement propertyElement = (PropertyElement) propertyListIterator
					.next();
			if (propertyElement.getAttribute("name").equals("referenceClass")
					&& register instanceof JarComponentAutoRegister) {
				setReferenceClass(register, propertyElement);
			}

			if (propertyElement.getAttribute("name").equals("jarFileNames")
					&& register instanceof JarComponentAutoRegister) {
				setJarFileNames(register, propertyElement);
			}
		}
	}

	private void setReferenceClass(IAutoRegister register, PropertyElement propertyElement) {
		String referenceClass = propertyElement.getBody();
		if (referenceClass != null) {
			if (referenceClass.startsWith("@") && referenceClass.endsWith("@class")) {
				String className = referenceClass.substring(1,
						referenceClass.length() - 6);
				((JarComponentAutoRegister) register).setReferenceClass(className);
			}
		}
	}

	private void processInitMethod(IAutoRegister register, List initMethodList) {
		for (Iterator initMethodListIterator = initMethodList.iterator(); initMethodListIterator
				.hasNext();) {
			InitMethodElement initMethodElement = (InitMethodElement) initMethodListIterator
					.next();
			if (initMethodElement.getAttribute("name").equals("addClassPattern")) {
				addClassPattern(register, initMethodElement);
			} else if (initMethodElement.getAttribute("name").equals(
					"addIgnoreClassPattern")) {
				addIgnoreClassPattern(register, initMethodElement);
			} else if (initMethodElement.getAttribute("name").equals("addReferenceClass")
					&& register instanceof ComponentAutoRegister) {
				addReferenceClass(register, initMethodElement);
			}
		}
	}

	private void setJarFileNames(IAutoRegister register, PropertyElement propertyElement) {
		String jarFileNames = propertyElement.getBody();
		if (jarFileNames != null && StringUtils.isString(jarFileNames)) {
			((JarComponentAutoRegister) register).setJarFileNames(StringUtils
					.decodeString(jarFileNames));
		}
	}

	private void addReferenceClass(IAutoRegister register,
			InitMethodElement initMethodElement) {
		List argList = initMethodElement.getArgList();
		Iterator argListIterator = argList.iterator();
		ArgElement argElement = null;
		argElement = (ArgElement) argListIterator.next();
		String referenceClass = argElement.getBody();
		if (referenceClass.startsWith("@") && referenceClass.endsWith("@class")) {
			String referenceClassName = referenceClass.substring(1, referenceClass
					.length() - 6);
			((ComponentAutoRegister) register).addReferenceClass(referenceClassName);
		}
	}

	private void addIgnoreClassPattern(IAutoRegister register,
			InitMethodElement initMethodElement) {
		addClassPattern(register, initMethodElement);
	}

	private void addClassPattern(IAutoRegister register,
			InitMethodElement initMethodElement) {
		List argList = initMethodElement.getArgList();
		Iterator argListIterator = argList.iterator();
		ArgElement argElement = null;
		argElement = (ArgElement) argListIterator.next();
		String packageName = argElement.getBody();
		argElement = (ArgElement) argListIterator.next();
		String shortClassNames = argElement.getBody();
		register.addClassPattern(StringUtils.decodeString(packageName), StringUtils
				.decodeString(shortClassNames));
	}

}
