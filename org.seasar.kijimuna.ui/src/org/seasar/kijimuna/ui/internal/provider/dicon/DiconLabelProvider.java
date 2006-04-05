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
package org.seasar.kijimuna.ui.internal.provider.dicon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.image.OverlayImageDescriptor;
import org.seasar.kijimuna.ui.util.WidgetUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconLabelProvider extends LabelProvider implements ConstUI {

	private ImageRegistry registry = new ImageRegistry();

	private Image getImageFromRegistry(String name) {
		if (StringUtils.existValue(name)) {
			Image image = registry.get(name);
			if (image == null) {
				ImageDescriptor descriptor = WidgetUtils.getImageDescriptor(name);
				image = descriptor.createImage();
				registry.put(name, image);
			}
			return image;
		}
		return null;
	}

	private String getKey(String base, String left, String right) {
		return base + "&" + left + "&" + right;
	}

	private Image getOverlayImage(String imageName, IContentWalker walker) {
		String left = null;
		String right = null;
		if (walker instanceof IInternalContainer) {
			IInternalContainer internal = ((IInternalContainer) walker);
			int markerSeverity = internal.getMarkerSeverity();
			if (markerSeverity == MARKER_SEVERITY_ERROR) {
				left = IMAGE_DECORATOR_ERROR;
			} else if (markerSeverity == MARKER_SEVERITY_WARNING) {
				left = IMAGE_DECORATOR_WARNING;
			}
			if (internal.isOGNL()) {
				right = IMAGE_DECORATOR_OGNL;
			}
		}
		if (walker instanceof IInjectedComponent) {
			int status = ((IInjectedComponent) walker).getInjectedStatus();
			if (status == IInjectedComponent.INJECTED_AUTO) {
				right = IMAGE_DECORATOR_AUTO;
			} else {
				right = IMAGE_DECORATOR_AUTO_NULL;
			}
		}
		if (StringUtils.existValue(left) || StringUtils.existValue(right)) {
			String key = getKey(imageName, left, right);
			Image ret = registry.get(key);
			if (ret == null) {
				Image baseImage = getImageFromRegistry(imageName);
				Image leftImage = getImageFromRegistry(left);
				Image rightImage = getImageFromRegistry(right);
				ImageDescriptor descriptor = new OverlayImageDescriptor(baseImage,
						leftImage, rightImage);
				ret = descriptor.createImage();
				registry.put(key, ret);
			}
			return ret;
		}
		return getImageFromRegistry(imageName);
	}

	public Image getImage(Object element) {
		if (element instanceof IContentWalker) {
			String imageName = ((IContentWalker) element).getImageName();
			if (StringUtils.existValue(imageName)) {
				return getOverlayImage(imageName, (IContentWalker) element);
			}
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof IContentWalker) {
			return ((IContentWalker) element).getDisplayName();
		}
		return super.getText(element);
	}

}
