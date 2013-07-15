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
import java.util.Date;
import java.util.Iterator;

import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.config.ClassNameProvider;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.repository.config.InitializerChecker;
import org.telosys.tools.repository.model.ModelVersion;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * @author Sylvain LEROY, Laurent GUERIN, Eric LEMELIN
 * 
 */

public class RepositoryGenerator extends RepositoryManager
{
	/**
	 * Constructor
	 * 
	 * @param inichk
	 * @param classNameProvider
	 * @param logger
	 */
	public RepositoryGenerator(InitializerChecker inichk, ClassNameProvider classNameProvider, TelosysToolsLogger logger) 
	{
		super(inichk, classNameProvider, logger);
	}

	/**
	 * Generates the repository model
	 * @param con
	 * @param sDatabaseName
	 * @param sCatalog
	 * @param sSchema
	 * @param sTableNamePattern
	 * @param arrayTableTypes
	 * @return
	 * @throws TelosysToolsException
	 */
	public RepositoryModel generate(Connection con, String sDatabaseName, String sCatalog, String sSchema,
			String sTableNamePattern, String[] arrayTableTypes) throws TelosysToolsException 
	{
		_logger.log("--> Repository generation ");

			_logger.log(" . get meta-data ");
			DatabaseMetaData dbmd = getMetaData(con);

			RepositoryModel repositoryModel = new RepositoryModel();
						
			try {
				//--- Init new repository	
				repositoryModel.setDatabaseName(sDatabaseName);
				repositoryModel.setDatabaseType(dbmd.getDatabaseProductName() );
				repositoryModel.setGenerationDate( new Date() );
				repositoryModel.setVersion(ModelVersion.VERSION);

				//--- Add all tables/entities to the new repository	
				//generateEntities(repositoryModel, dbmd, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);
				generateEntities(repositoryModel, con, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);
				
			} catch (SQLException e) {
				throw new TelosysToolsException("SQLException", e);
			}

		return repositoryModel ;
	}


/***
	private void generateEntities(RepositoryModel repositoryModel, DatabaseMetaData dbmd, 
			String sCatalog, String sSchema,
			String sTableNamePattern, String[] arrayTableTypes) throws SQLException 
	{
		// --- Get METADATA parameters
		if (sTableNamePattern == null) {
			sTableNamePattern = "%";
		}

		_logger.log("   ... Metadata parameters : ");
		_logger.log("   ... * Catalog = " + sCatalog);
		_logger.log("   ... * Schema  = " + sSchema);
		_logger.log("   ... * Table Name Pattern  = " + sTableNamePattern);

		StringBuffer sb = new StringBuffer(100);
		for (int i = 0 ; i < arrayTableTypes.length ; i++ ) {
			sb.append("[" + arrayTableTypes[i] + "] ");
		}
		_logger.log("   ... * Table Types Array  = " + sb.toString());

		// --- Get tables list
		ResultSet rsTables = dbmd.getTables(sCatalog, sSchema, sTableNamePattern, arrayTableTypes);

		// _progress.next();
		// --- For each table ...
		int iTablesCount = 0;
		while (rsTables.next()) 
		{
			iTablesCount++;
			String sCurrentCatalog = rsTables.getString(1);
			String sCurrentSchema = rsTables.getString(2);
			String sTableName = rsTables.getString(3);
			_logger.log("   --------------------------------------------------------------");
			_logger.log("   Table '" + sTableName + "' ( catalog = '" + sCurrentCatalog + "', schema = '"
					+ sCurrentSchema + "' )");
			// --- add table with column
			addEntity(repositoryModel, dbmd, sCurrentCatalog, sCurrentSchema, sTableName);
		}
		rsTables.close();
		_logger.log("   --------------------------------------------------------------");
		_logger.log("   " + iTablesCount + " table(s) generated.");
		_logger.log("   --------------------------------------------------------------");

		_logger.log("   ... Writing repository file... ");
	}
***/
	
	private void generateEntities(RepositoryModel repositoryModel, Connection con, 
			String sCatalog, String sSchema,
			String sTableNamePattern, String[] arrayTableTypes) throws SQLException 
	{
		// --- Get METADATA parameters
		if (sTableNamePattern == null) {
			sTableNamePattern = "%";
		}

		_logger.log("   ... Metadata parameters : ");
		_logger.log("   ... * Catalog = " + sCatalog);
		_logger.log("   ... * Schema  = " + sSchema);
		_logger.log("   ... * Table Name Pattern  = " + sTableNamePattern);

		StringBuffer sb = new StringBuffer(100);
		for (int i = 0 ; i < arrayTableTypes.length ; i++ ) {
			sb.append("[" + arrayTableTypes[i] + "] ");
		}
		_logger.log("   ... * Table Types Array  = " + sb.toString());

		//--- Load the Database Model
		DatabaseModelManager manager = new DatabaseModelManager( this.getLogger() );
		DatabaseTables dbTables = manager.getDatabaseTables(con, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);

		//--- For each table add an Entity in the repository
		Iterator<DatabaseTable> iter = dbTables.iterator();
		int iTablesCount = 0;
		while ( iter.hasNext() )
		{
			iTablesCount++;
			DatabaseTable dbTable = (DatabaseTable) iter.next();
			_logger.log("   --------------------------------------------------------------");
			_logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");
			addEntity(repositoryModel, dbTable) ;
		}
		_logger.log("   --------------------------------------------------------------");
		_logger.log("   " + iTablesCount + " table(s) generated.");
		_logger.log("   --------------------------------------------------------------");
			
	}

}
