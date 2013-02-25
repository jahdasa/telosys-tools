package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.GeneratorConfigManager;
import org.telosys.tools.generator.config.IGeneratorConfig;
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
			
		} catch (InvocationTargetException e) {
			MsgBox.error("Error during generation", e.getCause() );
		} catch (InterruptedException e) {
			MsgBox.info("Generation interrupted");
		}
		
    	return generationTask.getResult();
		
	}
}
