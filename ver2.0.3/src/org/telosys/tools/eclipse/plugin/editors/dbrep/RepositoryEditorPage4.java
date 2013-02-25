package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;

/**
 * Page 3 of the editor <br>
 * 
 * Shows the project configuration 
 * 
 */
/* package */ class RepositoryEditorPage4 extends RepositoryEditorPage 
{

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public RepositoryEditorPage4(FormEditor editor, String id, String title) {
		super(editor, id, title);
		PluginLogger.log(this, "constructor(.., '"+id+"', '"+ title +"')..." );		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		log(this, "createFormContent(..)..." );
		
		ProjectConfig config = getProjectConfig();
		if ( config == null )
		{
			MsgBox.error("ProjectConfig is null");
			return;
		}
		
		// What do we have here ?
		// * pageControl (Composite)
		//  . class  : org.eclipse.ui.forms.widgets.ScrolledForm ( see API JavaDoc )
		//  . layout : org.eclipse.swt.custom.ScrolledCompositeLayout
		// * body 
		//  . class  : org.eclipse.ui.forms.widgets.LayoutComposite ( no API doc ! )
		//  . layout : none
		//
		/* Example from API doc :
		  ScrolledForm form = toolkit.createScrolledForm(parent);
		  form.setText("Sample form");
		  form.getBody().setLayout(new GridLayout());
		  toolkit.createButton(form.getBody(), "Checkbox", SWT.CHECK);
		*/
		
		ScrolledForm form = managedForm.getForm();
		
		Composite body = form.getBody(); 
		// body.getClass() --> org.eclipse.ui.forms.widgets.LayoutComposite
		// No Layout for the body at this moment
		
		//--- Set a LAYOUT to the BODY
		GridLayout bodyLayout = new GridLayout();	
		bodyLayout.numColumns = 2 ;
		bodyLayout.makeColumnsEqualWidth = false ;
		
		body.setLayout( bodyLayout );
		
		//---------------------------------------------------------------
		// Line 0 - Columns 1 & 2 (span) : The page title
		//---------------------------------------------------------------
		GridData gdTitle = new GridData(GridData.FILL_HORIZONTAL);
		gdTitle.horizontalSpan = 2;		
		Label labelTitle = Util.setPageTitle(body, "Project configuration" ) ;
		labelTitle.setLayoutData(gdTitle);
		
		
		
		addConfigRow(body, "", "" );
		addConfigRow(body, "Project name :", config.getProjectName() );
		
		addConfigRow(body, "Workspace folder :", config.getWorkspaceFolder() );
		addConfigRow(body, "Project folder :", config.getProjectFolder() );
		addConfigRow(body, "Plugin configuration file :", config.getPluginConfigFile() );

		addConfigRow(body, "Templates folder :", config.getTemplatesFolder() );
		addConfigRow(body, "Templates folder full path :", config.getTemplatesFolderFullPath() );
		addConfigRow(body, "Repositories folder :", config.getRepositoriesFolder() );

		
		addConfigRow(body, "", "" );
		//addConfigRow(body, "Generator version", GeneratorConst.GENERATOR_VERSION );
		
	}
	
	//----------------------------------------------------------------------------------------------
	private void addConfigRow(Composite c, String s1, String s2)
	{
		Label label1 = new Label( c, SWT.LEFT );
		label1.setText(s1) ;

		Label label2 = new Label( c, SWT.LEFT );
		label2.setText(s2) ;
	}
	//----------------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		PluginLogger.log(this, "init(..,..) : site id = '" + site.getId() + "'" );
		PluginLogger.log(this, "init(..,..) : input name = '" + input.getName() + "'" );
	}

}