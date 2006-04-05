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
package org.seasar.kijimuna.core.internal.dicon.refactor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.dicon.DiconNature;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TypeRenameParticipant extends RenameParticipant {
    
    private IFile[] dicons;
    
    public String getName() {
        return KijimunaCore.getResourceString("dicon.refactor.TypeRenameParticipant.1");
    }
    
    protected boolean initialize(Object element) {
        dicons = null;
        if(element instanceof IType) {
            IType type = (IType)element;
            try {
                IFile file = (IFile)type.getUnderlyingResource();
                IProject project = file.getProject();
                DiconNature nature = DiconNature.getInstance(project);
                if(nature != null) {
                    dicons = nature.getModel().getRelatedFiles(file, true);
                    return dicons.length > 0;
                }
            } catch (JavaModelException e) {
                KijimunaCore.reportException(e);
            }
        }
        return false;
    }
    
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        return RefactoringStatus.createInfoStatus("OK");
    }

    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return new Change() {
            public String getName() {
                return KijimunaCore.getResourceString("dicon.refactor.TypeRenameParticipant.1");
            }

            public Object getModifiedElement() {
                return null;
            }

            public void initializeValidationData(IProgressMonitor pm) {
            }

            public RefactoringStatus isValid(IProgressMonitor pm)
                    throws CoreException, OperationCanceledException {
                return RefactoringStatus.createInfoStatus("OK");
            }

            public Change perform(IProgressMonitor pm) throws CoreException {
                // TODO リファクタリング追随機能
                for(int i = 0; i < dicons.length; i++) {
                    System.out.println(dicons[i].getFullPath());
                }
                
                return null;
            }
        };
    }

}
