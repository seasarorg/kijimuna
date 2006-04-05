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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.views.properties.IPropertySource;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.IHasJavaElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.MethodProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MethodItem extends AbstractInternalContainer
		implements IHasJavaElement, ConstUI {

    private IRttiMethodDesctiptor method;
    private boolean needsSeverity;
    
    public MethodItem(IInternalContainer parent, 
            IRttiMethodDesctiptor method, boolean needsSeverity) {
        super(parent);
        this.method = method;
        this.needsSeverity = needsSeverity;
    }

    protected IPropertySource createProperty() {
        return new MethodProperty(method);
    }

    public int getMarkerSeverity() {
        if(needsSeverity) {
            if(method.isFinal() || method.isStatic()) {
                return MarkerSetting.getDiconMarkerPreference(
                        getElement().getProject(),
                        MARKER_CATEGORY_DICON_PROBLEM, false);
            }
        }
        return super.getMarkerSeverity();
    }

    public IJavaElement getJavaElement() {
        return method.getMember();
    }
    
    public String getDisplayName() {
        return ModelUtils.getMethodDisplay(method, false);
    }
    
    public String getImageName() {
        return IMAGE_ICON_JAVA_METHOD;
    }
    
}
