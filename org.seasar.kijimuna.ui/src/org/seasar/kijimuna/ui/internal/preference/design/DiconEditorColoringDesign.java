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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.seasar.kijimuna.core.preference.IPreferences;
import org.seasar.kijimuna.core.util.PreferencesUtil;
import org.seasar.kijimuna.ui.editor.configuration.xml.XmlConsts;

public class DiconEditorColoringDesign extends Composite implements
		XmlConsts, IStorableDesigin {

	private List colorFieldEditors = new ArrayList();
	private IProject project;
	
	public DiconEditorColoringDesign(Composite parent, int style) {
		super(parent, style);
		buildDesign();
	}
	
	public void store() {
		for (int i = 0; i < colorFieldEditors.size(); i++) {
			ColorFieldEditor c = (ColorFieldEditor) colorFieldEditors.get(i);
			putRGB(c.getPreferenceName(), c.getColorSelector().getColorValue());
		}
	}
	
	public void loadDefault() {
		for (int i = 0; i < colorFieldEditors.size(); i++) {
			ColorFieldEditor c = (ColorFieldEditor) colorFieldEditors.get(i);
			setColor(c, c.getPreferenceName(), true);
		}
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void buildDesign() {
		setLayout(new GridLayout(2, false));
		setFont(getParent().getFont());
		
		addColorFieldEditor("DiconEditorColoringDesign.2", PREF_COLOR_COMMENT);
		addColorFieldEditor("DiconEditorColoringDesign.3", PREF_COLOR_XML_DECL);
		addColorFieldEditor("DiconEditorColoringDesign.4", PREF_COLOR_DOC_DECL);
		addColorFieldEditor("DiconEditorColoringDesign.5", PREF_COLOR_TAG);
		addColorFieldEditor("DiconEditorColoringDesign.6", PREF_COLOR_ATTRIBUTE);
		addColorFieldEditor("DiconEditorColoringDesign.7", PREF_COLOR_DEFAULT);
	}
	
	private ColorFieldEditor addColorFieldEditor(String labelKey, String prefKey) {
		ColorFieldEditor c = new ColorFieldEditor(prefKey, Messages.getString(
				labelKey), this);
		setColor(c, prefKey, false);
		colorFieldEditors.add(c);
		return c;
	}
	
	private void setColor(ColorFieldEditor c, String prefKey, boolean isDefault) {
		c.getColorSelector().setColorValue(getRGB(prefKey, isDefault));
	}
	
	private RGB getRGB(String prefKey, boolean isDefault) {
		IPreferences pref = PreferencesUtil.getPreferencesExactly(getProject());
		try {
			return StringConverter.asRGB(isDefault ? pref.getDefault(prefKey) :
				pref.get(prefKey));
		} catch (DataFormatException e) {
			return COLOR_DEFAULT;
		}
	}
	
	private void putRGB(String prefKey, RGB rgb) {
		IPreferences pref = PreferencesUtil.getPreferences(getProject());
		if (pref != null) {
			pref.put(prefKey, StringConverter.asString(rgb));
		}
	}
	
	private IProject getProject() {
		return project;
	}

}
