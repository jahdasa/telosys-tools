package org.telosys.tools.db.metadata;


public class TableMetaData 
{
	private String tableName ;
	
	// Typical types are : 
	// "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". 
	private String tableType ;
	
	private String catalogName ; // (may be null) 
	
	private String schemaName ; // (may be null) 
	
	private String comment ; // explanatory comment on the table 

	//private Hashtable columnsMetaData = new Hashtable() ; 

	//private Hashtable foreignKeysMetaData = new Hashtable() ;

	// private PrimaryKeyMetaData primaryKeyMetaData ;

	
	//----------------------------------------------------------------------------------
	public TableMetaData(String tableName, String tableType, String catalogName, String schemaName, String comment) {
		super();
		this.tableName = tableName;
		this.tableType = tableType;
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.comment = comment;
	}

	//----------------------------------------------------------------------------------
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	//----------------------------------------------------------------------------------
	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	
	//----------------------------------------------------------------------------------
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	

	//----------------------------------------------------------------------------------
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	


	//----------------------------------------------------------------------------------
	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	

	
}
