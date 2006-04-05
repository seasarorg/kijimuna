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
package org.seasar.kijimuna.ui.internal.editor.dicon;

import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconEditor extends MultiPageEditorPart implements ConstUI {
   
	private DiconOutlinePage outline;
	private IEditorPart editor;
	
    private int addEditorPage(IEditorPart part, String title) {
        int ret = 0;
		try {
			ret = addPage(part, getEditorInput());
			setPageText(ret, title);
		} catch (PartInitException e) {
		    KijimunaUI.reportException(e);
		}
		return ret;
    }
    
    public DiconOutlinePage getOutlinePage() {
        if(outline == null) {
        	outline = new DiconOutlinePage(this);
        }
        return outline;
    }

	public IEditorPart getSourceEditor(){
		if(editor instanceof DiconXmlEditor){
			return (DiconXmlEditor)editor;
		} else {
			return ((DiconEditor)editor).getSourceEditor();
		}
	}

    protected void createPages() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = 
            registry.getExtensionPoint(EXTENSION_DICONEDITOR);
        if(point != null) {
	        IExtension[] extensions = point.getExtensions();
	        TreeSet set = new TreeSet();
	        for(int i = 0; i < extensions.length; i++) {
	            IConfigurationElement conf = extensions[i].getConfigurationElements()[0];
	            try {
	                Object obj = conf.createExecutableExtension(
	                        EXTENSION_ATTR_CLASS);
	                if(obj instanceof DiconXmlEditor) {
	                    editor = (IEditorPart)obj;
	                    String title = conf.getAttribute(
	                            EXTENSION_ATTR_NAME);
	                    int index = Integer.parseInt(conf.getAttribute(
	                            EXTENSION_ATTR_INDEX));
	                    set.add(new EditorPartItem(editor, title, index));
	                }
	            } catch (Exception e) {
	                KijimunaUI.reportException(e);
	            }
	        }
	        for(Iterator it = set.iterator(); it.hasNext();) {
	            EditorPartItem item = (EditorPartItem)it.next();
	            addEditorPage(item.getEditorPart(), item.getTitle());
	        }
        }
        setPartName(getEditorInput().getName());
        setTitleToolTip(getEditorInput().getToolTipText());
    }

    public void doSave(IProgressMonitor monitor) {
        for(int i = 0; i < getPageCount(); i++) {
		    IEditorPart editor = getEditor(i); 
	        if(editor.isSaveAsAllowed() && editor.isDirty()) {
	            editor.doSave(monitor);
	            break;
	        }
		}
     }

    public void doSaveAs() {
		for(int i = 0; i < getPageCount(); i++) {
		    IEditorPart editor = getEditor(i); 
	        if(editor.isSaveAsAllowed()) {
	            editor.doSaveAs();
	            IEditorInput input = editor.getEditorInput();
	    		setInput(input);
	    		setPartName(input.getName());
	            setTitleToolTip(getEditorInput().getToolTipText());
	    		return;
	        }
		}
    }

    public boolean isSaveAsAllowed() {
		for(int i = 0; i < getPageCount(); i++) {
		    IEditorPart editor = getEditor(i);
		    if(editor.isSaveAsAllowed()) {
		        return true;
		    }
		}
		return false;
    }
    
    public Object getAdapter(Class adapter) {
		if (adapter.equals(IContentOutlinePage.class)) {
		    return getOutlinePage();
		} else if(adapter.equals(IGotoMarker.class)) {
			for(int i = 0; i < getPageCount(); i++) {
			    IEditorPart editor = getEditor(i);
			    if(editor instanceof IGotoMarker) {
			        return editor;
			    }
		    	Object obj = editor.getAdapter(IGotoMarker.class);
		    	if(obj != null) {
		    		return obj;
		    	}
			}
			return null;
		}
        return super.getAdapter(adapter);
    }
}

