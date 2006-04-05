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
package org.seasar.kijimuna.core.internal.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;

import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultElement implements IElement, IAdaptable, Serializable {

	private IElement root;

	private transient IProject project;
	private String projectName;
	private transient IStorage storage;
	private String fullPath;
	private String elementName;
	private int depth;
	private int startLine;
	private int startColumn;
	private Map attributes;

	private IElement parent;
	private List children;

	private int endLine;
	private int endColumn;
	private String body;

	public DefaultElement(IProject project, IStorage storage, String elementName) {
		this.elementName = elementName;
		this.project = project;
		this.storage = storage;
		projectName = project.getName();
		fullPath = ProjectUtils.getPathString(storage);
		children = new ArrayList();
	}

	public IElement getRootElement() {
		return root;
	}

	public void setRootElement(IElement root) {
		this.root = root;
	}

	public String getElementName() {
		return elementName;
	}

	public int getDepth() {
		return depth;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartLocation(int depth, int startLine, int startColumn) {
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.depth = depth;
	}

	public String[] getAttributeNames() {
		return (String[]) attributes.keySet().toArray(new String[attributes.size()]);
	}

	public String getAttribute(String name) {
		return (String) attributes.get(name);
	}

	public void setAttributes(Map properties) {
		this.attributes = properties;
	}

	public IElement getParent() {
		return parent;
	}

	public void setParent(IElement parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public int getEndLine() {
		return endLine;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setEndLocation(int endLine, int endColumn) {
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	public List getChildren() {
		return children;
	}

	public List getChildren(String elementName) {
		List list = new ArrayList();
		for (Iterator it = children.iterator(); it.hasNext();) {
			IElement child = (IElement) it.next();
			if (child.getElementName().equals(elementName)) {
				list.add(child);
			}
		}
		return list;
	}

	public void addChild(IElement child) {
		children.add(child);
	}

	public IProject getProject() {
		if ((project == null) && StringUtils.existValue(projectName)) {
			project = ProjectUtils.getProject(projectName);
		}
		return project;
	}

	public IStorage getStorage() {
		if ((storage == null) && StringUtils.existValue(fullPath)) {
			storage = ProjectUtils.getStorage(getProject(), fullPath);
		}
		return storage;
	}

	public Object getAdapter(Class adapter) {
		if (IProject.class.equals(adapter)) {
			return getProject();
		} else if (IStorage.class.equals(adapter)) {
			return getStorage();
		} else if (IElement.class.equals(adapter)) {
			return this;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('<').append(getElementName());
		String[] attributes = getAttributeNames();
		for (int i = 0; i < attributes.length; i++) {
			buf.append(' ').append(attributes[i]).append('=');
			buf.append('"').append(getAttribute(attributes[i])).append('"');
		}
		buf.append('>');
		return buf.toString();
	}

}
