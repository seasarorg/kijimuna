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
import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiInvokableDesctiptor;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.AutoInjectedArgProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedArgItem extends AbstractInternalContainer implements
		IInjectedComponent, ConstUI {

	private IRttiInvokableDesctiptor method;
	private int index;

	public AutoInjectedArgItem(IInternalContainer parent,
			IRttiInvokableDesctiptor method, int index) {
		super(parent);
		this.method = method;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public IRttiInvokableDesctiptor getMethod() {
		return method;
	}

	protected IPropertySource createProperty() {
		return new AutoInjectedArgProperty(method, index);
	}

	public int getMarkerSeverity() {
		IRtti[] args = method.getValues();
		if (args != null) {
			if (args[index] instanceof ITooManyRegisted) {
				IProject project = getElement().getProject();
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_DICON_FETAL, false);
			} else if (args[index] instanceof IComponentNotFound) {
				IProject project = getElement().getProject();
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_NULL_INJECTION, false);
			}
		}
		return MARKER_SEVERITY_NONE;
	}

	public boolean isOGNL() {
		return false;
	}

	public int getInjectedStatus() {
		IRtti args[] = method.getValues();
		if (args != null) {
			if (args[index] instanceof ITooManyRegisted) {
				return IInjectedComponent.INJECTED_AUTO_TOOMANY;
			} else if (args[index] instanceof IComponentNotFound) {
				return IInjectedComponent.INJECTED_AUTO_NULL;
			}
		}
		return IInjectedComponent.INJECTED_AUTO;
	}

	public IDiconElement getInjectedElement() {
		IRtti[] args = method.getValues();
		if ((args != null) && (args[index] != null)) {
			return (IDiconElement) args[index].getAdapter(IComponentElement.class);
		}
		return null;
	}

	public String getDisplayName() {
		IRtti[] args = method.getArgs();
		return KijimunaUI.getResourceString(
				"dicon.provider.walker.AutoInjectedArgItem.1", new Object[] {
					args[index].getQualifiedName()
				});
	}

	public String getImageName() {
		return IMAGE_ICON_ARG;
	}

}
