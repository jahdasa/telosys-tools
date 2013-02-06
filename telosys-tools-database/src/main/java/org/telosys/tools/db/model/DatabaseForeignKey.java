package org.telosys.tools.db.model;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.db.metadata.ForeignKeyColumnMetaData;

public class DatabaseForeignKey 
{

	private final String foreignKeyName ;
	
	private LinkedList<DatabaseForeignKeyColumn> foreignKeyColumns = new LinkedList<DatabaseForeignKeyColumn>();

	public DatabaseForeignKey(String foreignKeyName, List<ForeignKeyColumnMetaData> fkColumnsMetaData ) 
	{
		super();
		
		//--- The name of the Foreign Key
		this.foreignKeyName = foreignKeyName;
		
		//--- The columns of the Foreign Key
		if ( fkColumnsMetaData != null )
		{
//			Iterator iter = fkColumnsMetaData.iterator() ;
//			while ( iter.hasNext() )
//			{
//				ForeignKeyColumnMetaData fkCol = (ForeignKeyColumnMetaData) iter.next();
			for ( ForeignKeyColumnMetaData fkCol : fkColumnsMetaData ) {
				if ( fkCol != null )
				{
					String name = fkCol.getFkName();
					if ( name != null )
					{
						if ( name.equalsIgnoreCase(foreignKeyName) )
						{
							DatabaseForeignKeyColumn dbFK = new DatabaseForeignKeyColumn(fkCol);
							foreignKeyColumns.addLast(dbFK);
						}
					}
				}
			}
		}
		
	}

	public String getForeignKeyName() {
		return foreignKeyName;
	}

	public List<DatabaseForeignKeyColumn> getForeignKeyColumns() {
		return foreignKeyColumns;
	}

}
