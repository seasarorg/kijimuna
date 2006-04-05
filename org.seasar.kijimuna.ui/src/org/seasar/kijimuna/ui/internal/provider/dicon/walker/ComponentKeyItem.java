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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IHasJavaElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.ComponentKeyProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentKeyItem extends AbstractInternalContainer implements
		IHasJavaElement, ConstUI {

	private IComponentKey key;

	public ComponentKeyItem(ComponentKeyRoot parent, IComponentKey key) {
		super(parent);
		this.key = key;
	}

	protected IPropertySource createProperty() {
		return new ComponentKeyProperty(key);
	}

	public int getMarkerSeverity() {
		int tooMany = key.getTooMany();
		if (tooMany == IComponentKey.TOO_MANY_PROBLEM) {
			IProject project = getElement().getProject();
			return MarkerSetting.getDiconMarkerPreference(project,
					MARKER_CATEGORY_DICON_PROBLEM, false);
		} else if (tooMany == IComponentKey.TOO_MANY_FETAL) {
			IProject project = getElement().getProject();
			return MarkerSetting.getDiconMarkerPreference(project,
					MARKER_CATEGORY_DICON_FETAL, false);
		}
		return super.getMarkerSeverity();
	}

	public IJavaElement getJavaElement() {
		IRtti rtti = (IRtti) key.getAdapter(IRtti.class);
		if (rtti != null) {
			return rtti.getType();
		}
		return null;
	}

	public Object[] getChildren() {
		if (key.getKeyType() == IComponentKey.INTERFACE) {
			IRtti keyRtti = (IRtti) key.getAdapter(IRtti.class);
			return new Object[] {
				new InterfaceItem(this, null, keyRtti)
			};
		}
		return new Object[0];
	}

	public String getDisplayName() {
		return key.getDisplayName();
	}

	public String getImageName() {
		if (key.getKeyType() == IComponentKey.INTERFACE) {
			return IMAGE_ICON_KEY_INTERFACE;
		}
		return IMAGE_ICON_KEY_STRING;
	}

}
