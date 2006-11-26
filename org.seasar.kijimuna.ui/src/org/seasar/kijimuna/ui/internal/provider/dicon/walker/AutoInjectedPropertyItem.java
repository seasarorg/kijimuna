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
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IExpressionElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.AutoInjectedPropertyProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedPropertyItem extends AbstractInternalContainer
		implements IInjectedComponent {

	private IPropertyModel propModel;
	
	public AutoInjectedPropertyItem(IPropertyModel propModel, ContentItem parent) {
		super(parent);
		this.propModel = propModel;
	}
	
	public AutoInjectedPropertyItem(IPropertyModel propModel, IPropertyElement prop,
			ContentItem parent) {
		super(prop, parent);
		this.propModel = propModel;
	}
	
	public String getDisplayName() {
		if (propModel.wasDoneAutoBinding()) {
			StringBuffer buf = new StringBuffer();
			if (propModel.getPropertyName() != null &&
					!getPropDesc().getName().equals(propModel.getPropertyName())) {
				buf.append("<").append(propModel.getPropertyName()).append(">");
			}
			return KijimunaUI.getResourceString(
					"dicon.provider.walker.AutoInjectedArgItem.1",
					new Object[] {
							buf.insert(0, getPropDesc().getName()).toString()
					});
		}
		return propModel.getPropertyName();
	}

	public String getImageName() {
		return IMAGE_ICON_PROPERTY;
	}
	
	public int getMarkerSeverity() {
		IRtti propValue = getPropDesc().getValue();
		if (propValue instanceof ITooManyRegisted) {
			return MarkerSetting.getDiconMarkerPreference(getProject(),
					MARKER_CATEGORY_DICON_FETAL, false);
		}
		if (propValue instanceof IComponentNotFound) {
			String bt = propModel.getBindingType();
			if (propModel.isAutoBindingType()) {
				if (DICON_VAL_BINDING_TYPE_MAY.equals(bt) ||
						DICON_VAL_BINDING_TYPE_NONE.equals(bt)) {
					return MARKER_SEVERITY_NONE;
				} else if (DICON_VAL_BINDING_TYPE_SHOULD.equals(bt) ||
						DICON_VAL_BINDING_TYPE_NONE.equals(bt)) {
					return MarkerSetting.getDiconMarkerPreference(getProject(),
							MARKER_CATEGORY_NULL_INJECTION, false);
				} else  {
					return MarkerSetting.getDiconMarkerPreference(getProject(),
							MARKER_CATEGORY_DICON_FETAL, false);
				}
			} else {
				// mustのときだけエラーでそれ以外は何もしない（S2.3.x）
				if (DICON_VAL_BINDING_TYPE_MUST.equals(bt)) {
					return MarkerSetting.getDiconMarkerPreference(getProject(),
							MARKER_CATEGORY_DICON_FETAL, false);
				}
			}
		}
		return MARKER_SEVERITY_NONE;
	}

	public int getInjectedStatus() {
		if (getPropDesc().getValue() instanceof ITooManyRegisted) {
			return IInjectedComponent.INJECTED_AUTO_TOOMANY;
		} else if (propModel.wasDoneAutoBinding()) {
			return IInjectedComponent.INJECTED_AUTO;
		}
		return INJECTED_AUTO_NULL;
	}

	public IDiconElement getInjectedElement() {
		IRtti arg = getPropDesc().getValue();
		if (arg != null) {
			return (IDiconElement) arg.getAdapter(IComponentElement.class);
		}
		return null;
	}
	
	public boolean isOGNL() {
		IDiconElement element = getElement();
		return element instanceof IExpressionElement ? ((IExpressionElement)
				element).isOGNL() : false;
	}

	protected IPropertySource createProperty() {
		return new AutoInjectedPropertyProperty(getPropDesc());
	}

	private IProject getProject() {
		return getPropDesc().getParent().getRttiLoader()
				.getProject().getProject();
	}
	
	private IRttiPropertyDescriptor getPropDesc() {
		return (IRttiPropertyDescriptor) propModel.getAdapter(
				IRttiPropertyDescriptor.class);
	}

}
