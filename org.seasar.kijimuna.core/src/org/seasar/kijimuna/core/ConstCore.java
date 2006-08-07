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
package org.seasar.kijimuna.core;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public interface ConstCore {

	public static final String ID_PLUGIN_CORE = "org.seasar.kijimuna.core";

	public static final String ID_NATURE_DICON = ID_PLUGIN_CORE + ".nature";
	public static final String ID_PROCESSOR_DICON_BUILDER = ID_PLUGIN_CORE + ".builder";
	public static final String ID_PROCESSOR_DICON_VALIDATOR = ID_PLUGIN_CORE + ".validator";

	public static final String ID_MARKER = ID_PLUGIN_CORE + ".marker";
	public static final String ID_MARKER_DICONVALIDAION = ID_MARKER + ".diconvalidation";
	public static final String ID_MARKER_DICONXML = ID_MARKER + ".diconxml";

	public static final String PREFERENCES_KEY_VERSION = ID_PLUGIN_CORE + ".version";

	public static final String PATH_RESOURCE = ID_PLUGIN_CORE + ".resources";

	public static final String SYSTEM_ID_DICON_20 = "http://www.seasar.org/dtd/components.dtd";
	public static final String PUBLIC_ID_DICON_20 = "-//SEASAR//DTD S2Container//EN";
	public static final String DTD_DICON_20 = "/components.dtd";
	public static final String DTD_DISPLAY_20 = "2.0";

	public static final String SYSTEM_ID_DICON_21 = "http://www.seasar.org/dtd/components21.dtd";
	public static final String PUBLIC_ID_DICON_21 = "-//SEASAR2.1//DTD S2Container//EN";
	public static final String DTD_DICON_21 = "/components21.dtd";
	public static final String DTD_DISPLAY_21 = "2.1";

	public static final String SYSTEM_ID_DICON_23 = "http://www.seasar.org/dtd/components23.dtd";
	public static final String PUBLIC_ID_DICON_23 = "-//SEASAR//DTD S2Container 2.3//EN";
	public static final String DTD_DICON_23 = "/components23.dtd";
	public static final String DTD_DISPLAY_23 = "2.3";

	public static final String SYSTEM_ID_DICON_24 = "http://www.seasar.org/dtd/components24.dtd";
	public static final String PUBLIC_ID_DICON_24 = "-//SEASAR//DTD S2Container 2.4//EN";
	public static final String DTD_DICON_24 = "/components24.dtd";
	public static final String DTD_DISPLAY_24 = "2.4";

	public static final String EXT_DICON = "dicon";

	public static final String DICON_TAG_ARG = "arg";
	public static final String DICON_TAG_ASPECT = "aspect";
	public static final String DICON_TAG_COMPONENT = "component";
	public static final String DICON_TAG_CONTAINER = "components";
	public static final String DICON_TAG_DESCRIPTION = "description";
	public static final String DICON_TAG_DESTROYMETHOD = "destroyMethod";
	public static final String DICON_TAG_INCLUDE = "include";
	public static final String DICON_TAG_INITMETHOD = "initMethod";
	public static final String DICON_TAG_META = "meta";
	public static final String DICON_TAG_PROPERTY = "property";
	public static final String DICON_BODY = "expression";
	public static final String DICON_DESCRIPTION = "description";
	public static final String DICON_INJECTED_VALUE = "injected_value";
	public static final String DICON_ATTR_PATH = "path";
	public static final String DICON_ATTR_NAME = "name";
	public static final String DICON_ATTR_POINTCUT = "pointcut";
	public static final String DICON_ATTR_CLASS = "class";
	public static final String DICON_ATTR_INSTANCE = "instance";
	public static final String DICON_ATTR_AUTOBINDING = "autoBinding";
	public static final String DICON_ATTR_NAMESPACE = "namespace";
	public static final String DICON_ATTR_BINDINGTYPE = "bindingType";
	public static final String[] DICON_ATTRS_ARG = new String[] {
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_ASPECT = new String[] {
			DICON_ATTR_POINTCUT,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_COMPONENT = new String[] {
			DICON_ATTR_NAME,
			DICON_ATTR_CLASS,
			DICON_ATTR_INSTANCE,
			DICON_ATTR_AUTOBINDING,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_CONTAINER = new String[] {
			DICON_ATTR_NAMESPACE,
			DICON_ATTR_PATH
	};
	public static final String[] DICON_ATTRS_DESCRIPTION = new String[] {
			DICON_DESCRIPTION
	};
	public static final String[] DICON_ATTRS_DESTROYMETHOD = new String[] {
			DICON_ATTR_NAME,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_INCLUDE = new String[] {
			DICON_ATTR_PATH
	};
	public static final String[] DICON_ATTRS_INITMETHOD = new String[] {
			DICON_ATTR_NAME,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_META = new String[] {
			DICON_ATTR_NAME,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String[] DICON_ATTRS_PROPERTY = new String[] {
			DICON_ATTR_NAME,
			DICON_ATTR_BINDINGTYPE,
			DICON_BODY,
			DICON_INJECTED_VALUE
	};
	public static final String DICON_VAL_INSTANCE_SINGLETON = "singleton";
	public static final String DICON_VAL_INSTANCE_PROTOTYPE = "prototype";
	public static final String DICON_VAL_INSTANCE_OUTER = "outer";
	public static final String DICON_VAL_INSTANCE_REQUEST = "request";
	public static final String DICON_VAL_INSTANCE_RESPONSE = "response";
	public static final String DICON_VAL_INSTANCE_SESSION = "session";
	public static final String DICON_VAL_AUTO_BINDING_AUTO = "auto";
	public static final String DICON_VAL_AUTO_BINDING_CONSTRUCTOR = "constructor";
	public static final String DICON_VAL_AUTO_BINDING_PROPERTY = "property";
	public static final String DICON_VAL_AUTO_BINDING_NONE = "none";
	public static final String DICON_VAL_BINDING_TYPE_MUST = "must";
	public static final String DICON_VAL_BINDING_TYPE_SHOULD = "should";
	public static final String DICON_VAL_BINDING_TYPE_MAY = "may";
	public static final String DICON_VAL_BINDING_TYPE_NONE = "none";

	public static final String DICON_NS_SEP = ".";

	// magic components
	public static final String MODEL_INTERFACE_INTERCEPTOR = "org.aopalliance.intercept.MethodInterceptor";
	public static final String MODEL_INTERFACE_S2CONTAINER = "org.seasar.framework.container.S2Container";
	public static final String MODEL_INTERFACE_REQUEST = "javax.servlet.http.HttpServletRequest";
	public static final String MODEL_INTERFACE_RESPONSE = "javax.servlet.http.HttpServletResponse";
	public static final String MODEL_INTERFACE_SESSION = "javax.servlet.http.HttpSession";
	public static final String MODEL_INTERFACE_SERVLETCONTEXT = "javax.servlet.ServletContext";
	public static final String MODEL_NAME_CONTAINER = "container";
	public static final String MODEL_NAME_REQUEST = "request";
	public static final String MODEL_NAME_RESPONSE = "response";
	public static final String MODEL_NAME_SESSION = "session";
	// for before version 2.3
	public static final String MODEL_NAME_SERVLETCONTEXT = "servletContext";
	// for since version 2.4
	public static final String MODEL_NAME_APPLICATION = "application";

	public static final int RECORDER_EVENT_INIT = 0;
	public static final int RECORDER_EVENT_RESTORE = 1;
	public static final int RECORDER_EVENT_SAVE = 2;
	public static final int RECORDER_VALIDATE = 3;

	public static final String RECORDER_FOLDERNAME = ".recorder";
	public static final String RECORDER_EXT_MODEL = "model";

	public static final String MARKER_ATTR_CATEGORY = "category";

	public static final int MARKER_SEVERITY_NONE = -1;
	public static final int MARKER_SEVERITY_ERROR = 0;
	public static final int MARKER_SEVERITY_WARNING = 1;
	public static final int MARKER_SEVERITY_INFO = 2;
	public static final int MARKER_SEVERITY_IGNORE = 3;

	public static final int MARKER_CATEGORY_XML_ERROR = 0;
	public static final int MARKER_CATEGORY_XML_WARNING = 1;
	public static final int MARKER_CATEGORY_NULL_INJECTION = 2;
	public static final int MARKER_CATEGORY_AUTO_INJECTION = 3;
	public static final int MARKER_CATEGORY_JAVA_FETAL = 4;
	public static final int MARKER_CATEGORY_DICON_FETAL = 5;
	public static final int MARKER_CATEGORY_DICON_PROBLEM = 6;
	public static final int MARKER_CATEGORY_UNKNOWN = 7;

	public static final String MARKER_SEVERITY_XML_ERROR = "marker.severity.xml_error";
	public static final String MARKER_SEVERITY_XML_WARNING = "marker.severity.xml_warning";
	public static final String MARKER_SEVERITY_NOT_VALIDATION = "marker.severity.not_validation";
	public static final String MARKER_SEVERITY_NULL_INJECTION = "marker.severity.null_injection";
	public static final String MARKER_SEVERITY_AUTO_INJECTION = "marker.severity.auto_injection";
	public static final String MARKER_SEVERITY_JAVA_FETAL = "marker.severity.java_fetal";
	public static final String MARKER_SEVERITY_DICON_FETAL = "marker.severity.dicon_fetal";
	public static final String MARKER_SEVERITY_DICON_PROBLEM = "marker.severity.dicon_problem";

	public static final String[] MARKER_SEVERITY_ALL = new String[] {
			MARKER_SEVERITY_XML_ERROR,
			MARKER_SEVERITY_XML_WARNING,
			MARKER_SEVERITY_NULL_INJECTION,
			MARKER_SEVERITY_AUTO_INJECTION,
			MARKER_SEVERITY_JAVA_FETAL,
			MARKER_SEVERITY_DICON_FETAL,
			MARKER_SEVERITY_DICON_PROBLEM,
	};

	public static final String[] MARKER_SET_XML_ERROR = new String[] {
			"parser.DocumentHandler.1"
	};
	public static final String[] MARKER_SET_XML_WARNING = new String[] {
			"parser.DocumentHandler.2"
	};
	public static final String[] MARKER_SET_NULL_INJECTION = new String[] {
			"dicon.validation.AutoConstructorInvoke.3",
			"dicon.validation.AutoMethodInvoke.2",
			"dicon.validation.AutoSetterInjection.2"
	};
	public static final String[] MARKER_SET_AUTO_INJECTION = new String[] {
			"dicon.validation.AutoConstructorInvoke.2",
			"dicon.validation.AutoMethodInvoke.1",
			"dicon.validation.AutoSetterInjection.1"
	};
	public static final String[] MARKER_SET_JAVA_FETAL = new String[] {
			"dicon.validation.AspectValidation.4",
			"dicon.validation.AutoConstructorInvoke.1",
			"dicon.validation.AutoMethodInvoke.3",
			"dicon.validation.ClasspathValidation.1",
			"dicon.validation.ComponentHolderValidation.1",
			"dicon.validation.ComponentValidation.1",
			"dicon.validation.ManualConstructorInvoke.2",
			"dicon.validation.ManualSetterInjection.1",
			"dicon.validation.ManualMethodInvoke.1"
	};
	public static final String[] MARKER_SET_DICON_FETAL = new String[] {
			"dicon.validation.AspectAssemble.1",
			"dicon.validation.AspectValidation.1",
			"dicon.validation.AspectValidation.2",
			"dicon.validation.AutoConstructorInvoke.4",
			"dicon.validation.AutoConstructorInvoke.5",
			"dicon.validation.AutoMethodInvoke.4",
			"dicon.validation.AutoSetterInjection.3",
			"dicon.validation.ComponentHolderValidation.3",
			"dicon.validation.ComponentValidation.2",
			"dicon.validation.ComponentValidation.4",
			"dicon.validation.ExpressionMethodInvoke.1",
			"dicon.validation.IncludeValidation.1",
			"dicon.validation.IncludeValidation.2",
			"dicon.validation.IncludeValidation.3",
			"dicon.validation.ManualConstructorInvoke.1",
			"dicon.validation.MethodValidation.2",
			"dicon.validation.PropertyValidation.1"
	};
	public static final String[] MARKER_SET_DICON_PROBLEM = new String[] {
			"dicon.validation.AspectValidation.3",
			"dicon.validation.AspectValidation.5",
			"dicon.validation.AspectValidation.6",
			"dicon.validation.AspectValidation.7",
			"dicon.validation.AspectValidation.8",
			"dicon.validation.AspectValidation.9",
			"dicon.validation.ComponentHolderValidation.2",
			"dicon.validation.ComponentHolderValidation.4",
			"dicon.validation.ComponentValidation.3",
			"dicon.validation.DestroyMethodValidation.1",
			"dicon.validation.MethodValidation.1",
			"dicon.validation.MethodValidation.3"
	};

}
