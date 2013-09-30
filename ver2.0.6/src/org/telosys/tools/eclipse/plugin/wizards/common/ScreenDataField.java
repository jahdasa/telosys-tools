package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;

/**
 * @author Laurent GUERIN
 *  
 */
public class ScreenDataField implements ISelectedClassField  
{
//	private int _iFieldId = 0;

	//private IWizardPageEvents _wizardPageEvt = null;

	private LabelButtonField _field = null;

	private Shell _shell = null;

	private String _sMask = null;

	private IJavaProject _javaProject = null;

	//	private final static boolean isFieldValueOK ( String s )
	//	{
	//		// NOK
	//		if ( s != null )
	//		{
	//			String s2 = s.trim() ;
	//			if ( s2.length() > 0 )
	//			{
	//				// OK
	//			}
	//		}
	//	}

//	//======================================================================
//	// Listeners class for field events
//	//======================================================================
//	private class FieldEvents implements ModifyListener {
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
//		 */
//		public void modifyText(ModifyEvent e) {
//			PluginLogger.log("ScreenDataField : FieldEvents : modifyText() : '" + _field.getInputFieldText()+"'");
////			String s = _field.getInputFieldText();
////			if (s != null) {
////
////			}
////			_wizardPageEvt.specificFieldsChanged(_iFieldId);
//			_wizardPageEvt.specificFieldsChanged(_field.getId());
//		}
//	}

	//======================================================================
	// Listeners class for button events
	//======================================================================
	private class ButtonEvents implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			MsgBox.info("widgetDefaultSelected ");
		}

		public void widgetSelected(SelectionEvent e) {
			//MsgBox.info("widgetSelected ");
			if (_shell != null && _javaProject != null) {
				//String sMask = _sMask != null ? _sMask : "";
				IType type = DlgBox.selectClass(_shell, _javaProject, _sMask);
				if (type != null) {
					//String s = type.getElementName(); // Class name only
					String s = type.getFullyQualifiedName();
					//MsgBox.info("Class selected = " + type.getElementName());
					_field.setInputFieldText(s);
				} else {
					//MsgBox.info("No class selected.");
				}
			} else {
				String s1 = "";
				String s2 = "";
				if (_shell == null) {
					s1 = "Shell is null ";
				}
				if (_javaProject == null) {
					s1 = "JavaProject is null ";
				}
				MsgBox.error("Cannot open class selector : " + s1 + s2);
			}
		}
	};

	//======================================================================
	// The class itself
	//======================================================================
	public ScreenDataField(int iId, IWizardPageEvents wizardPageEvt,
			Shell shell, IJavaProject javaProject, String sMask) {
//		_iFieldId = iId ;
//		_wizardPageEvt = wizardPageEvt;
//		_field = new LabelButtonField(iId, "Screen Data :", "Browse ...",
//				wizardPageEvt, new FieldEvents(), new ButtonEvents());
		_field = new LabelButtonField(iId, "Screen Data :", "Browse ...",
				wizardPageEvt, new ButtonEvents());
		_shell = shell;
		_javaProject = javaProject;
		_sMask = sMask;
	}

	public void setInComposite(Composite composite, int nColumns) {
		_field.doFillIntoGrid(composite, nColumns);
	}

	public String getFieldValue() {
		return _field.getInputFieldText();
	}

	public void setFieldValue(String s) {
		_field.setInputFieldText(s);
	}

}