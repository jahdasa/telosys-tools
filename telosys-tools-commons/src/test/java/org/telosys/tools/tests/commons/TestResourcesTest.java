package org.telosys.tools.tests.commons;

import java.io.File;

import junit.framework.TestCase;

public class TestResourcesTest extends TestCase {

	public void testFile()  {
		File file = new File("src/test/resources/cfg/telosys-tools.cfg");
		System.out.println("File : " + file );
		System.out.println("File absolute path: " + file.getAbsolutePath() );
		
		assertTrue( file.exists() );
	}
	
}
