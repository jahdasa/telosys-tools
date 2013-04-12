package org.telosys.tools.eclipse.plugin.commons;

import java.net.URI;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class HttpProxyUtil {
	
	public void f1(URI uri) {
		ServiceTracker proxyTracker = new ServiceTracker(FrameworkUtil.getBundle(
                this.getClass()).getBundleContext(), IProxyService.class
                .getName(), null);
        proxyTracker.open();
        try {
        	f ( uri, proxyTracker);
        }
        finally {
        	proxyTracker.close() ;
        }
	}
	
	public void f(URI uri, ServiceTracker proxyTracker) {
		
        
        Object o = proxyTracker.getService();
        if ( o instanceof IProxyService ) {
        	IProxyService proxyService = (IProxyService) proxyTracker.getService();
        	
        	IProxyData[] proxyDataForHost = proxyService.select(uri);
        	
        	for (IProxyData data : proxyDataForHost) {
        		System.out.println("getType : "+ data.getType() ) ;
        		System.out.println("getHost : "+ data.getHost() ) ;
        		System.out.println("getPort : "+ data.getPort() ) ;
        		System.out.println("getUserId : " + data.getUserId() ) ;
        		System.out.println("getPassword : " + data.getPassword() ) ;
        		if ( IProxyData.HTTP_PROXY_TYPE.equals(data.getType()) ) {
        			System.out.println("--- HTTP type");
        		}
        		if ( IProxyData.HTTPS_PROXY_TYPE.equals(data.getType()) ) {
        			System.out.println("--- HTTPS type");
        		}
        		if ( IProxyData.SOCKS_PROXY_TYPE.equals(data.getType()) ) {
        			System.out.println("--- SOCKS type");
        		}
        		
                if (data.getHost() != null) {
                    System.setProperty("http.proxySet", "true");
                    System.setProperty("http.proxyHost", data.getHost());
                }
                if (data.getHost() != null) {
                    System.setProperty("http.proxyPort", String.valueOf(data
                            .getPort()));
                }
            }
        }
        else {
        	MsgBox.error("Cannot get ProxyService ");
        	return ;
        }
        
	}
	
	
}
