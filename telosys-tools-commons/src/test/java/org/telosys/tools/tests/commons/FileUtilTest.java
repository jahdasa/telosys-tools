package org.telosys.tools.tests.commons;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.telosys.tools.commons.FileUtil;

public class FileUtilTest extends TestCase {

	public void testFileCopy() throws IOException, Exception {
		
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
