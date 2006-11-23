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

import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.info.IComponentNotFound;
import org.seasar.kijimuna.core.dicon.info.ITooManyRegisted;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.AutoInjectedPropertyProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedPropertyItem extends AbstractInternalContainer implements
		IInjectedComponent, ConstUI {

	private IRttiPropertyDescriptor property;
	private IPropertyStrategy strategy;

	public AutoInjectedPropertyItem(ContentItem parent, IRttiPropertyDescriptor property) {
		super(parent);
		this.property = property;
		this.strategy = new WithoutPropertyElementStrategy();
	}
	
	public AutoInjectedPropertyItem(IPropertyElement prop, IRttiPropertyDescriptor propDesc,
			IContentWalker walker) {
		super(prop, walker);
		this.property = propDesc;
		this.strategy = new WithPropertyElementStrategy();
	}
	
	protected IPropertySource createProperty() {
		return new AutoInjectedPropertyProperty(property);
	}

	public int getMarkerSeverity() {
		return strategy.getMarkerSeverity();
//		IRtti arg = property.getValue();
//		if (arg instanceof ITooManyRegisted) {
//			IProject project = getElement().getProject();
//			return MarkerSetting.getDiconMarkerPreference(project,
//					MARKER_CATEGORY_DICON_FETAL, false);
//		} else if (arg instanceof IComponentNotFound) {
//			IProject project = getElement().getProject();
//			IBindingAnnotation annotation = (IBindingAnnotation) property
//					.getAdapter(IBindingAnnotation.class);
//			if (annotation != null) {
//				switch (annotation.getBindingType()) {
//				case IBindingAnnotation.BINDING_TYPE_MAY:
//					return MARKER_SEVERITY_NONE;
//				case IBindingAnnotation.BINDING_TYPE_MUST:
//				case IBindingAnnotation.BINDING_TYPE_UNKNOWN:
//					return MarkerSetting.getDiconMarkerPreference(project,
//							MARKER_CATEGORY_DICON_FETAL, false);
//				}
//			}
//			return MarkerSetting.getDiconMarkerPreference(project,
//					MARKER_CATEGORY_NULL_INJECTION, false);
//		}
//		return MARKER_SEVERITY_NONE;
	}

	public int getInjectedStatus() {
		IRtti arg = property.getValue();
		if (arg instanceof ITooManyRegisted) {
			return IInjectedComponent.INJECTED_AUTO_TOOMANY;
		} else if (arg instanceof IComponentNotFound) {
			return IInjectedComponent.INJECTED_AUTO_NULL;
		} else {
			return IInjectedComponent.INJECTED_AUTO;
		}
	}

	public IDiconElement getInjectedElement() {
		IRtti arg = property.getValue();
		if (arg != null) {
			return (IDiconElement) arg.getAdapter(IComponentElement.class);
		}
		return null;
	}

	public String getDisplayName() {
		IBindingAnnotation ba = (IBindingAnnotation) property.getAdapter(
				IBindingAnnotation.class);
		StringBuffer buf = new StringBuffer();
		if (ba != null && ba.getPropertyName() != null) {
			buf.append("<").append(ba.getPropertyName()).append(">");
		}
		return KijimunaUI.getResourceString(
				"dicon.provider.walker.AutoInjectedArgItem.1", new Object[] {
					buf.insert(0, property.getName()).toString()
				});
	}

	public String getImageName() {
		return IMAGE_ICON_PROPERTY;
	}
	
	
	private interface IPropertyStrategy {
		int getMarkerSeverity();
	}
	
	private class WithPropertyElementStrategy implements IPropertyStrategy {
		
		public int getMarkerSeverity() {
			IRtti arg = property.getValue();
			if (arg instanceof ITooManyRegisted) {
				IProject project = getElement().getProject();
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_DICON_FETAL, false);
			} else if (arg instanceof IComponentNotFound) {
				IProject project = getElement().getProject();
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_NULL_INJECTION, false);
			}
			return MARKER_SEVERITY_NONE;
		}
	}
	
	private class WithoutPropertyElementStrategy implements IPropertyStrategy {
		
		public int getMarkerSeverity() {
			IRtti arg = property.getValue();
			if (arg instanceof ITooManyRegisted) {
				IProject project = getElement().getProject();
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_DICON_FETAL, false);
			} else if (arg instanceof IComponentNotFound) {
				IProject project = getElement().getProject();
				IBindingAnnotation annotation = (IBindingAnnotation) property
						.getAdapter(IBindingAnnotation.class);
				if (annotation != null) {
					switch (annotation.getBindingType()) {
					case IBindingAnnotation.BINDING_TYPE_MAY:
						return MARKER_SEVERITY_NONE;
					case IBindingAnnotation.BINDING_TYPE_MUST:
					case IBindingAnnotation.BINDING_TYPE_UNKNOWN:
						return MarkerSetting.getDiconMarkerPreference(project,
								MARKER_CATEGORY_DICON_FETAL, false);
					}
				}
				return MarkerSetting.getDiconMarkerPreference(project,
						MARKER_CATEGORY_NULL_INJECTION, false);
			}
			return MARKER_SEVERITY_NONE;
		}
	}

}
