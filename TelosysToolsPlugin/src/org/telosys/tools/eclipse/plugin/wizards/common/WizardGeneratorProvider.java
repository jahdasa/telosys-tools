package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.core.resources.IProject;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.GeneratorConfigManager;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.context.Target;


/**
 * @author Laurent GUERIN
 *  
 */
public class WizardGeneratorProvider {
	
//	//----------------------------------------------------------------------------------------
//	/**
//	 * Returns a Velocity generator for the given template file,<br> 
//	 * with a basic context (containing "today" and "generator") and a default logger
//	 * 
//	 * @param project
//	 * @param sTemplateFile
//	 * @return
//	 * @throws GeneratorException
//	 */
//	private static Generator getGenerator( IProject project, String sTemplateFile ) throws GeneratorException 
//	{				
//		PluginLogger.log("GeneratorProvider.getGenerator(project, sTemplateFile )...");
//
////		if ( project == null )
////		{
////			MsgBox.error("getJavaClassGenerator() : project parameter is null");
////		}
////		
////		//--- Get the project configuration
////		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig(project);
////		
////		//--- Get the templates directory		
////		String sTemplateDirectory = projectConfig.getTemplatesFolderFullPath();		
////		PluginLogger.log("Template Directory : " + sTemplateDirectory);
////		
////		//--- Create the GENERATOR 
////		Generator generator = new Generator(sTemplateDirectory,
////				sTemplateFile,
////				new GeneratorDefaultLogger());
////		
////		return generator ;
//
////		return getGenerator(project, sTemplateFile, new OldGeneratorDefaultLogger() ) ;
//		return getGenerator(project, sTemplateFile, new ConsoleLogger() ) ;
//	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns a Velocity generator for the given template file,<br> 
	 * with a basic context (containing "today" and "generator") and a specific logger
	 * 
	 * @param project
	 * @param sTemplateFile
	 * @param logger
	 * @return
	 * @throws GeneratorException
	 */
	public static Generator getGenerator( IProject project, String sTemplateFile, TelosysToolsLogger logger ) throws GeneratorException 
	{				
		PluginLogger.log("getGenerator(project, " + sTemplateFile + ", logger )...");

		if ( project == null )
		{
			MsgBox.error("GeneratorProvider.getGenerator() : project parameter is null"
					+ "\n Cannot get project configuration ! ");
		}
		
//		String sConfigFullFileName   = ProjectConfigManager.getProjectConfigFileName(project);
//		PluginLogger.log("getGenerator() : config full file name = " + sConfigFullFileName );
//		
//		//--- Init the generator configuration from the "telosys-tools.cfg" file 
//		GeneratorConfigManager mgr = new GeneratorConfigManager(logger);
//		PluginLogger.log("getGenerator() : GeneratorConfigManager created. " );
//		
//		IGeneratorConfig generatorConfig;
//		try {
//			generatorConfig = mgr.initFromFile(sConfigFullFileName);
//		} catch (GeneratorException e) {
//			MsgBox.error("Cannot initialize generator configuration \n"
//			+ "\n Config file name = '" + sConfigFullFileName + "'" );
//			throw e ;
//		}
//		PluginLogger.log("getGenerator() : GeneratorConfigManager initialized from file " + sConfigFullFileName);
//		
//		//--- Create the TARGET 
//		Target target = new Target( sTemplateFile ); // [LGU] 2012-11-30
//		
//		//--- Create the GENERATOR 
//		PluginLogger.log("getGenerator() : try to create a new Generator instance... " );
//		Generator generator = new Generator(target, generatorConfig, null, logger); // v 2.0.7
//		PluginLogger.log("getGenerator() : Generator instance created. " );
//
//		return generator ;
		
		// TODO : WIZARDS to be removed
		return null ;
	}
	
	//----------------------------------------------------------------------------------------
//	/**
//	 * Returns a Velocity generator for the given template file, with an initialized context 
//	 * @param project the Eclipse project where to process the generation
//	 * @param sTemplateFile
//	 * @param sPackage
//	 * @param sClassName
//	 * @return
//	 * @throws GeneratorException
//	 */
//	public static Generator getJavaClassGenerator( IProject project, String sTemplateFile, String sPackage, String sClassName ) throws GeneratorException 
//	{
//		return getJavaClassGenerator( project, sTemplateFile, sPackage, sClassName, null );
//	}
	
//	//----------------------------------------------------------------------------------------
//	/**
//	 * Returns a Velocity generator for the given template file, with an initialized context 
//	 * @param project the Eclipse project where to process the generation
//	 * @param sTemplateFile
//	 * @param sPackage
//	 * @param sClassName
//	 * @param sSuperClass
//	 * @return
//	 * @throws GeneratorException
//	 */
//	public static Generator getJavaClassGenerator( IProject project, String sTemplateFile, String sPackage, String sClassName, String sSuperClass ) throws GeneratorException 
//	{				
//		PluginLogger.log("GeneratorProvider.getJavaClassGenerator()...");
//
//		Generator generator = getGenerator( project, sTemplateFile );
//		
////		//--- Java Class to generate
////		OldJavaClass javaClass = null ;
////		if (sSuperClass != null) 
////		{
////			//--- With super class
////			javaClass = new OldJavaClass(sClassName, sPackage, sSuperClass);
////		}
////		else
////		{
////			//--- No super class
////			javaClass = new OldJavaClass(sClassName, sPackage);
////		}
//		//--- Java Class to generate
//		JavaClass javaClass = null ;
//		if (sSuperClass != null) 
//		{
//			//--- With super class
//			javaClass = new JavaClass(sClassName, sPackage, sSuperClass);
//		}
//		else
//		{
//			//--- No super class
//			javaClass = new JavaClass(sClassName, sPackage);
//		}
//
//
//		//--- Populate the context with the Java class to generate 
//		generator.setJavaClassTargetInContext(javaClass);
//
//		return generator ;
//	}

}