package org.telosys.tools.commons.http;

import java.util.Properties;

import org.telosys.tools.commons.StrUtil;

public class HttpClientConfig {

	private final boolean   useSystemProxies ;
	private final HttpProxy httpProxy ;
	private final HttpProxy httpsProxy ;
	
	public HttpClientConfig() {
		super();
		this.httpProxy  = null ;
		this.httpsProxy = null;
		this.useSystemProxies = true ;
	}

	public HttpClientConfig(HttpProxy httpProxy) {
		super();
		this.httpProxy  = httpProxy;
		this.httpsProxy = null;
		this.useSystemProxies = false ;
	}

	public HttpClientConfig(HttpProxy httpProxy, HttpProxy httpsProxy) {
		super();
		this.httpProxy  = httpProxy;
		this.httpsProxy = httpsProxy;
		this.useSystemProxies = false ;
	}
	
	public HttpClientConfig(Properties properties) {
		super();
		if (properties != null) {
			this.httpProxy  = getProxy(properties, "http");
			this.httpsProxy = getProxy(properties, "https");
			this.useSystemProxies = false ;
		}
		else {
			this.httpProxy  = null ;
			this.httpsProxy = null;
			this.useSystemProxies = true ;
		}
	}

	private HttpProxy getProxy(Properties properties, String protocol) {
		String host     = properties.getProperty( protocol+ ".proxyHost");
		String port     = properties.getProperty( protocol+ ".proxyPort");
		String user     = properties.getProperty( protocol+ ".proxyUser"); // can be null
		String password = properties.getProperty( protocol+ ".proxyPassword"); // can be null
		if ( host != null && port != null ) {
			int numPort = StrUtil.getInt(port,0);
			return new HttpProxy(protocol, host, numPort, user, password) ;
		}
		return null ;
	}
	
	public boolean isUseSystemProxies() {
		return useSystemProxies;
	}

//	public void setUseSystemProxies(boolean useSystemProxies) {
//		this.useSystemProxies = useSystemProxies;
//	}

	public HttpProxy getHttpProxy() {
		return httpProxy;
	}

	public HttpProxy getHttpsProxy() {
		return httpsProxy;
	}
	
	
}
