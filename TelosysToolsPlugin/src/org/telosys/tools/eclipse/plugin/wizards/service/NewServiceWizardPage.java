package org.telosys.tools.eclipse.plugin.wizards.service;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.telosys.tools.eclipse.plugin.commons.Const;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizardPage;

/**
 *
 */
public class NewServiceWizardPage extends StandardNewJavaClassWizardPage
{

	private final static String TITLE = "Service class" ;

	private final static String DESCRIPTION = "Create a new Service class" ;

	private final static String [] comboItems = { "SQLSearch", "SQLSearchWithCount", "SQLSearchWithPages" };
	
	public NewServiceWizardPage(IStructuredSelection selection) {
		super(true, "ServiceWizardPage1", TITLE, DESCRIPTION, selection);
	}
	
	//--- Search service super class combo box
	private Combo _comboBoxSuperClass = null; 

	//--- Service types radio buttons
	private Button _radioButtonRPC = null ;
	private Button _radioButtonNavig = null ;
	private Button _radioButtonSearch = null ;

	private void initFields() {
		PluginLogger.log(getClass().getName() + " : initFields()...");		
		initStandardFields();		
	}
	
	/**
	 * Creates the window that will represent the wizard page.
	 * 
	 * @param composite
	 */
	public void createControl(Composite pageComposite) 
	{
		try
		{
			initMainComposite(pageComposite);
			createStandardControl() ;		
			createSeparator();
	
			createSpecificControls(pageComposite);
	
			//setControl(pageComposite); // correction 20/08/2008
			setControl(getGridComposite()); // correction 20/08/2008
			
			initFields(); 
			doStatusUpdate();
			setFocus(); // Sets the focus on the type name input field.
		}
		catch ( Throwable t )
		{
			MsgBox.error ( "Exception in createControl() : " + t.getClass().getName() + " : "+  t.getMessage() ) ;
			t.printStackTrace();
		}
	}
	
	private void createSpecificControls(Composite pageComposite) 
	{
		Composite gridComposite = getGridComposite();
		createLabel(gridComposite, "Service Type :" );		
		createPanelRadioButtons( gridComposite ); 
	}
	
	private Label createLabel(Composite composite, String sText ) 
	{
		//--- Label
		Label label = new Label(composite, SWT.LEFT | SWT.TOP | SWT.WRAP );
		label.setText(sText);
		//label.setFont(composite.getFont());
		
		//label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING );
		gd.horizontalSpan = 1;
		label.setLayoutData(gd);
		return label ;
	}
	
	private Composite createPanelRadioButtons( Composite parentComposite ) 
	{
		Composite panel = new Composite(parentComposite, SWT.LEFT | SWT.WRAP | SWT.BORDER );
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		panel.setLayoutData(gd);		
		panel.setLayout(new GridLayout(1, false));	

		_radioButtonRPC = createRadioButton(panel, "RPC", Const.TEMPLATE_WIZARD_SERVICE_RPC, true );
		_radioButtonNavig = createRadioButton(panel, "Navigation", Const.TEMPLATE_WIZARD_SERVICE_NAV, false );
		_radioButtonSearch = createRadioButton(panel, "Search", Const.TEMPLATE_WIZARD_SERVICE_SEARCH, false );
		
		_comboBoxSuperClass = createComboBox(panel);
		
		return panel ;
	}
	
	private Button createRadioButton(Composite composite, String sText, String sTemplate, boolean bSelected ) 
	{
		Button button = new Button(composite, SWT.RADIO);
		button.setText(sText);
		//button.setToolTipText(sTemplate);
		button.setData(sTemplate);
		button.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					//--- Enable/Disable "Search type" ComboBox
					if ( e.widget instanceof Button )
					{
						Button b = (Button) e.widget ;
						if ( b.getText().startsWith("S") ) // "Search"
						{
							_comboBoxSuperClass.setEnabled(true);
						}
						else
						{
							_comboBoxSuperClass.setEnabled(false);
						}
					}
				}
			});
		
		if ( bSelected )
		{
			button.setSelection(true);
			//setWidgetSelected(button);
		}
		return button ;
	}
	
	private Combo createComboBox( Composite composite ) 
	{
		//--- Create the combo
		Combo comboBox = new Combo(composite, SWT.CENTER | SWT.READ_ONLY );
		//--- Put the items of the combo
		comboBox.setItems( comboItems );		
		//--- Select the first item
		comboBox.select(0);
		
		comboBox.setEnabled(false) ;
		// comboBox.setVisible(false); 

		return comboBox ;
	}
	
//	/**
//	 * Updates status when a field changes.
//	 * 
//	 * @param fieldName
//	 *            the name of the field that had change
//	 */
//	protected void handleFieldChanged(String sfieldName) {
//
//		super.handleFieldChanged(sfieldName);
//
//		doStatusUpdate();
//	}
	
	protected String getServiceTemplate() {
		if ( _radioButtonRPC.getSelection() ) return (String) _radioButtonRPC.getData() ;
		if ( _radioButtonNavig.getSelection() ) return (String) _radioButtonNavig.getData() ;
		if ( _radioButtonSearch.getSelection() ) return (String) _radioButtonSearch.getData() ;
		//return _sWidgetNameSelected;
		return null ;
	}

	/**
	 * @return the superclass selectionned
	 */
	protected String getSearchSuperClass()
	{
		if ( _radioButtonSearch.getSelection() ) // Search service
		{
			return _comboBoxSuperClass.getItem(_comboBoxSuperClass.getSelectionIndex());
		}
		return null ;
	}
	
//	/**
//	 * Updates status.
//	 */
//	private void doStatusUpdate() {
//		setPageComplete(true);
//		setErrorMessage(null);
//
//		// --- status of all used components
//		IStatus[] status = new IStatus[] {
//				this.fContainerStatus,
//				isEnclosingTypeSelected() ? this.fEnclosingTypeStatus
//						: this.fPackageStatus, this.fTypeNameStatus,
//				this.fModifierStatus};
//
//		// --- the mode severe status will be displayed and the ok
//		// --- button enabled/disabled.
//		updateStatus(status);
//	}


	/* (non-Javadoc)
	 * @see org.objectweb.telosys.plugin.wizards.common.IWizardPageEvents#specificFieldsChanged(int, java.lang.String)
	 */
	public void specificFieldsChanged(int iFieldId, String sNewValue) {
		// No specific fields in this page => nothing to do
	}
}
