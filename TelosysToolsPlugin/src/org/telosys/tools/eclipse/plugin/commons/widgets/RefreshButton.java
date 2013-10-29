package org.telosys.tools.eclipse.plugin.commons.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.editors.dbrep.RepositoryEditor;

public class RefreshButton {

	private final static int  BUTTON_HEIGHT =  26 ;
	private final static int  BUTTON_WIDTH  =  74 ; // 80 ;
	
	private final Button           _button ;
	private final RepositoryEditor _editor ;
	
	public RefreshButton(Composite parent, RepositoryEditor editor) {
		super();

		_editor = editor ; // v 2.0.7
		
		_button = new Button(parent, SWT.NONE);
		_button.setText("Refresh");
		_button.setToolTipText("Reload targets from file");

		_button.setImage( PluginImages.getImage(PluginImages.REFRESH ) );
		
		_button.setLayoutData ( new GridData (BUTTON_WIDTH, BUTTON_HEIGHT) );
		
		_button.addSelectionListener(new SelectionListener()  // v 2.0.7
		{
	        public void widgetSelected(SelectionEvent arg0)
	        {
	        	//--- Reload the targets list
	        	_editor.refreshAllTargetsTablesFromConfigFile();
	        }
	        public void widgetDefaultSelected(SelectionEvent arg0)
	        {
	        }
	    });
		
	}
	
	public Button getButton() {
		return _button ;
	}
	
	public void setEnabled(boolean enabled) {
		_button.setEnabled(enabled);
	}
	
//	public void addSelectionListener(SelectionListener listener) {
//		_button.addSelectionListener( listener ) ;
//	}
}
