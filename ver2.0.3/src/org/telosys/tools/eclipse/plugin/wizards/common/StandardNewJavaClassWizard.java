package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaClass;

public abstract class StandardNewJavaClassWizard extends BasicNewResourceWizard implements INewWizard
{
	// The project can be stored as an instance variable 
	// This wizard object is created each time the wizard is used
	private IProject _project = null ;
	
	//----------------------------------------------------------------------------------------
	/**
	 * STEP 1 : Creation 
	 * Constructor of the Wizard Page<br>
	 * Called each time the wizard is used 
	 */
	public StandardNewJavaClassWizard(String sTitle) {
		super();
		PluginLogger.log(this, "constructor...");
		//MsgBox.info( getClass().getName() + " : constructor() ");
		//setDefaultPageImageDescriptor(WizardImages.getImageDescriptor(WizardImages.TELOSYS_LOGO));
		setWindowTitle(sTitle);

		// Do not use "getSelection()" here ( not yet set ) 
	}

	//----------------------------------------------------------------------------------------
	/* 
	 * STEP 2 : initialization
	 * Called each time the wizard is used 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		super.init(workbench, selection);
		// SUPER-CLASS CODE :		
		// init(IWorkbench workbench, IStructuredSelection currentSelection) {		
		//		this.workbench = workbench;
		//		this.selection = currentSelection;		
		//		initializeDefaultPageImageDescriptor();
		//	}
		
		PluginLogger.log(this, "init()...");


		IProject project = WizardSelectionUtil.getResourceProject( getSelection() );
		if ( project != null )
		{
			PluginLogger.log(this, "init() : set current project " + project );
			
			//ProjectConfigManager.setCurrentProject(project) ;
			_project = project ;
		}
		else
		{
			MsgBox.error( " ResourceProject is null ! ( cannot get it )");
			_project = null ;
		}
	}

	//----------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * Overrides the superclass method to force a new "default image"
	 * @see org.eclipse.ui.wizards.newresource.BasicNewResourceWizard#initializeDefaultPageImageDescriptor()
	 */
	protected void initializeDefaultPageImageDescriptor() 
	{		
		PluginLogger.log(this, "initializeDefaultPageImageDescriptor()...");
		setDefaultPageImageDescriptor(PluginImages.getImageDescriptor(PluginImages.TELOSYS_LOGO));
	}

//	private String getTemplateFullPath(String sTemplateDirectory, String sTemplateFileName) throws GeneratorException 
//	{
//		log( "getTemplateFullPath(..,..)...");
//		log( " . Template directory : " + sTemplateDirectory );
//		log( " . Template file      : " + sTemplateFileName );
//
//		if (sTemplateDirectory == null) {
//			throw new GeneratorException("Template directory is null !");
//		}
//		if (sTemplateFileName == null) {
//			throw new GeneratorException("Template file name is null !");
//		}
//		File dir = new File(sTemplateDirectory);
//		if (!dir.exists()) {
//			throw new GeneratorException("Template directory '"
//					+ sTemplateDirectory + "' doesn't exist !");
//		}
//		if (!dir.isDirectory()) {
//			throw new GeneratorException("Template directory '"
//					+ sTemplateDirectory + "' is not a directory !");
//		}
//
//		String sTemplateFullPath = null;
//		if (sTemplateDirectory.endsWith("/")) {
//			sTemplateFullPath = sTemplateDirectory + sTemplateFileName;
//		} else {
//			sTemplateFullPath = sTemplateDirectory + "/" + sTemplateFileName;
//		}
//		File file = new File(sTemplateFullPath);
//		if (!file.exists()) {
//			throw new GeneratorException("Template file '" + sTemplateFullPath
//					+ "' doesn't exist !");
//		}
//		if (!file.isFile()) {
//			throw new GeneratorException("Template file '" + sTemplateFullPath
//					+ "' is not a file !");
//		}
//		return sTemplateFullPath ;
//	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns a Velocity generator for the given template file, with an initialized context 
	 * @param sTemplateFile
	 * @param sPackage
	 * @param sClassName
	 * @return
	 * @throws GeneratorException
	 */
	protected Generator getJavaClassGenerator( String sTemplateFile, String sPackage, String sClassName ) throws GeneratorException 
	{
		return getJavaClassGenerator( sTemplateFile, sPackage, sClassName, null );
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns a Velocity generator for the given template file, with an initialized context 
	 * @param sTemplateFile
	 * @param sPackage
	 * @param sClassName
	 * @param sSuperClass
	 * @return
	 * @throws GeneratorException
	 */
	protected Generator getJavaClassGenerator( String sTemplateFile, String sPackage, String sClassName, String sSuperClass ) throws GeneratorException 
	{				
		log( getClass().getName() + " : getJavaClassGenerator()...");
		log( " . Template file    : " + sTemplateFile );
		log( " . Java Package     : " + sPackage );
		log( " . Java class       : " + sClassName );
		log( " . Java super class : " + sSuperClass );
		
		TelosysToolsLogger logger = new ConsoleLogger();
		
		//--- Java Class to be generated
		JavaClass javaClass = null ;
		if (sSuperClass != null) 
		{
			//--- With super class
			javaClass = new JavaClass(sClassName, sPackage, sSuperClass);
		}
		else
		{
			//--- No super class
			javaClass = new JavaClass(sClassName, sPackage);
		}

		//--- Get a new generator instance
		Generator generator = WizardGeneratorProvider.getGenerator( _project, sTemplateFile, logger ) ;
		
		//--- Populate the context with the Java class to generate 
		generator.setJavaClassTargetInContext(javaClass);

		return generator ;
	}
	
	protected void log(String sMsg) 
	{
		PluginLogger.log( this.getClass().getName() + " : " + sMsg);
	}
}
