package test.github;

import org.telosys.tools.eclipse.plugin.commons.ZipUtil;

public class TestZipUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Getting repositories... ");

//	try {
//		ZipUtil.unzip("D:/TMP/TestUnzip/file1.zip", "D:/TMP/TestUnzip/extract", true ) ;
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		
		testCutEntryName("scala-templates-TT204/toto") ;
		testCutEntryName("scala-templates-TT204") ;
		testCutEntryName("scala-templates-TT204/") ;
	}
	
	static void testCutEntryName ( String s ) {
		System.out.println("cut '" + s + "'");
		System.out.println("result : '" + ZipUtil.cutEntryName(s) + "'");
	}

}
