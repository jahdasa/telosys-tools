package org.telosys.tools.db.metadata;

public class ColumnMetaData 
{
	private String  catalogName ; // table catalog (may be null) 
	
	private String  schemaName ; // table schema (may be null) 
	
	private String  tableName ; // table name

	private String  columnName = "" ;
	
	private int     jdbcTypeCode = 0 ;
	
	private String  dbTypeName = "" ;
	
	private boolean notNull = false ;
	
	private int     size = 0 ;
	
	private int     decimalDigits = 0 ;
	
	private int     numPrecRadix = 0 ;
		
	private String  defaultValue ;  
	
	private int     charOctetLength = 0 ;
	
	private int     ordinalPosition = 0 ;
	
	private String  comment ; // explanatory comment on the table 
		
	
	//------------------------------------------------------------------------------
	public ColumnMetaData(
			String catalogName, String schemaName, String tableName,
			String columnName, int typeCode, String typeName, boolean notNull, int size, 
			int decimalDigits, int numPrecRadix,
			int charOctetLength, int ordinalPosition,
			String defaultValue,
			String comment) 
	{
		super();
		this.catalogName = catalogName;
		this.schemaName  = schemaName;
		this.tableName   = tableName;
		
		this.columnName   = columnName;
		this.jdbcTypeCode = typeCode;
		this.dbTypeName   = typeName;
		this.notNull      = notNull;
		this.size         = size;
		
		this.decimalDigits = decimalDigits ;
		this.numPrecRadix  = numPrecRadix ;
		
		this.charOctetLength = charOctetLength ;
		this.ordinalPosition = ordinalPosition ;
		
		this.defaultValue  = defaultValue ;
		this.comment      = comment;
	}

	//----------------------------------------------------------------------------------
	public String getCatalogName() {
		return catalogName;
	}
	
	//----------------------------------------------------------------------------------
	public String getSchemaName() {
		return schemaName;
	}

	//----------------------------------------------------------------------------------
	public String getTableName() {
		return tableName;
	}
	
	//------------------------------------------------------------------------------
	/**
	 * Returns the column size <br>
	 * For char or date types this is the maximum number of characters, <br>
	 * for numeric or decimal types this is precision
	 * 
	 * @return the column size (or 0 if not applicable)
	 */
	public int getSize() {
		return size;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the JDBC Type of the column (cf "java.sql.Types" )
	 * 
	 * @return
	 */
	public int getJdbcTypeCode() {
		return jdbcTypeCode;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the original database type 
	 * @return the database type (never null)
	 */
	public String getDbTypeName() {
		return dbTypeName;
	}
	
	//------------------------------------------------------------------------------
	/**
	 * Returns the name of the column in the database 
	 * @return the name ( never null )
	 */
	public String getColumnName() {
		return columnName;
	}

	//------------------------------------------------------------------------------
	public boolean isNotNull() {
		return notNull ;
	}
		
	//------------------------------------------------------------------------------
	/**
	 * Returns "true" if the column is "NOT NULL", else "false"
	 * 
	 * @return
	 */
	public String getNotNullAsString() {
		return ( notNull ? "true" : "false" ) ;
	}
	
	//------------------------------------------------------------------------------
	/**
	 * Returns the number of fractional digits
	 * @return
	 */
	public int getDecimalDigits() {
		return decimalDigits;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the default value (may be null) 
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the radix (typically either 10 or 2)
	 * @return
	 */
	public int getNumPrecRadix() {
		return numPrecRadix;
	}
	
	//------------------------------------------------------------------------------
	/**
	 * Returns for char types the maximum number of bytes in the column 
	 * @return
	 */
	public int getCharOctetLength() {
		return charOctetLength;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the index of the column in table (starting at 1)
	 * @return
	 */
	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	//------------------------------------------------------------------------------
	/**
	 * Returns the comment describing column (may be null) 
	 * @return
	 */
	public String getComment() {
		return comment;
	}

}
