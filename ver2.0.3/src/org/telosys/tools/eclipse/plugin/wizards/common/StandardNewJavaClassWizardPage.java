package org.telosys.tools.eclipse.plugin.wizards.common;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.commons.config.ClassNameProvider;
import org.telosys.tools.eclipse.plugin.commons.JModel;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.config.ProjectClassNameProvider;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;

public abstract class StandardNewJavaClassWizardPage extends NewTypeWizardPage implements IWizardPageEvents  {
	
	private static final int NB_COL = 4 ;

	private final static Status STATUS1_OK = WizardTools.getStatusOK();
	
	private Status _status1 = STATUS1_OK ; 
	
	private IStructuredSelection _selection = null;

	private IJavaProject _javaProjectSelected = null;
	
	private IJavaElement _javaElementSelected = null;
	
	private ProjectConfig _projectConfig = null ;
		
	//--- Composite field ( for the selection of ClassData )
	private ISelectedClassField _classToUse = null;
	
	private Composite _mainComposite = null ;
	private Composite _gridComposite = null ;
	
	
	public StandardNewJavaClassWizardPage(boolean isClass, String sPageName, String sTitle, String sDesc, IStructuredSelection selection ) {
		super(isClass, sPageName);
		setTitle(sTitle);
		setDescription(sDesc);
		
		//--- Keep the current selection 
		_selection = selection;
		
		//--- Set the current project  
		_javaElementSelected = getInitialJavaElement(selection);
		if (_javaElementSelected != null) {
			_javaProjectSelected = _javaElementSelected.getJavaProject();
		} else {
			_javaProjectSelected = null;
		}
		
//		ProjectConfigManager.setCurrentProject(_javaProjectSelected);		
//		_projectConfig = ProjectConfigManager.getCurrentProjectConfig(); 
		IProject project = JModel.toProject( _javaProjectSelected );
		_projectConfig = ProjectConfigManager.getProjectConfig(project); 
		
//		if ( javaElement instanceof IType )
//		{
//			IType type = (IType) javaElement ;
//			String s = type.getFullyQualifiedName();
//			String sSuperClass = type.getSuperclassName();
//		}
	}
	
	protected void log(String s)
	{
		PluginLogger.log(this, s);
	}
	
	/**
	 * Set the value of the "Package" field 
	 * @param s
	 */
	public void setPackageFieldValue(String s)
	{
		if ( _javaProjectSelected != null )
		{
			IPackageFragment pf = JModel.getPackageFragment(_javaProjectSelected, s);
			if ( pf != null )
			{
				setPackageFragment(pf, true); // true for editable ( false = readonly )
			}
			else
			{
				MsgBox.warning("The package '" + s + "' doesn't exist !");
			}
		}
		else
		{
			MsgBox.error("setPackageFieldValue() : no JavaProject selected !");
		}
	}
	
	/**
	 * Set the value of the "Name" field ( name of the new class ) 
	 * @param s
	 */
	public void setClassNameFieldValue(String s)
	{
		setTypeName(s, true); // true for editable ( false = readonly )
	}
	
	/**
	 * Set the value of the "Class to use" ( fully qualified name expected )<br>
	 * e.g. : ScreenData class for triggers, VO class for XmlMapper, ...
	 * @param s
	 */
	public void setClassToUseFieldValue(String s)
	{
		if ( _classToUse != null )
		{
			_classToUse.setFieldValue(s);
		}
	}
	
	public String getClassToUseFieldValue()
	{
//		if ( _textScreenDataClass != null )
//		{
//			return _textScreenDataClass.getText();
//		}
		if ( _classToUse != null )
		{
			return _classToUse.getFieldValue();
		}
		return null ;
	}
	
	protected Composite getMainComposite()
	{
		return _mainComposite;
	}
	
	protected Composite getGridComposite()
	{
		return _gridComposite;
	}
	
	protected Composite initMainComposite( Composite pageComposite ) 
	{
		//--- Init and set the  MAIN COMPOSITE of the PAGE
		initializeDialogUnits(pageComposite);
//		Composite mainComposite = new Composite(pageComposite, SWT.NONE);
//		mainComposite.setLayout(new GridLayout(1, false));
//		return mainComposite ;
		_mainComposite = pageComposite ;
		
		//--- Standard GRID LAYOUT PANEL ( with 4 columns )
		_gridComposite = new Composite(_mainComposite, SWT.NONE);
		_gridComposite.setLayout(new GridLayout(NB_COL, false));
		_gridComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return _mainComposite ;
	}
	
	protected boolean checkMainComposite() 
	{
		if ( _mainComposite != null )
		{
			return true ;			
		}
		else
		{
			MsgBox.error("Main Composite is null !");
			return false ;			
		}			
	}
	
	protected boolean checkGridComposite() 
	{
		if ( _gridComposite != null )
		{
			return true ;			
		}
		else
		{
			MsgBox.error("Grid Composite is null !");
			return false ;			
		}			
	}
	
	//=====================================================================================
	protected void createSeparator() 
	{
		if ( checkGridComposite() ) 
		{
			createSeparator(_gridComposite);	
		}
	}
	
	private void createSeparator( Composite composite ) 
	{
		createSeparator(composite, NB_COL);
	}
	
	//=====================================================================================
	protected void createStandardControl() 
	{
		if ( checkGridComposite() ) 
		{
			createStandardControl(_gridComposite);			
		}
	}
	
	//private Composite createStandardControl( Composite composite ) 
	private void createStandardControl( Composite composite ) 
	{

//		Composite compositeStandard = new Composite(composite, SWT.NONE);
//
//		// ---Layout Data
//		compositeStandard.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//		// ajout du layout au container
//		compositeStandard.setLayout(new GridLayout(NB_COL, false));

		// permet la selection du projet
		createContainerControls(composite, NB_COL);

		// permet la selection du package
		createPackageControls(composite, NB_COL);

		// crée une ligne de séparation
		//createSeparator(compositeStandard, NB_COL);

		// permet d'inserer le nom de la Classe
		createTypeNameControls(composite, NB_COL);

		// crée une ligne de séparation
		//createSeparator(composite, NB_COL);

		//return compositePrincipal;
		//return compositeStandard;
	}

	//=====================================================================================
	//protected void createScreenDataClassControl( int iFieldId, String sMask ) 
	protected void createClassToUseControl( ISelectedClassField classToUseField ) 
	{
//		MsgBox.info("createClassToUseControl : " + classToUseField.getClass() );
		if ( checkGridComposite() ) 
		{
//			_voBeanField = new VOBeanField( iFieldId, this, getShell(), _javaProjectSelected, sMask );
//			_voBeanField.setInComposite(_gridComposite, NB_COL);
			if ( classToUseField != null )
			{
				_classToUse = classToUseField ;
				_classToUse.setInComposite(_gridComposite, NB_COL);
			}
		}
	}
	
	//=====================================================================================
	protected IStructuredSelection getSelection() 
	{
		return _selection ;
	}
	
	protected IJavaElement getJavaElementSelected() 
	{
		return _javaElementSelected  ;
	}

	protected IJavaProject getJavaProjectSelected() 
	{
		return _javaProjectSelected  ;
	}
	
	protected IType getJavaTypeSelected() 
	{
		return JModel.getJavaType( _javaElementSelected ) ;
	}
	
	/**
	 * Returns the "ProjectConfig" for the "selection" of the wizard  
	 * @return
	 */
	protected ProjectConfig getProjectConfig() 
	{
		return _projectConfig ;
	}

	/**
	 * Returns the "ClassNameProvider" for the current project configuration
	 * or null if no current ProjectConfig
	 * @return 
	 */
	protected ClassNameProvider getClassNameProvider() 
	{
		if ( _projectConfig != null )
		{
			ClassNameProvider classNameProvider = new ProjectClassNameProvider( _projectConfig );
			return classNameProvider ;
		}
		return null ;
	}
	
	private String shortName( String s ) 
	{
		int i = s.lastIndexOf('.');
		if ( i >= 0 )
		{
			return s.substring(i+1);
		}
		return s ;
	}
	protected boolean isSelectionExtends( String sAncestor ) 
	{
		PluginLogger.log("isSelectionExtends('" + sAncestor +"')");
		if ( _javaElementSelected != null && sAncestor != null )
		{
			String sSuperClass = JModel.getSuperClass(_javaElementSelected);
			PluginLogger.log("SuperClass = " + sSuperClass);
			if ( sSuperClass != null )
			{
				if ( sSuperClass.equals(sAncestor) )
				{
					return true ;
				}
				if ( sSuperClass.equals( shortName( sAncestor ) ) )
				{
					return true ;
				}
//				int i = sAncestor.lastIndexOf('.');
//				if ( i >= 0 )
//				{
//					String sShortName = sAncestor.substring(i+1);
//					return sSuperClass.equals(sShortName) ;
//				}
//				else
//				{
//					if ( sSuperClass.indexOf('.') >= 0 )
//					{
//						return sSuperClass.equals(sAncestor) ;
//					}
//				}
			}
		}
		return false ;
	}
	
	/**
	 * Init the standard fields "Source folder" and "Package" using the 
	 * wizard current selection 
	 */
	protected void initStandardFields() 
	{
		IStructuredSelection selection = getSelection() ;
		if ( selection != null )			
		{
			IJavaElement javaElement = getInitialJavaElement(selection);
			if ( javaElement != null )			
			{
				//--- init the "Source folder field"
				initContainerPage(javaElement);
				//--- init all the fields of the new JavaClass ( package + class name )
				initTypePage(javaElement);
			}
		}
		
//		// NewTypeWizardPage methods 
//		setPackageFragmentRoot(xx,true); // Source folder field
//		setPackageFragment(xx,true); // Package field
//		setTypeName("", true); // Class name
//		setSuperClass("", true); // Super Class name
	}
	
	protected void setStatus(Status status) {
		_status1 = status ;
	}
	protected void setStatusOK() {
		_status1 = STATUS1_OK ;
	}
	
	/**
	 * Updates status when a field changes.
	 * 
	 * @param fieldName
	 *            the name of the field that had change
	 */
	protected void handleFieldChanged(String sField) {
		log("handleFieldChanged(" + sField + ")");
		super.handleFieldChanged(sField); // Set the standard status (fPackageStatus, fTypeNameStatus, ... )
		doStatusUpdate();
	}

	/**
	 * Updates status.
	 */
	protected void doStatusUpdate() {

		log("doStatusUpdate...");
		log("_status1 : " + _status1 );
		
		//--- Update the status line and the "OK" button according to the given status collection
		// ( only the most severe message is displayed )
		IStatus[] status = new IStatus[] {
				this.fContainerStatus,
				this.fPackageStatus, 
				this.fTypeNameStatus,
				_status1 };

		updateStatus(status); 
		
	}	
	
}
