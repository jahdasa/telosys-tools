package org.telosys.tools.tests.commons.bundles;

import java.util.Properties;

import junit.framework.TestCase;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.bundles.BundleStatus;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.tests.commons.TestsFolders;

public class BundlesManagerTest extends TestCase {

	private void printProperty(Properties properties, String name) {
		System.out.println(name + " : " + properties.getProperty(name));
	}
	
	private BundlesManager getBundlesManager() {
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(TestsFolders.getTestsRootFolder());
		TelosysToolsCfg telosysToolsCfg;
		try {
			telosysToolsCfg = cfgManager.loadProjectConfig();
		} catch (TelosysToolsException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot load project properties", e);
		}
		Properties properties = telosysToolsCfg.getProperties();
		System.out.println("HTTP properties : ");
		printProperty(properties, "http.proxyHost");
		printProperty(properties, "http.proxyPort");
		printProperty(properties, "https.proxyHost");
		printProperty(properties, "https.proxyPort");
		BundlesManager bm = new BundlesManager( telosysToolsCfg );
		return bm;
	}
	
	public void testFolder() throws TelosysToolsException {
		

		String bundlesFolder = TestsFolders.getTestsBundlesFolder();
		System.out.println("Bundles folder : '" + bundlesFolder + "'");
//		BundlesManager bm = new BundlesManager( telosysToolsCfg );
		BundlesManager bm = getBundlesManager();
		
		String bundleName = "foo";
		System.out.println("Getting folder for " + bundleName + "'... ");		
		String folder = bm.getFileSystemFolder(bundleName);
		System.out.println(" result = '" + folder + "'... ");
		
		String expected = bundlesFolder + "/" + bundleName ;
		assertEquals(expected, folder);
	}

	public void testIsBundleInstalled() throws TelosysToolsException {
		
		BundlesManager bm = getBundlesManager();
		boolean b = bm.isBundleAlreadyInstalled("no-installed");
		assertFalse(b);
	}

	public void testDownloadBundle() throws TelosysToolsException {
		String bundleName = "persistence-jpa-TT210-R2" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName);
		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		System.out.println("Zip file : " + status.getZipFile());
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
	}

	public void testDownloadBundleInSpecificFolder() throws TelosysToolsException {
		String bundleName = "basic-templates-TT210" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName, "TelosysTools/downloads2");
		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		System.out.println("Zip file : " + status.getZipFile());
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
	}

	public void testDownloadAndInstallBundle() throws TelosysToolsException {
		String bundleName = "persistence-jpa-TT210-R2" ;
		BundlesManager bm = getBundlesManager();
		System.out.println("Downloading bundle '" + bundleName + "'...");
		
		BundleStatus status = bm.downloadBundle("telosys-tools", bundleName);

		System.out.println("Satus message : " + status.getMessage() );
		System.out.println("Satus is done ? : " + status.isDone() );
		System.out.println("Zip file : " + status.getZipFile());
		if ( status.getException() != null ) {
			System.out.println("Exception : " + status.getException());
		}
		
		assertTrue(status.isDone() );
		assertNull(status.getException());
		String zipFile = status.getZipFile();
		if ( status.isDone() && status.getException() == null ) {
			System.out.println("Installing bundle '" + bundleName + "' from " + zipFile );
			BundleStatus status2 = bm.installBundle(zipFile, bundleName);
			System.out.println("Satus message : " + status2.getMessage() );
			System.out.println("Satus is done ? : " + status2.isDone() );
			System.out.println("Exception : " + status2.getException());
			System.out.println("Satus log : "  );
			System.out.println( status2.getLog() );
		}
	}

}
