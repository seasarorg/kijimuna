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

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ProposalComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		if ((o1 instanceof ICompletionProposal) && (o2 instanceof ICompletionProposal)) {
			ICompletionProposal proposal1 = (ICompletionProposal) o1;
			ICompletionProposal proposal2 = (ICompletionProposal) o2;
			return proposal1.getDisplayString().compareTo(proposal2.getDisplayString());
		}
		return -1;
	}

}
