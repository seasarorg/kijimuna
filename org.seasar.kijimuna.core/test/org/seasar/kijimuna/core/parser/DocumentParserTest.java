package org.seasar.kijimuna.core.parser;

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.seasar.kijimuna.core.ConstCore;
import org.seasar.kijimuna.core.dicon.DiconElementFactory;
import org.seasar.kijimuna.core.internal.dicon.model.ComponentElement;
import org.seasar.kijimuna.core.internal.dicon.model.ContainerElement;
import org.seasar.kijimuna.core.test.KijimunaProject;

public class DocumentParserTest extends TestCase implements ConstCore {

	private KijimunaProject testProject;
	private IProject prj;

	protected void setUp() throws Exception {
		testProject = new KijimunaProject();
		prj = testProject.getProject();
		testProject.createPackage("test");
	}

	protected void tearDown() throws Exception {
		testProject.dispose();
	}

	public void testParseNormal() throws Exception {
		IParseResult result = executeParser("testParseNormal.dicon");
		ContainerElement container = (ContainerElement) result.getRootElement();
		assertEquals(2, container.getChildren().size());

		ComponentElement component = (ComponentElement) container.getChildren().get(0);
		assertEquals("greeting", component.getComponentName());
		assertEquals("test.GreetingImpl", component.getComponentClassName());

		Attribute classAttr = component.getAttributeObject("class");
		assertEquals(194, classAttr.getOffset());
		assertEquals(25, classAttr.getLength());
		assertEquals("class", classAttr.getName());
		assertEquals("test.GreetingImpl", classAttr.getValue());
	}

	public void testParseMultiLine() throws Exception {
		IParseResult result = executeParser("testParseMultiLine.dicon");
		ContainerElement container = (ContainerElement) result.getRootElement();
		ComponentElement component = (ComponentElement) container.getChildren().get(0);
		Attribute classAttr = component.getAttributeObject("class");
		assertEquals(197, classAttr.getOffset());
		assertEquals(31, classAttr.getLength());
	}

	private IParseResult executeParser(String filename) throws Exception {
		DocumentHandler handler = new DocumentHandler(new DiconElementFactory(),
				ID_MARKER_DICONXML, 0, 0);
		handler.putDtdPath(PUBLIC_ID_DICON_20, DTD_DICON_20);
		handler.putDtdPath(PUBLIC_ID_DICON_21, DTD_DICON_21);
		handler.putDtdPath(PUBLIC_ID_DICON_23, DTD_DICON_23);
		handler.putDtdPath(PUBLIC_ID_DICON_24, DTD_DICON_24);
		DocumentParser parser = new DocumentParser();

		IParseResult result = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("test/org/seasar/kijimuna/core/parser/testdata/"
					+ filename);
			IFile dicon = prj.getFile("src/test/test.dicon");
			dicon.create(fis, true, null);
			result = parser.parse(prj, dicon, null, handler);

		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return result;
	}
}
