package org.seasar.kijimuna.ui.editor.hyperlink;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

public class ClassHyperlink implements IHyperlink {

	private XmlRegion xmlRegion;
	private String hyperlinkText;
	private IRegion hyperlinkRegion;

	/**
	 * Creates a new hyperlink.
	 * 
	 * @param xmlRegion
	 */
	public ClassHyperlink(XmlRegion xmlRegion) {
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
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IType type = javaProject.findType(hyperlinkText.trim());
			WorkbenchUtils.openJavaEditor(type);
		} catch (JavaModelException e) {
		}
	}

}
