package org.telosys.tools.tests.commons.env;

import java.io.File;

import junit.framework.TestCase;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.env.EnvironmentManager;
import org.telosys.tools.tests.commons.TestsFolders;

public class EnvironmentManagerTest extends TestCase {

	public void printSeparator() {
		System.out.println("==============================================================" );
	}
	public void print(File file) {
		System.out.println("File   : " + file.toString());
		System.out.println("Parent : " + file.getParent());
	}
	
	public void testGetEnvironmentDirectory() throws TelosysToolsException {
		printSeparator();
		System.out.println("getEnvironmentDirectory()...");
		EnvironmentManager em = new EnvironmentManager( TestsFolders.getTestsRootFolder() );
		String dir = em.getEnvironmentDirectory();
		System.out.println(dir);
		assertEquals(TestsFolders.getTestsRootFolder() , dir);
	}
	
	public void testCreateFolder() throws TelosysToolsException {
		printSeparator();
		System.out.println("createFolder('foo1')...");
		EnvironmentManager em = new EnvironmentManager( TestsFolders.getTestsRootFolder() );
		StringBuffer sb = new StringBuffer();
		em.createFolder("foo1", sb);
		System.out.println(sb.toString());
	}

	public void testInitTelosysToolsConfigFile() throws TelosysToolsException {
		printSeparator();
		System.out.println("testInitTelosysToolsConfigFile()...");

		EnvironmentManager em = new EnvironmentManager( TestsFolders.getTestsRootFolder() );

		String filePath = em.getTelosysToolsConfigFile();
		System.out.println("File : " + filePath);
		File file = new File(filePath);
		if ( file.exists() ) {
			System.out.println("Delete " + filePath);
			file.delete();
		}
		
		StringBuffer sb = new StringBuffer();
		em.initTelosysToolsConfigFile(sb);
		String result = sb.toString();
		System.out.println(result);
		assertFalse( result.contains("not created"));
		assertTrue( file.exists() );
	}

	public void testInitDatabasesConfigFile() throws TelosysToolsException {
		printSeparator();
		System.out.println("testInitDatabasesConfigFile()...");

		EnvironmentManager em = new EnvironmentManager( TestsFolders.getTestsRootFolder() );

//		String filePath = em.getTelosysToolsConfigFile();
//		System.out.println("File : " + filePath);
//		File file = new File(filePath);
//		if ( file.exists() ) {
//			System.out.println("Delete " + filePath);
//			file.delete();
//		}
		
		StringBuffer sb = new StringBuffer();
		em.initDatabasesConfigFile("aaaa", sb);
		String result = sb.toString();
		System.out.println(result);
		assertTrue( result.contains("cannot create"));

		sb = new StringBuffer();
		em.initDatabasesConfigFile("TelosysTools", sb);
		result = sb.toString();
		System.out.println(result);
		assertTrue( result.contains("created"));
	}

}
