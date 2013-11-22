package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.sql.Types;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.repository.model.Column;


/**
 * Label provider for the TableViewer
 *  
 * Provides the cell text or image for each column of each "row element" 
 * the return is set in the TableItem cell of the SWT table 
 * 
 * For each column :
 *  1. call getColumnText(Object element, int columnIndex)
 *  2. call getColumnImage(Object element, int columnIndex)
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
class TableViewerLabelProvider 	extends LabelProvider implements ITableLabelProvider
{
//	/**
//	 * Note: An image registry owns all of the image objects registered with it,
//	 * and automatically disposes of them the SWT Display is disposed.
//	 */ 
//	static {
//		String iconPath = "icons/"; 
//		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
//				TableViewerExample.class, 
//				iconPath + CHECKED_IMAGE + ".gif"
//				)
//			);
//		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
//				TableViewerExample.class, 
//				iconPath + UNCHECKED_IMAGE + ".gif"
//				)
//			);	
//	}
//	
//	/**
//	 * Returns the image with the given key, or <code>null</code> if not found.
//	 */
//	private Image getImage(boolean isSelected) {
//		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
//		return  imageRegistry.get(key);
//	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 * 
	 * Returns the label text for the given column of the given element.
	 * ( method called by the TableViewer )
	 * 
	 * Parameters: 
	 *  element - the object representing the entire row, 
	 *            or null indicating that no input object is set in the viewer 
	 *  columnIndex - the zero-based index of the column in which the label appears
	 *   
	 */
	public String getColumnText(Object element, int columnIndex) 
	{
		PluginLogger.log(this, "getColumnText(element, " + columnIndex + ")..." );
		//if ( element instanceof TableRow != true )
		if ( element instanceof Column != true )
		{
			MsgBox.error ( "getColumnText() : element is not a TableRow !" );
			return "??";
		}
		//TableRow row = (TableRow) element ;
		Column modelColumn = (Column) element ;
		switch (columnIndex) {
			case ColumnNames.SELECTED_INDEX :  
				//return "" ;
				return null ;
				
			case ColumnNames.PRIMARY_KEY_INDEX :  
				return null ;
				
			case ColumnNames.DB_NAME_INDEX :
				return modelColumn.getDatabaseName();
				
			case ColumnNames.DB_TYPE_INDEX : // SMALLINT, VARCHAR(n), DATE, ...
//				String sDbType = modelColumn.getDatabaseTypeName();
//				//--- If VARCHAR type add the size
//				int iJdbcType = modelColumn.getJdbcTypeCode() ;
//				if ( iJdbcType == Types.VARCHAR || iJdbcType == Types.CHAR )
//				{
//					sDbType = sDbType + "(" + modelColumn.getDatabaseSize() + ")" ;
//				}
//				return sDbType ;
				return modelColumn.getDatabaseTypeNameWithSize(); // v 2.0.7
				
			case ColumnNames.JDBC_TYPE_INDEX :
				return "" + modelColumn.getJdbcTypeCodeWithText();
				
			case ColumnNames.JAVA_NAME_INDEX :
				return modelColumn.getJavaName();
				
			case ColumnNames.JAVA_TYPE_INDEX :
				String sType = modelColumn.getJavaType(); // Java Type stored in the model 
				JavaTypes types = JavaTypesManager.getJavaTypes();
				String sText = types.getTextForType(sType);
				if ( sText != null )
				{
					return sText ; // The text to show in the table
				}
				MsgBox.error("Cannot found text for type " + sType );
				return "????";

			case ColumnNames.SPECIAL_INDEX :
				//return row.getSpecial();
				return modelColumn.getSpecialTypeInfo();
		}
		return "?";
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 * 
	 * Returns the label image for the given column of the given element.
	 * The image is put at the left of the text in the same cell.
	 * ( method called by the TableViewer )
	 * 
	 * Parameters: 
	 *  element - the object representing the entire row, 
	 *            or null indicating that no input object is set in the viewer
	 *  columnIndex - the zero-based index of the column in which the label appears
	 *  
	 * Returns:
	 *  Image or null if there is no image for the given object at columnIndex
	 * 
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		PluginLogger.log(this, "getColumnImage(element, " + columnIndex + ")..." );
		//if ( element instanceof TableRow != true )
		if ( element instanceof Column != true )
		{
			MsgBox.error ( "getColumnImage() : element is not a TableRow !" );
			return null ;
		}
		Column column = (Column) element ;
		switch (columnIndex) 
		{
		case ColumnNames.PRIMARY_KEY_INDEX : // Column for PK / FK images
//		if ( columnIndex == ColumnNames.PRIMARY_KEY_INDEX ) // Column for PK / FK images
//		{
//			Column column = (Column) element ;
			if ( column.isPrimaryKey() ) 
			{
				if ( column.isAutoIncremented() ) 
				{
					//--- Primary Key and Auto-incremented
					return PluginImages.getImage(PluginImages.PRIMARYKEY_AUTOINCR);
				}
				if ( column.isForeignKey() ) 
				{
					//--- Primary Key and Foreign Key
					return PluginImages.getImage(PluginImages.PRIMARYKEY_FK);
				}
				//--- Simple Primary Key
				return PluginImages.getImage(PluginImages.PRIMARYKEY);
			}
			else if ( column.isForeignKey() )
			{
				//--- Simple Foreign Key
				return PluginImages.getImage(PluginImages.FOREIGNKEY);
			}
			break;
//		}
		case ColumnNames.DB_TYPE_INDEX : // Column for "Database type" with "not null" image
//		else if ( columnIndex == ColumnNames.DB_TYPE_INDEX )  // Column for "Database type" with "not null" image
//		{
//			Column row = (Column) element ;
			if ( column.isDatabaseNotNull() )
			{
				return PluginImages.getImage(PluginImages.NOTNULL_ON);
			}
			else
			{
				return PluginImages.getImage(PluginImages.NOTNULL_OFF);
			}
//		}
		case ColumnNames.JAVA_TYPE_INDEX :
		//case ColumnNames.SPECIAL_INDEX :
//			if ( JavaTypeUtil.isPrimitiveType( column.getJavaType() ) )
//			{
//				return WizardImages.getImage(WizardImages.NOTNULL_ON);
//			}
			return null ; // No image
			
		case ColumnNames.SPECIAL_INDEX :
			return TableUtil.getJavaNotNullImage(column) ;
		}
		return null ; // No image
	}

}
