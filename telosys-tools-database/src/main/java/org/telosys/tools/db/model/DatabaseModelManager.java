package org.telosys.tools.db.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.telosys.tools.commons.StandardTool;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.db.metadata.ColumnMetaData;
import org.telosys.tools.db.metadata.ForeignKeyColumnMetaData;
import org.telosys.tools.db.metadata.MetaDataManager;
import org.telosys.tools.db.metadata.PrimaryKeyColumnMetaData;
import org.telosys.tools.db.metadata.TableMetaData;

public class DatabaseModelManager extends StandardTool
{

	public DatabaseModelManager(TelosysToolsLogger logger) {
		super(logger);
	}

	public DatabaseTables getDatabaseTables(Connection con, String catalog, String schema, 
			String tableNamePattern, String[] tableTypes ) throws SQLException
	{
		DatabaseTables databaseTables = new DatabaseTables();
		
		MetaDataManager mgr = new MetaDataManager( this.getLogger() );
		
		//try {
			
			//--- Get the database Meta-Data
			DatabaseMetaData dbmd = con.getMetaData();		

			//--- Initialize the tables ( table, columns, PK, FK ) 
			List<TableMetaData> tablesMetaData = mgr.getTables(dbmd, catalog, schema, tableNamePattern, tableTypes);	
			
			//--- For each table get columns, primary key and foreign keys
//			Iterator iter = tablesMetaData.iterator() ;
//			while ( iter.hasNext() )
//			{
//				TableMetaData tableMetaData = (TableMetaData) iter.next();
			for ( TableMetaData tableMetaData : tablesMetaData ) {
				//--- Table columns
				List<ColumnMetaData> columnsMetaData = mgr.getColumns(dbmd, tableMetaData.getCatalogName(), tableMetaData.getSchemaName(), tableMetaData.getTableName() );

				//--- Table primary key columns
				List<PrimaryKeyColumnMetaData> pkColumnsMetaData = mgr.getPKColumns(dbmd, tableMetaData.getCatalogName(), tableMetaData.getSchemaName(), tableMetaData.getTableName() );

				//--- Table foreign keys columns
				List<ForeignKeyColumnMetaData> fkColumnsMetaData = mgr.getFKColumns(dbmd, tableMetaData.getCatalogName(), tableMetaData.getSchemaName(), tableMetaData.getTableName() );

				//--- Build the table model
				DatabaseTable databaseTable = new DatabaseTable(tableMetaData,columnsMetaData,pkColumnsMetaData,fkColumnsMetaData);
				
				//--- Set auto-incremented columns if any
				findAutoIncrementedColums(mgr, con, databaseTable);
				
				databaseTables.addTable(databaseTable);
			}
			
			//--- Initialize the stored procedures
			// in the future ...
			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return databaseTables ;
	}
	
	private void findAutoIncrementedColums( MetaDataManager mgr, Connection con, DatabaseTable databaseTable )
	{
		List<String> autoIncrColumns = null ;
		
		try {
			autoIncrColumns = mgr.getAutoIncrementedColumns(con, databaseTable.getSchemaName(), databaseTable.getTableName() );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// ERROR : cannot get autoincremented columns
		}
		
		if ( autoIncrColumns != null ) {
			if ( ! autoIncrColumns.isEmpty() ) {
//				Iterator it = autoIncrColumns.iterator();
//				while ( it.hasNext() )
//				{
//					String columnName = (String) it.next();
				for ( String columnName : autoIncrColumns ) {
					DatabaseColumn c = databaseTable.getColumnByName(columnName);
					if ( c != null ) {
						c.setAutoIncremented(true);
					}
				}
			}
		}
	}
}
