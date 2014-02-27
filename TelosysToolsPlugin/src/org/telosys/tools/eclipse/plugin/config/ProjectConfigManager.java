package org.telosys.tools.eclipse.plugin.config;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.variables.Variable;
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
		TelosysToolsCfgManager telosysToolsCfgManager = new TelosysToolsCfgManager(projectFolder);
		TelosysToolsCfg telosysToolsCfg = null ;
		try {
			telosysToolsCfg = telosysToolsCfgManager.loadProjectConfig(); // Never null
		} catch (TelosysToolsException e) {
			MsgBox.error( "Cannot load configuration", e);
			//--- Create a void configuration
			telosysToolsCfg = new TelosysToolsCfg(telosysToolsCfgManager.getProjectAbsolutePath()) ; 
		}
		if ( ! telosysToolsCfg.hasBeenInitializedFromFile() ) {
			//--- No specific variables => initialize
			String projectName = project.getName() ;
			List<Variable> vars = new LinkedList<Variable>();
			//-- Keep alphabetic order
			vars.add( new Variable("MAVEN_ARTIFACT_ID", projectName            ) ) ; // for pom.xml artifactId
			vars.add( new Variable("MAVEN_GROUP_ID",    "group.to.be.defined"  ) ) ; // for pom.xml artifactId
			vars.add( new Variable("PROJECT_NAME",      projectName            ) ) ; 
			vars.add( new Variable("PROJECT_VERSION",   "0.1"                  ) ) ; 
			//-- Set variables in current configuration
			telosysToolsCfg.setSpecificVariables(vars);
		}
		
		return new ProjectConfig(project, telosysToolsCfg);
	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Saves the given configuration in the "telosys-tools.cfg" file in the given project
	 * @param project
	 * @param projectConfig
	 */
	public static void saveProjectConfig( IProject project, ProjectConfig projectConfig ) 
	{
		String projectFolder = EclipseProjUtil.getProjectDir( project );
		PluginLogger.log("ProjectConfigManager.saveProjectConfig("+projectFolder+", properties)..." );
		
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder);
		try {
			cfgManager.saveProjectConfig( projectConfig.getTelosysToolsCfg() ) ;
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
	}
}
