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
package org.seasar.kijimuna.ui.editor.contentassist;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public class JavaPackageProposalHolder implements Comparable {
    private IJavaElement element;
    private ICompletionProposal proposal; 
    
    public JavaPackageProposalHolder(IJavaElement element, ICompletionProposal proposal) {
        this.element = element;
        this.proposal = proposal;
    }
    
    public ICompletionProposal getProposal() {
        return proposal;
    }
    
    private boolean isArchive() {
        IJavaElement parent = element;
        while(parent != null) {
            if(parent instanceof IPackageFragmentRoot) {
                return ((IPackageFragmentRoot)parent).isArchive();
            }
            parent = parent.getParent();
        }
        return true;
    }
    
    private boolean isType() {
        return (element instanceof IType); 
    }
    
    private String getElementName() {
        return element.getElementName();
    }

    public int compareTo(Object test) {
        if(test instanceof JavaPackageProposalHolder) {
            JavaPackageProposalHolder testProposal = (JavaPackageProposalHolder)test;
            return getElementName().compareTo(testProposal.getElementName());
        }
        return -1;
    }
}