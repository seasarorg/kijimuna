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
package org.seasar.kijimuna.ui.editor.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.seasar.kijimuna.core.search.IPackageRequestor;
import org.seasar.kijimuna.core.search.JavaPackageSearcher;
import org.seasar.kijimuna.core.util.StringUtils;
import org.seasar.kijimuna.ui.ConstUI;
import org.seasar.kijimuna.ui.KijimunaUI;
import org.seasar.kijimuna.ui.editor.contentassist.xml.AbstractXmlAssistant;

public class JavaPackageProposalCreator implements IPackageRequestor, ConstUI {

	private AbstractXmlAssistant assistant;
	private List packageResult;
	private List typeResult;
	private String prefix;
	private int offset;
	private Set packageSet;
	private Set typeSet;
	private IProject project;
	private String postFix;
	private int postLength;

	public JavaPackageProposalCreator(AbstractXmlAssistant assistant, IProject project,
			String prefix, int offset, String postFix, int postLength) {
		this.assistant = assistant;
		this.project = project;
		this.prefix = prefix;
		this.offset = offset;
		this.postFix = postFix;
		this.postLength = postLength;
		packageSet = new HashSet();
		typeSet = new HashSet();
	}

	private boolean isUpperPrefix(String prefix) {
		int lastDot = prefix.lastIndexOf('.');
		String dotAfterPrefix = prefix;
		if (lastDot > 0) {
			dotAfterPrefix = prefix.substring(lastDot + 1);
		}
		if (dotAfterPrefix.length() > 0
				&& Character.isLowerCase(dotAfterPrefix.charAt(0))) {
			return false;
		} else {
			return true;
		}
	}

	public ICompletionProposal[] getJavaPackageProposals() {
		packageResult = new ArrayList();
		typeResult = new ArrayList();
		new JavaPackageSearcher(project).searchPackagesAndTypes(prefix, this);
		Collections.sort(packageResult);
		Collections.sort(typeResult);
		List result = new ArrayList();
		if (this.isUpperPrefix(prefix)) {
			result.addAll(typeResult);
			result.addAll(packageResult);
		} else {
			result.addAll(packageResult);
			result.addAll(typeResult);
		}
		ICompletionProposal[] ret = new ICompletionProposal[result.size()];
		for (int i = 0; i < result.size(); i++) {
			JavaPackageProposalHolder item = (JavaPackageProposalHolder) result.get(i);
			ret[i] = item.getProposal();
		}
		return ret;
	}

	public void acceptPackage(IPackageFragment pack, boolean archive) {
		String packageName = pack.getElementName();
		if (StringUtils.existValue(packageName) && !packageSet.contains(packageName)) {
			String imageName;
			if (archive) {
				imageName = IMAGE_ICON_JAVA_JAR_PACKAGE;
			} else {
				imageName = IMAGE_ICON_JAVA_PACKAGE;
			}
			String replaceText = packageName;
			// String replaceText = packageName + ".";
			ICompletionProposal tempProposal = assistant.createProposal(replaceText,
					packageName, prefix, offset, replaceText.length(), imageName);
			packageResult.add(new JavaPackageProposalHolder(pack, tempProposal));
			packageSet.add(packageName);
		}
	}

	public void acceptType(IType type) {
		String className = type.getFullyQualifiedName();
		String imageName;
		try {
			if (!typeSet.contains(className)) {
				if (type.isInterface()) {
					imageName = IMAGE_ICON_JAVA_INTERFACE;
				} else {
					imageName = IMAGE_ICON_JAVA_CLASS;
				}
				String replaceText = className;
				String displayText = type.getElementName() + " - " + className;
				int cursorPos = replaceText.length();
				if (StringUtils.existValue(postFix)) {
					replaceText += postFix;
					cursorPos += postLength;
				}
				ICompletionProposal tempProposal = assistant.createProposal(replaceText,
						displayText, prefix, offset, cursorPos, imageName);
				typeResult.add(new JavaPackageProposalHolder(type, tempProposal));
				typeSet.add(className);
			}
		} catch (JavaModelException e) {
			KijimunaUI.reportException(e);
		}
	}

}
