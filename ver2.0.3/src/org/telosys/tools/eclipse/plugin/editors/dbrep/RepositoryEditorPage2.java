package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.commons.listeners.OpenTemplateFileInEditor;
import org.telosys.tools.eclipse.plugin.commons.widgets.GenerateButton;
import org.telosys.tools.eclipse.plugin.commons.widgets.GridPanel;
import org.telosys.tools.eclipse.plugin.commons.widgets.RefreshButton;
import org.telosys.tools.eclipse.plugin.commons.widgets.SelectDeselectButtons;
import org.telosys.tools.eclipse.plugin.commons.widgets.TargetsButton;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * Editor Page 2 : "Bulk Generation"
 * 
 */
/* package */ class RepositoryEditorPage2 extends RepositoryEditorPage {

	private Table  _tableEntities = null ;
	
	private Table  _tableTargets = null ;
	
	/**
	 * Constructor
	 * @param editor
	 * @param id
	 * @param title
	 */
	public RepositoryEditorPage2(FormEditor editor, String id, String title) {
		super(editor, id, title);
		//super(editor, id, null); // ERROR if title is null
		
		log(this, "constructor(.., '"+id+"', '"+ title +"')..." );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		log(this, "createFormContent(..)..." );
		Control pageControl = getPartControl();
		
		if ( pageControl != null )
		{
			log(this, "createFormContent(..) : getPartControl() != null " );
		}
		else
		{
			log(this, "createFormContent(..) : getPartControl() is null !!! " );
			return ;
		}
		
//		if ( pageControl instanceof Composite )
//		{
//			PluginLogger.log(this, "- pageControl is a Composite  " );
//			PluginLogger.log(this, "- pageControl class = " + pageControl.getClass() );
//			
//			Composite pageComposite = (Composite) pageControl ;
//			Layout layout = pageComposite.getLayout();			
//			PluginLogger.log(this, "- pageControl layout class = " + layout.getClass() );
//		}
//		else
//		{
//			PluginLogger.log(this, "- pageControl() is NOT a Composite !!! " );
//		}

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
		// Page title 
		// form.setText( repEditor.getDatabaseTitle() );
		
		Composite scrolledFormBody = form.getBody();
		log(this, "- body class = " + scrolledFormBody.getClass() );
		
		GridLayout bodyLayout = new GridLayout(2, false); // Grid 2 columns

		// marginWidth specifies the number of pixels of horizontal margin 
		// that will be placed along the left and right edges of the layout. The default value is 5.
		//bodyLayout.marginWidth = 20 ;
		bodyLayout.marginWidth = RepositoryEditor.LAYOUT_MARGIN_WIDTH ;
		
		scrolledFormBody.setLayout( bodyLayout );
		
		//---------------------------------------------------------------
		// Line 0 - Columns 1 & 2 (span) : The page title
		//---------------------------------------------------------------
		GridData gdTitle = new GridData(GridData.FILL_HORIZONTAL);
		gdTitle.horizontalSpan = 2;		
		Label labelTitle = Util.setPageTitle(scrolledFormBody, "Bulk generation" ) ;
		labelTitle.setLayoutData(gdTitle);
		
		//---------------------------------------------------------------
		// Line 1 - Column 1 in the "body layout" : Entities panel
		//---------------------------------------------------------------
		Composite panel1 = new Composite(scrolledFormBody, SWT.NONE | SWT.BORDER );
		panel1.setLayout(new GridLayout(1, false));
		//panel1.setSize(200, 100);
		GridData gdpanel1 = new GridData();
		gdpanel1.verticalAlignment = SWT.TOP ;
		gdpanel1.horizontalAlignment = GridData.FILL ;
		//gd2.widthHint = 300 ;
		panel1.setLayoutData(gdpanel1);

		//--- Create the buttons 
		//createSelectButtons1(panel1);
		//createSelectDeselectButtons(panel1, _tableEntities );
		SelectDeselectButtons buttons1 = new SelectDeselectButtons(panel1, SelectDeselectButtons.CREATE_PANEL) ;

		//--- Create the standard "SWT Table" for entities
		_tableEntities = createEntitiesTable(panel1);
		//_table.setLocation(20, 20);
		GridData gdTableEntities = new GridData();
		gdTableEntities.heightHint = 344 ;
		gdTableEntities.widthHint  = 420 ;
		_tableEntities.setLayoutData(gdTableEntities);
		buttons1.setTable(_tableEntities);
		
		//---------------------------------------------------------------
		// Line 1 - Column 2 in the "body layout" : Targets panel
		//---------------------------------------------------------------
		Composite panel2 = new Composite(scrolledFormBody, SWT.NONE | SWT.BORDER );
		
		panel2.setLayout(new GridLayout(1, false));
		GridData gdpanel2 = new GridData();
		//gdpanel2.verticalAlignment   = SWT.TOP ;
		gdpanel2.verticalAlignment   = GridData.FILL ;
		gdpanel2.horizontalAlignment = GridData.FILL ;
		//gd2.widthHint = 300 ;
		panel2.setLayoutData(gdpanel2);
		

		//--- Create the buttons 
		GridPanel gridPanel = new GridPanel(panel2, 7); // 7 columns
		SelectDeselectButtons buttons2 = new SelectDeselectButtons( gridPanel.getPanel() ) ; // 2 buttons

		gridPanel.addFiller(45); // 1 filler
		
		//TargetsButton targetsButton = 
		new TargetsButton(gridPanel.getPanel(), getProject() );  // 1 button
		
		RefreshButton refreshButton = new RefreshButton(gridPanel.getPanel());  // 1 button
		refreshButton.addSelectionListener(new SelectionListener() 
		{
	        public void widgetSelected(SelectionEvent arg0)
	        {
	        	//--- Reload the targets list
	        	RepositoryEditor editor = getRepositoryEditor();
	        	editor.refreshAllTargetsTablesFromConfigFile();
	        }
	        public void widgetDefaultSelected(SelectionEvent arg0)
	        {
	        }
	    });
	
		gridPanel.addFiller(45); // 1 filler
		
		GenerateButton generateButton = new GenerateButton(gridPanel.getPanel()); // 1 button
		generateButton.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	if ( confirmBulkGeneration() )
        		{
        	    	Shell shell = Util.cursorWait();
        	    	launchBulkGeneration();
        			Util.cursorArrow(shell);
        		}                
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
		
		//--- Create the standard "SWT Table" for TARGETS
		_tableTargets = createTargetsTable(panel2);
		GridData gdTableTargets = new GridData();
		gdTableTargets.heightHint = 344 ;
		gdTableTargets.widthHint  = 460 ;
		_tableTargets.setLayoutData(gdTableTargets);

		buttons2.setTable(_tableTargets);
		
		//---------------------------------------------------------------
		// Populate the 2 tables 
		//---------------------------------------------------------------
		populateEntitiesTable();
		populateTargetsTable();

	}
	
	//----------------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		log(this, "init(..,..)..." );
		log(this, "init(..,..) : site id = '" + site.getId() + "'" );
		log(this, "init(..,..) : input name = '" + input.getName() + "'" );
	}
    
	//----------------------------------------------------------------------------------------------
	private Table createEntitiesTable(Composite composite)
	{
		log(this, "createTable(..)..." );
		
		// Table style
		// SWT.CHECK : check box in the first column of each row
//		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK ;
		
//		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
//		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL 
						| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.CHECK ;
		Table table = new Table(composite, iTableStyle);
		
		
		//table.setSize(400, 400);
		//table.setSize(300, 100);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		//--- Columns
		TableColumn col = null ;
		int iColumnIndex = 0 ;

		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("Table Name");
		col.setWidth(220);
		
		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("Java Bean");
		col.setWidth(200);
		
		return table;
	}
	//----------------------------------------------------------------------------------------------
	private Table createTargetsTable(Composite composite)
	{
		log(this, "createTableTargetsList(..)..." );
		
		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL 
						| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.CHECK ;
		Table table = new Table(composite, iTableStyle);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		//--- Columns
		TableColumn col = null ;
		int iColumnIndex = 0 ;

		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("Target");
		col.setWidth(220);
		
		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("Template");
		col.setWidth(200);
		
		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("");
		col.setWidth(20);
		
		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		col.setText("");
		col.setWidth(20);

		return table;
	}
	
	//----------------------------------------------------------------------------------------------	
	/**
	 * Populates the list of entities ( left side table )
	 */
	private void populateEntitiesTable()
	{
		log(this, "populateEntitiesTable()");
		
		RepositoryEditor repEditor = (RepositoryEditor) getEditor();
		RepositoryModel dbRep = repEditor.getDatabaseRepository();
		
		Entity[] entities = dbRep.getEntities();
		if ( entities != null )
		{
			for ( int i = 0 ; i < entities.length ; i++ )
			{
				Entity entity = entities[i];
				String sTableName = entity.getName() ;
				
				String sBeanClass = entity.getBeanJavaClass();
				
				if ( sBeanClass == null ) sBeanClass = "???" ;
				
                //--- Create the row content 
                String[] row = new String[] { sTableName, sBeanClass };
				
                //--- Create the TableItem and set the row content 
            	TableItem tableItem = new TableItem(_tableEntities, SWT.NONE );
                tableItem.setChecked(false);                
                tableItem.setText(row);                
                tableItem.setData( sTableName );
			}
		}
	}
	
	//----------------------------------------------------------------------------------------------
	/**
	 * Refresh the targets table from the current configuration supposed to be up to date 
	 */
	protected void refreshTargetsTable()
	{
		//--- Re-populate the SWT table
		populateTargetsTable();
	}

	//----------------------------------------------------------------------------------------------
	private void populateTargetsTable()
	{
		ProjectConfig projectConfig = getProjectConfig();
		if ( projectConfig == null )
		{
			return ;
		}
		
		//List<SpecificTemplate> list = projectConfig.getSpecificTemplates(); // NB : the list can be null 
		List<TargetDefinition> list = projectConfig.getTemplates(); // NB : the list can be null 
		if ( list != null )
		{
			_tableTargets.removeAll();
			//for ( SpecificTemplate st : list ) {
			for ( TargetDefinition targetDef : list ) {
				
//		        // Build a target instance and bind it with the table item
//		        GenericTarget target = new GenericTarget(st.getName(), st.getTargetFile(), 
//		        		st.getTargetFolder(), st.getTemplate() );
		        
				log(this, " . Target : " + targetDef.getName() + " - " + targetDef.getTemplate() );
				
                //--- Create the TableItem and set the row content 
            	TableItem tableItem = new TableItem(_tableTargets, SWT.NONE );
                tableItem.setChecked(false);                
				tableItem.setImage((Image)null);
                tableItem.setData( targetDef ); // Keep the target as "Data"
                
                //--- Col 0
				tableItem.setText(0, targetDef.getName()) ;
				//tableItem.getImageIndent();
                //tableItem.setImage(0,  null );
				
                //--- Col 1
				tableItem.setText(1, targetDef.getTemplate()) ;
				
                //tableItem.setText( new String[] { st.getName(), st.getTemplate() } );    
                
                //--- Col 2
                tableItem.setImage(2,  PluginImages.getImage(PluginImages.EDIT_ICON ) );
                
                //--- Col 3
                tableItem.setText(3, (targetDef.isOnce() ? "1" : "*" ) );
			}
		}
		
		// Edit template file if click on column 2
		OpenTemplateFileInEditor listener = new OpenTemplateFileInEditor( getProject(), _tableTargets, 2 ) ;
		_tableTargets.addListener(SWT.MouseDown, listener );
	}
	
    private boolean confirmBulkGeneration()
    {
		log(this, "confirmBulkGeneration()");
		String sMsg = "This bulk generation will overwrite existing files if they exist." 
			+ "\n\n" + "Launch generation ?";
		return MsgBox.confirm(" Confirm generation", sMsg) ;
    }
    
    //private LinkedList<TargetDefinition> getSelectedTargets(ProjectConfig projectConfig)
    private LinkedList<TargetDefinition> getSelectedTargets()
    {
    	LinkedList<TargetDefinition> selectedTargets = new LinkedList<TargetDefinition>();
    	
    	TargetsUtil.addSelectedItemsToList(selectedTargets, _tableTargets);
    	
		return selectedTargets ;
    }
    
    /**
     * Returns a List of entity names ( list of String )
     * @return
     */
    private LinkedList<String> getSelectedEntities()
    {
    	LinkedList<String> selectedEntities = new LinkedList<String>();
    	
		int n = _tableEntities.getItemCount() ;
		for ( int i = 0 ; i < n ; i++ )
		{
			TableItem item = _tableEntities.getItem(i);
			if ( item.getChecked() == true )
			{
				// Retrieve the Target associated with the table item
		    	Object oData = item.getData();
		    	if ( oData != null )
		    	{
			    	if ( oData instanceof String )
			    	{
			    		String entityName = (String) oData ;
			    		selectedEntities.addLast(entityName);
						log(this, "getSelectedEntities() : add entity " + entityName );
			    	}
			    	else
			    	{
			    		MsgBox.error("Table item " + i + " : invalid Data type");
			    	}
		    	}
		    	else
		    	{
		    		MsgBox.error("Table item " + i + " : no data");
		    	}
			}
		}
    	return selectedEntities ;
    }
    
    private int launchBulkGeneration()
    {
    	log("launchBulkGeneration()...");
    	
        //--- Get the project configuration
    	//RepositoryEditor editor = (RepositoryEditor) getEditor();
		IProject iProject = getProject();
    	
		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig( iProject );
        if ( projectConfig == null )
        {
        	MsgBox.error("Cannot get project configuration");
        	return 0;
        }
//        String sSourceDir = projectConfig.getSourceFolder();
//		PluginLogger.log( "Source Folder : " + sSourceDir );
		
		//String sProjectLocation = EclipseProjUtil.getProjectDir(iProject);

		//--- Get the selected entities list
		LinkedList<String> selectedEntities = getSelectedEntities();
    	log("launchBulkGeneration() : " + selectedEntities.size() + "selected entitie(s)" );
		
    	//--- Get the selected targets list
    	LinkedList<TargetDefinition> selectedTargets = getSelectedTargets();
    	log("launchBulkGeneration() : " + selectedTargets.size() + " selected target(s)");
    	
    	GenerationTask generationTask = new GenerationTask( getRepositoryEditor() );
    	
    	return generationTask.generateTargets(selectedEntities, selectedTargets);
    }
    
}