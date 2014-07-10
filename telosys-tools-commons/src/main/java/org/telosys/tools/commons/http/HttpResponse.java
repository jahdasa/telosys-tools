/**
 *  Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.commons.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponse {

	protected static final boolean STORE_RESPONSE_BODY = true ;
	
	protected static final boolean DO_NOT_STORE_RESPONSE_BODY = true ;
	
	private int    statusCode    = 0 ;
	private String statusMessage = "" ;
	
	private int    contentLength   = 0 ;
	private String contentType     = "" ;
	private String contentEncoding = "" ;

	private Map<String, List<String>> headerFields = null ;
	
	private byte[] bodyContent = new byte[0];
	
	public HttpResponse(HttpURLConnection connection) throws Exception 
	{
		try {
			contentType     = connection.getContentType();
			contentLength   = connection.getContentLength();
			contentEncoding = connection.getContentEncoding();
			
			statusCode      = connection.getResponseCode();
			statusMessage   = connection.getResponseMessage();
			
			bodyContent = readResponseBody(connection);
			
		} catch (IOException e) {
			throw new Exception("Cannot create HttpResponse", e); 
		}
		headerFields = connection.getHeaderFields();
	}
	
	private byte[] readResponseBody( HttpURLConnection connection ) throws IOException
	{
		byte[] buffer = new byte[1024] ; 
		int totalLength = 0 ;

		int len = 0 ;
		InputStream is = connection.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream (1024);
		while ( ( len = is.read(buffer) ) > 0 )
		{
			baos.write(buffer, 0, len);
			totalLength = totalLength + len ;
		}
		baos.close();
		return baos.toByteArray();
	}
	
	public int getStatusCode()
	{
		return statusCode ;
	}
	
	public String getStatusMessage()
	{
		return statusMessage ;
	}
	
	public int getContentLength()
	{
		return contentLength ;
	}
	
	public String getContentType()
	{
		return contentType ;
	}
	
	public String getContentEncoding()
	{
		return contentEncoding ;
	}
	
	public byte[] getBodyContent()
	{
		return bodyContent ;
	}
	
	public String getHeader(String name)
	{
		//return (String) headerFields.get(name);
		List<String> values = headerFields.get(name);
		if ( values != null ) {
			if ( values.size() > 0 ) {
				return values.get(0);
			}
		}
		return null ;
	}
	
	public Map<String, List<String>> getHeaderMap()
	{
		return  headerFields;
	}
}
