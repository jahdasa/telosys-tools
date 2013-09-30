package org.telosys.tools.eclipse.plugin.config;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

/**
 * Projects configuration manager <br>
 * . save and load the projects configuration<br>
 * . hold each project configuration in a cache<br>
 */
public class ProjectConfigManager {

    private final static String PLUGIN_PROPERTIES_FILE = "telosys-tools.cfg";

//	/**
//	 * Table (cache) of projects configurations 
//	 * The key is the "Project Configuration File Name" 
//	 * The value is the "ProjectConfig" instance
//	 */
//    private static ProjectConfigs $cache = new ProjectConfigs();
// cache REMOVED IN V 2.0.6
	
	
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
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Returns the project configuration for the given Eclipse project<br>
	 * @param project
	 * @return the project configuration or null if the configuration file doesn't exist
	 */
	public static ProjectConfig getProjectConfig( IProject project ) 
	{
		PluginLogger.log("ProjectConfigManager.getProjectConfig(project)..." );
		if ( project == null )
		{
			MsgBox.error("getProjectConfig : project parameter is null ");
			return null ;
		}
		
//		ProjectConfig projectConfig = $cache.get(project);
//		if ( projectConfig != null )
//		{
//			PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Found in cache." );
//			return projectConfig ;
//		}
//		else
//		{
//			PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Not found in cache => load." );
//			// Not yet loaded => load it now 
//			loadProjectConfig(project) ;
//			projectConfig = $cache.get(project);
//			return projectConfig ;
//		}

		PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Load config..." );
		ProjectConfig projectConfig = loadProjectConfig(project) ;
		return projectConfig ;
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * Returns the configuration file name for the given Eclipse project<br>
	 * Returns the "full path" file name ( i.e. "C:/aaa/bbb/workspace/project/telosys-tools.cfg" )
	 * @param project
	 * @return the configuration file name
	 */
	public static String getProjectConfigFileName( IProject project ) 
	{
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
	/**
	 * Loads the project's configuration from the properties file  
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
//				// Store in cache
//				$cache.put(project,projectConfig);
				return projectConfig ;
			}
			else
			{
				// Properties file not found, no properties loaded : use default values
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
	//public static ProjectConfig saveProjectConfig( IProject project, Properties prop ) 
	public static void saveProjectConfig( IProject project, Properties prop ) 
	{
		PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop)..." );
		String sConfigFileName = getProjectConfigFileName(project);
		if ( sConfigFileName != null )
		{
			PropertiesManager propManager = new PropertiesManager( sConfigFileName ) ;
			propManager.save(prop);
			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : file = " + sConfigFileName );
			//--- Refresh the file in the Eclipse Workspace
			File file = new File(sConfigFileName);
			EclipseWksUtil.refresh(file);
			
//			//--- Update the configuration cache
//			ProjectConfig projectConfig = new ProjectConfig(project, prop, sConfigFileName);
//			$cache.put(project,projectConfig);
//			return projectConfig ;
		}
		else
		{
			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : no config file name " );
			String sMsg = "Cannot save current project configuration" ;
			MsgBox.error( sMsg );
//			return null ;
		}
	}
	
}
