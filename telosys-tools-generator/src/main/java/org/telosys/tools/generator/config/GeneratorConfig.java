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
package org.telosys.tools.generator.config;

import java.util.Properties;

import org.telosys.tools.commons.Variable;
import org.telosys.tools.commons.VariablesUtil;
import org.telosys.tools.generator.context.ProjectConfiguration;

/**
 * Generator configuration implementation 
 * 
 * @author Laurent GUERIN
 *
 */
public class GeneratorConfig implements IGeneratorConfig
{
	public static final String PROJECT_CONFIG_FILE = "telosys-tools.cfg" ;
	
	private String _sProjectLocation ; // Project folder ( absolute path )
	
	private Variable[] _projectVariables ;
	
	private String _sSourceFolder ;
	private String _sWebContentFolder ;
	
	private String _sTemplatesFolder ;
	
	private String _sPackageVO ;

	public GeneratorConfig(String sProjectLocation, Properties prop) 
	{
		_sProjectLocation = sProjectLocation;
		
		//--- Files folders
    	_sSourceFolder       = prop.getProperty(GeneratorConfigConst.SOURCE_FOLDER, _sSourceFolder);
    	_sWebContentFolder   = prop.getProperty(GeneratorConfigConst.WEB_CONTENT_FOLDER, _sWebContentFolder);
    	_sTemplatesFolder    = prop.getProperty(GeneratorConfigConst.TEMPLATES_FOLDER, _sTemplatesFolder);
		
		//--- Packages names
    	_sPackageVO        = prop.getProperty(GeneratorConfigConst.PACKAGE_VO, _sPackageVO);
    	
    	//--- Project user defined variables
    	_projectVariables = VariablesUtil.getVariablesFromProperties( prop );
    	
	}

	//---------------------------------------------------------------------
	// Specific variables
	//---------------------------------------------------------------------
	public Variable[] getProjectVariables() 
	{
		return _projectVariables ;
	}

	//---------------------------------------------------------------------
	// Project Configuration for Generator context
	//---------------------------------------------------------------------
	public ProjectConfiguration getProjectConfiguration() 
	{
		ProjectConfiguration projectConfiguration = new ProjectConfiguration( 
				_sSourceFolder, _sWebContentFolder, getTemplatesFolderFullPath(),
				_sPackageVO, 
				_projectVariables );
		
		return projectConfiguration;
	}

	//---------------------------------------------------------------------
	// Folders
	//---------------------------------------------------------------------
    public String getProjectLocation()
	{
    	return _sProjectLocation ;
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
    			// No templates folder 
    			return null ;
    		}
    		else
    		{
    			// Templates folder is set => build the full path
    			return getProjectFullPath(_sTemplatesFolder) ;
    		}
    	}
    	else
    	{
    		return null ;
    	}
	}

	//---------------------------------------------------------------------
	// Packages
	//---------------------------------------------------------------------
	public String getVOPackage() {
		return _sPackageVO ;
	}

}
