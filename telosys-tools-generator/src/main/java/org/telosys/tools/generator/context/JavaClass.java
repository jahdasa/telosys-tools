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

import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.doc.VelocityMethod;


/**
 * Standard informations about a Java Class <br>
 * It provides : <br>
 * . the short class name (without package)<br>
 * . the class package <br>
 * . the full class name (with package)<br>
 * . the super class (if the class extends another class)<br>
 * <br>
 * 
 * @author Laurent GUERIN
 *
 */
public class JavaClass 
{	
	private final static String   NONE = "" ;
	
	private String     _sName        = NONE ;
	private String     _sPackage     = NONE ;
	private String     _sFullName    = NONE ;
	private String     _sSuperClass  = NONE ;	
	
	/**
	 * Constructor based on a full class name ( package + "." + class name )
	 * @param sFullName : e.g. 'pkg1.pkg2.MyClass'
	 */
	public JavaClass(String sFullName ) 
	{
		_sFullName = sFullName ;
		if ( sFullName != null )
		{
			int i = sFullName.lastIndexOf('.');
			if ( i >= 0 )
			{
				_sName    = sFullName.substring(i+1);
				_sPackage = sFullName.substring(0, i);
			}		
			else
			{
				//--- No '.' => only class name
				_sName    = sFullName ;
				_sPackage = NONE ;
			}
		}
	}
	
	/**
	 * Constructor based on separated class name and package 
	 * @param shortClassName : e.g. 'MyClass'
	 * @param sPackage : e.g. 'pkg1.pkg2'
	 */
	public JavaClass(String shortClassName, String sPackage) 
	{
		_sName = shortClassName ;
		_sPackage = sPackage;
		_sFullName = sPackage + "." + _sName;
	}
	
	/**
	 * Constructor based on separated class name and package, with a superclass for inheritence 
	 * @param shortClassName : e.g. 'MyClass'
	 * @param sPackage : e.g. 'pkg1.pkg2'
	 * @param superClass 
	 */
	public JavaClass(String shortClassName, String sPackage, String superClass) 
	{
		this(shortClassName, sPackage);
		_sSuperClass = superClass ;
	}
	
	/**
	 * Returns the Java class name without the package ( ie : "MyClass" )
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns the class name for the entity without the package ( ie : \"MyClass\" )"
		},
		example="$entity.name"
	)
	public String getName()
	{
		return _sName ;
	}
	
	/**
	 * Returns the Java class package or void ( ie : "my.package" or "" )
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns the package name (or void) for the entity ( ie : \"my.package\" or \"\" )"
		},
		example="$entity.package"
	)
	public String getPackage()
    {
        return _sPackage ;
    }
	
	/**
	 * Returns the super class of this Java class 
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns the super class for the entity's class (or void if none)"
		},
		example="$entity.superClass"
	)
	public String getSuperClass()
    {
        return _sSuperClass ;
    }

	/**
	 * Returns the Java class full name ( ie : "my.package.MyClass" )
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns the full class name for the entity (ie : \"my.package.MyClass\" )"
		},
		example="$entity.fullName"
	)
	public String getFullName()
    {
		return _sFullName ;
    }
	
    /**
     * Returns the Java line instruction for the toString() method
     * @return
     */
    public String getToStringInstruction()
    {
    	return "\"JavaClass : '" + getName() + "' \"";
    }
    
    public String toStringMethodCodeLines( int iLeftMargin )
    {
    	String leftMargin = GeneratorUtil.blanks(iLeftMargin);
    	return leftMargin + "return \"JavaClass : '" + getName() + "' \" ; \n";
    }
    
	/* (non-Javadoc)
	 * Same as getName() 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		// NB : must return only the class name => do not change
		// Usage example in ".vm" : ${beanClass}.class 
		return getName() ;
	}
	
}
