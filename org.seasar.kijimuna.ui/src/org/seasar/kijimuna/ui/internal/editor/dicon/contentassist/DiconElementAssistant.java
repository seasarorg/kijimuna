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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.core.dicon.DiconOgnlRtti;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IDiconElement;
import org.seasar.kijimuna.core.dicon.model.IIncludeElement;
import org.seasar.kijimuna.core.dicon.model.IMethodElement;
import org.seasar.kijimuna.core.dtd.IDtd;
import org.seasar.kijimuna.core.parser.IElement;
import org.seasar.kijimuna.core.parser.IParseResult;
import org.seasar.kijimuna.core.rtti.HasErrorRtti;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.IRttiFieldDescriptor;
import org.seasar.kijimuna.core.rtti.IRttiMethodDesctiptor;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.editor.contentassist.JavaPackageProposalCreator;
import org.seasar.kijimuna.ui.editor.contentassist.ProposalComparator;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlAssistProcessor;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlElementAssistant;
import org.seasar.kijimuna.ui.editor.contentassist.xml.XmlRegion;
import org.seasar.kijimuna.ui.util.CoreUtils;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DiconElementAssistant extends XmlElementAssistant implements ConstUI {

	private RttiLoader loader;

	public DiconElementAssistant(XmlAssistProcessor processor, IDtd dtd,
			XmlRegion xmlRegion, String rootElementName) {
		super(processor, dtd, xmlRegion, rootElementName);
	}

	private RttiLoader getRttiLoader() {
		if (loader == null) {
			IFile file = getXmlRegion().getFile();
			loader = new RttiLoader(file.getProject().getName(), true);
		}
		return loader;
	}

	private IRtti execOGNL(IContainerElement container, String el) {
		DiconOgnlRtti ognlRtti = new DiconOgnlRtti(getRttiLoader());
		return ognlRtti.getValue(container, el);
	}

	private IRtti execMethodOGNL(IComponentElement component, String el) {
		DiconOgnlRtti ognlRtti = new DiconOgnlRtti(getRttiLoader());
		ognlRtti.setComponent(component);
		return ognlRtti.getValue(component.getContainerElement(), el);
	}

	private String getLastPrefix(String prefix) {
		int last = prefix.lastIndexOf('.');
		return last < 0 ? prefix : prefix.substring(last + 1);
	}
	
	private List getComponentProposals(IContainerElement container,
			String prefix, int offset) {
		List proposals = new ArrayList();
		List componentList = container.getComponentList();
		for (int i = 0; i < componentList.size(); i++) {
			IComponentElement comp = (IComponentElement) componentList.get(i);
			String name = comp.getComponentName();
			if (StringUtils.existValue(name) && isMatch(name, getLastPrefix(
					prefix))) {
				ICompletionProposal proposal = createProposal(name, name,
						getLastPrefix(prefix), offset, name.length(),
						IMAGE_ICON_COMPONENT);
				proposals.add(proposal);
			}
		}
		Collections.sort(proposals, new ProposalComparator());
		return proposals;
	}
	
	private List getNamespaceProposals(IContainerElement container,
			String prefix, int offset) {
		Map proposals = new HashMap();
		List includes = container.getIncludeList();
		for (int i = 0; i < includes.size(); i++) {
			IIncludeElement include = (IIncludeElement) includes.get(i);
			String namespace = include.getChildContainer().getNamespace();
			if (StringUtils.existValue(namespace) && isMatch(namespace,
					getLastPrefix(prefix))) {
				if (proposals.containsKey(namespace)) {
					continue;
				}
				ICompletionProposal proposal = createProposal(namespace,
						namespace + " - " + include.getPath(),
						getLastPrefix(prefix), offset, namespace.length(),
						IMAGE_ICON_CONTAINER);
				proposals.put(namespace, proposal);
			}
		}
		List ret = new ArrayList(proposals.values());
		Collections.sort(ret, new ProposalComparator());
		return ret;
	}
	
	private List getOGNLProposals(IDiconElement element, String prefix, int offset) {
		List proposals = new ArrayList();
		if (prefix.length() == 0) {
			String item = "@";
			proposals.add(createProposal(item, item, prefix, offset, item.length(),
					IMAGE_ICON_EXPRESSION));
		}
		if (element instanceof IMethodElement) {
			String[] items = new String[] {
					"#err", "#out", "#self"
			};
			for (int i = 0; i < items.length; i++) {
				if (isMatch(items[i], prefix)) {
					proposals.add(createProposal(items[i], items[i], prefix, offset,
							items[i].length(), IMAGE_ICON_EXPRESSION));
				}
			}
		}
		String item = "new ";
		if (isMatch(item, prefix)) {
			proposals.add(createProposal(item, item, prefix, offset, item.length(),
					IMAGE_ICON_EXPRESSION));
		}
		return proposals;
	}

	private List getMemberProposals(IRtti rtti, String fixed, String member,
			boolean isStatic, String prefix, int offset) {
		List proposals = new ArrayList();
		Pattern pattern = Pattern.compile(member + ".*");
		IRttiMethodDesctiptor[] methods = rtti.getMethods(pattern);
		for (int i = 0; i < methods.length; i++) {
			if (!isStatic || methods[i].isStatic()) {
				String methodName = ModelUtils.getMethodDisplay(methods[i], false);
				String replaceStr = fixed + methods[i].getMethodName() + "()";
				int posDeferencial = (methods[i].getArgs().length == 0) ? 0 : 1;
				proposals.add(createProposal(replaceStr, methodName, prefix, offset,
						replaceStr.length() - posDeferencial, IMAGE_ICON_JAVA_METHOD));
			}
		}
		IRttiFieldDescriptor[] fields = rtti.getFields(pattern);
		for (int i = 0; i < fields.length; i++) {
			if (!isStatic || fields[i].isStatic()) {
				String fieldName = fields[i].getName();
				String replaceStr = fixed + fieldName;
				proposals.add(createProposal(replaceStr, fieldName, prefix, offset,
						replaceStr.length(), IMAGE_ICON_JAVA_FIELD));
			}
		}
		Collections.sort(proposals, new ProposalComparator());
		return proposals;
	}

	private void addJavaPackageProposals(List proposals, IProject project, String prefix,
			int offset, String postFix, int postLength) {
		JavaPackageProposalCreator creator = new JavaPackageProposalCreator(this,
				project, prefix, offset, postFix, postLength);
		ICompletionProposal[] packageProposals = creator.getJavaPackageProposals();
		for (int i = 0; i < packageProposals.length; i++) {
			proposals.add(packageProposals[i]);
		}
	}

	private boolean isMethodArg(String prefix) {
		XmlRegion xmlRegion = getXmlRegion();
		String regionText = xmlRegion.getText();
		char c = 0;
		for (int i = xmlRegion.getCursorOffset() - prefix.length() - 1; i > -1; i--) {
			c = regionText.charAt(i);
			if (StringUtils.isWhitespace(c)) {
				continue;
			}
			if (c == ',' || c == '(') {
				return true;
			}
			return false;
		}
		return false;
	}

	private boolean hasNewOperator(String prefix) {
		XmlRegion xmlRegion = getXmlRegion();
		String regionText = xmlRegion.getText();
		char c = 0;
		StringBuffer buffer = null;
		for (int i = xmlRegion.getCursorOffset() - prefix.length() - 1; i > -1; i--) {
			c = regionText.charAt(i);
			if (StringUtils.isWhitespace(c)) {
				buffer = new StringBuffer();
			} else {
				if (buffer != null && Character.isJavaIdentifierPart(c)) {
					buffer.insert(0, c);
				} else {
					break;
				}
			}
		}
		if (buffer != null) {
			String operator = buffer.toString();
			return "new".equals(operator);
		}
		return false;
	}

	protected void addBodyProposals(List superProposals, String prefix, int offset) {
		IElement lastStack = getParentElement();
		List proposals = new ArrayList();
		if (lastStack instanceof IDiconElement) {
			IDiconElement element = (IDiconElement) lastStack;
			if (prefix.startsWith("@")) {
				int close = prefix.lastIndexOf('@');
				if (close > 1) {
					String className = prefix.substring(1, close);
					String member = "";
					if (close < prefix.length() - 1) {
						member = prefix.substring(close + 1);
					}
					IRtti rtti = getRttiLoader().loadRtti(className);
					proposals.addAll(getMemberProposals(rtti, "@" + className + "@",
							member, true, prefix, offset));
				} else {
					String packagePrefix = prefix.substring(1);
					addJavaPackageProposals(proposals, element.getProject(),
							packagePrefix, offset, "@", 1);
				}
			} else {
				if (hasNewOperator(prefix)) {
					// reset super class's list.
					superProposals.clear();
					addJavaPackageProposals(proposals, element.getProject(), prefix,
							offset, "()", 1);
				} else {
					if (isMethodArg(prefix)) {
						superProposals.clear();
					}
					int pos = prefix.lastIndexOf('.');
					if (pos > 0) {
						String el = prefix.substring(0, pos);
						String elPrefix = "";
						if (pos < prefix.length() - 1) {
							elPrefix = prefix.substring(pos + 1, prefix.length());
						}
						if (el.startsWith("#")) {
							IRtti elRtti;
							if (element instanceof IMethodElement) {
								elRtti = execMethodOGNL((IComponentElement) element
										.getParent(), el);
								if ((elRtti != null) && !(elRtti instanceof HasErrorRtti)) {
									proposals.addAll(getMemberProposals(elRtti, el + ".",
											elPrefix, false, prefix, offset));
								}
							}
						} else {
							IRtti elRtti;
							if (element instanceof IMethodElement) {
								elRtti = execMethodOGNL((IComponentElement) element
										.getParent(), el);
							} else {
								elRtti = execOGNL(getContainerElement(), el);
							}
							if ((elRtti != null) && !(elRtti instanceof HasErrorRtti)) {
								if (elRtti.getQualifiedName().equals(
										MODEL_INTERFACE_S2CONTAINER)) {
									IContainerElement container = (IContainerElement)
											elRtti.getAdapter(IContainerElement.class);
									if (container != null) {
										proposals.addAll(getComponentProposals(
												container, prefix, offset));
										proposals.addAll(getNamespaceProposals(
												container, prefix, offset));
									}
								}
								proposals.addAll(getMemberProposals(elRtti, el + ".",
										elPrefix, false, prefix, offset));
							}
						}
					} else if (pos == -1) {
						IContainerElement container = getContainerElement();
						proposals
								.addAll(getComponentProposals(container, prefix, offset));
						proposals
								.addAll(getNamespaceProposals(container, prefix, offset));
						proposals.addAll(getOGNLProposals(element, prefix, offset));
					}
				}
			}
		}
		superProposals.addAll(proposals);
	}
	
	private IContainerElement getContainerElement() {
		XmlRegion region = getXmlRegion();
		IParseResult result = CoreUtils.parse(region.getStringToEnd(), region
				.getFile());
		return (IContainerElement) result.getRootElement();
	}

}
