package test.github;

import org.telosys.tools.eclipse.plugin.commons.ZipUtil;

public class TestZipUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Getting repositories... ");

		ZipUtil.unzip("D:/TMP/TestUnzip/file1.zip", "D:/TMP/TestUnzip/extract", true ) ;
	}

}
