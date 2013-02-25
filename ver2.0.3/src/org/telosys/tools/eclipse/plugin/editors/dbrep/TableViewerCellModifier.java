package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.sql.Types;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;
import org.telosys.tools.commons.JavaTypeUtil;
import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.repository.model.Column;

/**
 *
 */
class TableViewerCellModifier implements ICellModifier 
{
	RepositoryEditor _editor = null ;
	
	/**
	 * @param viewer
	 */
	public TableViewerCellModifier( RepositoryEditor editor ) {
		super();
		_editor = editor;
	}
	
	private void setDirty()
	{
		_editor.setDirty();
	}

	private void log(Object o, String s)
	{
		PluginLogger.log(o,s);
	}

	//private TableRow getTableRow( Object element )
	private Column getTableRow( Object element )
	{
		//if ( element instanceof TableRow )
		if ( element instanceof Column )
		{
			//return (TableRow) element ;
			return (Column) element ;
		}
		else
		{
			//MsgBox.error("TableViewerCellModifier.getTableRow(element) : the element is not an instance of TableRow !");
			MsgBox.error("TableViewerCellModifier.getTableRow(element) : the element is not an instance of Column !");
			return null ;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		log(this, "canModify(..," + property + ")..." );
		if ( ColumnNames.JAVA_NAME.equals(property) ) return true;
		if ( ColumnNames.JAVA_TYPE.equals(property) ) return true;
		if ( ColumnNames.SPECIAL.equals(property) ) 
		{
			//TableRow row = getTableRow( element );
			Column row = getTableRow( element );
			if ( row != null )
			{
//				String sJavaType = row.getJavaType() ;				
//				if ( "java.lang.String".equals(sJavaType) ) {
//					return true;
//				}
//				if ( "java.lang.Boolean".equals(sJavaType) ) {
//					return true;
//				}
//				if ( "boolean".equals(sJavaType) ) {
//					return true;
//				}
//				if ( "java.util.Date".equals(sJavaType) ) {
//					return true;
//				}
				return true ;
			}
		}
		return false ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property) 
	{
		log(this, "getValue(element,'" + property + "')..." );
		
		Column column = getTableRow( element );

		if ( column != null )
		{
			//--- "Java Name" property --> String ( Text field value )
			if ( ColumnNames.JAVA_NAME.equals(property) ) 
			{
				return column.getJavaName();
			}
			
			//--- "Java Type" property --> Integer ( ComboBox index ) 
			if ( ColumnNames.JAVA_TYPE.equals(property) )
			{
				String sJavaType = column.getJavaType();
				if ( sJavaType != null )
				{
					JavaTypes types = JavaTypesManager.getJavaTypes();
					int index = types.getTypeIndex( sJavaType );
					if ( index < 0 )
					{
						MsgBox.error("getValue(element, '" + property + "') : "
								+ "\n Cannot found index for type '" + sJavaType + "' !");
						return (new Integer(0));
					}
					else
					{
						return new Integer(index); // OK
					}
				}
				else
				{
					MsgBox.error("getValue(element, '" + property + "') : "
							+ "\n 'Java Type' value is null !");
				}
				return (new Integer(0));
				
			}

			//--- "Special" property --> String ( Text field value )
			if ( ColumnNames.SPECIAL.equals(property) ) 
			{
//				String sJavaType = column.getJavaType();
//				if ( sJavaType != null )
//				{
//					if ( "java.lang.String".equals(sJavaType) ) {
//						PluginLogger.log(this, "getValue(..," + property + ") : return SpecialValueForString" );
//						return new SpecialValueForString(column);
//					}
//					if (   "java.util.Date".equals(sJavaType)  
//						|| "java.sql.Date".equals(sJavaType) 
//						|| "java.sql.Time".equals(sJavaType) 
//						|| "java.sql.Timestamp".equals(sJavaType)  ) {
//						PluginLogger.log(this, "getValue(..," + property + ") : return SpecialValueForDate" );
//						return new SpecialValueForDate(column);
//					}
//					if ( "java.lang.Boolean".equals(sJavaType) ) {
//						PluginLogger.log(this, "getValue(..," + property + ") : return SpecialValueForBoolean" );
//						return new SpecialValueForBoolean(column);
//					}
//					if ( "boolean".equals(sJavaType) ) {
//						PluginLogger.log(this, "getValue(..," + property + ") : return SpecialValueForBoolean" );
//						return new SpecialValueForBoolean(column);
//					}
//				}
//				
				//--- There's 4 cases of types with special further informations
				if ( column.isJavaTypeString() ) {
					log(this, "getValue(..," + property + ") : return SpecialValueForString" );
					return new SpecialValueForString(column);
				}
				else if ( column.isJavaTypeDateOrTime() ) {
					log(this, "getValue(..," + property + ") : return SpecialValueForDate" );
					return new SpecialValueForDate(column);
				}
				else if ( column.isJavaTypeBoolean() ) {
					log(this, "getValue(..," + property + ") : return SpecialValueForBoolean" );
					return new SpecialValueForBoolean(column);					
				}
				else if ( column.isJavaTypeNumber() ) {
					log(this, "getValue(..," + property + ") : return SpecialValueForNumber" );
					return new SpecialValueForNumber(column);					
				}
				PluginLogger.log(this, "getValue(..," + property + ") : return 'NULL'" );
				return null ;
			}			
		}
		return "?" ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {
		log(this, "modify(element, '" + property + "', value)..." );

		if ( element instanceof TableItem != true )
		{
			MsgBox.error("modify(element, property, val) : the element is not an instance of TableItem !");
			return ;
		}

		TableItem tableItem = (TableItem) element ;
		//TableRow row = TableUtil.getTableRow(tableItem);
		Column modelColumn = TableUtil.getTableColumn(tableItem);
		
		//--- "Java Name" column
		if ( ColumnNames.JAVA_NAME.equals(property) ) 
		{
			log(this, "modify(..," + property + ", " + value + ") : 'Java Name' column " );
			String sNewValue = getString( value );
			
			//--- Has it realy change ?
			//String sOldValue = row.getJavaName() ;
			String sOldValue = modelColumn.getJavaName() ;
			if ( ! sNewValue.equals( sOldValue ) )
			{
				//--- Set the new value in the "model"  
				//row.setJavaName(sNewValue);
				modelColumn.setJavaName(sNewValue);
				//--- Set the new value in the "view" ( TableItem cell )  
				tableItem.setText(ColumnNames.JAVA_NAME_INDEX, sNewValue); 
				//tableItem.setImage( WizardImages.getImage(Const.SAMPLE_IMAGE));
				//tableItem.setImage( WizardImages.getImage(WizardImages.SAMPLE) );
				setDirty();
			}
		}
		
		//--- "Java Type" column
		if ( ColumnNames.JAVA_TYPE.equals(property) ) 
		{
			log(this, "modify(..," + property + ", " + value + ") : 'Java Type' column ( original value = '" + modelColumn.getJavaType() + "' )");
			String sNewInput = "???";
			int iChoice = getInt( value );

			JavaTypes types = JavaTypesManager.getJavaTypes();
			
			// The text to print in the "UI view"
			sNewInput = types.getText(iChoice);
			// The full Java type 
			String sNewFullType = types.getType(iChoice);
			log(this, "modify(..," + property + ", " + value + ") : Full type = " + sNewFullType );
			
			//--- Has it realy change ?
			String sOldFullType = modelColumn.getJavaType() ;
			if ( sNewFullType.equals( sOldFullType ) )
			{
				log(this, "modify(..," + property + ", " + value + ") : New value = Original value (no change) ");
				return ;
			}
			else
			{
				log(this, "modify(..," + property + ", " + value + ") : has changed '" + sOldFullType + "' -> '" + sNewFullType + "' ");
			}
	/***
			//--- Set the new value in the "model"  
			modelColumn.setJavaType(sNewFullType);
			
			//--- Impact on the "Special" column ?
			if ( "java.util.Date".equals(sNewFullType) ) 
			{
				//row.clearSpecial();
				modelColumn.clearSpecialTypeInfo();
				int t = modelColumn.getJdbcTypeCode();
				switch ( t )
				{
					case Types.DATE :
						//row.setDateType( JavaClassAttribute.DATE_ONLY );
						modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
						break;
					case Types.TIME :
						//row.setDateType( JavaClassAttribute.TIME_ONLY );
						modelColumn.setDateType( Column.SPECIAL_TIME_ONLY );
						break;
					case Types.TIMESTAMP :
						//row.setDateType( JavaClassAttribute.DATE_AND_TIME );
						modelColumn.setDateType( Column.SPECIAL_DATE_AND_TIME );
						break;
					default :
						//row.setDateType( JavaClassAttribute.DATE_ONLY );
						modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
						break;
				}
			}
			else if ( "java.lang.String".equals(sNewFullType) ) 
			{
				//row.clearSpecial();
				modelColumn.clearSpecialTypeInfo();
				int t = modelColumn.getJdbcTypeCode();
				if ( t == Types.LONGVARCHAR || t == Types.CLOB )
				{
					modelColumn.setLongText(true);
				}
			}
			else if ( "java.lang.Boolean".equals(sNewFullType) || "boolean".equals(sNewFullType) ) 
			{
				if ( "java.lang.Boolean".equals(sOldFullType) || "boolean".equals(sOldFullType) ) 
				{
					// Still a boolean => keep the special value
					// Nothing to do
				}
				else
				{
					//row.clearSpecial();
					modelColumn.clearSpecialTypeInfo();
					int t = modelColumn.getJdbcTypeCode();
					if ( t != Types.BOOLEAN )
					{
						modelColumn.setBooleanTrueValue("1");
						modelColumn.setBooleanFalseValue("0");
					}
				}
			}
			else
			{
				//row.clearSpecial();
				modelColumn.clearSpecialTypeInfo();
			}
	****/
			updateJavaTypeInModel( modelColumn, sNewFullType );
			
			//--- Set the new value in the "view" ( TableItem cell )  
			tableItem.setText(ColumnNames.JAVA_TYPE_INDEX, sNewInput); 
			
			//tableItem.setText(ColumnNames.SPECIAL_INDEX, modelColumn.getSpecialTypeInfo() );
			refreshSpecialColumn( tableItem );

			setDirty();
		}
		
		//--- "Special" property --> String ( Text field value )
		if ( ColumnNames.SPECIAL.equals(property) ) 
		{
			log(this, "modify(..," + property + ", " + value + ") : 'Special' column " );
			if ( value != null )
			{
				if ( value instanceof SpecialValue )
				{
					SpecialValue specialValue = (SpecialValue) value ;
					if ( specialValue.hasChanged() )
					{
						//--- Set the new value in the "view" ( TableItem cell )  
						//tableItem.setText(ColumnNames.SPECIAL_INDEX, specialValue.toString() ); 
						refreshSpecialColumn( tableItem );
						setDirty();
					}
				}
				else
				{
					// Happends for types without special values : byte[], Blob, Clob
					// Its not an error
					// MsgBox.error("TableViewerCellModifier.modify() : value is not an instance of SpecialValue");
				}
			}
			else
			{
				log(this, "modify(..," + property + ", " + value + ") : 'Special' column : value is NULL => no change" );
				//--- Set the same text
				String s = tableItem.getText(ColumnNames.SPECIAL_INDEX ); 
				tableItem.setText(ColumnNames.SPECIAL_INDEX, s ); 
			}
		}
	}
	
	private void refreshSpecialColumn( TableItem tableItem )
	{
		Column modelColumn = TableUtil.getTableColumn(tableItem);
		if ( modelColumn != null ) 
		{
			tableItem.setText(ColumnNames.SPECIAL_INDEX, modelColumn.getSpecialTypeInfo() );
//			Image image = null ;
//			if ( modelColumn.isJavaPrimitiveType() )
//			{
//				image = WizardImages.getImage(WizardImages.NOTNULL_ON);
//			}
//			else
//			{
//				if ( modelColumn.getJavaNotNull() ) {
//					image = WizardImages.getImage(WizardImages.NOTNULL_ON);
//				}
//				else {
//					image = WizardImages.getImage(WizardImages.NOTNULL_OFF);
//				}
//			}
			Image image = TableUtil.getJavaNotNullImage(modelColumn) ;
			tableItem.setImage(ColumnNames.SPECIAL_INDEX, image );
		}
	}
	
	private String getString( Object oValue )
	{
		if ( oValue instanceof String )
		{
			return (String) oValue ;
		}
		else
		{
			MsgBox.error("ERROR in modify(element, property, value) : "
					+ "\n The value is not an instance of String !"
					+ "\n Class is " + oValue.getClass().getName() );
			return "?" ;
		}
	}

	private int getInt( Object oValue )
	{
		log(this, "getInt(" + oValue + ")");
		if ( oValue instanceof Integer )
		{
			Integer integer = (Integer) oValue ;
			int v = integer.intValue();
			log(this, "getInt(" + oValue + ") : " + v );
			return v ;
		}
		else
		{
			MsgBox.error("ERROR in modify(element, property, value) : "
					+ "\n The value is not an instance of Integer !"
					+ "\n Class is " + oValue.getClass().getName() );
			return 0 ;
		}
	}
	
	/**
	 * Update the model with the new Java Type after a user change
	 * ( update the javaType and special info if necessary )
	 * @param modelColumn
	 * @param sNewFullType
	 */
	private void updateJavaTypeInModel( Column modelColumn, String sNewFullType )
	{
		//--- Set the original value in the "model"  
		String sOldFullType = modelColumn.getJavaType() ;

		//--- Set the new value in the "model"  
		modelColumn.setJavaType(sNewFullType);
		
		if ( JavaTypeUtil.getCategory(sOldFullType) != JavaTypeUtil.getCategory(sNewFullType) ) 
		{
			//--- Not the same type category => Reset the values
			resetFurtherInfo( modelColumn );
		}
		else if ( JavaTypeUtil.isCategoryDateOrTime(sNewFullType) ) 
		{
			//--- Same type category but DATE or TIME or DATE+TIME => Reset the values
			resetFurtherInfo( modelColumn );
		}
		// ELSE : keep the same values 
		
//		//--- Impact on the "Special" column ?
//		if ( "java.util.Date".equals(sNewFullType) ) 
//		{
//			modelColumn.clearSpecialTypeInfo();
//			int t = modelColumn.getJdbcTypeCode();
//			switch ( t )
//			{
//				case Types.DATE :
//					modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
//					break;
//				case Types.TIME :
//					modelColumn.setDateType( Column.SPECIAL_TIME_ONLY );
//					break;
//				case Types.TIMESTAMP :
//					modelColumn.setDateType( Column.SPECIAL_DATE_AND_TIME );
//					break;
//				default :
//					modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
//					break;
//			}
//		}
//		else if ( "java.lang.String".equals(sNewFullType) ) 
//		{
//			modelColumn.clearSpecialTypeInfo();
//			int t = modelColumn.getJdbcTypeCode();
//			if ( t == Types.LONGVARCHAR || t == Types.CLOB )
//			{
//				modelColumn.setLongText(true);
//			}
//		}
//		else if ( "java.lang.Boolean".equals(sNewFullType) || "boolean".equals(sNewFullType) ) 
//		{
//			if ( "java.lang.Boolean".equals(sOldFullType) || "boolean".equals(sOldFullType) ) 
//			{
//				// Still a boolean => keep the special value
//				// Nothing to do
//			}
//			else
//			{
//				modelColumn.clearSpecialTypeInfo();
//				int t = modelColumn.getJdbcTypeCode();
//				if ( t != Types.BOOLEAN )
//				{
//					modelColumn.setBooleanTrueValue("1");
//					modelColumn.setBooleanFalseValue("0");
//				}
//			}
//		}
//		else
//		{
//			modelColumn.clearSpecialTypeInfo();
//		}
	}
	
	private void resetFurtherInfo( Column modelColumn )
	{
		modelColumn.clearSpecialTypeInfo();

		String sFullJavaType = modelColumn.getJavaType() ;
		int jdbcCode = modelColumn.getJdbcTypeCode();
		
		//--- Init with the default values depending on the Java Type and the JDBC Code
		if ( "java.util.Date".equals(sFullJavaType) ) 
		{
			switch ( jdbcCode )
			{
				case Types.DATE :
					modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
					break;
				case Types.TIME :
					modelColumn.setDateType( Column.SPECIAL_TIME_ONLY );
					break;
				case Types.TIMESTAMP :
					modelColumn.setDateType( Column.SPECIAL_DATE_AND_TIME );
					break;
				default :
					modelColumn.setDateType( Column.SPECIAL_DATE_ONLY );
					break;
			}
		}
		//else if ( "java.lang.String".equals(sFullJavaType) ) 
		else if ( modelColumn.isJavaTypeString() ) 
		{
			if ( jdbcCode == Types.LONGVARCHAR || jdbcCode == Types.CLOB )
			{
				modelColumn.setLongText(true);
			}
		}
		//else if ( "java.lang.Boolean".equals(sFullJavaType) || "boolean".equals(sFullJavaType) ) 
		else if ( modelColumn.isJavaTypeBoolean() ) 
		{
			if ( jdbcCode != Types.BOOLEAN )
			{
				modelColumn.setBooleanTrueValue("1");
				modelColumn.setBooleanFalseValue("0");
			}
		}
	}
}
