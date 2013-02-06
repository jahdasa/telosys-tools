package org.telosys.tools.db.model;

import org.telosys.tools.db.metadata.ForeignKeyColumnMetaData;

public class DatabaseForeignKeyColumn {

	private final ForeignKeyColumnMetaData foreignKeyColumnMetaData ;

	public DatabaseForeignKeyColumn(ForeignKeyColumnMetaData foreignKeyColumnMetaData) 
	{
		super();
		this.foreignKeyColumnMetaData = foreignKeyColumnMetaData;
	}

//	public int compareTo(Object obj) {
//		return foreignKeyColumnMetaData.compareTo(obj);
//	}
//
//	public boolean equals(Object arg0) {
//		return foreignKeyColumnMetaData.equals(arg0);
//	}

	public int getDeferrability() {
		return foreignKeyColumnMetaData.getDeferrability();
	}

	public int getDeleteRule() {
		return foreignKeyColumnMetaData.getDeleteRule();
	}

	public String getFkCatalogName() {
		return foreignKeyColumnMetaData.getFkCatalogName();
	}

	public String getFkColumnName() {
		return foreignKeyColumnMetaData.getFkColumnName();
	}

	public String getFkName() {
		return foreignKeyColumnMetaData.getFkName();
	}

	public String getFkSchemaName() {
		return foreignKeyColumnMetaData.getFkSchemaName();
	}

	public int getFkSequence() {
		return foreignKeyColumnMetaData.getFkSequence();
	}

	public String getFkTableName() {
		return foreignKeyColumnMetaData.getFkTableName();
	}

	public String getPkCatalogName() {
		return foreignKeyColumnMetaData.getPkCatalogName();
	}

	public String getPkColumnName() {
		return foreignKeyColumnMetaData.getPkColumnName();
	}

	public String getPkName() {
		return foreignKeyColumnMetaData.getPkName();
	}

	public String getPkSchemaName() {
		return foreignKeyColumnMetaData.getPkSchemaName();
	}

	public String getPkTableName() {
		return foreignKeyColumnMetaData.getPkTableName();
	}

	public int getUpdateRule() {
		return foreignKeyColumnMetaData.getUpdateRule();
	}

	public int hashCode() {
		return foreignKeyColumnMetaData.hashCode();
	}

	public String toString() {
		return foreignKeyColumnMetaData.toString();
	}
	
	
}
