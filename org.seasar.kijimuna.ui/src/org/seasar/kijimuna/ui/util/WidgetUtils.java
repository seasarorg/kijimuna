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
package org.seasar.kijimuna.ui.util;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class WidgetUtils implements ConstUI {

	public static ImageDescriptor getImageDescriptor(String name) {
		ImageDescriptor descriptor;
		URL url = KijimunaUI.getEntry(PATH_IMAGES + name);
		if (url != null) {
			descriptor = ImageDescriptor.createFromURL(url);
		} else {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		return descriptor;
	}
}
