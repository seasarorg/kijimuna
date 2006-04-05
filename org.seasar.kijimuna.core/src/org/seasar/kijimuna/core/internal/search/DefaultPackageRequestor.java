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
package org.seasar.kijimuna.core.internal.search;

import java.util.Collection;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import org.seasar.kijimuna.core.search.IPackageRequestor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DefaultPackageRequestor implements IPackageRequestor {

	private Collection collection;

	public DefaultPackageRequestor(Collection collection) {
		this.collection = collection;
	}

	public void acceptPackage(IPackageFragment pack, boolean archive) {
		collection.add(pack);
	}

	public void acceptType(IType type) {
		collection.add(type);
	}

}
