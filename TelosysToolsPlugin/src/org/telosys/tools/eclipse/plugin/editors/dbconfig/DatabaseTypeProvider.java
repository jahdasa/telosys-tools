package org.telosys.tools.eclipse.plugin.editors.dbconfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseTypeProvider {

	private final static List<DatabaseType>        dbTypesList = new LinkedList<DatabaseType>();
	private final static Map<String, DatabaseType> dbTypesMap  = new HashMap<String, DatabaseType>();
	
	static {
		// NB : the type name must be unique (used as key in map)
		
		dbTypesList.add( new DatabaseType(
				"DERBY",       
				"org.apache.derby.jdbc.ClientDriver", 
				"jdbc:derby://localhost:1527/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"H2 embedded", 
				"org.h2.Driver",
				"jdbc:h2:mem",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"H2 server",   
				"org.h2.Driver",
				"jdbc:h2:tcp://localhost/~/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"HSQL-DB",     
				"org.hsqldb.jdbcDriver",
				"jdbc:hsqldb:hsql://localhost:9001/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"INGRES",       
				"com.ingres.jdbc.IngresDriver",
				"jdbc:ingres://localhost:117/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"InterSystems CacheDB",       
				"com.intersys.jdbc.CacheDriver",
				"jdbc:Cache://localhost:1972/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"MYSQL",       
				"com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost:3306/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"ORACLE",      
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@localhost:1521:DB_NAME",
				"!") ) ; // null for Catalog
		
		dbTypesList.add( new DatabaseType(
				"POSTGRESQL",      
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost:5432/DB_NAME",
				"") ) ;
		
		dbTypesList.add( new DatabaseType(
				"SQL-SERVER",  
				"com.microsoft.sqlserver.jdbc.SQLServerDriver",
				"jdbc:sqlserver://localhost:1433;databaseName=DB_NAME",
				"") ) ;
		
		for ( DatabaseType t : dbTypesList ) {
			dbTypesMap.put(t.getTypeName(), t);
		}
	}

	public final static List<DatabaseType> getDbTypesList() {
		return dbTypesList ;
	}
	
	public final static Map<String,DatabaseType> getDbTypesMap() {
		return dbTypesMap ;
	}

	public final static DatabaseType getDbType(String typeName) {
		return dbTypesMap.get(typeName) ;
	}
}
