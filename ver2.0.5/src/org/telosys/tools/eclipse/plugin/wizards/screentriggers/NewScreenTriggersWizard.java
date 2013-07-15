package org.telosys.tools.eclipse.plugin.wizards.screentriggers;


import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaClass;


public class NewScreenTriggersWizard extends StandardNewJavaClassWizard {

	private final static String TITLE = "New Telosys Screen Triggers" ;
	
	private NewScreenTriggersWizardPage _wizardPage = null;

	/**
	 * Constructor of the class
	 */
	public NewScreenTriggersWizard() {
		super(TITLE);
	}

	/**
	 * add a page to put information for the class
	 */
	public void addPages() {
		PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewScreenTriggersWizardPage(selection);
		addPage(this._wizardPage);
	}

	/**
	 * call when finish
	 */
	public boolean performFinish() {
		PluginLogger.log(getClass().getName() + " : performFinish()...");
		try {
			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();

			Generator generator = getJavaClassGenerator( Const.TEMPLATE_WIZARD_SCREEN_TRIGGERS, sPackage, sClassName );
			
			//--- ScreenData Java Class to use
			String sScreenDataClass = _wizardPage.getScreenDataClassFieldValue();
			PluginLogger.log("=== ScreenData...  : " + sScreenDataClass );
			//OldJavaClass screenDataClass = new OldJavaClass(sScreenDataClass);
			JavaClass screenDataClass = new JavaClass(sScreenDataClass);

			//--- Triggers to generate
			PluginLogger.log("=== new CtxTriggers(..)... " );
			CtxTriggers triggers = new CtxTriggers( _wizardPage.getSelectedTriggers() );				
		
			//--- Get data from Wizard page(s)
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
		
			//--- Populate the context
			PluginLogger.log("Populate the context... " );
			generator.setContextAttribute("screendata", screenDataClass);
			generator.setContextAttribute("triggers", triggers);
			
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
