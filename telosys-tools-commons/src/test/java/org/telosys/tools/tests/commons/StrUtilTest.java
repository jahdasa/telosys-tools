package org.telosys.tools.tests.commons;

import org.telosys.tools.commons.StrUtil;

import junit.framework.TestCase;

public class StrUtilTest extends TestCase {

	public void testDifferent() {
		assertTrue( StrUtil.different("aa", "bb") ) ;
		assertTrue( StrUtil.different(null, "bb") ) ;
		assertTrue( StrUtil.different(null, "") ) ;
		assertTrue( StrUtil.different("aa", null) ) ;
		assertTrue( StrUtil.different("", null) ) ;

		assertFalse( StrUtil.different("aa", "aa") ) ;
		assertFalse( StrUtil.different("",   "") ) ;
		assertFalse( StrUtil.different(null, null) ) ;

	}


}
