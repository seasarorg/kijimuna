package org.seasar.kijimuna.core.util;

import junit.framework.TestCase;

public class ClassUtilTest extends TestCase {

	public void testConcatName() {
		assertEquals("aaa.Bar", ClassUtil.concatName("aaa", "Bar"));
		assertEquals("Bar", ClassUtil.concatName(null, "Bar"));
		assertEquals("Bar", ClassUtil.concatName("", "Bar"));
		assertEquals("a.b", ClassUtil.concatName("a.b", null));
		assertEquals("a.b", ClassUtil.concatName("a.b", ""));
		assertEquals(null, ClassUtil.concatName(null, null));
		assertEquals(null, ClassUtil.concatName("", null));
		assertEquals(null, ClassUtil.concatName(null, ""));
		assertEquals(null, ClassUtil.concatName("", ""));
	}

	public void test() {
		assertEquals("a.b", ClassUtil.splitFQCN("a.b.Bar")[0]);
		assertEquals("Bar", ClassUtil.splitFQCN("a.b.Bar")[1]);
		assertEquals("", ClassUtil.splitFQCN("Bar")[0]);
		assertEquals("Bar", ClassUtil.splitFQCN("Bar")[1]);
		assertEquals("a.b", ClassUtil.splitFQCN("a.b")[0]);
		assertEquals("", ClassUtil.splitFQCN("a.b")[1]);
	}
}
