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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface IElement extends IAdaptable {

	void addChild(IElement child);

	IElement getParent();

	void setRootElement(IElement root);

	void setParent(IElement parent);

	void setStartLocation(int depth, int startLine, int startColumn);

	int getStartLine();

	void setEndLocation(int endLine, int endColumn);

	int getEndLine();

	void setAttributes(Map properties);

	String getElementName();

	String getBody();

	void setBody(String body);

	String getAttribute(String name);

	List getChildren();

	IStorage getStorage();

	IProject getProject();

}
