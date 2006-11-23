package org.seasar.kijimuna.ui.editor.hyperlink;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.seasar.kijimuna.core.util.ProjectUtils;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

public class IncludeHyperlink implements IHyperlink {

	private XmlRegion xmlRegion;
	private String hyperlinkText;
	private IRegion hyperlinkRegion;

	/**
	 * Creates a new hyperlink.
	 * 
	 * @param xmlRegion
	 */
	public IncludeHyperlink(XmlRegion xmlRegion) {
		Assert.isNotNull(xmlRegion);

		this.xmlRegion = xmlRegion;
		this.hyperlinkText = xmlRegion.getHyperlinkText();
		this.hyperlinkRegion = xmlRegion.getHyperlinkRegion();
	}

	public IRegion getHyperlinkRegion() {
		return hyperlinkRegion;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		IProject project = xmlRegion.getFile().getProject();
		IStorage storage = ProjectUtils.findDiconStorage(project, hyperlinkText.trim());
		WorkbenchUtils.openDiconEditor(storage);
	}

}
