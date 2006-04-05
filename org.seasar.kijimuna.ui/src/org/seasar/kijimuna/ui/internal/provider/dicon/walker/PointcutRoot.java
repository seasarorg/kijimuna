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
import java.util.List;

import org.seasar.kijimuna.core.dicon.info.IAspectInfo;
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class PointcutRoot extends AbstractInternalContainer implements ConstUI {

	private IAspectInfo info;

	public PointcutRoot(ContentItem parent, IAspectInfo info) {
		super(parent);
		this.info = info;
	}

	public Object[] getChildren() {
		IPointcut[] pointcuts = info.getPointcuts();
		List ret = new ArrayList();
		for (int i = 0; i < pointcuts.length; i++) {
			ret.add(new PointcutItem(this, pointcuts[i]));
		}
		return ret.toArray();
	}

	public String getDisplayName() {
		return KijimunaUI.getResourceString("dicon.provider.walker.PointcutRoot.1");
	}

	public String getImageName() {
		return IMAGE_ICON_ASPECT_POINTCUT;
	}

}
