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
package org.seasar.kijimuna.core.internal.dicon.validation;

import java.util.Iterator;
import java.util.Stack;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.IValidation;
import org.seasar.kijimuna.core.dicon.MarkerSetting;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.core.util.StringUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class IncludeValidation  implements IValidation, ConstCore {

    public void validation(IDiconElement element) {
        if(element instanceof IIncludeElement) {
            validInclude((IIncludeElement)element);
        }
    }
    
    private void validInclude(IIncludeElement include) {
        String path = include.getPath();
        if(StringUtils.noneValue(path)) {
            MarkerSetting.createDiconMarker(
                    "dicon.validation.IncludeValidation.1",
                    include);
            return;
        }
        IContainerElement containerElement = include.getChildContainer();
        if(containerElement == null) {
            MarkerSetting.createDiconMarker(
                    "dicon.validation.IncludeValidation.2",
                    include, new Object[]{ path });
            return;
        }
        if (isRoopInclude(containerElement, new Stack())) {
            MarkerSetting.createDiconMarker(
                    "dicon.validation.IncludeValidation.3",
                    include, new Object[]{ path });
            return;
        }
    }

    private boolean isRoopInclude(IContainerElement containerElement, Stack stack) {
		for (Iterator it = containerElement.getIncludeList().iterator(); it.hasNext();) {
			IIncludeElement incl = (IIncludeElement) it.next();
			IContainerElement childContainer = incl.getChildContainer();
			if (childContainer == null) {
				return false;
			} else if (stack.contains(childContainer)) {
				return true;
			} else {
				stack.push(childContainer);
				boolean isRoop = this.isRoopInclude(childContainer, stack);
				stack.pop();
				if (isRoop) {
					return isRoop;
				}
			}
		}
		return false;
    }
}
