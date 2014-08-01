package org.telosys.tools.tests.commons;

public class TestsFolders {

	private final static String TESTS_ROOT_FOLDER = "D:/tmp/telosys-tools-tests" ;
	
	public final static String getTestsRootFolder() {
		return TESTS_ROOT_FOLDER ;
	}
	
	public final static String getTestsBundlesFolder() {
		return TESTS_ROOT_FOLDER + "/TelosysTools/templates";
	}
	
	public final static String getTestsDownloadFolder() {
		return TESTS_ROOT_FOLDER + "/TelosysTools/downloads";
	}
	
	public final static String getTestsProxyPropertiesFile() {
		return TESTS_ROOT_FOLDER + "/proxy.properties";
	}
	
	public final static String getFullFileName(String fileName) {
		return TESTS_ROOT_FOLDER + "/" + fileName ;
	}
	
}
