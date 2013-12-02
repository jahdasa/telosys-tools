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
package org.telosys.tools.generator.variables;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;

import org.telosys.tools.generator.ContextName;

public class VariablesUtil 
{
    public final static String PROJECT_VARIABLE_PREFIX  = "ProjectVariable.";
		
    //---------------------------------------------------------------------------------------------------------
	/**
	 * Returns all the variables available for the project from the given properties <br>
	 * The variables array contains the specific variables AND the standard variables ($SRC, $ENTITY_PKG, ...)
	 * @param prop
	 * @return
	 * @since 2.0.7
	 */
	public static Variable[] getAllVariablesFromProperties( Properties prop )
	{
    	//--- All variables : specific project variables + folders 
    	Hashtable<String, String> allVariables = new Hashtable<String, String>();
    	
    	//--- 1) Project specific variables (defined by user)
    	Variable[] specificVariables = VariablesUtil.getVariablesFromProperties( prop );
    	for ( Variable v : specificVariables ) {
    		allVariables.put(v.getName(), v.getValue());
    	}
    	//--- 2) Packages and folders ( at the end to override specific variables if any )
    	allVariables.put( ContextName.ROOT_PKG,   prop.getProperty(ContextName.ROOT_PKG,    "") ); // v 2.0.6
    	allVariables.put( ContextName.ENTITY_PKG, prop.getProperty(ContextName.ENTITY_PKG,  "") ); // v 2.0.6
    	
    	allVariables.put( ContextName.SRC,      prop.getProperty(ContextName.SRC,      "") );
    	allVariables.put( ContextName.RES,      prop.getProperty(ContextName.RES,      "") );
    	allVariables.put( ContextName.WEB,      prop.getProperty(ContextName.WEB,      "") );
    	allVariables.put( ContextName.TEST_SRC, prop.getProperty(ContextName.TEST_SRC, "") );
    	allVariables.put( ContextName.TEST_RES, prop.getProperty(ContextName.TEST_RES, "") );
    	allVariables.put( ContextName.DOC,      prop.getProperty(ContextName.DOC,      "") );
    	allVariables.put( ContextName.TMP,      prop.getProperty(ContextName.TMP,      "") );
    	
    	//--- 3) Get all variables to build the array
    	LinkedList<Variable> variablesList = new LinkedList<Variable>();
    	for ( String varName : allVariables.keySet() ) {
    		String varValue = allVariables.get(varName) ;
    		variablesList.add( new Variable( varName, varValue) ) ;
    	}
    	//--- Convert list to array
    	Variable[] allVariablesArray = variablesList.toArray( new Variable[variablesList.size()] );
    	
		return allVariablesArray ;
	}
	
    //---------------------------------------------------------------------------------------------------------
    /**
	 * Returns the specific variables defined in the given properties, using the standard project prefix <br>
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
		/***
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
		***/
		return list.toArray( new Variable[list.size()] );
	}
	
    //---------------------------------------------------------------------------------------------------------
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
	
    //---------------------------------------------------------------------------------------------------------
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
	
    //---------------------------------------------------------------------------------------------------------
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
