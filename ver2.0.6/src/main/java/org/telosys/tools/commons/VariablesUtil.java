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
	 * @return array of variables (never null)
	 */
	public static Variable[] getVariablesFromProperties( Properties properties )
	{
		LinkedList<Variable> list = new LinkedList<Variable>();
		Enumeration<Object> e = properties.keys() ;
		
		while ( e.hasMoreElements() )
		{
			String key = (String) e.nextElement() ;
			if ( key.startsWith( PROJECT_VARIABLE_PREFIX ) )
			{
				String sVarValue = properties.getProperty(key);
				String sVarName = key.substring( PROJECT_VARIABLE_PREFIX.length() ) ;
				Variable newItem = new Variable(sVarName, sVarValue );
				
				//--- Insert in ascending order 
				ListIterator<Variable> iter = list.listIterator();
				while ( iter.hasNext() ) 
				{
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
			return new Variable[0] ;
		}
	}
	
	public static Variable getVariableFromProperties( String variableName, Properties properties )
	{
		if ( null == variableName ) {
			throw new IllegalArgumentException("Variable name argument is null");
		}
		if ( null == properties ) {
			throw new IllegalArgumentException("Properties argument is null");
		}
		if ( variableName.length() > 0 ) {
			String sPropName = PROJECT_VARIABLE_PREFIX + variableName ;
			String value = (String) properties.get(sPropName);
			Variable variable = new Variable(variableName, value);
			return variable ;
		}
		else {
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
		int count = 0 ;
		if ( null == variables ) return 0 ;
		if ( null == properties ) {
			throw new IllegalArgumentException("Properties argument is null");
		}
		for ( Variable var : variables ) {
			putVariableInProperties(var, properties);
			count++ ;
		}
		return count ;
	}
	
	/**
	 * Put the given variable in the properties, using the standard project prefix <br> 
	 * @param variable
	 * @param properties
	 */
	public static void putVariableInProperties( Variable variable, Properties properties ) {
		if ( null == variable ) {
			throw new IllegalArgumentException("Variable argument is null");
		}
		if ( null == properties ) {
			throw new IllegalArgumentException("Properties argument is null");
		}
		String variableName = variable.getName().trim();
		if ( variableName.length() > 0 ) {
			String sPropName = PROJECT_VARIABLE_PREFIX + variableName ;
			properties.put(sPropName, variable.getValue() );
		}
	}
}
