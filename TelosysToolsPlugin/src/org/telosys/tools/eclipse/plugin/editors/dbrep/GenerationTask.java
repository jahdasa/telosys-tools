package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
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

public class GenerationTask {

	private final RepositoryEditor    editor ;
//	private final RepositoryModel     repositoryModel ;
	private final IGeneratorConfig    generatorConfig ;
	private final IProject            project;
	private final TelosysToolsLogger  telosysToolsLogger;
	
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * @param editor
	 */
	public GenerationTask(RepositoryEditor editor)
	{
		this.editor = editor ;
		
    	//--- Prepare the generation environment 	
    	telosysToolsLogger = ( editor.getLogger() != null ? editor.getLogger() : new ConsoleLogger() ) ;
    	
    	String currentBundelName = editor.getCurrentBundleName();
    	
    	telosysToolsLogger.log("GenerationTask initialization");
    	
//    	repositoryModel = editor.getDatabaseRepository();

		ProjectConfig projectConfig = editor.getProjectConfig();
		
		GeneratorConfigManager configManager = new GeneratorConfigManager(null);
    	try {
			generatorConfig = configManager.initFromDirectory( projectConfig.getProjectFolder(), currentBundelName );
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
	
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * Generates the targets with the given selected entities, selected targets and copy resources if any 
	 * @param selectedEntities the selected entities
	 * @param selectedTargets the selected targets/templates
	 * @param resourcesTargets the resources to be copied (or null if none)
	 * @return
	 */
	public GenerationTaskResult generateTargets(LinkedList<String> selectedEntities, LinkedList<TargetDefinition> selectedTargets, 
			List<TargetDefinition> resourcesTargets )
	{
		//--- Create the generation task (with progress monitor)
		GenerationTaskWithProgress generationTask;
		try {
			generationTask = new GenerationTaskWithProgress(
					editor, // v 2.0.7
					selectedEntities, selectedTargets, 
					resourcesTargets, // v 2.0.7
					// repositoryModel, generatorConfig, project, telosysToolsLogger); 
					generatorConfig, telosysToolsLogger); // v 2.0.7
			
		} catch (TelosysPluginException e1) {
    		MsgBox.error("Cannot create GenerationTaskWithProgress instance", e1);
    		return new GenerationTaskResult() ;
		}
		
		//--- De-activate "Build Automatically"  ( ver 2.0.7 )
		boolean originalFlag = EclipseWksUtil.setBuildAutomatically(false);
		
		//--- Run the generation task via the progress monitor 
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog( Util.getActiveWindowShell() ) ;
		try {
			telosysToolsLogger.log("Run generation task ..."  );
			progressMonitorDialog.run(false, false, generationTask);
			telosysToolsLogger.log("End of generation task."  );
			
			GenerationTaskResult result = generationTask.getResult() ;
			MsgBox.info("Normal end of generation." 
					+ "\n\n" + result.getNumberOfResourcesCopied() + " resources(s) copied."
					+ "\n\n" + result.getNumberOfFilesGenerated() + " file(s) generated.");
			
		} catch (InvocationTargetException invocationTargetException) {
			showGenerationError(invocationTargetException, 
					generationTask.getCurrentTemplateName(), generationTask.getCurrentEntityName() ); // v 2.0.7
		} catch (InterruptedException e) {
			MsgBox.info("Generation interrupted");
		}
		
		//--- Re-activate "Build Automatically"  ( ver 2.0.7 )
		EclipseWksUtil.setBuildAutomatically(originalFlag);
		
    	return generationTask.getResult();
		
	}
	
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * Specific message depending on the type of exception
	 * @param invocationTargetException
	 * @since 2.0.7
	 */
	private void showGenerationError(InvocationTargetException invocationTargetException, String templateName, String entityName) {
		Throwable cause = invocationTargetException.getCause();
		if ( cause instanceof GeneratorException ) {
			GeneratorException generatorException = (GeneratorException) cause ;
			Throwable generatorExceptionCause = generatorException.getCause() ;
			
			if ( generatorExceptionCause instanceof DirectiveException ) {
				//--- DIRECTIVE ERROR ( Telosys Tools exception )
				// eg : #using ( "varNotDefined" )
				DirectiveException directiveException = (DirectiveException) generatorExceptionCause ;
				String msg1 = buildErrorMessageHeader( directiveException.getTemplateName(), 
						directiveException.getLineNumber(), entityName);
				
				String msg2 = "Directive  #" + directiveException.getDirectiveName() + " \n\n" 
					+ directiveException.getMessage() ;

				MsgBox.error( "Directive error", msg1 + msg2 );
			}
			else if ( generatorExceptionCause instanceof ParseErrorException ) {
				//--- TEMPLATE PARSING ERROR ( Velocity exception )
				// eg : #set(zzz)
				ParseErrorException parseErrorException = (ParseErrorException) generatorExceptionCause ;
				String msg1 = buildErrorMessageHeader( parseErrorException.getTemplateName(), 
						parseErrorException.getLineNumber(), entityName);
				String msg2 = parseErrorException.getMessage() 
//					+ "\n\n" 
//					+ "Invalid syntax : \n" + parseErrorException.getInvalidSyntax()  // Always null
					;
				MsgBox.error( "Template parsing error", msg1 + msg2 );
			}
			else if ( generatorExceptionCause instanceof MethodInvocationException ) {
				//--- METHOD INVOCATION ( Velocity exception )
				// eg : $fn.isNotVoid("") : collection argument expected 
				MethodInvocationException methodInvocationException = (MethodInvocationException) generatorExceptionCause ;
				String msg1 = buildErrorMessageHeader( methodInvocationException.getTemplateName(), 
						methodInvocationException.getLineNumber(), entityName);
				String msg2 =  methodInvocationException.getMessage() 
					+ "\n\n" 
					+ "Method name : \n" + methodInvocationException.getMethodName()
					+ "Reference name : \n" + methodInvocationException.getReferenceName()
					;
				MsgBox.error( "Method invocation error", msg1 + msg2 );
			}			
			else if ( generatorExceptionCause instanceof ResourceNotFoundException ) {
				//--- RESOURCE NOT FOUND ( Velocity exception )
				ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) generatorExceptionCause ;
				String msg1 = buildErrorMessageHeader( templateName, 0, entityName);
				String msg2 = resourceNotFoundException.getMessage(); 
				MsgBox.error( "Resource not found", msg1 + msg2 );
			}			
			else if ( generatorExceptionCause instanceof GeneratorContextException ) {
				//--- CONTEXT ERROR ( Telosys Tools exception )
				// Reflection error encapsulation
				// eg : $entity.tototo / $entity.getTTTTTTTTT() / $entity.name.toAAAAA()
				// or errors due to invalid model 
				GeneratorContextException generatorContextException = (GeneratorContextException) generatorExceptionCause ;
				// generatorContextException.getTemplateName() not always know the template => use templateName arg
				String msg1 = buildErrorMessageHeader( templateName,  // keep templateName here
						generatorContextException.getLineNumber(), entityName); 
				String msg2 = generatorContextException.getMessage() ;
				MsgBox.error( "Context error", msg1 + msg2 );
				
			}
			else {
				MsgBox.error("Error during generation", cause );
			}
		}
		else {
			
			MsgBox.error("Error during generation", cause );
		}
		
	}
	
	//-------------------------------------------------------------------------------------------------------------
	private String buildErrorMessageHeader(String template, int line, String entity ) {
		String lineMsg = "" ;
		if ( line > 0 ) {
			lineMsg = "  ( line " + line + " )" ;
		}
		return "Template \"" + template + "\"" + lineMsg + "  -  Entity : \"" 
				+ entity + "\" \n\n" ;
	}
}
