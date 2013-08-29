package org.telosys.tools.commons.dbcfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabasesConfigurations {

	private int databaseMaxId     = 0 ;
	
	private int databaseDefaultId = 0 ;
	
	private Map<Integer, DatabaseConfiguration> databasesMap = new HashMap<Integer, DatabaseConfiguration>();

	
	public int getDatabaseMaxId() {
		return databaseMaxId;
	}
	public void setDatabaseMaxId(int dbMaxId) {
		this.databaseMaxId = dbMaxId;
	}

	public int getDatabaseDefaultId() {
		return databaseDefaultId;
	}
	public void setDatabaseDefaultId(int dbDefaultId) {
		this.databaseDefaultId = dbDefaultId;
	}

	/**
	 * Store a database configuration (add a new one or replace if the id already exists )
	 * @param databaseConfiguration
	 */
	public void storeDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
		Integer databaseId = new Integer(databaseConfiguration.getDatabaseId());
		databasesMap.put(databaseId, databaseConfiguration);
	}
	
	/**
	 * Returns the database configuration for the given database id
	 * @param id
	 * @return the database configuration (or null if none)
	 */
	public DatabaseConfiguration getDatabaseConfiguration(int id) {
		Integer databaseId = new Integer(id);
		return databasesMap.get(databaseId) ;
	}
	
	public boolean removeDatabaseConfiguration(int id) {
		Integer databaseId = new Integer(id);
		DatabaseConfiguration removed = databasesMap.remove(databaseId) ;
		return removed != null ;
	}
	
	/**
	 * Returns the number of databases configurations stored
	 * @return
	 */
	public int getNumberOfDatabases() {
		return databasesMap.size();
	}
	
	/**
	 * Return a list of all the databases configuration ordered by id
	 * @return
	 */
	public List<DatabaseConfiguration> getDatabaseConfigurationsList() {
		
		//--- List of sorted id
		ArrayList<Integer> keysArrayList = new ArrayList<Integer>(databasesMap.keySet()) ;
		Collections.sort(keysArrayList) ;
		
		
		LinkedList<DatabaseConfiguration> list = new LinkedList<DatabaseConfiguration>();
		for ( Integer id : keysArrayList ) {
			list.add( databasesMap.get(id) ) ;
		}
		return list;
	}
	
	
}
