package org.telosys.tools.eclipse.plugin.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	/**
	 * Unzip the given ZIP file in the output folder
	 * @param zipFile
	 * @param outputFolder
	 * @param createFolder
	 */
	public static void unzip(final String zipFile, final String outputFolder,
			final boolean createFolder) {

		log("UnZip file '" + zipFile + "'");
		log("        in '" + outputFolder + "'");
		try {

			//--- Check output directory existence
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				if (createFolder) {
					folder.mkdir();
				} else {
					throw new RuntimeException("Folder '" + outputFolder
							+ "' doesn't exist ");
				}
			}

			//--- Read each entry in the zip file
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {

				File destFile = new File(outputFolder + File.separator + zipEntry.getName() );
				log(" . entry : " + zipEntry.getName() );
				log("      to : " + destFile.getAbsolutePath() );
				if ( zipEntry.isDirectory() ) {
					destFile.mkdirs(); // create directory (including parents)
				}
				else {
					unzipEntry(zis, destFile); // extract to file
				}
				
				zipEntry = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			log("Done");

		} catch (IOException ex) {
			throw new RuntimeException("UnZip Error (IOException)", ex);
		}
	}
	
	private static void unzipEntry(ZipInputStream zis, File newFile) throws IOException {

		// create non existent parent folders (to avoid FileNotFoundException) ???
		// new File(newFile.getParent()).mkdirs();

		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream(newFile);
		int len;
		while ((len = zis.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}
		fos.close();
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}
}
