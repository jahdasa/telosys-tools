package org.telosys.tools.eclipse.plugin.wizards.screentriggers;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.telosys.tools.eclipse.plugin.commons.JModel;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.wizards.common.Cell;
import org.telosys.tools.eclipse.plugin.wizards.common.ScreenDataField;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizardPage;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;

public class NewScreenTriggersWizardPage extends StandardNewJavaClassWizardPage 
{

	private final static String TITLE = "Screen Triggers class" ;

	private final static String DESCRIPTION = "Create a new Screen Triggers class" ;

	private final static int  FIELD_SCREEN_DATA_CLASS = 1 ;
//	private final static String  SCREEN_DATA_CLASS_MASK = Config.getScreenDataClassMask() ;
	
//	private final static String STATUS1_ERR_MSG = "ScreenData class name is empty" ;
//	private final static Status STATUS1_OK = new Status(Status.OK, Const.PLUGIN_ID, Status.OK, "", null)  ;
//	private final static Status STATUS1_ERR = new Status(Status.ERROR, Const.PLUGIN_ID, Status.ERROR, STATUS1_ERR_MSG, null)  ;
	
//	private final static Status STATUS1_OK = WizardTools.getStatusOK();
	private final static Status STATUS1_ERR = WizardTools.getStatusError("ScreenData class name is empty") ;

//	private Status _status1 = STATUS1_OK ;
	
	private static final String TRIGGERS []  = CtxTriggers.getTriggers() ;
		
//	private Vector _vListCompletePath = null;
	
	//---Number of method which are selected
	private int _iNbTriggerSelected = 0;
	
	//---List of method
	private Button[] _buttonList = null;
	
	public NewScreenTriggersWizardPage(IStructuredSelection selection) {
		super(true, "MyWizardPageName", TITLE, DESCRIPTION, selection);
	}

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 * @throws JavaModelException 
	 * @throws JavaModelException 
	 */
	// public void init(IStructuredSelection pissSelection)
//	private void init(IStructuredSelection structuredSelection) throws JavaModelException {
//		PluginLogger.log(getClass().getName() + " : init()...");
//		// --- Get the selection in the project
//		// _isSelection = pissSelection;
//		// --- init the JavaElement of the project
//		_javaElement = getInitialJavaElement(structuredSelection);
//
//		// --- init the project
//		initContainerPage(_javaElement);
//		// --- init the page
//		initTypePage(_javaElement);
//
//		this.setTypeName("MyClass", true);
//
//		IPath javaElementPath = _javaElement.getPath(); // "src_dir/pkg1/pkg2/Class.java"
//		int iType = _javaElement.getElementType();
//		PluginLogger.log("javaElement type = " + iType);
//		if (iType == IJavaElement.TYPE) {
//			System.out
//					.println("javaElement type is TYPE ( CLASS or INTERFACE ) ");
//		}
//		if (iType == IJavaElement.COMPILATION_UNIT) {
//			PluginLogger.log("javaElement type is COMPILATION_UNIT ");
//		}
//		if (iType == IJavaElement.PACKAGE_FRAGMENT) {
//			PluginLogger.log("javaElement type is PACKAGE_FRAGMENT ");
//		}
//		if (iType == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
//			PluginLogger.log("javaElement type is PACKAGE_FRAGMENT_ROOT ");
//		}
//
//		PluginLogger.log(getClass().getName() + " : javaElementPath = "
//				+ javaElementPath);
//		
//		
//		checkSuperClass();
//
//		doStatusUpdate();
//	}
	
	private void initFields() {
		PluginLogger.log(getClass().getName() + " : initFields()...");		
		initStandardFields(); // Init "Source folder" + "Package"	

		ProjectConfig projectConfig = getProjectConfig() ;
		
//		//--- Set the specific package defined in the project config if any 
//		String sPackage = projectConfig.getPackageForScreenTriggers();
//		if ( ! StrUtil.nullOrVoid(sPackage) ) // Specific package defined in the project config 
//		{
//			PluginLogger.log(getClass().getName() + " : initFields() : package = '" + sPackage + "' (from project config)");		
//			setPackageFieldValue(sPackage); // The package must exist
//		}

		//--- If the resource selected is a Java Class that extends the ScreenData 
		//String sScreenDataAncestor = projectConfig.getScreenDataAncestor() ;
		String sScreenDataAncestor = "XXXX" ; // TODO
		if ( isSelectionExtends( sScreenDataAncestor) )
		{
			PluginLogger.log(getClass().getName() + " : initFields() : SCREEN DATA SELECTED ");		
//			//--- Set the trigger package 
//			String s = projectConfig.getScreenTriggersPackage();
//			if ( s != null )
//			{
//				PluginLogger.log(getClass().getName() + " : initFields() : package =  " + s);		
//				setPackageFieldValue(s); // The package must exist
//			}
			//--- Set the ScreenData class
			IJavaElement je = getJavaElementSelected();
			if ( je != null )
			{
				IType type = JModel.getJavaType(je);
				if ( type != null )
				{
					PluginLogger.log(getClass().getName() + " : initFields() : screen data =  " + type.getFullyQualifiedName());		
					setClassToUseFieldValue( type.getFullyQualifiedName() );
				}
			}
		}
		else
		{
			PluginLogger.log(getClass().getName() + " : initFields() : NO SCREEN DATA SELECTED ");		
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
		//String sScreenDataClassMask = projectConfig.getScreenDataClassMask();
		String sScreenDataClassMask = "XXXX"; // TODO
		ScreenDataField field = new ScreenDataField( FIELD_SCREEN_DATA_CLASS, this, getShell(), getJavaProjectSelected(), sScreenDataClassMask );
		createClassToUseControl(field) ;
		
		createSeparator();
		createTriggersControl() ;
		
		//setControl(mainComposite);
		//setControl(pageComposite); // correction 20/08/2008
		setControl(getGridComposite()); // correction 20/08/2008
		
		
//		try {
//			init(_selection);
//		} catch (JavaModelException e) {
//			MsgBox.error("No Source", e.getMessage());
//		}
		initFields();
		doStatusUpdate();
	}
	
	public void createTriggersControl() 
	{
		if ( checkGridComposite() )
		{
			Composite gridComposite = getGridComposite();
			
			//--- Label		
			Label label= new Label(gridComposite, SWT.LEFT | SWT.WRAP );
			label.setFont(gridComposite.getFont());
			label.setText("Triggers :");
			label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	
			//--- Panel with all the check boxes ( SPAN 2 Columns )
			Composite panel = createPanelCheckBoxes(gridComposite);
			panel.setLayoutData(Cell.HSpan2());
			
			//--- Panel with the 2 buttons 
			panel = createPanelButtons(gridComposite);			
		}
	}

	private Composite createPanelCheckBoxes(Composite composite) 
	{
//		// ---Creation d'un scrolledComposite
//		ScrolledComposite scrolledComposite = new ScrolledComposite(composite,
//				SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
//
//		// ---Composite dans le scrolled
//		Composite panel = new Composite(scrolledComposite, SWT.NONE);
//
//		scrolledComposite.setContent(panel);
//		scrolledComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//Composite panel= new Composite(composite, SWT.NONE);
		Composite panel= new Composite(composite, SWT.BORDER );
//		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		panel.setLayout(new GridLayout(2, false));
		
		int nb = TRIGGERS.length ;		
		_buttonList = new Button[nb];
		Button button = null;
		String sLabelTrigger = null;
		for (int i = 0; i < nb; i++) 
		{
			button = new Button(panel, SWT.CHECK);
			sLabelTrigger = TRIGGERS[i];
			
			button.setText(sLabelTrigger);
			button.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.getSource();
					boolean select = button.getSelection();
					if(select)
					{
						_iNbTriggerSelected++;
					}
					else
					{
						_iNbTriggerSelected--;
					}
					doStatusUpdate();
				}
			});
			
			_buttonList[i] = button;
			panel.setSize(panel.computeSize( SWT.DEFAULT, SWT.DEFAULT));
			panel.layout();
		}
		return panel ;
	}

	public String getScreenDataClassFieldValue()
	{
		return getClassToUseFieldValue();
	}
	
	public boolean[] getSelectedTriggers()
	{
		int n = _buttonList.length ;
		if ( n != TRIGGERS.length )
		{
			MsgBox.error(n + "checkboxes for " + TRIGGERS.length + " triggers ! ");
			return null;
		}
		//String[] selected = new String[ n ];
		boolean[] selected = new boolean[ n ];
		for ( int i=0 ; i < n ; i++)
		{
			Button button = _buttonList[i];
			selected[i] = button.getSelection(); // true = selected
		}
		return selected ;
	}
	
	private Composite createPanelButtons(Composite composite) 
	{		
		GridData gdHorizFill = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		// ---Panel pour les boutons
		Composite panelBouton = new Composite(composite, SWT.NONE);
		//panelBouton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		panelBouton.setLayout(new GridLayout(1, false));

		//--- 1rst button 
		Button selectAll = new Button(panelBouton, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.setLayoutData( gdHorizFill );
		selectAll.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				selectAll(_buttonList);
			}
		});
		
		//--- 2nd button 
		Button deselectAll = new Button(panelBouton, SWT.PUSH);
		deselectAll.setText("Deselect All");
		selectAll.setLayoutData( gdHorizFill );
		deselectAll.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				deselectAll(_buttonList);
			}
		});
		return panelBouton ;
	}
	
	/**
	 * Use when the button "SelectAll" is pressed
	 * 
	 * @param button
	 *            List of the checkbox
	 */
	private void selectAll(Button[] button) {
		int nb = button.length;
		for (int i = 0; i < nb; i++) {
			button[i].setSelection(true);
		}
		_iNbTriggerSelected = _buttonList.length;
		//doStatusUpdate();
	}

	/**
	 * Use when the button "DeselectAll" is pressed
	 * 
	 * @param button
	 *            List of the checkbox
	 */
	private void deselectAll(Button[] button) {
		int nb = button.length;
		for (int i = 0; i < nb; i++) {
			button[i].setSelection(false);
		}
		_iNbTriggerSelected = 0;
		//doStatusUpdate();
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

	public void specificFieldsChanged(int iFieldId,  String sNewValue )
	{
		log("specificFieldsChanged(" + iFieldId + ")");
		if ( iFieldId == FIELD_SCREEN_DATA_CLASS )
		{			
			//String s = getScreenDataClassFieldValue();
			log("specificFieldsChanged(" + iFieldId + ") : check ScreenData class : '" + sNewValue + "'");
			//--- Check the ScreenData field 
//			_status1 = STATUS1_ERR ;
			setStatus(STATUS1_ERR);
			if ( sNewValue != null )
			{
				if ( sNewValue.trim().length() > 0 )
				{
//					_status1 = STATUS1_OK ;
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
//		//setPageComplete(true); // used by the Wizard to decide when it is okay to move on to the next page or finish up
//		
////		setErrorMessage("+++ My Error Message +++");
////		setMessage("+++ My Message +++");
//		
//
//		// --- status of all used components
////		IStatus[] status = new IStatus[] {
////				this.fContainerStatus,
////				isEnclosingTypeSelected() ? this.fEnclosingTypeStatus : this.fPackageStatus, 
////				this.fTypeNameStatus,
////				this.fModifierStatus };
//
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
//////		setPageComplete(true);
////		
////		if(isPageComplete())
////		{
//////			if(_iNbTriggerSelected == 0)
//////			{
//////				setErrorMessage("Vous devez selectionnez au moins une methode");
//////				setPageComplete(false);
//////				return;
//////			}
//////			
//////			if(_text.getText().length() == 0)
//////			{
//////				setErrorMessage("Vous devez selectionnez une classe");
//////				setPageComplete(false);
//////				return;
//////			}
////		}
//	}
	
//	/**
//	 * Replace the Data by Trg
//	 * @param sClassName Name of the class like ClassData
//	 */
//	private void replateData(String sClassName)
//	{
//		int index = sClassName.indexOf("Data");
//		StringBuffer sClass = null;
//		if(index != -1)
//		{
//			sClass = new StringBuffer(sClassName.substring(0, index));
//			//PluginLogger.log(sClass);
//			sClass.append("Trg");
//			this.setTypeName(String.valueOf(sClass), true);
//		}
//		else
//		{
//			sClass = new StringBuffer(sClassName);
//			//PluginLogger.log(sClass);
//			sClass.append("Trg");
//			this.setTypeName(String.valueOf(sClass), true);
//		}
//	}
	
//	/**
//	 * Use to control if the class which is selected extends of StandardScreenData
//	 * @throws JavaModelException
//	 */
//	private void checkSuperClass() throws JavaModelException
//	{
//		if(_javaElement.getElementType() == IJavaElement.COMPILATION_UNIT)
//		{
//			ICompilationUnit classUnit;
//			classUnit = (ICompilationUnit) _javaElement;
//			IType[] type = classUnit.getAllTypes();
//			String sClassData = null;
//			
//			boolean superclass = false;
//			int i = 0;
//			while(i<type.length && !superclass)
//			{
//				if(type[i].getSuperclassTypeSignature().indexOf("StandardScreenData") != -1 && type[i].getSuperclassTypeSignature().indexOf("StandardScreenDataAccessor") == -1)
//				{
//					String sClass = _javaElement.getElementName();
//					int index = sClass.indexOf(".java");
//					sClassData = sClass.substring(0, index);
//					//_text.setText(sClassData);
//					replateData(sClassData);
//					superclass = true;
//					sClassData = null;
//				}
//				else
//				{
//					i++;
//				}
//			}
//		}
//			
//	}
	

	
//	private void initVector()
//	{
//		if(_vListCompletePath == null)
//		{
//			_vListCompletePath = new Vector();
//		}
//	}
	


//	/**
//	 * @return the _buttonList
//	 */
//	public Button[] getButtonList() {
//		return _buttonList;
//	}

//	/**
//	 * @return the _iNbTriggerSelected
//	 */
//	public int getNbTriggerSelected() {
//		return _iNbTriggerSelected;
//	}
}
