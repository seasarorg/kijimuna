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
package org.seasar.kijimuna.core.dicon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IExpressionElement;
import org.seasar.kijimuna.core.project.AbstractProcessor;
import org.seasar.kijimuna.core.project.IFileProcessor;
import org.seasar.kijimuna.core.util.FileUtils;
import org.seasar.kijimuna.core.util.MarkerUtils;

import com.sun.corba.se.spi.ior.MakeImmutable;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconValidator extends AbstractProcessor
	implements ConstCore {
    
    private static IValidation[] validations;
    private static IProjectValidation[] projectValidations;
    
    static {
        validations = DiconValidationFactory.createValidation();
        projectValidations = DiconValidationFactory.createProjectValidation();
    }

    //-------------------------------------------------
    
    public String getNatureID() {
        return ID_NATURE_DICON;
    }

    public void handleFileAdded(IFile addedFile, boolean fullBuild, IProgressMonitor monitor) {
        if(!fullBuild && FileUtils.isJavaFile(addedFile)) {
            fullValidate(addedFile.getProject(), monitor);
        } else {
            process(addedFile.getProject(), addedFile, monitor);
        }
    }
    
    public void handleFileRemoved(IFile removedFile, IProgressMonitor monitor) {
        Set set = new HashSet();
        relatedValidate(removedFile, monitor, set);
    }
    
    public void handleFileChanged(IFile changedFile, IProgressMonitor monitor) {
        process(changedFile.getProject(), changedFile, monitor);
        Set set = new HashSet();
        relatedValidate(changedFile, monitor, set);
    }
    
    public void handlePrepareFullProcess(IProject project, IProgressMonitor monitor) {
    }
	
    public void handleClassPassChanged(IProject project, IProgressMonitor monitor) {
        fullValidate(project, monitor);
    }

    public IFileProcessor getFileBuilder() {
        return this;
    }

    private void fullValidate(IProject project, IProgressMonitor monitor) {
        DiconNature nature = DiconNature.getInstance(project);
        if(nature != null) {
	        IContainerElement[] containers = nature.getModel().getContainers(monitor);
	        for(int i = 0; i < containers.length; i++) {
                IStorage storage = (IStorage)containers[i].getAdapter(IStorage.class);
        		if(storage instanceof IFile) {
                	MarkerUtils.deleteMarker((IFile)storage,
                			ID_MARKER_DICONVALIDAION);
                }
                validElement(containers[i], monitor);
	        }
        }
    }
    
    public void process(IProject project, IStorage storage, IProgressMonitor monitor) {
		if (FileUtils.isDiconFile(storage) && MarkerSetting.isDiconValidation(project)) {
		    if(storage instanceof IFile) {
		        MarkerUtils.deleteMarker((IFile)storage, ID_MARKER_DICONVALIDAION);
		    }
	        DiconNature nature = DiconNature.getInstance(project);
            if(nature != null) {
    		    if (monitor != null) {
    			    monitor.subTask(
    			            KijimunaCore.getResourceString("dicon.DiconBuilder.2",
    			            new Object[] { storage.getName() }));
    			}
                IContainerElement container = 
                    nature.getModel().getContainer(storage, monitor);
	            if(container != null) {
	                validElement(container, monitor);
	            }
	    		if (monitor != null) {
	    			monitor.done();
	    		}
            }
		}
    }

	public void handleFinish(IProject project, IProgressMonitor monitor) {
        try {
            project.deleteMarkers(ID_MARKER_DICONVALIDAION,
                    true, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
        }
        for(int i = 0; i < projectValidations.length; i++) {
            try {
                projectValidations[i].validation(project);
            } catch(Exception e) {
                KijimunaCore.reportException(e);
            }
        }
	}

    private void validElement(IDiconElement element, IProgressMonitor monitor) {
        synchronized (element) {
	        if(element instanceof IExpressionElement) {
	            ((IExpressionElement)element).setLocking(true);
	        }
	        for(int i = 0; i < validations.length; i++) {
	            try {
	                validations[i].validation(element);
	            } catch(Exception e) {
	                KijimunaCore.reportException(e);
	            }
	        }
	        List children = element.getChildren();
	        for(Iterator it = children.iterator(); it.hasNext();) {
	        	Object obj = it.next();
	        	if(obj instanceof IDiconElement) {
	        		IDiconElement child = (IDiconElement)obj;
	        		validElement(child, monitor);
	        	}
	        }
	        if(element instanceof IExpressionElement) {
	            ((IExpressionElement)element).setLocking(false);
	        }
        }
    }
    
    private void relatedValidate(IFile file, IProgressMonitor monitor, Set set) {
		if (FileUtils.isDiconFile(file) || FileUtils.isJavaFile(file)) {
		    DiconNature nature = DiconNature.getInstance(file.getProject());
		    if(nature != null) {
		        IFile[] relateds = nature.getModel().getRelatedFiles(file, true);
		        for(int i = 0; i < relateds.length; i++) {
		            if(!set.contains(relateds[i])) { 
		                process(file.getProject(), relateds[i], monitor);
		                set.add( relateds[i]);
		            }
		        }
		    }
		}
    }
}
