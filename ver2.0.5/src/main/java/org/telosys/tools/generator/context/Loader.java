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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.velocity.VelocityContext;
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;

/**
 * Special class used as a specific class loader <br>
 * Used to load a specific Java Class tool in the Velocity Context
 * 
 * @author Laurent GUERIN
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName= ContextName.LOADER ,
		text = {
				"Special object used as a specific class loader by the generator",
				"",
				"Can be used to load a specific Java Class tool in the Velocity Context",
				"Allows users to create their own classes and to use them in the templates"
		},
		since = ""
 )
//-------------------------------------------------------------------------------------
public class Loader {

	private final static String CLASSES = "classes" ;
	
	private final ProjectConfiguration  projectConfig ;
	private final VelocityContext       velocityContext ;
	
	public Loader(ProjectConfiguration projectConfig, VelocityContext velocityContext) {
		super();
		this.projectConfig   = projectConfig;
		this.velocityContext = velocityContext;
	}

	//--------------------------------------------------------------------------------------------------------------
	/**
	 * Returns the folder where to search the classes to be loaded <br>
	 * The folder is "classes" in the project templates folder
	 * @return
	 */
	private File getClassesFolderAsFile()
	{
		String templatesFolder = projectConfig.getTemplatesFolderFullPath();
		String javaClassFolder ;		
		if ( templatesFolder.endsWith("/") || templatesFolder.endsWith("\\") ) {
			javaClassFolder = templatesFolder + CLASSES ;
		}
		else {
			javaClassFolder = templatesFolder + "/" + CLASSES ;
		}
		
		// Create a File object on the root of the directory containing the class file
		File file = new File(javaClassFolder);
		return file ;
	}
	
	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the folder where the classes are searched by the loader"
			},
		since="2.0.5"
	)
	public String getClassesFolder()
	{
		File folder = getClassesFolderAsFile();
		return folder.toString() ;
	}
	
	//--------------------------------------------------------------------------------------------------------------
	@VelocityMethod (
		text = {
				"Loads the given java class, creates an instance and put it in the Velocity context",
				"The Java 'class' file must be located in the 'templates/classes' folder of the project",
				"NB : The Java class must have a default constructor (in order to be created by 'javaClass.newInstance()'"
		},
		parameters = {
				"variableNameInContext : the name of the instance in the Velocity context", 
				"javaClassName : the Java class to be loaded "
		},
		example = {
				"## load the class and put an instance in the context",
				"$loader.loadJavaClass(\"tools\", \"MyClass\")",
				"## use the instance ",
				"$tools.myMethod()"
			}
		
	)
	public void loadJavaClass(String nameInContext, String javaClassName ) throws GeneratorException
	{
		File file = getClassesFolderAsFile();
		
		Class<?> javaClass = null ;
		
		try {
		    // Convert File to URL
		    URL url = file.toURL();          //  "file:/c:/templatesFolder/"
		    URL[] urls = new URL[]{url};

		    // Create a new class loader with the directory
		    ClassLoader classLoader = new URLClassLoader(urls);

		    // Load the class ( should be located in "file:/c:/templatesFolder/" )
		    javaClass = classLoader.loadClass(javaClassName);
		} catch (MalformedURLException e) {
			throw new GeneratorException("Cannot load class " + javaClassName + " (MalformedURLException)", e);
		} catch (ClassNotFoundException e) {
			throw new GeneratorException("Cannot load class " + javaClassName + " (ClassNotFoundException)", e);
		}
		
		Object instance = null ;
		if ( javaClass != null ) {
			try {
				instance = javaClass.newInstance() ;
			} catch (InstantiationException e) {
				throw new GeneratorException("Cannot create instance for " + javaClassName + " (InstantiationException)", e);
			} catch (IllegalAccessException e) {
				throw new GeneratorException("Cannot create instance for " + javaClassName + " (IllegalAccessException)", e);
			}
		}
		
		if ( instance != null ) {
			velocityContext.put(nameInContext, instance);
		}
	}
	
}
