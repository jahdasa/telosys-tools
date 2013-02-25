package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.repository.model.Entity;

/**
 * This class is the "Content Provider" for the "Table Viewer"
 * It contains the current entity that provides the table rows to display
 * 
 * @author Laurent Guerin
 *
 */
class TableViewerContentProvider implements IStructuredContentProvider 
{
	//private TableRowList _currentTableRowList = null ;
	private Entity _currentTableViewerEntity = null ;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{
		// This method is implemented in order to 
		// . verify the type of the input ( old and new )
		// . keep a reference on the input
		
		PluginLogger.log(this, "inputChanged("
				+ "viewer : " + ( viewer != null ? viewer.getClass().getName() : "null" )
				+ ", oldInput : " + ( oldInput != null ? oldInput.getClass().getName() : "null" )
				+ ", newInput : " + ( newInput != null ? newInput.getClass().getName() : "null" )
				+ ")...");
		
		//TableViewer tableViewer = null;
		if (viewer != null) 
		{
			if (viewer instanceof TableViewer != true) 
			{
				MsgBox.error("inputChanged(..) : viewer is not a TableViewer");
				return;
			}
			TableViewer tableViewer = (TableViewer) viewer;
			Table table = tableViewer.getTable(); 
			
			PluginLogger.log(this, "inputChanged(..,..,..) table.getItemCount() : " + table.getItemCount() );
			// At this moment the table contains the OLD rows ( new input not yet set )
		} 
		else 
		{
			MsgBox.error("inputChanged(..) : viewer is NULL !");
			return;
		}
		
		if (oldInput != null) 
		{
			//if (oldInput instanceof TableRowList != true) 
			if (oldInput instanceof Entity != true) 
			{
				//String msg = "inputChanged(..,..,..) : oldInput is not an instance of TableRowList" ;
				String msg = "inputChanged(..,..,..) : oldInput is not an instance of Entity" ;
				PluginLogger.log(this,msg);
				MsgBox.error(msg);
				return;
			}
		}
		
		if (newInput != null) 
		{
			//if (newInput instanceof TableRowList != true) 
			if (newInput instanceof Entity != true) 
			{
				//String msg = "inputChanged(..,..,..) : newInput is not an instance of TableRowList" ;
				String msg = "inputChanged(..,..,..) : newInput is not an instance of Entity" ;
				PluginLogger.log(this,msg);
				MsgBox.error(msg);
				return;
			}
			
			//_currentTableRowList = (TableRowList) newInput;
			_currentTableViewerEntity = (Entity) newInput;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		PluginLogger.log(this, "dispose(..,..,..)...");
		//_currentTableRowList = null ;
		_currentTableViewerEntity = null ;
	}

	//----------------------------------------------------------------------------------------------
	/* 
	 * IStructuredContentProvider implementation.
	 * 
	 * Returns the elements to display in the viewer when its input is set to the given element. 
	 * These elements can be presented as rows in a table, items in a list, etc. 
	 * The result is not modified by the viewer. 
	 * 
	 * Here returns the table rows 
	 */
	public Object[] getElements(Object parent) 
	{
		PluginLogger.log(this, "getElements(..)...");
		//return taskList.getTasks().toArray();

//		if ( _currentTableRowList != null )
//		{
//			return _currentTableRowList.getRows();
//		}
		if ( _currentTableViewerEntity != null )
		{
			return _currentTableViewerEntity.getColumns();
		}
		return new Object[0] ;
	}

	//		/* (non-Javadoc)
	//		 * @see ITaskListViewer#addTask(ExampleTask)
	//		 */
	//		public void addTask(ExampleTask task) {
	//			tableViewer.add(task);
	//		}
	//
	//		/* (non-Javadoc)
	//		 * @see ITaskListViewer#removeTask(ExampleTask)
	//		 */
	//		public void removeTask(ExampleTask task) {
	//			tableViewer.remove(task);
	//		}
	//
	//		/* (non-Javadoc)
	//		 * @see ITaskListViewer#updateTask(ExampleTask)
	//		 */
	//		public void updateTask(ExampleTask task) {
	//			tableViewer.update(task, null);
	//		}
	
	
}