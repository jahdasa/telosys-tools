package org.telosys.tools.tests.commons.bundles;

import junit.framework.TestCase;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.bundles.BundlesManager;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.tests.commons.TestsFolders;

public class BundlesManagerTest extends TestCase {

	public void testFolder() throws TelosysToolsException {
		
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(TestsFolders.getTestsRootFolder());
		TelosysToolsCfg telosysToolsCfg = cfgManager.loadProjectConfig();

		String bundlesFolder = TestsFolders.getTestsBundlesFolder();
		System.out.println("Bundles folder : '" + bundlesFolder + "'");
		BundlesManager bm = new BundlesManager( telosysToolsCfg );
		
		String bundleName = "foo";
		System.out.println("Getting folder for " + bundleName + "'... ");		
		String folder = bm.getFileSystemFolder(bundleName);
		System.out.println(" result = '" + folder + "'... ");
		
		String expected = bundlesFolder + "/" + bundleName ;
		assertEquals(expected, folder);
	}
}
