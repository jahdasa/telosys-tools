package org.telosys.tools.eclipse.plugin.wizards.xmlmapper;


import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.generator.context.JavaBeanClassAttribute;
import org.telosys.tools.generator.context.JavaClass;

/**
 * Wizard class to create a new XML mapper class
 * 
 * @author L. Guerin
 *
 */
public class NewXmlMapperWizard extends StandardNewJavaClassWizard {

	private final static String TITLE = "New V.O. XML mapper" ;
	
	private NewXmlMapperWizardPage _wizardPage = null; // the single page of this wizard

	/**
	 * Constructor 
	 */
	public NewXmlMapperWizard() {
		super(TITLE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewXmlMapperWizardPage(selection);
		addPage(this._wizardPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	// Method called at then end of the wizard 
	public boolean performFinish() {
		PluginLogger.log(getClass().getName() + " : performFinish()...");

		try {		
			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();
			
			//--- VO Bean Java Class to use
			String sVOBeanClass = _wizardPage.getVOBeanClassFieldValue();
			if ( sVOBeanClass == null )
			{
				MsgBox.error("No bean class => cannot generate !");
				return false ; // Don't close the wizard window
			}
			PluginLogger.log("=== VO Bean ...  : " + sVOBeanClass );

			//OldJavaClass beanClass = new OldJavaClass(sVOBeanClass);

			//--- VO Bean Attributes to use
//			OldJavaClassAttribute[] beanAttributes = _wizardPage.getVOBeanAttributes();
			JavaBeanClassAttribute[] beanAttributes = _wizardPage.getVOBeanAttributes();
			if ( beanAttributes == null )
			{
				MsgBox.error("No bean attributes => cannot generate !");
				return false ; // Don't close the wizard window
			}
			JavaClass beanClass = new JavaBeanClass(sVOBeanClass, beanAttributes);
			//beanClass.addAttributes(beanAttributes);
			printAttributes(beanAttributes);
			
			//--- Get data from Wizard page(s)
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
			
			//--- Create a new Generator for the given Java class target
			//Generator generator = getJavaClassGenerator( Const.TEMPLATE_XML_MAPPER, sPackage, sClassName );
			Generator generator = getJavaClassGenerator( Const.TEMPLATE_VO_XML, sPackage, sClassName );
	
			// --- Populate the context
			PluginLogger.log("Populate the context... " );
			//generator.setContextAttribute(javaClass); // class to generate 
			generator.setContextAttribute("beanClass", beanClass); // the V.O. bean class to use 
			//generator.setContextAttribute("beanAttributes", beanAttributes ); // bean attributes to use
			

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
	
//	private void printAttributes(OldJavaClassAttribute[] attributes)
	private void printAttributes(JavaBeanClassAttribute[] attributes)
	{
		PluginLogger.log("-----");
		PluginLogger.log("Bean class attributes array : ");
		if ( attributes != null )
		{
			for ( int i=0 ; i < attributes.length ; i++)
			{
//				OldJavaClassAttribute attr = attributes[i];
				JavaBeanClassAttribute attr = attributes[i];
				PluginLogger.log(" . " + attr.getName() + " : " + attr.getType() 
						+ " : " + attr.getGetter() + " / " + attr.getSetter() );
			}
		}
		else
		{
			PluginLogger.log(" array is null ! " );
		}
		PluginLogger.log("-----");
	}
	
}
