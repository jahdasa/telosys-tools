package org.telosys.tools.db.metadata;

/**
 * Foreign Key part (column).
 *  
 * A part of a Foreign Key definition ( one of the 1..N columns defining a foreign key )
 * 
 * Must implement Comparable ( to be sorted by FK name ) 
 * 
 * @author Eric LEMELIN, Laurent GUERIN
 *
 */
public class ForeignKeyColumnMetaData implements Comparable<ForeignKeyColumnMetaData> 
{
	
	//--- 1..4 : Primary Key Column
	private String pkCatalogName ; // (may be null) 	
	private String pkSchemaName ; // (may be null) 
	private String pkTableName ;
	private String pkColumnName ;
	
	//--- 5..8 : Foreign Key Column
	private String fkCatalogName ; // (may be null) 	
	private String fkSchemaName ; // (may be null) 
	private String fkTableName ;
	private String fkColumnName ;
	
	//--- 9 : KEY_SEQ : short : sequence number within a foreign key 
	private short fkSequence ;

	//--- 10 : UPDATE_RULE : short => What happens to a foreign key when the primary key is updated: 
	private short updateRule ;
	
	//--- 11 : DELETE_RULE : short => What happens to the foreign key when primary is deleted. 
	private short deleteRule ;
	
	//--- 12 : FK_NAME : String => foreign key name (may be null) 
	private String fkName ; // (may be null)

	//--- 13 : PK_NAME : String => primary key name (may be null)
	private String pkName ; // (may be null)
	
	//--- 14 : DEFERRABILITY : short => can the evaluation of foreign key constraints be deferred until commit 
	private short deferrability ;

	//--------------------------------------------------------------------------------------------
	public ForeignKeyColumnMetaData(
			String pkCatalogName, String pkSchemaName, String pkTableName, String pkColumnName,
			String fkCatalogName, String fkSchemaName, String fkTableName, String fkColumnName,
			short fkSequence, short updateRule, short deleteRule,
			String fkName, String pkName, short deferrability
			) 
	{
		super();
		
		this.pkCatalogName = pkCatalogName ;
		this.pkSchemaName = pkSchemaName ;
		this.pkTableName = pkTableName ;
		this.pkColumnName = pkColumnName ;
		
		this.fkCatalogName = fkCatalogName ;
		this.fkSchemaName = fkSchemaName ;
		this.fkTableName = fkTableName ;
		this.fkColumnName = fkColumnName ;
		
		this.fkSequence = fkSequence ;
		this.updateRule = updateRule ;
		this.deleteRule = deleteRule ;
		
		this.fkName = fkName ;
		this.pkName = pkName ;
		this.deferrability = deferrability ;
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * @return the _fkName
	 */
	public String getFkName() {
		return fkName;
	}

	public String getPkName() {
		return pkName;
	}

	public int getFkSequence() {
		return fkSequence;
	}

	//--------------------------------------------------------------------------------------------
	public String getFkCatalogName() {
		return fkCatalogName;
	}
	public String getFkSchemaName() {
		return fkSchemaName;
	}
	public String getFkTableName() {
		return fkTableName;
	}
	public String getFkColumnName() {
		return fkColumnName;
	}

	//--------------------------------------------------------------------------------------------
	public String getPkCatalogName() {
		return pkCatalogName;
	}
	public String getPkSchemaName() {
		return pkSchemaName;
	}
	public String getPkTableName() {
		return pkTableName;
	}
	public String getPkColumnName() {
		return pkColumnName;
	}

	//--------------------------------------------------------------------------------------------
	
	public int getUpdateRule() {
		return updateRule;
	}

	public int getDeleteRule() {
		return deleteRule;
	}

	public int getDeferrability() {
		return deferrability;
	}

	//--------------------------------------------------------------------------------------------
//	public int compareTo(Object obj) {
//		ForeignKeyColumnMetaData fk = (ForeignKeyColumnMetaData) obj;
	public int compareTo(ForeignKeyColumnMetaData fk) {
		
		//return fkName.compareTo(fk.getFkName());
		String thisFkName = ( this.fkName != null ? this.fkName : "" );
		String otherFkName = ( fk.getFkName() != null ? fk.getFkName() : "" );		
		return thisFkName.compareTo(otherFkName);
	}

}