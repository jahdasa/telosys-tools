package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.repository.model.Column;

/**
 * 
 *  
 * @author Laurent GUERIN
 *
 */
public class TableUtil {

	//-----------------------------------------------------------------------------

	//public static TableRow getTableRow( TableItem tableItem ) 
	public static Column getTableColumn( TableItem tableItem ) 
	{
		if ( tableItem != null )
		{
			Object oData = tableItem.getData();
			if ( oData == null )
			{
				MsgBox.error("TableUtil.getTableRow() : no data in the TableItem !");
				return null ;
			}
			//if ( oData instanceof TableRow != true )
			if ( oData instanceof Column != true )
			{
				//MsgBox.error("TableUtil.getTableRow() : TableItem data is not a TableRow !");
				MsgBox.error("TableUtil.getTableRow() : TableItem data is not a Column !");
				return null ;
			}
			//return (TableRow) oData ;
			return (Column) oData ;
		}
		else
		{
			MsgBox.error("TableUtil.getTableRow() : TableItem param is null !");
			return null ;
		}
	}
	
	public static Image getJavaNotNullImage( Column modelColumn ) 
	{
		if ( null == modelColumn ) {
			MsgBox.error("TableUtil.getJavaNotNullImage() : Column param is null !");
			return null ;
		}
		
		Image image = null ;
		if ( modelColumn.isJavaPrimitiveType() )
		{
			image = PluginImages.getImage(PluginImages.NOTNULL_ON);
		}
		else
		{
			if ( modelColumn.getJavaNotNull() ) {
				image = PluginImages.getImage(PluginImages.NOTNULL_ON);
			}
			else {
				image = PluginImages.getImage(PluginImages.NOTNULL_OFF);
			}
		}
		return image ;
	}
}
