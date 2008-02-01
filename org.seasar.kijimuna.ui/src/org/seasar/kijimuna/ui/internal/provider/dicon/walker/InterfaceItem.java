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

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IHasJavaElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.InterfaceProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class InterfaceItem extends AbstractInternalContainer implements IHasJavaElement,
		ConstUI {

	private IRtti instanceRtti;
	private IRtti implemeniting;

	public InterfaceItem(IInternalContainer parent, IRtti instanceRtti,
			IRtti implemeniting) {
		super(parent);
		this.instanceRtti = instanceRtti;
		this.implemeniting = implemeniting;
	}

	protected IPropertySource createProperty() {
		return new InterfaceProperty(implemeniting);
	}

	public IJavaElement getJavaElement() {
		return implemeniting.getType();
	}

	public Object[] getChildren() {
		IRttiMethodDesctiptor[] desc;
		boolean needsSeverity;
		if (instanceRtti != null) {
			needsSeverity = true;
			desc = ModelUtils.getImplementMethods(instanceRtti, implemeniting);
		} else {
			needsSeverity = false;
			desc = implemeniting.getMethods(Pattern.compile(".*"));
		}
		Object[] ret = new Object[desc.length];
		for (int i = 0; i < desc.length; i++) {
			ret[i] = new MethodItem(this, desc[i], needsSeverity);
		}
		return ret;
	}

	public int getMarkerSeverity() {
		if (implemeniting instanceof HasErrorRtti) {
			IProject project = getElement().getProject();
			IPreferenceStore pref = PreferencesUtil.getPreferenceStore(project);
			return pref.getInt(MARKER_SEVERITY_JAVA_FETAL);
		}
		return MARKER_SEVERITY_NONE;
	}

	public String getDisplayName() {
		return implemeniting.getQualifiedName();
	}

	public String getImageName() {
		if (implemeniting.isInterface()) {
			return IMAGE_ICON_JAVA_INTERFACE;
		} else {
			return IMAGE_ICON_JAVA_CLASS;
		}
	}

}
