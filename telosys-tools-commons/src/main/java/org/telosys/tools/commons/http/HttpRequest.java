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

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

	private final static byte[] VOID_CONTENT = new byte[0];
	
	private final String url ;

	private final Map<String, String> headers ;
	
	private byte[]  content = VOID_CONTENT ;
	
	public HttpRequest(String url) 
	{
		this.url = url ;
		this.headers = new HashMap<String, String>();
	}
	
	public String getURL() {
		return url ;
	}
	
	public void setHeader(String name, String value) {
		this.headers.put(name, value);
	}
	
	public Map<String, String> getHeadersMap() {
		return  this.headers;
	}

	public void setContent(String contentToSet) {
		if ( contentToSet != null ) {
			this.content = contentToSet.getBytes() ;
		}
		else {
			this.content = VOID_CONTENT ;
		}
	}
	public void setContent(byte[] contentToSet) {
		if ( contentToSet != null ) {
			this.content = contentToSet ;
		}
		else {
			this.content = VOID_CONTENT ;
		}
	}
	public byte[] getContent() {
		return  this.content;
	}
}
