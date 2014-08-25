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

	//----------- File to File ------------------
	public void testCopyFileToFile1() throws TelosysToolsException {
		int n = copy (	getOriginFile("file1.txt"), 
						getDestinationFile("file1-copy.txt"), 
						OverwriteChooser.YES);
		assertEquals(1, n);
	}

	public void testCopyFileToFile2() throws TelosysToolsException {
		int n = copy (	getOriginFile("file1.txt"), 
						getDestinationFile("file1-copy.txt"), 
						OverwriteChooser.NO);
		assertEquals(0, n);
	}

	//----------- Folder to Folder ------------------
	public void testCopyFolderToFolder1() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo"), 
						getDestinationFile("foo2"), 
						OverwriteChooser.YES);
		assertTrue(n > 0 );
	}
	
	public void testCopyFolderToFolder2() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo"), 
						getDestinationFile("foo3"), 
						OverwriteChooser.YES);
		assertTrue(n > 0 );
	}

	public void testCopyFolderToFolder3() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/bar"), 
						getDestinationFile("foo-bar"), 
						OverwriteChooser.YES);
		assertTrue(n > 0 );
	}
	
	public void testCopyFolderToFolder4() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/bar"), 
						getDestinationFile("foo-bar"), 
						OverwriteChooser.NO);
		assertTrue(n == 0 );
	}
	
	//----------- File to Folder ------------------
	public void testCopyFileToFolder1() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir"), 
						OverwriteChooser.YES);
		assertTrue(n == 1 );
	}
	public void testCopyFileToFolder1bis() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir"), 
						OverwriteChooser.NO);
		assertTrue(n == 0 );
	}
	
	public void testCopyFileToFolder2() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir/dest-A"), 
						OverwriteChooser.YES);
		assertTrue(n == 1 );
	}
	public void testCopyFileToFolder2bis() throws TelosysToolsException {
		int n = copy (	getOriginFile("foo/fileA.txt"), 
						getDestinationFile("mydir/dest-A"), 
						OverwriteChooser.NO);
		assertTrue(n == 0 );
	}
	
	//----------------------------------------------
	private File getOriginFile(String fileOrFolderName ) {
		return new File(TestsFolders.getFullFileName("resources-origin/" + fileOrFolderName));
	}
	
	private File getDestinationFile(String fileOrFolderName ) {
		return new File(TestsFolders.getFullFileName("resources-destination/" + fileOrFolderName));
	}
	
	private int copy(File source, File destination, int choice ) {
		int n = 0 ;
		System.out.println("===== COPY ");
		System.out.println("  from : " + source  );
		System.out.println("  to   : " + destination );
		ResourcesCopier copier = new ResourcesCopier(new DefaultOverwriteChooser(choice));
		try {
			n = copier.copy(source, destination);
			System.out.println(n + " file(s) copied");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n ;
	}
	
}
