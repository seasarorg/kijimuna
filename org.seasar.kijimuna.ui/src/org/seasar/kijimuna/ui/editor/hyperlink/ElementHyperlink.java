package org.seasar.kijimuna.ui.editor.hyperlink;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.internal.provider.dicon.IInternalContainer;
import org.seasar.kijimuna.ui.util.WorkbenchUtils;

public class ElementHyperlink implements IHyperlink {

	private IRegion hyperlinkRegion;
	private IInternalContainer item;

	/**
	 * Creates a new hyperlink.
	 * 
	 * @param xmlRegion
	 */
	public ElementHyperlink(XmlRegion xmlRegion, IInternalContainer element) {
		Assert.isNotNull(xmlRegion);

		this.hyperlinkRegion = xmlRegion.getHyperlinkRegion();
		this.item = element;
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
		WorkbenchUtils.showSource(WorkbenchUtils.getActiveEditor(), item);
	}

}
