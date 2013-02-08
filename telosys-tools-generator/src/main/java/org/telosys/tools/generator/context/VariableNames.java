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

import java.util.LinkedList;

import org.telosys.tools.commons.Variable;

/**
 * Reserved variable names
 *  
 * @author L. Guerin
 *
 */
public class VariableNames {

	// TODO : update the variable names list
	private final static String[] RESERVED_NAMES =
	{
		//--- Generator variables
		"generator", "today",
		
		//--- Project variables
		"project",
		
		//--- Entity variables
		"class", 
		"beanClass", "listClass", "daoClass", "xmlClass",
		
		//--- Wizards variables
		"context", 
		"screendata",
		"triggers"
	} ;
	
	/**
	 * Returns all the variable names used in the Velocity Context
	 * @return
	 */
	public final static String[] getReservedNames()
	{
		return RESERVED_NAMES ;
	}
	
	/**
	 * Returns true if the given string is a variable name used in the Velocity Context
	 * @param s
	 * @return
	 */
	public final static boolean isReservedName(String s)
	{
		if ( s != null )
		{
			for ( int i = 0 ; i < RESERVED_NAMES.length ; i++ )
			{
				if ( s.equals( RESERVED_NAMES[i] ) ) return true ;
			}
		}
		return false ;
	}
	
	/**
	 * Returns an array containing the invalid variable names, <br>
	 * or null if all the names are valid.
	 * @param variables
	 * @return
	 */
	public final static String[] getInvalidVariableNames(Variable[] variables)
	{
		LinkedList<String> invalidVariables = null ; 
		if ( null == variables ) return null ;
		for ( int i = 0 ; i < variables.length ; i++ )
		{
			Variable v = variables[i];
			String sVarName = v.getName();
			if ( isReservedName(sVarName) )
			{
				if ( null == invalidVariables )
				{
					invalidVariables = new LinkedList<String>();
				}
				invalidVariables.add(sVarName);
			}
		}
		if ( invalidVariables != null ) return (String[]) invalidVariables.toArray(new String[0]);
		else return null ;
	}
}
