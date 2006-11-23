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
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class DeltaVisitor implements IResourceDeltaVisitor {

	private String natureID;
	private IFileProcessor builder;
	private IProgressMonitor monitor;

	public DeltaVisitor(String natureID, IFileProcessor builder, IProgressMonitor monitor) {
		this.natureID = natureID;
		this.builder = builder;
		this.monitor = monitor;
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			return (project.isOpen() && (ProjectUtils.getNature(project, natureID) != null));
		} else if (resource instanceof IFolder) {
			return true;
		} else if (resource instanceof IFile) {
			ProjectUtils.clearDiconStorageWithCash(resource.getProject());
			IPath path = resource.getFullPath();
			if ((path.segmentCount() == 2) && path.lastSegment().equals(".classpath")) {
				builder.handleClassPassChanged(resource.getProject(), monitor);
			}
			if (FileUtils.isInJavaSourceFolder((IFile) resource)) {
				int kind = delta.getKind();
				IFile file = (IFile) resource;
				if (kind == IResourceDelta.REMOVED) {
					builder.handleFileRemoved(file, monitor);
				} else if (kind == IResourceDelta.ADDED) {
					builder.handleFileAdded(file, false, monitor);
				} else if (kind == IResourceDelta.CHANGED) {
					builder.handleFileChanged(file, monitor);
				}
			}
		}
		return false;
	}

}
