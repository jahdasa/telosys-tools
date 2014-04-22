package org.telosys.tools.tests.commons.cfg;

import java.io.File;

import junit.framework.TestCase;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.variables.Variable;

public class TelosysToolsCfgManagerTest extends TestCase {

	public void printSeparator() {
		System.out.println("==============================================================" );
	}
	public void print(File file) {
		System.out.println("File   : " + file.toString());
		System.out.println("Parent : " + file.getParent());
	}
	
	public void print(TelosysToolsCfg telosysToolsCfg) {
		System.out.println( "getProjectAbsolutePath = " + telosysToolsCfg.getProjectAbsolutePath() );
		System.out.println( "getCfgFileAbsolutePath = " + telosysToolsCfg.getCfgFileAbsolutePath() );
		System.out.println( "getDatabasesDbCfgFile             = " + telosysToolsCfg.getDatabasesDbCfgFile());
		System.out.println( "getDatabasesDbCfgFileAbsolutePath = " + telosysToolsCfg.getDatabasesDbCfgFileAbsolutePath());
		
		Variable[] variables = telosysToolsCfg.getAllVariables();
		print(variables);
	}
	
	public void print(Variable[] variables) {
		System.out.println("VARIABLES : ");
		for ( Variable v : variables ) {
			System.out.println(" . " + v.getName() + " = " + v.getValue() + " ( " + v.getSymbolicName() + " )");
		}
	}
	
	public String toString(String[] array) {
		StringBuffer sb = new StringBuffer();
		for ( String s : array ) {
			sb.append("'");
			sb.append(s);
			sb.append("' ");
		}
		return sb.toString();
	}
	
	/**
	 * ZERO database configuration
	 * @throws TelosysToolsException
	 */
	public void testLoad0() throws TelosysToolsException {
		printSeparator();
		File file = FileUtil.getFileByClassPath("/cfg/telosys-tools.cfg");
		String projectFolder = file.getParent();
		print(file);
		
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder);
		TelosysToolsCfg telosysToolsCfg = cfgManager.loadProjectConfig();
		
		print(telosysToolsCfg);
		
//		assertEquals(0, databasesConfigurations.getDatabaseDefaultId() ) ;
//		assertEquals(4, databasesConfigurations.getDatabaseMaxId() ) ;

	}

//	/**
//	 * ONE database configuration
//	 * @throws TelosysToolsException
//	 */
//	public void testLoad1() throws TelosysToolsException {
//		
//		printSeparator();
//		//--- Load 
//		File file = FileUtil.getFileByClassPath("/dbcfg/databases-test1.dbcfg");
//		print(file);
//		
//		DbConfigManager dbDonfigManager = new DbConfigManager(file);
//		DatabasesConfigurations databasesConfigurations = dbDonfigManager.load();
//		
//		print(databasesConfigurations);
//		
//		assertEquals(0, databasesConfigurations.getDatabaseDefaultId() ) ;
//		assertEquals(4, databasesConfigurations.getDatabaseMaxId() ) ;
//
//		assertEquals(1, databasesConfigurations.getNumberOfDatabases() ) ;
//		
//		DatabaseConfiguration databaseConfiguration = databasesConfigurations.getDatabaseConfiguration(0);
//		assertNotNull(databaseConfiguration);
//		assertEquals(0, databaseConfiguration.getDatabaseId());
//		
//		//--- Update 
//		System.out.println("UPDATED CONFIG : ");
//		databaseConfiguration.setDatabaseName("New name");
//		databaseConfiguration.setDriverClass("my.new.driver");
//		databaseConfiguration.setUser(databaseConfiguration.getUser()+"-new") ;
//		databaseConfiguration.setPassword(databaseConfiguration.getPassword()+"-new") ;
//		print(databaseConfiguration) ;
//		
//		//--- Save 
//		System.out.println("SAVING...");
//		File out = FileUtil.getFileByClassPath("/dbcfg/databases-test1-out.dbcfg");
//		print(out);
//		
//		dbDonfigManager = new DbConfigManager(out);
//		dbDonfigManager.save(databasesConfigurations);
//		System.out.println("SAVED.");
//	}
//	
//	/**
//	 * TWO databases configurations
//	 * @throws TelosysToolsException
//	 */
//	public void testLoad2() throws TelosysToolsException {
//		
//		printSeparator();
//		File file = FileUtil.getFileByClassPath("/dbcfg/databases-test2.dbcfg");
//		print(file);
//		
//		DbConfigManager dbDonfigManager = new DbConfigManager(file);
//		DatabasesConfigurations databasesConfigurations = dbDonfigManager.load();
//		
//		print(databasesConfigurations);
//		
//		assertEquals(0, databasesConfigurations.getDatabaseDefaultId() ) ;
//		assertEquals(0, databasesConfigurations.getDatabaseMaxId() ) ;
//
//		assertEquals(2, databasesConfigurations.getNumberOfDatabases() ) ;
//		
//		DatabaseConfiguration databaseConfiguration = databasesConfigurations.getDatabaseConfiguration(1);
//		assertNotNull(databaseConfiguration);
//		assertEquals(1, databaseConfiguration.getDatabaseId());
//		
//		databaseConfiguration = databasesConfigurations.getDatabaseConfiguration(2);
//		assertNotNull(databaseConfiguration);
//		assertEquals(2, databaseConfiguration.getDatabaseId());
//		
//		//--- Save 
//		System.out.println("SAVING...");
//		File out = FileUtil.getFileByClassPath("/dbcfg/databases-test2-out.dbcfg");
//		print(out);
//		
//		dbDonfigManager = new DbConfigManager(out);
//		dbDonfigManager.save(databasesConfigurations);
//		System.out.println("SAVED.");
//	}
	
}
