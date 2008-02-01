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
package org.seasar.kijimuna.ui.internal.preference.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class DesignPane extends Composite {

	private TabFolder folder;
	
	public DesignPane(Composite parent, int style) {
		super(parent, style);
		GridLayout g = new GridLayout(1, true);
		g.marginHeight = 0;
		g.marginWidth = 0;
		setLayout(g);
		setFont(parent.getFont());
	}
	
	public TabFolder getTabFolder() {
		if (folder == null) {
			GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			folder = new TabFolder(this, SWT.NULL);
			folder.setLayoutData(gd);
			folder.setFont(getParent().getFont());
		}
		return folder;
	}

}
