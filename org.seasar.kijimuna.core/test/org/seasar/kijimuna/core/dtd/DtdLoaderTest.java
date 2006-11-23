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

import junit.framework.TestCase;

import org.seasar.kijimuna.core.KijimunaCore;

/**
 * @author Toshitaka Agata (Nulab, Inc.)
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class DtdLoaderTest extends TestCase {

	public void testLoadDtd() {
		IDtd dtd = DtdLoader.loadDtd(KijimunaCore.getEntry("/components.dtd"));
		assertNotNull(dtd);
		assertEquals(9, dtd.getElementAll().length);

		IElementDef components = dtd.getElement("components");
		assertEquals("components", components.getName());
		assertEquals(3, components.getElements().length);
		assertEquals(1, components.getAttributes().length);
		assertEquals("namespace", components.getAttribute("namespace").getName());
		assertEquals(0, components.getAttribute("namespace").getItems().length);

		IElementDef component = dtd.getElement("component");
		assertEquals(3, component.getAttribute("instance").getItems().length);
		assertEquals("singleton", component.getAttribute("instance").getDefaultValue());
		assertFalse(component.isEmpty());
		assertTrue(component.hasPCData());

		IElementDef include = dtd.getElement("include");
		assertTrue(include.isEmpty());
		assertFalse(include.hasPCData());
		assertEquals(IAttributeDef.REQUIRED, include.getAttribute("path").getDecl());
	}

}
