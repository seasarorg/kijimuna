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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.project.IProjectRecordChangeListener;
import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.internal.provider.dicon.DiconContentProvider;
import org.seasar.kijimuna.ui.internal.provider.dicon.DiconLabelProvider;
import org.seasar.kijimuna.ui.internal.provider.dicon.IContentWalker;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.internal.provider.dicon.walker.OutlineContent;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconOutlinePage extends ContentOutlinePage implements
		IProjectRecordChangeListener, IDoubleClickListener, ConstUI {

	private DiconEditor part;
	private TreeViewer viewer;

	public DiconOutlinePage(DiconEditor part) {
		this.part = part;
	}

	private void setInput() {
		IStorage storage = WorkbenchUtils.getInputResource(part);
		if (storage != null) {
			IProject project;
			if (storage instanceof IFile) {
				project = ((IFile) storage).getProject();
			} else {
				project = ProjectUtils.getProjectFromDiconStorage(storage);
			}
			if (project != null) {
				viewer.setInput(new OutlineContent(project, storage));
				return;
			}
		}
		viewer.setInput(null);
	}

	private DiconNature getNature() {
		IStorage storage = WorkbenchUtils.getInputResource(part);
		if (storage != null) {
			if (storage instanceof IFile) {
				IProject project = ((IFile) storage).getProject();
				return DiconNature.getInstance(project);
			}
		}
		return null;
	}

	private IContentWalker findLine(IContentWalker walker, int lineNumber) {
		IElement element = (IElement) walker.getAdapter(IElement.class);
		if (element != null) {
			int startLine = element.getStartLine();
			int endLine = element.getEndLine();
			if ((startLine <= lineNumber) && (lineNumber <= endLine)) {
				return walker;
			}
		}
		Object[] obj = walker.getChildren();
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof IContentWalker) {
				return findLine((IContentWalker) obj[i], lineNumber);
			}
		}
		return null;
	}

	public void syncEditor(int lineNumber) {
		OutlineContent content = (OutlineContent) viewer.getInput();
		Object[] obj = content.getTopLevelItems();
		if (obj[1] instanceof IContentWalker) {
			IContentWalker walker = (IContentWalker) obj[1];
			IContentWalker selected = findLine(walker, lineNumber);
			if (selected != null) {
				ISelection selection = new StructuredSelection(selected);
				viewer.setSelection(selection, true);
			}
		}
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setContentProvider(new DiconContentProvider());
		viewer.setLabelProvider(new DiconLabelProvider());
		viewer.setAutoExpandLevel(2);
		viewer.addDoubleClickListener(this);
		setInput();
		DiconNature nature = getNature();
		if (nature != null) {
			nature.getModel().addRecordChangeListener(this);
		}
	}

	public void dispose() {
		DiconNature nature = getNature();
		if (nature != null) {
			nature.getModel().removeRecordChangeListener(this);
		}
		super.dispose();
	}

	public void finishChanged() {
		final Control control = viewer.getTree();
		Display display = control.getDisplay();
		display.asyncExec(new Runnable() {

			public void run() {
				if (control.isDisposed()) {
					return;
				}
				setInput();
			}
		});
	}

	public void doubleClick(DoubleClickEvent event) {
		Object obj = WorkbenchUtils.getFirstSelectedElement(event.getSelection());
		WorkbenchUtils.showSource(part, obj);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		Object obj = WorkbenchUtils.getFirstSelectedElement(event.getSelection());
		if (obj instanceof IInternalContainer) {
			IDiconElement element = ((IInternalContainer) obj).getElement();
			WorkbenchUtils.moveLine(part, element.getStartLine());
		}
	}

}
