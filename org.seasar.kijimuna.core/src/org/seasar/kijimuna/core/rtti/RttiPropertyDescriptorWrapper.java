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

public class RttiPropertyDescriptorWrapper implements IRttiPropertyDescriptor {

	IRttiPropertyDescriptor propDesc;
	
	public RttiPropertyDescriptorWrapper(IRttiPropertyDescriptor propDesc) {
		this.propDesc = propDesc;
	}
	
	public IRtti getParent() {
		return propDesc != null ? propDesc.getParent() : null;
	}

	public IRtti getType() {
		return propDesc != null ? propDesc.getType() : null;
	}

	public String getName() {
		return propDesc != null ? propDesc.getName() : null;
	}

	public IRtti getValue() {
		return propDesc != null ? propDesc.getValue() : null;
	}

	public void setValue(IRtti value) {
		if (propDesc != null) {
			propDesc.setValue(value);
		}
	}

	public boolean isWritable() {
		return propDesc != null ? propDesc.isWritable() : false;
	}

	public boolean isReadable() {
		return propDesc != null ? propDesc.isReadable() : false;
	}

	public Object getAdapter(Class adapter) {
		return propDesc != null ? propDesc.getAdapter(adapter) : null;
	}

	public int compareTo(Object o) {
		if (propDesc == null) {
			return 1;
		}
		if (o instanceof RttiPropertyDescriptorWrapper) {
			o = ((RttiPropertyDescriptorWrapper) o).getDelegate();
		}
		return propDesc.compareTo(o);
	}
	
	public int hashCode() {
		return propDesc != null ? propDesc.hashCode() : super.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (propDesc == null) {
			return super.equals(obj);
		}
		if (obj instanceof RttiPropertyDescriptorWrapper) {
			obj = ((RttiPropertyDescriptorWrapper) obj).getDelegate();
		}
		return propDesc.equals(obj);
	}
	
	protected IRttiPropertyDescriptor getDelegate() {
		return propDesc;
	}

}
