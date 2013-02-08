package org.telosys.tools.eclipse.plugin.config;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

/**
 * Projects configuration holder
 * This static class holds and provides the Telosys projects configurations
 */
public class ProjectConfigManager {

    private final static String PLUGIN_PROPERTIES_FILE = "telosys-tools.cfg";

	/**
	 * Table (cache) of projects configurations 
	 * The key is the "Project Configuration File Name" 
	 * The value is the "ProjectConfig" instance
	 */
	//private static Hashtable $htConfigs = new Hashtable();
	
	private static ProjectConfigs $cache = new ProjectConfigs();
	
	
	//-------------------------------------------------------------------------------------------------
	public static String getProjectConfigDir( IProject project ) 
	{
		return EclipseProjUtil.getProjectDir( project ) ;
	}
	
	//-------------------------------------------------------------------------------------------------
	public static String getConfigFileName() 
	{
		return PLUGIN_PROPERTIES_FILE ;
	}
	
//	private static IProject $currentProject = null ;
	
	
	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Set or reset the configuration associated with the given file name 
//	 * @param sConfigFileName
//	 * @param prop
//	 * @return
//	 */
//	private static ProjectConfig init( IProject project, Properties prop ) 
//	{
//		ProjectConfig projectConfig = null;
//		if ( prop != null )
//		{
//			projectConfig = new ProjectConfig(project, prop);
//		}
//		else
//		{
//			projectConfig = new ProjectConfig();
//		}
//		String sConfigFileName = getProjectConfigFileName(project) ;
//		$htConfigs.put(sConfigFileName, projectConfig);
//		return projectConfig ;
//	}
	
	//-------------------------------------------------------------------------------------------------
//	private static void setProjectConfig(IProject project, ProjectConfig projectConfig)
//	{
//		if ( project == null )
//		{
//			MsgBox.error("setProjectConfig : project parameter is null ");
//			return;
//		}
//		String sProjectName = project.getName();
//		$htConfigs.put(sProjectName, projectConfig );
//	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Returns the project configuration for the given Eclipse project<br>
	 * The project config is search in the cache and loaded from file if not 
	 * found in the cache
	 * @param project
	 * @return the project configuration or null if the config file doesn't exist
	 */
	public static ProjectConfig getProjectConfig( IProject project ) 
	{
		PluginLogger.log("ProjectConfigManager.getProjectConfig(project)..." );
		if ( project == null )
		{
			MsgBox.error("getProjectConfig : project parameter is null ");
			return null ;
		}
		
		ProjectConfig projectConfig = $cache.get(project);
		if ( projectConfig != null )
		{
			PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Found in cache." );
			return projectConfig ;
		}
		else
		{
			PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Not found in cache => load." );
			// Not yet loaded => load it now 
			loadProjectConfig(project) ;
			projectConfig = $cache.get(project);
			return projectConfig ;
		}
		
//		String sProjectName = project.getName();
//		Object obj = $htConfigs.get(sProjectName);
//		if ( obj != null )
//		{
//			return (ProjectConfig) obj ;
//		}
//		else
//		{
//			// Not yet loaded => load it now 
//			ProjectConfig projectConfig = null ;
//			String sConfigFileName = getProjectConfigFileName(project);
//			PropertiesManager propManager = new PropertiesManager(sConfigFileName) ;
//			Properties prop = propManager.load();
//			if ( prop == null )
//			{
//				MsgBox.error("Cannot load properties from file '" + sConfigFileName + "' \n"
//						+ "The default values will be used." );
//				projectConfig = new ProjectConfig();
//			}
//			else
//			{
//				projectConfig = new ProjectConfig(project, prop);
//			}
//			
//			//--- Keep in the table
//			$htConfigs.put(sProjectName, projectConfig);
//			
//			return projectConfig ;
//		}
	}

	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Returns the "full path" file name ( i.e. "C:/aaa/bbb/workspace/project" )
//	 * 
//	 * @param project
//	 * @return
//	 */
//	private static String getCurrentProjectDir( IResource project ) 
//	{
//		if ( project != null )
//		{
//			IPath path = project.getLocation();
//			if ( path != null )
//			{
//				return path.toString();			
//			}
//			else
//			{
//				MsgBox.error("getCurrentProjectDir() : Project location is null " );
//				return null ;
//			}
//		}
//		else
//		{
//			MsgBox.error("getCurrentProjectDir() : Project is null " );
//			return null ;
//		}
//	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Returns the configuration file name for the given Eclipse project<br>
	 * Returns the "full path" file name ( i.e. "C:/aaa/bbb/workspace/project/telosys-tools.cfg" )
	 * @param project
	 * @return the configuration file name
	 */
	//private static String getProjectConfigFileName( IResource resource ) 
	public static String getProjectConfigFileName( IProject project ) 
	{
		//String sCurrentProjectDir = getCurrentProjectDir( project ) ;
//		if ( ! ( resource instanceof IProject ) )
//		{
//			MsgBox.error("getProjectConfigFileName(IResource) : "+
//					"resource is not an instance of IProject ");
//			return null ;
//		}
//		String sCurrentProjectDir = EclipseProjUtil.getProjectDir( (IProject) resource ) ;
		
		//String sCurrentProjectDir = EclipseProjUtil.getProjectDir( project ) ;
		String sCurrentProjectDir = getProjectConfigDir( project ) ;
		if ( sCurrentProjectDir != null )
		{
			return sCurrentProjectDir + "/" + PLUGIN_PROPERTIES_FILE ;
		}
		else
		{
			MsgBox.error("getProjectConfigFileName() : Cannot get project directory !" );
			return null ;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Set the current project ( or unset if the project is null ) 
//	 * @param javaProject 
//	 */
//	public static void setCurrentProject( IJavaProject javaProject ) 
//	{
//		IResource resourceProject = null ;
//		if ( javaProject != null )
//		{
//			resourceProject = javaProject.getResource() ;
//		}
//		setCurrentProject( resourceProject ) ;
//	}
	
	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Set the current project ( or unset if the project is null ) 
//	 * @param project
//	 */
//	public static void setCurrentProject( IResource project ) 
//	{
//		if ( project != null )
//		{
//			if ( project instanceof IProject )
//			{
//				$currentProject = (IProject) project ;
//			}
//			else
//			{
//				MsgBox.error("setCurrentProject() : argument is not an instance of IProject" );
//			}
//		}
//		
//	}
	
	//-------------------------------------------------------------------------------------------------
//	public static void setCurrentProjectConfigFileName( String sConfigFileName ) 
//	{
//		$sCurrentConfigFileName = sConfigFileName ;
//	}
	
	//-------------------------------------------------------------------------------------------------
//	protected static String getCurrentProjectConfigFileName() 
//	{
//		//return $sCurrentConfigFileName;
//		return getProjectConfigFileName($currentProject);
//	}
	
	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Returns the current project name 
//	 * @return
//	 */
//	private static String getCurrentProjectName() 
//	{
//		if ( $currentProject != null )
//		{
//			return $currentProject.getName();
//		}
//		else
//		{
//			return null ;
//		}
//	}
	
	//-------------------------------------------------------------------------------------------------
//	/**
//	 * Returns the instance of the current project configuration.
//	 * @return
//	 */
//	public static ProjectConfig getCurrentProjectConfig() 
//	{
//		if ( $currentProject != null )
//		{
//			return getProjectConfig( $currentProject ); 
//		}
//		else
//		{
//			String sMsg = "Cannot get current project configuration ( no current project )" ;
//			MsgBox.error( sMsg );
//			throw new RuntimeException(sMsg);
//		}
//	}
	
	//-------------------------------------------------------------------------------------------------
//	protected static Properties loadCurrentProjectProperties() 
//	{
//		if ( $currentProject != null )
//		{
//			return loadCurrentProjectProperties( $currentProject ); 
//		}
//		else
//		{
//			String sMsg = "Cannot get current project configuration ( no current project )" ;
//			MsgBox.error( sMsg );
//			throw new RuntimeException(sMsg);
//		}
//	}
	//-------------------------------------------------------------------------------------------------
	/**
	 * Loads the project's configuration from the file  
	 * @param project
	 * @return
	 */
	public static ProjectConfig loadProjectConfig(IProject project) 
	{
		PluginLogger.log("ProjectConfigManager.loadProjectConfig(p)..." );

		String sConfigFileName = getProjectConfigFileName(project);
		if ( sConfigFileName != null )
		{
			PluginLogger.log("ProjectConfigManager.loadProjectConfig(p) : " + sConfigFileName );
			PropertiesManager propManager = new PropertiesManager( sConfigFileName ) ;
			Properties prop = propManager.load(); // Ret NULL if file not found
			if ( prop != null )
			{
				// Properties loaded
				ProjectConfig projectConfig = new ProjectConfig(project, prop, sConfigFileName);
				// Store in cache
				$cache.put(project,projectConfig);
				return projectConfig ;
			}
			else
			{
				// File not found => no properties loaded
				MsgBox.info( "Configuration file not found.\n\n" 
						+ "(" + sConfigFileName + ")\n\n"
						+ "Cannot load properties.\n\n" );
				ProjectConfig projectConfig = new ProjectConfig(project, null, sConfigFileName);
				return projectConfig ;
			}
		}
		else
		{
			String sMsg = "Cannot get project configuration file name" ;
			MsgBox.error( sMsg );
			//throw new RuntimeException(sMsg);
			return null ;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ProjectConfig saveProjectConfig( IProject project, Properties prop ) 
	{
		PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop)..." );
		String sConfigFileName = getProjectConfigFileName(project);
		if ( sConfigFileName != null )
		{
			PropertiesManager propManager = new PropertiesManager( sConfigFileName ) ;
			propManager.save(prop);
			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : file = " + sConfigFileName );
			ProjectConfig projectConfig = new ProjectConfig(project, prop, sConfigFileName);
			// Store in cache
			$cache.put(project,projectConfig);
			return projectConfig ;
		}
		else
		{
			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : no config file name " );
			String sMsg = "Cannot save current project configuration" ;
			MsgBox.error( sMsg );
			//throw new RuntimeException(sMsg);
			return null ;
		}
	}
	
}
