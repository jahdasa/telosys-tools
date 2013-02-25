package org.telosys.tools.eclipse.plugin.wizards.screenmanager;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.eclipse.plugin.commons.JModel;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.wizards.common.ScreenDataField;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizardPage;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;

public class NewScreenManagerWizardPage extends StandardNewJavaClassWizardPage 
{

	private final static String TITLE = "Screen Manager class" ;

	private final static String DESCRIPTION = "Create a new Screen Manager class" ;

	private final static int  FIELD_SCREEN_DATA_CLASS = 1 ;

//	private final static Status STATUS1_OK = WizardTools.getStatusOK();
	private final static Status STATUS1_ERR = WizardTools.getStatusError("ScreenData class name is empty") ;
	
	//private Status _status1 = STATUS1_OK ; 
	
	public NewScreenManagerWizardPage(IStructuredSelection selection) {
		super(true, "MyWizardPageName", TITLE, DESCRIPTION, selection);
	}

	private void initFields() {
		//PluginLogger.log(getClass().getName() + " : initFields()...");		
		log("initFields()...");		
		initStandardFields(); // Init "Source folder" + "Package"	
		
		ProjectConfig projectConfig = getProjectConfig() ;
		
//		//--- Set the specific package defined in the project config if any 
//		String sPackage = projectConfig.getPackageForScreenManager();
//		if ( ! StrUtil.nullOrVoid(sPackage) ) // Specific package defined in the project config 
//		{
//			//PluginLogger.log(getClass().getName() + " : initFields() : package = '" + sPackage + "' (from project config)");		
//			log( "initFields() : package = '" + sPackage + "' (from project config)");		
//			setPackageFieldValue(sPackage); // The package must exist
//		}

		//--- If the resource selected is a Java Class that extends the ScreenData 
		String sScreenDataAncestor = projectConfig.getScreenDataAncestor() ;
		if ( isSelectionExtends( sScreenDataAncestor) )
		{
			//PluginLogger.log(getClass().getName() + " : initFields() : SCREEN DATA SELECTED ");		
			log( "initFields() : SCREEN DATA SELECTED ");		
			//--- Set the ScreenData class
			IJavaElement je = getJavaElementSelected();
			if ( je != null )
			{
				IType type = JModel.getJavaType(je);
				if ( type != null )
				{
					//PluginLogger.log(getClass().getName() + " : initFields() : screen data =  " + type.getFullyQualifiedName());		
					log("initFields() : screen data =  " + type.getFullyQualifiedName());		
					setClassToUseFieldValue( type.getFullyQualifiedName() );
				}
			}			
		}
		else
		{
			//PluginLogger.log(getClass().getName() + " : initFields() : NO SCREEN DATA SELECTED ");		
			log("initFields() : NO SCREEN DATA SELECTED ");		
			//--- Keep the package value ( the selected package )
		}
		
		
		//setClassNameFieldValue("");		
	}

	/**
	 * Create the wizard IHM
	 */
	public void createControl(Composite pageComposite) 
	{
		initMainComposite(pageComposite);
		
		createStandardControl() ;		
		createSeparator();
		
		ProjectConfig projectConfig = getProjectConfig() ;
		String sScreenDataClassMask = projectConfig.getScreenDataClassMask();
		ScreenDataField field = new ScreenDataField( FIELD_SCREEN_DATA_CLASS, this, getShell(), getJavaProjectSelected(), sScreenDataClassMask );
		createClassToUseControl(field) ;
		
		//setControl(pageComposite); // correction 20/08/2008
		setControl(getGridComposite()); // correction 20/08/2008
		
		initFields();
		doStatusUpdate();
	}
	
	public String getScreenDataClassFieldValue()
	{
		return getClassToUseFieldValue();
	}
	
	
	
//	/**
//	 * Updates status when a field changes.
//	 * 
//	 * @param fieldName
//	 *            the name of the field that had change
//	 */
//	protected void handleFieldChanged(String sField) {
//		log("handleFieldChanged(" + sField + ")");
//		super.handleFieldChanged(sField); // Set the standard status (fPackageStatus, fTypeNameStatus, ... )
//		doStatusUpdate();
//	}

	public void specificFieldsChanged(int iFieldId,  String sNewValue )
	{
		log("specificFieldsChanged(" + iFieldId + ")");
		if ( iFieldId == FIELD_SCREEN_DATA_CLASS )
		{			
			//String s = getScreenDataClassFieldValue();
			log("specificFieldsChanged(" + iFieldId + ") : check ScreenData class : '" + sNewValue + "'");
			//--- Check the ScreenData field 
			//_status1 = STATUS1_ERR ;
			setStatus(STATUS1_ERR);
			if ( sNewValue != null )
			{
				if ( sNewValue.trim().length() > 0 )
				{
					//_status1 = STATUS1_OK ;
					setStatusOK();
					log("specificFieldsChanged(" + iFieldId + ") : check ScreenData class : '" + sNewValue + "' : Status OK");
				}
			}
		}
		doStatusUpdate();
	}
	
//	/**
//	 * Updates status.
//	 */
//	private void doStatusUpdate() {
//
//		log("doStatusUpdate...");
//		log("_status1 : " + _status1 );
//		
//		//--- Update the status line and the "OK" button according to the given status collection
//		// ( only the most severe message is displayed )
//		IStatus[] status = new IStatus[] {
//				this.fContainerStatus,
//				this.fPackageStatus, 
//				this.fTypeNameStatus,
//				_status1 };
//
//		updateStatus(status); 
//		
//	}	
}
