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

import java.util.regex.Pattern;

import org.eclipse.jdt.core.IType;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class RttiWrapper implements IRtti {

	private IRtti rtti;

	public RttiWrapper(IRtti rtti) {
		this.rtti = rtti;
	}

	protected IRtti getWrappedRtti() {
		return rtti;
	}

	public boolean equals(Object obj) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.equals(obj);
		}
		return false;
	}

	public int hashCode() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.hashCode();
		}
		return super.hashCode();
	}

	public String toString() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.toString();
		}
		return super.toString();
	}

	public int compareTo(Object test) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.compareTo(test);
		}
		return 0;
	}

	public Object getAdapter(Class adapter) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getAdapter(adapter);
		}
		return null;
	}

	public IType getType() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getType();
		}
		return null;
	}

	public RttiLoader getRttiLoader() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getRttiLoader();
		}
		return null;
	}

	public boolean isInterface() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.isInterface();
		}
		return false;
	}

	public boolean isFinal() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.isFinal();
		}
		return false;
	}

	public boolean isAssignableFrom(IRtti testRtti) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.isAssignableFrom(testRtti);
		}
		return false;
	}

	public String getQualifiedName() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getQualifiedName();
		}
		return null;
	}

	public String getShortName() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getShortName();
		}
		return null;
	}

	public IRtti[] getInterfaces() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getInterfaces();
		}
		return new IRtti[0];
	}

	public IRtti getSuperClass() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getSuperClass();
		}
		return null;
	}

	public IRttiFieldDescriptor getField(String field, boolean staticAccess) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getField(field, staticAccess);
		}
		return null;
	}

	public IRttiFieldDescriptor[] getFields(Pattern pattern) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getFields(pattern);
		}
		return null;
	}

	public IRttiConstructorDesctiptor getConstructor(IRtti[] args) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getConstructor(args);
		}
		return null;
	}

	public IRttiConstructorDesctiptor[] getConstructors() {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getConstructors();
		}
		return new IRttiConstructorDesctiptor[0];
	}

	public IRttiMethodDesctiptor getMethod(String name, IRtti[] args, boolean staticAccess) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getMethod(name, args, staticAccess);
		}
		return null;
	}

	public IRttiMethodDesctiptor[] getMethods(Pattern pattern) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getMethods(pattern);
		}
		return new IRttiMethodDesctiptor[0];
	}

	public IRttiPropertyDescriptor getProperty(String name) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getProperty(name);
		}
		return null;
	}

	public IRttiPropertyDescriptor[] getProperties(Pattern pattern) {
		IRtti thisRtti = getWrappedRtti();
		if (thisRtti != null) {
			return thisRtti.getProperties(pattern);
		}
		return new IRttiPropertyDescriptor[0];
	}

}
