package org.telosys.tools.eclipse.plugin.config.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.commons.VariablesManager;
import org.telosys.tools.commons.VariablesUtil;
import org.telosys.tools.eclipse.plugin.MyPlugin;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.commons.Util;
import org.telosys.tools.eclipse.plugin.commons.github.GitHubAPI;
import org.telosys.tools.eclipse.plugin.commons.github.GitHubRepository;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;
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

	private final static String WEB_CONTENT     = "WebContent" ;
	private final static String DATABASES_DBCFG = "databases.dbcfg" ;

    //private final static String PLUGIN_PROPERTIES_FILE = "telosys-tools.cfg";
    
    //--- Tab "General"
	private Text _tProjectName = null ;
	private Text _tProjectLocation = null;
	private Text _tWorkspaceLocation = null ;
	
//	private Text _tSourceFolder = null ;
//	private Text _tWebContentFolder = null ;
	private Text _tTemplatesFolder = null ;
	private Text _tRepositoriesFolder = null ;
	private Text _tDownloadsFolder = null ;
	
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
	
	//--- Tab "Download"
	private Text  _tGitHubUserName = null;
	private Text  _tGitHubUrlPattern  = null;
	//private Table _tableGitHubRepositories = null ;
	private List  _listGitHubRepositories = null ;
	private Text  _tLogger = null ;
	
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
			createTabDownload(tabFolder);
			createTabAdvanced(tabFolder);
			createTabAboutPlugin(tabFolder);
	
			//shell = parent.getShell();
			//--- Init screen fields values
			initFields();
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
		_tProjectName.setEnabled(false);	

		_tWorkspaceLocation = createTextField(tabContent, "Workspace location :") ;
		_tWorkspaceLocation.setEnabled(false);		

		_tProjectLocation = createTextField(tabContent, "Project location :") ;
		_tProjectLocation.setEnabled(false);		

		_tPluginConfigFile = createTextField(tabContent, "Project config file : ") ;
		_tPluginConfigFile.setEnabled(false);	
		
		//-------------------------------------------------------------------------------
		_tRepositoriesFolder = createTextField(tabContent, "Models folder :") ;

		_tTemplatesFolder = createTextField(tabContent, "Templates folder :") ;
		
		_tDownloadsFolder = createTextField(tabContent, "Downloads folder :") ;
		
		//-------------------------------------------------------------------------------
		
		createTabGeneralButton(tabContent);
		
//		_tTelosysProp = createTextField(tabContent, "Telosys properties file :") ;
		
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
		String pluginResourcesFolder = MyPlugin.getResourcesDirectory();
		String  fullFileName = FileUtil.buildFilePath(pluginResourcesFolder, DATABASES_DBCFG );
		
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
			try {
				FileUtil.copy(fullFileName, destinationAbsolutePath);
				EclipseProjUtil.refreshResource(project, destinationInProject);
				sb.append(". file '" + destinationInProject + "' copied");
			} catch (Exception e) {
				MsgBox.error("Cannot copy '" + DATABASES_DBCFG + "' file. \n\n"
						+ "Source : \n" + fullFileName + "\n" 
						+ "Destination : \n" + destinationAbsolutePath + "\n" 
					 ) ;
				sb.append(". ERROR : cannot copy file '" + destinationInProject + "' ");
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

		_tBeanPackage = createTextField(tabContent, "Entity classes package ") ;
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
            	String[] reserverdNames = VariableNames.getSortedReservedNames() ;
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
		label.setText("Download templates and resources from GitHub");
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
		_tGitHubUserName.setText("telosys-tools-community");
		
		//------------------------------------------------------------------------------------
		//--- Void Label + Button  
		label = new Label(tabContent, SWT.NONE);
		label.setText("");
		
		Button b = new Button(tabContent, SWT.PUSH);
		b.setText("Get available files");
		b.setToolTipText(" Get available files \n from GitHub site ");
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
		label.setText("GitHub files : ");	
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
//		for (int i = 1; i <= 5; i++) {
//			_listGitHubRepositories.add("Item Number " + i);
//		}
//		_listGitHubRepositories.add("basic-templates-TT203");
		
		//------------------------------------------------------------------------------------
		//--- Label + Text field 
		label = new Label(tabContent, SWT.NONE);
		label.setText("GitHub URL pattern : ");
		
		_tGitHubUrlPattern = new Text(tabContent, SWT.BORDER);
		gd = getCellGridData2();
		gd.widthHint   = Col2With ;
		_tGitHubUrlPattern.setLayoutData(gd);
		_tGitHubUrlPattern.setText("https://github.com/${USER}/${REPO}/archive/master.zip");
		
		//------------------------------------------------------------------------------------
		//--- Void Label + Button  
		label = new Label(tabContent, SWT.NONE);
		label.setText("");
		label.setLayoutData(getCellGridData1());
		
		b = new Button(tabContent, SWT.PUSH);
		b.setText("Download selected file(s)");
		b.setToolTipText(" Download selected files \n from GitHub site ");
		b.addSelectionListener(new SelectionListener() 
    	{
            public void widgetSelected(SelectionEvent arg0)
            {
            	if ( _listGitHubRepositories.getSelectionCount() > 0 ) {
                	String[] selectedRepo = _listGitHubRepositories.getSelection();
                	downloadSelectedFiles(selectedRepo); 
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

		//------------------------------------------------------------------------------------
		//--- Label + Text field 
		label = new Label(tabContent, SWT.NONE);
		label.setText("Log : ");
		label.setLayoutData(getCellGridData1());
		
		// TODO : TextAREA for logging
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
		Shell shell = Util.cursorWait();
		
		_listGitHubRepositories.removeAll();
//		for (int i = 1; i <= 5; i++) {
//			_listGitHubRepositories.add("Item Number " + i);
//		}
//		_listGitHubRepositories.add("basic-templates-TT203");
		
		String sGitHubUserName = getGitHubUserName();
		if ( sGitHubUserName != null ) {
			java.util.List<GitHubRepository> repositories = GitHubAPI.getRepositories(sGitHubUserName);
			for ( GitHubRepository repo : repositories ) {
				if ( repo.getSize() > 0 ) {
					_listGitHubRepositories.add( repo.getName() );
				}
			}
		}
		Util.cursorArrow(shell);
	}
	//------------------------------------------------------------------------------------------
	private long downloadSelectedFiles(String[] repoNames) {
//		try {
//        	URI uri = new URI(sURI);
//        	String log = HttpProxyUtil.initHttpProxyProperties(uri);
//        	MsgBox.info("LOG HttpProxyUtil.initHttpProxyProperties",log);
//		} catch (URISyntaxException e) {
//			MsgBox.error("Invalid URI ( URISyntaxException ) : \n" + sURI );
//			e.printStackTrace();
//		}

		String sDownloadFolder = getDownloadFolder();
		if ( null == sDownloadFolder ) {
			return 0 ;
		}
		String sGitHubUrlPattern = getGitHubUrlPattern() ;
		if ( null == sGitHubUrlPattern ) {
			return 0 ;
		}
		if ( null == repoNames ) {
			MsgBox.error("Selection is null !");
			return 0 ;
		}
	
		//Shell shell = Util.cursorWait();
		
//		int count = 0 ;
//		if ( repoNames.length > 0 ) {
//			_tLogger.setText("");
//			for ( String repoName : repoNames ) {
//				String sFileURL = buildFileURL(repoName, sGitHubUrlPattern);
//				if ( sFileURL != null ) {
//					String sDestinationFile = buildDestinationFileName(repoName, sDownloadFolder);
//					count++;
//					_tLogger.append("-> Download #" + count + " '" + repoName + "' ... \n");
//					_tLogger.append("  " + sFileURL + "\n");
//					_tLogger.append("  " + sDestinationFile + "\n");
//					long r = 0;
//					try {
//						r = HttpDownloader.download(sFileURL, sDestinationFile);
//						_tLogger.append("  done (" + r + " bytes).\n");
//						File file = new File(sDestinationFile);
//						EclipseWksUtil.refresh(file);
//					}
//					catch (Exception e) {
//						String msg = "Cannot download file \n" 
//							+ sFileURL + "\n\n"
//							+ ( e.getCause() != null ? e.getCause().getMessage() : "") ;
//						MsgBox.error(msg );
//						_tLogger.append("ERROR \n");
//						_tLogger.append(msg);
//					}
//				}
//			}
//		}
//		else {
//			MsgBox.error("Selection is void !");
//		}

		//--- Run the generation task via the progress monitor 
		DownloadTaskWithProgress task = null ;
		try {
			task = new DownloadTaskWithProgress(this.getCurrentProject(), 
					getGitHubUserName(), 
					repoNames, 
					sDownloadFolder, sGitHubUrlPattern, _tLogger );
			//task.run(progressMonitor);
		} catch (TelosysPluginException e) {
    		MsgBox.error("Cannot create DownloadTaskWithProgress instance", e);
    		return 0 ;
		}

		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog( Util.getActiveWindowShell() ) ;
		try {
			progressMonitorDialog.run(false, false, task);
			
			//MsgBox.info("Normal end of generation\n\n" + generationTask.getResult() + " file(s) generated.");
			
		} catch (InvocationTargetException e) {
			MsgBox.error("Error during download", e.getCause() );
		} catch (InterruptedException e) {
			MsgBox.info("Download interrupted");
		}
		
		//Util.cursorArrow(shell);		
		return task.getResult();
	}
	
	private String getGitHubUrlPattern() {
		String sPattern = _tGitHubUrlPattern.getText();
		// ${USER}/${REPO}
		if ( sPattern.indexOf("${USER}") < 0 ) {
			MsgBox.warning("Invalid GitHub URL pattern, '${USER}' expected");
			return null ;
		}
		if ( sPattern.indexOf("${REPO}") < 0 ) {
			MsgBox.warning("Invalid GitHub URL pattern, '${REPO}' expected");
			return null ;
		}
		return sPattern ;
	}
	
	private String getDownloadFolder() {
		String sFolder = _tDownloadsFolder.getText().trim();
		if ( sFolder.length() == 0  ) {
			MsgBox.warning("Download folder is not defined");
			return null ;
		}
		return sFolder ;
	}
	
	private String getGitHubUserName() {
		String user = _tGitHubUserName.getText().trim();
		if ( user.length() == 0  ) {
			MsgBox.warning("GitHub user name is void");
			return null ;
		}
		return user ;
	}
	
	private String buildFileURL(String repoName, String sGitHubURLPattern ) {
//		String user = _tGitHubUserName.getText().trim();
//		if ( user.length() == 0 ) {
//			MsgBox.warning("GitHub user name is void");
//			return null ;
//		}
		String user = getGitHubUserName() ;
		if ( null == user ) return null;
		
		String repo = repoName.trim();
		if ( repo.length() == 0 ) {
			MsgBox.warning("GitHub repository name is void");
			return null ;
		}
		HashMap<String,String> hmVariables = new HashMap<String,String>();
		hmVariables.put("${USER}", user);
		hmVariables.put("${REPO}", repo);
		VariablesManager variablesManager = new VariablesManager(hmVariables);
		String sFileURL = variablesManager.replaceVariables(sGitHubURLPattern);
		// MsgBox.info("File URL : " + sFileURL);
		return sFileURL ;
	}
	private String buildDestinationFileName(String repoName, String sDownloadFolder) {
		// file path in project
		String sFile = repoName + ".zip" ;
		String pathInProject = FileUtil.buildFilePath(sDownloadFolder, sFile);
		// file path in Operating System 
		IProject project = getCurrentProject(); 
		String projectDir = EclipseProjUtil.getProjectDir(project);
		String fullPath = FileUtil.buildFilePath(projectDir, pathInProject);
		return fullPath;
	}
	//------------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------------
	private Table createGitHubRepositiriesTable(Composite composite, int colWidth)
	{
		// Table style
		// SWT.CHECK : check box in the first column of each row
//		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK ;
		
//		int iTableStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
//		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
		int iTableStyle = SWT.BORDER | SWT.V_SCROLL 
						 | SWT.HIDE_SELECTION | SWT.CHECK | SWT.MULTI ;
		
		Table table = new Table(composite, iTableStyle);
		
		
		//table.setSize(380, 200);
		//table.setSize(300, 100);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		//--- Columns
		TableColumn col = null ;
		int iColumnIndex = 0 ;

		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
		//col.setText("GitHub repository name");
		col.setWidth(colWidth);
		
//		col = new TableColumn(table, SWT.LEFT, iColumnIndex++);
//		col.setText("Java Bean");
//		col.setWidth(200);
		
		return table;
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
		
		t = createTextField(tabContent, "Resources dir :") ;
		t.setEnabled(false);
		t.setText( MyPlugin.getResourcesDirectory() );
		
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
		} catch ( Exception e ) {
			MsgBox.error("Cannot save properties.", e );
		}
	}

/***
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
**/
	
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
		_tRepositoriesFolder.setText( projectConfig.getRepositoriesFolder() );
		_tTemplatesFolder.setText( projectConfig.getTemplatesFolder() );
		_tDownloadsFolder.setText( projectConfig.getDownloadsFolder() );
		
		//--- Tab "Packages"
		_tBeanPackage.setText( projectConfig.getPackageForJavaBeans() );
		
		//--- Tab "Folders" ( considered as pre-defined variables )
		_tSrcFolder.setText( projectConfig.getSRC() ) ;
		_tResFolder.setText( projectConfig.getRES() ) ;
		_tWebFolder.setText( projectConfig.getWEB() ) ;
		_tTestSrcFolder.setText( projectConfig.getTEST_SRC() ) ;
		_tTestResFolder.setText( projectConfig.getTEST_RES() ) ;
		_tDocFolder.setText( projectConfig.getDOC() ) ;
		_tTmpFolder.setText( projectConfig.getTMP() ) ;

		
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
		props.put(GeneratorConfigConst.REPOS_FOLDER,      _tRepositoriesFolder.getText() );
		props.put(GeneratorConfigConst.TEMPLATES_FOLDER,  _tTemplatesFolder.getText() );
		props.put(GeneratorConfigConst.DOWNLOADS_FOLDER,  _tDownloadsFolder.getText() );
				
		//--- Tab "Packages"
		props.put(GeneratorConfigConst.ENTITIES_PACKAGE,  _tBeanPackage.getText());
		
		//--- Tab "Folders" ( considered as pre-defined variables )
		props.put(ContextName.SRC,       _tSrcFolder.getText() );
		props.put(ContextName.RES,       _tResFolder.getText() );
		props.put(ContextName.WEB,       _tWebFolder.getText() );
		props.put(ContextName.TEST_SRC,  _tTestSrcFolder.getText() );
		props.put(ContextName.TEST_RES,  _tTestResFolder.getText() );
		props.put(ContextName.DOC,       _tDocFolder.getText() );
		props.put(ContextName.TMP,       _tTmpFolder.getText() );

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