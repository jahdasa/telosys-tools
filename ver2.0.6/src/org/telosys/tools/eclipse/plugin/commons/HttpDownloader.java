package org.telosys.tools.eclipse.plugin.commons;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {
	
	/**
	 * Downloads a file from the given URL to the given file name 
	 * @param urlString the URL to be download
	 * @param destFileName the destination for the downloaded file 
	 * @return the number of bytes (file size)
	 */
	public static long download(String urlString, String destFileName ) {

        long totalBytesRead = 0L;
//		long duration = 0L; 
//		long startTime = System.currentTimeMillis();
		
		//System.out.println("Connecting the site...\n");
 
        URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException ("MalformedURLException", e);
		}
		
		
        try {
			url.openConnection();
			InputStream reader = url.openStream();
			
	        FileOutputStream writer = new FileOutputStream(destFileName);
	        int BUFFER_SIZE = 128 * 1024 ;
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int bytesRead = 0;
	 
	        //System.out.println("Reading ZIP file ( 128 KB blocks at a time).\n");
	 
	        while ((bytesRead = reader.read(buffer)) > 0)
	        {  
	           writer.write(buffer, 0, bytesRead);
	           buffer = new byte[BUFFER_SIZE];
	           totalBytesRead += bytesRead;
	        }

	        writer.close();
	        reader.close();
	 
//	        long endTime = System.currentTimeMillis();
//	        duration = endTime - startTime ;
	        
	        //System.out.println( "Done.");
	        //System.out.println( totalBytesRead + " bytes read, duration : " + duration + " millseconds");
	        
		} catch (IOException e) {
			throw new RuntimeException ("IOException", e);
		}
		//return duration ;
		return totalBytesRead ;
	}
}
