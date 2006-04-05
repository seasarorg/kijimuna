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
package org.seasar.kijimuna.core.rtti;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IType;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface IRtti extends IAdaptable, Serializable, Comparable {

	IType getType();

	RttiLoader getRttiLoader();

	boolean isInterface();

	boolean isFinal();

	boolean isAssignableFrom(IRtti testRtti);

	String getQualifiedName();

	String getShortName();

	IRtti[] getInterfaces();

	IRtti getSuperClass();

	IRttiFieldDescriptor getField(String field, boolean staticAccess);

	IRttiFieldDescriptor[] getFields(Pattern pattern);

	IRttiConstructorDesctiptor getConstructor(IRtti[] args);

	IRttiConstructorDesctiptor[] getConstructors();

	IRttiMethodDesctiptor getMethod(String name, IRtti[] args, boolean staticAccess);

	IRttiMethodDesctiptor[] getMethods(Pattern pattern);

	IRttiPropertyDescriptor getProperty(String name);

	IRttiPropertyDescriptor[] getProperties(Pattern pattern);

}
