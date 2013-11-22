package org.telosys.tools.eclipse.plugin.wizards.vobean;

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.generator.context.JavaBeanClassAttribute;

/**
 * Wizard class to create a new V.O. bean class
 *
 * @author L. Guerin
 * 
 */
public class NewVOWizard extends StandardNewJavaClassWizard {

	private final static String TITLE = "New Value Object bean" ;
	
	private NewVOWizardPage _wizardPage = null ; // the single page of this wizard
	
	/**
	 * Constructor 
	 */
	public NewVOWizard() {
		super(TITLE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewVOWizardPage(selection);
		addPage(_wizardPage);
	}
	
	private JavaBeanClassAttribute[] getAttributes( VOAttributeTableItem[] arrayAtt ) 
	{
		if ( null == arrayAtt ) {
			return new JavaBeanClassAttribute[0];
		}
		else {
			LinkedList attributes = new LinkedList();
			
			for(int i = 0; i < arrayAtt.length; i++) 
			{
				VOAttributeTableItem item = arrayAtt[i] ;
				if( item.sAttributeName.trim().length() > 0 ) 
				{
					String sAttributeName = item.sAttributeName.trim();
					
//					String sShortType = VOConst.ARRAY_OF_JAVA_TYPES[ arrayAtt[i].iType ] ;
//					String sFullType = JavaTypeUtil.fullType(sShortType);
					int iType = arrayAtt[i].iType ;
					JavaTypes javaTypes = JavaTypesManager.getJavaTypes();
					String sShortType = javaTypes.getShortType(iType);
					String sFullType  = javaTypes.getType(iType);
					
					String sInitialValue = null ;
					if ( item.sInitialValue.trim().length() > 0 )
					{
						sInitialValue = item.sInitialValue.trim();
					}
					
//					JavaBeanClassAttribute attribute = new JavaBeanClassAttribute(
//						sAttributeName,
//						sShortType,
//						sFullType,
//						sInitialValue,
//						(arrayAtt[i].bGetter) ? getGetterName(arrayAtt[i].sAttributeName) : null,
//						(arrayAtt[i].bSetter) ? getSetterName(arrayAtt[i].sAttributeName) : null
//					);
					// v 2.0.7
					JavaBeanClassAttribute attribute = new JavaBeanClassAttribute(
							sAttributeName,
							sShortType,
							sFullType,
							sInitialValue
						);
					attributes.add(attribute);
				}
			}
			return (JavaBeanClassAttribute[]) attributes.toArray( new JavaBeanClassAttribute[0] );
		}
		
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

			//--- Create a new Generator for the given Java class target
			Generator generator = getJavaClassGenerator( Const.TEMPLATE_WIZARD_VO_BEAN, sPackage, sClassName );
			
			//--- Retrieve the Java class to populate attributes
//			OldJavaClass javaBeanClass = generator.getJavaClassTargetFromContext();
//			if ( javaBeanClass == null )
//			{
//				MsgBox.error("No Java class in the generator context !");
//				return false ;
//			}
			
			//--- Populate the Java class attributes
			PluginLogger.log("Populate the Java class attributes ... " );
			
			VOAttributeTableItem[] arrayAtt = _wizardPage.getArrayAttributes();
			
//			for(int i = 0; i < arrayAtt.length; i++) 
//			{
//				VOAttributeTableItem item = arrayAtt[i] ;
//				if( item.sAttributeName.trim().length() > 0 ) 
//				{
//					String sAttributeName = item.sAttributeName.trim();
//					String sShortType = VOConst.ARRAY_TYPES[ arrayAtt[i].iType ] ;
//					String sFullType = JavaTypeUtil.fullType(sShortType);
//					String sInitialValue = null ;
//					if ( item.sInitialValue.trim().length() > 0 )
//					{
//						sInitialValue = item.sInitialValue.trim();
//					}
//					
////					OldJavaClassAttribute attribute = new OldJavaClassAttribute(
//					JavaBeanClassAttribute attribute = new JavaBeanClassAttribute(
//						sAttributeName,
//						sShortType,
//						sFullType,
//						sInitialValue,
//						(arrayAtt[i].bGetter) ? getGetterName(arrayAtt[i].sAttributeName) : null,
//						(arrayAtt[i].bSetter) ? getSetterName(arrayAtt[i].sAttributeName) : null
//					);
//				
//					javaBeanClass.addAttribute(attribute);
//				}
//			}
//			
//			javaBeanClass.endOfDefinition(); // close the class definition (prepares imports list)
			
			JavaBeanClassAttribute[] attributes = getAttributes(arrayAtt);
			JavaBeanClass javaClass = new JavaBeanClass(sClassName, sPackage, attributes);
			generator.setJavaClassTargetInContext(javaClass);
			
			//--- Source directory
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
			
			//--- Generation
			IFile file = WizardTools.generateJavaClass(sSourceDir,
					sPackage, sClassName, generator);
			
			if (file != null) {	
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
	
	private String getGetterName(String attName) {
		return "get"+attName.substring(0, 1).toUpperCase()+attName.substring(1, attName.length());
	}

	private String getSetterName(String attName) {
		return "set"+attName.substring(0, 1).toUpperCase()+attName.substring(1, attName.length());
	}
}
