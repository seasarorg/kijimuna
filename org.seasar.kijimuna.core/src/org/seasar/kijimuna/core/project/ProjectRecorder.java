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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.util.ProjectUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProjectRecorder implements ISaveParticipant, ConstCore {
    
    private Plugin plugin;
    
    public ProjectRecorder(Plugin plugin) {
        this.plugin = plugin;
    }
    
	private IPath getRecorderFolderPath() {
	    IPath ret = plugin.getStateLocation().append(RECORDER_FOLDERNAME);
	    ret.toFile().mkdirs();
	    return ret;
	}
	
    private void handlerWorkspaceRecordable(
    		ISaveContext saveContext, int type, IProgressMonitor monitor) {
        int kind = ISaveContext.FULL_SAVE;
        if(saveContext != null) {
            kind = saveContext.getKind();
        }
        if(kind == ISaveContext.PROJECT_SAVE) {
            IProject project = saveContext.getProject();
            handleProjectRecordable(project, type, monitor);
        } else if(kind == ISaveContext.FULL_SAVE) {
            IProject[] projects = ProjectUtils.getAllProjects();
            for(int i = 0; i < projects.length; i++) {
                handleProjectRecordable(projects[i], type, monitor);
            }
        }
    }

    private void handleProjectRecordable(
    		IProject project, int type, IProgressMonitor monitor) {
        if(project.isOpen()) {
	        String[] natureIds = ProjectUtils.getNatureIds(project);
	        for(int i = 0; i < natureIds.length; i++) {
	            try {
	                IProjectNature nature = project.getNature(natureIds[i]);
	                if(nature instanceof IProjectRecordable) {
	                    IProjectRecordable recordable = (IProjectRecordable)nature;
	                    IPath path = getRecorderFolderPath();
	                    if(type == RECORDER_EVENT_INIT) {
	                   	    recordable.initProjectRecords(monitor);
	                    } else if(type == RECORDER_EVENT_RESTORE) {
	                   	    if(!recordable.restoreProjectRecords(path, monitor)) {
	                   	        recordable.initProjectRecords(monitor);
	                   	    }
	                    } else if(type == RECORDER_EVENT_SAVE) {
	                   	    recordable.saveProjectRecords(path, monitor);
	                    } else {
	                        recordable.customProcess(type, path, monitor);
	                    }
	                }
	            } catch (Exception e) {
	            }
	        }
        }
    }
    
    public void inithialize(IProgressMonitor monitor) {
       	handlerWorkspaceRecordable(null, RECORDER_EVENT_RESTORE, monitor);
    }

    public void cleanup(IProgressMonitor monitor) {
       	handlerWorkspaceRecordable(null, RECORDER_EVENT_INIT, monitor);
    }
    
    public void cleanup(IProject project, IProgressMonitor monitor) {
        handleProjectRecordable(project, RECORDER_EVENT_INIT, monitor);
    }
    
    public void validate(IProject project, IProgressMonitor monitor) {
        handleProjectRecordable(project, RECORDER_VALIDATE, monitor);
    }
    
    public void saving(ISaveContext saveContext) throws CoreException {
        handlerWorkspaceRecordable(saveContext, RECORDER_EVENT_SAVE, null);
    }

    public void prepareToSave(ISaveContext context) throws CoreException {
    }
    
    public void doneSaving(ISaveContext context) {
    }

    public void rollback(ISaveContext context) {
    }
}
