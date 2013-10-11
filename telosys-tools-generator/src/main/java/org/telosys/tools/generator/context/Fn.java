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

import java.util.List;

import org.telosys.tools.commons.XmlUtil;
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;

/**
 * Set of functions usable in Velocity template with $fn.functionName(...) 
 * 
 * @author Laurent Guerin
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName=ContextName.FN,
		text = { 
				"Object providing a set of utility functions ",
				""
		},
		since = "2.0.3"
 )
//-------------------------------------------------------------------------------------
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
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the given string is 'blank' ",
			"(true if the string is null or void or only composed of blanks)"
			},
		parameters = { 
			"s : the string to be tested" 
			},
		since = "2.0.3"
	)
	public boolean isBlank (String s) {
		if ( s != null ) {
			if ( s.trim().length() > 0 ) {
				return false ; 
			}
		}
		return true ;
	}
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the given string is not 'blank' ",
			"(true if the string is not null, not void and not only composed of blanks)"},
		parameters = { "s : the string to be tested" }
			)
	public boolean isNotBlank (String s) {
		return ! isBlank (s)  ;
	}
	
	/**
	 * Returns the same string with a double quote at the beginning and at the end
	 * @param s
	 * @return
	 */
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Adds a double quote character at the beginning and at the end of the given string "
			},
		parameters = { "s : the string to be quoted" }
			)
	public String quote(String s) {
		return "\"" + s + "\"" ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(text={	
			"Returns the XML string for the given string",
			"Replaces special characters (&, <, >, etc) by their corresponding XML notation "
			},
			parameters = { "s : the string to be escaped" },
			deprecated=false
			)
	public String escapeXml(String s) {
		return XmlUtil.escapeXml(s) ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(text={	
			"Returns a single tabulation character " 
			}
	)
	public String getTab() {
		return "\t" ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(text={	
			"Returns N tabulation characters "
			},
			parameters = { "n : the number of tabulations to be returned" }
			)
	public String tab(int n) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0 ; i < n ; i++ ) {
			sb.append("\t");
		}
		return sb.toString();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing a list of field names separated by a comma"
			},
		example={ 
				"$fn.argumentsList( $entity.attributes )",
				"Returns : 'id, firstName, lastName, age' "},
		parameters = { "fields : list of fields to be added in the arguments list" },
		since = "2.0.5"
			)
	public String argumentsList( List<JavaBeanClassAttribute> fieldsList ) {
		if ( fieldsList != null ) {
			StringBuilder sb = new StringBuilder();
			int n = 0 ;
			for ( JavaBeanClassAttribute field : fieldsList ) {
				if ( n > 0 ) sb.append(", ");
				sb.append( field.getName() ) ;
				n++;
			}
			return sb.toString();
		} 
		else {
			return "null" ;
		}
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing a list of fields (type and name) separated by a comma"
			},
		example={ 
			"$fn.argumentsListWithType( $entity.attributes )",
			"Returns : 'int id, String firstName, String lastName, int age' "},
		parameters = { "fields : list of fields to be added in the arguments list" },
		since = "2.0.5"
			)
	public String argumentsListWithType( List<JavaBeanClassAttribute> fieldsList ) {
		if ( fieldsList != null ) {
			StringBuilder sb = new StringBuilder();
			int n = 0 ;
			for ( JavaBeanClassAttribute field : fieldsList ) {
				if ( n > 0 ) sb.append(", ");
				sb.append( field.getType() ) ;
				sb.append( " " ) ;
				sb.append( field.getName() ) ;
				n++;
			}
			return sb.toString();
		} 
		else {
			return "null" ;
		}
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing a list of fields getters separated by a comma"
			},
		example={ 
			"$fn.argumentsListWithGetter( 'person', $entity.attributes )",
			"Returns : 'person.getId(), person.getFirstName(), person.getLastName(), person.getAge()' "},
		parameters = { 
			"object : name of the object providing the getters",
			"fields : list of fields to be added in the arguments list" },
		since = "2.0.5"
			)
	public String argumentsListWithGetter( String objectName, List<JavaBeanClassAttribute> fieldsList ) {
		if ( fieldsList != null ) {
			StringBuilder sb = new StringBuilder();
			int n = 0 ;
			for ( JavaBeanClassAttribute field : fieldsList ) {
				if ( n > 0 ) sb.append(", ");
				sb.append( objectName ) ;
				sb.append( "." ) ;
				sb.append( field.getGetter() ) ;
				sb.append( "()" ) ;
				n++;
			}
			return sb.toString();
		} 
		else {
			return "null" ;
		}
	}
	
	//==============================================================================================
	// Version 2.0.7
	//==============================================================================================
	@VelocityMethod(
			text={	
				"Checks if the given expression is TRUE, else throws an Exception with the given message"
				},
			example={ 
				"$fn.assertTrue( $entity.attributes, \"...\" )"
				},
			parameters = { 	"expr : expression to be evaluted as TRUE", 
							"message : the error message if not true" },
			since = "2.0.7"
				)
	public void assertTrue(boolean expr, String message) throws Exception {
		if ( expr != true ) {
			throw new Exception(message) ;
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Checks if the given expression is FALSE, else throws an Exception with the given message"
				},
			example={ 
				"$fn.assertFalse( $entity.attributes, \"...\" )"
				},
			parameters = { 	"expr : expression to be evaluted as FALSE", 
							"message : the error message if not false" },
			since = "2.0.7"
				)
	public void assertFalse(boolean expr, String message) throws Exception {
		if ( expr != false ) {
			throw new Exception(message) ;
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Checks if the given List/Array is NOT VOID, else throws an Exception with the given message"
				},
			example={ 
				"$fn.assertNotVoid( $entity.attributes, \"No attributes\" )"
				},
			parameters = { 	"collection : List or Array", 
							"message : the error message if void" },
			since = "2.0.7"
				)
	public void assertNotVoid(Object o, String message) throws Exception {
		if ( isVoid(o, "assertNotVoid" ) == true ) {
			throw new Exception(message) ;
		}
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Checks if the given List/Array is VOID, else throws an Exception with the given message"
				},
			example={ 
				"$fn.assertVoid( $entity.attributes, \"Attributes Not void\" )"
				},
			parameters = { 	"collection : List or Array", 
							"message : the error message if not void" },
			since = "2.0.7"
				)
	public void assertVoid(Object o, String message) throws Exception {
		if ( isVoid(o, "assertVoid" ) == false ) {
			throw new Exception(message) ;
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns TRUE if the given List/Array is NOT VOID"
				},
			example={ 
				"#if ( $fn.isNotVoid( $entity.attributes ) ) "
				},
			parameters = { 	"collection : List or Array" },
			since = "2.0.7"
				)
	public boolean isNotVoid(Object o) throws Exception {
		return ! isVoid(o, "isNotVoid" ) ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns TRUE if the given List/Array is VOID"
				},
			example={ 
				"#if ( $fn.isVoid( $entity.attributes ) ) "
				},
			parameters = { 	"collection : List or Array" },
			since = "2.0.7"
				)
	public boolean isVoid(Object o) throws Exception {
		return isVoid(o, "isVoid" ) ;
	}

	//-------------------------------------------------------------------------------------
	public boolean isVariableDefined (String variableName) {
		// TODO
		return true ;
	}
	
	//-------------------------------------------------------------------------------------
	private boolean isVoid(Object o, String functionName ) throws Exception {
		if ( o != null ) {
			if ( o instanceof List ) {
				List<?> list = (List<?>) o ;
				return list.size() == 0 ;
			}
			else if ( o instanceof Object[] ) {
				Object[] array = (Object[]) o ;
				return array.length == 0 ;
			}
			else {
				throw new Exception(functionName + " : list or array expected") ;
			}
		}
		else {
			throw new Exception(functionName + " : list or array is null") ;
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Throws an Exception with the given message"
				},
			example={ 
				"#if ( $fn.isVoid( $entity.attributes ) ) ",
				"$fn.error( \"No attributes\" )",
				"#end"
				},
			parameters = { 	"message : the error message" },
			since = "2.0.7"
				)
	public void error (String message) throws Exception  {
		throw new Exception(message) ;
	}
		
}
