package org.telosys.tools.eclipse.plugin.config.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.github.GitHubClient;
import org.telosys.tools.commons.http.HttpUtil;
import org.telosys.tools.commons.variables.Variable;
import org.telosys.tools.eclipse.plugin.MyPlugin;
import org.telosys.tools.eclipse.plugin.PluginBuildInfo;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.commons.PluginResources;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
import org.telosys.tools.eclipse.plugin.settings.SettingsManager;
import org.telosys.tools.generator.GeneratorVersion;
import org.telosys.tools.generator.context.names.ContextNames;

/**
 * Project properties configuration page ( one configuration file for each project )
 * This page contains 5 tabs
 * 
 */
public class PropertiesPage extends PropertyPage {
	
	private final static boolean DEBUG_MODE = false ;

	private final static String WEB_CONTENT     = "WebContent" ;
	private final static String DATABASES_DBCFG = "databases.dbcfg" ;
	private final static String DEFAULT_GITHUB_USER_NAME = "telosys-tools" ;

    //--- Tab "General"
	private Text _tProjectName = null ;
	private Text _tProjectLocation = null;
	private Text _tWorkspaceLocation = null ;
	
	private Text _tTemplatesFolder = null ;
	private Text _tRepositoriesFolder = null ;
	private Text _tDownloadsFolder = null ;
	private Text _tLibrariesFolder = null ;
	
    //--- Tab "Packages"
	private Text _tEntityPackage = null ;
	private Text _tRootPackage = null ; // v 2.0.6
	
	//--- Tab "Variables"
	private VariablesTable _variablesTable = null ;
	
	//--- Tab "Download"
	private Text    _tGitHubUserName = null;
	//private Text    _tGitHubUrlPattern  = null;
	private List    _listGitHubRepositories = null ;
	private Button  _checkBoxUnzipDownload = null ;
	private Text    _tLogger = null ;
	
    //--- Tab "Info"
	private Text _tPluginConfigFile = null ;
	
    //--------------------------------------

	private Label checkClassDirLabel;

	private Text checkClassDirText;

	private Label checkClassLabel;

	private Text checkClassText;

	private Group checkGroup;

	private Button classDirPickerButton;

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
	private Button specificCheck;

	private Button testClassButton;

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
			createTabDownload(tabFolder);
			createTabAdvanced(tabFolder);
			createTabAboutPlugin(tabFolder);
			
			//--- This tab is for DEBUG only 
			if ( DEBUG_MODE ) {
				createTabDebug(tabFolder) ;
			}
			
			//--- Init screen fields values
			//initFields();
			ProjectConfig projectConfig = loadProjectConfig();
			configToFields( projectConfig );

		} 
		catch ( Exception e )
		{
			MsgBox.error("Error in createContents().", e);	
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
		_tProjectName.setEditable(false);	

		_tWorkspaceLocation = createTextField(tabContent, "Workspace location :") ;
		_tWorkspaceLocation.setEditable(false);		

		_tProjectLocation = createTextField(tabContent, "Project location :") ;
		_tProjectLocation.setEditable(false);		

		_tPluginConfigFile = createTextField(tabContent, "Project config file : ") ;
		_tPluginConfigFile.setEditable(false);	
		
		//-------------------------------------------------------------------------------
		_tRepositoriesFolder = createTextField(tabContent, "Models folder :") ;

		_tTemplatesFolder = createTextField(tabContent, "Templates folder :") ;
		
		_tDownloadsFolder = createTextField(tabContent, "Downloads folder :") ;
		
		_tLibrariesFolder = createTextField(tabContent, "Libraries folder :") ;
		
		//-------------------------------------------------------------------------------
		
		createTabGeneralButton(tabContent);
		
		//-------------------------------------------------------------------------------
	}	
	//------------------------------------------------------------------------------------------
	private void createTabGeneralButton(Composite composite ){
		//--- Creates the void Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText("");
		
		//--- Creates the Button 
		Button initButton = new Button(composite, SWT.PUSH);
		initButton.setText("Init Telosys Tools");
		initButton.setToolTipText(" Creates the Telosys Tools folders \n"
				+ " and the databases configuration file \n"
				+ " if they don't exist");
		initButton.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	initTelosysToolsEnv();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);
	}
	//------------------------------------------------------------------------------------------
	private void initTelosysToolsEnv(){
		IProject project = getCurrentProject();
		StringBuffer sb = new StringBuffer();
		sb.append("Telosys Tools environment initialized.\n\n");
		createFolder(project, _tRepositoriesFolder, sb ) ;
		createFolder(project, _tTemplatesFolder, sb ) ;
		createFolder(project, _tDownloadsFolder, sb ) ;
		createFolder(project, _tLibrariesFolder, sb ) ;
		initDatabasesConfigFile(project, _tTemplatesFolder.getText(), sb);
		MsgBox.info(sb.toString());
	}
	//------------------------------------------------------------------------------------------
	private void createFolder(IProject project, Text folderText, StringBuffer sb){
		String folderName = folderText.getText() ;
		if ( ! StrUtil.nullOrVoid(folderName) )  {
			folderName = folderName.trim() ;
			if ( EclipseProjUtil.folderExists(project, folderName) ) {
				sb.append(". folder '" + folderName + "' exists (not created)");
			}
			else {
				boolean created = EclipseProjUtil.createFolder(project, folderName ) ;	
				if ( created ) {
					sb.append(". folder '" + folderName + "' created");
				}
				else {
					sb.append(". folder '" + folderName + "' not created (ERROR)");
				}
			}
		}
		sb.append("\n");
	}	
	//------------------------------------------------------------------------------------------
	private void initDatabasesConfigFile(IProject project, String sTemplatesFolder, StringBuffer sb){
		
		//--- File provided with the plugin distribution
		//String pluginResourcesFolder = MyPlugin.getResourcesDirectory();
		//String  fullFileName = FileUtil.buildFilePath(pluginResourcesFolder, DATABASES_DBCFG );
		
		//--- Destination file (in the project)
		if ( StrUtil.nullOrVoid(sTemplatesFolder) ) {
			MsgBox.error("Templates folder is void !");
			return ;
		}
		StringBuffer sbDestination = new StringBuffer();
		String s = sTemplatesFolder.trim();
		String[] parts = StrUtil.split(s, '/');
		for ( int i = 0 ; i < ( parts.length - 1 ) ; i++ ) {
			if ( i > 0 ) {
				sbDestination.append("/");
			}
			sbDestination.append(parts[i]);
		}
		sbDestination.append("/");
		sbDestination.append(DATABASES_DBCFG);		
		String destinationInProject = sbDestination.toString() ;
		
		//--- Destination file (in the filesystem)
		String destinationAbsolutePath = EclipseProjUtil.getAbsolutePathInFileSystem(project, destinationInProject);
		//MsgBox.info("Destination absolute path : \n" + destinationAbsolutePath );
		File file = new File(destinationAbsolutePath) ;
		if ( file.exists() ) {
			//--- Already exists : no change
			sb.append(". file '" + destinationInProject + "' exists (not copied)");
		}
		else {
			//--- Doesn't exist yet : Initialize by copy ( from plugin folder to project folder )
			URL databaseConfigURL = PluginResources.getResourceURL(DATABASES_DBCFG);
			if ( databaseConfigURL != null ) {
				try {
					FileUtil.copy(databaseConfigURL, destinationAbsolutePath, false);
					EclipseProjUtil.refreshResource(project, destinationInProject);
					sb.append(". file '" + destinationInProject + "' copied");
				} catch (Exception e) {
					MsgBox.error("Cannot copy '" + DATABASES_DBCFG + "' file. \n\n"
							+ "Source (URL) : \n" + databaseConfigURL.toString() + "\n\n" 
							+ "Destination : \n" + destinationAbsolutePath + "\n" 
						 ) ;
					sb.append(". ERROR : cannot copy file '" + destinationInProject + "' ");
				}
			}
		}
		sb.append("\n");
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

		//_tEntityPackage = createTextField(tabContent, "Entity classes package ") ;
		createSingleLabel(tabContent, "Define here the project packages variables (usable in targets and templates)");
		_tRootPackage   = createTextField(tabContent, "Root package ",           "${ROOT_PKG}") ;
		_tEntityPackage = createTextField(tabContent, "Entity classes package ", "${ENTITY_PKG}") ;
		
		createOneLabel(tabContent, "" ); 
		createOneLabel(tabContent, "If you need more packages define them in the \"Variables\" " );

	}
	
	//------------------------------------------------------------------------------------------
	private Text _tSrcFolder = null ;
	private Text _tResFolder = null ;
	private Text _tWebFolder = null ;
	private Text _tTestSrcFolder = null ;
	private Text _tTestResFolder = null ;
	private Text _tDocFolder = null ;
	private Text _tTmpFolder = null ;
	
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

		createSingleLabel(tabContent, "Define here the project folders variables (usable in targets and templates)");
		_tSrcFolder = createTextField(tabContent, "Sources",      "${SRC}") ;
		_tResFolder = createTextField(tabContent, "Resources ",   "${RES}") ;
		_tWebFolder = createTextField(tabContent, "Web content ", "${WEB}" ) ;

		_tTestSrcFolder = createTextField(tabContent, "Tests sources  ",  "${TEST_SRC}") ;
		_tTestResFolder = createTextField(tabContent, "Tests resources ", "${TEST_RES}") ;
		
		_tDocFolder = createTextField(tabContent, "Documentation",   "${DOC}" ) ;
		_tTmpFolder = createTextField(tabContent, "Temporary files", "${TMP}" ) ;
		
		createTabFoldersButtons(tabContent);
		
		createOneLabel(tabContent, "" ); 
		createOneLabel(tabContent, "If you need more folders define them in the \"Variables\" " );
		 
//		createOneLabel(tabContent, "Project source folders : " ); 
//
//		IProject project = this.getCurrentProject();
//		String[] srcFolders = EclipseProjUtil.getSrcFolders(project);
//		for ( String srcFolder : srcFolders ) {
//			createOneLabel(tabContent, " . " + srcFolder );
//		}

	}
	
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
		label.setText("Define here the project variables (usable in targets and templates)");
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
		
		Button button = new Button(tabContent, SWT.PUSH);
		button.setText("Show reserved variable names");
		button.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	String[] reserverdNames = ContextNames.getSortedReservedNames() ;
            	StringBuffer sb = new StringBuffer();
            	sb.append("The following names are reserved : \n\n") ;
            	for ( String name : reserverdNames ) {
            		sb.append(name);
            		sb.append(" \n");
            	}
            	MsgBox.info(sb.toString());
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);

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
	/**
	 * Creates the "Packages" TabItem
	 * @param tabFolder
	 */
	private void createTabDownload(TabFolder tabFolder) {
		
		final int Col2With = 400 ;
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Download ");
		
		//--- GRID with 2 columns
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(2, false));
		tabItem.setControl(tabContent);
		//------------------------------------------------------------------------------------
		//--- Label  ( SPAN 2 )
		Label label = new Label(tabContent, SWT.NONE);
		label.setText("Download bundles from GitHub (templates and resources)");
		label.setLayoutData(getColSpan(2));
		
		//------------------------------------------------------------------------------------
		//--- SEPARATOR  ( SPAN 2 )
		label = new Label(tabContent, SWT.NONE);
		label.setText("");
		label.setLayoutData(getColSpan(2));

		//------------------------------------------------------------------------------------
		//--- Label + Text field 
		label = new Label(tabContent, SWT.NONE);
		label.setText("GitHub user name : ");
		_tGitHubUserName = new Text(tabContent, SWT.BORDER);
		GridData gd = getCellGridData2();
		gd.widthHint   = Col2With ;
		_tGitHubUserName.setLayoutData(gd);
		_tGitHubUserName.setText(DEFAULT_GITHUB_USER_NAME);
		
		//------------------------------------------------------------------------------------
		//--- Void Label + Button  
		label = new Label(tabContent, SWT.NONE);
		label.setText("");
		
		Button b = new Button(tabContent, SWT.PUSH);
		b.setText("Get available bundles");
		b.setToolTipText(" Get available bundles \n from GitHub site ");
		b.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	populateGitHubRepoList();
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);

		//------------------------------------------------------------------------------------
		//--- Label + List of Repositories 
		label = new Label(tabContent, SWT.NONE);
		label.setText("GitHub bundles : ");	
		label.setLayoutData(getCellGridData1());
		
//		_tableGitHubRepositories = createGitHubRepositiriesTable(tabContent, 400);
//		GridData gd = getCellGridData2();
//		//gd.minimumHeight = 200 ;
//		gd.heightHint = 200 ;
//		gd.widthHint  = 400 ;
//		_tableGitHubRepositories.setLayoutData(gd);
//		_tableGitHubRepositories.setSize(400, 200);
//		//createTwoLabels(tabContent, "", "" ); // Separator
		_listGitHubRepositories = new List(tabContent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		gd = getCellGridData2();
		gd.heightHint  = 120 ;
		gd.widthHint   = Col2With - 10 ;
		_listGitHubRepositories.setLayoutData(gd);
		
		//------------------------------------------------------------------------------------
// Removed in v 2.1.1
//		//--- Label + Text field 
//		label = new Label(tabContent, SWT.NONE);
//		label.setText("GitHub URL pattern : ");
//		
//		_tGitHubUrlPattern = new Text(tabContent, SWT.BORDER);
//		gd = getCellGridData2();
//		gd.widthHint   = Col2With ;
//		_tGitHubUrlPattern.setLayoutData(gd);
//		_tGitHubUrlPattern.setText("https://github.com/${USER}/${REPO}/archive/master.zip");
		
		//------------------------------------------------------------------------------------
		//--- Void Label + Composite [ Button + CheckBox ]  
		label = new Label(tabContent, SWT.NONE);
		label.setText("");
		label.setLayoutData(getCellGridData1());
		
		Composite composite1 = new Composite(tabContent, SWT.NONE);
		GridLayout gdComposite = new GridLayout(2, false);
		gdComposite.marginLeft = 0 ;
		gdComposite.horizontalSpacing = 80 ;
		gdComposite.marginWidth = 0 ;
		composite1.setLayout(gdComposite);

			b = new Button(composite1, SWT.PUSH);
			b.setText("Download selected bundles(s)");
			b.setToolTipText(" Download selected bundle(s) \n from GitHub site ");
			b.addSelectionListener(new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	if ( _listGitHubRepositories.getSelectionCount() > 0 ) {
	            		boolean bUnzip = _checkBoxUnzipDownload.getSelection() ;
	        			String[] selectedRepo = _listGitHubRepositories.getSelection();
	        			downloadSelectedFiles(selectedRepo, bUnzip);
	            	}
	            	else {
	            		MsgBox.warning("Select at least one file");
	            	}
	            }
	            public void widgetDefaultSelected(SelectionEvent arg0)
	            {
	            }
	        }
			);
	
			_checkBoxUnzipDownload = new Button(composite1, SWT.CHECK);
			_checkBoxUnzipDownload.setText("Install downloaded bundle(s)");
			_checkBoxUnzipDownload.setSelection(true);
		
		
		//------------------------------------------------------------------------------------
		//--- Label + Text field 
		label = new Label(tabContent, SWT.NONE);
		label.setText("Log : ");
		label.setLayoutData(getCellGridData1());
		
		//Text _tLogger = new Text (tabContent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL );
		_tLogger = new Text (tabContent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		gd = getCellGridData2();
		gd.widthHint   = Col2With - 10 ;
		gd.heightHint  = 80 ;
		_tLogger.setLayoutData(gd);
		_tLogger.setEditable(false);


		//--- Label  ( SPAN 2 )
		label = new Label(tabContent, SWT.NONE);
		label.setText("If you experience download problems, check Eclipse proxy setting");
		label.setLayoutData(getColSpan(2));
//		label = new Label(tabContent, SWT.NONE);
//		label.setText("");
//		label = new Label(tabContent, SWT.NONE);
//		label.setText("");
	}
	//------------------------------------------------------------------------------------------
	private GridData getCellGridData1() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.LEFT ;
		gd.verticalAlignment = SWT.TOP ;
		return gd;
	}
	private GridData getCellGridData2() {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.LEFT ;
		gd.verticalAlignment = SWT.TOP ;
		return gd;
	}
	//------------------------------------------------------------------------------------------
	private void populateGitHubRepoList() {
		
		String sGitHubUserName = getGitHubUserName();
		if ( sGitHubUserName != null ) {
			//--- Create the task
			PopulateListTaskWithProgress task = new PopulateListTaskWithProgress( 
					getTelosysToolsCfgFromFields(),
					sGitHubUserName, 
					_listGitHubRepositories );
			
			//--- Run the task with monitor
			ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog( Util.getActiveWindowShell() ) ;
			try {
				progressMonitorDialog.run(false, false, task);
			} catch (InvocationTargetException e) {
				MsgBox.error("Error during task", e.getCause() );
			} catch (InterruptedException e) {
				MsgBox.info("Task interrupted");
			}
			
		}
	}
	//------------------------------------------------------------------------------------------
	private long downloadSelectedFiles(String[] repoNames, boolean bUnzip) {

		String sDownloadFolder = getDownloadFolder();
		if ( null == sDownloadFolder ) {
			return 0 ;
		}
//		String sGitHubUrlPattern = getGitHubUrlPattern() ;
//		if ( null == sGitHubUrlPattern ) {
//			return 0 ;
//		}
		if ( null == repoNames ) {
			MsgBox.error("Selection is null !");
			return 0 ;
		}
		String sTemplatesFolder = getTemplatesFolder();
		if ( null == sTemplatesFolder ) {
			return 0 ;
		}
	
		//--- Run the generation task via the progress monitor 
		DownloadTaskWithProgress task = null ;
		try {
			task = new DownloadTaskWithProgress(//this.getCurrentProject(), 
					getTelosysToolsCfgFromFields(),
					getGitHubUserName(), 
					repoNames, 
//					sDownloadFolder, 
//					sGitHubUrlPattern, 
					bUnzip, // Unzip or not the downloaded file
//					sTemplatesFolder, // ie "TelosysTools/templates"
					_tLogger );
		} catch (TelosysPluginException e) {
    		MsgBox.error("Cannot create DownloadTaskWithProgress instance", e);
    		return 0 ;
		}

		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog( Util.getActiveWindowShell() ) ;
		try {
			progressMonitorDialog.run(false, false, task);
		} catch (InvocationTargetException e) {
			MsgBox.error("Error during download", e.getCause() );
		} catch (InterruptedException e) {
			MsgBox.info("Download interrupted");
		}
		
		return task.getResult();
	}
	
// REMOVED in ver 2.1.1
//	private String getGitHubUrlPattern() {
//		String sPattern = _tGitHubUrlPattern.getText();
//		// ${USER}/${REPO}
//		if ( sPattern.indexOf("${USER}") < 0 ) {
//			MsgBox.warning("Invalid GitHub URL pattern, '${USER}' expected");
//			return null ;
//		}
//		if ( sPattern.indexOf("${REPO}") < 0 ) {
//			MsgBox.warning("Invalid GitHub URL pattern, '${REPO}' expected");
//			return null ;
//		}
//		return sPattern ;
//	}
	
	private String getDownloadFolder() {
		String sFolder = _tDownloadsFolder.getText().trim();
		if ( sFolder.length() == 0  ) {
			MsgBox.warning("Download folder is not defined");
			return null ;
		}
		if ( EclipseProjUtil.folderExists(getCurrentProject(), sFolder) ) {
			return sFolder ;
		}
		else {
			MsgBox.warning("Download folder '" + sFolder + "' does not exist !");
			return null ;
		}
	}
	
	private String getTemplatesFolder() {
		String sFolder = _tTemplatesFolder.getText().trim();
		if ( sFolder.length() == 0  ) {
			MsgBox.warning("Templates folder is not defined");
			return null ;
		}
		if ( EclipseProjUtil.folderExists(getCurrentProject(), sFolder) ) {
			return sFolder ;
		}
		else {
			MsgBox.warning("Templates folder '" + sFolder + "' does not exist !");
			return null ;
		}
	}
	
	private String getGitHubUserName() {
		String user = _tGitHubUserName.getText().trim();
		if ( user.length() == 0  ) {
			MsgBox.warning("GitHub user name is void");
			return "" ;
		}
		return user ;
	}
	
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
		
		Text t = null ; 
		t = createTextField(tabContent, "Name :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getName() );
		
		t = createTextField(tabContent, "Version :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getVersion() + " - " + PluginBuildInfo.BUILD_ID + "  ( " + PluginBuildInfo.BUILD_DATE + " ) ");
		
		t = createTextField(tabContent, "Id :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getId() );
		
		t = createTextField(tabContent, "Directory URL :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getBaseURLAsString() );
		
		t = createTextField(tabContent, "Directory :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getDirectory() );
		
		t = createTextField(tabContent, "Resources dir :") ;
		t.setEditable(false);
		t.setText( MyPlugin.getResourcesDirectory() );
		
		t = createTextField(tabContent, "Generator version :") ;
		t.setEditable(false);
		t.setText( GeneratorVersion.GENERATOR_VERSION );

		t = createTextField(tabContent, "GitHub URL pattern :") ;
		t.setEditable(false);
		t.setText( GitHubClient.GIT_HUB_REPO_URL_PATTERN );
		
		t = createTextArea(tabContent, "Http proxy config :") ;
		t.setEditable(false);
		t.setText( HttpUtil.getSystemProxyPropertiesAsString("-----") );
	}	
	
	//------------------------------------------------------------------------------------------
	/**
	 * Creates the "About plugin" TabItem
	 * @param tabFolder
	 */
	private void createTabDebug(TabFolder tabFolder) 
	{
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(" Debug ");
		
		Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayout(new GridLayout(2, false));
		tabItem.setControl(tabContent);
		
		//-------------------------------------------------- ROW ------------------------------------
		//--- Creates the Label 
		Label label = new Label(tabContent, SWT.NONE);
		label.setText("Settings test 1 : ");

		new Label(tabContent, SWT.NONE).setText("xxx");
		
		//-------------------------------------------------- ROW ------------------------------------
		//--- Creates the Button 
		Button button = new Button(tabContent, SWT.PUSH);
		button.setText("isBundleStaticResourcesCopied");
//		button.setToolTipText(" Creates the Telosys Tools folders \n"
//				+ " and the databases configuration file \n"
//				+ " if they don't exist");
		button.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	
            	SettingsManager settingsManager = new SettingsManager( getCurrentProject() ) ;
            	boolean r = settingsManager.readBundleStaticResourcesCopiedFlag("fakeBundle");
            	MsgBox.info("Result = " + r );
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);
		
		button = new Button(tabContent, SWT.PUSH);
		button.setText("setBundleStaticResourcesCopied");
//		button.setToolTipText(" Creates the Telosys Tools folders \n"
//				+ " and the databases configuration file \n"
//				+ " if they don't exist");
		button.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	
            	SettingsManager settingsManager = new SettingsManager( getCurrentProject() ) ;
            	settingsManager.updateBundleStaticResourcesCopiedFlag("fakeBundle", true);
            	MsgBox.info("Done (set to TRUE)");
            }
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }
        }
		);
		
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
		projectButton.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent event)
            {
            	//Object source = event.getSource();
            	//MsgBox.info("source : " + source.getClass().getCanonicalName() );
            	String projectSourceFolder = getProjectSourceFolder(); 
            	_tSrcFolder.setText( projectSourceFolder );
            	_tResFolder.setText("");
            	_tWebFolder.setText( getProjectWebContentFolder() );
            	_tTestSrcFolder.setText( projectSourceFolder );
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
	private String getProjectSourceFolder() {
		IProject project = getCurrentProject();
		if ( EclipseProjUtil.isJavaProject(project) != true ) {
			return "" ;
		}
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
	
	private String getProjectWebContentFolder() {
		IProject project = getCurrentProject();
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
	private Text createTextArea(Composite composite, String sLabel) {
		//--- Creates the Label 
		Label label = new Label(composite, SWT.NONE);
		label.setText(sLabel);
		//--- Creates the Text area 
		Text textArea = new Text (composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.heightHint     = 100 ;
		textArea.setLayoutData(gd);
		return textArea;
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
//	private void createTwoLabels(Composite composite, String sLabel1, String sLabel2) {
//		//--- Creates the 1st Label 
//		Label label = new Label(composite, SWT.NONE);
//		label.setText(sLabel1);
//		//--- Creates the 2nd Label 
//		label = new Label(composite, SWT.NONE);
//		label.setText(sLabel2);
//		label.setLayoutData(getColSpan(2));
//	}

	//------------------------------------------------------------------------------------------
	private GridData getColSpan(int n) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = n;
		return gd;
	}

//	//------------------------------------------------------------------------------------------
//	private void initFields()
//	{
//		IProject project = getCurrentProject();
//		ProjectConfig projectConfig = ProjectConfigManager.loadProjectConfig(project) ;
//
//		configToFields( projectConfig );
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
	private ProjectConfig loadProjectConfig() 
	{
		log("loadProjectConfig()...");
		ProjectConfig projectConfig = ProjectConfigManager.loadProjectConfig( getCurrentProject() ) ;
		return projectConfig ;
	}
	//------------------------------------------------------------------------------------------
	private void saveProjectConfig(ProjectConfig projectConfig ) 
	{
		log("saveProjectConfig()...");
		ProjectConfigManager.saveProjectConfig(getCurrentProject(), projectConfig);
	}
//	//------------------------------------------------------------------------------------------
//	private void saveProperties(Properties props) 
//	{
//		log("saveProperties(Properties props)...");
//		ProjectConfigManager.saveProjectConfig(getCurrentProject(), props);
//	}
	
	//------------------------------------------------------------------------------------------
	/*
	 * overwritten method for "Apply" button
	 */
	protected void performApply() 
	{
		try {
			ProjectConfig projectConfig = new ProjectConfig(getCurrentProject());
			fieldsToConfig( projectConfig );
			
			//-- Save the Telosys-Tools configuration for the current project
			saveProjectConfig( projectConfig ); 
			
		} catch ( Exception e ) {
			MsgBox.error("Cannot save properties.", e );
		}
	}

	//------------------------------------------------------------------------------------------
	/*
	 * overwritten method for "Restore Defaults" button
	 */
	protected void performDefaults() {
	}

	//------------------------------------------------------------------------------------------
	/**
	 * @param projectConfig
	 */
	private void configToFields( ProjectConfig projectConfig  ) 
	{
		log("propertiesToFields ...");

		//--- Tab "General"
		_tProjectName.setText( projectConfig.getProjectName() );
		_tProjectLocation.setText(projectConfig.getProjectFolder() );		
		_tWorkspaceLocation.setText(projectConfig.getWorkspaceFolder() );
		
		TelosysToolsCfg telosysToolsCfg = projectConfig.getTelosysToolsCfg();

		_tPluginConfigFile.setText( telosysToolsCfg.getCfgFileAbsolutePath() );
		
		_tRepositoriesFolder.setText( telosysToolsCfg.getRepositoriesFolder() );
		_tTemplatesFolder.setText( telosysToolsCfg.getTemplatesFolder() );
		_tDownloadsFolder.setText( telosysToolsCfg.getDownloadsFolder() );
		_tLibrariesFolder.setText( telosysToolsCfg.getLibrariesFolder() );
		
		//--- Tab "Packages"
		//_tEntityPackage.setText( projectConfig.getPackageForJavaBeans() );
		_tEntityPackage.setText( telosysToolsCfg.getEntityPackage() ); // v 2.0.6
		_tRootPackage.setText( telosysToolsCfg.getRootPackage() ); // v 2.0.6
		
		//--- Tab "Folders" ( considered as pre-defined variables )
		_tSrcFolder.setText( telosysToolsCfg.getSRC() ) ;
		_tResFolder.setText( telosysToolsCfg.getRES() ) ;
		_tWebFolder.setText( telosysToolsCfg.getWEB() ) ;
		_tTestSrcFolder.setText( telosysToolsCfg.getTEST_SRC() ) ;
		_tTestResFolder.setText( telosysToolsCfg.getTEST_RES() ) ;
		_tDocFolder.setText( telosysToolsCfg.getDOC() ) ;
		_tTmpFolder.setText( telosysToolsCfg.getTMP() ) ;

		
		//--- Tab "Variables"
		Variable[] items = telosysToolsCfg.getSpecificVariables();
		if ( items != null )
		{
			_variablesTable.initItems(items);
		}
	}
	
	//------------------------------------------------------------------------------------------
	/**
	 * Populates the given properties with screen fields values
	 * @param props
	 */
	private void fieldsToConfig( ProjectConfig projectConfig ) {
		log("fieldsToConfig ...");
		TelosysToolsCfg telosysToolsCfg = projectConfig.getTelosysToolsCfg();
		
		//--- Tab "General"
		telosysToolsCfg.setRepositoriesFolder ( _tRepositoriesFolder.getText() ) ;
		telosysToolsCfg.setTemplatesFolder    ( _tTemplatesFolder.getText() ) ;
		telosysToolsCfg.setDownloadsFolder    ( _tDownloadsFolder.getText() ) ;
		telosysToolsCfg.setLibrariesFolder    ( _tLibrariesFolder.getText() ) ;

		//--- Tab "Packages"
		telosysToolsCfg.setRootPackage   (_tRootPackage.getText()   );
		telosysToolsCfg.setEntityPackage (_tEntityPackage.getText() );

		//--- Tab "Folders" ( considered as pre-defined variables )
		telosysToolsCfg.setSRC     ( _tSrcFolder.getText()     );
		telosysToolsCfg.setRES     ( _tResFolder.getText()     );
		telosysToolsCfg.setWEB     ( _tWebFolder.getText()     );
		telosysToolsCfg.setTEST_SRC( _tTestSrcFolder.getText() );
		telosysToolsCfg.setTEST_RES( _tTestResFolder.getText() );
		telosysToolsCfg.setDOC     ( _tDocFolder.getText()     );
		telosysToolsCfg.setTMP     ( _tTmpFolder.getText()     );
		
		//--- Tab "Variables"		
		log("fieldsToConfig : variables ...");
		Variable[] variables = getVariablesFromView();
		if ( checkVariablesNames(variables) ) {
			telosysToolsCfg.setSpecificVariables(variables);
		}
		log("fieldsToConfig : END");
	}
	//------------------------------------------------------------------------------------------
	private TelosysToolsCfg getTelosysToolsCfgFromFields() {
		ProjectConfig projectConfig = new ProjectConfig(getCurrentProject());
		fieldsToConfig( projectConfig );
		return projectConfig.getTelosysToolsCfg();
	}	
	//------------------------------------------------------------------------------------------
	private Variable[] getVariablesFromView() {
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
				return new Variable[0] ;
			}
		}
		return variables ;
	}
	//------------------------------------------------------------------------------------------
	private boolean checkVariablesNames(Variable[] variables) {
		//-- are there invalid names ? 
		String[] invalidNames = ContextNames.getInvalidVariableNames(variables);
		if ( invalidNames != null )
		{
			//--- Invalid names found => display all the invalid names
			StringBuffer sb = new StringBuffer();
			for ( int i = 0 ; i < invalidNames.length ; i++ )
			{
				if ( i > 0 ) sb.append(", ");
				sb.append("'"+invalidNames[i]+"'");
			}
			MsgBox.error("Invalid variable name(s) : " + sb.toString() 
					+ "\n Name(s) reserved for standard variables."
					+ "\n The current variables will not be saved !");
			return false ;
		}
		return true ;
	}
	
}