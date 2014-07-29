package org.telosys.tools.tests.commons;

import junit.framework.TestCase;

import org.telosys.tools.commons.ZipUtil;

public class ZipUtilTest extends TestCase {

	public void testCutEntryName() {
		System.out.println("Getting repositories... ");

//		try {
//			ZipUtil.unzip("D:/TMP/TestUnzip/file1.zip", "D:/TMP/TestUnzip/extract", true ) ;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String s ;
		s = cutEntryName("scala-templates-TT204/toto") ;
		assertEquals("toto", s);
		
		s = cutEntryName("scala-templates-TT204") ;
		assertEquals("", s);

		s = cutEntryName("scala-templates-TT204/") ;
		assertEquals("", s);

		s = cutEntryName("scala-templates-TT204/foo/bar") ;
		assertEquals("foo/bar", s);
	}
	
	private String cutEntryName ( String s ) {
		System.out.println("cut '" + s + "'");
		String r = ZipUtil.cutEntryName(s) ;
		System.out.println("result : '" + r + "'");
		return r ;
	}

	public void testUnZip1() throws Exception {
		System.out.println("Unzip file... ");
		ZipUtil.unzip("D:/tmp/telosys-tools-tests/TelosysTools/downloads/persistence-jpa-TT210-R2.zip", 
				"D:/tmp/telosys-tools-tests/TelosysTools/templates/persistence-jpa-TT210-R2", true);
	}

	public void testUnZip2() throws Exception {
		System.out.println("Unzip file... ");
		Exception error = null ;
		try {
			ZipUtil.unzip("D:/tmp/telosys-tools-tests/TelosysTools/downloads/persistence-jpa-TT210-R2.zip", 
					"D:/tmp/telosys-tools-tests/TelosysTools/templates/inex", false);
		} catch (Exception e) {
			error = e ;
		}
		System.out.println("Expected error : " + error.getMessage() );
		assertNotNull(error);
	}
}
