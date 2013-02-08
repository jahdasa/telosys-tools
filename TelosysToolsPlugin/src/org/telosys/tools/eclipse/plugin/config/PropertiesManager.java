package org.telosys.tools.eclipse.plugin.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.telosys.tools.eclipse.plugin.commons.MsgBox;

/**
 * @author Eric Lemelin, Laurent Guerin
 * 
 * Class to load and save properties 
 */

public class PropertiesManager {

	private final static String ERR_CANNOT_LOAD = "Cannot load properties.\n" ;
	
	private final static String ERR_CANNOT_SAVE = "Cannot save properties.\n" ;
	
//	private final static String ERR_LOAD_NO_FILE_NAME = "Cannot load properties.\nFile name is null !" ;
//	
//	private final static String ERR_SAVE_NO_FILE_NAME = "Cannot save properties.\nFile name is null !" ;
//	
//	private final static String ERR_SAVE_NO_FILE = "Cannot save properties.\nFile is null !" ;
//	
//	private final static String ERR_SAVE_NO_PROPERTIES = "Cannot save properties.\nProperties object is null !" ;
	
	//private String _sFileName = null ;	
	
	private File   _file = null ;	
	
	/**
	 * Constructs a PropertiesManager for the given full file name
	 * @param name
	 */
	public PropertiesManager( String sFileName ) 
	{
		super();
		if ( sFileName == null )
		{
			MsgBox.error("PropertiesManager constructor error : file name is null !");
		}
		//_sFileName = sFileName;
		_file = new File(sFileName);
	}
	
	/**
	 * Constructs a PropertiesManager for the given file instance
	 * @param file
	 */
	public PropertiesManager( File file ) 
	{
		super();
		if ( file == null )
		{
			MsgBox.error("PropertiesManager constructor error : file is null !");
		}
		_file = file ;
	}
	
	/**
	 * Loads the properties from the current file 
	 * @return the properties loaded or null if the file doesn't exist
	 */
	public Properties load() {
		//return load(_sFileName);
		return load( _file );
	}
	
	/**
	 * Saves the given properties in the current file 
	 * @param prop
	 */
	public void save(Properties prop) {
		//save(_sFileName, prop);
		save(_file, prop);
	}
	
	public String getFileName() {
		//return _sFileName ;
		return _file.getAbsolutePath() ; 
	}
	
//	/**
//	 * Loads the properties from the given file name
//	 * @param fileName the file to read 
//	 * @return the properties loaded or null if the file doesn't exist
//	 */
//	private Properties load(String fileName) {
//		//MsgBox.info("PropertiesManager.load (" + fileName + ")");
//		if ( fileName == null )
//		{
//			MsgBox.error(ERR_LOAD_NO_FILE_NAME);
//			return null ;
//		}
//		File propFile = new File(fileName);
//		return load(propFile);
//	}
	
	/**
	 * Loads the properties from the given file 
	 * @param propFile
	 * @return
	 */
	private Properties load( File propFile ) 
	{
		//--- If the file doesn't exist ... return null (it's not an error)
		if ( propFile.exists() != true ) {
			return null ;
		}

		//--- The file exists => load it !  
		Properties props = new Properties();
		FileInputStream fis = null ;
		try {
			//fis = new FileInputStream(fileName);
			fis = new FileInputStream(propFile);
			props.load(fis);
		} catch (IOException ioe) {
			//ioe.printStackTrace();
			MsgBox.error(ERR_CANNOT_LOAD + "IOException : \n" + ioe.getMessage() );
		}
		finally
		{
			try {
				if ( fis != null )
				{
					fis.close();
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		return props;
	}

//	/**
//	 * Saves the given properties in the given file name
//	 * @param fileName the file to write
//	 * @param props the properties to save
//	 */
//	private void save(String fileName, Properties props) 
//	{
//		//MsgBox.info("PropertiesManager.save (" + fileName + ")");
//		if ( fileName == null )
//		{
//			MsgBox.error(ERR_SAVE_NO_FILE_NAME);
//			return  ;
//		}
//		if ( props == null )
//		{
//			MsgBox.error(ERR_SAVE_NO_PROPERTIES);
//			return  ;
//		}
//		File propFile = new File(fileName);
//		save(propFile, props );
//	}
		
	/**
	 * Saves the given properties in the given file
	 * @param propFile
	 * @param props
	 */
	private void save(File propFile, Properties props) 
	{
		if ( propFile == null )
		{
			MsgBox.error(ERR_CANNOT_SAVE + "File parameter is null !");
			return  ;
		}
		if ( props == null )
		{
			MsgBox.error(ERR_CANNOT_SAVE + "Properties parameter is null !");
			return  ;
		}
		
		//props.list(System.out);
		
		FileOutputStream fos = null ;
		try {
			//fos = new FileOutputStream(fileName);
			fos = new FileOutputStream(propFile);
			props.store(fos, "Telosys plugin properties");
		} catch (IOException ioe) {
			MsgBox.error(ERR_CANNOT_SAVE + "IOException : \n" + ioe.getMessage() );
		}
		finally
		{
			try {
				if ( fos != null )
				{
					fos.close();
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

}