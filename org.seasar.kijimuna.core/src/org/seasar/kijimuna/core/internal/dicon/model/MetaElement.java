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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IMetaElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MetaElement extends ComponentHolderElement implements IMetaElement,
		ConstCore {

	public MetaElement(IProject project, IStorage storage) {
		super(project, storage, DICON_TAG_META);
	}

	public String getMetaName() {
		return getAttribute(DICON_ATTR_NAME);
	}

	public String getDisplayName() {
		StringBuffer buffer = new StringBuffer();
		String metaName = getMetaName();
		if (StringUtils.existValue(metaName)) {
			buffer.append(metaName);
		}
		String superDisplay = super.getDisplayName();
		if (StringUtils.existValue(superDisplay)) {
			if (StringUtils.existValue(metaName)) {
				buffer.append(" ");
			}
			buffer.append(superDisplay);
		}
		return buffer.toString();
	}

	protected IRtti getNonExpressionValue() {
		List children = getChildren();
		int size = children.size();
		if (size != 0) {
			Object element = children.get(size - 1);
			if (element instanceof IComponentElement) {
				IComponentElement component = (IComponentElement) element;
				return (IRtti) component.getAdapter(IRtti.class);
			}
		}
		return null;
	}

}
