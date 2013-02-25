package org.telosys.tools.eclipse.plugin.config;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;

/**
 * 
 */
/* package */ class ProjectConfigs {

	/**
	 * Table (cache) of projects configurations 
	 * The key is the "Project Configuration File Name" 
	 * The value is the "ProjectConfig" instance
	 */
	private Hashtable<String,ProjectConfig> _htConfigs = new Hashtable<String,ProjectConfig>();
	
	//-------------------------------------------------------------------------------------------------
	protected void put(IProject project, ProjectConfig projectConfig)
	{
		if ( project == null )
		{
			MsgBox.error("ProjectConfigs.put() : project parameter is null ");
			return;
		}
		String sProjectName = project.getName();
		if ( sProjectName == null )
		{
			MsgBox.error("ProjectConfigs.put() : project name is null ");
			return;
		}		
		_htConfigs.put(sProjectName, projectConfig );
	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Returns the project configuration for the given config file name
	 * @param sConfigFileName
	 * @return
	 */
	protected ProjectConfig get( IProject project ) 
	{
		if ( project == null )
		{
			MsgBox.error("ProjectConfigs.get() : project parameter is null ");
			return null ;
		}

		String sProjectName = project.getName();
		if ( sProjectName == null )
		{
			MsgBox.error("ProjectConfigs.get() : project name is null ");
			return null;
		}
		
		Object obj = _htConfigs.get(sProjectName);
		if ( obj != null )
		{
			return (ProjectConfig) obj ;
		}
		else
		{
			return null ;
		}
	}
}
