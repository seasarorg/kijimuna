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
package org.seasar.kijimuna.ui;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.core.ConstCore;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public interface ConstUI extends ConstCore {

	String ID_PLUGIN_UI = "org.seasar.kijimuna.ui";
	String ID_EDITOR_DICON = ID_PLUGIN_UI + ".editor.dicon";

	String PATH_RESOURCES = ID_PLUGIN_UI + ".resources";
	String PATH_IMAGES = "/images/";

	String EXTENSION_DICONEDITOR = "org.seasar.kijimuna.ui.diconeditorpart";
	String EXTENSION_ATTR_CLASS = "class";
	String EXTENSION_ATTR_NAME = "name";
	String EXTENSION_ATTR_INDEX = "index";

	String IMAGE_DECORATOR_AUTO = "decorator/auto.gif";
	String IMAGE_DECORATOR_AUTO_NULL = "decorator/auto_null.gif";
	String IMAGE_DECORATOR_OGNL = "decorator/ognl.gif";
	String IMAGE_DECORATOR_ERROR = "decorator/error.gif";
	String IMAGE_DECORATOR_WARNING = "decorator/warning.gif";

	String IMAGE_ICON_ARG = "icon/arg.gif";
	String IMAGE_ICON_ASPECT = "icon/aspect.gif";
	String IMAGE_ICON_ASPECT_POINTCUT = "icon/aspect_pointcut.gif";
	String IMAGE_ICON_ASPECT_REGEXP = "icon/aspect_regexp.gif";
	String IMAGE_ICON_COMPONENT = "icon/component.gif";
	String IMAGE_ICON_CONTAINER = "icon/container.gif";
	String IMAGE_ICON_DESCRIPTION = "icon/description.gif";
	String IMAGE_ICON_DESTROYMETHOD = "icon/destroyMethod.gif";
	String IMAGE_ICON_EXPRESSION = "icon/expression.gif";
	String IMAGE_ICON_INCLUDE = "icon/include.gif";
	String IMAGE_ICON_INITMETHOD = "icon/initMethod.gif";
	String IMAGE_ICON_JAVA_CLASS = "icon/java_class.gif";
	String IMAGE_ICON_JAVA_INTERFACE = "icon/java_interface.gif";
	String IMAGE_ICON_JAVA_FIELD = "icon/java_field.gif";
	String IMAGE_ICON_JAVA_JAR_PACKAGE = "icon/java_jar_package.gif";
	String IMAGE_ICON_JAVA_METHOD = "icon/java_method.gif";
	String IMAGE_ICON_JAVA_PACKAGE = "icon/java_package.gif";
	String IMAGE_ICON_KEY_INTERFACE = "icon/key_interface.gif";
	String IMAGE_ICON_KEY_ROOT = "icon/key_root.gif";
	String IMAGE_ICON_KEY_STRING = "icon/key_string.gif";
	String IMAGE_ICON_META = "icon/meta.gif";
	String IMAGE_ICON_PARENT = "icon/parent.gif";
	String IMAGE_ICON_PARENTS = "icon/parents.gif";
	String IMAGE_ICON_PROPERTY = "icon/property.gif";
	String IMAGE_ICON_XML_ATTR = "icon/xml_attr.gif";
	String IMAGE_ICON_XML_ITEM = "icon/xml_item.gif";
	String IMAGE_ICON_XML_TAG = "icon/xml_tag.gif";

	String IMAGE_ICON_SEARCH_DICON = "icon/search_dicon.gif";
	String IMAGE_ICON_NEW_DICON = "icon/new_dicon.gif";
	String IMAGE_NEW_DICON = "picture/wiz_new_dicon.gif";

	String[] ECLIPSE_ENCODINGS = new String[] {
			"ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE", "US-ASCII",
			"Shift_JIS", "Windows-31J"
	};

	String ACTION_CONTENTASSIST_PROPOSAL = "ContentAssistProposal";
	String ID_CONTENTASSIST_PROPOSAL = ID_PLUGIN_UI + "." + ACTION_CONTENTASSIST_PROPOSAL;

	ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];

	int PROPOSAL_MODE_NONE = 0;
	int PROPOSAL_MODE_XML_DECL = 1;
	int PROPOSAL_MODE_DOC_DECL = 2;
	int PROPOSAL_MODE_ELEMENT = 3;
	int PROPOSAL_MODE_ELEMENT_CLOSING = 4;
	int PROPOSAL_MODE_ATTRIBUTE = 5;
	int PROPOSAL_MODE_ATTRIBUTE_VALUE = 6;

}
