package org.telosys.tools.commons.http;

public class HttpProxy {

	public final static String ALL   = "*" ;
	public final static String HTTP  = "http" ;
	public final static String HTTPS = "https" ;
	public final static String FTP   = "ftp" ;
	
	private final String protocol ;
	private final String host ;
	private final int    port ;
	private final String user ;
	private final String password ;
	private final String nonProxyHosts ;
	
	public HttpProxy(String protocol, String host, int port) {
		super();
		this.protocol = protocol ;
		this.host = host;
		this.port = port;
		this.user     = null;
		this.password = null;
		this.nonProxyHosts = null ;
	}
	
	public HttpProxy(String protocol, String host, int port, String nonProxyHosts) {
		super();
		this.protocol = protocol ;
		this.host = host;
		this.port = port;
		this.user     = null;
		this.password = null;
		this.nonProxyHosts = nonProxyHosts ;
	}
	
	public HttpProxy(String protocol, String host, int port, String user, String password ) {
		super();
		this.protocol = protocol ;
		this.host     = host;
		this.port     = port;
		this.user     = user;
		this.password = password;
		this.nonProxyHosts = null ;
	}
	
	public HttpProxy(String protocol, String host, int port, String user, String password, String nonProxyHosts ) {
		super();
		this.protocol = protocol ;
		this.host     = host;
		this.port     = port;
		this.user     = user;
		this.password = password;
		this.nonProxyHosts = nonProxyHosts ;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getNonProxyHosts() {
		return nonProxyHosts;
	}
}
