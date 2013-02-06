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

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;

public class VariablesUtil 
{
    public final static String PROJECT_VARIABLE_PREFIX  = "ProjectVariable.";
		
	/**
	 * Get an array of variables from the given properties, using the standard project prefix <br>
	 * @param properties
	 * @return
	 */
	public static Variable[] getVariablesFromProperties( Properties properties )
	{
		return getVariablesFromProperties(properties, PROJECT_VARIABLE_PREFIX);
	}
	
	/**
	 * Get an array of variables from the given properties
	 * @param properties
	 * @param prefix
	 * @return
	 */
	public static Variable[] getVariablesFromProperties( Properties properties, String prefix )
	{
		LinkedList<Variable> list = new LinkedList<Variable>();
		Enumeration<Object> e = properties.keys() ;
		
		while ( e.hasMoreElements() )
		{
			String key = (String) e.nextElement() ;
			if ( key.startsWith( prefix ) )
			{
				String sVarValue = properties.getProperty(key);
				String sVarName = key.substring( prefix.length() ) ;
				Variable newItem = new Variable(sVarName, sVarValue );
				
				//--- Insert in ascending order 
				ListIterator<Variable> iter = list.listIterator();
				while ( iter.hasNext() ) 
				{
					//Object item = iter.next();
					Variable item = iter.next();
					if ( newItem.compareTo(item) <= 0 ) 
					{
						// newItem should come BEFORE item in the list.
						// Move the iterator back one space so that it points to the correct insertion point,
						// and end the loop.
						iter.previous();
						break;
					} 
				}
				iter.add(newItem);
			}
		}
		
		//--- Convert to VariableItem[]
		int listSize = list.size() ;
		if ( listSize > 0 )
		{
			Variable[] items = new Variable[ listSize ] ;
			for ( int i = 0 ; i < listSize ; i++ )
			{
				items[i] = (Variable) list.get(i);
			}
			return items ;
		}
		else
		{
			return null ;
		}
	}
	
	/**
	 * Put the given variables in the properties, using the standard project prefix <br>
	 * All the given variable names are supposed to be valid.
	 * @param variables
	 * @param properties
	 * @return the number of variables stored in the properties
	 */
	public static int putVariablesInProperties( Variable[] variables, Properties properties )
	{
		return putVariablesInProperties( variables, properties, PROJECT_VARIABLE_PREFIX );
	}
	
	/**
	 * Put the given variables in the properties, using the default prefix <br>
	 * All the given variable names are supposed to be valid.
	 * @param variables
	 * @param properties
	 * @param prefix
	 * @return the number of variables stored in the properties
	 */
	public static int putVariablesInProperties( Variable[] variables, Properties properties, String prefix )
	{
		int count = 0 ;
		if ( null == variables ) return 0 ;
		for ( int i = 0 ; i < variables.length ; i++ )
		{
			Variable v = variables[i];
			String sVarName = v.getName();
			String sPropName = prefix + sVarName ;
			if ( sPropName.trim().length() > 0 )
			{
				properties.put(sPropName, v.getValue() );
				count++;
			}
		}
		return count ;
	}
}
