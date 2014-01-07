package org.telosys.tools.eclipse.plugin.config;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
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
		
		PluginLogger.log("ProjectConfigManager.getProjectConfig(project) : Load config..." );
		ProjectConfig projectConfig = loadProjectConfig(project) ;
		return projectConfig ;
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * Loads the project's configuration from the properties file  
	 * @param project
	 * @return
	 */
	public static ProjectConfig loadProjectConfig(IProject project) 
	{
		
		String projectFolder = EclipseProjUtil.getProjectDir( project );
		PluginLogger.log("ProjectConfigManager.loadProjectConfig("+projectFolder+")..." );
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder);
		TelosysToolsCfg telosysToolsCfg = null ;
		try {
			telosysToolsCfg = cfgManager.loadProjectConfig(); // Never null
		} catch (TelosysToolsException e) {
			MsgBox.error( "Cannot load configuration", e);
			//--- Create a void configuration
			telosysToolsCfg = new TelosysToolsCfg(cfgManager.getProjectAbsolutePath(), 
					cfgManager.getCfgFileAbsolutePath(), null);
		}
		
		return new ProjectConfig(project, telosysToolsCfg);
		
//		String sConfigFileName = getProjectConfigFileName(project);
//		if ( sConfigFileName != null )
//		{
//			PluginLogger.log("ProjectConfigManager.loadProjectConfig(p) : " + sConfigFileName );
//			PropertiesManager propManager = new PropertiesManager( sConfigFileName ) ;
//			Properties prop = propManager.load(); // Ret NULL if file not found
//			if ( prop != null )
//			{
//				// Properties loaded
//				ProjectConfig projectConfig = new ProjectConfig(project, prop, sConfigFileName);
////				// Store in cache
////				$cache.put(project,projectConfig);
//				return projectConfig ;
//			}
//			else
//			{
//				// Properties file not found, no properties loaded : use default values
//				ProjectConfig projectConfig = new ProjectConfig(project, null, sConfigFileName);
//				return projectConfig ;
//			}
//		}
//		else
//		{
//			String sMsg = "Cannot get project configuration file name" ;
//			MsgBox.error( sMsg );
//			//throw new RuntimeException(sMsg);
//			return null ;
//		}
	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Saves the given properties in the "telosys-tools.cfg" file in the given project
	 * @param project
	 * @param prop
	 */
	public static void saveProjectConfig( IProject project, Properties prop ) 
	{
		String projectFolder = EclipseProjUtil.getProjectDir( project );
		PluginLogger.log("ProjectConfigManager.saveProjectConfig("+projectFolder+", properties)..." );
		
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder);
		try {
			cfgManager.saveProjectConfig(prop) ;
		} catch (TelosysToolsException e) {
			MsgBox.error( "Cannot save configuration", e);
		}

		//--- Refresh the file in the Eclipse Workspace
		String cfgFileAbsolutePath = cfgManager.getCfgFileAbsolutePath();
		File file = new File(cfgFileAbsolutePath);
		if ( file.exists() ) {
			EclipseWksUtil.refresh(file);
		}
		else {
			MsgBox.error( "Cannot refresh file \n"
					+ "'" + cfgFileAbsolutePath + "' \n"
					+ "This file doesn't exist");
		}
		
//		PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop)..." );
//		String sConfigFileName = getProjectConfigFileName(project);
//		if ( sConfigFileName != null )
//		{
//			PropertiesManager propManager = new PropertiesManager( sConfigFileName ) ;
//			propManager.save(prop);
//			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : file = " + sConfigFileName );
//			//--- Refresh the file in the Eclipse Workspace
//			File file = new File(sConfigFileName);
//			EclipseWksUtil.refresh(file);
//			
////			//--- Update the configuration cache
////			ProjectConfig projectConfig = new ProjectConfig(project, prop, sConfigFileName);
////			$cache.put(project,projectConfig);
////			return projectConfig ;
//		}
//		else
//		{
//			PluginLogger.log("ProjectConfigManager.saveProjectConfig(project, prop) : no config file name " );
//			String sMsg = "Cannot save current project configuration" ;
//			MsgBox.error( sMsg );
////			return null ;
//		}
	}
	
}
