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

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.repository.config.EntityInformationProvider;
import org.telosys.tools.repository.config.UserInterfaceInformationProvider;
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
	 * @param entityInformationProvider
	 * @param uiInfoProvider
	 * @param logger
	 */
	public RepositoryGenerator(EntityInformationProvider entityInformationProvider, UserInterfaceInformationProvider uiInfoProvider, TelosysToolsLogger logger) 
	{
		super(entityInformationProvider, uiInfoProvider, logger);
	}

	/**
	 * Generates the repository model from the given database
	 * @param con
	 * @param databaseConfig
	 * @return
	 * @throws TelosysToolsException
	 */
	public RepositoryModel generate(Connection con, DatabaseConfiguration databaseConfig) throws TelosysToolsException 
	{
		logger.log("--> Repository generation ");

		logger.log(" . get meta-data ");
		DatabaseMetaData dbmd = getMetaData(con);

		RepositoryModel repositoryModel = new RepositoryModel();
					
		try {
			//--- Init new repository	
			repositoryModel.setDatabaseName( databaseConfig.getDatabaseName() );
			repositoryModel.setDatabaseId( databaseConfig.getDatabaseId() );
			repositoryModel.setDatabaseType( dbmd.getDatabaseProductName() );
			repositoryModel.setGenerationDate( new Date() );
			repositoryModel.setVersion( ModelVersion.VERSION );

			//--- Add all tables/entities to the new repository	
			generateEntities(repositoryModel, 
					con, 
					databaseConfig.getMetadataCatalog(), 
					databaseConfig.getMetadataSchema(), 
					databaseConfig.getMetadataTableNamePattern(), 
					databaseConfig.getMetadataTableTypesArray());
			
		} catch (SQLException e) {
			throw new TelosysToolsException("SQLException", e);
		}

		return repositoryModel ;
	}
	
//	/**
//	 * Generates the repository model
//	 * @param con
//	 * @param sDatabaseName
//	 * @param sCatalog
//	 * @param sSchema
//	 * @param sTableNamePattern
//	 * @param arrayTableTypes
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	public RepositoryModel generate(Connection con, String sDatabaseName, String sCatalog, String sSchema,
//			String sTableNamePattern, String[] arrayTableTypes) throws TelosysToolsException 
//	{
//		logger.log("--> Repository generation ");
//
//		logger.log(" . get meta-data ");
//		DatabaseMetaData dbmd = getMetaData(con);
//
//		RepositoryModel repositoryModel = new RepositoryModel();
//					
//		try {
//			//--- Init new repository	
//			repositoryModel.setDatabaseName(sDatabaseName);
//			repositoryModel.setDatabaseType(dbmd.getDatabaseProductName() );
//			repositoryModel.setGenerationDate( new Date() );
//			repositoryModel.setVersion(ModelVersion.VERSION);
//
//			//--- Add all tables/entities to the new repository	
//			//generateEntities(repositoryModel, dbmd, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);
//			generateEntities(repositoryModel, con, sCatalog, sSchema, sTableNamePattern, arrayTableTypes);
//			
//		} catch (SQLException e) {
//			throw new TelosysToolsException("SQLException", e);
//		}
//
//		return repositoryModel ;
//	}

	private void generateEntities(RepositoryModel repositoryModel, Connection con, 
			String sCatalog, String sSchema,
			String sTableNamePattern, String[] arrayTableTypes) throws SQLException 
	{
		// --- Get METADATA parameters
		if (sTableNamePattern == null) {
			sTableNamePattern = "%";
		}

		logger.log("   ... Metadata parameters : ");
		logger.log("   ... * Catalog = " + sCatalog);
		logger.log("   ... * Schema  = " + sSchema);
		logger.log("   ... * Table Name Pattern  = " + sTableNamePattern);

		StringBuffer sb = new StringBuffer(100);
		for (int i = 0 ; i < arrayTableTypes.length ; i++ ) {
			sb.append("[" + arrayTableTypes[i] + "] ");
		}
		logger.log("   ... * Table Types Array  = " + sb.toString());

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
			logger.log("   --------------------------------------------------------------");
			logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");
			addEntity(repositoryModel, dbTable) ;
		}
		logger.log("   --------------------------------------------------------------");
		logger.log("   " + iTablesCount + " table(s) generated.");
		logger.log("   --------------------------------------------------------------");
			
	}

}
