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
package org.seasar.kijimuna.core.dicon;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.internal.dicon.model.ArgElement;
import org.seasar.kijimuna.core.internal.dicon.model.AspectElement;
import org.seasar.kijimuna.core.internal.dicon.model.ComponentElement;
import org.seasar.kijimuna.core.internal.dicon.model.ContainerElement;
import org.seasar.kijimuna.core.internal.dicon.model.DescriptionElement;
import org.seasar.kijimuna.core.internal.dicon.model.DestroyMethodElement;
import org.seasar.kijimuna.core.internal.dicon.model.IncludeElement;
import org.seasar.kijimuna.core.internal.dicon.model.InitMethodElement;
import org.seasar.kijimuna.core.internal.dicon.model.MetaElement;
import org.seasar.kijimuna.core.internal.dicon.model.PropertyElement;
import org.seasar.kijimuna.core.parser.ElementFactory;
import org.seasar.kijimuna.core.parser.IElement;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconElementFactory extends ElementFactory implements ConstCore {

	public IElement createElement(IProject project, IStorage storage, String elementName) {
		IElement element = null;
		if (DICON_TAG_ARG.equals(elementName)) {
			element = new ArgElement(project, storage);
		} else if (DICON_TAG_ASPECT.equals(elementName)) {
			element = new AspectElement(project, storage);
		} else if (DICON_TAG_COMPONENT.equals(elementName)) {
			element = new ComponentElement(project, storage);
		} else if (DICON_TAG_CONTAINER.equals(elementName)) {
			element = new ContainerElement(project, storage);
		} else if (DICON_TAG_DESCRIPTION.equals(elementName)) {
			element = new DescriptionElement(project, storage);
		} else if (DICON_TAG_DESTROYMETHOD.equals(elementName)) {
			element = new DestroyMethodElement(project, storage);
		} else if (DICON_TAG_INCLUDE.equals(elementName)) {
			element = new IncludeElement(project, storage);
		} else if (DICON_TAG_INITMETHOD.equals(elementName)) {
			element = new InitMethodElement(project, storage);
		} else if (DICON_TAG_META.equals(elementName)) {
			element = new MetaElement(project, storage);
		} else if (DICON_TAG_PROPERTY.equals(elementName)) {
			element = new PropertyElement(project, storage);
		} else {
			element = super.createElement(project, storage, elementName);
		}
		return element;
	}

}
