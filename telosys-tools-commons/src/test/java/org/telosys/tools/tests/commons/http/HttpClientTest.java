package org.telosys.tools.tests.commons.http;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.telosys.tools.commons.http.HttpClient;
import org.telosys.tools.commons.http.HttpResponse;

/**
 * HttpClient test case.
 * 
 * NB : if HttpClient is created without configuration the last configuration is still active 
 * ( because stored as System Properties )
 *  
 * @author L. Guerin
 *
 */
public class HttpClientTest extends TestCase {

	private Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cache-Control",   "max-age=0");
		headers.put("Accept-Encoding", "gzip,deflate");		
		headers.put("User-Agent",      "Apache-HttpClient/4.1.3 (java 1.5)");
		return headers ;
	}

	private HttpClient getHttpClient() {
//		//HttpClientConfig config = getNoProxyConfig();
//		HttpClientConfig config = getSpecificProxyConfig();
//		return new HttpClient(config);		
		return HttpTestConfig.getHttpClient();
	}
	
	//==========================================================================================

	public void testGet() throws Exception {
		//showSystemProxies();
		System.out.println("--- Test http GET --- ");		
		HttpClient c = getHttpClient();
		Map<String, String> headers = getHeaders();
		
		doGet(c, "http://myhttp.info/", headers, 200);
		doGet(c, "https://www.google.fr", headers, 200);
	}
	
	public void doGet(HttpClient httpClient, String url, Map<String, String> headers, int expectedRetCode) throws Exception {
		System.out.println("GET " + url + " ...");
		HttpResponse response = httpClient.get(url, headers);
		assertEquals(expectedRetCode, response.getStatusCode());
		System.out.println(" Ret Code = " +response.getStatusCode() );
		System.out.println(" ---------- " );
	}
	
	//==========================================================================================
	
	public void testHead() throws Exception {
		//showSystemProxies();
		System.out.println("--- Test http HEAD --- ");		
		HttpClient c = getHttpClient();
		Map<String, String> headers = getHeaders();
		
		doHead(c, "http://myhttp.info/", headers, 200);
		doHead(c, "https://www.google.fr", headers, 200);
	}
	
	public void doHead(HttpClient httpClient, String url, Map<String, String> headers, int expectedRetCode) throws Exception {
		System.out.println("HEAD " + url + " ...");
		HttpResponse response = httpClient.head(url, headers);
		assertEquals(expectedRetCode, response.getStatusCode());
		System.out.println(" Ret Code = " +response.getStatusCode() );
		System.out.println(" ---------- " );
	}
	
	//==========================================================================================
	public void testDownload() throws Exception {
		//showSystemProxies();
		System.out.println("Test DOWNLOAD... ");
		HttpClient c = getHttpClient();
		
		String urlString = "https://api.github.com/users/telosys/repos" ;
		long r = c.downloadFile(urlString, "D:/tmp/download/file1.tmp");
		System.out.println("File " + urlString + " donwloaded (" + r + " bytes)");
	}
}
