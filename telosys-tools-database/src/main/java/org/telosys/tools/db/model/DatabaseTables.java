package org.telosys.tools.db.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DatabaseTables implements Iterable<DatabaseTable>
{
	private LinkedList<DatabaseTable> tables = new LinkedList<DatabaseTable>();
	
	
	protected void addTable(DatabaseTable databaseTable )
	{
		tables.addLast(databaseTable);
	}
	
	public List<DatabaseTable> getTables()
	{
		return tables ;
	}
	
	public Iterator<DatabaseTable> iterator()
	{
		return tables.iterator();
	}
	
	public DatabaseTable getTableByName(String tableName)
	{
		if ( null == tableName ) throw new IllegalArgumentException("Table name is null");
//		Iterator iter = tables.iterator();
//		while ( iter.hasNext() )
//		{
//			DatabaseTable databaseTable = (DatabaseTable) iter.next();
		for ( DatabaseTable databaseTable : tables ) {
			if ( tableName.equals( databaseTable.getTableName() ) )
			{
				return databaseTable ;
			}
		}
		return null ;
	}
}
