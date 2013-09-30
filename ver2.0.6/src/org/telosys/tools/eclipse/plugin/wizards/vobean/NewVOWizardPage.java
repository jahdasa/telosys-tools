package org.telosys.tools.eclipse.plugin.wizards.vobean;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizardPage;

/**
 * Wizard Page
 * 
 * 
 */
public class NewVOWizardPage extends StandardNewJavaClassWizardPage 
{
	private final static String NAME = "Page1"; // Page id in the wizard

	private final static String TITLE = "Value Object Bean";

	private final static String DESCRIPTION = "Create a new Value Object Bean class";

	//private VOAttributeTable _attributesTable = null;
	private VOAttributeTable _attributesTable = null;

	public NewVOWizardPage(IStructuredSelection selection) {
		super(true, NAME, TITLE, DESCRIPTION, selection);
	}

	public void createControl(Composite parent) 
	{
		try {

			initMainComposite(parent);

			createStandardControl();

			createSeparator();

			if ( checkGridComposite() ) 
			{
				Composite gridComposite = getGridComposite();
				
			//--- Label ( SPAN / ALL THE ROW ) 
			GridData gd = new GridData();
			gd.horizontalSpan = 4;
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING ;
			Label label = new Label(gridComposite, SWT.LEFT | SWT.WRAP);
			//label.setFont(gridComposite.getFont());
			label.setText("Attributes :");
			label.setLayoutData(gd);

			//--- Table ( SPAN / Col 1,2,3 in the GRID )
			gd = new GridData();
			gd.horizontalSpan = 3;
			gd.heightHint = 300;
			gd.widthHint  = 580; // for width initialization
			gd.horizontalAlignment = GridData.FILL; // for automatic fill
			//gd.grabExcessHorizontalSpace = false;
			
			_attributesTable = new VOAttributeTable(gridComposite, gd);
			
			//--- Init the table with a first void attribute
//			_attributesTable.addItem( new VOAttributeTableItem(1, "", 0, "", true, true) );
			_attributesTable.addItem( getVoidItem() );

			//--- Buttons ( Col 4 in the GRID )
			createPanelButtons(gridComposite);
			
			}
			
			setControl(getGridComposite());
			initStandardFields();
			setFocus(); // Sets the focus on the type name input field.

			doStatusUpdate();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private VOAttributeTableItem getVoidItem() 
	{
		return new VOAttributeTableItem(1, "", 0, "", true, true);
	}

	/***
	private void createAttributesTable() 
	{
		if ( checkGridComposite() ) 
		{
			Composite gridComposite = getGridComposite();

//			//--- Label ( Col 1 in the GRID )
//			Label label = new Label(gridComposite, SWT.LEFT | SWT.WRAP);
//			label.setFont(gridComposite.getFont());
//			label.setText("Attributes :");
//			label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
//
//			//--- Table ( SPAN / Col 2 & 3 in the GRID )
//			_attributesTable = new VOAttributeTable(gridComposite);
//			GridData gd = new GridData();
//			gd.heightHint = 300;
//			gd.horizontalAlignment = GridData.FILL;
//			gd.grabExcessHorizontalSpace = false;
//			gd.horizontalSpan = 2;
//			_attributesTable.setLayoutData(gd);
			
			//--- Table ( SPAN / Col 1,2,3 in the GRID )
			GridData gd = new GridData();
			gd.heightHint = 300;
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = false;
			gd.horizontalSpan = 3;
			_attributesTable = new VOAttributeTableBIS(panel, gd);
			Composite panel1 = createPanel1(gridComposite) ;
			panel1.setLayoutData(gd);
			
			//--- Init the table with a first void attribute
//			_attributesTable.addItem( new VOAttributeTableItem(1, "", 0, "", true, true) );
			_attributesTable.addItem( getVoidItem() );

			//--- Buttons ( Col 4 in the GRID )
			createPanelButtons(gridComposite);
		}
	}
	***/
	private Composite createPanel1(Composite parent) 
	{
		GridLayout panelLayout = new GridLayout(); 
		panelLayout.numColumns = 1 ;
		
		Composite panel = new Composite(parent, SWT.NONE | SWT.BORDER );
		panel.setLayout(panelLayout);
		
		//--- Label 
		Label label = new Label(panel, SWT.LEFT | SWT.WRAP);
		//label.setFont(gridComposite.getFont());
		label.setText("Attributes :");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		//--- Table 
		//_attributesTable = new VOAttributeTable(panel);
//		_attributesTable = new VOAttributeTableBIS(panel);
//		GridData gd = new GridData();
//		gd.heightHint = 300;
//		gd.horizontalAlignment = GridData.FILL;
//		gd.grabExcessHorizontalSpace = false;
//		_attributesTable.setLayoutData(gd);

		GridData gd = new GridData();
		gd.heightHint = 300;
		gd.widthHint  = 600;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		_attributesTable = new VOAttributeTable(panel, gd);

		return panel ;
	}
	
	private Composite createPanelButtons(Composite composite) 
	{
		RowLayout panelLayout = new RowLayout(); // Vertical row ( = column )
		panelLayout.type = SWT.VERTICAL;
		panelLayout.fill = true; // same width for all controls
		panelLayout.pack = false; // no pack for all controls size

		// --- Panel for buttons
		Composite panelBouton = new Composite(composite, SWT.NONE);
		panelBouton.setLayout(panelLayout);

		// Color color = new Color( panelBouton.getDisplay(), 0xFF, 0, 0 );
		// panelBouton.setBackground(color);

		Button button = null;

		// --- 1rst button
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Add");
		button.setToolTipText("Add an attribute at the end of the list");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// OnClick => Action
				//_attributesTable.addItem(new VOAttributeTableItem(1, "", 0, "", true, true));
				_attributesTable.addItem( getVoidItem() );
				
			}
		});

		// --- "Insert" button
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Insert");
		button.setToolTipText("Insert an attribute");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// OnClick => Action
				//_attributesTable.insertItem(new VOAttributeTableItem(1, "", 0, "", true, true));
				_attributesTable.insertItem( getVoidItem() );
			}
		});

		// --- "Delete" button
		button = new Button(panelBouton, SWT.PUSH);
		button.setText("Delete");
		button.setToolTipText("Delete the selected attribute");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// OnClick => Action
				_attributesTable.removeItem();
			}
		});

		return panelBouton;
	}

	public VOAttributeTableItem[] getArrayAttributes() {
		return _attributesTable.getItems();
	}

//	/**
//	 * Updates status when a field changes.
//	 * 
//	 * @param fieldName
//	 *            the name of the field that had change
//	 */
//	protected void handleFieldChanged(String sField) {
//		log("handleFieldChanged(" + sField + ")");
//		super.handleFieldChanged(sField); // Set the standard status
//											// (fPackageStatus, fTypeNameStatus,
//											// ... )
//		doStatusUpdate();
//	}

//	private void doStatusUpdate() {
//		// status of all used components
//		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
//				fTypeNameStatus };
//
//		// the mode severe status will be displayed and the OK button
//		// enabled/disabled.
//		updateStatus(status);
//	}

	public void specificFieldsChanged(int iFieldId, String sNewValue) {
		// Auto-generated method stub

	}

}
