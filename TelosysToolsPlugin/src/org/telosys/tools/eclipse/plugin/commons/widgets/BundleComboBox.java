package org.telosys.tools.eclipse.plugin.commons.widgets;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.dialogbox.TemplateBundleUtil;
import org.telosys.tools.eclipse.plugin.editors.dbrep.RepositoryEditor;

public class BundleComboBox {

	private final static int COMBO_WIDTH  = 260 ;
	private final static int COMBO_HEIGHT =  24 ;
	private final static int COMBO_VISIBLE_ITEMS =  10 ;
	
	private final RepositoryEditor _editor ;
	private final Combo            _combo ;
	
	private String _selectedItem = "";
	
	public BundleComboBox(Composite parent, int initialItem, RepositoryEditor editor) {
		super();

		_editor = editor ; 
		
    	_combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		//combo.setSize(COMBO_WIDTH, COMBO_WIDTH);
//		GridData gdTableTargets = new GridData();
//		gdTableTargets.heightHint = 344 ;
//		gdTableTargets.widthHint  = 460 ;
		//_combo.setLayoutData( new RowData(COMBO_WIDTH, COMBO_HEIGHT));

		_combo.setVisibleItemCount(COMBO_VISIBLE_ITEMS); // Show a list of N items 

        _combo.addSelectionListener( new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent event)
            {
        		updateSelectedItem();
				if ( StrUtil.different( _selectedItem, _editor.getCurrentBundleName() )) {
					// only if the bundle name has changed : to avoid refresh (visual list effect) if unchanged
					_editor.setCurrentBundleName(_selectedItem);
					_editor.refreshAllTargetsTablesFromConfigFile();					
				}
            }
        });
        
		//--- Populate combo
        IProject eclipseProject = _editor.getProject() ;
        List<String> bundles = TemplateBundleUtil.getBundlesFromTemplatesFolder(eclipseProject);
		for ( String s : bundles ) {
			_combo.add(s); 
		}
		
//		//--- Select initial item 
//		if ( initialItem >= 0 && initialItem < bundles.size() ) {
//			_combo.select(initialItem);
//    		updateSelectedItem();
//		}
//		else {
//			MsgBox.error("Combobox creation error : invalid item " + initialItem );
//		}

	}
	
	public Combo getCombo() {
		return _combo ;
	}

	private void updateSelectedItem() {
		String[] items = _combo.getItems();
		_selectedItem = items[ _combo.getSelectionIndex() ] ;
	}	
	
	public String getSelectedItem() {
		return _selectedItem ;
	}
	
}
