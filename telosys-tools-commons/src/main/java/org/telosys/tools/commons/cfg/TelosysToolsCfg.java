/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.commons.cfg;

import java.util.Properties;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.variables.Variable;
import org.telosys.tools.commons.variables.VariablesUtil;
import org.telosys.tools.commons.variables.VariablesNames;

/**
 * Telosys Tools project configuration <br>
 * This class provides the configuration loaded from the properties file <br>
 * for a project of the current workspace
 *   
 * @author Laurent GUERIN
 *
 */
public class TelosysToolsCfg 
{
	private final static String DATABASES_DBCFG_FILE = "databases.dbcfg";
	
    //--- Properties Names for directories 
    public final static String REPOS_FOLDER      = "RepositoriesFolder";
    public final static String TEMPLATES_FOLDER  = "TemplatesFolder";
    public final static String DOWNLOADS_FOLDER  = "DownloadsFolder";    
    public final static String LIBRARIES_FOLDER  = "LibrariesFolder";
    
	
	//----------------------------------------------------------------------------------------
	private final String _projectAbsolutePath ; 
	private final String _cfgFileAbsolutePath ; 

	//----------------------------------------------------------------------------------------
	//--- Project folders default values
	private String _sRepositoriesFolder = "TelosysTools" ; 
	private String _sTemplatesFolder    = "TelosysTools/templates" ; 
	private String _sDownloadsFolder    = "TelosysTools/downloads" ; 
	private String _sLibrariesFolder    = "TelosysTools/lib" ; 
	
	//----------------------------------------------------------------------------------------
	//--- Standard variables : packages default values
	private String _ENTITY_PKG = "org.demo.bean" ;
	private String _ROOT_PKG   = "org.demo" ;

	//--- Standard variables : folders default values
	private String _SRC      =  "" ;
	private String _RES      =  "" ;
	private String _WEB      =  "" ;
	private String _TEST_SRC =  "" ;
	private String _TEST_RES =  "" ;
	private String _DOC      =  "doc" ;
	private String _TMP      =  "tmp" ;
	
	//----------------------------------------------------------------------------------------
	//--- Project Variables
	private final Variable[] _specificVariables  ; // Specific variables defined by the user 

	private final Variable[] _allVariables ; // Standard variables + specific variables 
	
	//----------------------------------------------------------------------------------------
    /**
     * Constructor 
     * @param projectAbsolutePath the project directory (full path) 
     * @param cfgFileAbsolutePath the configuration file (full path)
     * @param prop the project configuration properties (if null default values will be used) 
     */
    public TelosysToolsCfg ( String projectAbsolutePath, String cfgFileAbsolutePath, Properties prop )
    {
    	if ( projectAbsolutePath == null ) {
    		throw new IllegalArgumentException("projectAbsolutePath is null");
    	}
    	if ( cfgFileAbsolutePath == null ) {
    		throw new IllegalArgumentException("cfgFileAbsolutePath is null");
    	}
    	_projectAbsolutePath  = projectAbsolutePath ;
    	_cfgFileAbsolutePath = cfgFileAbsolutePath ;
    		
    	if ( prop != null)
    	{    	
	    	// Initialization with the given properties, use original values as default values
	    	_sRepositoriesFolder = prop.getProperty(REPOS_FOLDER,     _sRepositoriesFolder);
	    	_sTemplatesFolder    = prop.getProperty(TEMPLATES_FOLDER, _sTemplatesFolder);
	    	_sDownloadsFolder    = prop.getProperty(DOWNLOADS_FOLDER, _sDownloadsFolder);
	    	_sLibrariesFolder    = prop.getProperty(LIBRARIES_FOLDER, _sLibrariesFolder);
	    	
	    	//--- Packages 
	    	_ROOT_PKG   = prop.getProperty(VariablesNames.ROOT_PKG,   _ROOT_PKG);
	    	_ENTITY_PKG = prop.getProperty(VariablesNames.ENTITY_PKG, _ENTITY_PKG);
	
	    	//--- Folders  
	    	_SRC      =  prop.getProperty(VariablesNames.SRC,      _SRC);
	    	_RES      =  prop.getProperty(VariablesNames.RES,      _RES);
	    	_WEB      =  prop.getProperty(VariablesNames.WEB,      _WEB);
	    	_TEST_SRC =  prop.getProperty(VariablesNames.TEST_SRC, _TEST_SRC);
	    	_TEST_RES =  prop.getProperty(VariablesNames.TEST_RES, _TEST_RES);
	    	_DOC      =  prop.getProperty(VariablesNames.DOC,      _DOC);
	    	_TMP      =  prop.getProperty(VariablesNames.TMP,      _TMP);
	    	
	    	//--- Project user defined variables
	    	_specificVariables = VariablesUtil.getVariablesFromProperties( prop );
	    	_allVariables      = VariablesUtil.getAllVariablesFromProperties(prop); 
    	}
    	else {
    		//--- Keep the default values 
	    	//--- No user defined variables (void)
    		_specificVariables = new Variable[0] ;
    		_allVariables = new Variable[0] ;
    	}
    }
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the file system project folder (absolute path)
     * @return
     */
    public String getProjectAbsolutePath()
	{
    	return _projectAbsolutePath ;
	}
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the absolute file name of the configuration file 
     * @return
     */
    public String getCfgFileAbsolutePath()
	{
    	return _cfgFileAbsolutePath;
	}

    //==============================================================================
    // "databases.dbcfg" file  
    //==============================================================================
    /**
     * Returns the "databases.dbcfg" in the current project (relative path in the project) <br>
     * ( e.g. 'TelosysTools/databases.dbcfg' )
     * @return
     */
    public String getDatabasesDbCfgFile() {
    	return FileUtil.buildFilePath(getRepositoriesFolder(), DATABASES_DBCFG_FILE);
	}
    /**
     * Returns the absolute file name of the "databases.dbcfg" file 
     * ( e.g. 'X:/dir/myproject/TelosysTools/databases.dbcfg' )
     * @return
     */
    public String getDatabasesDbCfgFileAbsolutePath()
	{
    	return FileUtil.buildFilePath(getRepositoriesFolderAbsolutePath(), DATABASES_DBCFG_FILE);
	}

    //==============================================================================
    // Folders 
    //==============================================================================
    public String getSRC() {
    	return _SRC;
	}
    public String getRES() {
    	return _RES;
	}
    public String getWEB() {
    	return _WEB;
	}
    public String getTEST_RES() {
    	return _TEST_RES;
	}
    public String getTEST_SRC() {
    	return _TEST_SRC;
	}
    public String getDOC(){
    	return _DOC;
	}
    public String getTMP() {
    	return _TMP;
	}
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the templates folder in the current project (relative path in the project) <br>
     * ( e.g. 'TelosysTools/templates' )
     * @return
     */
    public String getTemplatesFolder() {
    	return _sTemplatesFolder;
	}
    /**
     * Returns the templates folder absolute path <br>
     * ( e.g. 'X:/dir/myproject/TelosysTools/templates' )
     * @return
     */
    public String getTemplatesFolderAbsolutePath() {
    	return FileUtil.buildFilePath(_projectAbsolutePath, _sTemplatesFolder ) ;
	}

	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the download folder in the current project (relative path in the project)<br>
     * ( e.g. 'TelosysTools/downloads' )
     * @return
     */
    public String getDownloadsFolder() {
    	return _sDownloadsFolder;
    }
    /**
     * Returns the download folder absolute path <br>
     * ( e.g. 'X:/dir/myproject/TelosysTools/downloads' )
     * @return
     */
    public String getDownloadsFolderAbsolutePath() {
    	return FileUtil.buildFilePath(_projectAbsolutePath, _sDownloadsFolder ) ;
	}
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the libraries folder in the current project (relative path in the project)<br>
     * ( e.g. 'TelosysTools/lib' )
     * @return
     */
    public String getLibrariesFolder() {
    	return _sLibrariesFolder ;
    }
    /**
     * Returns the download folder absolute path <br>
     * ( e.g. 'X:/dir/myproject/TelosysTools/lib' )
     * @return
     */
    public String getLibrariesFolderAbsolutePath() {
    	return FileUtil.buildFilePath(_projectAbsolutePath, _sLibrariesFolder ) ;
	}
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the repositories folder in the current project (relative path in the project) <br>
     * ( e.g. 'TelosysTools' )
     * @return
     */
    public String getRepositoriesFolder()
	{
    	return _sRepositoriesFolder ;
	}
    /**
     * Returns the repositories folder absolute path <br>
     * ( e.g. 'X:/dir/myproject/TelosysTools' )
     * @return
     */
    public String getRepositoriesFolderAbsolutePath() {
    	return FileUtil.buildFilePath(_projectAbsolutePath, _sRepositoriesFolder ) ;
	}
    
    //=======================================================================================================
    // Packages 
    //=======================================================================================================
	/**
	 * Returns the package for entity classes 
	 * ie "org.demo.bean"
	 * @return 
	 */
	public String getEntityPackage() 
	{
		return _ENTITY_PKG ;
	}
	
	//------------------------------------------------------------------------------------------------------
	/**
	 * Returns the root package  
	 * ie "org.demo"
	 * @return 
	 */
	public String getRootPackage() 
	{
		return _ROOT_PKG ;
	}
	
    //=======================================================================================================
    // Variables 
    //=======================================================================================================
	/**
	 * Returns the specific variables defined for the project  
	 * @return array of variables, or null if none
	 */
	public Variable[] getSpecificVariables()
	{
		return _specificVariables ;
	}	

	//------------------------------------------------------------------------------------------------------
	/**
	 * Returns all the variables defined for the project <br>
	 * (standard variables + specific variables )
	 * @return
	 */
	public Variable[] getAllVariables()
	{
		return _allVariables ;
	}	
	
}
