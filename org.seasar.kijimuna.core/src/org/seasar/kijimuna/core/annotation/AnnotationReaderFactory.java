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
package org.seasar.kijimuna.core.annotation;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.annotation.ConstantAnnotationReader;

public class AnnotationReaderFactory {
	
    private static final String TIGER_ANNOTATION_HANDLER_CLASS_NAME =
    	"org.seasar.framework.container.factory.TigerAnnotationHandler";

    private static final String BACKPORT175_ANNOTATION_HANDLER_CLASS_NAME =
    	"org.seasar.framework.container.factory.Backport175AnnotationHandler";

    private static ConstantAnnotationReader constantAnnotationReader;
    
	public static IAnnotationReader getAnnotationReader(IProject project) {
		if (project == null) {
			return getConstantAnnotationReader();
		}
		IJavaProject jproj = JavaCore.create(project);
		try {
			if (jproj.findType(TIGER_ANNOTATION_HANDLER_CLASS_NAME) != null) {
//				return new TigerAnnotationReader();
			} else if (jproj.findType(BACKPORT175_ANNOTATION_HANDLER_CLASS_NAME)
					!= null) {
//				return new Backport175AnnotationReader();
			}
		} catch (JavaModelException e) {
			KijimunaCore.reportException(e);
		}
		return getConstantAnnotationReader();
	}
	
	private static ConstantAnnotationReader getConstantAnnotationReader() {
		if (constantAnnotationReader == null) {
			constantAnnotationReader = new ConstantAnnotationReader();
		}
		return constantAnnotationReader;
	}

}
