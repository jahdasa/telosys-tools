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
package org.telosys.tools.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.context.Const;
import org.telosys.tools.generator.context.EmbeddedGenerator;
import org.telosys.tools.generator.context.Fn;
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.generator.context.JavaClass;
import org.telosys.tools.generator.context.Loader;
import org.telosys.tools.generator.context.ProjectConfiguration;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.context.Today;
import org.telosys.tools.generator.events.GeneratorEvents;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * This class is a Velocity generator ready to use. <br>
 * It is not supposed to be used directly by the application ( visibility "package" ) <br>
 * It is designed to be used only by the GenerationManager <br>
 * 
 * It holds : <br>
 * . the template file to use <br>
 * . the Velocity Engine <br>
 * . the Velocity Context  <br>
 * <br> 
 * After creation, each instance of this class has a Velocity Context initialized with <br>
 * . the generator variables : $generator, $today <br>
 * . the project variables <br>
 * . etc
 * 
 * @author Laurent Guerin
 *  
 */
public class Generator {

	public final static boolean CREATE_DIR = true ;
	public final static boolean DO_NOT_CREATE_DIR = false ;
	
	
	private final VelocityEngine     _velocityEngine ;

	private final VelocityContext    _velocityContext ;

	private final IGeneratorConfig   _generatorConfig ;
	
	private final TelosysToolsLogger _logger ;

	private final String             _sTemplateFileName ;

	/**
	 * Constructor
	 * @param target the target to be generated
	 * @param generatorConfig the generator configuration
	 * @param logger 
	 * @throws GeneratorException
	 */
	public Generator( Target target, IGeneratorConfig generatorConfig, TelosysToolsLogger logger) throws GeneratorException 
	{
		_logger = logger;
		
		if ( null == target) {
			throw new GeneratorException("Target is null (Generator constructor argument)");
		}
		String sTemplateFileName = target.getTemplate(); 
		
		log("Generator constructor (" + sTemplateFileName + ")");

		if ( null == sTemplateFileName) {
			throw new GeneratorException("Template file name is null (Generator constructor argument)");
		}
		if ( null == generatorConfig) {
			throw new GeneratorException("Generator configuration is null (Generator constructor argument)");
		}
		
		_generatorConfig = generatorConfig ;
		
		//------------------------------------------------------------------
		// 1) Init Velocity context
		//------------------------------------------------------------------
		//--- Create a context
		log("Generator constructor : VelocityContext creation ...");
		_velocityContext = new VelocityContext();
		log("Generator constructor : VelocityContext created.");
		
		log("Generator constructor : VelocityContext events attachment ...");
		GeneratorEvents.attachEvents(_velocityContext);
		log("Generator constructor : VelocityContext events attached.");

		log("Generator constructor : VelocityContext initialization ...");
		initContext(generatorConfig, logger); 
		log("Generator constructor : VelocityContext initialized.");
		
		//------------------------------------------------------------------
		// 2) Init Velocity engine
		//------------------------------------------------------------------
		//--- Get the templates directory and use it to initialize the engine		
		String sTemplateDirectory = generatorConfig.getTemplatesFolderFullPath();		
		log("Templates Directory : '" + sTemplateDirectory + "'");

		//--- Check template file existence		
		checkTemplate(sTemplateDirectory, sTemplateFileName);
		_sTemplateFileName  = sTemplateFileName;

		log("Generator constructor : VelocityEngine initialization ...");
		_velocityEngine = new VelocityEngine();
		_velocityEngine.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, sTemplateDirectory);
		try {
			_velocityEngine.init();
		} catch (Exception e) {
			throw new GeneratorException("Cannot init VelocityEngine", e );
		}
		log("Generator constructor : VelocityEngine initialized.");
	}

	private void log(String s) {
		if (_logger != null) {
			_logger.log(s);
		}
	}

	private void checkTemplate(String sTemplateDirectory,
			String sTemplateFileName) throws GeneratorException {
		if (sTemplateDirectory == null) {
			throw new GeneratorException("Template directory is null !");
		}
		if (sTemplateFileName == null) {
			throw new GeneratorException("Template file name is null !");
		}
		File dir = new File(sTemplateDirectory);
		if (!dir.exists()) {
			throw new GeneratorException("Template directory '"
					+ sTemplateDirectory + "' doesn't exist !");
		}
		if (!dir.isDirectory()) {
			throw new GeneratorException("Template directory '"
					+ sTemplateDirectory + "' is not a directory !");
		}

		String sTemplateFullPath = null;
		if (sTemplateDirectory.endsWith("/")) {
			sTemplateFullPath = sTemplateDirectory + sTemplateFileName;
		} else {
			sTemplateFullPath = sTemplateDirectory + "/" + sTemplateFileName;
		}
		File file = new File(sTemplateFullPath);
		if (!file.exists()) {
			throw new GeneratorException("Template file '" + sTemplateFullPath
					+ "' doesn't exist !");
		}
		if (!file.isFile()) {
			throw new GeneratorException("Template file '" + sTemplateFullPath
					+ "' is not a file !");
		}
	}
	
	//========================================================================
	// CONTEXT MANAGEMENT
	//========================================================================
	private void initContext( IGeneratorConfig generatorConfig, TelosysToolsLogger logger)
	{
		log("initContext()..." );

		//--- Special Characters  [LGU 2012-11-29 ]
		_velocityContext.put(ContextName.DOLLAR , "$"  );
		_velocityContext.put(ContextName.SHARP,   "#"  );
		_velocityContext.put(ContextName.AMP,     "&"  ); // ampersand 
		_velocityContext.put(ContextName.QUOT,    "\"" ); // double quotation mark
		_velocityContext.put(ContextName.LT,      "<"  ); // less-than sign
		_velocityContext.put(ContextName.GT,      ">"  ); // greater-than sign
		_velocityContext.put(ContextName.LBRACE,  "{"  ); // left brace
		_velocityContext.put(ContextName.RBRACE,  "}"  ); // right brace
		
		//--- Set the standard Velocity variables in the context
		_velocityContext.put(ContextName.GENERATOR,     new EmbeddedGenerator());  // Limited generator without generation capability 
		_velocityContext.put(ContextName.TODAY,         new Today()); // Current date and time 
		_velocityContext.put(ContextName.CONST,         new Const()); // Constants (static values)
		_velocityContext.put(ContextName.FN,            new Fn());    // Utility function
		_velocityContext.put(ContextName.CLASS, null);
		
		ProjectConfiguration projectConfiguration = generatorConfig.getProjectConfiguration();
		
		//--- Set the dynamic loader 
		Loader loader = new Loader(projectConfiguration, _velocityContext);
		_velocityContext.put(ContextName.LOADER, loader);
		
		//--- Set the "$project" variable in the context
		_velocityContext.put(ContextName.PROJECT, projectConfiguration);
		
		//--- Get the project variables and put them in the context	
		Variable[] projectVariables = projectConfiguration.getVariables();
		log("initContext() : Project variables count = " + ( projectVariables != null ? projectVariables.length : 0 ) );

		//--- Set the project variables in the context ( if any )
		if ( projectVariables != null )
		{
			for ( int i = 0 ; i < projectVariables.length ; i++ )
			{
				Variable var = projectVariables[i];
				_velocityContext.put( var.getName(), var.getValue() );
			}
		}
	}

	/**
	 * Set the selected entities Java Bean class in the context <br>
	 * Useful for "Multi-Entities" targets 
	 * @param javaBeanClasses
	 * @since Version 2.0.3 ( 2013-Feb )
	 */
	public void setSelectedEntitiesInContext( List<JavaBeanClass> javaBeanClasses )
	{
		if ( javaBeanClasses != null ) {
			_velocityContext.put(ContextName.SELECTED_ENTITIES, javaBeanClasses);
		}
	}
	
	/**
	 * Set the current JavaClass target in the context ( the "$class" variable ) <br>
	 * Useful for WIZARDS to set the current "$class"
	 * 
	 * @param javaClass
	 */
	public void setJavaClassTargetInContext(JavaClass javaClass) 
	{
		_velocityContext.put(ContextName.CLASS, javaClass);
	}
	
	/**
	 * Set a new attribute (variable) in the Velocity Context <br>
	 * Useful for WIZARDS to add specific variables if necessary 
	 * 
	 * @param sName
	 * @param oValue
	 */
	public void setContextAttribute(String sName, Object oValue) 
	{
		_velocityContext.put(sName, oValue);
	}

	/**
	 * Returns the Velocity Template instance
	 * @return
	 * @throws GeneratorException
	 */
	private Template getTemplate() throws GeneratorException {
		if (_velocityEngine == null) {
			throw new GeneratorException("Velocity engine is null!");
		}
		log("getTemplate() : Template file name = '" + _sTemplateFileName + "'");
		Template template = null;
		try {
			template = _velocityEngine.getTemplate(_sTemplateFileName);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			throw new GeneratorException("Cannot get template : ResourceNotFoundException ! ", e );
		} catch (ParseErrorException e) {
			e.printStackTrace();
			throw new GeneratorException("Cannot get template : Velocity ParseErrorException ! ", e );
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneratorException("Cannot get template : Exception ! ", e );
		}
		return template;
	}

	private void generate(Writer writer, Template template)
			throws GeneratorException {
		log("generate(writer, template)...");
		try {
			//--- Generate in a Writer
			template.merge(_velocityContext, writer);
		} catch (ResourceNotFoundException e) {
			throw new GeneratorException("Generation error : ResourceNotFoundException ", e);
		} catch (ParseErrorException e) {
			throw new GeneratorException("Generation error : ParseErrorException ", e);
		} catch (MethodInvocationException e) {
			throw new GeneratorException("Generation error : MethodInvocationException ", e);
		} catch (GeneratorContextException e) {
			throw new GeneratorException("Generation error : GeneratorContextException ", e);
		} catch (Exception e) {
			throw new GeneratorException("Generation error : Exception ", e);
		}
	}

	private void generate(Writer writer) throws GeneratorException {
		log("generate(writer) : getTemplate() ...");
		Template template = getTemplate();
		log("generate(writer) : generate(writer, template) ...");		
		generate(writer, template);
	}

	/**
	 * Generates in memory and returns the InputStream on the generation result
	 * @return
	 * @throws GeneratorException
	 */
	public InputStream generateInMemory() throws GeneratorException {
		log("generateInMemory()...");
		StringWriter stringWriter = new StringWriter();
		generate(stringWriter);
		byte[] bytes = stringWriter.toString().getBytes();
		return new ByteArrayInputStream(bytes);
	}

	//================================================================================================
	// generateTarget moved from GenerationManager to Generator 
	//================================================================================================
	/**
	 * Generated the given target 
	 * @param target the target to be generated
	 * @param repositoryModel the repository model 
	 * @param generatedTargets list of generated targets to be updated (or null if not useful)
	 * @throws GeneratorException
	 */
	public void generateTarget(Target target, RepositoryModel repositoryModel, List<Target> generatedTargets) throws GeneratorException
	{
		_logger.info("Generation in progress : target = " + target.getTargetName() + " / entity = " + target.getEntityName() );
		
		ProjectConfiguration projectConfiguration = _generatorConfig.getProjectConfiguration();
		
		// 2013-02-04
		//JavaBeanClass javaBeanClass = RepositoryModelUtil.buildJavaBeanClass(target, repositoryModel, projectConfiguration) ;
		JavaBeanClass javaBeanClass = null ;
		if ( target.getEntityName().trim().length() > 0 ) {
			//--- Target with entity ( classical target )
			javaBeanClass = RepositoryModelUtil.buildJavaBeanClass(target.getEntityName(), repositoryModel, projectConfiguration) ;
		}
		else {
			//--- Target without entity ( e.g. "once" target )
			javaBeanClass = null ;
		}
		

		//---------- Set additional objects in the Velocity Context
		//--- Set the "$target"  in the context 
		_velocityContext.put(ContextName.TARGET, target);
		//--- Set the "$beanClass"  in the context ( the Java Bean Class for this target )
		_velocityContext.put(ContextName.BEAN_CLASS, javaBeanClass );
		//--- Set the "$generator"  in the context ( "real" embedded generator )
		EmbeddedGenerator embeddedGenerator = new EmbeddedGenerator(repositoryModel, _generatorConfig, _logger, generatedTargets );
		_velocityContext.put(ContextName.GENERATOR, embeddedGenerator );
		
		//---------- Generate the target in memory
		InputStream is = generateInMemory();
		_logger.info("Generation done.");

		//---------- Save the result in the file
		String outputFileName = target.getOutputFileNameInFileSystem( _generatorConfig.getProjectLocation() );
		_logger.info("Saving target file : " + outputFileName );
		saveStreamInFile(is, outputFileName, true );
		_logger.info("Target file saved." );
		
		//---------- Add the generated target in the list if any
		if ( generatedTargets != null ) {
			generatedTargets.add(target);
		}
	}
	
	private void saveStreamInFile(InputStream is, String fileName, boolean bCreateDir) throws GeneratorException
	{
		File f = new File(fileName);
		
		//--- Check if it's possible to write the file
		if ( f.exists() )
		{
			if ( ! f.canWrite() )				
			{
				throw new GeneratorException("Cannot write on existing target file '"+ f.toString() + "' !");
			}
		}
		else
		{
			File parent = f.getParentFile();
			if ( ! parent.exists() )
			{
				if ( bCreateDir == false )
				{
					throw new GeneratorException("Target directory '"+ parent.toString() + "' not found !");
				}
				else
				{
					// Create the target file directory(ies)
					parent.mkdirs();				
				}
			}
		}
		
		//--- Write the file
		try {
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			is.close();
		} catch (FileNotFoundException e) {
			throw new GeneratorException("Cannot save file "+fileName, e);
		} catch (IOException e) {
			throw new GeneratorException("Cannot save file "+fileName, e);
		}
	}
	
}