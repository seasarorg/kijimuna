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
package org.seasar.kijimuna.ui.internal.editor.dicon;

import org.eclipse.ui.IEditorPart;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class EditorPartItem implements Comparable {

	private IEditorPart editor;
	private String title;
	private int index;

	public EditorPartItem(IEditorPart editor, String title, int index) {
		this.editor = editor;
		this.title = title;
		this.index = index;
	}

	public int compareTo(Object obj) {
		EditorPartItem comparer = (EditorPartItem) obj;
		int compIndex = comparer.index;
		return index - compIndex;
	}

	public IEditorPart getEditorPart() {
		return editor;
	}

	public String getTitle() {
		return title;
	}

}
