package org.telosys.tools.eclipse.plugin.commons;

import java.net.URI;
import java.util.Properties;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class HttpProxyUtil {
	
	public static String initHttpProxyProperties(URI uri) {
		
//		ServiceTracker proxyTracker = new ServiceTracker(FrameworkUtil.getBundle(
//                this.getClass()).getBundleContext(), IProxyService.class.getName(), null);
		String log = "" ;
		BundleContext osgiBundleContext = FrameworkUtil.getBundle(HttpProxyUtil.class).getBundleContext() ;
		ServiceTracker proxyTracker = new ServiceTracker(osgiBundleContext, IProxyService.class.getName(), null);
        proxyTracker.open();
        try {
        	log = initHttpProxyProperties( uri, proxyTracker);
        }
        finally {
        	proxyTracker.close() ;
        }
        return log ;
	}
	
	private static String initHttpProxyProperties(URI uri, ServiceTracker proxyTracker) {
        StringBuffer sb = new StringBuffer();
        Object o = proxyTracker.getService();
        if ( o instanceof IProxyService ) {
        	IProxyService proxyService = (IProxyService) proxyTracker.getService();
        	
        	IProxyData[] proxyDataForHost = proxyService.select(uri);
        	
    		sb.append ( "Proxy Data defined in Eclipse : \n") ;
        	for (IProxyData data : proxyDataForHost) {
        		
        		sb.append ( ". Type '" + data.getType() + "' : ") ;
        		System.out.println("getType : "+ data.getType() ) ;
        		sb.append ( "host '" + data.getHost() + "', ") ;
        		sb.append ( "port " + data.getPort() + ", ") ;
        		sb.append ( "user '" + data.getUserId() + "', ") ;
        		sb.append ( "password '" + data.getPassword() + "' ") ;

        		boolean bSetInProperties = setSystemProperties(data);
        		
        		sb.append ( ( bSetInProperties ? " SET in system properties" : " NOT SET in system properties") ) ;
        		sb.append ( "\n" ) ;
            }
        }
        else {
        	MsgBox.error("Cannot get ProxyService ");
        	sb.append ( "ERROR : cannot get ProxyService (not an instance of IProxyService) \n" ) ;
        }
    	return sb.toString();
	}
	
	private static boolean setSystemProperties(IProxyData proxyData) {
		
		Properties systemProperties = System.getProperties();		
		String protocol = null ;
		if ( IProxyData.HTTP_PROXY_TYPE.equals(proxyData.getType()) ) {
			protocol = "http" ;
		}
		else if ( IProxyData.HTTPS_PROXY_TYPE.equals(proxyData.getType()) ) {
			protocol = "https" ;
		}		
		else {
			// IProxyData.SOCKS_PROXY_TYPE or other
			return false ; // Other protocol : nothing to do
		}
		systemProperties.setProperty( protocol + ".proxySet",  "true");
		systemProperties.setProperty( protocol + ".proxyHost", proxyData.getHost() );
		systemProperties.setProperty( protocol + ".proxyPort", String.valueOf(proxyData.getPort()) );
		if ( proxyData.getUserId() != null ) {
			// TODO
			// systemProperties.setProperty( protocol + ".xxxx",  proxyData.getUserId() );
		}
		if ( proxyData.getPassword() != null ) {
			// TODO
			// systemProperties.setProperty( protocol + ".xxxx",  proxyData.getPassword() );
		}
		return true ;
	}	
}
