package org.seasar.kijimuna.ui.internal.editor.dicon;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

public class DiconEditorContributor extends MultiPageEditorActionBarContributor {

	private TextEditorActionContributor contributer = new TextEditorActionContributor();

	public DiconEditorContributor() {
		super();
	}

	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		contributer.init(bars, page);
	}

	public void setActivePage(IEditorPart activeEditor) {
	}

	public void setActiveEditor(IEditorPart part) {
		if (part instanceof DiconEditor) {
			part = ((DiconEditor) part).getSourceEditor();
			contributer.setActiveEditor(part);
		}
		super.setActiveEditor(part);
	}

	public void dispose() {
		contributer.dispose();
		super.dispose();
	}

}
