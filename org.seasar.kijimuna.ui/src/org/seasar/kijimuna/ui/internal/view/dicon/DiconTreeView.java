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
package org.seasar.kijimuna.ui.internal.view.dicon;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.ui.internal.provider.dicon.DiconContentProvider;
import org.seasar.kijimuna.ui.internal.provider.dicon.DiconLabelProvider;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.walker.ViewContent;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconTreeView extends ViewPart implements ISelectionChangedListener,
		IPartListener, IDoubleClickListener {

	private TreeViewer viewer;
	private ISelectionProvider selectionProvider;
	private IWorkbenchPart focusPart;
	private IProject currentProject;

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new DiconContentProvider());
		viewer.setLabelProvider(new DiconLabelProvider());
		viewer.setAutoExpandLevel(1);
		viewer.addDoubleClickListener(this);
		getSite().setSelectionProvider(viewer);
		getViewSite().setSelectionProvider(viewer);
		initInput();
		getSite().getPage().addPartListener(this);
	}

	private void initInput() {
		IWorkbenchPart part = WorkbenchUtils.getActiveEditor();
		if (part == null) {
			part = WorkbenchUtils.getWorkbenchPart();
		}
		partActivated(part);
	}

	private void setInput(IProject project) {
		if (project != null) {
			if (!project.equals(currentProject)) {
				currentProject = project;
				viewer.setInput(new ViewContent(project));
			}
		}
	}

	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public void dispose() {
		if (selectionProvider != null) {
			selectionProvider.removeSelectionChangedListener(this);
		}
		getSite().getPage().removePartListener(this);
		super.dispose();
	}

	public void doubleClick(DoubleClickEvent event) {
		Object obj = WorkbenchUtils.getFirstSelectedElement(event.getSelection());
		WorkbenchUtils.showSource(WorkbenchUtils.getActiveEditor(), obj);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		if (focusPart == this) {
			IEditorPart editorPart = WorkbenchUtils.getActiveEditor();
			if (editorPart != null) {
				IStorage displayFile = WorkbenchUtils.getInputResource(editorPart);
				if (displayFile != null) {
					Object obj = WorkbenchUtils.getFirstSelectedElement(event
							.getSelection());
					if (obj instanceof IInternalContainer) {
						IDiconElement element = ((IInternalContainer) obj).getElement();
						if (element != null) {
							IStorage selectedFile = (IStorage) element
									.getAdapter(IStorage.class);
							if (displayFile.equals(selectedFile)) {
								WorkbenchUtils.moveLine(editorPart, element
										.getStartLine());
							}
						}
					}
				}
			}
		} else {
			if (selectionProvider != null) {
				setInput(WorkbenchUtils.getCurrentProject(selectionProvider
						.getSelection()));
			}
		}
	}

	public void partActivated(IWorkbenchPart part) {
		if ((part != null) && (part != focusPart)) {
			if (selectionProvider != null) {
				selectionProvider.removeSelectionChangedListener(this);
				selectionProvider = null;
			}
			if (focusPart != part) {
				focusPart = part;
				if (focusPart != null) {
					if (focusPart instanceof IEditorPart) {
						IEditorPart editorPart = (IEditorPart) focusPart;
						IStorage storage = WorkbenchUtils.getInputResource(editorPart);
						if (storage instanceof IFile) {
							IFile file = (IFile) storage;
							setInput(file.getProject());
							return;
						}
						// currentProject
						return;
					}
					selectionProvider = focusPart.getSite().getSelectionProvider();
					if (selectionProvider != null) {
						selectionProvider.addSelectionChangedListener(this);
						setInput(WorkbenchUtils.getCurrentProject(selectionProvider
								.getSelection()));
						return;
					}
				}
			}
			setInput(null);
		}
	}

	public void partClosed(IWorkbenchPart part) {
		if (part == focusPart) {
			if (selectionProvider != null) {
				selectionProvider.removeSelectionChangedListener(this);
				selectionProvider = null;
			}
			focusPart = null;
		}
	}

	public void partOpened(IWorkbenchPart part) {
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

}
