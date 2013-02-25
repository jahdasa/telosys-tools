/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.generator.context;

import org.telosys.tools.commons.XmlUtil;

/**
 * Set of functions usable in Velocity template with $fn.functionName(...) 
 * 
 * @author Laurent Guerin
 *
 */
public class Fn {

// USELESS 
//	public boolean isNull (Object o) {
//		return null == o ;
//	}
//	
//	public boolean isNotNull (Object o) {
//		return ! isNull(o);
//	}
	
	/**
	 * Returns true is the string is null or void or contains only spaces
	 * @param s
	 * @return
	 */
	public boolean isBlank (String s) {
		if ( s != null ) {
			if ( s.trim().length() > 0 ) {
				return false ; 
			}
		}
		return true ;
	}
	public boolean isNotBlank (String s) {
		return ! isBlank (s)  ;
	}
	
	/**
	 * Returns the same string with a double quote at the beginning and at the end
	 * @param s
	 * @return
	 */
	public String quote(String s) {
		return "\"" + s + "\"" ;
	}

	/**
	 * Converts the given string in a XML string (escape special characters &, <, >, etc ) 
	 * @param s
	 * @return
	 */
	public String escapeXml(String s) {
		return XmlUtil.escapeXml(s) ;
	}

	public String getTab() {
		return "\t" ;
	}
	public String tab(int n) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0 ; i < n ; i++ ) {
			sb.append("\t");
		}
		return sb.toString();
	}
	
	
}
