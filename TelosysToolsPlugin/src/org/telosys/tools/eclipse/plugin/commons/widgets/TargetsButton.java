package org.telosys.tools.eclipse.plugin.commons.widgets;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.commons.TargetUtil;

public class TargetsButton {

	private final static int  BUTTON_HEIGHT =  26 ;
	private final static int  BUTTON_WIDTH  =  80 ;
	
	private final IProject _project ;
	private final Button _button ;
	
	public TargetsButton(Composite parent, IProject project ) {
		super();

		_project = project ;
		
		_button = new Button(parent, SWT.NONE);
		_button.setText("Targets");
		_button.setToolTipText("Edit targets file");

		_button.setImage( PluginImages.getImage(PluginImages.TARGETS ) );
		
		_button.setLayoutData ( new GridData (BUTTON_WIDTH, BUTTON_HEIGHT) );
		
		_button.addSelectionListener( new SelectionListener() 
	    	{
	            public void widgetSelected(SelectionEvent arg0)
	            {
	            	//--- Standard behavior : open the targets/templates configuration file in text editor
	            	TargetUtil.openTargetsConfigFileInEditor(_project);
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
	
	public void addSelectionListener(SelectionListener listener) {
		_button.addSelectionListener( listener ) ;
	}
}
