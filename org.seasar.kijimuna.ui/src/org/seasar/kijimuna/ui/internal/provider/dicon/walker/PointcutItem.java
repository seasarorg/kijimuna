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
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.property.PointcutProperty;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PointcutItem
		extends AbstractInternalContainer implements ConstUI {

    private IPointcut pointcut;
    
    public PointcutItem(PointcutRoot parent, IPointcut pointcut) {
        super(parent);
        this.pointcut = pointcut;
    }

    protected IPropertySource createProperty() {
        return new PointcutProperty(pointcut);
    }

    
    public int getMarkerSeverity() {
        if(pointcut.hasError()) {
            IProject project = getElement().getProject();
            return MarkerSetting.getDiconMarkerPreference(
                    project, MARKER_CATEGORY_DICON_PROBLEM, false);
        }
        return MARKER_SEVERITY_NONE;
    }
    
    public Object[] getChildren() {
        if(pointcut.isAutoApply()) {
            IRtti instanceRtti = pointcut.getInstanceRtti();
            IRtti[] implementings = pointcut.getImplementings();
            Object[] ret = new Object[implementings.length];
            for(int i = 0; i < implementings.length; i++) {
                ret[i] = new InterfaceItem(this, instanceRtti, implementings[i]);
            }
            return ret;
        }
        IRttiMethodDesctiptor[] methods = pointcut.getApplyMethods();
        Object[] ret = new Object[methods.length];
        for(int i = 0; i < methods.length; i++) {
            ret[i] = new MethodItem(this, methods[i], true);
        }
        return ret;
    }
    
    public String getDisplayName() {
        return pointcut.getRegexp();
    }
    
    public String getImageName() {
        return IMAGE_ICON_ASPECT_REGEXP;
    }
    
}
