package org.seasar.kijimuna.ui.editor.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.seasar.kijimuna.core.dicon.model.IArgElement;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.dicon.model.IComponentHolderElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.internal.provider.dicon.walker.ContentItem;
import org.seasar.kijimuna.ui.util.CoreUtils;

public class HyperlinkDetector implements IHyperlinkDetector, ConstUI {

	private ITextViewer fTextViewer;

	XmlAssistProcessor fProcessor;

	/**
	 * Creates a new hyperlink detector.
	 * 
	 * @param textViewer
	 *            the text viewer in which to detect the hyperlink
	 */
	public HyperlinkDetector(ITextViewer textViewer,
			XmlAssistProcessor processor) {
		Assert.isNotNull(textViewer);
		fTextViewer = textViewer;
		fProcessor = processor;
	}

	protected IHyperlink getXmlDeclHyperlink(XmlRegion xmlRegion) {
		return null;
	}

	protected IHyperlink getDocDeclHyperlink(XmlRegion xmlRegion) {
		return null;
	}

	protected IHyperlink getElementHyperlink(XmlRegion xmlRegion) {
		String stringToOffset = xmlRegion.getStringToOffset();
		IFile file = xmlRegion.getFile();
		IParseResult result = CoreUtils.parse(stringToOffset, file);
		IElement parentElement = result.getLastStackElement();
		if (parentElement instanceof IAspectElement
				|| parentElement instanceof IPropertyElement
				|| parentElement instanceof IArgElement) {
			String body = xmlRegion.getText();
			if (body.length() > 0 && body.charAt(0) != '\"' && body.startsWith("new ") == false) {
				parentElement.setBody(body);
				ContentItem contentItem = new ContentItem(
						(IComponentHolderElement) parentElement, null, true);
				return new ElementHyperlink(xmlRegion, contentItem);
			}
		}
		return null;
	}

	protected IHyperlink getElementClosingHyperlink(XmlRegion xmlRegion) {
		return null;
	}

	protected IHyperlink getAttributeHyperlink(XmlRegion xmlRegion) {
		return null;
	}

	protected IHyperlink getAttributeValueHyperlink(XmlRegion xmlRegion) {
		String attributeName = xmlRegion.getAttributeName();
		String elementName = xmlRegion.getElementName();
		if (DICON_TAG_COMPONENT.equals(elementName)
				&& DICON_ATTR_CLASS.equals(attributeName)) {
			return new ClassHyperlink(xmlRegion);
		} else if (DICON_TAG_INCLUDE.equals(elementName)
				&& DICON_ATTR_PATH.equals(attributeName)) {
			return new IncludeHyperlink(xmlRegion);
		}
		return null;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || fTextViewer == null)
			return null;

		IDocument document = fTextViewer.getDocument();
		if (document == null) {
			return null;
		}
		int offset = region.getOffset();

		XmlRegion xmlRegion = null;
		try {
			xmlRegion = fProcessor.getXmlRegiton(document, offset);
		} catch (BadLocationException ex) {
			return null;
		}

		IHyperlink hyperlink = null;
		if (xmlRegion.getProposalMode() == PROPOSAL_MODE_XML_DECL) {
			hyperlink = getXmlDeclHyperlink(xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_DOC_DECL) {
			hyperlink = getDocDeclHyperlink(xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ELEMENT) {
			hyperlink = getElementHyperlink(xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ELEMENT_CLOSING) {
			hyperlink = getElementClosingHyperlink(xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ATTRIBUTE) {
			hyperlink = getAttributeHyperlink(xmlRegion);
		} else if (xmlRegion.getProposalMode() == PROPOSAL_MODE_ATTRIBUTE_VALUE) {
			hyperlink = getAttributeValueHyperlink(xmlRegion);
		}

		if (hyperlink != null && hyperlink.getHyperlinkRegion() != null) {
			return new IHyperlink[] { hyperlink };
		}
		return null;
	}

}
