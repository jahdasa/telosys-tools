package org.telosys.tools.eclipse.plugin.wizards.service;

import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;

/**
 * Wizard class to create a new TelosysService
 */
public class NewServiceWizard extends StandardNewJavaClassWizard  {
	
	private final static String TITLE = "New Telosys Service" ;

	private NewServiceWizardPage _wizardPage = null;

	//----------------------------------------------------------------------------------------
	/**
	 * Constructor of the Wizard class
	 */
	public NewServiceWizard() {
		super(TITLE);
	}

	//----------------------------------------------------------------------------------------
	/* Method called by Eclipse to add the 1..N pages of the wizard 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		log("addPages()...");
		// MsgBox.info( getClass().getName() + " : addPages() ");
		//super.addPages();
		_wizardPage = new NewServiceWizardPage(selection);
		addPage(this._wizardPage);
	}

	//----------------------------------------------------------------------------------------
	/* Method called when the user click on the "Finish" button
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		log( "performFinish()...");
		try {
			String sTemplateFile = _wizardPage.getServiceTemplate();
			//MsgBox.info("Template : '" + sTemplateFile + "'");			
			if ( sTemplateFile == null )
			{
				MsgBox.error("Template file is null ! " );
				return false ;
			}
			log("Template file : " + sTemplateFile  );
		
			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();
			String sSuperClass = _wizardPage.getSearchSuperClass();

			Generator generator = getJavaClassGenerator( sTemplateFile, sPackage, sClassName, sSuperClass );
			
			//--- Source directory
			String sSourceDir = _wizardPage.getPackageFragmentRootText();

			log("Run generation : " + sSourceDir + ", " + sPackage );
			final IFile file = WizardTools.generateJavaClass(sSourceDir, sPackage,
					sClassName, generator);
						
			if (file != null) {
				// Selects and reveals the newly added resource
				// selectAndReveal(file); // Fired a "NullPointerException" !!!

				// Open the generated file
				log("Open the generated file..." );
				WizardTools.openFileEditor(file, getShell());
			}
		} catch (GeneratorException e) {
			MsgBox.error("GeneratorException : " + e.toString() );
			e.printStackTrace();
		} catch (RuntimeException e) {
			MsgBox.error("RuntimeException : " + e.toString() );
			e.printStackTrace();
		} catch (Throwable e) {
			MsgBox.error("Throwable : " + e.toString());
			e.printStackTrace();
		}

		return true;
	}
}
