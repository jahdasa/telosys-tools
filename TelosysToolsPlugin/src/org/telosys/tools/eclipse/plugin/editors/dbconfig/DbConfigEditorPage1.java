package org.telosys.tools.eclipse.plugin.editors.dbconfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.config.ClassNameProvider;
import org.telosys.tools.commons.dbcfg.XmlDatabase;
import org.telosys.tools.commons.dbcfg.XmlDbConfig;
import org.telosys.tools.commons.jdbc.ConnectionManager;
import org.telosys.tools.db.metadata.ColumnMetaData;
import org.telosys.tools.db.metadata.ForeignKeyColumnMetaData;
import org.telosys.tools.db.metadata.MetaDataManager;
import org.telosys.tools.db.metadata.PrimaryKeyColumnMetaData;
import org.telosys.tools.db.metadata.SchemaMetaData;
import org.telosys.tools.db.metadata.TableMetaData;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.config.ProjectClassNameProvider;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.repository.RepositoryGenerator;
import org.telosys.tools.repository.RepositoryUpdator;
import org.telosys.tools.repository.UpdateLogWriter;
import org.telosys.tools.repository.config.DefaultInitializerChecker;
import org.telosys.tools.repository.config.InitializerChecker;
import org.telosys.tools.repository.model.RepositoryModel;
import org.telosys.tools.repository.persistence.StandardFilePersistenceManager;


/**
 * 
 */
/* package */ class DbConfigEditorPage1 extends DbConfigEditorPage 
{

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd.HHmmss");

    private final static int GROUP_X = 12 ;
	private final static int GROUP_WIDTH = 600 ;
	
	private final static int TEXT_X = 10 ;

	//private final static int TEXT_WIDTH = 180 ;

	private final static int TEXT_HEIGHT = 22 ;

	private final static int GET_NOTHING      = 0 ;
	private final static int GET_COLUMNS      = 1 ;
	private final static int GET_PRIMARY_KEYS = 2 ;
	private final static int GET_FOREIGN_KEYS = 3 ;
	private final static int GET_CATALOGS  = 11 ;
	private final static int GET_SCHEMAS   = 12 ;
	
    //-----------------------------------------------------------
	private DbConfigEditor _editor = null ;
    //-----------------------------------------------------------
	
//	private Color _backgroundColor = null ;
	
	private Combo _ComboDatabases = null ;
	
    private Text _Id        = null ;
    private Text _Name      = null ;
    private Text _Driver    = null ;
    private Text _Url       = null ;
    private Text _Isolation = null ;
    private Text _PoolSize  = null ;
    private Text _User      = null ;
    private Text _Password  = null ;

    private Text _InfoURL       = null ;
    private Text _InfoProdName  = null ;
    private Text _InfoProdVer   = null ;
    private Text _InfoDriverName = null ;
    private Text _InfoDriverVer  = null ;
    private Text _InfoMaxConn    = null ;
    private Text _InfoUser      = null ;
    private Text _InfoIsolation = null ;
    private Text _InfoCatalogTerm = null ;
    private Text _InfoCatalogSepar = null ;
    private Text _InfoSchemaTerm = null ;
    private Text _InfoSearchEscape = null ;
    
	private Text _tMetaDataCatalog = null ;
	private Text _tMetaDataSchema = null;
	private Text _tMetaDataTablePattern = null;
	private Text _tMetaDataTableTypes = null;

    private Text _tMetaData = null ;
//    private TelosysToolsLogger _logger = new DefaultLogger();
    
    //-----------------------------------------------------------
	
	private boolean _bPopulateInProgress = false ;
	
	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public DbConfigEditorPage1(FormEditor editor, String id, String title) {
		super(editor, id, title);
		//PluginLogger.log(this, "constructor(.., '"+id+"', '"+ title +"')..." );
		log(this, "constructor(.., '"+id+"', '"+ title +"')..." );
		_editor = (DbConfigEditor) editor;
	}

//	protected void log(String s)
//	{
//		PluginLogger.log(s);
//	}
//	protected void log(Object o, String s)
//	{
//		PluginLogger.log(o,s);
//	}
	
	
//	private void setDirty()
//	{
//		//RepositoryEditor repEditor = (RepositoryEditor) getEditor();
//		// repEditor.setDirty();
//		_editor.setDirty();
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		log(this, "createFormContent(..)..." );
		Control pageControl = getPartControl();
		
//		Display display = pageControl.getDisplay();
//		_backgroundColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);		
//		pageControl.setBackground(_backgroundColor ) ;
		
		if ( pageControl != null )
		{
			log(this, "createFormContent(..) : getPartControl() != null " );
		}
		else
		{
			log(this, "createFormContent(..) : getPartControl() is null !!! " );
			return ;
		}
		
		if ( pageControl instanceof Composite )
		{
			log(this, "- pageControl is a Composite  " );
			log(this, "- pageControl class = " + pageControl.getClass() );
			
			Composite pageComposite = (Composite) pageControl ;
			Layout layout = pageComposite.getLayout();			
			log(this, "- pageControl layout class = " + layout.getClass() );
		}
		else
		{
			log(this, "- pageControl() is NOT a Composite !!! " );
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
		// Page title 
		// form.setText( _repEditor.getDatabaseTitle() );
		
		Composite body = form.getBody();
		log(this, "- body class = " + body.getClass() );
		
		Layout layout = body.getLayout();			
		if ( layout != null )
		{
			log(this, "- body layout class = " + layout.getClass() );
		}
		else
		{
			log(this, "- body layout class = NO LAYOUT ! ");
		}
		
		Layout bodyLayout = new RowLayout(SWT.VERTICAL);
		
		body.setLayout( bodyLayout );
		
		//--------------------------------------------------------------
		//--- Combo Box "Database table"
		Group group1 = new Group(body, SWT.NONE);
		group1.setText("Database");
		group1.setSize(GROUP_WIDTH, 60);
		//tab.setBackground(DAOColor.color(disp));
		//group1.setLocation(GROUP_X, 20);
        group1.setBackground( getBackgroundColor() );

		_ComboDatabases = new Combo(group1, SWT.BORDER | SWT.READ_ONLY);
		_ComboDatabases.setBounds(TEXT_X, 25, 260, TEXT_HEIGHT);
		_ComboDatabases.setVisibleItemCount(12);
		setDatabasesComboAction(_ComboDatabases);

		//--------------------------------------------------------------
		Button button = new Button(group1, SWT.NONE);
		button.setText("Generate repository");
		button.setBounds(300, 25, 180, 25);

    	button.addSelectionListener( new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
                actionGenerateRepository();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
		
		//--------------------------------------------------------------
		button = new Button(group1, SWT.NONE);
		button.setText("Update repository");
		button.setBounds(500, 25, 180, 25);

    	button.addSelectionListener( new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
                actionUpdateRepository();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
		
		//--------------------------------------------------------------
		//--- Tab Folder 
		Composite composite = null ;

		composite = new Composite(body, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLocation(GROUP_X, 100);
		composite.setSize(400, 200);
		composite.setBackground( getBackgroundColor() );
		

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		
		//tabFolder.setLocation(GROUP_X, 100);
		//tabFolder.setSize(400, 200);
		tabFolder.setBackground( getBackgroundColor() ); // No effect : cannot change the TabFolder color 

		createTabFolder1(tabFolder);
		createTabFolder2(tabFolder);
		createTabFolder3(tabFolder);

		//--------------------------------------------------------------
		
		log(this, "Populate DATABASES combo ..." );

		//DatabaseRepository dbRep = _repEditor.getDatabaseRepository();
		//dbRep.populateTablesCombo(_ComboTables);
		populateDatabases();
		
		log(this, "Populate DATABASES combo : done." );
	}
	
	//----------------------------------------------------------------------------------------------
	private void createTabFolder1(TabFolder tabFolder) {
		log(this, "createTabFolder1() ..." );

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("  Configuration  ");

		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setBackground( getBackgroundColor() );
		
	    // Color color = DAOColor.color(tabContent.getDisplay());
		createConfigurationFields(tabContent) ;

		//--- Button "Test connection"
		Button button = new Button(tabContent, SWT.NONE);
		button.setText("Test connection");
		button.setBounds(460, 20, 120, 25);

    	button.addSelectionListener( new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	actionTestConnection();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
    	
		//--- Button "Show Libraries"
		Button buttonLibraries = new Button(tabContent, SWT.NONE);
		buttonLibraries.setText("Show libraries");
		buttonLibraries.setBounds(460, 50, 120, 25);

		buttonLibraries.addSelectionListener( new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	showLibraries();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
    	
		tabItem.setControl(tabContent);
	}
	//----------------------------------------------------------------------------------------------
	private void createConfigurationFields(Composite container) 
	{
		int x = 5 ; 
		int y = 20 ; 
		int yGap = 30 ; 
		int labelWidth = 100 ;
		int textWidth  = 320 ;
		
	    _Id       = createTextWithLabel(container, x, y, "Id", false, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _Name     = createTextWithLabel(container, x, y, "Name", false, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _Driver   = createTextWithLabel(container, x, y, "Driver", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _Url      = createTextWithLabel(container, x, y, "URL", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _Isolation = createTextWithLabel(container, x, y, "Isolation level", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _PoolSize  = createTextWithLabel(container, x, y, "Pool size", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;

	    _User     = createTextWithLabel(container, x, y, "User", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _Password = createTextWithLabel(container, x, y, "Password", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	}
	
	//----------------------------------------------------------------------------------------------
	private void createTabFolder2(TabFolder tabFolder) 
	{
		log(this, "createTabFolder2() ..." );
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("  Information  ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setBackground( getBackgroundColor() );
		
		createInformationFields(tabContent) ;
		
		//--- Button "Get infos"
		Button button = new Button(tabContent, SWT.NONE);
		button.setText("Get database info");
		button.setBounds(460, 20, 120, 25);

    	button.addSelectionListener( new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	actionGetInformations();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        });
		
		tabItem.setControl(tabContent);
	}
	//----------------------------------------------------------------------------------------------
	private void createInformationFields(Composite container) 
	{
		int x = 5 ; 
		int y = 20 ; 
		int yGap = 30 ; 
		int labelWidth = 120 ;
		int textWidth  = 320 ;
		
	    _InfoProdName  = createTextWithLabel(container, x, y, "Product name", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoProdVer   = createTextWithLabel(container, x, y, "Product version", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoDriverName  = createTextWithLabel(container, x, y, "Driver name", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoDriverVer   = createTextWithLabel(container, x, y, "Driver version", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoURL       = createTextWithLabel(container, x, y, "JDBC URL", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoUser      = createTextWithLabel(container, x, y, "User name", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoIsolation = createTextWithLabel(container, x, y, "Def. isolation level", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoMaxConn = createTextWithLabel(container, x, y, "Max connections", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoCatalogTerm = createTextWithLabel(container, x, y, "Catalog term", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoCatalogSepar = createTextWithLabel(container, x, y, "Catalog separator", true, labelWidth, textWidth ) ;  
	    y = y + yGap ;
	    _InfoSchemaTerm = createTextWithLabel(container, x, y, "Schema term", true, labelWidth, textWidth ) ; 
	    y = y + yGap ;
	    _InfoSearchEscape = createTextWithLabel(container, x, y, "Search escape", true, labelWidth, textWidth ) ; 
	}

	//----------------------------------------------------------------------------------------------
	private void createTabFolder3(TabFolder tabFolder) 
	{
		log(this, "createTabFolder3() ..." );
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("  Meta-data  ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setBackground( getBackgroundColor() );
		
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 12;
		tabContent.setLayout(gridLayout);
		
		createTabFolder3Fields(tabContent) ;
		
		tabItem.setControl(tabContent);

	}
	//----------------------------------------------------------------------------------------------
	private void createTabFolder3Panel1(Composite container) 
	{
		GridData gdPanel = new GridData();
		gdPanel.verticalAlignment = SWT.BEGINNING ;
		gdPanel.widthHint = 400 ;
		
		Composite panel = new Composite(container, SWT.NONE );
		GridLayout gridLayout = new GridLayout ();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 12;
		panel.setLayout(gridLayout);
		panel.setLayoutData(gdPanel);

		{
			GridData gd = new GridData();
			gd.widthHint = 260;
			gd.verticalAlignment = SWT.BEGINNING ;

			new Label(panel, SWT.NONE).setText( "Catalog : ");
			_tMetaDataCatalog = new Text(panel, SWT.BORDER);
			_tMetaDataCatalog.setLayoutData (gd);		
	
			new Label(panel, SWT.NONE).setText( "Schema : ");
			_tMetaDataSchema = new Text(panel, SWT.BORDER);
			_tMetaDataSchema.setLayoutData (gd);		
	
			new Label(panel, SWT.NONE).setText( "Table name pattern : ");
			_tMetaDataTablePattern = new Text(panel, SWT.BORDER);
			_tMetaDataTablePattern.setLayoutData (gd);		
	
			new Label(panel, SWT.NONE).setText( "Table types : ");
			_tMetaDataTableTypes = new Text(panel, SWT.BORDER);
			_tMetaDataTableTypes.setLayoutData (gd);
		}
	}
	//----------------------------------------------------------------------------------------------
	private void createTabFolder3Panel2(Composite container) 
	{
		GridData gdPanel = new GridData();
		gdPanel.verticalAlignment = SWT.BEGINNING ;
		//gdPanel.widthHint = 200 ;

		Composite panel = new Composite(container, SWT.NONE );
		GridLayout gl = new GridLayout ();
		gl.numColumns = 1;
		gl.marginHeight = 12;
		gl.marginLeft = 20 ;
		panel.setLayout(gl);
		panel.setLayoutData(gdPanel);

		GridData gd = new GridData();
		gd.widthHint = 120;
		gd.verticalAlignment = SWT.BEGINNING ;
		{
			Button button = new Button(panel, SWT.NONE);
			button.setText("Get tables");
			button.setLayoutData(gd);
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_NOTHING);
	            	//actionGetInformations();
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}

		{
	    	Button button = new Button(panel, SWT.NONE);
			button.setText("Get columns");
			button.setLayoutData(gd);		
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_COLUMNS);
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}

		{
	    	Button button = new Button(panel, SWT.NONE);
			button.setText("Get primary keys");
			button.setLayoutData(gd);		
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_PRIMARY_KEYS);
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}
		{
	    	Button button = new Button(panel, SWT.NONE);
			button.setText("Get foreign keys");
			button.setLayoutData(gd);		
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_FOREIGN_KEYS);
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}
	}
	//----------------------------------------------------------------------------------------------
	private void createTabFolder3Panel3(Composite container) 
	{
		GridData gdPanel = new GridData();
		gdPanel.verticalAlignment = SWT.BEGINNING ;
		//gdPanel.widthHint = 200 ;

		Composite panel = new Composite(container, SWT.NONE  );
		GridLayout gl = new GridLayout ();
		gl.numColumns = 1;
		gl.marginHeight = 12;
		gl.marginLeft = 20 ;
		panel.setLayout(gl);
		panel.setLayoutData(gdPanel);

		GridData gd = new GridData();
		gd.widthHint = 120;
		gd.verticalAlignment = SWT.BEGINNING ;
		{
			Button button = new Button(panel, SWT.NONE);
			button.setText("Get catalogs");
			button.setLayoutData(gd);
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_CATALOGS);
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}

		{
	    	Button button = new Button(panel, SWT.NONE);
			button.setText("Get schemas");
			button.setLayoutData(gd);		
	    	button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	actionGetMetaData(GET_SCHEMAS);
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        });
		}

	}
	//----------------------------------------------------------------------------------------------
	private void createTabFolder3Fields(Composite container) 
	{
		//--- ROW 1
		createTabFolder3Panel1(container);
		
		createTabFolder3Panel2(container);
		
		createTabFolder3Panel3(container);
		
		//--- ROW 2 ( Span 3 )
		{
			GridData gd = new GridData();
			gd.widthHint  = 700;
			gd.heightHint = 340 ;
			gd.horizontalSpan = 3 ;
			_tMetaData = new Text (container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
			
			_tMetaData.setLayoutData(gd);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private Text createTextWithLabel(Composite container, int x, int y, String sLabel, boolean b, int labelWidth, int textWidth ) 
	{
	     Label label = new Label(container, SWT.NONE);
	     label.setText( sLabel + " : ");
	     label.setBounds(x, y, labelWidth, TEXT_HEIGHT);
	     //label.setLocation(x, y);
	     
	     Text text = new Text(container, SWT.BORDER);
	     text.setBounds(x + labelWidth + 10 , y, 320, TEXT_HEIGHT);
	     //text.setLocation(x + 80, y);
	     text.setEnabled(b);
	     
	     return text ;
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
	
	
    //------------------------------------------------------------------------------------------------------
	private void loadComboBases(Combo comboBases, XmlDbConfig xmlDbConfig )
    {
		log(this, "loadComboBases()");
        comboBases.removeAll(); 
        
        XmlDatabase[] databases = xmlDbConfig.getDatabases();
		log(this, "loadComboBases() : nb bases = " + databases.length );
        for ( int i = 0 ; i < databases.length ; i++ )
        {
        	XmlDatabase db = databases[i];
        	if ( db != null ) {
                String sItem = db.getDatabaseId() + " - " + db.getDatabaseName() ;
                comboBases.add(sItem);
        		log(this, "loadComboBases() : add " + sItem );
        	}
        }
        
//        Element root = _doc.getDocumentElement();
//        if (root == null)
//        {
//            MsgBox.error("loadComboBase() : No XML document ( DOM root is null ) ");
//        }
//        else
//        {
//            NodeList basesList = root.getElementsByTagName(ConstXML.DB_ELEMENT);
//            if (basesList != null)
//            {
//                for ( int i = 0 ; i < basesList.getLength() ; i++ )
//                {
//                    Element base = (Element) basesList.item(i);
//
////                    String sDbId = Xml.getNodeAttribute(base, ConstXML.DB_ID_ATTRIBUTE);
////                    String sDbName = Xml.getNodeAttribute(base, ConstXML.DB_NAME_ATTRIBUTE);
//                    
//                    String sDbId   = _xmlDoc.getNodeAttribute(base, ConstXML.DB_ID_ATTRIBUTE);
//                    String sDbName = _xmlDoc.getNodeAttribute(base, ConstXML.DB_NAME_ATTRIBUTE);
//                    
//                    if (sDbId == null)
//                        sDbId = "?";
//                    if (sDbName == null)
//                        sDbName = "?????";
//                    String sItem = sDbId + " - " + sDbName;
//                    comboBases.add(sItem);
//                }
//            }
//        }
//        return;
    }
	
    private void populateDatabases() 
    {
    	XmlDbConfig xmlDbConfig = _editor.getXmlDbConfig();
    	if ( xmlDbConfig != null )
    	{
    		if ( _ComboDatabases != null )
    		{
        		//xmlDbConfig.loadComboBases(_ComboDatabases);
        		loadComboBases(_ComboDatabases, xmlDbConfig);
    		}
    		else
    		{
        		MsgBox.error("populateDatabases : ComboBox is null !");
    		}
    	}
    	else
    	{
    		MsgBox.error("populateDatabases : XmlDbConfig is null ( File not loaded ) ");
    	}
    	
//    	Document doc = _editor.getXmlDocument();
//    	if ( doc != null )
//    	{
//        	if ( _ComboDatabases != null )
//        	{
//            	populateDatabases(_ComboDatabases, doc); 
//        	}
//        	else
//        	{
//        		MsgBox.error("populateDatabases : ComboBox is null !");
//        	}
//    	}
//    	else
//    	{
//    		MsgBox.error("populateDatabases : XML document is null !");
//    	}
    }
    
//    private Element getDatabaseElement(String sDbId) 
//    {
//    	if ( sDbId == null )
//    	{
//    		MsgBox.error("getDatabaseNode : DB id is null !");
//    		return null ;
//    	}
//    	
//    	NodeList list = getDatabasesList() ;
//    	if ( list != null )
//    	{
//            for (int i=0; i< list.getLength(); i++)
//            {
//                Element e = (Element) list.item(i);                
//                String a = e.getAttribute(ConstXML.DB_ID_ATTRIBUTE);
//                if ( a != null)
//                {
//                	if ( a.equals(sDbId) )
//                	{
//                		return e ;
//                	}
//                }
//            }
//    	}
//    	return null ;
//    }
    
//    private String getDatabaseProperty(Element eDB, String sName) 
//    {
//		PluginLogger.log(this, "getDatabaseProperty(..," + sName + ")" );
//    	NodeList list = eDB.getElementsByTagName(ConstXML.DB_PROPERTY_ELEMENT);
//    	if ( list != null )
//    	{
//    		PluginLogger.log(this, "getDatabaseProperty(..," + sName + ") : ChildNodes size " + list.getLength() );
//            for (int i=0; i< list.getLength(); i++)
//            {
//                Element e = (Element) list.item(i); 
//        		PluginLogger.log(this, "getDatabaseProperty(..," + sName + ") : TAG " + e.getTagName() );
//
//            	String name = e.getAttribute( ConstXML.DB_PROPERTY_NAME_ATTRIBUTE ) ;
//            	if ( name != null )
//            	{
//            		if ( name.equals(sName) )
//            		{
//            			return e.getAttribute(ConstXML.DB_PROPERTY_VALUE_ATTRIBUTE );
//            		}
//            	}
//            }
//    	}
//    	
//    	return null ;
//    }
    
//    private void populateDatabases(Combo combo, Document doc) 
//    {
//    	combo.removeAll();
//        //NodeList databasesList = doc.getElementsByTagName(ConstXML.DB_ELEMENT);
//        NodeList databasesList = getDatabasesList() ;
//    	if ( databasesList != null )
//    	{
//	        for (int i=0; i< databasesList.getLength(); i++)
//	        {
//	            Element base = (Element) databasesList.item(i);
//	            
//	            String sId   = base.getAttribute(ConstXML.DB_ID_ATTRIBUTE);
//	            String sName = base.getAttribute(ConstXML.DB_NAME_ATTRIBUTE);
//	            
//	            combo.add(sId + " - " + sName);
//	        }
//    	}
//    }
	
//	private Group createTableGroup(Composite composite)
//	{
//		PluginLogger.log(this, "createTableGroup(..)..." );
//		
//		Group group = new Group(composite, SWT.NONE);
//		group.setSize(650, 400);
//		//group2.setLocation(GROUP_X, 20);
//        group.setBackground( _backgroundColor );
//        
//        createTable(group);
//        return group;
//	}
//
//	private Composite createTableComposite(Composite composite)
//	{
//		PluginLogger.log(this, "createTableComposite(..)..." );
//		
//		Composite c = new Composite(composite, SWT.NONE);
//		c.setSize(650, 400);
//        c.setBackground( _backgroundColor );
//        createTable(c);
//        return c;
//	}
	
	private void clearFields()
	{
		//--- Tab 1
		_Id.setText("");
		_Name.setText("");
		_Driver.setText("");
		_Url.setText("");
		_Isolation.setText("");
		_PoolSize.setText("");
		_User.setText("");
		_Password.setText("");
	      
		//--- Tab 2
		_InfoURL.setText( "" );
		_InfoProdName.setText( "" );
		_InfoProdVer.setText( "" );
		_InfoDriverName.setText( "" );
		_InfoDriverVer.setText( "" );
		_InfoMaxConn.setText( "" );
		_InfoUser.setText("");
		_InfoIsolation.setText( "" );
		_InfoCatalogTerm.setText( "" );
		_InfoCatalogSepar.setText( "" );
		_InfoSchemaTerm.setText( "" );
		_InfoSearchEscape.setText( "" );
	}
	
	private String nn ( String s )
	{
		return s != null ? s : "" ;
	}

	private String arrayToString ( String[] array )
	{
		if ( array == null ) return "" ;
		StringBuilder sb = new StringBuilder();
		for ( int i = 0 ; i < array.length ; i++ )
		{
			if ( i > 0 ) sb.append(", ");
			sb.append(array[i]);
		}
		return sb.toString();
	}
	private String[] stringToArray ( String s )
	{
		if ( s == null ) return new String[0] ;
		String[] parts = s.split(",");
		String[] parts2 = new String[parts.length];
		for ( int i = 0 ; i < parts.length ; i++ ) {
			parts2[i] = parts[i].trim() ;
		}
		return parts2 ;
	}
	
	
	private void populateFields( String sDbId )
	{
		log(this, "populateFields('" + sDbId + "')");
		
		_bPopulateInProgress = true ;
		
		XmlDbConfig xmlDbConfig = _editor.getXmlDbConfig();
		if ( xmlDbConfig != null )
		{
			XmlDatabase db = xmlDbConfig.getDatabaseConfig(sDbId);
			if ( db != null )
			{
				//--- Attributes  
				_Id.setText       ( nn( db.getDatabaseId() ) );
				_Name.setText     ( nn( db.getDatabaseName() ) );
				_Driver.setText   ( nn( db.getDriverClass() ) );
				_Url.setText      ( nn( db.getJdbcUrl() ) );
				_Isolation.setText( nn( db.getIsolationLevel() ) );
				_PoolSize.setText ( nn( db.getPoolSize() ) );
				//--- Properties 
				_User.setText    ( nn( db.getUser() ) );
			    _Password.setText( nn( db.getPassword() ));
			    
			    //--- Meta-Data
			    _tMetaDataCatalog.setText( nn( db.getMetadataCatalog() ) ) ;
			    _tMetaDataSchema.setText( nn( db.getMetadataSchema() ) ) ;
			    _tMetaDataTablePattern.setText( nn( db.getMetadataTableNamePattern() ) );
			    _tMetaDataTableTypes.setText( arrayToString ( db.getMetadataTableTypes() ) );
			}
			else 
			{
				MsgBox.error("Database '" + sDbId + "' not found in the XML DOM");
			}
		}

		_bPopulateInProgress = false ;
	}
	
    /**
     * Listener for DATABASE COMBO BOX 
     * Event fired when a database is selected
     * @param combo
     */
    private void setDatabasesComboAction(Combo combo)
    {
        combo.addSelectionListener( new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event)
            {
            	clearFields();
            	
        		//log(this, "Tables combo listener : widgetSelected()" );
                Combo combo = (Combo) event.widget ;
                String sDbId = "";
                String s = combo.getText();
                if ( s != null )
                {
                    String[] parts = StrUtil.split(s, '-');
                    if ( parts.length > 0 )
                    {
                    	sDbId = parts[0].trim();
                    }
                }
        		log(this, "Databases combo listener : widgetSelected() : DB id = " + sDbId );
                
        		//--- Populate the database fields
        		populateFields( sDbId );
        		
            }
        });
    }
    
    private void setModifyListener(Text text, String sAttributeName )
    {
    	//--- Set the XML attribute name associated with this Text Widget
    	text.setData(sAttributeName);
    	
    	//--- Add the listener
    	text.addModifyListener(new ModifyListener()
    	{
    		// This event is fired even when the Text is modified programmatically
    		// without user action
			public void modifyText(ModifyEvent e) {
				if ( ! _bPopulateInProgress )
				{					
					Text text = (Text) e.widget ;
					String sTextValue = text.getText();
	        		log(this, "Text modified : '" + sTextValue + "'" );
	        		//--- Update the model 
	        		// ...
					setDirty();
				}
				else
				{
	        		log(this, "Text modified : populate in progress => no action" );
				}
			}
    		
    	});
    }
    
    /**
     * Returns the Database configuration for the selected Database ID
     * ( loaded from the XML DOM tree )
     * @return
     */
    private XmlDatabase getDatabaseConfig() 
    {
        XmlDbConfig xmlDbConfig = _editor.getXmlDbConfig();
        if ( xmlDbConfig != null )
        {
        	String id = _Id.getText() ;
        	XmlDatabase db = xmlDbConfig.getDatabaseConfig( id ) ;
        	if ( db != null )
        	{
        		return db ;
        	}
        	else
        	{
        		MsgBox.error("No database configuration for id '" + id + "'" );
        	}
        }
        else
        {
    		MsgBox.error("No databases configuration file (file probably not loaded) " );
        }
        return null ;
    }
    
    private void msgBoxErrorWithClassPath(String msg, Exception e, String[] classPath) 
    {
		StringBuffer sb = new StringBuffer();
		sb.append(msg + "\n" ) ;
		sb.append("Project Class Path : \n" ) ;
		for ( String s : classPath ) {
			sb.append(" . " + s + "\n") ;
		}
		MsgBox.error(sb.toString(), e );
    }
    
    private ConnectionManager getConnectionManager() 
    {
        IProject project = _editor.getProject();
        TelosysToolsLogger logger = _editor.getTextWidgetLogger();
        String[] libraries = EclipseProjUtil.getClassPathLibraries(project);
        ConnectionManager cm = null ;
		try {
			cm = new ConnectionManager( libraries, logger );
		} catch (TelosysToolsException e) {
			logException(e);
			msgBoxErrorWithClassPath("Cannot create ConnectionManager", e, libraries);
    		cm = null ;
		}
        return cm ;
    }
    
    private String getRepositoryFileName(String sDatabaseName) 
    {
		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig( _editor.getProject() );		
        String dir   = projectConfig.getRepositoriesFolder();
        return FileUtil.buildFilePath(dir, sDatabaseName+".dbrep" );
    }
    
    private File getRepositoryFile(String sDatabaseName) 
    {
		String sRepositoryFile = getRepositoryFileName( sDatabaseName ) ;

		IProject project = _editor.getProject();
        if ( project == null )
        {
        	MsgBox.error("actionGenerateRepository() : Cannot get project ");
        	return null ;
        }
		IFile iFile = project.getFile(sRepositoryFile);
		File file = EclipseWksUtil.toFile(iFile);
		return file ;
    }
    
    /**
     * Returns the log file name ( built from the repository file name )
     * @param sRepoFile
     * @return
     */
    private String getUpdateLogFileName(String sRepoFile)
    {
    	Date now = new Date();
    	String suffix = ".update." + DATE_FORMAT.format( now ) + ".log";
    	if ( sRepoFile.endsWith(".dbrep") )
    	{
    		int last = sRepoFile.length() - 6 ; 
    		return sRepoFile.substring(0,last) + suffix ;
    	}
    	else
    	{
    		return sRepoFile + suffix ;
    	}
    }
    
    private File getUpdateLogFile(String sRepoFile) 
    {
		String sRepositoryFile = getUpdateLogFileName( sRepoFile ) ;
		File file = new File(sRepositoryFile);
		return file ;
    }
    
    private Connection getConnection() 
    {
        XmlDatabase db = getDatabaseConfig() ;
        if ( null == db ) return null ;
        
		ConnectionManager cm = getConnectionManager();
        if ( null == cm ) return null ;

        Connection con = null ;
		try {
			
			con = cm.getConnection( db.getDriverClass(), db.getJdbcUrl(), db.getProperties() );

		} catch (TelosysToolsException e) {
			logException(e);
			Throwable cause = e.getCause();
			if ( cause != null && cause instanceof SQLException ) {
				SQLException sqlException = (SQLException)cause;
	            MsgBox.error("Cannot connect to the database ! "
	                      + "\n SQLException :"
	                      + "\n . Message : " + sqlException.getMessage() 
	                      + "\n . ErrorCode : " + sqlException.getErrorCode() 
	                      + "\n . SQLState : " + sqlException.getSQLState() 
	                      );
			}
			else {
				msgBoxErrorWithClassPath("Cannot connect to the database !", e, cm.getLibraries());

//	            MsgBox.error("Cannot connect to the database ! "
//	                      + "\n TelosysToolsException :"
//	                      + "\n . Message : " + e.getMessage() 
//	                      );
			}
			return null ;
		} catch (Throwable e) {
			logException(e);
            MsgBox.error("Cannot connect to the database ! "
                    + "\n Exception : " + e.getClass().getName()
                    + "\n . Message : " + e.getMessage() 
                    );
			return null ;
		}
		return con ;
    }
    
    private void closeConnection(Connection con) 
    {
	    try {
		    con.close();
		} catch (SQLException e) {
			MsgBox.error("Cannot close connection \n\n SQLException : " + e.getMessage() );
		}
    }
    
    /**
     * Show the project's libraries defined in the "Java Build Path"
     */
    private void showLibraries() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Project JAR files : \n\n") ;
    	String[] libraries = EclipseProjUtil.getClassPathLibraries( _editor.getProject() );
    	if ( libraries != null ) {
    		for ( String s : libraries ) {
    			sb.append(s + "\n");
    		}
    	}
    	else {
    		sb.append("\n");
    		sb.append("No JAR file !\n");
    	}
    	MsgBox.info(sb.toString());
    }
    
    /**
     * Test the Database connection
     */
    private void actionTestConnection() 
    {
    	Shell shell = Util.cursorWait();

    	Connection con = getConnection();
    	if ( con != null )
    	{
    		boolean ok = true ;
	    	String catalog = "" ;
	    	boolean autocommit = false ;
	    	
			try {
				catalog = con.getCatalog() ;
			} catch (SQLException e) {
				ok = false ;
				String s = "Cannot get 'catalog' from the connection ! " ;
				logError(s);
				MsgBox.error(s);
			}
	
			try {
				autocommit = con.getAutoCommit() ;
			} catch (SQLException e) {
				ok = false ;
				String s = "Cannot get 'auto-commit' from the connection ! " ;
				logError(s);
				MsgBox.error(s);
			} 
			
			closeConnection(con);

			if ( ok ) {
				MsgBox.info("Connection is OK.\n\n" 
						+ " . catalog = '" + catalog + "' \n" 
						+ " . auto-commit = '" + autocommit + "' \n" );
			}
    	}
		Util.cursorArrow(shell);
    }

    private void actionGetInformations() 
    {
    	Shell shell = Util.cursorWait();
    	
        //MsgBox.debug("Get Informations");
        
//        XmlDatabase db = getDatabaseConfig() ;
//		ConnectionManager cm = getConnectionManager();
//        if ( db != null && cm != null )
//        {
//    		Connection con = cm.getConnection(db.getDriverClass(), db.getJdbcUrl(), db.getProperties() );
    		Connection con = getConnection();
    		if ( con != null )
    		{
	            try {
					DatabaseMetaData dbmd = con.getMetaData();
					
				    _InfoURL.setText( dbmd.getURL() );
				    _InfoProdName.setText( dbmd.getDatabaseProductName() );
				    _InfoProdVer.setText( dbmd.getDatabaseProductVersion() );
				    _InfoDriverName.setText( dbmd.getDriverName() );
				    _InfoDriverVer.setText( dbmd.getDriverVersion() );
				    _InfoMaxConn.setText( ""+dbmd.getMaxConnections() );
				    _InfoUser.setText(dbmd.getUserName());
				    _InfoIsolation.setText( ""+dbmd.getDefaultTransactionIsolation() );
				    
				    _InfoCatalogTerm.setText( dbmd.getCatalogTerm() );
				    _InfoCatalogSepar.setText( dbmd.getCatalogSeparator() );
					
				    _InfoSchemaTerm.setText( dbmd.getSchemaTerm() );
				    _InfoSearchEscape.setText( dbmd.getSearchStringEscape() );
				    
				    con.close();
				    
				} catch (SQLException e) {
					MsgBox.error("SQLException : " + e.getMessage() );
				} finally {
					closeConnection(con);
				}
	            
				//cm.closeConnection(con);
    		}
    		// else : nothing to do ( message already showned )

//        }
    		Util.cursorArrow(shell);
    }

    private void actionGetMetaData(int whatElse) 
    {
    	Shell shell = Util.cursorWait();
		_tMetaData.setText("");
    	Connection con = getConnection();
    	if ( con != null )
    	{
    		String sCatalog = _tMetaDataCatalog.getText();
    		String sSchema  = _tMetaDataSchema.getText();
    		String sTableNamePattern = _tMetaDataTablePattern.getText();
    		String[] tableTypes = stringToArray(_tMetaDataTableTypes.getText());
    		
			DatabaseMetaData dbmd = null ;
			List<TableMetaData> tables = null ;
			try {
				//--- Get the database Meta-Data
				dbmd = con.getMetaData();		

			} catch (SQLException e) {
				MsgBox.error("Cannot get meta-data ! " );
			} 
			
			TelosysToolsLogger logger = _editor.getTextWidgetLogger();
    		MetaDataManager metaDataManager = new MetaDataManager(logger);

    		//--- Get the tables
    		try {
				tables = metaDataManager.getTables(dbmd, sCatalog, sSchema, sTableNamePattern, tableTypes );
			} catch (SQLException e) {
				tables = null ;
				MsgBox.error("Cannot get tables from meta-data ! " );
			} 
			
			if ( tables != null ) 
			{
				switch ( whatElse )
				{
				case GET_COLUMNS :
					getMetaDataColumns( metaDataManager, dbmd, tables, sCatalog, sSchema);
	//	    		//--- Get the columns for each table
	//				Iterator iter = tables.iterator();
	//				while ( iter.hasNext() )
	//				{
	//					TableMetaData t = (TableMetaData) iter.next();
	//					String sTableName = t.getTableName();
	//					try {
	//						List columns = metaDataManager.getColumns(dbmd, sCatalog, sSchema, sTableName );
	//						printMetaDataColumns(sTableName, columns);
	//					} catch (SQLException e) {
	//						MsgBox.error("Cannot get columns for table '" + sTableName + "'" );
	//						break;
	//					} 
	//				}
					break;
					
				case GET_PRIMARY_KEYS :
					getMetaDataPrimaryKeys( metaDataManager, dbmd, tables, sCatalog, sSchema);
					break;
					
				case GET_FOREIGN_KEYS :
					getMetaDataForeignKeys( metaDataManager, dbmd, tables, sCatalog, sSchema);
					break;
					
				case GET_CATALOGS :
					getMetaDataCatalogs( metaDataManager, dbmd);
					break;
					
				case GET_SCHEMAS :
					getMetaDataSchemas( metaDataManager, dbmd);
					break;
					
				default :
					printMetaDataTables(tables);
					break;
				}
			}
			closeConnection(con);
    	}
		Util.cursorArrow(shell);
    }
    
    private void getMetaDataColumns(MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String sCatalog, String sSchema) 
    {
		//--- Get the columns for each table
//		Iterator iter = tables.iterator();
//		while ( iter.hasNext() )
//		{
//			TableMetaData t = (TableMetaData) iter.next();
		for ( TableMetaData t : tables ) {
			String sTableName = t.getTableName();
			try {
				List<ColumnMetaData> columns = metaDataManager.getColumns(dbmd, sCatalog, sSchema, sTableName );
				printMetaDataColumns(sTableName, columns);
			} catch (SQLException e) {
				MsgBox.error("Cannot get columns for table '" + sTableName + "'" );
				break;
			} 
		}
    }
    
    private void getMetaDataPrimaryKeys(MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String sCatalog, String sSchema) 
    {
		//--- Get the columns for each table
//		Iterator iter = tables.iterator();
//		while ( iter.hasNext() )
//		{
//			TableMetaData t = (TableMetaData) iter.next();
		for ( TableMetaData t : tables ) {
			String sTableName = t.getTableName();
			try {
				List<PrimaryKeyColumnMetaData> columns = metaDataManager.getPKColumns(dbmd, sCatalog, sSchema, sTableName );
				printMetaDataPrimaryKeys(sTableName, columns);
			} catch (SQLException e) {
				MsgBox.error("Cannot get Primary Key for table '" + sTableName + "'" );
				break;
			} 
		}
    }

    private void getMetaDataForeignKeys(MetaDataManager metaDataManager, DatabaseMetaData dbmd, 
    		List<TableMetaData> tables, String sCatalog, String sSchema) 
    {
		//--- Get the columns for each table
//		Iterator iter = tables.iterator();
//		while ( iter.hasNext() )
//		{
//			TableMetaData t = (TableMetaData) iter.next();
		for ( TableMetaData t : tables ) {
			String sTableName = t.getTableName();
			try {
				List<ForeignKeyColumnMetaData> columns = metaDataManager.getFKColumns(dbmd, sCatalog, sSchema, sTableName );
				printMetaDataForeignKeys(sTableName, columns);
			} catch (SQLException e) {
				MsgBox.error("Cannot get Foreign Keys for table '" + sTableName + "'" );
				break;
			} 
		}
    }
    
    //----------------------------------------------------------------------------------------------------
    private void getMetaDataCatalogs(MetaDataManager metaDataManager, DatabaseMetaData dbmd ) 
    {
    	try {
			List<String> list = metaDataManager.getCatalogs(dbmd);
			printMetaDataCatalogs(list);
		} catch (SQLException e1) {
			MsgBox.error("Cannot get catalogs" );
		}
    }
    private void printMetaDataCatalogs(List<String> list)
    {
//		Iterator iter = list.iterator();
//		while ( iter.hasNext() )
//		{
//			String s = (String) iter.next();
		for ( String s : list ) {
			_tMetaData.append(" . " + s + " \n");
		}
    }

    //----------------------------------------------------------------------------------------------------
    private void getMetaDataSchemas(MetaDataManager metaDataManager, DatabaseMetaData dbmd ) 
    {
    	try {
			List<SchemaMetaData> list = metaDataManager.getSchemas(dbmd);
			printMetaDataSchemas(list);
		} catch (SQLException e1) {
			MsgBox.error("Cannot get schemas" );
		}
    }
    private void printMetaDataSchemas(List<SchemaMetaData> list)
    {
//		Iterator iter = list.iterator();
//		while ( iter.hasNext() )
//		{
//			SchemaMetaData schema = (SchemaMetaData) iter.next();
		for ( SchemaMetaData schema : list ) {
			_tMetaData.append(" . " + schema.getSchemaName() + " ( catalog : "+ schema.getSchemaName() + " ) \n");
		}
    }
    //----------------------------------------------------------------------------------------------------
    
    private void printMetaDataTables(List<TableMetaData> tables)
    {
//		Iterator iter = tables.iterator();
//		while ( iter.hasNext() )
//		{
//			TableMetaData t = (TableMetaData) iter.next();
		for ( TableMetaData t : tables ) {
			_tMetaData.append(" . " + t.getTableName() + " (" + t.getTableType() + ") "
				+ " catalog = '" + t.getCatalogName() + "'"
				+ " schema = '" + t.getSchemaName() + "'" 
				+ "\n");

		}
    }
    private void printMetaDataColumns(String tableName, List<ColumnMetaData> columns)
    {
    	_tMetaData.append("Table '" + tableName + "' : \n");
//		Iterator iter = columns.iterator();
//		while ( iter.hasNext() )
//		{
//			ColumnMetaData c = (ColumnMetaData) iter.next();
		for ( ColumnMetaData c : columns ) {
			String s = 
					"[" +c.getOrdinalPosition() + "]"
					+ " " + c.getColumnName() + " : " 
					+ "  " + c.getDbTypeName() 
					+ "  (jdbc:" + c.getJdbcTypeCode()+")" 
					+ "  size=" + c.getSize()
					+ "  " + ( c.isNotNull() ? "NOT NULL" : "" )
					+ "  charOctetLength=" + c.getCharOctetLength()
					+ "  decimalDigits=" + c.getDecimalDigits()
					+ "  numPrecRadix=" + c.getNumPrecRadix()
					+ "  defaultValue=" + c.getDefaultValue()
					;
			
			_tMetaData.append(" . " + s + " \n");
		}
    	_tMetaData.append("\n");
    }
    
    private void printMetaDataPrimaryKeys(String tableName, List<PrimaryKeyColumnMetaData> columns)
    {
    	_tMetaData.append("Table '" + tableName + "' : \n");
//		Iterator iter = columns.iterator();
//		while ( iter.hasNext() )
//		{
//			PrimaryKeyColumnMetaData c = (PrimaryKeyColumnMetaData) iter.next();
		for ( PrimaryKeyColumnMetaData c : columns ) {
			String s = 
					" " + c.getPkName() + " : " 
					+ " [" + c.getPkSequence() + "]"
					+ "  " + c.getColumnName()
					;
			
			_tMetaData.append(" . " + s + " \n");
		}
    	_tMetaData.append("\n");
    }
    
    private void printMetaDataForeignKeys(String tableName, List<ForeignKeyColumnMetaData> columns)
    {
    	_tMetaData.append("Table '" + tableName + "' : \n");
//		Iterator iter = columns.iterator();
//		while ( iter.hasNext() )
//		{
//			ForeignKeyColumnMetaData c = (ForeignKeyColumnMetaData) iter.next();
		for ( ForeignKeyColumnMetaData c : columns ) {
			System.out.println(
					" . " 
					+ " fkName=" + c.getFkName()
					+ " pkName=" + c.getPkName()
					+ " fkTableName=" + c.getFkTableName() 
					+ " fkColumnName=" + c.getFkColumnName() 
					+ " pkTableName=" + c.getPkTableName() 
					+ " pkColumnName=" + c.getPkColumnName() 
					);
			
			String s = 
					" " + c.getFkName() + " : " 
					+ c.getFkTableName() + "." + c.getFkColumnName()
					+ " --> " 
					+ c.getPkTableName()  + "." + c.getPkColumnName()
					+ "  ( PK : " + c.getPkName() + ")"
					;
			
			_tMetaData.append(" . " + s + " \n");
		}
    	_tMetaData.append("\n");
    }
    
    private IProject getEclipseProject()
    {
        IProject project = _editor.getProject();
        if ( project == null )
        {
        	MsgBox.error("actionGenerateRepository() : Cannot get project ");
        }
    	return project ;
    }
    
    private void syncRepoFolder()
    {
    	IProject project = getEclipseProject();
    	ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig(project);
		log("repositoriesFolder = " + projectConfig.getRepositoriesFolder() );
		IFolder repositoriesFolder = project.getFolder(projectConfig.getRepositoriesFolder());
		log("is repositories Folder Synchronized = " + repositoriesFolder.isSynchronized(IResource.DEPTH_INFINITE) );
		try {
			repositoriesFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			log("refreshLocal : CoreException " );
		}
		log("is repositories Folder Synchronized = " + repositoriesFolder.isSynchronized(IResource.DEPTH_INFINITE) );
    	
    }

    /**
     * Generates the repository file 
     */
    private void actionGenerateRepository() 
    {
        // MsgBox.debug("Generate Repository");
        boolean repositoryCreated = false ;
        String sRepositoryFile = null ;
        
        IProject project = _editor.getProject();
        if ( project == null )
        {
        	MsgBox.error("actionGenerateRepository() : Cannot get project ");
        	return ;
        }

        //TelosysToolsLogger logger = new DefaultLogger();	
        TelosysToolsLogger logger = _editor.getTextWidgetLogger();	
		
        XmlDatabase db = getDatabaseConfig() ;
		String sDatabaseName = db.getDatabaseName();		
		sRepositoryFile = getRepositoryFileName( db.getDatabaseName() ) ;
		
		IFile repositoryFile = project.getFile(sRepositoryFile);

		ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig(project);
        
		String sMsg = "This operation will replace the current version of the repository if it exists." 
				+ "\n\n" + "Repository file : \n" + sRepositoryFile 
				+ "\n\n" + "Database name : \n" + sDatabaseName 
				+ "\n\n" + "Launch the generation ?";
		if ( MsgBox.confirm(" Confirm generation", sMsg) )
		{
			Shell shell = Util.cursorWait();
			
    		Connection con = getConnection() ;
    		if ( con != null )
    		{
				try {
					repositoryCreated = generateRepository(con, db, projectConfig, repositoryFile, logger ) ;
				} 
				catch (Exception e)  // Catch ALL exceptions 
				{
					MsgBox.error("Exception : " + e.getClass() + " \n\n" + e.getMessage() ) ;
				} 
				finally {
					closeConnection(con);
				}

				if ( repositoryCreated )
		        {
		        	syncRepoFolder();
					Util.cursorArrow(shell);
					MsgBox.info("Repository generated.\n\nSee file " + sRepositoryFile );
		        }
				else
				{
					Util.cursorArrow(shell);
				}
			}
    		// if connection is null message already shown
			Util.cursorArrow(shell);
		}
    }
    
    private boolean generateRepository(Connection con, XmlDatabase db, ProjectConfig projectConfig, 
    		IFile repositoryFile,
    		TelosysToolsLogger logger ) 
    {
		InitializerChecker initchk = new DefaultInitializerChecker();
		ClassNameProvider classNameProvider = new ProjectClassNameProvider(projectConfig);

		RepositoryModel repo = null ;

		//--- 1) Generate the repository in memory
		try {
			RepositoryGenerator generator = new RepositoryGenerator(initchk, classNameProvider, logger) ;			
			repo = generator.generate(con, 
					db.getDatabaseName(), db.getMetadataCatalog(), db.getMetadataSchema(), 
					db.getMetadataTableNamePattern(), db.getMetadataTableTypes());
		} catch (TelosysToolsException e) {
			MsgBox.error("Cannot generate.", e);
			return false ;
		}
			
		//--- 2) Save the repository in the file
		try {
			File file = EclipseWksUtil.toFile(repositoryFile);
			logger.info("Saving repository in file " + file.getAbsolutePath() );
			StandardFilePersistenceManager pm = new StandardFilePersistenceManager(file, logger);
			pm.save(repo);
			logger.info("Repository saved.");
			
		} catch (TelosysToolsException e) {
			MsgBox.error("Cannot save file", e);
			return false ;
		}

		return true ;
    }

    private int updateRepository(Connection con, XmlDatabase db, ProjectConfig projectConfig, TelosysToolsLogger logger ) 
    	throws TelosysToolsException
    {
		InitializerChecker initchk = new DefaultInitializerChecker();
		ClassNameProvider classNameProvider = new ProjectClassNameProvider(projectConfig);
		
		//--- 1) LOAD the repository from the file
		File repositoryFile = getRepositoryFile( db.getDatabaseName() );
		logger.info("Load repository from file " + repositoryFile.getAbsolutePath());
		StandardFilePersistenceManager persistenceManager = new StandardFilePersistenceManager(repositoryFile, logger);
		RepositoryModel repositoryModel = persistenceManager.load();		
		logger.info("Repository loaded : " + repositoryModel.getNumberOfEntities() + " entitie(s)"  );

		
		//--- 2) UPDATE the repository in memory
		File updateLogFile = getUpdateLogFile( repositoryFile.getAbsolutePath() );
		UpdateLogWriter    updateLogger      = new UpdateLogWriter( updateLogFile ) ;
		

		RepositoryUpdator updator = new RepositoryUpdator(initchk, classNameProvider, logger,  updateLogger);
		int nbChanges = updator.updateRepository(con, repositoryModel, 
					db.getMetadataCatalog(), db.getMetadataSchema(), 
					db.getMetadataTableNamePattern(), db.getMetadataTableTypes() );
		
		//--- 3) SAVE the repository in the file
		logger.info("Save repository in file " + repositoryFile.getAbsolutePath());
		persistenceManager.save(repositoryModel);
		logger.info("Repository saved.");
		
		return nbChanges ;
    }
    
    /**
     * Updates the repository file 
     */
    private void actionUpdateRepository() 
    {
    	int nbChanges = 0 ;
    	
        //MsgBox.debug("Update Repository");
        boolean repositoryUpdated = false ;
        //String sRepositoryFile = null ;
        
        IProject project = _editor.getProject();
        if ( project == null )
        {
        	MsgBox.error("actionGenerateRepository() : Cannot get project ");
        	return ;
        }

        XmlDatabase db = getDatabaseConfig() ;
		String sDatabaseName = db.getDatabaseName();
		String sRepositoryFile = getRepositoryFileName( db.getDatabaseName() ) ;
		
		//--- Check repository existence
		IFile iFile = project.getFile(sRepositoryFile);
		if ( ! iFile.exists() )
		{
        	MsgBox.info("The repository file doesn't exist => cannot update."
        			+ "\n\nRepository file : \n" + sRepositoryFile );
        	return ;
		}
        
        ConnectionManager cm = getConnectionManager();
        
        if ( db != null && cm != null )
        {
			String sMsg = "This operation will update the current repository if it exists." 
				+ "\n\n" + "Repository file : \n" + sRepositoryFile 
				+ "\n\n" + "Database name : \n" + sDatabaseName 
				+ "\n\n" + "Launch update ?";
			if ( MsgBox.confirm(" Confirm ", sMsg) )
			{
				Shell shell = Util.cursorWait();
				
	    		//Connection con = cm.getConnection(db.getDriverClass(), db.getJdbcUrl(), db.getProperties() );
	    		Connection con = getConnection();
	    		if ( con != null )
	    		{
	    	        try {
	    	        	ProjectConfig projectConfig = ProjectConfigManager.getProjectConfig(project);
//	    	        	TelosysToolsLogger logger = new DefaultLogger();
	    	        	TelosysToolsLogger logger = _editor.getTextWidgetLogger();
	    	        	
	    	        	nbChanges = updateRepository(con, db, projectConfig, logger ) ;
							
    	        		repositoryUpdated = true ;
					}
	    	        catch ( Throwable e ) // Catch ALL exceptions 
					{
						MsgBox.error("Exception : " + e.getClass() + " \n\n" + e.getMessage() ) ;
					}
	    	        finally
	    	        {
						closeConnection(con);
	    	        }
	    		}
	    		// else : no connection : nothing to do ( message already showned )

	    		if ( repositoryUpdated )
	            {
	            	syncRepoFolder();

	            	Util.cursorArrow(shell);

	            	String msgChanges = "No change." ;
	            	if ( nbChanges > 0 )
	            	{
	            		msgChanges = nbChanges + " change(s).\n\n"
	            		  			+ "See the update log file for details.";
	            	}
	                MsgBox.info("Repository updated.\n"
	                		+ "\n" 
	                		+ "Repository file : \n" 
	                		+ sRepositoryFile + "\n"
	                		+ "\n"
	                		+ msgChanges );
	            }
	    		else
	    		{
	    			Util.cursorArrow(shell);
	    		}
			}

        }
        
    }

}