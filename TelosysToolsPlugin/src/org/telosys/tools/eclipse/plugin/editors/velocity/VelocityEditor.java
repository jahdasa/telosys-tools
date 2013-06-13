package org.telosys.tools.eclipse.plugin.editors.velocity;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.telosys.tools.eclipse.plugin.editors.velocity.contentassist.ContentAssistMessages;

public class VelocityEditor extends TextEditor {

//	/**
//	 * The OSGI bundle name of the plugin providing help
//	 */
//	private final static String TELOSYS_TOOLS_HELP_PLUGIN_ID = "TelosysToolsPluginHelp" ;
//
//	/**
//	 * The "Help Context ID" defined in the "contexts.xml" file 
//	 */
//	private final static String VELOCITY_EDITOR_HELP_CONTEXT_ID = "VelocityEditorHelp" ;

	
	private ColorManager colorManager;

	public VelocityEditor() {
		super();
		
		colorManager = new ColorManager();
		
		//setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setSourceViewerConfiguration(new VelocityEditorConfiguration(colorManager));
		
		setDocumentProvider(new VelocityDocumentProvider());
		
		//setHelpContextId(getHelpContextId(VELOCITY_EDITOR_HELP_CONTEXT_ID));
		setHelpContextId( ContextualHelp.getVelocityEditorHelpContextId() ) ;

	}
	
//	/**
//	 * Return the Help Context ID ( made of "PluginHelpID" + "." + "HelpContextID" )
//	 * @param contextId
//	 * @return
//	 */
//	private String getHelpContextId (String contextId ) {
////		//Bundle bundle = text_editor.Activator.getDefault().getBundle() ;
////		Bundle bundle = MyPlugin.getBundle() ;
////		String pluginId = bundle.getSymbolicName();
////		System.out.println("Plugin ID = " + pluginId );
//		return TELOSYS_TOOLS_HELP_PLUGIN_ID + "." + contextId ;
//	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		ResourceBundle bundle = ContentAssistMessages.getResourceBundle();
		
		// Content assist action
		IAction action = new TextOperationAction(bundle, "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        // setAction("ContentAssistProposal", action); //$NON-NLS-1$
        setAction("ContentAssist", action);
        
        
	}

}
