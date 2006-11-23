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
package org.seasar.kijimuna.ui.internal.provider.dicon.walker;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentRoot;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ViewContent implements IContentRoot {

	private IProject project;

	public ViewContent(IProject project) {
		this.project = project;
	}

	public Object[] getTopLevelItems() {
		final DiconNature nature = DiconNature.getInstance(project);
		if (nature != null) {
			List rootContainers = nature.getModel().getRootContainers(null);
			if (rootContainers != null) {
				Collections.sort(rootContainers);
				ContentItem[] items = new ContentItem[rootContainers.size()];
				Iterator it = rootContainers.iterator();
				for (int i = 0; i < rootContainers.size(); i++) {
					items[i] = new ContentItem((IContainerElement) it.next(), null, false);
				}
				return items;
			}
		}
		return new Object[0];
	}

}
