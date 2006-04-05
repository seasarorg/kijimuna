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
package org.seasar.kijimuna.ui.image;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class OverlayImageDescriptor extends CompositeImageDescriptor {

	private ImageData sourceData;
	private int sourceWidth;
	private int sourceHeight;
	private ImageData leftData;
	private int leftHeight;
	private ImageData rightData;
	private int rightWidth;
	private int rightHeight;

	public OverlayImageDescriptor(Image source, Image leftOverlay, Image rightOverlay) {
		if (source != null) {
			sourceData = source.getImageData();
			sourceWidth = source.getBounds().width;
			sourceHeight = source.getBounds().height;
			if (leftOverlay != null) {
				leftData = leftOverlay.getImageData();
				leftHeight = leftOverlay.getBounds().height;
			}
			if (rightOverlay != null) {
				rightData = rightOverlay.getImageData();
				rightWidth = rightOverlay.getBounds().width;
				rightHeight = rightOverlay.getBounds().height;
			}
		}
	}

	protected void drawCompositeImage(int width, int height) {
		if (sourceData != null) {
			drawImage(sourceData, 0, 0);
			if (leftData != null) {
				drawImage(leftData, 0, sourceHeight - leftHeight);
			}
			if (rightData != null) {
				drawImage(rightData, sourceWidth - rightWidth, sourceHeight - rightHeight);
			}
		}
	}

	protected Point getSize() {
		return new Point(sourceWidth, sourceHeight);
	}

}
