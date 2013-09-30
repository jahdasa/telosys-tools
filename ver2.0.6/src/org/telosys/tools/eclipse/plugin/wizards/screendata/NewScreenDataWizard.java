package org.telosys.tools.eclipse.plugin.wizards.screendata;

import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;

/**
 * Wizard class to create a new Telosys Screen Manager
 */
public class NewScreenDataWizard extends StandardNewJavaClassWizard
{	
	private final static String TITLE = "New Telosys Screen Data" ;

	private NewScreenDataWizardPage _wizardPage = null ;

	//----------------------------------------------------------------------------------------
	/**
	 * Constructor of the Wizard class
	 */
	public NewScreenDataWizard() {
		super(TITLE);
	}

	//----------------------------------------------------------------------------------------
	/* Method called by Eclipse to add the 1..N pages of the wizard 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewScreenDataWizardPage(selection);
		addPage(this._wizardPage);
	}

	//----------------------------------------------------------------------------------------
	/* Method called when the user click on the "Finish" button
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		PluginLogger.log(getClass().getName() + " : performFinish()...");

		try {

			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();

			Generator generator = getJavaClassGenerator( Const.TEMPLATE_WIZARD_SCREEN_DATA, sPackage, sClassName );
				
//			//--- ScreenData Java Class to use
//			String sScreenDataClass = _wizardPage.getScreenDataClassFieldValue();
//			PluginLogger.log("=== ScreenData...  : " + sScreenDataClass );
//			JavaClass screenDataClass = new JavaClass(sScreenDataClass);

			//--- Source directory
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
			
			// --- Populate the context
			PluginLogger.log("Populate the context... " );
//			generator.setContextAttribute("screendata", screenDataClass);

			final IFile file = WizardTools.generateJavaClass(sSourceDir,
					sPackage, sClassName, generator);
			if (file != null) {
				// Selects and reveals the newly added resource
				// selectAndReveal(file); // Fired a "NullPointerException" !!!

				// Open the generated file
				WizardTools.openFileEditor(file, getShell());
			}
		} catch (GeneratorException e) {
			MsgBox.error("GeneratorException : " + e.toString());
			e.printStackTrace();
		} catch (RuntimeException e) {
			MsgBox.error("RuntimeException : " + e.toString());
			e.printStackTrace();
		} catch (Throwable e) {
			MsgBox.error("Throwable : " + e.toString());
			e.printStackTrace();
		}
		return true;
	}
}