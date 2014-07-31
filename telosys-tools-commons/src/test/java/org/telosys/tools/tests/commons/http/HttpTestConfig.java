package org.telosys.tools.tests.commons.http;

import java.util.Properties;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.PropertiesManager;
import org.telosys.tools.commons.http.HttpClient;
import org.telosys.tools.commons.http.HttpClientConfig;
import org.telosys.tools.commons.http.HttpProxy;
import org.telosys.tools.tests.commons.TestsFolders;

public class HttpTestConfig {

	/**
	 * Returns a configuration for a specific proxy<br>
	 * 
	 * @return
	 */
	public static Properties getSpecificProxyProperties()  {
		
		System.out.println("Loading http properties from " + TestsFolders.getTestsProxyPropertiesFile() +" ...");
		PropertiesManager pm = new PropertiesManager(TestsFolders.getTestsProxyPropertiesFile());
		Properties proxyProperties;
		try {
			proxyProperties = pm.load();
		} catch (TelosysToolsException e) {
			throw new RuntimeException("ERROR : Cannot load proxy properties", e);
		}
		if ( proxyProperties == null ) {
			throw new RuntimeException("ERROR : No proxy properties");
		}
		return proxyProperties ;
		
//		Properties properties = new Properties();
//		// HTTP
//		properties.setProperty("http.proxyHost",  "proxyitx.idf.fr.ad.sotranet.net");
//		properties.setProperty("http.proxyPort",  "8080");
//		// HTTPS
//		properties.setProperty("https.proxyHost", "proxyitx.idf.fr.ad.sotranet.net");
//		properties.setProperty("https.proxyPort", "8080");
//		return properties ;
	}

	/**
	 * Returns a configuration for a specific proxy<br>
	 * 
	 * @return
	 */
	public static HttpClientConfig getSpecificProxyConfig() {
//		HttpProxy httpProxy  = new HttpProxy(HttpProxy.HTTP,  "proxy_host", 8080);
//		HttpProxy httpsProxy = new HttpProxy(HttpProxy.HTTPS, "proxy_host", 8080);
//		return new HttpClientConfig(httpProxy, httpsProxy);
		
		return new HttpClientConfig( getSpecificProxyProperties() );		
	}

	/**
	 * Returns a configuration for a 'localhost:8888' proxy<br>
	 * Useful for Fiddler 
	 * @return
	 */
	public static HttpClientConfig getLocalhostProxyConfig() {
		HttpProxy httpProxy  = new HttpProxy(HttpProxy.HTTP,  "localhost", 8888);
		HttpProxy httpsProxy = new HttpProxy(HttpProxy.HTTPS, "localhost", 8888);
		return new HttpClientConfig(httpProxy, httpsProxy);
	}

	/**
	 * Returns a configuration for no proxy<br>
	 * 
	 * @return
	 */
	public static HttpClientConfig getNoProxyConfig() {
		return new HttpClientConfig();
	}
	
	/**
	 * Returns an instance of HttpClient with the default configuration <br>
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient() {
		//HttpClientConfig config = getNoProxyConfig();
		HttpClientConfig config = getSpecificProxyConfig();
		return new HttpClient(config);		
	}
	
}
