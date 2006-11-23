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
package org.seasar.kijimuna.core.util;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kijimuna.core.dtd.IAttributeDef;
import org.seasar.kijimuna.core.dtd.IElementDef;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 */
public class DtdUtils {

	public static IAttributeDef[] getRequiredAttributes(IElementDef element) {
		IAttributeDef[] attributes = element.getAttributes();
		List requiredAttributes = new ArrayList();
		for (int i = 0; i < attributes.length; i++) {
			IAttributeDef attribute = attributes[i];
			if (attribute.getDecl().equals(IAttributeDef.REQUIRED)) {
				requiredAttributes.add(attribute);
			}
		}
		return (IAttributeDef[]) requiredAttributes
				.toArray(new IAttributeDef[requiredAttributes.size()]);
	}

}
