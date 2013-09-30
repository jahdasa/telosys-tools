package org.telosys.tools.eclipse.plugin.wizards.volist;


import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaClass;


public class NewVOListWizard extends StandardNewJavaClassWizard {

	private final static String TITLE = "New V.O. List" ;
	
	private NewVOListWizardPage _wizardPage = null;

	/**
	 * Constructor of the class
	 */
	public NewVOListWizard() {
		super(TITLE);
	}

	/**
	 * add a page to put information for the class
	 */
	public void addPages() {
		//PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewVOListWizardPage(selection);
		addPage(this._wizardPage);
	}

	/**
	 * call when finish
	 */
	public boolean performFinish() {
		//PluginLogger.log(getClass().getName() + " : performFinish()...");
		log("performFinish()...");

		try {
		
			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();
			Generator generator = getJavaClassGenerator( Const.TEMPLATE_VO_LIST, sPackage, sClassName );
	
			//--- VO Bean Java Class to use
			String sVOBeanClass = _wizardPage.getVOBeanClassFieldValue();
			if ( sVOBeanClass == null )
			{
				MsgBox.error("No bean class => cannot generate !");
				return false ; // Don't close the wizard window
			}
			log("=== VO Bean ...  : " + sVOBeanClass );
//			OldJavaClass beanClass = new OldJavaClass(sVOBeanClass);
			JavaClass beanClass = new JavaClass(sVOBeanClass);

			//--- VO Bean Attributes to use
//			JavaBeanAttribute[] beanAttributes = _wizardPage.getVOBeanAttributes();
//			if ( beanAttributes == null )
//			{
//				MsgBox.error("No bean attributes => cannot generate !");
//				return false ; // Don't close the wizard window
//			}
//			printAttributes(beanAttributes);
			
			//--- Get data from Wizard page(s)
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
			
//			PluginLogger.log("Generator creation : " + sTemplateDirectory
//					+ ", " + sTemplateFile);
//			Generator generator = new Generator(sTemplateDirectory,
//					sTemplateFile,
//					new SysLogWriter());

			// --- Populate the context
			log("Populate the context... " );
			//generator.setContextAttribute(javaClass); // class to generate 
			generator.setContextAttribute("beanClass", beanClass); // bean class to use 
//			generator.setContextAttribute("beanAttributes", beanAttributes ); // bean attributes to use
			

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
		}


		return true; // Close the wizard window
	}
	
//	private void printAttributes(JavaBeanAttribute[] attributes)
//	{
//		PluginLogger.log("-----");
//		PluginLogger.log("Attributes array : ");
//		if ( attributes != null )
//		{
//			for ( int i=0 ; i < attributes.length ; i++)
//			{
//				JavaBeanAttribute attr = (JavaBeanAttribute) attributes[i];
//				PluginLogger.log(" . " + attr.getName() + " : " + attr.getType() 
//						+ " : " + attr.getGetter() + " / " + attr.getSetter() );
//			}
//		}
//		else
//		{
//			PluginLogger.log(" array is null ! " );
//		}
//		PluginLogger.log("-----");
//	}
	
}
