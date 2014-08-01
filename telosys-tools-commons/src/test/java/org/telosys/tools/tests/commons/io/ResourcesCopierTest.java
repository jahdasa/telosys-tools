package org.telosys.tools.tests.commons.io;

import java.io.File;

import junit.framework.TestCase;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.io.DefaultOverwriteChooser;
import org.telosys.tools.commons.io.OverwriteChooser;
import org.telosys.tools.commons.io.ResourcesCopier;
import org.telosys.tools.tests.commons.TestsFolders;

public class ResourcesCopierTest extends TestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		System.out.println("===== [ SETUP ] =====");
//		System.out.println("Loading configuration from folder : " + TestsFolders.getTestsRootFolder() );
	}

	public void testCopyFileToFile1() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("file1.txt")), 
				new File(TestsFolders.getFullFileName("file1-copy.txt")), 
				OverwriteChooser.YES);
		assertEquals(1, n);
	}

	public void testCopyFileToFile2() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("file1.txt")), 
				new File(TestsFolders.getFullFileName("file1-copy.txt")), 
				OverwriteChooser.NO);
		assertEquals(0, n);
	}

	public void testCopyFolderToFolder1() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo")), 
				new File(TestsFolders.getFullFileName("foo2")), 
				OverwriteChooser.YES);
		assertTrue(n > 0 );
	}
	
	public void testCopyFolderToFolder2() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo")), 
				new File(TestsFolders.getFullFileName("foo3")), 
				OverwriteChooser.YES);
		assertTrue(n > 0 );
	}

	public void testCopyFolderToFolder3() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo/bar")), 
				new File(TestsFolders.getFullFileName("foo-bar")), 
				OverwriteChooser.YES);
		assertTrue(n > 0 );
	}
	
	public void testCopyFolderToFolder4() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo/bar")), 
				new File(TestsFolders.getFullFileName("foo-bar")), 
				OverwriteChooser.NO_TO_ALL);
		assertTrue(n == 0 );
	}
	
	public void testCopyFileToFolder1() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo/fileA.txt")), 
				new File(TestsFolders.getFullFileName("mydir")), 
				OverwriteChooser.NO_TO_ALL);
		assertTrue(n == 1 );
	}
	
	public void testCopyFileToFolder2() throws TelosysToolsException {
		int n = copy (	new File(TestsFolders.getFullFileName("foo/fileA.txt")), 
				new File(TestsFolders.getFullFileName("mydir/dest-A")), 
				OverwriteChooser.NO_TO_ALL);
		//assertTrue(n == 1 );
	}
	
	private int copy(File source, File destination, int choice ) {
		int n = 0 ;
		System.out.println("===== COPY ");
		System.out.println("  from : " + source  );
		System.out.println("  to   : " + destination );
		ResourcesCopier copier = new ResourcesCopier(new DefaultOverwriteChooser(choice));
		try {
			//n = copier.recursiveCopy(source, destination);
			n = copier.copy(source, destination);
			System.out.println(n + " file(s) copied");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n ;
	}
	
}
