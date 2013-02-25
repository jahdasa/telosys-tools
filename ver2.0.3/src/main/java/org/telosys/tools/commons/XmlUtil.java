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
package org.telosys.tools.commons;

public class XmlUtil {

    private static final int HIGHEST_SPECIAL = '>';
    
    private static char[][] xmlRepresentation = new char[HIGHEST_SPECIAL + 1][];
    static {
        xmlRepresentation['&']  = "&amp;".toCharArray();
        xmlRepresentation['<']  = "&lt;".toCharArray();
        xmlRepresentation['>']  = "&gt;".toCharArray();
        xmlRepresentation['"']  = "&#034;".toCharArray();
        xmlRepresentation['\''] = "&#039;".toCharArray();
    }
    
    //-----------------------------------------------------------------------------------
    /** 
     * Private constructor (no instantiation) 
     */
    private XmlUtil()
    {
    }
    
    //-----------------------------------------------------------------------------------	
    /**
     * Converts the given original string to an XML string.<br>
     * Replaces "&", "<", ">", "double quote" and "single quote" 
     * @param originalString
     * @return 
     */
    public static String escapeXml(String originalString) {
    	
    	if ( null == originalString ) {
    		return "" ;
    	}
    	
        int start = 0;
        int length = originalString.length();
        char[] arrayBuffer = originalString.toCharArray();
        StringBuffer escapedStringBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= HIGHEST_SPECIAL) {
                char[] escaped = xmlRepresentation[c];
                if (escaped != null) {
                    // new StringBuffer to build the xml string
                    if (start == 0) {
                        escapedStringBuffer = new StringBuffer(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedStringBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedStringBuffer.append(escaped);
                }
            }
        }
        // escaping not required
        if (start == 0) {
            return originalString;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedStringBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedStringBuffer.toString();
    }
	
}
