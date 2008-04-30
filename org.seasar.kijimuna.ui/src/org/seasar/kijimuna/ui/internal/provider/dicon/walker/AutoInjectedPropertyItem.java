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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.AutoInjectedPropertyProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedPropertyItem extends AbstractInternalContainer implements
		IInjectedComponent, ConstUI {

	private IRttiPropertyDescriptor propDesc;
	private IPropertyElement prop;
	private String propName;
	private IRtti propValue;

	public AutoInjectedPropertyItem(ContentItem parent, IRttiPropertyDescriptor propDesc) {
		super(parent);
		this.propDesc = propDesc;
		this.propName = propDesc.getName();
		this.propValue = propDesc.getValue();
	}

	public AutoInjectedPropertyItem(ContentItem parent, IPropertyElement prop) {
		super(parent);
		this.prop = prop;
		this.propName = prop.getPropertyName();
		this.propValue = (IRtti) prop.getAdapter(IRtti.class);
	}

	protected IPropertySource createProperty() {
		return propDesc != null ? new AutoInjectedPropertyProperty(propDesc)
				: new AutoInjectedPropertyProperty(prop);
	}

	public int getMarkerSeverity() {
		IProject project = getElement().getProject();
		IPreferenceStore store = PreferencesUtil.getPreferenceStore(project);

		IRtti arg = propValue;
		if (arg instanceof ITooManyRegisted) {
			return store.getInt(MARKER_SEVERITY_DICON_FETAL);
		} else if (arg instanceof IComponentNotFound) {
			if (prop != null) {
				if (DICON_VAL_BINDING_TYPE_MAY.equals(prop.getBindingType())) {
					return MARKER_SEVERITY_NONE;
				} else if (DICON_VAL_BINDING_TYPE_MUST.equals(prop.getBindingType())) {
					return store.getInt(MARKER_SEVERITY_DICON_FETAL);
				}
			}
			return store.getInt(MARKER_SEVERITY_NULL_INJECTION);
		} else {
			return MARKER_SEVERITY_NONE;
		}
	}

	public int getInjectedStatus() {
		IRtti arg = propValue;
		if (arg instanceof ITooManyRegisted) {
			return IInjectedComponent.INJECTED_AUTO_TOOMANY;
		} else if (arg instanceof IComponentNotFound) {
			return IInjectedComponent.INJECTED_AUTO_NULL;
		} else {
			return IInjectedComponent.INJECTED_AUTO;
		}
	}

	public IDiconElement getInjectedElement() {
		IRtti arg = propValue;
		if (arg != null) {
			return (IDiconElement) arg.getAdapter(IComponentElement.class);
		}
		return null;
	}

	public String getDisplayName() {
		return KijimunaUI.getResourceString(
				"dicon.provider.walker.AutoInjectedArgItem.1", new Object[] {
					propName
				});
	}

	public String getImageName() {
		return IMAGE_ICON_PROPERTY;
	}

	public IDiconElement getElement() {
		return prop != null ? prop : super.getElement();
	}

}
