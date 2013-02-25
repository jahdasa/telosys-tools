package org.telosys.tools.eclipse.plugin.config;

import org.telosys.tools.commons.config.ClassNameProvider;

// TODO : remove this class => change repository generation
public class ProjectClassNameProvider implements ClassNameProvider
{
	
//	private ProjectConfig _projectConfig = null ;

	//-------------------------------------------------------------------------------------------------------
	/**
	 * @param projectConfig
	 */
	public ProjectClassNameProvider(ProjectConfig projectConfig) 
	{
		super();
//		this._projectConfig = projectConfig;
	}
	
	//-------------------------------------------------------------------------------------------------------
//	/**
//	 * @param sBeanClassName
//	 * @param sClassNameExpr
//	 * @return
//	 */
//	private String getClassName(String sBeanClassName, String sClassNameExpr )
//	{
//		if ( sBeanClassName != null && sClassNameExpr != null ) 
//		{
//			String s = sBeanClassName.trim();
//			if (s.length() > 0) 
//			{
//				// Replace "${BEANNAME}" by s in the expression
//				return StrUtil.replaceVar(sClassNameExpr, "${" + ConfigDefaults.BEANNAME + "}", s);
//			}
//		}
//		return "";
//	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the VO List class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getVOListClassName(String sBeanClassName)
	{
//		if ( _projectConfig != null )
//		{
//			// Get the parameter or the given default value
//			String s = _projectConfig.getClassNameForVOList( ConfigDefaults.DEFAULT_LIST_CLASS_NAME );
//			return getClassName ( sBeanClassName, s );
//		}
		return "";
	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the DAO class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getDAOClassName(String sBeanClassName)
	{
//		if ( _projectConfig != null )
//		{
//			// Get the parameter or the given default value
//			String s = _projectConfig.getClassNameForDAO( ConfigDefaults.DEFAULT_DAO_CLASS_NAME );
//			return getClassName ( sBeanClassName, s );
//		}
		return "";
	}

	//-------------------------------------------------------------------------------------------------------
	/**
	 * Returns the Xml Mapper class name for the given VO Bean class name
	 * @param sBeanClassName
	 * @return
	 */
	public String getXmlMapperClassName(String sBeanClassName)
	{
//		if ( _projectConfig != null )
//		{
//			// Get the parameter or the given default value
//			String s = _projectConfig.getClassNameForXmlMapper( ConfigDefaults.DEFAULT_XML_MAPPER_CLASS_NAME );
//			return getClassName ( sBeanClassName, s );
//		}
		return "";
	}

}
