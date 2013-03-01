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

/**
 * Generator configuration properties names <br>
 * for properties not available in the Velocity Context <br>
 * ( properties used as context variables are defined in ContextName ) 
 * 
 * @author Laurent GUERIN
 *
 */
public class GeneratorConfigConst {

    //--- Directories 
    public final static String REPOS_FOLDER      = "RepositoriesFolder";

    public final static String TEMPLATES_FOLDER  = "TemplatesFolder";

    public final static String DOWNLOADS_FOLDER  = "DownloadsFolder";
    
    //--- Packages 
    public final static String ENTITIES_PACKAGE  = "packageVo";

}
