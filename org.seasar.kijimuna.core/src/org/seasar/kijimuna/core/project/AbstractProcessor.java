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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kijimuna.core.KijimunaCore;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public abstract class AbstractProcessor 
		extends IncrementalProjectBuilder implements IFileProcessor {

	protected final IProject[] build(int kind, Map args,
			IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		if(project != null) {
			if (kind != FULL_BUILD) {
				IResourceDelta resourceDelta = getDelta(project);
				if (resourceDelta != null) {
					resourceDelta.accept(new DeltaVisitor(getNatureID(), this, monitor));
				}
			} else {
				project.accept(new ResourceVisitor(getNatureID(), this, monitor));
			}
			handleFinish(project, monitor);
		}
		return null;
	}

    public void processProject(IProject project, IProgressMonitor monitor) {
        if(project != null) {
            try {
                project.accept(new ResourceVisitor(getNatureID(), this, monitor));
    			handleFinish(project, monitor);
            } catch (CoreException e) {
                KijimunaCore.reportException(e);
            }
        }
    }
	
}