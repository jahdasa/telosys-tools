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

import org.telosys.tools.commons.Variable;
import org.telosys.tools.generator.context.ProjectConfiguration;

/**
 * Generator configuration interface 
 * 
 * @author Laurent GUERIN
 *
 */
public interface IGeneratorConfig {

//	public String getVOPackage() ;
	
    /**
     * Returns the full path directory where the project is located
     * @return
     */
    public String getProjectLocation();

	/**
	 * Returns the full path where the generator's templates are located
	 * @return
	 */
	public String getTemplatesFolderFullPath();
	
//	/**
//	 * Returns the specific variables to be put in the context
//	 * @return
//	 */
//	public Variable[] getProjectVariables();
	
	/**
	 * Returns the project configuration to be set in the generator's context 
	 * @return
	 */
	public ProjectConfiguration getProjectConfiguration() ;
}
