package org.telosys.tools.commons.http;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class HttpUtil {
	
	public static void showSystemProxies(PrintStream out) {
		// Show system proxies on Windows : 
		// > netsh winhttp show proxy" 
		// The proxy settings for WinHTTP are not the proxy settings for Microsoft Internet Explorer

		out.println("Current system proxies : ");
		//System.setProperty("java.net.useSystemProxies", "true");
		
		List<Proxy> list = null;
		try {
		    list = ProxySelector.getDefault().select(new URI("http://foo/bar"));
		} 
		catch (URISyntaxException e) {
		    e.printStackTrace();
		}
		if (list != null) {
			int n = 0 ;
		    for ( Proxy proxy : list ) {
		    	n++;
		    	out.println("Proxy #" + n + " : " );
		    	// Type type = proxy.type();
		    	// . DIRECT : Represents a direct connection, or the absence of a proxy.
		    	// . HTTP   : Represents proxy for high level protocols such as HTTP or FTP.
		    	// . SOCKS  : Represents a SOCKS (V4 or V5) proxy.
	    		out.println(" proxy type (DIRECT|HTTP|SOCKS) : " + proxy.type());
	    		
		    	InetSocketAddress addr = (InetSocketAddress) proxy.address();
		    	if (addr == null) {
		    		out.println(" no address (InetSocketAddress)");
		    	} else {
		    		out.println(" proxy host : " + addr.getHostName());
		    		out.println(" proxy port : " + addr.getPort());
		    	}
		    }
		}
	}
}
