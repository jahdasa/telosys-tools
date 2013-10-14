package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.GeneratorContextException;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.GeneratorConfigManager;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.directive.DirectiveException;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.repository.model.RepositoryModel;

public class GenerationTask {

	private final RepositoryModel     repositoryModel ;
	private final IGeneratorConfig    generatorConfig ;
	private final IProject            project;
	private final TelosysToolsLogger  telosysToolsLogger;
	
	/**
	 * Constructor
	 * @param editor
	 */
	public GenerationTask(RepositoryEditor editor)
	{
    	//--- Prepare the generation environment 	
    	telosysToolsLogger = ( editor.getLogger() != null ? editor.getLogger() : new ConsoleLogger() ) ;
//    	if ( null == telosysToolsLogger )
//    	{
//    		telosysToolsLogger = new ConsoleLogger();
//    	}
    	telosysToolsLogger.log("GenerationTask initialization");
    	
    	repositoryModel = editor.getDatabaseRepository();

		ProjectConfig projectConfig = editor.getProjectConfig();
		
		GeneratorConfigManager configManager = new GeneratorConfigManager(null);
    	try {
			generatorConfig = configManager.initFromDirectory( projectConfig.getProjectFolder() );
		} catch (GeneratorException e) {
        	MsgBox.error("GenerationTask constructor : Cannot initialize the generator configuration");
        	throw new RuntimeException("Cannot initialize the generator configuration");
		}
    	
        project = editor.getProject();
        if ( null == project )
        {
        	MsgBox.error("GenerationTask constructor : Cannot get project from editor");
        	throw new RuntimeException("Cannot get project from editor");
        }

    	telosysToolsLogger.log("GenerationTask environment ready");
	}
	
	public int generateTargets(LinkedList<String> entities, LinkedList<TargetDefinition> genericTargets)
	{
		//--- Create the generation task (with progress monitor)
		GenerationTaskWithProgress generationTask;
		try {
			generationTask = new GenerationTaskWithProgress(entities, genericTargets, 
					repositoryModel, generatorConfig, project, telosysToolsLogger);
			
		} catch (TelosysPluginException e1) {
    		MsgBox.error("Cannot create GenerationTaskWithProgress instance", e1);
    		return 0 ;
		}
		
		//--- Run the generation task via the progress monitor 
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog( Util.getActiveWindowShell() ) ;
		try {
			telosysToolsLogger.log("Run generation task ..."  );
			progressMonitorDialog.run(false, false, generationTask);
			telosysToolsLogger.log("End of generation task."  );
			
			MsgBox.info("Normal end of generation\n\n" + generationTask.getResult() + " file(s) generated.");
			
		} catch (InvocationTargetException invocationTargetException) {
			//MsgBox.error("Error during generation", e.getCause() );
			showGenerationError(invocationTargetException); // v 2.0.7
		} catch (InterruptedException e) {
			MsgBox.info("Generation interrupted");
		}
		
    	return generationTask.getResult();
		
	}
	
	/**
	 * Specific message depending on the type of exception
	 * @param invocationTargetException
	 * @since 2.0.7
	 */
	private void showGenerationError(InvocationTargetException invocationTargetException) {
		Throwable cause = invocationTargetException.getCause();
		if ( cause instanceof GeneratorException ) {
			GeneratorException generatorException = (GeneratorException) cause ;
			Throwable generatorExceptionCause = generatorException.getCause() ;
			
			if ( generatorExceptionCause instanceof DirectiveException ) {
				//--- DIRECTIVE ERROR
				DirectiveException directiveException = (DirectiveException) generatorExceptionCause ;
				String msg = "Directive error ( #" + directiveException.getDirectiveName() + " ) \n\n" 
					+ directiveException.getMessage() 
					+ "\n\n" 
					+ "Template : " + directiveException.getTemplateName() 
					+ " ( line " + directiveException.getLineNumber() + " )" ;  
				MsgBox.error( msg );
			}
			else if ( generatorExceptionCause instanceof ParseErrorException ) {
				//--- TEMPLATE PARSING ERROR
				ParseErrorException parseErrorException = (ParseErrorException) generatorExceptionCause ;
				String msg = "Template parsing error \n\n" 
					+ parseErrorException.getMessage() 
					+ "\n\n" 
					+ "Template : " + parseErrorException.getTemplateName() 
					+ " ( line " + parseErrorException.getLineNumber() + " )" 
					+ "\n\n" 
					+ "Invalid syntax : \n" + parseErrorException.getInvalidSyntax()  
					;
				MsgBox.error( msg );
			}
			else if ( generatorExceptionCause instanceof MethodInvocationException ) {
				//--- METHOD INVOCATION
				MethodInvocationException methodInvocationException = (MethodInvocationException) generatorExceptionCause ;
				String msg = "Method invocation error \n\n" 
					+ methodInvocationException.getMessage() 
					+ "\n\n" 
					+ "Template : " + methodInvocationException.getTemplateName() 
					+ " ( line " + methodInvocationException.getLineNumber() + " )" 
					+ "\n\n" 
					+ "Method name : \n" + methodInvocationException.getMethodName()
					+ "Reference name : \n" + methodInvocationException.getReferenceName()
					;
				MsgBox.error( msg );
			}			
			else if ( generatorExceptionCause instanceof ResourceNotFoundException ) {
				//--- RESOURCE NOT FOUND
				ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) generatorExceptionCause ;
				String msg = "Resource not found \n\n" 
					+ resourceNotFoundException.getMessage() 
					;
				MsgBox.error( msg );
			}			
			else if ( generatorExceptionCause instanceof GeneratorContextException ) {
				//--- CONTEXT ERROR
				GeneratorContextException generatorContextException = (GeneratorContextException) generatorExceptionCause ;
				String msg = "Context error \n\n" 
					+ generatorContextException.getMessage() 
					;
				MsgBox.error( msg );
			}
			else {
				MsgBox.error("Error during generation", cause );
			}
		}
		else {
			
			MsgBox.error("Error during generation", cause );
		}
		
	}
}
