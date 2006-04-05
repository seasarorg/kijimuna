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
package org.seasar.kijimuna.core.project;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ResourceVisitor implements IResourceVisitor {
	
	private String natureID;
    private IFileProcessor builder;
	private IProgressMonitor monitor;

	public ResourceVisitor(String natureID, IFileProcessor builder, IProgressMonitor monitor) {
		this.natureID = natureID;
	    this.builder = builder;
		this.monitor = monitor;
	}
	
	public boolean visit(IResource resource) throws CoreException {
		if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			if(project.isOpen() &&
			        (ProjectUtils.getNature(project, natureID) != null)) {
				builder.handlePrepareFullProcess(project, monitor);
				return true;
			}
		} else if (resource instanceof IFolder) {
			return true;
		} else if (resource instanceof IFile) {
		    if(FileUtils.isInJavaSourceFolder((IFile) resource)) {
		        IFile file = (IFile) resource;
		        builder.handleFileAdded(file, true, monitor);
		    }
		}
		return false;
	}

}
