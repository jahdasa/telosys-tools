/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.telosys.tools.commons.StandardTool;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.config.ClassNameProvider;
import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;
import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseForeignKeyColumn;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.repository.config.InitializerChecker;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.ForeignKey;
import org.telosys.tools.repository.model.ForeignKeyColumn;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * Abstract repository manager ancestor<br>
 * Common functions for repository "generator" and "updator" <br>
 * 
 * @author Sylvain LEROY, Laurent GUERIN, Eric LEMELIN
 * 
 */

public abstract class RepositoryManager extends StandardTool
{
	protected InitializerChecker _inichk = null;
	
	protected ClassNameProvider  _classNameProvider = null;
	
	protected TelosysToolsLogger            _logger = null;


	public RepositoryManager(InitializerChecker inichk, ClassNameProvider classNameProvider, TelosysToolsLogger logger) 
	{
		super(logger);
		_inichk = inichk;
		_classNameProvider = classNameProvider ;
		_logger = logger;
	}


	protected DatabaseMetaData getMetaData(Connection con) throws TelosysToolsException {
		DatabaseMetaData dbmd = null;
		try {
			dbmd = con.getMetaData();
		} catch (SQLException e) {
			_logger.error("Cannot get Meta-Data");
			throw new TelosysToolsException("Cannot get Meta-Data", e);
		}
		return dbmd;
	}


//	/**
//	 * Add a table entity in the repository
//	 * 
//	 * @param doc
//	 * @param dbmd
//	 * @param sCatalog
//	 * @param sSchema
//	 * @param sTableName
//	 * @return
//	 * @throws SQLException
//	 * @Deprecated
//	 */
//	protected void addEntity(RepositoryModel repositoryModel, DatabaseMetaData dbmd, 
//			String sCatalog, String sSchema, String sTableName)
//		throws SQLException 
//	{
//		_logger.log("*** addEntity() step 1 ");
//
//		//--- Create Entity = DB TABLE
//		Entity entity = new Entity();
//		entity.setName(sTableName);
//
//		// --- Get the VO Bean class name from the Table Name
//		String sBeanClassName = _inichk.getJavaBeanClassName(sTableName);
//		// --- Get the other class names from the VO Bean class name
//		String sVOListClassName = _classNameProvider.getVOListClassName(sBeanClassName);
//		String sDAOClassName = _classNameProvider.getDAOClassName(sBeanClassName);
//		String sXmlMapperClassName = _classNameProvider.getXmlMapperClassName(sBeanClassName);
//
//		_logger.log("*** addEntity() step 4 ");
//		entity.setBeanJavaClass(sBeanClassName);		
//		entity.setListJavaClass(sVOListClassName);
//		entity.setDaoJavaClass(sDAOClassName);
//		entity.setConverterJavaClass(sXmlMapperClassName);
//		
//		_logger.log("*** addEntity() step 5 ");
//
//		//--- Add the "table" element in the XML tree
//		repositoryModel.storeEntity(entity);
//
//		//--- Get the PrimaryKey columns list
//		List listPK = getColumnsPK(dbmd, sCatalog, sSchema, sTableName);
//
//		//--- Get the ForeignKeys list
//		List listFK = getForeignKeyParts(dbmd, sCatalog, sSchema, sTableName);
//
//		//--- Get the columns of the table ...
//		ResultSet rsColumns = dbmd.getColumns(sCatalog, sSchema, sTableName, "%");
//
//		// --- For each column of the table ...
//		while (rsColumns.next()) 
//		{
//			
//			ColumnMetaData col = new ColumnMetaData(rsColumns);
//			
//			String dbColName   = col.getName(); //--- Column Name
//			int    iDbTypeCode = col.getTypeCode(); //--- Column JDBC Type (cf "java.sql.Types" )
//			String dbTypeName  = col.getTypeName(); //--- Column Type (original database type)
//			String dbNotNull   = col.getNotNull(); //--- Column NOT NULL ( "true" or "false" )
//			int    iDbSize     = col.getSize(); //--- Column Size (max nb of characters or decimal precision 
//			
//			// --- Java field name and type
//			String sJavaType = "???";
//			String sJavaName = "???";
//			String sLongTextFlag = null;
//			String sDateType = null;
//			try {
//				sJavaType = _inichk.getAttributeType(dbTypeName, iDbTypeCode);
//				if (sJavaType == null)
//					sJavaType = "null";
//
//				sJavaName = _inichk.getAttributeName(dbColName, dbTypeName, iDbTypeCode);
//				if (sJavaName == null)
//					sJavaName = "null";
//
//				// --- Special informations
//				sLongTextFlag = _inichk.getAttributeLongTextFlag(dbTypeName, iDbTypeCode, sJavaType);
//				sDateType = _inichk.getAttributeDateType(dbTypeName, iDbTypeCode, sJavaType);
//
//			} catch (Throwable t) {
//				_logger.log("   ERROR : " + t.toString() + " - " + t.getMessage());
//			}
//			_logger.log("   - Column : " + dbColName + " ( " + iDbTypeCode + " : " + dbTypeName + " ) ---> "
//					+ sJavaName + " ( " + sJavaType + " ) ");
//
//			if (sJavaName.equals("boolean")) {
//				// bUseBooleanType = true;
//			}
//
//			//--- Create a new "column" for this "table/entity"
//			Column column = new Column();
//			column.setDatabaseName(dbColName);
//			column.setDatabaseTypeName(dbTypeName);
//			column.setDatabaseTypeCode(iDbTypeCode);
//			column.setNotNull(dbNotNull);
//			column.setDatabaseSize(iDbSize);
//			column.setJavaName(sJavaName);
//			column.setJavaType(sJavaType);
//			
//			if (sLongTextFlag != null) {
//				column.setLongText(sLongTextFlag);
//			}
//			if (sDateType != null) {
//				column.setDateType(sDateType);
//			}
//
//			// --- If this column is in the Table Primary Key
//			if (listPK.contains(dbColName.toUpperCase())) {
//				column.setPrimaryKey(true);
//			}
//
//			// --- If this column is a member of a Foreign Key
//			setFkAttribute(dbColName, column, listFK);
//
//			// --- Add the "column" element in the XML tree
//			entity.storeColumn(column);
//		}
//
//		// -- add FK elements to XML output
//		addForeignKeyParts( entity, listFK);
//
//		rsColumns.close();
//	}
	
	protected void addEntity(RepositoryModel repositoryModel, DatabaseTable dbTable)
	{
		_logger.log("addEntity()...");

		//--- Create Entity = DB TABLE
		Entity entity = new Entity();
		entity.setName( dbTable.getTableName() );

		//--- Get the VO Bean class name from the Table Name
		String sBeanClassName      = _inichk.getJavaBeanClassName(entity.getName());

// REMOVED in v 2.0.7 
//		//--- Get the other class names from the VO Bean class name
//		String sVOListClassName    = _classNameProvider.getVOListClassName(sBeanClassName);
//		String sDAOClassName       = _classNameProvider.getDAOClassName(sBeanClassName);
//		String sXmlMapperClassName = _classNameProvider.getXmlMapperClassName(sBeanClassName);

		entity.setBeanJavaClass(sBeanClassName);		
// REMOVED in v 2.0.7 
//		entity.setListJavaClass(sVOListClassName);
//		entity.setDaoJavaClass(sDAOClassName);
//		entity.setConverterJavaClass(sXmlMapperClassName);
		
		entity.setCatalog ( dbTable.getCatalogName() ); 
		entity.setSchema  ( dbTable.getSchemaName() ); 
		
		entity.setDatabaseType ( dbTable.getTableType() ) ; // v 2.0.7 #LGU
		
		//--- Add the columns of this table
		addColumns( entity, dbTable) ;
				
		//--- Add the Foreign Keys of this table
		addForeignKeyParts( entity, dbTable);
		
		
		//--- Add the entity in the repository
		repositoryModel.storeEntity(entity);

		_logger.log("addEntity() : entity " + dbTable.getTableName() + "stored");

		//--- Get the PrimaryKey columns list
//		List listPK = getColumnsPK(dbmd, sCatalog, sSchema, sTableName);

		//--- Get the ForeignKeys list
//		List listFK = getForeignKeyParts(dbmd, sCatalog, sSchema, sTableName);

		//--- Get the columns of the table ...
//		ResultSet rsColumns = dbmd.getColumns(sCatalog, sSchema, sTableName, "%");
		
//		// --- For each column of the table ...
//		Iterator iter = dbTable.getColumns().iterator();
//		while ( iter.hasNext() )
////		while (rsColumns.next()) 
//		{
//			DatabaseColumn col = (DatabaseColumn) iter.next();
//
//			//ColumnMetaData col = new ColumnMetaData(rsColumns);
//			
//			String dbColName   = col.getColumnName(); //--- Column Name
//			int    iDbTypeCode = col.getJdbcTypeCode(); //--- Column JDBC Type (cf "java.sql.Types" )
//			String dbTypeName  = col.getDbTypeName(); //--- Column Type (original database type)
//			String dbNotNull   = col.getNotNullAsString(); //--- Column NOT NULL ( "true" or "false" )
//			int    iDbSize     = col.getSize(); //--- Column Size (max nb of characters or decimal precision 
//			
//			//--- Java field name and type
//			String sJavaType = "???";
//			String sJavaName = "???";
//			String sLongTextFlag = null;
//			String sDateType = null;
//			try {
//				sJavaType = _inichk.getAttributeType(dbTypeName, iDbTypeCode);
//				if (sJavaType == null)
//					sJavaType = "null";
//
//				sJavaName = _inichk.getAttributeName(dbColName, dbTypeName, iDbTypeCode);
//				if (sJavaName == null)
//					sJavaName = "null";
//
//				// --- Special informations
//				sLongTextFlag = _inichk.getAttributeLongTextFlag(dbTypeName, iDbTypeCode, sJavaType);
//				sDateType = _inichk.getAttributeDateType(dbTypeName, iDbTypeCode, sJavaType);
//
//			} catch (Throwable t) {
//				_logger.log("   ERROR : " + t.toString() + " - " + t.getMessage());
//			}
//			_logger.log("   - Column : " + dbColName + " ( " + iDbTypeCode + " : " + dbTypeName + " ) ---> "
//					+ sJavaName + " ( " + sJavaType + " ) ");
//
//			if (sJavaName.equals("boolean")) {
//				// bUseBooleanType = true;
//			}
//
//			//--- Create a new "column" for this "table/entity"
//			Column column = new Column();
//			column.setDatabaseName(dbColName);
//			column.setDatabaseTypeName(dbTypeName);
//			column.setDatabaseTypeCode(iDbTypeCode);
//			column.setNotNull(dbNotNull);
//			column.setDatabaseSize(iDbSize);
//			column.setJavaName(sJavaName);
//			column.setJavaType(sJavaType);
//			
//			if (sLongTextFlag != null) {
//				column.setLongText(sLongTextFlag);
//			}
//			if (sDateType != null) {
//				column.setDateType(sDateType);
//			}
//
//			// --- If this column is in the Table Primary Key
////			if (listPK.contains(dbColName.toUpperCase())) {
////				column.setPrimaryKey(true);
////			}
//			column.setPrimaryKey( col.isInPrimaryKey());
//
//			// --- If this column is a member of a Foreign Key
//			//setFkAttribute(dbColName, column, listFK);
//			column.setForeignKey( col.getUsedInForeignKey() > 0 );
//
//			// --- Add the "column" element in the XML tree
//			entity.storeColumn(column);
//		}

		// -- add FK elements to XML output

	}
	
	private void addColumns( Entity entity, DatabaseTable dbTable) 
	{
		//--- For each column of the table ...
//		Iterator iter = dbTable.getColumns().iterator();
//		while ( iter.hasNext() )
//		{
//			DatabaseColumn dbCol = (DatabaseColumn) iter.next();
		for ( DatabaseColumn dbCol : dbTable.getColumns() ) {
			//--- Create a new column from the database model
			Column column = buildColumn( dbCol );
			
			//--- Add the "column" element in the XML tree
			entity.storeColumn(column);
		}
	}
	
	protected Column buildColumn( DatabaseColumn dbCol ) 
	{	
		String dbColName     = dbCol.getColumnName(); //--- Column Name
		String dbTypeName    = dbCol.getDbTypeName(); //--- Column Type (original database type)
		int    iDbSize       = dbCol.getSize(); //--- Column Size (max nb of characters or decimal precision 
		int    iJdbcTypeCode = dbCol.getJdbcTypeCode(); //--- Column JDBC Type (cf "java.sql.Types" )
		String dbNotNull     = dbCol.getNotNullAsString(); //--- Column NOT NULL ( "true" or "false" )
		
		//--- Java field name and type
		String sJavaType = "???";
		String sJavaName = "???";
		String sLongTextFlag = null;
		String sDateType = null;
		try {
			sJavaType = _inichk.getAttributeType(dbTypeName, iJdbcTypeCode, dbCol.isNotNull() );
			if (sJavaType == null)
				sJavaType = "null";

			sJavaName = _inichk.getAttributeName(dbColName, dbTypeName, iJdbcTypeCode);
			if (sJavaName == null)
				sJavaName = "null";

			// --- Special informations
			sLongTextFlag = _inichk.getAttributeLongTextFlag(dbTypeName, iJdbcTypeCode, sJavaType);
			sDateType = _inichk.getAttributeDateType(dbTypeName, iJdbcTypeCode, sJavaType);

		} catch (Throwable t) {
			_logger.log("   ERROR : " + t.toString() + " - " + t.getMessage());
		}
		_logger.log("   - Column : " + dbColName + " ( " + iJdbcTypeCode + " : " + dbTypeName + " ) ---> "
				+ sJavaName + " ( " + sJavaType + " ) ");

//		if (sJavaName.equals("boolean")) {
//			// bUseBooleanType = true;
//		}

		//--- Create a new "column" for this "table/entity"
		Column column = new Column();
		column.setDatabaseName(dbColName);
		column.setDatabaseTypeName(dbTypeName);
		column.setJdbcTypeCode(iJdbcTypeCode);
		column.setDatabaseNotNull(dbNotNull);
		column.setDatabaseSize(iDbSize);
		column.setJavaName(sJavaName);
		column.setJavaType(sJavaType);
		
		//--- Java default value for primitive types
		JavaTypes javaTypes = JavaTypesManager.getJavaTypes();
		String sDefaultValue = javaTypes.getDefaultValueForType(sJavaType);
		if ( sDefaultValue != null ) {
			// Not null only for primitive types
			column.setJavaDefaultValue(sDefaultValue);
		}
		
		if (sLongTextFlag != null) {
			column.setLongText(sLongTextFlag);
		}
		if (sDateType != null) {
			column.setDateType(sDateType);
		}

		//--- Is this column in the Table Primary Key ?
//		if (listPK.contains(dbColName.toUpperCase())) {
//			column.setPrimaryKey(true);
//		}
		column.setPrimaryKey( dbCol.isInPrimaryKey());

		//--- Is this column a member of a Foreign Key ?
		//setFkAttribute(dbColName, column, listFK);
		column.setForeignKey( dbCol.getUsedInForeignKey() > 0 );

		//--- Is this column auto-incremented ?
		column.setAutoIncremented(dbCol.isAutoIncremented());
		
		column.setDatabasePosition( dbCol.getOrdinalPosition() ); // #LGU 10/08/2011
		
		column.setDatabaseDefaultValue( dbCol.getDefaultValue() ); // #LGU 10/08/2011

		//--- Further information ( v 2.0.3 )
		column.setLabel(     _inichk.getAttributeLabel(dbColName, dbTypeName, iJdbcTypeCode) );
		column.setInputType( _inichk.getAttributeInputType(dbColName, dbTypeName, iJdbcTypeCode, sJavaType));
		
		//--- Further information for Java Validator 
		if ( ! column.isJavaPrimitiveType() ) {
			if ( dbCol.isNotNull()  ) {
				column.setJavaNotNull( true );
				column.setNotEmpty(true);
			}
			if ( column.isJavaTypeString() )
			{
				column.setMaxLength(""+iDbSize);
			}
		}
		
		return column ;
	}
	
//	/**
//	 * Flags the column as a "Foreign Key" if the column name is in the given FK list
//	 * 
//	 * @param colName
//	 * @param column
//	 * @param listFK
//	 * @since v 0.9.0
//	 */
//	//private void setFkAttribute(String colName, Element column, List listFK) 
//	protected void setFkAttribute(String colName, Column column, List listFK) 
//	{
//		// --- If this column is a member of a Foreign Key
//		for (int i = 0; i < listFK.size(); i++) {
//			ForeignKeyMetaData fk = (ForeignKeyMetaData) listFK.get(i);
//			if ( colName.equalsIgnoreCase( fk.getColName() ) ) 
//			{
//				//column.setAttribute(DatabaseRepository.COLUMN_FOREIGN_KEY_ATTRIBUTE, "true");
//				column.setForeignKey(true);
//				break;
//			}
//		}
//
//	}

//	/**
//	 * Adds each FK element to the XML document
//	 * 
//	 * @param doc
//	 * @param table
//	 * @param listFK list of Foreign Key elements (supposed to be sorted by FK name)
//	 * @since v 0.9.0
//	 */
//	protected void addForeignKeyParts( Entity entity, List listFK) 
//	{
//		if ( listFK == null ) return ;
//		if ( listFK.isEmpty() ) return ;
//		
//		String fkname = "";
//		ForeignKey foreignKey = null ;
//		for (int i = 0; i < listFK.size(); i++) 
//		{
//			ForeignKeyMetaData fk = (ForeignKeyMetaData) listFK.get(i);
//			
//			if (!fk.getFkName().equals(fkname)) // Not the same Foreign Key name => create a new FK tag
//			{
//				//--- Creates XML element <fk name="" >
//				foreignKey = new ForeignKey();
//				
//				foreignKey.setName( fk.getFkName() ); // the name must be set before 'store'
//				entity.storeForeignKey(foreignKey);
//				fkname = fk.getFkName();
//			}
//			
//			//--- Creates XML child element <fkcol  >
//			ForeignKeyColumn foreignKeyColumn = new ForeignKeyColumn();
//			
//			foreignKeyColumn.setTableName( fk.getTableName() );
//			foreignKeyColumn.setColumnName(fk.getColName());
//			foreignKeyColumn.setTableRef( fk.getRefTable() );
//			foreignKeyColumn.setColumnRef( fk.getRefCol() );
//			foreignKeyColumn.setUpdateRule( String.valueOf(fk.getUpdateRule()) );
//			foreignKeyColumn.setDeleteRule( String.valueOf(fk.getDeleteRule()) );
//			foreignKeyColumn.setDeferrable( String.valueOf(fk.getDeferrable()) );
//			
//			foreignKey.storeForeignKeyColumn(foreignKeyColumn);
//		}
//	}
	
	protected ForeignKey buildForeignKey( DatabaseForeignKey dbFK ) 
	{
		ForeignKey foreignKey = new ForeignKey();
		foreignKey.setName( dbFK.getForeignKeyName() ); // the name must be set before 'storeForeignKey'
		
//		Iterator iterDbForeignKeyColumns = dbFK.getForeignKeyColumns().iterator();
//		while ( iterDbForeignKeyColumns.hasNext() )
//		{
//			DatabaseForeignKeyColumn dbFkCol = (DatabaseForeignKeyColumn) iterDbForeignKeyColumns.next();
		for ( DatabaseForeignKeyColumn dbFkCol : dbFK.getForeignKeyColumns() ) {
			ForeignKeyColumn foreignKeyColumn = new ForeignKeyColumn();
			
			foreignKeyColumn.setSequence( dbFkCol.getFkSequence() );
			
			foreignKeyColumn.setTableName( dbFkCol.getFkTableName() );
			foreignKeyColumn.setColumnName(dbFkCol.getFkColumnName() );
			
			foreignKeyColumn.setTableRef( dbFkCol.getPkTableName() );
			foreignKeyColumn.setColumnRef( dbFkCol.getPkColumnName() );
			
			foreignKeyColumn.setUpdateRule( String.valueOf(dbFkCol.getUpdateRule()) );
			foreignKeyColumn.setDeleteRule( String.valueOf(dbFkCol.getDeleteRule()) );
			foreignKeyColumn.setDeferrable( String.valueOf(dbFkCol.getDeferrability()) );
			
			foreignKey.storeForeignKeyColumn(foreignKeyColumn);
		}
		return foreignKey ;
	}
	
	protected void addForeignKeyParts( Entity entity, DatabaseTable dbTable) 
	{
		//--- For each foreign key of the table ...
//		Iterator iterDbForeignKeys = dbTable.getForeignKeys().iterator();
//		while ( iterDbForeignKeys.hasNext() )
//		{
//			DatabaseForeignKey dbFK = (DatabaseForeignKey) iterDbForeignKeys.next();
		for ( DatabaseForeignKey dbFK : dbTable.getForeignKeys() ) {
			ForeignKey foreignKey = buildForeignKey( dbFK ) ;
			
			entity.storeForeignKey(foreignKey);
		}
		
	}

}
