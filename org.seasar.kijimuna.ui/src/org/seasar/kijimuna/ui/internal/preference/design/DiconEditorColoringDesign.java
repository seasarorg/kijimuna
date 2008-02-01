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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlConsts;

public class DiconEditorColoringDesign extends Composite implements XmlConsts {

	private List colorFieldEditors = new ArrayList();
	
	private IPreferenceStore store;

	private Group group;
	
	public DiconEditorColoringDesign(Composite parent, int style, IPreferenceStore store) {
		super(parent, style);
		this.store = store;
		buildDesign();
	}
	
	public void buildDesign() {
		setLayout(new GridLayout(2, false));
		setFont(getParent().getFont());
		
		group = new Group(this, SWT.SHADOW_ETCHED_IN);
				
		addColorFieldEditor("DiconEditorColoringDesign.2", PREF_COLOR_COMMENT);
		addColorFieldEditor("DiconEditorColoringDesign.3", PREF_COLOR_XML_DECL);
		addColorFieldEditor("DiconEditorColoringDesign.4", PREF_COLOR_DOC_DECL);
		addColorFieldEditor("DiconEditorColoringDesign.5", PREF_COLOR_TAG);
		addColorFieldEditor("DiconEditorColoringDesign.6", PREF_COLOR_ATTRIBUTE);
		addColorFieldEditor("DiconEditorColoringDesign.7", PREF_COLOR_DEFAULT);
	}
	
	private ColorFieldEditor addColorFieldEditor(String labelKey, String prefKey) {
		ColorFieldEditor c = new ColorFieldEditor(prefKey, Messages.getString(labelKey), group);
		setColor(c, store.getString(prefKey));
		colorFieldEditors.add(c);
		return c;
	}
	
	private void setColor(ColorFieldEditor editor, String rgbStr) {
		RGB rgb;
		try {
			rgb = StringConverter.asRGB(rgbStr);
		} catch (DataFormatException e) {
			KijimunaCore.reportException(e);
			rgb = COLOR_DEFAULT;
		}
		editor.getColorSelector().setColorValue(rgb);
		
	}
	
	public void store() {
		for (Iterator iterator = colorFieldEditors.iterator(); iterator.hasNext();) {
			ColorFieldEditor editor = (ColorFieldEditor) iterator.next();
			String prefKey = editor.getPreferenceName();
			RGB rgb = editor.getColorSelector().getColorValue();
			String color = StringConverter.asString(rgb);
			store.putValue(prefKey, color);
		}
	}
	
	public void loadDefault() {
		for (Iterator iterator = colorFieldEditors.iterator(); iterator.hasNext();) {
			ColorFieldEditor editor = (ColorFieldEditor) iterator.next();
			String prefName = editor.getPreferenceName();
			setColor(editor, store.getDefaultString(prefName));
		}
	}

}
