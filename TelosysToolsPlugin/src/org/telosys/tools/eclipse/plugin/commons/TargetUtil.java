package org.telosys.tools.eclipse.plugin.commons;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.generator.target.TargetDefinition;

public class TargetUtil {

	// Editor id defined in "plugin.xml" 
	private final static String VELOCITY_EDITOR_ID = "org.telosys.tools.eclipse.plugin.editors.velocity.VelocityEditor";
	//private final static String TEXT_EDITOR_ID     = "org.eclipse.ui.DefaultTextEditor" ;
	
	private static void log(String s) 
	{
		PluginLogger.log( TargetUtil.class.getName() + " : " + s );
	}

	public static IFile getFileFromTemplatesFolder( IProject project, String fileName ) {
		log( "getFileFromTemplatesFolder(..)..." + fileName );
		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig( project );
		if ( projectConfig != null ) {
			String templateFile = null ;
			String templatesFolder = projectConfig.getTemplatesFolder();
			log( "  templates folder = " + templatesFolder );
			if ( templatesFolder.endsWith("/") || templatesFolder.endsWith("\\") ) {
				templateFile = templatesFolder + fileName;
			}
			else {
				templateFile = templatesFolder + "/" + fileName;
			}		
			log("getTemplateFile(target) : file = " + templateFile );			
			File f = EclipseProjUtil.getResourceAsFile( project, templateFile);
			if ( f != null) {
				return EclipseWksUtil.toIFile(f);
			}
			else {
				MsgBox.error("Template file '" + templateFile + "' not found !");
			}
		}
		return null ;
	}
	
	/**
	 * Returns the template file instance for the given target
	 * @param project
	 * @param target
	 * @return
	 */
	public static IFile getTemplateFile( IProject project,  TargetDefinition target ) {
		
		log( "getTemplateFile(target)..." + target );
		return getFileFromTemplatesFolder( project, target.getTemplate() );
	}
	
	/**
	 * Opens the template file of the given target in an editor
	 * @param project
	 * @param target
	 */
	public static void openTemplateFileInEditor(IProject project, TargetDefinition target) {
		
		IFile templateFile = getTemplateFile( project, target );
		
		IEditorInput editorInput = new FileEditorInput(templateFile);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		try {
			// Use class TextEditor : The standard/default text editor.
			// This editor has id "org.eclipse.ui.DefaultTextEditor". 
			//workbenchPage.openEditor(editorInput, "org.eclipse.ui.DefaultTextEditor" );
			workbenchPage.openEditor(editorInput, VELOCITY_EDITOR_ID );
			
		} catch (PartInitException e) {
			MsgBox.error("Cannot open file in editor (PartInitException)");
		}
	}

	public static void openTargetsConfigFileInEditor(IProject project) {
		
		IFile targetsConfigFile = getFileFromTemplatesFolder( project, ProjectConfig.TEMPLATES_CFG );
		
		IEditorInput editorInput = new FileEditorInput(targetsConfigFile);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		try {
			// Use class TextEditor : The standard/default text editor.
			// This editor has id "org.eclipse.ui.DefaultTextEditor". 
			workbenchPage.openEditor(editorInput, "org.eclipse.ui.DefaultTextEditor" );
		} catch (PartInitException e) {
			MsgBox.error("Cannot open file in editor (PartInitException)");
		}
	}

}
