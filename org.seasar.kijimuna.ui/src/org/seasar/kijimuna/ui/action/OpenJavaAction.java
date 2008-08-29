package org.seasar.kijimuna.ui.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.internal.editor.dicon.DiconEditor;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

public class OpenJavaAction implements IWorkbenchWindowActionDelegate,
		IEditorActionDelegate, IObjectActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		IEditorPart editor = WorkbenchUtils.getActiveEditor();
		DiconEditor diconEditor = (DiconEditor) editor;
		ITextSelection textSelection = (ITextSelection) ((ITextEditor) diconEditor
				.getSourceEditor()).getSelectionProvider().getSelection();
		String selectedElementText = textSelection.getText();
		IFile diconFile = ((FileEditorInput) editor.getEditorInput()).getFile();
		IProject project = diconFile.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IType type = javaProject.findType(selectedElementText);
			WorkbenchUtils.openJavaEditor(type);
		} catch (JavaModelException e) {
			KijimunaUI.reportException(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
