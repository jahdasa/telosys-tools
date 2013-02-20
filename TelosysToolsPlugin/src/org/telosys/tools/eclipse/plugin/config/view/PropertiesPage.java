package org.telosys.tools.eclipse.plugin.config.view;

import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.commons.VariablesUtil;
import org.telosys.tools.eclipse.plugin.MyPlugin;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.eclipse.plugin.config.PropName;
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.GeneratorVersion;
import org.telosys.tools.generator.config.GeneratorConfigConst;
import org.telosys.tools.generator.context.VariableNames;

/**
 * Plugin properties configuration page ( for each project )
 * This page contains 5 tabs
 * 
 */
public class PropertiesPage extends PropertyPage {

	private final static String WEB_CONTENT = "WebContent" ;

    //private final static String PLUGIN_PROPERTIES_FILE = "telosys-tools.cfg";
    
    //--- Tab "General"
	private Text _tProjectName = null ;
	private Text _tProjectLocation = null;
	private Text _tWorkspaceLocation = null ;
	
//	private Text _tSourceFolder = null ;
//	private Text _tWebContentFolder = null ;
	private Text _tTemplatesFolder = null ;
	private Text _tRepositoriesFolder = null ;
//	private Text _tTelosysProp = null ;
	
    //--- Tab "Packages"
	private Text _tBeanPackage = null ;
//	private Text _tVOListPackage = null ;
//	private Text _tDaoPackage = null ;
//	private Text _tXmlMapperPackage = null ;
//	
//	private Text _tScreenDataPackage = null ;
//	private Text _tScreenManagerPackage = null ;
//	private Text _tScreenTriggersPackage = null ;
//	private Text _tScreenProceduresPackage = null ;
	
//    //--- Tab "Classes names"
//	private Text _tVOListClassName = null ;
//	private Text _tXmlMapperClassName = null ;
//	private Text _tDAOClassName = null ;
	
	//--- Tab "Variables"
	private VariablesTable _variablesTable = null ;
	
    //--- Tab "Info"
	private Text _tPluginConfigFile = null ;
	
    //--------------------------------------

	private Label checkClassDirLabel;

	private Text checkClassDirText;

	private Label checkClassLabel;

	private Text checkClassText;

	private Group checkGroup;

	private Button classDirPickerButton;

	//private String currentProject;

	//private Text daoPackageText;

	//private Label dbConfigLabel;

	private Button defaultCheck;

	private Label resultTest;

/**
	private SelectionListener selistener = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
//			if (e.getSource().equals(checkTemplate)) {
//				if (checkTemplate.getSelection()) {
//					_tTemplatesDirText.setEnabled(true);
//					_tTemplatesDirText.setText("");
//					openTemplate.setEnabled(true);
//
//				} else {
//					_tTemplatesDirText.setText("Default");
//					_tTemplatesDirText.setEnabled(false);
//					openTemplate.setEnabled(false);
//				}
//			} else if (e.getSource().equals(classDirPickerButton)) {
			if (e.getSource().equals(classDirPickerButton)) {
				DirectoryDialog directoryDialog = new DirectoryDialog(shell);
				directoryDialog.setText("Check class directory picker");
				directoryDialog.setFilterPath("");
				String nomFichier = directoryDialog.open();
				if ((nomFichier != null) && (nomFichier.length() != 0)) {
					checkClassDirText.setText(nomFichier);
				}
//			} else if (e.getSource().equals(openDbConfig)) {
//				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
//				dialog.setFilterExtensions(new String[] { "*.xml" });
//				String nomFichier = dialog.open();
//				if ((nomFichier != null) && (nomFichier.length() != 0)) {
//					dbConfigText.setText(nomFichier);
//				}
//			} else if (e.getSource().equals(openTemplate)) {
//				String nomFichier;
//				DirectoryDialog directoryDialog = new DirectoryDialog(shell);
//				directoryDialog.setText("Template directory picker");
//				directoryDialog.setFilterPath("");
//				nomFichier = directoryDialog.open();
//				if ((nomFichier != null) && (nomFichier.length() != 0)) {
//					_tTemplatesDirText.setText(nomFichier);
//				}
			} else if (e.getSource().equals(defaultCheck)) {
				if (defaultCheck.getSelection()) {
					setSpecificInitCheckClass(false);
				}
			} else if (e.getSource().equals(specificCheck)) {
				if (specificCheck.getSelection()) {
					setSpecificInitCheckClass(true);
				}
			} else if (e.getSource().equals(testClassButton)) {
				String sClassToLoad = checkClassText.getText();
				String[] paths = new String[1];
				paths[0] = checkClassDirText.getText();

				//--- Provider creation for specific class with specific path
//				InitializerCheckerProvider provider;
//				try {
//					provider = new InitializerCheckerProvider(sClassToLoad,
//							paths);
//				} catch (Exception ex) {
//					MessageDialog.openError(null, null,
//							"ERROR : cannot create provider  \n"
//									+ "exception = " + ex.getMessage() + " - "
//									+ e.toString() + " \n");
//					provider = null;
//				} catch (Throwable t) {
//					MessageDialog.openError(null, null,
//							"ERROR : cannot create provider  \n" + "throwable"
//									+ t.getMessage() + " - " + t.toString()
//									+ " \n");
//					provider = null;
//				}
//				if (provider != null) {
//					InitializerChecker tool = null;
//					try {
//						tool = provider.getInitializerChecker();
//					} catch (Throwable t) {
//						tool = null;
//					}
//					if (tool == null) {
//						MessageDialog.openError(null, null,
//								"ERROR : Cannot load the specific class ! \n"
//										+ "Error Code : "
//										+ provider.getErrorCode() + " \n"
//										+ "Error Message : "
//										+ provider.getErrorMsg() + " \n");
//						resultTest.setText("Load class : ERROR");
//					} else {
//						String sMsg = "OK : Specific class '" + sClassToLoad
//								+ "' loaded. \n";
//						sMsg += "Plugin class : " + tool.getClass().getName()
//								+ "\n";
//						sMsg += "About this specific class : \n";
//						sMsg += tool.about();
//						MessageDialog.openInformation(null, null, sMsg);
//						resultTest.setText("Load class : OK");
//					}
//				}
			}
		}
	};
**/
	//private Shell shell;

	//private Text sourceDirText;

	private Button specificCheck;

	//private Text _tTemplatesDirText;

	private Button testClassButton;

//	private Text testPackageText;
//
//	private Text voListPackageText;
//
//	private Text voPackageText;
	
	//private PropertiesManager _propManager = null ;

	/**
	 *  
	 */
	public PropertiesPage() {
		super();
		//MsgBox.info("PropertiesPage constructor " );
		// NB : do not use getElement here ( no yet set ) 
	}

	private void log(String s) {
		PluginLogger.log(s);
	}
	
	private void createCheckDirPicker(Composite group, final Composite cevent) {
		classDirPickerButton = new Button(group, SWT.PUSH);
		classDirPickerButton.setText("...");
		//classDirPickerButton.addSelectionListener(selistener);
	}

	private void createCheckGroup(Composite composite) {
		checkGroup = new Group(composite, SWT.NONE);
		checkGroup.setLayoutData(getColSpan(5));
		checkGroup.setText("Init / Check");
		checkGroup.setLayout(new GridLayout(3, false));

		defaultCheck = new Button(checkGroup, SWT.RADIO);
		defaultCheck.setText("Default");
		defaultCheck.setLayoutData(getColSpan(3));
		specificCheck = new Button(checkGroup, SWT.RADIO);
		specificCheck.setText("Specific");
		specificCheck.setLayoutData(getColSpan(3));
		checkClassLabel = new Label(checkGroup, SWT.NONE);
		checkClassLabel.setText("Check class");
		checkClassText = new Text(checkGroup, SWT.BORDER);
		checkClassText.setLayoutData(getColSpan(2));
		checkClassDirLabel = new Label(checkGroup, SWT.NONE);
		checkClassDirLabel.setText("Directory");
		checkClassDirText = new Text(checkGroup, SWT.BORDER);
		checkClassDirText.setLayoutData(getColSpan(1));

		createCheckDirPicker(checkGroup, composite);
		testClassButton = new Button(checkGroup, SWT.PUSH);
		testClassButton.setText("Test class loading");
		resultTest = new Label(checkGroup, SWT.BORDER);
		resultTest.setLayoutData(getColSpan(2));

//		defaultCheck.addSelectionListener(selistener);
//		checkTemplate.addSelectionListener(selistener);
//		specificCheck.addSelectionListener(selistener);
//		testClassButton.addSelectionListener(selistener);
	}

	//------------------------------------------------------------------------------------------
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = null ;
		try {
			composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new FillLayout());
	
			TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
			
			createTabGeneral(tabFolder);
			createTabPackages(tabFolder); 
			// createTabClassesNames(tabFolder); // TODO : remove method ??
			createTabFolders(tabFolder); 
			createTabVariables(tabFolder);
			createTabAdvanced(tabFolder);
			createTabAboutPlugin(tabFolder);
	
			//shell = parent.getShell();
			//--- Init screen fields values
			initFields();
		} 
		catch ( Throwable t )
		{
			MsgBox.error("Error in createContents() : " + t.toString());	
			t.printStackTrace();
		}
		return composite;
	}
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "General" TabItem
	 * @param tabFolder
	 */
	private void createTabGeneral(TabFolder tabFolder) 
	{
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" General ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(3, false));
		tabItem.setControl(tabContent);
		
		//-------------------------------------------------------------------------------
		
		_tProjectName = createTextField(tabContent, "Project name : ") ;
		_tProjectName.setEnabled(false);	

		_tWorkspaceLocation = createTextField(tabContent, "Workspace location :") ;
		_tWorkspaceLocation.setEnabled(false);		

		_tProjectLocation = createTextField(tabContent, "Project location :") ;
		_tProjectLocation.setEnabled(false);		

		_tPluginConfigFile = createTextField(tabContent, "Project config file : ") ;
		_tPluginConfigFile.setEnabled(false);	
		
		//-------------------------------------------------------------------------------
		_tRepositoriesFolder = createTextField(tabContent, "Repositories folder :") ;

		_tTemplatesFolder = createTextField(tabContent, "Templates folder :") ;
		
		//-------------------------------------------------------------------------------
		
//		_tSourceFolder = createTextField(tabContent, "Source folder :") ;
//
//		_tWebContentFolder = createTextField(tabContent, "Web content folder :") ;

//		_tTelosysProp = createTextField(tabContent, "Telosys properties file :") ;
		
		//-------------------------------------------------------------------------------
	}	
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "Packages" TabItem
	 * @param tabFolder
	 */
	private void createTabPackages(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Packages ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(3, false));
		tabItem.setControl(tabContent);

		_tBeanPackage = createTextField(tabContent, "Bean class package") ;
//		_tVOListPackage = createTextField(tabContent, "VO List package") ; 
//		_tDaoPackage = createTextField(tabContent, "DAO package") ;
//		_tXmlMapperPackage = createTextField(tabContent, "XML mapper package") ;
		
		createTwoLabels(tabContent, "", "" ); // Separator

//		_tScreenDataPackage = createTextField(tabContent, "Screen Data package") ;
//		_tScreenManagerPackage = createTextField(tabContent, "Screen Manager package") ;
//		_tScreenTriggersPackage = createTextField(tabContent, "Screen Triggers package") ;
//		_tScreenProceduresPackage = createTextField(tabContent, "Screen Procedures package") ;
	}
	
	//------------------------------------------------------------------------------------------
	private Text _tSrcFolder = null ;
	private Text _tResFolder = null ;
	private Text _tWebFolder = null ;
	private Text _tTestSrcFolder = null ;
	private Text _tTestResFolder = null ;
	
	/**
	 * Creates the "Folders" TabItem
	 * @param tabFolder
	 */
	private void createTabFolders(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Folders ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(3, false));
		tabItem.setControl(tabContent);

		createSingleLabel(tabContent, "Define here the project folders variables ");
		_tSrcFolder = createTextField(tabContent, "Sources",      "${SRC}") ;
		_tResFolder = createTextField(tabContent, "Resources ",   "${RES}") ;
		_tWebFolder = createTextField(tabContent, "Web content ", "${WEB}" ) ;

		_tTestSrcFolder = createTextField(tabContent, "Tests sources  ",  "${TEST_SRC}") ;
		_tTestResFolder = createTextField(tabContent, "Tests resources ", "${TEST_RES}") ;
		
		createTabFoldersButtons(tabContent);
		
		createOneLabel(tabContent, "" ); 
		createOneLabel(tabContent, "Project source folders : " ); 

		IProject project = this.getCurrentProject();
		String[] srcFolders = EclipseProjUtil.getSrcFolders(project);
		for ( String srcFolder : srcFolders ) {
			createOneLabel(tabContent, " . " + srcFolder );
		}

	}
	
    
//	//------------------------------------------------------------------------------------------
//	/**
//	 * Creates the "Classes names" TabItem
//	 * @param tabFolder
//	 */
//	private void createTabClassesNames(TabFolder tabFolder) {
//		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
//		tabItem.setText("Classes names");
//		
//		Composite tabContent = new Composite(tabFolder, SWT.NONE);
//		tabContent.setLayout(new GridLayout(3, false));
//		tabItem.setControl(tabContent);
//
////		String sSyntax = "Syntax : Prefix${" + ConfigDefaults.BEANNAME + "}Suffix " ;
////		
////		_tVOListClassName = createTextField(tabContent, "VOList class ") ;
////		createTwoLabels(tabContent, "", 
////				sSyntax + " ( default : '" + ConfigDefaults.DEFAULT_LIST_CLASS_NAME + "' )");
////
////		_tDAOClassName = createTextField(tabContent, "DAO class ") ;
////		createTwoLabels(tabContent, "", 
////				sSyntax + " ( default : '" + ConfigDefaults.DEFAULT_DAO_CLASS_NAME + "' )");
////
////		_tXmlMapperClassName = createTextField(tabContent, "XML mapper class ") ;
////		createTwoLabels(tabContent, "", 
////				sSyntax + " ( default : '" + ConfigDefaults.DEFAULT_XML_MAPPER_CLASS_NAME + "' )");
//	}
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "Variables" TabItem
	 * @param tabFolder
	 */
	private void createTabVariables(TabFolder tabFolder) 
	{
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Variables ");
		
		/*
		 +----------------+---------+
		 ! Label (SPAN 2)           !
		 +----------------+---------+
		 ! Table          ! Buttons !
		 +----------------+---------+		 
		 */
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout( new GridLayout(2, false));
		tabItem.setControl(tabContent);
		
		//--- Label ( Col 1 and 2 in the GRID )
		Label label= new Label(tabContent, SWT.LEFT | SWT.WRAP );
		label.setFont(tabContent.getFont());
		label.setText("Define here the project variables usable in templates :");
		GridData gd = new GridData();
		//gd.heightHint = 300 ;
		//gd.horizontalAlignment = GridData.FILL;
		gd.horizontalAlignment = SWT.BEGINNING; 
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		//--- Table ( Col 1 in the GRID )
		_variablesTable = new VariablesTable(tabContent);
		GridData gdTable = new GridData();
		//gd.heightHint = 300 ;
		gdTable.heightHint = 300 ; // prefered height (in pixels)
		gdTable.widthHint = 460 ; // prefered width (in pixels)
		//gd.horizontalAlignment = GridData.FILL;
		gdTable.horizontalAlignment = SWT.BEGINNING; 
		gdTable.grabExcessHorizontalSpace = false;
		//gdTable.horizontalSpan = 2;
		_variablesTable.setLayoutData(gdTable);
		
		//--- Buttons ( Col 2 in the GRID )
		createTableButtons(tabContent, _variablesTable) ;
		
		/*
		Text t = null ; 
		t = createTextField(tabContent, "Name :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getName() );
		*/
	}	
	
	private Composite createTableButtons(Composite composite, VariablesTable table ) 
	{		
		RowLayout panelLayout = new RowLayout(); // Vertical row ( = column )
		panelLayout.type = SWT.VERTICAL ;
		panelLayout.fill = true ; // same width for all controls
		panelLayout.pack = false ; // no pack for all controls size
		
		//--- Panel for buttons
		Composite panelBouton = new Composite(composite, SWT.NONE);
		panelBouton.setLayout( panelLayout );
		
//		Color color = new Color( panelBouton.getDisplay(), 0xFF, 0, 0 );
//		panelBouton.setBackground(color);

		Button button = null;
		
		//--- "Add" button 
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add an attribute at the end of the list");	
		button.addSelectionListener(table.getAddSelectionAdapter() ); 
		
		//--- "Insert" button 
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Insert");
		button.setToolTipText("Insert an attribute");	
		button.addSelectionListener(table.getInsertSelectionAdapter() ); 
		
		//--- "Delete" button 
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Delete");
		button.setToolTipText("Delete the selected attribute");	
		button.addSelectionListener(table.getDeleteSelectionAdapter() ); 
		
		return panelBouton ;
	}
	
	//------------------------------------------------------------------------------------------
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "Advanced" TabItem
	 * @param tabFolder
	 */
	private void createTabAdvanced(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Advanced ");

		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(5, false));
		tabItem.setControl(tabContent);

		//createTemplateDirectory(tabContent);
		createCheckGroup(tabContent);
	}
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "About plugin" TabItem
	 * @param tabFolder
	 */
	private void createTabAboutPlugin(TabFolder tabFolder) 
	{
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" About plugin ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(3, false));
		tabItem.setControl(tabContent);
		
		//IResource resource = getElementAsResource();
		
		//createSingleLabel(tabContent, "Telosys Tools Plugin :");

		Text t = null ; 
		t = createTextField(tabContent, "Name :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getName() );
		
		t = createTextField(tabContent, "Version :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getVersion() );
		
		t = createTextField(tabContent, "Id :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getId() );
		
		t = createTextField(tabContent, "Directory URL :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getBaseURLAsString() );
		
		t = createTextField(tabContent, "Directory :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getDirectory() );
		
		t = createTextField(tabContent, "Templates dir :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getTemplatesDirectory() );
		
		t = createTextField(tabContent, "Generator version :") ;
		t.setEnabled(false);
		t.setText( GeneratorVersion.GENERATOR_VERSION );
	}	
	
	//------------------------------------------------------------------------------------------
	private void createTabFoldersButtons(Composite composite ){
		//--- Creates the Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText("");
		Label label2 = new Label(composite, SWT.NONE);
		label2.setText("");
		
		//--- Creates the buttons
		Composite buttons = new Composite(composite, SWT.NONE);
		buttons.setLayout(new FillLayout());

		Button mavenButton = new Button(buttons, SWT.PUSH);
		mavenButton.setText("Maven folders");
		mavenButton.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	_tSrcFolder.setText("src/main/java");
            	_tResFolder.setText("src/main/resources");
            	_tWebFolder.setText("src/main/webapp");
            	_tTestSrcFolder.setText("src/test/java");
            	_tTestResFolder.setText("src/test/resources");
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);

		Button projectButton = new Button(buttons, SWT.PUSH);
		projectButton.setText("Project folders");
		projectButton.setData(this);
		projectButton.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent event)
            {
            	//Object source = event.getSource();
            	//MsgBox.info("source : " + source.getClass().getCanonicalName() );
            	Button b = (Button) event.getSource() ;
            	PropertiesPage page = (PropertiesPage) b.getData() ;
            	_tSrcFolder.setText( getProjectSourceFolder(page) );
            	_tResFolder.setText("");
            	_tWebFolder.setText( getProjectWebContentFolder(page) );
            	_tTestSrcFolder.setText("");
            	_tTestResFolder.setText("");
            }
            public void widgetDefaultSelected(SelectionEvent event)
            {
            }
        }
		);
		
	}
	//------------------------------------------------------------------------------------------
	/**
	 * Try to determine the project source folder  
	 * @param page
	 * @return
	 */
	private String getProjectSourceFolder(PropertiesPage page) {
		IProject project = page.getCurrentProject();
		String[] srcFolders = EclipseProjUtil.getSrcFolders(project);
		String projectSourceFolder = null ;
		if ( srcFolders.length == 1 ) {
			projectSourceFolder = srcFolders[0] ;
		}
		else if ( srcFolders.length > 1 ) {
    		for ( String srcFolder : srcFolders ) {
    			if ( "src".equals(srcFolder) ) {
    				projectSourceFolder = "src" ;
    				break;
    			}
    		}
    		if ( null == projectSourceFolder ) {
    			projectSourceFolder = srcFolders[0] ; // the first one 
    		}
		}
		if ( null == projectSourceFolder ) { // still undefined 
			projectSourceFolder = "src" ;
		}
		return projectSourceFolder ;
	}
	
	private String getProjectWebContentFolder(PropertiesPage page) {
		IProject project = page.getCurrentProject();
		IFolder folder = project.getFolder(WEB_CONTENT);
		if ( folder.exists() ) {
			// Exists 
			return WEB_CONTENT ;
		}
//		IResource res = EclipseProjUtil.getResource(project, "/"+WEB_CONTENT);
//		if ( res != null ) {
//			// Exists 
//			return WEB_CONTENT ;
//		}
		return "" ;
	}	
	//------------------------------------------------------------------------------------------
	private Text createTextField(Composite composite, String sLabel) {
		//--- Creates the Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText(sLabel);
		//--- Creates the Text field 
		Text textField = new Text(composite, SWT.BORDER);
		textField.setLayoutData(getColSpan(2));
		return textField;
	}

	//------------------------------------------------------------------------------------------
	private Text createTextField(Composite composite, String label1, String label2) {
		//--- Creates the Label 1
		Label label = new Label(composite, SWT.NONE);
		label.setText(label1);
		//--- Creates the Label 2 
		label = new Label(composite, SWT.NONE);
		label.setText(label2);
		//--- Creates the Text field 
		Text textField = new Text(composite, SWT.BORDER);
		textField.setLayoutData(getColSpan(1));
		return textField;
	}

	//------------------------------------------------------------------------------------------
	private Label createSingleLabel(Composite composite, String sLabel) {
		//--- Creates the Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText(sLabel);
		label.setLayoutData(getColSpan(3));
		return label;
	}

	//------------------------------------------------------------------------------------------
	private void createOneLabel(Composite composite, String labelText) {
		//--- Creates the Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(getColSpan(3));
	}
	//------------------------------------------------------------------------------------------
	private void createTwoLabels(Composite composite, String sLabel1, String sLabel2) {
		//--- Creates the 1st Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText(sLabel1);
		//--- Creates the 2nd Label 
		label = new Label(composite, SWT.NONE);
		label.setText(sLabel2);
		label.setLayoutData(getColSpan(2));
	}

	//------------------------------------------------------------------------------------------
	private GridData getColSpan(int n) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = n;
		return gd;
	}

	//------------------------------------------------------------------------------------------
	private void initFields()
	{
		//checkCurrentProject();

		//ProjectConfig projectConfig = ProjectConfigManager.getCurrentProjectConfig() ;
		IProject project = getCurrentProject();
		ProjectConfig projectConfig = ProjectConfigManager.loadProjectConfig(project) ;

		configToFields( projectConfig );
	}
	
	//------------------------------------------------------------------------------------------
//	private IResource getElementAsResource()
//	{
//		IAdaptable adaptable = getElement() ;
//		if ( adaptable == null )
//		{
//			MsgBox.error("Cannot get IAdaptable element !" );
//			return null ;
//		}
//		else
//		{
//			if ( adaptable instanceof IResource )
//			{
//				return (IResource)adaptable;
//			}
//			else
//			{
//				MsgBox.error("Adaptable element is not an instance of IResource !" );
//				return null ;
//			}
//		}
//	}

	//------------------------------------------------------------------------------------------
	private IProject getCurrentProject()
	{
		IAdaptable adapt = getElement() ;
		if ( adapt == null )
		{
			MsgBox.error("Cannot get Adaptable element " );
			return null ;
		}		
		return (IProject)adapt;		
	}
	
	//------------------------------------------------------------------------------------------
//	private boolean checkCurrentProject()
//	{
//		//--- Set the current project 
//		IProject project = getCurrentProject();
//		if ( project != null )
//		{
//			ProjectConfigManager.setCurrentProject(project);
//			return true ;
//		}
//		return false ;
//	}
	
	//------------------------------------------------------------------------------------------
	/**
	 *  
	 */
//	private Properties loadProperties() {
////		if ( checkInitialization() )
////		{
////			return _propManager.load();
////		}
////		return null ;
//		if ( checkCurrentProject() )
//		{
//			return ProjectConfigManager.loadCurrentProjectProperties();
//		}
//		return null;
//	}
	
	//------------------------------------------------------------------------------------------
	private void saveProperties(Properties props) 
	{
//		if ( checkCurrentProject() )
//		{
//			ProjectConfigManager.saveCurrentProjectProperties(getCurrentProject(), props);
//		}
		log("saveProperties(Properties props)...");
		ProjectConfigManager.saveProjectConfig(getCurrentProject(), props);
	}
	
	//------------------------------------------------------------------------------------------
	/*
	 * overwritten method for "Apply" button
	 */
	protected void performApply() 
	{
		try {
			Properties props = new Properties();
			fieldsToProperties(props);
			
			//-- Save the Telosys-Tools configuration for the current project
			saveProperties(props);		
			
			//-- Check the "telosys.properties" is up to date
			// REMOVED TEMPORARLY 
			// TODO : keep it or remove it ?
			//checkTelosysProperties(props);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void checkTelosysProperties( Properties props ) 
	{
    	log("checkTelosysProperties(props)... " );
    	
//		String sTelosysPropFile    = props.getProperty(PropName.TELOSYS_PROP_FILE );
//		String sXmlMapperPackage   = props.getProperty(PropName.PACKAGE_XML_MAPPER );
//		String sXmlMapperClassName = props.getProperty(PropName.CLASS_NAME_XML_MAPPER );
//		if ( StrUtil.nullOrVoid( sXmlMapperClassName ) )
//		{
//			sXmlMapperClassName = ConfigDefaults.DEFAULT_XML_MAPPER_CLASS_NAME ;
//		}
		
		String sXmlMapperPackage   = null ; // [22-Jan-2012] Removing classes names
		String sXmlMapperClassName = null ; // [22-Jan-2012] Removing classes names
		
    	log("mapper package & class : '" +  sXmlMapperPackage + "' + '" + sXmlMapperClassName + "'");
    	
		if ( sXmlMapperPackage != null && sXmlMapperClassName != null )
		{
			//--- Mapper class defined in the plugin config
			String sPluginConfigMapperClass = sXmlMapperPackage + "." + sXmlMapperClassName ;

//			//--- Telosys properties value
//			IProject project = getCurrentProject();
//	    	File file = EclipseProjUtil.getResourceAsFile(project, sTelosysPropFile);
//	    	if ( file == null )
//	    	{
//				MsgBox.warning("The file '" + sTelosysPropFile + "' \n"
//						+ "doesn't exist in the project" );
//				return ;
//	    	}
//	    	else
//	    	{
//		    	log("EclipseProjUtil.getResourceAsFile(..,..) : file = " 
//		    			+ file.getAbsolutePath() );
//	    	}
//	    	log("Telosys properties : file = " + file );
	    	 
//			PropertiesManager pm = new PropertiesManager(file) ;
//	    	log("Load Telosys properties ... " );
//			Properties telosysProperties = pm.load();
//			if ( telosysProperties != null )
//			{
//		    	log("Telosys properties loaded : size " + telosysProperties.size() );
//			}
//			else
//			{
//		    	log("Telosys properties not loaded ( p = null )" );
//			}

//			//--- Mapper class defined in the "telosys.properties" project file
//			String sTelosysConfigMapperClass = telosysProperties.getProperty( TelosysPropName.MAPPER_CLASS );
//	    	log("Telosys properties : " + TelosysPropName.MAPPER_CLASS + " = '" + sTelosysConfigMapperClass +"'" );
//			
//			//--- If the telosys config is different from the plugin config propose to update the telosys properties
//			if ( ! sTelosysConfigMapperClass.equals(sPluginConfigMapperClass) ) // Different => update Telosys properties
//			{
//		    	log("checkTelosysProperties(props) : NEW value " );
//				boolean b = MsgBox.confirm(
//						  "The project configuration is different \n"
//						+ "from the 'telosys.properties' file.\n"
//						+ "\n"
//						+ "Do you want to update the 'telosys.properties' file ?");
//				
//				if ( b )
//				{
//					String sFullFileName = file.getAbsolutePath();
//			    	log("checkTelosysProperties(props) : update file " + sFullFileName ); 
//					PropUtil.update(sFullFileName, TelosysPropName.MAPPER_CLASS, sPluginConfigMapperClass); 
//					
//					//--- Refresh the Workspace resource
//					EclipseProjUtil.refreshResource(project, sTelosysPropFile);
//				}
//			}
//			else
//			{
//		    	log("checkTelosysProperties(props) : SAME value" );
//			}
		}
	}
	
	//------------------------------------------------------------------------------------------
	/*
	 * overwritten method for "Restore" button
	 */
	protected void performDefaults() {
//		Properties props = new Properties();
//		new DefaultProperties()
//				.setHashtableDefaults(props, this.currentProject);
//		initPageFields(props);
	}

//	private void setSpecificInitCheckClass(boolean bFlag) {
//
//		//--- Specific class (or not specific)
//		checkClassLabel.setEnabled(bFlag);
//		checkClassText.setEnabled(bFlag);
//		checkClassDirLabel.setEnabled(bFlag);
//		checkClassDirText.setEnabled(bFlag);
//		testClassButton.setEnabled(bFlag);
//		testClassButton.setEnabled(bFlag);
//		classDirPickerButton.setEnabled(bFlag);
//	}

	/**
	 * @param p
	 */
	private void configToFields( ProjectConfig projectConfig  ) 
	{
		log("propertiesToFields ...");

		//--- Tab "General"
		_tProjectName.setText( projectConfig.getProjectName() );
		_tProjectLocation.setText(projectConfig.getProjectFolder() );		
		_tWorkspaceLocation.setText(projectConfig.getWorkspaceFolder() );
		
		//_tPluginConfigFile.setText( ProjectConfigManager.getCurrentProjectConfigFileName() );
		_tPluginConfigFile.setText( projectConfig.getPluginConfigFile() );
		
//		_tSourceFolder.setText( projectConfig.getSourceFolder() );
//		_tWebContentFolder.setText ( projectConfig.getWebContentFolder() );
//		_tTelosysProp.setText( projectConfig.getTelosysPropFile() );
		_tTemplatesFolder.setText( projectConfig.getTemplatesFolder() );
		_tRepositoriesFolder.setText( projectConfig.getRepositoriesFolder() );
		
		//--- Tab "Packages"
		_tBeanPackage.setText( projectConfig.getPackageForVOBean() );
		
		//--- Tab "Folders" ( considered as pre-defined variables )
		_tSrcFolder.setText( projectConfig.getSRC() ) ;
		_tResFolder.setText( projectConfig.getRES() ) ;
		_tWebFolder.setText( projectConfig.getWEB() ) ;
		_tTestSrcFolder.setText( projectConfig.getTEST_SRC() ) ;
		_tTestResFolder.setText( projectConfig.getTEST_RES() ) ;

		
		//--- Tab "Variables"
		//VariableItem[] items = projectConfig.getProjectVariables();
		Variable[] items = projectConfig.getProjectVariables();
		if ( items != null )
		{
			_variablesTable.initItems(items);
		}
		
		
		/*
		if (((String) p.get(PropName.SPECIFIC_TEMPLATES)).equals("1")) {
			checkTemplate.setSelection(true);
			templateDirText.setText((String) p.get(PropName.TEMPLATE_DIRECTORY));
		} else {
			checkTemplate.setSelection(false);
			openTemplate.setEnabled(false);
			templateDirText.setEnabled(false);
			templateDirText.setText("Default");
		}

		String sSpecificInitCheck = (String) p.get(PropName.SPECIFIC_INIT_CHECK);
		if (sSpecificInitCheck.equals("1")) {
			//--- Specific initializer / Checker
			defaultCheck.setSelection(false);
			specificCheck.setSelection(true);
			checkClassText
					.setText((String) p.get(PropName.INIT_CHECK_CLASS_NAME));
			checkClassDirText.setText((String) p
					.get(PropName.INIT_CHECK_CLASS_DIR));
			//--- Enable all specific fields
			setSpecificInitCheckClass(true);
		} else {
			//--- Default initializer / Checker
			defaultCheck.setSelection(true);
			specificCheck.setSelection(false);
			checkClassText.setText("");
			checkClassDirText.setText("");
			//--- Disable all specific fields
			setSpecificInitCheckClass(false);
		}
*/
	}
	
	/**
	 * Populates the given properties with screen fields values
	 * @param props
	 */
	private void fieldsToProperties(Properties props) 
	{
		log("fieldsToProperties ...");
		
		//--- Tab "General"
		props.put(GeneratorConfigConst.TEMPLATES_FOLDER,  _tTemplatesFolder.getText() );
		props.put(PropName.REPOS_FOLDER,                  _tRepositoriesFolder.getText() );
				
		//--- Tab "Packages"
		props.put(GeneratorConfigConst.PACKAGE_VO,        _tBeanPackage.getText());
		
		//--- Tab "Folders" ( considered as pre-defined variables )
		props.put(ContextName.SRC,       _tSrcFolder.getText() );
		props.put(ContextName.RES,       _tResFolder.getText() );
		props.put(ContextName.WEB,       _tWebFolder.getText() );
		props.put(ContextName.TEST_SRC,  _tTestSrcFolder.getText() );
		props.put(ContextName.TEST_RES,  _tTestResFolder.getText() );

		//--- Tab "Variables"		
		log("propertiesToFields : variables ...");

		Object[] items = _variablesTable.getItems();
		Variable[] variables = new Variable[items.length];
		for ( int i = 0 ; i < items.length ; i++ )
		{
			if ( items[i] instanceof Variable )
			{
				variables[i] = (Variable) items[i] ;
			}
			else
			{
				MsgBox.error("Item [" + i + "] is not an instance of VariableItem" );
				return;
			}
		}
		
		//--- Check 
		String[] invalidNames = VariableNames.getInvalidVariableNames(variables);
		if ( invalidNames != null )
		{
			//--- Invalid names
			StringBuffer sb = new StringBuffer();
			for ( int i = 0 ; i < invalidNames.length ; i++ )
			{
				if ( i > 0 ) sb.append(", ");
				sb.append("'"+invalidNames[i]+"'");
			}
			MsgBox.error("Invalid variable name(s) : " + sb.toString() 
					+ "\n Name(s) reserved for standard variables."
					+ "\n The current variables will not be saved !");
		}
		else
		{
			log("propertiesToFields : all variables names OK => put in properties");
			//--- All names OK
			VariablesUtil.putVariablesInProperties( variables, props );
		}

		//--- Tab "Advanced"
//		props.put(PropName.TEMPLATES_DIRECTORY, _tTemplatesDirText.getText() );
		/*
		if (checkTemplate.getSelection()) {
			//--- Specific templates
			props.put(PropName.SPECIFIC_TEMPLATES,  "1");
			props.put(PropName.TEMPLATE_DIRECTORY, templateDirText.getText());
		} else {
			//--- Default templates
			props.put(PropName.SPECIFIC_TEMPLATES, "0");
			props
					.put(PropName.TEMPLATE_DIRECTORY, Plugin
							.getTemplatesDirectory());
		}

		//--- "SPECIFIC" Radio Button
		if (specificCheck.getSelection()) {
			//--- "SPECIFIC" Radio Button SELECTED
			props.put(PropName.SPECIFIC_INIT_CHECK, "1");
			props.put(PropName.INIT_CHECK_CLASS_NAME, checkClassText.getText());
			props.put(PropName.INIT_CHECK_CLASS_DIR, checkClassDirText.getText());
		} else {
			props.put(PropName.SPECIFIC_INIT_CHECK, "0");
			props.put(PropName.INIT_CHECK_CLASS_NAME, "");
			props.put(PropName.INIT_CHECK_CLASS_DIR, "");
		}
*/
		log("fieldsToProperties : END ");
	}
}