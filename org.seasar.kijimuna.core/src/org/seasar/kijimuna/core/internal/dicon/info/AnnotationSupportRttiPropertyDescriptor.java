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
package org.seasar.kijimuna.core.internal.dicon.info;

import org.eclipse.core.resources.IProject;

import org.seasar.kijimuna.core.annotation.AnnotationReaderFactory;
import org.seasar.kijimuna.core.annotation.IAnnotationReader;
import org.seasar.kijimuna.core.annotation.IBindingAnnotation;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.rtti.RttiPropertyDescriptorWrapper;

public class AnnotationSupportRttiPropertyDescriptor extends
		RttiPropertyDescriptorWrapper {

	public AnnotationSupportRttiPropertyDescriptor(IRttiPropertyDescriptor
			propDesc) {
		super(propDesc);
	}
	
	public Object getAdapter(Class adapter) {
		if (IBindingAnnotation.class.equals(adapter)) {
			return getBindingAnnotation();
		}
		return super.getAdapter(adapter);
	}
	
	private IBindingAnnotation getBindingAnnotation() {
		IAnnotationReader reader = AnnotationReaderFactory.getAnnotationReader(
				getProject());
		return reader.getBindingAnnotation(this);
	}
	
	private IProject getProject() {
		return (IProject) getParent().getAdapter(IProject.class);
	}

}
