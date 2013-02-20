package org.telosys.tools.eclipse.plugin.config;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.commons.VariablesUtil;
import org.telosys.tools.eclipse.plugin.MyPlugin;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.GeneratorConfigConst;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.generator.target.TargetsFile;

/**
 * Telosys Tools project configuration <br>
 * This class provides the configuration loaded from the properties file <br>
 * for a project of the current workspace
 *   
 * @author Laurent GUERIN
 *
 */
public class ProjectConfig 
{
	public final static String TEMPLATES_CFG = "templates.cfg" ;
		
	// The plugin congiguration file : ".../.../telosys-tools.cfg"
	private String _sPluginConfigFile   = null ; 
	
	private String _sProjectName        = null ; 
	private String _sProjectLocation    = null ; 

	private String _sWorkspaceLocation  = null ;
	
// [22-Jan-2012] Removed	
//	private String _sTelosysPropFile    = "WebContent/WEB-INF/conf/telosys.properties" ;
	
//	private String _sSourceFolder       = "src" ;
//	private String _sWebContentFolder   = "WebContent" ;
	private String _sTemplatesFolder    = "TelosysTools/templates" ; 
	private String _sRepositoriesFolder = "TelosysTools/repos" ; 
	
	//----------------------------------------------------------------------------------------
	// [22-Jan-2012] Removed	
	//--- Packages 	
	private String _sPackageVOBean = "org.demo.vo.bean" ;

	private String _SRC      =  "" ;
	private String _RES      =  "" ;
	private String _WEB      =  "" ;
	private String _TEST_SRC =  "" ;
	private String _TEST_RES =  "" ;
	
	//----------------------------------------------------------------------------------------
	//--- Classes names
	// [22-Jan-2012] Removing classes names
//	private String _sClassNameVOList      = ConfigDefaults.DEFAULT_LIST_CLASS_NAME ;
//	
//	private String _sClassNameDAO         = ConfigDefaults.DEFAULT_DAO_CLASS_NAME ;
//	
//	private String _sClassNameXmlMapper   = ConfigDefaults.DEFAULT_XML_MAPPER_CLASS_NAME ;

	//----------------------------------------------------------------------------------------
	//--- Masks
	private String _sScreenDataClassMask = "*Data" ;
	
	private String _sVOClassMask         = "*VO" ;
	
	//----------------------------------------------------------------------------------------
	//--- Project Variables
	//private VariableItem[] _projectVariables = null ;
	private Variable[] _projectVariables = null ;
	
	//----------------------------------------------------------------------------------------
	//private String _sTemplatesDirectory = "" ;
	// private List<SpecificTemplate> _specificTemplates = null ;
	private List<TargetDefinition> _templates = null ;
	
	//----------------------------------------------------------------------------------------	
//	/**
//	 * Return the parameter value if not null and not void, else return the default value 
//	 * @param sParam
//	 * @param sDefault
//	 * @return
//	 */
//	private String paramOrDefault( String sParam, String sDefault )
//	{
//		if ( sParam != null )
//		{
//			String s = sParam.trim() ;
//			if ( s.length() > 0 )
//			{
//				// Parameter set 
//				return s ;
//			}
//		}
//		// Parameter NOT set ( Null or Void ) => use default value
//		return sDefault ;
//	}	
	
    /**
     * Constructor 
     * @param project the project associated with the given properties 
     * @param prop the properties ( can be null : keep default values )
     * @param sPluginConfigFile the file where the properties are stored
     */
    public ProjectConfig (IProject project, Properties prop, String sPluginConfigFile )
    {
    	if ( project == null)
    	{
    		MsgBox.error("ProjectConfig constructor : project is null");
    		return ; // Keep the original default values 
    	}
    	_sPluginConfigFile = sPluginConfigFile ;
    		
    	_sProjectName = project.getName() ; 
    	
    	//--- Project location
    	if ( project.getLocation() != null )
    	{
        	_sProjectLocation = project.getLocation().toOSString() ;     	
    	}
    	else
    	{
    		MsgBox.error("ProjectConfig constructor : project location is null");
    	}
    	
    	//--- Workspace location
    	IWorkspace wks = project.getWorkspace();
    	IWorkspaceRoot wksRoot = wks.getRoot();
    	IPath wksLocation = wksRoot.getLocation();
    	if ( wksLocation != null )
    	{
    		_sWorkspaceLocation = wksLocation.toOSString() ;
    	}
    	else
    	{
    		MsgBox.error("ProjectConfig constructor : workspace location is null");
    	}
    	
    	if ( prop == null)
    	{
    		return ; // Keep the original default values 
    	}
    	
    	// Init with the given properties, use original values as default values
//    	_sSourceFolder = prop.getProperty(PropName.SOURCE_FOLDER, _sSourceFolder);
//    	_sWebContentFolder = prop.getProperty(PropName.WEB_CONTENT_FOLDER, _sWebContentFolder);
    	_sTemplatesFolder = prop.getProperty(GeneratorConfigConst.TEMPLATES_FOLDER, _sTemplatesFolder);
    	_sRepositoriesFolder = prop.getProperty(PropName.REPOS_FOLDER, _sRepositoriesFolder);
    	
    	//--- Packages 
    	_sPackageVOBean = prop.getProperty(GeneratorConfigConst.PACKAGE_VO, _sPackageVOBean);

    	//--- Folders  
    	_SRC      =  prop.getProperty(ContextName.SRC,      _SRC);
    	_RES      =  prop.getProperty(ContextName.RES,      _RES);
    	_WEB      =  prop.getProperty(ContextName.WEB,      _WEB);
    	_TEST_SRC =  prop.getProperty(ContextName.TEST_SRC, _TEST_SRC);
    	_TEST_RES =  prop.getProperty(ContextName.TEST_RES, _TEST_RES);
    	
    	//--- Project user defined variables
    	//_projectVariables = initProjectVariables( prop ); // Can be null
    	_projectVariables = VariablesUtil.getVariablesFromProperties( prop );
    	
    	
    	_templates = loadTemplates();
    }
    
    //==============================================================================
    // Files 
    //==============================================================================
    public String getPluginConfigFile()
	{
    	return _sPluginConfigFile;
	}
//    public String getTelosysPropFile()
//	{
//    	return _sTelosysPropFile;
//	}

    //==============================================================================
    // Folders 
    //==============================================================================
    public String getSRC()
	{
    	return _SRC;
	}
    public String getRES()
	{
    	return _RES;
	}
    public String getWEB()
	{
    	return _WEB;
	}
    public String getTEST_RES()
	{
    	return _TEST_RES;
	}
    public String getTEST_SRC()
	{
    	return _TEST_SRC;
	}
    public String getTemplatesFolder()
	{
    	return _sTemplatesFolder;
	}
    
    /**
     * Returns the plugin templates directory ( default templates directory ) 
     * @return
     */
    private String getPluginTemplatesFolder()
	{
    	return MyPlugin.getTemplatesDirectory();
	}
    
    /**
     * Returns the "Project Full Path" by adding the given subpath at the end of the project location
     * @param sSubPath the "sub path" to add
     * @return
     */
    private String getProjectFullPath(String sSubPath)
	{
    	if ( sSubPath != null )
    	{
    		String s = sSubPath.trim() ;
    		if ( s.startsWith("/") || s.startsWith("\\") )
    		{
    	    	return _sProjectLocation + s ;
    		}
    		else
    		{
    	    	return _sProjectLocation + "/" + s ;
    		}
    	}
    	else
    	{
    		return _sProjectLocation ;
    	}
	}
    
    /**
     * Returns the "full path" templates folder (never null)<br>
     * e.g. : "C:/dir/workspace/project/folder"
     * It can be the plugin templates folder ( if the template folder property is not set )
     * or the project templates folder
     * @return
     */
    public String getTemplatesFolderFullPath()
	{
    	if ( _sTemplatesFolder != null )
    	{
    		String s = _sTemplatesFolder.trim() ;
    		if ( s.length() == 0 )
    		{
    			// No templates folder => use the plugin templates
    			return getPluginTemplatesFolder() ;
    		}
    		else
    		{
    			// Templates folder is set => build the full path
    			return getProjectFullPath(_sTemplatesFolder) ;
    		}
    	}
    	else
    	{
    		MsgBox.warning("Templates Folder is null \n=> use plugin templates folder");
    		return getPluginTemplatesFolder() ;
    	}
	}
    
    public String getRepositoriesFolder()
	{
    	return _sRepositoriesFolder ;
	}
    
    public String getProjectName()
	{
    	return _sProjectName ;
	}
    
    /**
     * Returns the OS file system project location
     * @return
     */
    public String getProjectFolder()
	{
    	return _sProjectLocation ;
	}
    
    public String getWorkspaceFolder()
	{
    	return _sWorkspaceLocation ;
	}

    
//    //==============================================================================
//    // Directories  
//    //==============================================================================
//	public String getTemplatesDirectory()
//	{
//		return _sTemplatesDirectory ;
//	}
	
    
    
    //==============================================================================
    // Packages 
    //==============================================================================
	/**
	 * ie "org.demo.vo.bean"
	 * @return 
	 */
	public String getPackageForVOBean() 
	{
		return _sPackageVOBean ;
	}
	
//	/**
//	 * ie "org.demo.vo.list"
//	 * @return
//	 */
//	public String getPackageForVOList() 
//	{
//		return _sPackageVOList ;
//	}
	
//	/**
//	 * ie "org.demo.vo.xml"
//	 * @return
//	 */
//	public String getPackageForXmlMapper() 
//	{
//		return _sPackageXmlMapper ;
//	}
	
//	/**
//	 * ie "org.demo.vo.dao"
//	 * @return
//	 */
//	public String getPackageForDAO() 
//	{
//		return _sPackageDAO ;
//	}
	
//	/**
//	 * ie "org.demo.screen"
//	 * @return
//	 */
//	public String getPackageForScreenData() 
//	{
//		return _sPackageScreenData ;
//	}
	
//	/**
//	 * ie "org.demo.screen"
//	 * @return
//	 */
//	public String getPackageForScreenManager() 
//	{
//		return _sPackageScreenManager ;
//	}
	
//	public String getPackageForScreenProcedures() 
//	{
//		return _sPackageScreenProcedures ;
//	}
//	/**
//	 * ie "org.demo.screen"
//	 * @return
//	 */
//	public String getPackageForScreenTriggers() 
//	{
//		return _sPackageScreenTriggers ;
//	}
	
//	//==============================================================================
//    // Classes names  
//    //==============================================================================
//	/**
//	 * @return the trimed parameter or null
//	 */
//	public String getClassNameForXmlMapper()
//	{
//		return trim( _sClassNameXmlMapper ) ;
//	}
//	
//	/**
//	 * @param sDefault
//	 * @return the trimed parameter or the default value if not set
//	 */
//	public String getClassNameForXmlMapper( String sDefault )
//	{
//		return paramOrDefault( this.getClassNameForXmlMapper(), sDefault );
//	}
//	
//	//------------------------------------------------------------------------------
//	/**
//	 * @return the trimed parameter or null
//	 */
//	public String getClassNameForDAO()
//	{
//		return trim( _sClassNameDAO ) ;
//	}
//	
//	/**
//	 * @param sDefault
//	 * @return the trimed parameter or the default value if not set
//	 */
//	public String getClassNameForDAO( String sDefault )
//	{
//		return paramOrDefault( this.getClassNameForDAO(), sDefault );
//	}
//	
//	//------------------------------------------------------------------------------
//	/**
//	 * @return the trimed parameter or null
//	 */
//	public String getClassNameForVOList()
//	{
//		return trim( _sClassNameVOList ) ;
//	}
//	
//	/**
//	 * @param sDefault
//	 * @return the trimed parameter or the default value if not set
//	 */
//	public String getClassNameForVOList( String sDefault )
//	{
//		return paramOrDefault( this.getClassNameForVOList(), sDefault );
//	}
	
	//==============================================================================
    // Masks  
    //==============================================================================
	public String getScreenDataClassMask() 
	{
		return _sScreenDataClassMask ;
	}
	
	public String getVOClassMask() 
	{
		return _sVOClassMask ;
	}
	
    //==============================================================================
    // Super classes  
    //==============================================================================
	public String getScreenDataAncestor() 
	{
		// Universal form ( compare with full name AND short name  )
		return "org.objectweb.telosys.screen.core.StandardScreenData";
		//return "StandardScreenData";
		//return null ;
	}
	
    //==============================================================================
    // Specific Templates  
    //==============================================================================
    /**
     * Reload the specific templates file "templates.cfg"
     */
    public void refreshTemplates()
	{
    	_templates = loadTemplates();
	}

    /**
     * Returns the list of targets defined in "templates.cfg" or null if none
     * @return
     */
    //public List<SpecificTemplate> getSpecificTemplates()
    public List<TargetDefinition> getTemplates()
	{
    	return _templates ;
	}

	//private List<SpecificTemplate> loadSpecificTemplates()
	private List<TargetDefinition> loadTemplates()
	{
		String sTemplatesFolder = getTemplatesFolderFullPath();
		String sFile = FileUtil.buildFilePath(sTemplatesFolder, TEMPLATES_CFG );
		
		//TemplatesFile file = new TemplatesFile(sFile) ;
		//List<SpecificTemplate> list = file.load();
		
		TargetsFile targetsFile = new TargetsFile(sFile) ;
		List<TargetDefinition> list = null ;
		try {
			list = targetsFile.load();
		} catch (GeneratorException e) {
			MsgBox.error("Cannot load targets definition from file : \n" + sFile + "\n Exception : " + e.getMessage() );
			e.printStackTrace();
		}

		return list ;
	}	

	/**
	 * Returns all the defined project variables  
	 * @return array of variables, or null if none
	 */
	public Variable[] getProjectVariables()
	{
		return _projectVariables ;
	}	

}
