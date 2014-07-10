/**
 *  Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.commons.config;

import org.telosys.tools.commons.StrUtil;

public class DefaultClassNameProvider implements ClassNameProvider
{
	
	//-------------------------------------------------------------------------------------------------------
	/**
	 * @param sBeanClassName
	 * @param sClassNameExpr
	 * @return
	 */
	private String getClassName(String sBeanClassName, String sClassNameExpr )
	{
		if ( sBeanClassName != null && sClassNameExpr != null ) 
		{
			String s = sBeanClassName.trim();
			if (s.length() > 0) 
			{
				// Replace "${BEANNAME}" by s in the expression
				return StrUtil.replaceVar(sClassNameExpr, "${" + ConfigDefaults.BEANNAME + "}", s);
			}
		}
		return "";
	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the VO List class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getVOListClassName(String sBeanClassName)
	{
		return getClassName ( sBeanClassName, ConfigDefaults.DEFAULT_LIST_CLASS_NAME );
	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the DAO class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getDAOClassName(String sBeanClassName)
	{
		return getClassName ( sBeanClassName, ConfigDefaults.DEFAULT_DAO_CLASS_NAME );
	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the Xml Mapper class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getXmlMapperClassName(String sBeanClassName)
	{
		return getClassName ( sBeanClassName, ConfigDefaults.DEFAULT_XML_MAPPER_CLASS_NAME );
	}

}
