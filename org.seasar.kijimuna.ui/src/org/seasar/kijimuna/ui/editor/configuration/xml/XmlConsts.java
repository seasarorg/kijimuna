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
package org.seasar.kijimuna.ui.editor.configuration.xml;

import org.eclipse.swt.graphics.RGB;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface XmlConsts {

	String TYPE = "org.seasar.kijimuna.ui.editor.configuration.xml";

	String TYPE_COMMENT = TYPE + ".COMMENT";
	String TYPE_XML_DECL = TYPE + ".XML_DECL";
	String TYPE_DOC_DECL = TYPE + ".DOC_DECL";
	String TYPE_TAG = TYPE + ".TAG";

	RGB COLOR_COMMENT = new RGB(128, 0, 0);
	RGB COLOR_XML_DECL = new RGB(128, 128, 128);
	RGB COLOR_DOC_DECL = new RGB(64, 128, 128);
	RGB COLOR_TAG = new RGB(0, 0, 128);
	RGB COLOR_ATTRIBUTE = new RGB(0, 128, 0);
	RGB COLOR_DEFAULT = new RGB(0, 0, 0);
	
	String PREF_COLOR = "editor.color";
	String PREF_COLOR_COMMENT = PREF_COLOR + ".comment";
	String PREF_COLOR_XML_DECL = PREF_COLOR + ".xmldecl";
	String PREF_COLOR_DOC_DECL = PREF_COLOR + ".docdecl";
	String PREF_COLOR_TAG = PREF_COLOR + ".tag";
	String PREF_COLOR_ATTRIBUTE = PREF_COLOR + ".attribute";
	String PREF_COLOR_DEFAULT = PREF_COLOR + ".default";
	String PREF_COLOR_BACKGROUND = PREF_COLOR + ".background";

}
