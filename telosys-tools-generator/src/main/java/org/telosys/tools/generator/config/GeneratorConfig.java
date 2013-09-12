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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

import org.telosys.tools.commons.Variable;
import org.telosys.tools.commons.VariablesUtil;
import org.telosys.tools.generator.ContextName;
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
	
	private final String     _sProjectLocation ; // Project folder ( absolute path )
	
	private final String     _sTemplatesFolder ;
	
	private final String     _sEntityClassPackage ;

	private final Variable[] _projectVariables ;
	
	public GeneratorConfig(String sProjectLocation, Properties prop) 
	{
		_sProjectLocation = sProjectLocation;
		
		//--- Files folders
    	_sTemplatesFolder     = prop.getProperty(GeneratorConfigConst.TEMPLATES_FOLDER, null);
		
		//--- Packages names
    	//_sEntityClassPackage  = prop.getProperty(GeneratorConfigConst.ENTITIES_PACKAGE, null);
    	_sEntityClassPackage  = prop.getProperty(ContextName.ENTITY_PKG, null); // v 2.0.6
    	
    	//--- All variables : specific project variables + folders 
    	Hashtable<String, String> allVariables = new Hashtable<String, String>();
    	
    	//--- 1) Project user defined variables
    	Variable[] specificVariables = VariablesUtil.getVariablesFromProperties( prop );
    	for ( Variable v : specificVariables ) {
    		allVariables.put(v.getName(), v.getValue());
    	}
    	//--- 2) Packages and folders ( at the end to override specific variables if any )
    	allVariables.put( ContextName.ROOT_PKG,   prop.getProperty(ContextName.ROOT_PKG,    "") ); // v 2.0.6
    	allVariables.put( ContextName.ENTITY_PKG, prop.getProperty(ContextName.ENTITY_PKG,  "") ); // v 2.0.6
    	
    	allVariables.put( ContextName.SRC,      prop.getProperty(ContextName.SRC,      "") );
    	allVariables.put( ContextName.RES,      prop.getProperty(ContextName.RES,      "") );
    	allVariables.put( ContextName.WEB,      prop.getProperty(ContextName.WEB,      "") );
    	allVariables.put( ContextName.TEST_SRC, prop.getProperty(ContextName.TEST_SRC, "") );
    	allVariables.put( ContextName.TEST_RES, prop.getProperty(ContextName.TEST_RES, "") );
    	allVariables.put( ContextName.DOC,      prop.getProperty(ContextName.DOC,      "") );
    	allVariables.put( ContextName.TMP,      prop.getProperty(ContextName.TMP,      "") );
    	
    	//--- 3) Get all variables to build the array
    	LinkedList<Variable> variablesList = new LinkedList<Variable>();
    	for ( String varName : allVariables.keySet() ) {
    		String varValue = allVariables.get(varName) ;
    		variablesList.add( new Variable( varName, varValue) ) ;
    	}
    	Variable[] allVariablesArray = variablesList.toArray( new Variable[0] );
    	
    	_projectVariables = allVariablesArray ;
    	
	}

//	//---------------------------------------------------------------------
//	// Specific variables
//	//---------------------------------------------------------------------
//	public Variable[] getProjectVariables() 
//	{
//		return _projectVariables ;
//	}

	//---------------------------------------------------------------------
	// Project Configuration for Generator context
	//---------------------------------------------------------------------
	public ProjectConfiguration getProjectConfiguration() 
	{
		ProjectConfiguration projectConfiguration = new ProjectConfiguration( 
				//_sSourceFolder, _sWebContentFolder, 
				getTemplatesFolderFullPath(),
				_sEntityClassPackage, 
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

//	//---------------------------------------------------------------------
//	// Packages
//	//---------------------------------------------------------------------
//	public String getVOPackage() {
//		return _sPackageVO ;
//	}

}
