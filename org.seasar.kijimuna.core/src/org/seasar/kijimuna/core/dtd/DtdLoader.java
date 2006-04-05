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
package org.seasar.kijimuna.core.dtd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDChoice;
import com.wutka.dtd.DTDContainer;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDEmpty;
import com.wutka.dtd.DTDEnumeration;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDMixed;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDPCData;
import com.wutka.dtd.DTDParser;

import org.seasar.kijimuna.core.KijimunaCore;
import org.seasar.kijimuna.core.internal.dtd.DefaultAttributeDef;
import org.seasar.kijimuna.core.internal.dtd.DefaultDtd;
import org.seasar.kijimuna.core.internal.dtd.DefaultElementDef;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DtdLoader {

	public static IDtd loadDtd(URL resource) {
		try {
			return loadDtd(resource.openStream());
		} catch (IOException e) {
			throw new DtdException(KijimunaCore.getResourceString("dtd.DtdLoader.1",
					new Object[] {
						resource
					}), e);
		}
	}

	private static IDtd loadDtd(InputStream stream) throws IOException {
		DTDParser parser = new DTDParser(new InputStreamReader(stream));
		DTD wutkaDtd = parser.parse();
		Vector wutkaElements = wutkaDtd.getItemsByType(DTDElement.class);
		DefaultDtd dtd = new DefaultDtd();
		setElement(wutkaElements, dtd);
		setChildElement(wutkaElements, dtd);
		return dtd;
	}

	private static void setElement(Vector wutkaElements, DefaultDtd dtd) {
		for (Iterator iter = wutkaElements.iterator(); iter.hasNext();) {
			DTDElement wutkaElement = (DTDElement) iter.next();
			IElementDef element = toElement(wutkaElement);
			dtd.addElement(element);
		}
	}

	private static void setChildElement(Vector wutkaElements, DefaultDtd dtd) {
		for (Iterator iter = wutkaElements.iterator(); iter.hasNext();) {
			DTDElement wutkaElement = (DTDElement) iter.next();
			IElementDef element = dtd.getElement(wutkaElement.getName());
			DTDItem item = wutkaElement.getContent();
			if (item instanceof DTDContainer) {
				DTDContainer sequence = (DTDContainer) item;
				for (Iterator it = sequence.getItemsVec().iterator(); it.hasNext();) {
					setChildChoiceElement(dtd, element, it.next());
				}
			}
		}
	}

	private static void setChildChoiceElement(DefaultDtd dtd, IElementDef element,
			Object item) {
		if (item instanceof DTDChoice) {
			DTDChoice choice = (DTDChoice) item;
			for (Iterator it = choice.getItemsVec().iterator(); it.hasNext();) {
				setChildChoiceElement(dtd, element, it.next());
			}
		} else if (item instanceof DTDName) {
			DTDName name = (DTDName) item;
			IElementDef childElement = dtd.getElement(name.getValue());
			element.addElement(childElement);
		}
	}

	private static IElementDef toElement(DTDElement wutkaElement) {
		boolean pcdata = false;
		boolean empty = false;
		DTDItem item = wutkaElement.getContent();
		if (item instanceof DTDMixed) {
			DTDMixed mix = (DTDMixed) item;
			DTDItem[] contents = mix.getItems();
			for (int i = 0; i < contents.length; i++) {
				if (contents[i] instanceof DTDPCData) {
					pcdata = true;
				}
			}
		} else if (item instanceof DTDEmpty) {
			empty = true;
		}
		DefaultElementDef element = new DefaultElementDef(wutkaElement.getName(), empty,
				pcdata);
		for (Iterator it = wutkaElement.attributes.values().iterator(); it.hasNext();) {
			DTDAttribute wutkaAttribute = (DTDAttribute) it.next();
			DefaultAttributeDef attribute = new DefaultAttributeDef(wutkaAttribute
					.getName(), wutkaAttribute.getDecl().name, wutkaAttribute
					.getDefaultValue(), toItems(wutkaAttribute.getType()));
			element.addAttribute(attribute);
		}
		return element;
	}

	private static String[] toItems(Object type) {
		if (type instanceof DTDEnumeration) {
			DTDEnumeration types = (DTDEnumeration) type;
			return types.getItems();
		}
		return new String[0];
	}

}
