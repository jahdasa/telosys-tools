package org.telosys.tools.db.metadata;

public class PrimaryKeyColumnMetaData 
{

	private String  catalogName ; // table catalog (may be null) 
	
	private String  schemaName ; // table schema (may be null) 
	
	private String  tableName ; // table name

	private String  columnName ; // column name
	
	private short   pkSequence = 0 ; // sequence number within primary key 
	
	private String  pkName ; // primary key name (may be null) 

	
	//----------------------------------------------------------------------------------
	public PrimaryKeyColumnMetaData(String catalogName, String schemaName, String tableName, 
			String columnName, short pkSequence, String pkName) 
	{
		super();
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.tableName = tableName;
		
		this.columnName = columnName;
		this.pkSequence = pkSequence;
		this.pkName = pkName;
	}


	//----------------------------------------------------------------------------------
	public String getCatalogName() {
		return catalogName;
	}


	//----------------------------------------------------------------------------------
	public String getColumnName() {
		return columnName;
	}


	//----------------------------------------------------------------------------------
	public String getPkName() {
		return pkName;
	}


	//----------------------------------------------------------------------------------
	public short getPkSequence() {
		return pkSequence;
	}


	//----------------------------------------------------------------------------------
	public String getSchemaName() {
		return schemaName;
	}


	//----------------------------------------------------------------------------------
	public String getTableName() {
		return tableName;
	}
	

	
}
