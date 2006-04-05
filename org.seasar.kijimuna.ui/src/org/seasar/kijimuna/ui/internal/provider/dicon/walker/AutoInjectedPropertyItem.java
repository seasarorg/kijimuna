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
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.AutoInjectedPropertyProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class AutoInjectedPropertyItem extends AbstractInternalContainer
		implements IInjectedComponent, ConstUI {

    private IRttiPropertyDescriptor property;
    
    public AutoInjectedPropertyItem(ContentItem parent, IRttiPropertyDescriptor property) {
        super(parent);
        this.property = property;
    }
    
    protected IPropertySource createProperty() {
        return new AutoInjectedPropertyProperty(property);
    }

	public int getMarkerSeverity() {
        IRtti arg = property.getValue();
        if (arg instanceof ITooManyRegisted) {
            IProject project = getElement().getProject();
            return MarkerSetting.getDiconMarkerPreference(
                    project, MARKER_CATEGORY_DICON_FETAL, false); 
        } else if (arg instanceof IComponentNotFound) {
            IProject project = getElement().getProject();
            return MarkerSetting.getDiconMarkerPreference(
                    project, MARKER_CATEGORY_NULL_INJECTION, false); 
        } else {
            return MARKER_SEVERITY_NONE;
        }
	}
    
    public int getInjectedStatus() {
        IRtti arg = property.getValue();
        if (arg instanceof ITooManyRegisted) {
            return IInjectedComponent.INJECTED_AUTO_TOOMANY; 
        } else if(arg instanceof IComponentNotFound) {
            return IInjectedComponent.INJECTED_AUTO_NULL;
        } else {
            return IInjectedComponent.INJECTED_AUTO;
        }
    }
    
    public IDiconElement getInjectedElement() {
        IRtti arg = property.getValue();
        if(arg != null) {
            return (IDiconElement)arg.getAdapter(IComponentElement.class);
        }
        return null;
    }
    
    public String getDisplayName() {
        return KijimunaUI.getResourceString(
                "dicon.provider.walker.AutoInjectedArgItem.1",
                new Object[] { property.getName() }); 
    }
    
    public String getImageName() {
        return IMAGE_ICON_PROPERTY;
    }
    
}
