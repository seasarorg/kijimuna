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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.internal.parser.DefaultElement;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.rtti.RttiLoader;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public abstract class DiconElement extends DefaultElement implements IDiconElement,
		ConstCore {

	private int markerSeverity = MARKER_SEVERITY_NONE;

	public DiconElement(IProject project, IStorage storage, String elementName) {
		super(project, storage, elementName);
	}

	public IContainerElement getContainerElement() {
		IElement root = getRootElement();
		if ((root != null) && (root instanceof IContainerElement)) {
			return (IContainerElement) root;
		}
		return null;
	}

	public RttiLoader getRttiLoader() {
		DiconNature nature = getNature();
		if (nature != null) {
			return nature.getRttiLoader();
		}
		return null;
	}

	public DiconNature getNature() {
		IProject project = (IProject) getAdapter(IProject.class);
		if (project != null) {
			return DiconNature.getInstance(project);
		}
		return null;
	}

	public void setMarkerServerity(int markerSeverity) {
		if (this.markerSeverity < markerSeverity) {
			this.markerSeverity = markerSeverity;
		}
	}

	public int getMarkerSeverity() {
		return markerSeverity;
	}

	public abstract String getDisplayName();

}
