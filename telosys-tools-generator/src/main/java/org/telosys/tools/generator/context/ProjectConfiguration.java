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
package org.telosys.tools.generator.context;

import java.util.Properties;

import org.telosys.tools.commons.variables.Variable;
import org.telosys.tools.commons.variables.VariablesUtil;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;


/**
 * The current project configuration parameters ( folders, packages, ... )
 *  
 * @author Laurent GUERIN
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName= ContextName.PROJECT ,
		text = "Current project configuration parameters ( variables, folders, ... )",
		since = ""
 )
//-------------------------------------------------------------------------------------
public class ProjectConfiguration
{
	private final static Variable[] VOID_VARIABLES = new Variable[0];
	
	private final String templatesFolderFullPath ;
	
	private final String packageForBean ;

	private final Variable[] specificVariables ;
	private final Variable[] allVariables ;
	
    //---------------------------------------------------------------------------

//	public ProjectConfiguration( 
//			String templatesFolderFullPath,
//			String packageForBean, 
//			Variable[] projectVariables ) 
//	{
//		super();
//		this.templatesFolderFullPath = templatesFolderFullPath ;
//		
//		this.packageForBean = packageForBean;
//		
//		this.projectVariables = projectVariables;
//	}
	public ProjectConfiguration( 
			String templatesFolderFullPath,
			String packageForBean, 
			Properties projectProperties ) 
	{
		super();
		this.templatesFolderFullPath = templatesFolderFullPath ;
		
		this.packageForBean = packageForBean;
		
		this.specificVariables = VariablesUtil.getVariablesFromProperties(projectProperties);
		this.allVariables      = VariablesUtil.getAllVariablesFromProperties(projectProperties);
	}

	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod (
		text = { 
				"Returns the templates folder (the full path)",
				"(just for information, not supposed to be used in generation)" },
		example = {
				"$project.templatesFolderFullPath"
			}
	)
    public String getTemplatesFolderFullPath()
    {
        return templatesFolderFullPath ;
    }

	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod (
		text = { 
				"Returns the package for the Java Bean classes",
				"(just for information, not supposed to be used in generation)" },
		example = {
				"$project.packageForBean"
			}
		)
    public String getPackageForBean()
    {
        return packageForBean ;
    }

	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod (
		text = { 
				"Returns the specific variables defined for the current project",
				"( the specific variables defined for the project and the standard variables )"},
		example = {
				"#foreach( $var in $project.specificVariables )",
				"  $var.name = $var.value",
				"#end"
			},
		since = "2.0.7"
	)
    public Variable[] getSpecificVariables()
    {
    	if ( specificVariables != null ) {
            return specificVariables ;
    	}
    	else {
    		return VOID_VARIABLES ;
    	}
    }
	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod (
		text = { 
				"Returns all the variables available for the current project",
				"( the specific variables defined for the project and the standard variables )"},
		example = {
				"#foreach( $var in $project.allVariables )",
				"  $var.name = $var.value",
				"#end"
			},
		since = "2.0.7"
	)
    public Variable[] getAllVariables()
    {
    	if ( allVariables != null ) {
            return allVariables ;
    	}
    	else {
    		return VOID_VARIABLES ;
    	}
    }
}