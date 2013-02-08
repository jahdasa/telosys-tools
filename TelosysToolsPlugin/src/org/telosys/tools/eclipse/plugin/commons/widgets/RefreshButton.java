package org.telosys.tools.eclipse.plugin.commons.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;

public class RefreshButton {

	private final static int  BUTTON_HEIGHT =  26 ;
	private final static int  BUTTON_WIDTH  =  80 ;
	
	private final Button _button ;
	
	public RefreshButton(Composite parent) {
		super();

		_button = new Button(parent, SWT.NONE);
		_button.setText("Refresh");
		_button.setToolTipText("Reload targets from file");

		_button.setImage( PluginImages.getImage(PluginImages.REFRESH ) );
		
		_button.setLayoutData ( new GridData (BUTTON_WIDTH, BUTTON_HEIGHT) );
	}
	
	public Button getButton() {
		return _button ;
	}
	
	public void setEnabled(boolean enabled) {
		_button.setEnabled(enabled);
	}
	
	public void addSelectionListener(SelectionListener listener) {
		_button.addSelectionListener( listener ) ;
	}
}
