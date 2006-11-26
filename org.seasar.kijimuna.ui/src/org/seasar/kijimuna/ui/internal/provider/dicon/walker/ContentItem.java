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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource;

import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.dicon.binding.IPropertyModel;
import org.seasar.kijimuna.core.dicon.info.IApplyMethodInfo;
import org.seasar.kijimuna.core.dicon.info.IAspectInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentInfo;
import org.seasar.kijimuna.core.dicon.info.IComponentKey;
import org.seasar.kijimuna.core.dicon.model.IArgElement;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDescriptionElement;
import org.seasar.kijimuna.core.dicon.model.IDestroyMethodElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IExpressionElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.core.dicon.model.IInitMethodElement;
import org.seasar.kijimuna.core.dicon.model.IMetaElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiConstructorDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.ContentProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ContentItem extends AbstractInternalContainer implements ConstUI {

	private boolean isOutline;

	public ContentItem(IDiconElement element, IContentWalker parent, boolean isOutline) {
		super(element, parent);
		this.isOutline = isOutline;
	}

	protected IPropertySource createProperty() {
		return new ContentProperty(getElement());
	}
	
	private Collection createPropertyItems() {
		if (!(getElement() instanceof IComponentElement)) {
			return Collections.EMPTY_LIST;
		}
		IComponentElement component = (IComponentElement) getElement();
		IPropertyModel[] propModels = (IPropertyModel[]) component.getAdapter(
				IPropertyModel[].class);
		List ret = new ArrayList(propModels.length);
		for (int i = 0; i < propModels.length; i++) {
			if (isDisplayable(component, propModels[i])) {
				ret.add(PropertyItemFactory.createPropertyItem(propModels[i],
						this));
			}
		}
		return ret;
	}
	
	private boolean isDisplayable(IComponentElement component, IPropertyModel
			propModel) {
		IBindingAnnotation ba = (IBindingAnnotation) propModel.getAdapter(
				IBindingAnnotation.class);
		String ab = component.getAutoBindingMode();
		if (DICON_VAL_AUTO_BINDING_AUTO.equals(ab) ||
				DICON_VAL_AUTO_BINDING_PROPERTY.equals(ab)) {
			return propModel.wasDoneAutoBinding() ||
			(ba != null && propModel.requiresAutoBinding()) ||
			(propModel.isAutoBindingType() && propModel.requiresAutoBinding());
		} else {
			return propModel.getAdapter(IPropertyElement.class) != null;
		}
	}
	
	public Object[] getChildren() {
		List children = getElement().getChildren();
		List ret = new ArrayList();
		IComponentInfo info = (IComponentInfo) getElement().getAdapter(
				IComponentInfo.class);
		if (info != null) {
			IComponentKey[] keys = info.getComponentKeys();
			if (keys.length > 0) {
				ret.add(new ComponentKeyRoot(this, keys));
			}
			IRttiConstructorDesctiptor constructor = info.getAutoInjectedConstructor();
			if (constructor != null) {
				IRtti values[] = constructor.getValues();
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						ret.add(new AutoInjectedArgItem(this, constructor, i));
					}
				}
			}
//			IRttiPropertyDescriptor[] properties = info.getAutoInjectedProperties();
//			for (int i = 0; i < properties.length; i++) {
//				ret.add(new AutoInjectedPropertyItem(this, properties[i]));
//				already.add(properties[i]);
//			}
		} else {
			IApplyMethodInfo methodInfo = (IApplyMethodInfo) getElement().getAdapter(
					IApplyMethodInfo.class);
			if (methodInfo != null) {
				IRttiMethodDesctiptor method = methodInfo.getAutoInjectedMethod();
				if (method != null) {
					IRtti values[] = method.getValues();
					for (int i = 0; i < values.length; i++) {
						ret.add(new AutoInjectedArgItem(this, method, i));
					}
				}
			} else {
				IAspectInfo aspectInfo = (IAspectInfo) getElement().getAdapter(
						IAspectInfo.class);
				if (aspectInfo != null) {
					ret.add(new PointcutRoot(this, aspectInfo));
				}
			}
		}
		for (Iterator it = children.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (isOutline) {
				if (obj instanceof IIncludeElement) {
					ret.add(new IncludeItem((IIncludeElement) obj, this));
				} else if (obj instanceof IPropertyElement) {
					// TODO 現在のところプロパティだけ回避
//					IRtti r = (IRtti) getElement().getAdapter(IRtti.class);
//					IRttiPropertyDescriptor prop = r.getProperty(
//							((IPropertyElement) obj).getPropertyName());
//					if (!already.contains(prop)) {
//						ret.add(new PropertyItem((IPropertyElement) obj, this));
//					}
				} else if (obj instanceof IDiconElement) {
					ret.add(new ContentItem((IDiconElement) obj, this, true));
				}
			} else {
				if (obj instanceof IComponentElement) {
					ret.add(new ContentItem((IComponentElement) obj, this, false));
				} else if (obj instanceof IIncludeElement) {
					IContainerElement container = ((IIncludeElement) obj)
							.getChildContainer();
					ret.add(new ContentItem(container, this, false));
				}
			}
		}
		ret.addAll(createPropertyItems());
		return ret.toArray();
	}

	public int getMarkerSeverity() {
		IDiconElement element = getElement();
		return element.getMarkerSeverity();
	}

	public boolean isOGNL() {
		IDiconElement element = getElement();
		if (element instanceof IExpressionElement) {
			return ((IExpressionElement) element).isOGNL();
		}
		return false;
	}

	public String getDisplayName() {
		return getElement().getDisplayName();
	}

	public String getImageName() {
		IDiconElement element = getElement();
		if (element instanceof IArgElement) {
			return IMAGE_ICON_ARG;
		} else if (element instanceof IAspectElement) {
			return IMAGE_ICON_ASPECT;
		} else if (element instanceof IComponentElement) {
			return IMAGE_ICON_COMPONENT;
		} else if (element instanceof IContainerElement) {
			return IMAGE_ICON_CONTAINER;
		} else if (element instanceof IDescriptionElement) {
			return IMAGE_ICON_DESCRIPTION;
		} else if (element instanceof IDestroyMethodElement) {
			return IMAGE_ICON_DESTROYMETHOD;
		} else if (element instanceof IInitMethodElement) {
			return IMAGE_ICON_INITMETHOD;
		} else if (element instanceof IMetaElement) {
			return IMAGE_ICON_META;
		} else if (element instanceof IPropertyElement) {
			return IMAGE_ICON_PROPERTY;
		}
		return null;
	}

	public boolean isDefaultExpandedState() {
		return true;
	}

}
