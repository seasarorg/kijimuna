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
package org.seasar.kijimuna.ui.internal.editor.dicon.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.core.dicon.DiconNature;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.rtti.IRttiPropertyDescriptor;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.ui.editor.contentassist.JavaPackageProposalCreator;
import org.seasar.kijimuna.ui.editor.contentassist.ProposalComparator;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAttributeValueAssistant;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.util.CoreUtils;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconAttributeValueAssistant extends XmlAttributeValueAssistant {

	public DiconAttributeValueAssistant(XmlAssistProcessor processor, IDtd dtd,
			XmlRegion xmlRegion) {
		super(processor, dtd, xmlRegion);
	}

	public ICompletionProposal[] getCompletionProposal(String prefix, int offset) {
		String elementName = getXmlRegion().getElementName();
		String attributeName = getXmlRegion().getAttributeName();
		String stringToOffset = getXmlRegion().getStringToOffset();
		IFile file = getXmlRegion().getFile();
		if (DICON_TAG_COMPONENT.equals(elementName)
				&& DICON_ATTR_CLASS.equals(attributeName)) {
			return getClassCompletionProposal(file, prefix, offset);
		} else if (DICON_TAG_PROPERTY.equals(elementName)
				&& DICON_ATTR_NAME.equals(attributeName)) {
			IElement parent = getParentElement(stringToOffset, file);
			return getPropertyNameCompletionProposal(prefix, offset, parent);
		} else if ((DICON_TAG_INITMETHOD.equals(elementName) || DICON_TAG_DESTROYMETHOD
				.equals(elementName))
				&& DICON_ATTR_NAME.equals(attributeName)) {
			IElement parent = getParentElement(stringToOffset, file);
			return getMethodNameCompletionProposal(prefix, offset, parent);
		} else if (DICON_TAG_ASPECT.equals(elementName)
				&& DICON_ATTR_POINTCUT.equals(attributeName)) {
			IElement parent = getParentElement(stringToOffset, file);
			return getMethodNameCompletionProposal(prefix, offset, parent);
		} else if (DICON_TAG_INCLUDE.equals(elementName)) {
			return getIncludePathCompletionProposal(file, prefix, offset);
		} else {
			return super.getCompletionProposal(prefix, offset);
		}
	}

	private IElement getParentElement(String stringToOffset, IFile file) {
		IParseResult result = CoreUtils.parse(stringToOffset, file);
		return result.getLastStackElement();
	}

	private ICompletionProposal[] getClassCompletionProposal(IFile file, String prefix,
			int offset) {
		IProject project = file.getProject();
		JavaPackageProposalCreator creator = new JavaPackageProposalCreator(this,
				project, prefix, offset, null, 0);
		return creator.getJavaPackageProposals();
	}

	private ICompletionProposal[] getPropertyNameCompletionProposal(String prefix,
			int offset, IElement parentElement) {
		List proposals = new ArrayList();
		if (parentElement instanceof IComponentElement) {
			IRtti rtti = (IRtti) ((IComponentElement) parentElement)
					.getAdapter(IRtti.class);
			if (rtti != null) {
				IRttiPropertyDescriptor[] properties = rtti.getProperties(Pattern
						.compile(prefix + ".*"));
				for (int i = 0; i < properties.length; i++) {
					IRttiPropertyDescriptor property = properties[i];
					String propertyName = property.getName();
					String displayName = propertyName + " - "
							+ property.getType().getQualifiedName();
					ICompletionProposal tempProposal = createProposal(propertyName,
							displayName, prefix, offset, propertyName.length(),
							IMAGE_ICON_JAVA_METHOD);
					proposals.add(tempProposal);
				}
			}
		}
		Collections.sort(proposals, new ProposalComparator());
		return (ICompletionProposal[]) proposals
				.toArray(new ICompletionProposal[proposals.size()]);
	}

	private ICompletionProposal[] getMethodNameCompletionProposal(String prefix,
			int offset, IElement parentElement) {
		List proposals = new ArrayList();
		if (parentElement instanceof IComponentElement) {
			IRtti rtti = (IRtti) ((IComponentElement) parentElement)
					.getAdapter(IRtti.class);
			if (rtti != null) {
				IRttiMethodDesctiptor[] methods = rtti.getMethods(Pattern.compile(prefix
						+ ".*"));
				for (int i = 0; i < methods.length; i++) {
					String methodName = methods[i].getMethodName();
					String displayName = ModelUtils.getMethodDisplay(methods[i], false);
					ICompletionProposal tempProposal = createProposal(methodName,
							displayName, prefix, offset, methodName.length(),
							IMAGE_ICON_JAVA_METHOD);
					proposals.add(tempProposal);
				}
			}
		}
		Collections.sort(proposals, new ProposalComparator());
		return (ICompletionProposal[]) proposals
				.toArray(new ICompletionProposal[proposals.size()]);
	}

	private ICompletionProposal[] getIncludePathCompletionProposal(IFile file,
			String prefix, int offset) {
		DiconNature nature = DiconNature.getInstance(file.getProject());
		if (nature != null) {
			String[] paths = nature.getModel().getAllContainerPaths();
			List proposals = new ArrayList();
			for (int i = 0; i < paths.length; i++) {
				if (isMatch(paths[i], prefix)) {
					ICompletionProposal proposal = createProposal(paths[i], paths[i],
							prefix, offset, paths[i].length(), IMAGE_ICON_CONTAINER);
					proposals.add(proposal);
				}
			}
			Collections.sort(proposals, new ProposalComparator());
			return (ICompletionProposal[]) proposals
					.toArray(new ICompletionProposal[proposals.size()]);
		}
		return NO_PROPOSALS;
	}

}
