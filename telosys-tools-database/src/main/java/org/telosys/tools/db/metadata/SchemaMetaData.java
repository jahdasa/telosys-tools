package org.telosys.tools.db.metadata;


public class SchemaMetaData 
{
	private String schemaName ; // (may be null) 
	
	private String catalogName ; // (may be null) 
	
	//----------------------------------------------------------------------------------
	public SchemaMetaData( String catalogName, String schemaName) {
		super();
		this.catalogName = catalogName;
		this.schemaName = schemaName;
	}

	//----------------------------------------------------------------------------------
	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	
	//----------------------------------------------------------------------------------
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
}
