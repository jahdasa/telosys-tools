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

import org.telosys.tools.commons.Variable;
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.context.doc.VelocityObject;


/**
 * The current project configuration parameters ( folders, packages, ... )
 *  
 * @author Laurent GUERIN
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName= ContextName.PROJECT ,
		text = "Current project configuration parameters ( folders, packages, ... )<br>"
			+ " "
			,
		since = ""
 )
//-------------------------------------------------------------------------------------
public class ProjectConfiguration
{
	private final static Variable[] VOID_VARIABLES = new Variable[0];
	
//	private final String srcFolder ;
//	private final String webFolder ;
	private final String templatesFolderFullPath ;
	
	private final String packageForBean ;

	private final Variable[] projectVariables ;
	
    //---------------------------------------------------------------------------

	public ProjectConfiguration( // String srcFolder, String webFolder, 
			String templatesFolderFullPath,
			String packageForBean, 
			Variable[] projectVariables ) 
	{
		super();
//		this.srcFolder = srcFolder;
//		this.webFolder = webFolder;
		this.templatesFolderFullPath = templatesFolderFullPath ;
		
		this.packageForBean = packageForBean;
		
		this.projectVariables = projectVariables;
	}

	//---------------------------------------------------------------------------

//	public String getSrcFolder()
//    {
//        return srcFolder ;
//    }
//
//    public String getWebFolder()
//    {
//        return webFolder ;
//    }

    /**
     * Returns the templates folder with full path
     * @return
     */
    public String getTemplatesFolderFullPath()
    {
        return templatesFolderFullPath ;
    }

    //---------------------------------------------------------------------------
    
    public String getPackageForBean()
    {
        return packageForBean ;
    }

    //---------------------------------------------------------------------------
    public Variable[] getVariables()
    {
    	if ( projectVariables != null ) {
            return projectVariables ;
    	}
    	else {
    		return VOID_VARIABLES ;
    	}
    }
}