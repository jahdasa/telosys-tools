package org.telosys.tools.eclipse.plugin.wizards.dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizardPage;
/**
 * Classe implémentant la page du wizard 
 *
 *
 */
public class NewDataSetWizardPage extends StandardNewJavaClassWizardPage {

	private final static String NAME = "Page1" ; // Page id in the wizard

	private final static String TITLE = "SQL Dataset";

	private final static String DESCRIPTION = "Create a new SQL Dataset definition class" ;
	
	private String _destinationPath = "";
	
	private Combo _comboTemplates = null;
	private Combo _comboTemplatesTest = null;

	private Button _destinationBrowseButton = null;
	private Button _saveButton = null;
	
	private Text _requestText = null;
	
	private DatasetStaticCritTable _tableStaticCrit = null;
	private int _iCurrentStaticCritNumber = 0;
	

	public NewDataSetWizardPage(IStructuredSelection selection) {
		super(true, NAME, TITLE, DESCRIPTION, selection);
	}
	
	public void createControl(Composite parent) {
		try {
			NewDataSetWizard wizard = (NewDataSetWizard)getWizard();
			
			initMainComposite(parent);
			
			//Controle standard de wizard Eclipse
			createStandardControl() ;
			
			createSeparator();
			
			//Options du wizard
			//createOptionCheckBox(getGridComposite());
			//--- Combo boxes for templates
			createTemplatesChooser() ;
			createSeparator();
			
			//--- SQL Request Text Area
			//createSqlTextControl(getGridComposite());
			createSqlTextControl();
		        
		    createSeparator();
		    
			//--- Table for parameters types
		    //createStaticCritTable(getGridComposite());
		    createStaticCritTable();
			
			setControl(getGridComposite());
			initStandardFields();
			setFocus(); // Sets the focus on the type name input field.
			
			doStatusUpdate();
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
//	private void createOptionCheckBox(Composite parent) {
//		Label labelRequest = new Label(parent,SWT.WRAP);
//        labelRequest.setText("Options :");
//		
//		Button staticCheckBox = new Button(parent, SWT.CHECK);
//		staticCheckBox.setText("Static class");
//		staticCheckBox.setToolTipText("The dataset class will be static, so you can't instantiate her");
//		staticCheckBox.addListener(SWT.Selection, new Listener() {
//
//			public void handleEvent(Event event) {
//				context.bStatic = ((Button)event.widget).getSelection();
//			}
//			
//		});
//		
//		Button testCheckBox = new Button(parent, SWT.CHECK);
//		testCheckBox.setText("Test class");
//		testCheckBox.setToolTipText("Generate basic Test class for your Dataset class");
//		testCheckBox.addListener(SWT.Selection, new Listener() {
//
//			public void handleEvent(Event event) {
//				context.bTest = ((Button)event.widget).getSelection();
//			}
//			
//		});
//	}
	
	private void createTemplatesChooser() 
	{
		if ( checkGridComposite() )
		{
			Composite gridComposite = getGridComposite();
			
			//--- Label		
			Label label= new Label(gridComposite, SWT.LEFT | SWT.WRAP );
			label.setFont(gridComposite.getFont());
			label.setText("Templates :");
			label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

			//--- Panel
			Composite panel = createTemplateChooserPanel(gridComposite) ;
			
//			Button staticCheckBox = new Button(gridComposite, SWT.CHECK);
//			staticCheckBox.setText("Static class");
//			staticCheckBox.setToolTipText("The dataset class will be static, so you can't instantiate her");
//			staticCheckBox.addListener(SWT.Selection, new Listener() {
//	
//				public void handleEvent(Event event) {
//					context.bStatic = ((Button)event.widget).getSelection();
//				}
//				
//			});
//			
//			Button testCheckBox = new Button(gridComposite, SWT.CHECK);
//			testCheckBox.setText("Test class");
//			testCheckBox.setToolTipText("Generate basic Test class for your Dataset class");
//			testCheckBox.addListener(SWT.Selection, new Listener() {
//	
//				public void handleEvent(Event event) {
//					context.bTest = ((Button)event.widget).getSelection();
//				}
//				
//			});
		}
	}
	
	//private void createSqlTextControl(Composite parent) 
	private void createSqlTextControl() 
	{	
		if ( checkGridComposite() )
		{
			Composite gridComposite = getGridComposite();
			
			//--- Label		
			Label label= new Label(gridComposite, SWT.LEFT | SWT.WRAP );
			label.setFont(gridComposite.getFont());
			label.setText("Sql request :");
			label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
	        //--- Text area for SQL request
//			_requestText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			
			//Le gridData permet de formater le champs
//			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
//			data.widthHint = getMaxFieldWidth();
//		    data.heightHint = 200; 
//		    _requestText.setLayoutData(data);
//		    

			//_requestText.setLayoutData(Cell.HSpan2());
			_requestText = new Text(gridComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			GridData gd = new GridData();
			gd.heightHint = 200 ;
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = false;
			gd.horizontalSpan = 2;
		    _requestText.setLayoutData(gd);

		    _requestText.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event event) {
					updateStaticCritTypes();
				}
		    	
		    });

			Composite panel = null ;
			
			//--- Panel with the 2 buttons ( "Browse" & "Save" )
			panel = createPanelButtons(gridComposite);			

		}
	}
	
	private Composite createPanelButtons(Composite composite) 
	{		
		RowLayout panelLayout = new RowLayout(); // Vertical row ( = column )
		panelLayout.type = SWT.VERTICAL ;
		panelLayout.fill = true ; // same width for all controls
		panelLayout.pack = false ; // no pack for all controls size
		
		//--- Panel for buttons
		Composite panelBouton = new Composite(composite, SWT.NONE);
		//panelBouton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//panelBouton.setLayout(new GridLayout(1, false));
		panelBouton.setLayout( panelLayout );
		//panelBouton.setLayoutData(gd);
		
//		Color color = new Color( panelBouton.getDisplay(), 0xFF, 0, 0 );
//		panelBouton.setBackground(color);

		//--- 1rst button 
		Button buttonBrowse = new Button(panelBouton, SWT.PUSH);
		buttonBrowse.setText("Browse ...");
		////buttonBrowse.setLayoutData( gdHorizFill );
		//buttonBrowse.setLayoutData( gd );
		buttonBrowse.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				// OnClick => Action
				fileBrowse();
			}
		});
		
		//--- 2nd button 
		Button buttonSave = new Button(panelBouton, SWT.PUSH);
		buttonSave.setText("Save ...");
		////buttonBrowse.setLayoutData( gdHorizFill );
		//buttonBrowse.setLayoutData( gd );
		buttonSave.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				// OnClick => Action
				fileSave();
			}
		});
		return panelBouton ;
	}
	
	private Composite createTemplateChooserPanel(Composite composite) 
	{		
		
		//--- Panel 
		Composite panel = new Composite(composite, SWT.NONE);
		FillLayout panelLayout = new FillLayout(); // Horizontal row 
		//panelLayout.type = SWT.HORIZONTAL ;
		panelLayout.spacing = 10 ; // nb pixels between cells 
		panel.setLayout( panelLayout );
		
//		Color color = new Color( panel.getDisplay(), 0xFF, 0, 0 );
//		panel.setBackground(color);

        _comboTemplates = new Combo(panel, SWT.READ_ONLY);
        _comboTemplates.setItems(DatasetConst.ARRAY_TEMPLATES);
        _comboTemplates.select(0);
        _comboTemplates.setToolTipText("Select your Dataset class template");
		
        _comboTemplatesTest = new Combo(panel, SWT.READ_ONLY);
        _comboTemplatesTest.setItems(DatasetConst.ARRAY_TEMPLATES_TEST);
        _comboTemplatesTest.select(0);
        _comboTemplatesTest.setToolTipText("Select your test class template");
		
		return panel ;
	}
	
	//--------------------------------------------------------------------------------------------
	// Browse / Save SQL file 
	//--------------------------------------------------------------------------------------------
	private void fileBrowse()
	{
		FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setText("Select sql file");
        dialog.setFilterPath(_destinationPath);
        dialog.setFilterExtensions(new String[] {"*.sql"});
        
        String selectedFileName = dialog.open();

        if (selectedFileName != null) {
            setErrorMessage(null);
            _destinationPath = selectedFileName;
            loadSqlFile(selectedFileName);
        }
	}
	
	private void fileSave()
	{
		FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setText("Save request to sql file");
        dialog.setFilterExtensions(new String[] {"*.sql"});
        if (!_destinationPath.equals("")) {
        	dialog.setFileName(_destinationPath);
        }
        String selectedFileName = dialog.open();
        if (selectedFileName != null) {
        	try {
				FileWriter sqlFile = new FileWriter(selectedFileName);
				sqlFile.write(_requestText.getText());
				sqlFile.close();
				_destinationPath = selectedFileName;
			} catch (IOException e) {
				MsgBox.error("Error I/O");
			}
        }
	}
	
	//	Listener of the "browse..." button
	private class BrowseListener implements Listener {

		public BrowseListener() {
		}
		
		public void handleEvent(Event event) {
			FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
	        dialog.setText("Select sql file");
	        dialog.setFilterPath(_destinationPath);
	        dialog.setFilterExtensions(new String[] {"*.sql"});
	        
	        String selectedFileName = dialog.open();

	        if (selectedFileName != null) {
	            setErrorMessage(null);
	            _destinationPath = selectedFileName;
	            loadSqlFile(selectedFileName);
	        }
	        
			
		}
	}
	
	//Load SQL file in _requestText
	private void loadSqlFile(String selectedFileName) {
		try {
        	BufferedReader sqlFile = new BufferedReader(new FileReader(selectedFileName));
        	String sSql = "";
			String line = "";
			while((line=sqlFile.readLine()) != null) {
				sSql += line+"\n";
			}
			_requestText.setText(sSql);
			sqlFile.close();
        } catch (FileNotFoundException e) {
			MsgBox.error("File not found");
		} catch (IOException e) {
			MsgBox.error("Error I/O");
		}
	}

	//Listener of the Save Button
	private class SaveListener implements Listener {

		public SaveListener() {
		}
		
		public void handleEvent(Event event) {
			FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
	        dialog.setText("Save request to sql file");
	        dialog.setFilterExtensions(new String[] {"*.sql"});
	        if (!_destinationPath.equals("")) {
	        	dialog.setFileName(_destinationPath);
	        }
	        String selectedFileName = dialog.open();
	        if (selectedFileName != null) {
	        	try {
					FileWriter sqlFile = new FileWriter(selectedFileName);
					sqlFile.write(_requestText.getText());
					sqlFile.close();
					_destinationPath = selectedFileName;
				} catch (IOException e) {
					MsgBox.error("Error I/O");
				}
	        }
		}	
	}
	
	//private void createStaticCritTable(Composite parent) 
	private void createStaticCritTable() 
	{
		if ( checkGridComposite() )
		{
			Composite gridComposite = getGridComposite();
			
			//--- Label
	//		Label labelStaticCrit = new Label(parent,SWT.WRAP);
	//		labelStaticCrit.setText("Param types :");
			Label label= new Label(gridComposite, SWT.LEFT | SWT.WRAP );
			label.setFont(gridComposite.getFont());
			label.setText("Param types :");
			label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        
			//--- Table
	//		_tableStaticCrit = new DatasetStaticCritTable(parent, (NewDataSetWizard)getWizard());
	//		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
	//	    data.widthHint = 250;
	//	    data.heightHint = 100; 
	//	    _tableStaticCrit.setLayoutData(data);
		    
			_tableStaticCrit = new DatasetStaticCritTable(gridComposite);
			GridData gd = new GridData();
			gd.heightHint = 100 ;
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = false;
			gd.horizontalSpan = 2;
			_tableStaticCrit.setLayoutData(gd);
		    
		}
	}
	
	private void updateStaticCritTypes() {
		
		if(_iCurrentStaticCritNumber != analyzeRequest()) {
			//On ajoute les Critère statics
			for(int i = _iCurrentStaticCritNumber; i < analyzeRequest(); i++) {
				DatasetStaticCritTableItem item = new DatasetStaticCritTableItem(i+1,"",0);
				_tableStaticCrit.addInput(item);
			}
			
			//On supprime les critères static
			int lengthTable = _tableStaticCrit.getItems().length;
			if(lengthTable > analyzeRequest()) {
				for(int i = lengthTable-1; i >= analyzeRequest() ; i--) {
					_tableStaticCrit.removeInput(i);
				}
			}
		}
		_iCurrentStaticCritNumber = analyzeRequest();
	}
	
	private int analyzeRequest() {
		int iNbParameters = 0;
		int cpt = 0;
		while((cpt = _requestText.getText().indexOf("?",cpt)) != -1) {
			iNbParameters++;
			cpt++;
		}
		return iNbParameters;
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
	
//	private void doStatusUpdate() {
//		// status of all used components
//		IStatus[] status= new IStatus[] {
//			fContainerStatus,
//			fPackageStatus,
//			fTypeNameStatus
//		};
//		
//		// the mode severe status will be displayed and the OK button enabled/disabled.
//		updateStatus(status);
//	}

	public void specificFieldsChanged(int iFieldId, String sNewValue) {
		// Nop
		
	}

	public String getRequest() {
		return _requestText.getText();
	}
	
	public DatasetStaticCritTableItem[] getArrayCrit() {
		return _tableStaticCrit.getItems();
	}
	
	public String getSelectedTemplate() {
		return DatasetConst.ARRAY_TEMPLATES[_comboTemplates.getSelectionIndex()];
	}
	
	public String getSelectedTemplateTest() {
		return DatasetConst.ARRAY_TEMPLATES_TEST[_comboTemplatesTest.getSelectionIndex()];
	}
}
