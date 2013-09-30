package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

/**
 * Generic class for the composite widget Label + Field + Button 
 * @author Laurent GUERIN
 * 
 */
public class LabelButtonField {

	private final static int NUMBER_OF_CONTROLS = 3 ;
	
	private int _iFieldId = 0;
	private IWizardPageEvents _wizardPageEvt = null;

	//--- The 3 controls of the component :
	private Label  _label ;
	private String _labelText = "..." ;

	private Text   _inputField ; 
	private String _inputFieldText = "" ; 	
	private ModifyListener _inputFieldListener = null ;
	
	private Button _button ;
	private String _buttonText = "..." ;
	private SelectionListener _buttonListener = null;

	public LabelButtonField( int iId, String labelText, String buttonText, IWizardPageEvents wizardPageEvt, ModifyListener fieldListener, SelectionListener buttonListener ) {
		super();
		_iFieldId = iId ;
		_labelText = labelText ;
		_buttonText = buttonText ;
		
		_wizardPageEvt = wizardPageEvt;
		_buttonListener = buttonListener ;
		_inputFieldListener = fieldListener ;
	}

	public LabelButtonField( int iId, String labelText, String buttonText, IWizardPageEvents wizardPageEvt, SelectionListener buttonListener ) {
		super();
		_iFieldId = iId ;
		_labelText = labelText ;
		_buttonText = buttonText ;
		
		_wizardPageEvt = wizardPageEvt;
		_buttonListener = buttonListener ;
		_inputFieldListener = new FieldEvents() ; // Use the default listener
	}

	/**
	 * Returns the ID of this widget 
	 * @return
	 */
	public int getId() {
		return _iFieldId;
	}
	
	/**
	 * Sets the text of the label.
	 */
	public void setLabelText(String s) {
		_labelText = s;
		if ( _label != null )
		{
			_label.setText(_labelText);
		}
	}

	/**
	 * Sets the text of the input field.
	 */
	public void setInputFieldText(String s) {
		_inputFieldText = s;
		if ( _inputField != null )
		{
			_inputField.setText(_inputFieldText);
		}
	}
	
	/**
	 * Returns the text of the input field (or null if the control is not yet created).
	 */
	public String getInputFieldText() {
		if ( _inputField != null )
		{
			return _inputField.getText();
		}
		return null ;
	}
	
	/**
	 * Sets the text of the button.
	 */
	public void setButtonText(String s) {
		_buttonText = s;
		if ( _button != null )
		{
			_button.setText(_buttonText);
		}
	}

	// ------ adapter communication

	//	/**
	//	 * Programmatical pressing of the button
	//	 */
	//	public void changeControlPressed() {
	//		fStringButtonAdapter.changeControlPressed(this);
	//	}

	// ------- layout helpers

	/*
	 * @see DialogField#doFillIntoGrid
	 */
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);

		Label label = getLabelControl(parent); // Create Label Control
		label.setLayoutData(gridDataForLabel(1));

		Text text = getTextControl(parent); // Create Text Control
		text.setLayoutData(gridDataForText(nColumns - 2));

		Button button = getButtonControl(parent); // Create Button Control
		button.setLayoutData(gridDataForButton(button, 1));

		return new Control[] { label, text, button };
	}

	//============================================================================================
	private static GridData gridDataForLabel(int span) {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = span;
		return gd;
	}

	private static GridData gridDataForText(int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		return gd;
	}

	private static GridData gridDataForButton(Button button, int span) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
//		gd.heightHint = SWTUtil.getButtonHeightHint(button);
//		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		return gd;
	}

	//============================================================================================
	// ------- ui creation

	private Label getLabelControl(Composite parent) {
		//		if (fLabel == null) {
		//			assertCompositeNotNull(parent);
		//			
		//			fLabel= new Label(parent, SWT.LEFT | SWT.WRAP);
		//			fLabel.setFont(parent.getFont());
		//			fLabel.setEnabled(fEnabled);
		//			if (fLabelText != null && !"".equals(fLabelText)) { //$NON-NLS-1$
		//				fLabel.setText(fLabelText);
		//			} else {
		//				// to avoid a 16 pixel wide empty label - revisit
		//				fLabel.setText("."); //$NON-NLS-1$
		//				fLabel.setVisible(false);
		//			}
		//		}
		//		return fLabel;
		if (_label == null) {
			assertCompositeNotNull(parent);
			_label= new Label(parent, SWT.LEFT | SWT.WRAP);
			_label.setFont(parent.getFont());
			_label.setText(_labelText);
		}
		return _label ;
	}

	private Text getTextControl(Composite parent) {
		//		if (fTextControl == null) {
		//			assertCompositeNotNull(parent);
		//			fModifyListener= new ModifyListener() {
		//				public void modifyText(ModifyEvent e) {
		//					doModifyText(e);
		//				}
		//			};
		//			
		//			fTextControl= new Text(parent, SWT.SINGLE | SWT.BORDER);
		//			// moved up due to 1GEUNW2
		//			fTextControl.setText(fText);
		//			fTextControl.setFont(parent.getFont());
		//			fTextControl.addModifyListener(fModifyListener);
		//			
		//			fTextControl.setEnabled(isEnabled());
		//			if (fContentAssistProcessor != null) {
		//			    ControlContentAssistHelper.createTextContentAssistant(fTextControl,
		// fContentAssistProcessor);
		//			}
		//		}
		//		return fTextControl;
		
		if ( _inputField == null) {
			assertCompositeNotNull(parent);
			_inputField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			_inputField.setFont(parent.getFont());
			
			if ( _inputFieldListener != null )
			{
				_inputField.addModifyListener(_inputFieldListener);
			}
		}
		return _inputField ;
	}

	/**
	 * Creates or returns the created buttom widget.
	 * 
	 * @param parent
	 *            The parent composite or <code>null</code> if the widget has
	 *            already been created.
	 */
	private Button getButtonControl(Composite parent) {
		if (_button == null) {
			assertCompositeNotNull(parent);

			_button = new Button(parent, SWT.PUSH);
			_button.setText(_buttonText);
//			fBrowseButton.setEnabled(isEnabled() && fButtonEnabled);
//			fBrowseButton.addSelectionListener(new SelectionListener() {
//				public void widgetDefaultSelected(SelectionEvent e) {
//					changeControlPressed();
//				}
//
//				public void widgetSelected(SelectionEvent e) {
//					changeControlPressed();
//				}
//			});
			if ( _buttonListener != null )
			{
				_button.addSelectionListener( _buttonListener ) ;
			}
		}
		return _button;
	}

	//============================================================================================

	// ------ enable / disable management

	/**
	 * Enables the button.
	 */
	public void enableButton() {
		if ( _button != null) _button.setEnabled(true);
	}
	
	/**
	 * Disables the button.
	 */
	public void disableButton() {
		if ( _button != null) _button.setEnabled(false);
	}

	private final void assertCompositeNotNull(Composite comp) {
		Assert.isNotNull(comp,
				"uncreated control requested with composite null"); 
	}

	private final void assertEnoughColumns(int nColumns) {
		Assert.isTrue(nColumns >= NUMBER_OF_CONTROLS,
				"given number of columns is too small"); 
	}
	
	//======================================================================
	// Listeners class for field events
	//======================================================================
	private class FieldEvents implements ModifyListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			PluginLogger.log("LabelButtonField : Listener.ModifyText() : " +  _iFieldId + " '"+ _inputField.getText() +"'");
			if ( _wizardPageEvt != null )
			{
				//_wizardPageEvt.specificFieldsChanged(_iFieldId, _inputField.getText());
				_wizardPageEvt.specificFieldsChanged(_iFieldId, _inputField.getText());
			}
		}
	}
}