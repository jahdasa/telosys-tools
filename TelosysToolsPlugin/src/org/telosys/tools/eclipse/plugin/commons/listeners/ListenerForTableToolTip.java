package org.telosys.tools.eclipse.plugin.commons.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.telosys.tools.generator.target.TargetDefinition;

public class ListenerForTableToolTip implements Listener {

	private final Table table;

	private Shell tip = null;

	private Label label = null;

	private TableItem currentTableItem = null;

	public ListenerForTableToolTip(Table table) {
		super();
		this.table = table;
	}

	/***
	@Override
	public void handleEvent(Event event) {

		final Shell shell = table.getShell();
		final Display display = shell.getDisplay();

		switch (event.type) {

		case SWT.Dispose:
		case SWT.KeyDown:
		case SWT.MouseMove: {
			if (tip == null)
				break;
			tip.dispose();
			tip = null;
			label = null;
			break;
		}

		case SWT.MouseHover: {
			TableItem item = table.getItem(new Point(event.x, event.y));
			if (item != null) {
				if (tip != null && !tip.isDisposed())
					tip.dispose();
				tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
				tip.setLayout(new FillLayout());
				label = new Label(tip, SWT.NONE);
				label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
//				label.setData("_TABLEITEM", item);
				label.setText("tooltip " + item.getText());
//				label.addListener(SWT.MouseExit, labelListener);
//				label.addListener(SWT.MouseDown, labelListener);
				Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				Rectangle rect = item.getBounds(0);
				Point pt = table.toDisplay(rect.x, rect.y);
				tip.setBounds(pt.x, pt.y, size.x, size.y);
				tip.setVisible(true);
			}
		}
		}

	}
	***/
	
	@Override
	public void handleEvent(Event event) {

		TableItem tableItem = getTableItem(event);
		//System.out.println("ListenerForTableToolTip" + );

		switch (event.type) {

		case SWT.Dispose:
		case SWT.KeyDown:
			disposeToolTip();
			break;

		case SWT.MouseMove: 
//			if ( tableItem != currentTableItem ) {
//				// Not on the same item
//				disposeToolTip(); 
//			}
			disposeToolTip(); 
			break;

		case SWT.MouseHover: 
//			if ( tableItem != currentTableItem ) {
//				// Not on the same item
//				showToolTip(event);
//			}
			showToolTip(event);
			break;
		}
		currentTableItem = tableItem ;
	}
	
	private TableItem getTableItem(Event event) {
		return table.getItem(new Point(event.x, event.y));
	}
	
	private void showToolTip(Event event) {
		final Shell shell = table.getShell();
		final Display display = shell.getDisplay();
		TableItem item = table.getItem(new Point(event.x, event.y));
		if (item != null) {
			if (tip != null && !tip.isDisposed())
				tip.dispose();
			
			//--- Info to be displayed
			String info = "( no target info )" ;
			Object data = item.getData();
			if ( data != null ) {
				if ( data instanceof TargetDefinition ) {
					TargetDefinition targetDefinition = (TargetDefinition) data ;
					info = 
						targetDefinition.getFolder() +
						" / " +
						targetDefinition.getFile() ;
				}
			}

			//--- Tooltip creation
			Color foregroundColor = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND) ;
			Color backgroundColor = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND) ;
			
			tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
			FillLayout fillLayout = new FillLayout();
			fillLayout.marginWidth = 8; // left/right
			fillLayout.marginHeight = 3; // top/bottom
			tip.setLayout(fillLayout);
			tip.setForeground(foregroundColor);
			tip.setBackground(backgroundColor);
			
			label = new Label(tip, SWT.NONE);
			label.setForeground(foregroundColor);
			label.setBackground(backgroundColor);
//			label.setData("_TABLEITEM", item);
			label.setText(info);
//			label.addListener(SWT.MouseExit, labelListener);
//			label.addListener(SWT.MouseDown, labelListener);
			Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Rectangle rect = item.getBounds(0);
			Point pt = table.toDisplay(rect.x, rect.y);
			tip.setBounds(pt.x+20, pt.y+12, size.x, size.y);
			tip.setVisible(true);
			
		}
	}
	
	private void disposeToolTip() {
		if ( tip != null ) {
			tip.dispose();
		}
		tip = null;
		label = null;
	}
}
