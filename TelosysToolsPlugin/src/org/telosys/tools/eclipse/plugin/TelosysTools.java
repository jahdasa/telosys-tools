package org.telosys.tools.eclipse.plugin;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

/**
 * The activator class for the plug-in life cycle
 */
public class TelosysTools extends AbstractUIPlugin 
{
	//--- Bundle Context
	private BundleContext _bundleContext = null ;

    //------------------------------------------------------------------------------------------------
	/**
	 * The plugin constructor
	 */
	public TelosysTools() 
	{
		super();
		PluginLogger.log("Plugin constructor...");
	}

    //------------------------------------------------------------------------------------------------
	/**
	 * Launch the plugin
	 */
	public void start(BundleContext bundleContext) throws Exception 
	{
		PluginLogger.log("Plugin start()...");
		super.start(bundleContext);

		//--- Keep the bundle context
		_bundleContext = bundleContext;
		Bundle bundle = _bundleContext.getBundle();
		
		//--- Init the plugin static informations
		MyPlugin.init(bundle);
		
		File file = _bundleContext.getDataFile("testfile.txt");
		PluginLogger.log( "Data file : " + file.getPath() );
		
/***		
		//--- Init the plugin directory 
//		if ( $plugin != null )
//		{
			String sDirectory = null ;
			//URL url = $plugin.find( new Path("") ) ;
			URL url = this.find( new Path("") ) ;
			if ( url != null )
			{
				// URL like "file:/c:/eclipse/plugins/myplugin" 
				//       or "bundleentry://143/" when runing under Eclipse ( test mode ) 
				String sURL = url.toString();
//				MsgBox.info( "Plugin START : \n"
//						+ "Plugin directory URL : " + sURL );
				
				if ( sURL.startsWith("file:/") )
				{
					sDirectory = sURL.substring(6); // without "file:/"
				}
				else
				{
					String sLocation = bundle.getLocation();
					if ( sLocation != null )
					{
						if ( sLocation.startsWith("update@/") )
						{
							sDirectory = sLocation.substring(8); // without "update@/"
						}
					}
				}
				
//				if ( sDirectory != null )
//				{					
//					if ( sDirectory.endsWith("/") )
//					{
//						$sPluginDir = sDirectory.substring(0, sDirectory.length()-1) ;
//					}
//					else
//					{
//						$sPluginDir = sDirectory ;
//					}
//				}
				
			}
//		}
//		else
//		{
//		}
***/
		
//		doInitPreferences(_bundleContext);
	}

    //------------------------------------------------------------------------------------------------
	/**
	 * Stop the plugin
	 */
	public void stop(BundleContext bundleContext) throws Exception 
	{
		PluginLogger.log("Plugin stop()...");
		super.stop(bundleContext);
	}

//	/**
//	 * init the template load all the template
//	 */
//	private final void doInitPreferences(BundleContext pbcContext) {
//		try {
//			//$preferences = new Preferences();
//			
//			// --- Get the templates
//			Path filePath = new Path( Const.PREF_FILE_NAME );
//			InputStream is = super.openStream(filePath);
//
////			InputStream isInputStream = FileLocator.openStream(pbcContext
////					.getBundle(), new Path(getFile(Const.TEMPLATES_SUBDIR,
////					_bcContext)), false);
//			
//			// --- Load the templates
////			$pTemplate_Bundle.load(isInputStream);
//			//$preferences.load(is);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * @param psName
//	 *            name of the file
//	 * @param pbcContext
//	 *            context of the plugin
//	 * 
//	 * @return a file like name.properties
//	 */
//	private String getFile(String psName, BundleContext pbcContext) {
//		// --- Put the name in a String Buffer
//		StringBuffer sbStringBuffer = new StringBuffer(psName);
//		// --- put an extension
//		sbStringBuffer.append(".properties");
//		try {
//			// --- check if the file exist
//			InputStream isInputStream = FileLocator.openStream(pbcContext
//					.getBundle(), new Path(sbStringBuffer.toString()), false);
//		} catch (IOException e) {
//		}
//		return sbStringBuffer.toString();
//	}


	/**
	 * Returns the current shell
	 * 
	 * @return the current shell
	 */
//	public Shell getShell() {
//		return super.getWorkbench().getWorkbenchWindows()[0].getShell();
//	}

}
