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
package org.seasar.kijimuna.ui.internal.wizard.dicon;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.util.WidgetUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class NewDiconWizard extends BasicNewResourceWizard
		implements ConstUI {
    
    private NewDiconWizardPage page;
    
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
    	super.init(workbench, currentSelection);
    	setWindowTitle(KijimunaUI.getResourceString("dicon.wizard.NewDiconWizard.1")); //$NON-NLS-1$
    	setNeedsProgressMonitor(true);
    }
    
    public void addPages() {
    	super.addPages();
    	page = new NewDiconWizardPage("newDiconPage");
    	addPage(page);    	
    	page.init(getSelection());
    }

    public void dispose() {
        super.dispose();
        
    }

    protected void initializeDefaultPageImageDescriptor() {
        setDefaultPageImageDescriptor(
                WidgetUtils.getImageDescriptor(IMAGE_NEW_DICON));
    }    
    
    public boolean performFinish() {
        return page.createDiconFile();
    }
}
