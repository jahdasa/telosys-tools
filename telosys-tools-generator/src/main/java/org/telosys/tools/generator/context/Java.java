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

import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.tools.LinesBuilder;

//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName=ContextName.JAVA,
		text = { 
				"Object providing a set of utility functions for JAVA language code generation",
				""
		},
		since = "2.0.7"
 )
//-------------------------------------------------------------------------------------
public class Java {

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'equals' method",
			"Tabulations are used for code indentation"
			},
		example={ 
			"$java.equalsMethod( $entity.name, $entity.attributes )" },
		parameters = { 
			"className : the Java class name (simple name or full name)",
			"fieldsList : list of fields to be used in the equals method"},
		since = "2.0.7"
			)
	public String equalsMethod( String className, List<JavaBeanClassAttribute> fieldsList ) {
		
		return equalsMethod( className , fieldsList, new LinesBuilder() ); 
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'equals' method",
			"Spaces are used for code indentation"
			},
		example={ 
			"$java.equalsMethod( $entity.name, $entity.attributes, 4 )" },
		parameters = { 
			"className : the Java class name (simple name or full name)",
			"fieldsList : list of fields to be used in the equals method",
			"indentSpaces : number of spaces to be used for each indentation level"},
		since = "2.0.7"
			)
	public String equalsMethod( String className, List<JavaBeanClassAttribute> fieldsList, int indentSpaces ) {
		
		return equalsMethod( className , fieldsList, new LinesBuilder(indentSpaces) ); 
	}
	
	//-------------------------------------------------------------------------------------
	private String equalsMethod( String className, List<JavaBeanClassAttribute> fieldsList, LinesBuilder lb ) {

		int indent = 1 ;
		lb.append(indent, "public boolean equals(Object obj) { ");
		
		indent++;
		lb.append(indent, "if ( this == obj ) return true ; ");
		lb.append(indent, "if ( obj == null ) return false ;");
		lb.append(indent, "if ( this.getClass() != obj.getClass() ) return false ; ");
		
		// Cast, ie : MyClass other = (MyClass) obj;
		lb.append( indent, className + " other = (" + className + ") obj; ");
		
		if ( fieldsList != null ) {
			for ( JavaBeanClassAttribute attribute : fieldsList ) {
				
				String attributeName = attribute.getName() ;
				lb.append(indent, "//--- Attribute " + attributeName );
				if ( attribute.isPrimitiveType() ) {
					if ( attribute.isFloatType() ) {
						// float
						lb.append(indent, 
								"if ( Float.floatToIntBits(" + attributeName 
								+ ") != Float.floatToIntBits(other." + attributeName + ") ) return false ; ");
					}
					else if ( attribute.isDoubleType() ) {
						// double 
						lb.append(indent, 
								"if ( Double.doubleToLongBits(" + attributeName 
								+ ") != Double.doubleToLongBits(other." + attributeName + ") ) return false ; ");
					}
					else {
						// char, byte, short, int, long, boolean 
						lb.append(indent, "if ( " + attributeName + " != other." + attributeName + " ) return false ; ");
					}
				}
				else {
					lb.append(indent, "if ( " + attributeName + " == null ) { ");
						lb.append(indent+1, "if ( other." + attributeName + " != null ) ");
							lb.append(indent+2, "return false ; ");
					lb.append(indent, "} else if ( ! " + attributeName + ".equals(other."+attributeName+") ) " );
						lb.append(indent+1, "return false ; ");
				}
			}
		} 
		
		lb.append(indent, "return true; ");
		
		indent--;
		lb.append(indent, "} ");

		return lb.toString();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns a string containing all the code for a Java 'hashCode' method",
				"Tabulations are used for code indentation"
				},
			example={ 
				"$java.hashCode( $entity.name, $entity.attributes )" },
			parameters = { 
				"className  : the Java class name (simple name or full name)",
				"fieldsList : list of fields to be used in the equals method"},
			since = "2.0.7"
				)
	public String hashCodeMethod( String className, List<JavaBeanClassAttribute> fieldsList ) {
		return hashCodeMethod( className , fieldsList, new LinesBuilder() ); 
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns a string containing all the code for a Java 'hashCode' method",
				"Spaces are used for code indentation"
				},
			example={ 
				"$java.hashCode( $entity.name, $entity.attributes, 4 )" },
			parameters = { 
				"className  : the Java class name (simple name or full name)",
				"fieldsList : list of fields to be used in the equals method",
				"indentSpaces : number of spaces to be used for each indentation level"},
			since = "2.0.7"
				)
	public String hashCodeMethod( String className, List<JavaBeanClassAttribute> fieldsList, int indentSpaces ) {
		return hashCodeMethod( className , fieldsList, new LinesBuilder(indentSpaces) ); 
	}
	
	//-------------------------------------------------------------------------------------
	private String hashCodeMethod( String className, List<JavaBeanClassAttribute> fieldsList, LinesBuilder lb ) {

		int indent = 1 ;
		lb.append(indent, "public int hashCode() { ");

		boolean long_temp = false ;
		indent++;
			lb.append(indent, "final int prime = 31; ");
			lb.append(indent, "int result = 1; ");
			lb.append(indent, "");
			
			if ( fieldsList != null ) {
				for ( JavaBeanClassAttribute attribute : fieldsList ) {
					
					String attributeName = attribute.getName() ;
					lb.append(indent, "//--- Attribute " + attributeName );
					if ( attribute.isPrimitiveType() ) {
						//--- Primitive types
						if ( attribute.isBooleanType() ) {
							// boolean
							lb.append(indent, "result = prime * result + (" + attributeName + " ? 1231 : 1237 );");
						}
						else if ( attribute.isLongType() ) {
							// long (must be converted to int)
							lb.append(indent, "result = prime * result + (int) (" + attributeName 
									+ " ^ (" + attributeName + " >>> 32));");
						}
						else if ( attribute.isFloatType() ) {
							// float
							lb.append(indent, "result = prime * result + Float.floatToIntBits(" + attributeName + ");");
						}
						else if ( attribute.isDoubleType() ) {
							// double
							if ( long_temp == false ) {
								lb.append(indent, "long temp;");
								long_temp = true ;
							}
							lb.append(indent, "temp = Double.doubleToLongBits(" + attributeName + ");");
							lb.append(indent, "result = prime * result + (int) (temp ^ (temp >>> 32));");
						}
						else {
							// char, byte, short, int 
							lb.append(indent, "result = prime * result + " + attributeName + ";");
						}
					}
					else {
						//--- Objects : just use the 'hashCode' method
						lb.append(indent, "result = prime * result + ((" + attributeName + " == null) ? 0 : " 
								+ attributeName + ".hashCode() ) ; ");
					}
				}
			} 

			lb.append(indent, "");
			lb.append(indent, "return result; ");
		indent--;
		lb.append(indent, "} ");

		return lb.toString();
	}

}
