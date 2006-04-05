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
package org.seasar.kijimuna.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.editor.dicon.DiconEditor;
import org.seasar.kijimuna.ui.internal.provider.dicon.IExternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.IHasJavaElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInjectedComponent;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class WorkbenchUtils implements ConstUI {
    
    public static IWorkbenchPage getWorkbenchPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if(workbench != null) {
			IWorkbenchWindow window =workbench.getActiveWorkbenchWindow();
			if(window != null) {
				return window.getActivePage();
			}
		}
		return null;
    }

    public static IStorage getInputResource(IEditorPart part) {
        IEditorInput input = part.getEditorInput();
        if(input instanceof IFileEditorInput) {
            return ((IFileEditorInput)input).getFile();
        } else if(input instanceof IStorageEditorInput) {
            try {
                return ((IStorageEditorInput)input).getStorage();
            } catch (CoreException e) {
	            KijimunaUI.reportException(e);
            }
        }
        return null;
    }

    public static DiconEditor openDiconEditor(IStorage storage) {
    	if(storage != null) {
	        IWorkbenchPage page = getWorkbenchPage();
	        if(page != null) {
		        try {
		            if(storage instanceof IFile) {
		            	IEditorInput input = new FileEditorInput((IFile)storage);
		                return (DiconEditor)page.openEditor(input, ID_EDITOR_DICON);
		            }
	            	IEditorInput input = new JarEntryEditorInput(storage); 
	                return (DiconEditor)page.openEditor(input, ID_EDITOR_DICON);
		        } catch (CoreException e) {
		            KijimunaUI.reportException(e);
		        }
	        }
    	}
        return null;
    }
    
    public static IEditorPart openJavaEditor(IJavaElement java) {
    	try {
    	    IEditorPart part = JavaUI.openInEditor(java);
    	    JavaUI.revealInEditor( part, java);
			return part;
		} catch (Exception e) {
			KijimunaUI.reportException(e);
			return null;
		}
    }
    
    public static IEditorPart showSource(IEditorPart part, Object obj) {
        if(obj instanceof IHasJavaElement) {
            IJavaElement java = ((IHasJavaElement)obj).getJavaElement();
            if(java != null) {
                return openJavaEditor(java);
            }
        } else if(obj instanceof IInjectedComponent) {
		    IDiconElement element = ((IInjectedComponent)obj).getInjectedElement();
		    if(element != null) {
			    IStorage file = element.getStorage();
				if(file != null) {
				    IStorage source = null;
		            if(part != null) {
		                source = WorkbenchUtils.getInputResource(part);
		            }
	        		int lineNumber = element.getStartLine();
		            if(!file.equals(source)) {
		                DiconEditor editor = WorkbenchUtils.openDiconEditor(file);
		        		WorkbenchUtils.moveLine(editor, lineNumber);
	//	        		editor.getOutlinePage().syncEditor(lineNumber);
		                return editor;
		            }
		        	WorkbenchUtils.moveLine(part, lineNumber);
				}
		    }
        } else if(obj instanceof IExternalContainer) {
			IContainerElement container = 
				((IExternalContainer)obj).getExternalContainer();
			if(container != null) {
			    return openDiconEditor(container.getStorage());
			}
		} else if(obj instanceof IInternalContainer) {
		    IDiconElement element = ((IInternalContainer)obj).getElement();
		    IStorage file = element.getStorage();
			if(file != null) {
			    IStorage source = null;
	            if(part != null) {
	                source = WorkbenchUtils.getInputResource(part);
	            }
	            if(!file.equals(source)) {
	        		int lineNumber = element.getStartLine();
	        		DiconEditor editor = WorkbenchUtils.openDiconEditor(file);
	        		WorkbenchUtils.moveLine(editor, lineNumber);
//	        		editor.getOutlinePage().syncEditor(lineNumber);
	                return editor;
	            }		    
		        if(element.getElementName().equals(DICON_TAG_COMPONENT) &&
		                StringUtils.existValue(((IComponentElement)
		                        element).getComponentClassName())) {
		            IRtti rtti = (IRtti)element.getAdapter(IRtti.class);
		            if(rtti != null) {
	                    return openJavaEditor(rtti.getType());
		            }
		        } else {
		            IRtti rtti = (IRtti)element.getAdapter(IRtti.class);
		            if(rtti != null) {
			            IComponentElement injected = 
			                (IComponentElement)rtti.getAdapter(IComponentElement.class);
				        if(injected != null) {
				            IStorage injectedFile = injected.getStorage();
				            DiconEditor editor;
				            if(injectedFile != null) {
				                if(!injectedFile.equals(source)) {
					                editor = WorkbenchUtils.openDiconEditor(injectedFile);
//						        		editor.getOutlinePage().syncEditor(injected.getStartLine());
					            } else {
					                editor = (DiconEditor)part;
					            }
				                WorkbenchUtils.moveLine(editor, injected.getStartLine());
				                return editor;
				            }
				        }
		            }
		        }
			}
		}
        return part;
    }
    
    public static void moveLine(IEditorPart part, int lineNumber) {
        if((part != null) && (lineNumber > 0)) {
            try {
                IStorage file = getInputResource(part); 
	            if((file != null) && (file instanceof IFile)) {
					IMarker marker = ((IFile)file).createMarker(IMarker.TEXT);
					marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
					IDE.gotoMarker(part, marker);
					marker.delete();
	            }
	        } catch (CoreException e) {
	            KijimunaUI.reportException(e);
	        }
        }
    }
    
    public static IWorkbenchPart getWorkbenchPart() {
    	IWorkbenchPage page = getWorkbenchPage();
    	if(page != null) {
    		return page.getActivePart();
    	}
    	return null;
    }
    
    public static IEditorPart getActiveEditor() {
		IWorkbenchPage page = WorkbenchUtils.getWorkbenchPage();
		if(page != null) {
			return  page.getActiveEditor();
		}
		return null;
    }
    
    public static IProject getCurrentProject(ISelection selection) {
    	if (selection instanceof IStructuredSelection) {
    		for(Iterator it = ((IStructuredSelection)selection).iterator(); it.hasNext();) {
    			Object obj = it.next();
    			if (obj instanceof IAdaptable) {
    				IAdaptable adaptable = (IAdaptable)obj;
    				IResource resource = (IResource)adaptable.getAdapter(IResource.class);
    				if(resource != null) {
    					return resource.getProject();
    				}
    			}
    		}
    	}
    	return null;
    }
    
    public static Object getFirstSelectedElement(ISelection selection) {
	    if(selection instanceof IStructuredSelection) {
	        return ((IStructuredSelection)selection).getFirstElement();
	    }
	    return null;
    }
    
    public static String[] getAllWorkbenchEncodings() {
        List list = new ArrayList();
        for(int i = 0; i < ECLIPSE_ENCODINGS.length; i++) {
            list.add(ECLIPSE_ENCODINGS[i]);
        }
		String defaultEnc = System.getProperty("file.encoding", "UTF-8");
        if(!list.contains(defaultEnc)) {
        	list.add(defaultEnc);
        }
        String eclipseEnc = getWorkbenchEncoding();
        if(StringUtils.existValue(eclipseEnc) && !list.contains(eclipseEnc)) {
            list.add(eclipseEnc);
        }
        Collections.sort(list);
        return (String[])list.toArray(new String[list.size()]);
    }
    
    public static String getWorkbenchEncoding() {
        String enc = ResourcesPlugin.getPlugin().getPluginPreferences(
        		).getString(ResourcesPlugin.PREF_ENCODING);
        if(StringUtils.noneValue(enc)) {
            enc = System.getProperty("file.encoding", "UTF-8");
        }
        return enc;
    }
}
