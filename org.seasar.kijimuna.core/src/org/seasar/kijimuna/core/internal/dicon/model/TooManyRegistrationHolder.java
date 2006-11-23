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
package org.seasar.kijimuna.core.internal.dicon.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.internal.dicon.info.TooManyRegistedRtti;
import org.seasar.kijimuna.core.rtti.IRtti;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TooManyRegistrationHolder extends ComponentElement {

	private IComponentKey componentKey;
	private Set components = new HashSet();

	public TooManyRegistrationHolder(IProject project, IStorage storage,
			IComponentKey componentKey) {
		super(project, storage);
		this.componentKey = componentKey;
	}

	public void addComponentElement(IDiconElement element) {
		components.add(element);
	}

	private IDiconElement[] getComponents() {
		return (IDiconElement[]) components.toArray(new IDiconElement[components.size()]);
	}

	public Object getAdapter(Class adapter) {
		if (IRtti.class.equals(adapter)) {
			return new TooManyRegistedRtti(componentKey, getComponents());
		}
		return super.getAdapter(adapter);
	}

}
