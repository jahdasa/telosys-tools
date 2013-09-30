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
package org.telosys.tools.generator.context ;

import java.util.LinkedList;

import org.telosys.tools.commons.JavaClassUtil;

public class Util {
	
    public static String buildGetter(String attName, String sLongType) {
		String s = attName.substring(0, 1).toUpperCase()+attName.substring(1, attName.length());
		if ( "boolean".equals(sLongType) )
		{
			return "is"+s;
		}
		else
		{
			return "get"+s;
		}
	}

    public static String buildSetter(String attName) {
		return "set"+attName.substring(0, 1).toUpperCase()+attName.substring(1, attName.length());
	}

    private static boolean inList (String sLongType, LinkedList<String> fullNames) 
    {
    	for ( String s : fullNames ) {
    		if ( s != null )
    		{
    			if ( s.equals(sLongType) )
    			{
    				return true ; // Found 
    			}
    		}
    	}
    	return false ; // Not found
    }    
    /**
     * Determines the shortest type to use according with the given "not imported types" list
     * @param sLongType
     * @param notImportedTypes
     * @return
     */
    public static String shortestType(String sLongType, LinkedList<String> notImportedTypes) 
    {
    	if ( notImportedTypes != null )
    	{
    		if ( inList(sLongType, notImportedTypes) )
    		{
    			return sLongType ; // Keep the "long type" because not imported
    		}    		
    	}
		// No full names to keep => reduce it to short name
		return JavaClassUtil.shortName(sLongType);
	}

}
