package org.telosys.tools.tests.commons;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.telosys.tools.commons.FileUtil;

public class FileUtilTest extends TestCase {

	public void testFileCopy() throws IOException, Exception {
		
		String fileName = "/testfilecopy/origin/file1.txt" ;
		System.out.println("Searching file '" + fileName + "' by classpath..." );
		File file = FileUtil.getFileByClassPath(fileName);
		if ( file.exists() ) {
			System.out.println("File found : " + file);
			System.out.println(" . getAbsolutePath()  : " + file.getAbsolutePath() );
			System.out.println(" . getCanonicalPath() : " + file.getCanonicalPath() );
			System.out.println(" . getName()          : " + file.getName() );
			System.out.println(" . getPath()          : " + file.getPath() );
			System.out.println(" . getParent()        : " + file.getParent() );
		}
		else {
			System.out.println("File not found " );
		}
		assertTrue ( file.exists()) ;
		
		// Original file
		String originalFullFileName = file.getAbsolutePath();
		System.out.println("Original file    : " + originalFullFileName );

		// Destination file in inexistent folder 
		String destFullFileName = FileUtil.buildFilePath(file.getParentFile().getParent()+"/newfolder", "newfile1.txt");
		System.out.println("Destination file : " + destFullFileName );
		
		FileUtil.copy(originalFullFileName, destFullFileName, true);
	}
	
	public void testFolderCopy() throws IOException, Exception {
		
		String folderName = "/testfilecopy" ;
		System.out.println("Searching folder '" + folderName + "' by classpath..." );
		File folder = FileUtil.getFileByClassPath(folderName);
		if ( folder.exists() ) {
			System.out.println("Folder found : " + folder);
			System.out.println(" . getAbsolutePath()  : " + folder.getAbsolutePath() );
			System.out.println(" . getCanonicalPath() : " + folder.getCanonicalPath() );
			System.out.println(" . getName()          : " + folder.getName() );
			System.out.println(" . getPath()          : " + folder.getPath() );
			System.out.println(" . getParent()        : " + folder.getParent() );
		}
		else {
			System.out.println("Folder not found " );
		}
		assertTrue ( folder.exists()) ;
		
		for ( String fileName : folder.list() ) {
			System.out.println(" . " + fileName );
		}
	
		for ( File file : folder.listFiles() ) {
			System.out.println(" . " + file );
			if ( "origin".equals( file.getName() ) ) {
				System.out.println("'origin' folder found.");
				File originFolder = file ;
				
				File destinationFolder = new File(folder.getAbsolutePath(), "dest");
				FileUtil.copyFolder(originFolder, destinationFolder, false) ;
			}
		}
	}
	


}
