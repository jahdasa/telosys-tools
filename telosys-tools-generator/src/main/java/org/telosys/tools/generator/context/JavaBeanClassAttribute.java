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

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.JavaTypeUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.jdbctypes.JdbcTypes;
import org.telosys.tools.commons.jdbctypes.JdbcTypesManager;
import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.tools.AnnotationsForBeanValidation;
import org.telosys.tools.generator.context.tools.AnnotationsForJPA;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.persistence.util.RepositoryConst;


/**
 * Context class for a BEAN ATTRIBUTE ( with or without database mapping )
 *  
 * @author Laurent GUERIN
 */
public class JavaBeanClassAttribute 
{
	public final static int NO_MAXLENGTH   = -1 ;

    public final static int NO_DATE_TYPE   = 0 ;
    public final static int DATE_ONLY      = 1 ;
    public final static int TIME_ONLY      = 2 ;
    public final static int DATE_AND_TIME  = 3 ;
    
    private final static String TYPE_INT  = "int" ;
    private final static String TYPE_NUM  = "num" ;
    private final static String TYPE_DATE = "date" ;
    private final static String TYPE_TIME = "time" ;
    
	private String  _sName = null ;  // attribute name 
	private String  _sType = null ;  // Short java type without package, without blank, eg : "int", "BigDecimal", "Date"
	private String  _sFullType = null ;  // Full java type with package, : "java.math.BigDecimal", "java.util.Date"
	
	private String  _sInitialValue = null;
	private String  _sGetter = null ;
	private String  _sSetter = null ;
	
	private String  _sDefaultValue = null ;
	
	//--- Database infos -------------------------------------------------
    private boolean _bKeyElement       = false ;  // True if primary key
    private boolean _bUsedInForeignKey = false ;
    private boolean _bAutoIncremented  = false ;  // True if auto-incremented by the database
    private String  _sDataBaseName     = null ;  // Column name in the DB table
    private String  _sDataBaseType     = null ;  // Column type in the DB table
    private int     _iJdbcTypeCode     = 0 ;     // JDBC type for this column
    private int     _iDatabaseSize     = 0 ;     // Size of this column (if Varchar ) etc..
    private String  _sDatabaseDefaultValue = null ;  
    private boolean _bDatabaseNotNull = false ;  // True if "not null" in the database
    
    //--- Further info for ALL ---------------------------------------
    private boolean _bNotNull = false ;
    
    //--- Further info for BOOLEAN -----------------------------------
    private String  _sBooleanTrueValue  = null ; // eg "1", ""Yes"", ""true""
    private String  _sBooleanFalseValue = null ; // eg "0", ""No"",  ""false""
    
    //--- Further info for DATE/TIME ---------------------------------
    private int     _iDateType = DATE_ONLY ;  // By default only DATE
    private boolean _bDatePast   = false ;
    private boolean _bDateFuture = false ;
    private boolean _bDateBefore = false ;
    private String  _sDateBeforeValue = null ;
    private boolean _bDateAfter  = false ;
    private String  _sDateAfterValue  = null ;

    //--- Further info for NUMBER ------------------------------------
    private String  _sMinValue = null ; 
    private String  _sMaxValue = null ; 

    //--- Further info for STRING ------------------------------------
    private boolean _bLongText  = false ;  // True if must be stored as a separate tag in the XML flow
    private boolean _bNotEmpty  = false ;
    private boolean _bNotBlank  = false ;
    private String  _sMinLength = null ; 
    private String  _sMaxLength = null ; 
    private String  _sPattern = null ; 
    
    
	//--- JPA KEY Generation infos -------------------------------------------------
    private boolean _bGeneratedValue = false ;  // True if GeneratedValue ( annotation "@GeneratedValue" )
	private String  _sGeneratedValueStrategy  = null ; // "AUTO", "IDENTITY", "SEQUENCE", "TABLE" 
	private String  _sGeneratedValueGenerator = null ;
	
    private boolean _bSequenceGenerator = false ;  // True if SequenceGenerator ( annotation "@SequenceGenerator" )
	private String  _sSequenceGeneratorName = null ;
	private String  _sSequenceGeneratorSequenceName = null ;
	private int     _iSequenceGeneratorAllocationSize = -1;

    private boolean _bTableGenerator = false ;  // True if TableGenerator ( annotation "@TableGenerator" )
	private String  _sTableGeneratorName = null ;
	private String  _sTableGeneratorTable = null ;
	private String  _sTableGeneratorPkColumnName = null ;
	private String  _sTableGeneratorValueColumnName = null ;
	private String  _sTableGeneratorPkColumnValue = null ;

	//--- Annotations : Bean Validation JSR303 -------------------------------------------------
	private AnnotationsForBeanValidation _annotationsBeanValidation = null ;
	
	private AnnotationsForJPA            _annotationsJPA = null ;
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Constructor to create a Java Class Attribute without model <br>
	 * This constructor is designed to be used by the WIZARDS GENERATOR<br>
	 * 
	 * @param sName internal (private) java attribute name
	 * @param sType the shortest type to use ( "String", "int", "BigDecimal", "Date", "java.util.Date", ... ), <br>
	 *              can be a full type ( eg : if "java.util.Date" and "java.sql.Date" are used in the same class ) 
	 * @param sFullType standard full type with package ( "java.lang.String", "int", "java.math.BigDecimal", ... )
	 * @param sInitialValue
	 * @param sGetter
	 * @param sSetter
	 */
	public JavaBeanClassAttribute(String sName, String sType, String sFullType, String sInitialValue, String sGetter, String sSetter) 
	{
		_sName = sName ; 
		_sType = StrUtil.removeAllBlanks(sType);    
		_sFullType = StrUtil.removeAllBlanks(sFullType);  
		_sInitialValue = sInitialValue;
		_sGetter = sGetter ;
		_sSetter = sSetter ;		
	}
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Constructor to create a Java Class Attribute from the given model-column definition  
	 * @param column the column of the repository model
	 */
	public JavaBeanClassAttribute(final Column column) 
	{
		if (column != null) {
			
			_sName = column.getJavaName();
			_sGetter = Util.buildGetter(_sName, _sType);
			_sSetter = Util.buildSetter(_sName);
			
			// TODO gerer les duplicatedShortNames dans Util.shortestType mais dans ce cas a quoi cela peut servir ????
			_sType = StrUtil.removeAllBlanks(Util.shortestType(column.getJavaType(), new LinkedList<String>()));
			_sFullType = StrUtil.removeAllBlanks(column.getJavaType());
			
			// TODO _sInitialValue
			_sDefaultValue    = column.getJavaDefaultValue();
			
			_sDataBaseName    = column.getDatabaseName() ;
	        _sDataBaseType    = column.getDatabaseTypeName() ;
	        _iJdbcTypeCode    = column.getJdbcTypeCode() ;
	        _bKeyElement      = column.isPrimaryKey() ;
	        _bUsedInForeignKey = column.isForeignKey();
	        _bAutoIncremented = column.isAutoIncremented();
	        _iDatabaseSize    = column.getDatabaseSize() ;
	        _sDatabaseDefaultValue = column.getDatabaseDefaultValue(); 
	        _bDatabaseNotNull = column.isDatabaseNotNull();
	        
			//--- Further info for ALL
	        _bNotNull = column.getJavaNotNull();
	        
			//--- Further info for BOOLEAN 
	        _sBooleanTrueValue   = column.getBooleanTrueValue().trim() ;
			_sBooleanFalseValue  = column.getBooleanFalseValue().trim() ;
			
			//--- Further info for NUMBER 
		    _sMinValue = column.getMinValue() ; 
		    _sMaxValue = column.getMaxValue() ; 

			//--- Further info for STRING 
	        _bLongText  = column.getLongText() ;
	        _bNotEmpty  = column.getNotEmpty();
	        _bNotBlank  = column.getNotBlank();
	        _sMaxLength = column.getMaxLength();
	        _sMinLength = column.getMinLength();
	        _sPattern   = column.getPattern();
	        
        
			//--- Further info for DATE/TIME 
			if ( RepositoryConst.SPECIAL_DATE_ONLY.equalsIgnoreCase(column.getDateType()) ) {
				_iDateType = DATE_ONLY;
			} else if ( RepositoryConst.SPECIAL_TIME_ONLY.equalsIgnoreCase(column.getDateType()) )  {
				_iDateType = TIME_ONLY;
			} else if ( RepositoryConst.SPECIAL_DATE_AND_TIME.equalsIgnoreCase(column.getDateType()) )  {
				_iDateType = DATE_AND_TIME;
			} else {
				_iDateType =  -1  ; // Default : UNKNOWN
			}
	        _bDatePast   = column.isDatePast();
	        _bDateFuture = column.isDateFuture();
	        _bDateBefore = column.isDateBefore();
	        _sDateBeforeValue = column.getDateBeforeValue();
	        _bDateAfter  = column.isDateAfter();
	        _sDateAfterValue  = column.getDateAfterValue();
	        
			//--- Further info for JPA 
	        if ( column.isAutoIncremented() ) {
			    _bGeneratedValue = true ;
				_sGeneratedValueStrategy  = null ; // "AUTO" is the default strategy 
				_sGeneratedValueGenerator = null ;
	        } 
	        else {
				if (column.getGeneratedValue() != null) {
				    _bGeneratedValue = true ;
					_sGeneratedValueStrategy  = column.getGeneratedValue().getStrategy();
					_sGeneratedValueGenerator = column.getGeneratedValue().getGenerator();
				}
	        }
				        
			if (column.getTableGenerator() != null) {
			    _bTableGenerator = true ;
				_sTableGeneratorName = column.getTableGenerator().getName();
				_sTableGeneratorTable = column.getTableGenerator().getTable();
				_sTableGeneratorPkColumnName = column.getTableGenerator().getPkColumnName();
				_sTableGeneratorValueColumnName = column.getTableGenerator().getValueColumnName();
				_sTableGeneratorPkColumnValue = column.getTableGenerator().getPkColumnValue();
			}

			if (column.getSequenceGenerator() != null) {
			    _bSequenceGenerator = true;
				_sSequenceGeneratorName = column.getSequenceGenerator().getName();
				_sSequenceGeneratorSequenceName = column.getSequenceGenerator().getSequenceName();
				_iSequenceGeneratorAllocationSize = column.getSequenceGenerator().getAllocationSize();
			}
			
			//--- EXTENSION for Bean Validation Annotations 
			_annotationsBeanValidation = new AnnotationsForBeanValidation(this);

			//--- EXTENSION for JPA Annotations 
			_annotationsJPA = new AnnotationsForJPA(this);

			//--- EXTENSION for JPA Annotations  with embedded id (no @Id)
			//_annotationsJPAEmbeddedID = new AnnotationsForJPAEmbeddedID(this);

		} else {
			// TODO exception ???
		}
	}
    
	//-----------------------------------------------------------------------------------------------
	protected void forceType ( String sTypeToUse )
	{
		if ( sTypeToUse != null )
		{
			_sType = sTypeToUse ;
		}
	}
	
	public String getName()
	{
		return _sName;
	}

	public String formatedName(int iSize)
    {
        String s = _sName ;
        String sTrailingBlanks = "";
        int iDelta = iSize - s.length();
        if (iDelta > 0) // if needs trailing blanks
        {
            sTrailingBlanks = GeneratorUtil.blanks(iDelta);
        }
        return s + sTrailingBlanks;
    }

	public String getType()
	{
		return _sType;
	}

	public String getWrapperType()
	{
		if ( null == _sType ) return "UnknownType" ;
		
		final String typeObjectTmp = _sType.trim();
		if ("byte".equals(typeObjectTmp)) {
			return "Byte";
		} else if ("short".equals(typeObjectTmp)) {
			return "Short";
		} else if ("int".equals(typeObjectTmp)) {
			return "Integer";
		} else if ("long".equals(typeObjectTmp)) {
			return "Long";
		} else if ("float".equals(typeObjectTmp)) {
			return "Float";
		} else if ("double".equals(typeObjectTmp)) {
			return "Double";
		} else if ("boolean".equals(typeObjectTmp)) {
			return "Boolean";
		} else if ("char".equals(typeObjectTmp)) {
			return "Character";
		} else {
			return typeObjectTmp;
		}
	}


	public int getDateType()
	{
		return _iDateType ;
	}

	public String formatedType(int iSize)
    {
        String sTrailingBlanks = "";
        int iDelta = iSize - _sType.length();
        if (iDelta > 0) // if needs trailing blanks
        {
            sTrailingBlanks = GeneratorUtil.blanks(iDelta);
        }
        return _sType + sTrailingBlanks;
    }	
    
	public String getFullType()
	{
		return _sFullType;
	}
	
	public boolean hasInitialValue()
	{
		return _sInitialValue != null ;
	}
	public String getInitialValue()
	{
		return _sInitialValue;
	}
	
	public String getGetter()
	{
		return _sGetter;
	}
	
	public String getSetter()
	{
		return _sSetter;
	}

	//----------------------------------------------------------------------
	// Database 
	//----------------------------------------------------------------------
    public String getDatabaseName()
    {
        return _sDataBaseName;
    }

    public String getDatabaseType()
    {
        return _sDataBaseType;
    }

    public int getDatabaseSize()
    {
        return _iDatabaseSize ;
    }

    public boolean hasDatabaseDefaultValue()
    {
    	if ( _bAutoIncremented ) return false ; // No default value for auto-incremented fields
        if ( _sDatabaseDefaultValue != null )
        {
        	if ( _sDatabaseDefaultValue.length() > 0 ) return true ;
        }
        return false ;
    }
    
    public String getDatabaseDefaultValue()
    {
    	if ( hasDatabaseDefaultValue() ) return _sDatabaseDefaultValue ;
        return "" ;
    }
    
	//----------------------------------------------------------------------
    public boolean isDatabaseNotNull()
    {
        return _bDatabaseNotNull;
    }
    /**
     * Synonym for usage without "()"
     * @return
     */
    public boolean getIsDatabaseNotNull()
    {
        return isDatabaseNotNull();
    }
    
	//----------------------------------------------------------------------
    public int getJdbcTypeCode()
    {
        return _iJdbcTypeCode ;
    }

	//----------------------------------------------------------------------
    /**
     * Returns the recommended Java type for the JDBC type 
     * @return
     */
    public String getJdbcRecommendedJavaType()
    {
    	JdbcTypes types = JdbcTypesManager.getJdbcTypes();
    	return types.getJavaTypeForCode(_iJdbcTypeCode, _bDatabaseNotNull );
    }

	//----------------------------------------------------------------------
    /**
     * Returns true if the attribute is a Database Primary Key element
     * @return 
     */
    public boolean isKeyElement()
    {
        return _bKeyElement;
    }
    /**
     * Synonym for usage without "()"
     * @return
     */
    public boolean getIsKeyElement()
    {
        return isKeyElement();
    }

	//----------------------------------------------------------------------
    /**
     * Returns true if the attribute is used in (at least) one Foreign Key 
     * @return 
     */
    public boolean isUsedInForeignKey()
    {
        return _bUsedInForeignKey ;
    }
    /**
     * Synonym for usage without "()"
     * @return
     */
    public boolean getIsUsedInForeignKey()
    {
        return isKeyElement();
    }

    /**
     * Returns true if this attribute is involved in a link Foreign Key <br>
     * Useful for JPA, to avoid double mapping ( FK field and owning side link )
     * @param linksArray - list of the links to be checked 
     * @return
     */
    public boolean isUsedInLinkJoinColumn( List<JavaBeanClassLink> links )
    {
    	if ( null == _sDataBaseName ) {
    		return false ; // No mapping 
    	}
    	
		for ( JavaBeanClassLink link : links ) {
			if ( link.isOwningSide() && link.hasJoinColumns() ) {
				String[] joinColumns = link.getJoinColumns() ;
				if ( joinColumns != null ) {
					for ( int i = 0 ; i < joinColumns.length ; i++ ) {
						String colName = joinColumns[i];
						if ( _sDataBaseName.equalsIgnoreCase( colName ) ) {
							//--- Yes : this attribute's mapping column is a 'Join Column' 
							return true ;
						}
					}
				}
			}
		}
		return false ;
    }

	//----------------------------------------------------------------------
    /**
     * Returns true if the attribute is auto-incremented by the Database engine
     * @return 
     */
    public boolean isAutoIncremented()
    {
        return _bAutoIncremented;
    }
    
    /**
     * Synonym for usage without "()"
     * @return
     */
    public boolean getIsAutoIncremented()
    {
        return isAutoIncremented();
    }

	//----------------------------------------------------------------------
    /**
     * Returns true if the attribute has a "Not Null" constraint at the Java level
     * @return 
     */
    public boolean isNotNull()
    {
        return _bNotNull;
    }
    /**
     * Synonym for usage without "()"
     * @return
     */
    public boolean getIsNotNull()
    {
        return isNotNull();
    }

	//----------------------------------------------------------------------
    /**
     * Returns the java type starting by an Upper Case <br>
     * eg : returns "Double" for "double"/"Double", "Int" for "int", ...
     * @return 
     */
    public String javaTypeStartingByUC()
    {
    	return firstCharUC(_sType);
    }
    
    /**
     * Returns the "QueryContext setter" method name to use to set a query parameter <br>
     * Examples : setParamString, setParamByte, setParamBooleanAsInt, etc..
     * @return
     */
    public String getQuerySetter()
    {
    	if ( "boolean".equals(_sType) || "Boolean".equals(_sType) ) 
    	{
    		if ( isJdbcInteger() )
    		{
    			// 'boolean' or 'Boolean' stored as 'integer' in the DataBase
    			return "setParamBooleanAsInt" ;
    		}
    		if ( isJdbcString() )
    		{
    			return "setParamBooleanAsString" ;
    		}        		
    	}
    	if ( "byte[]".equals(_sType) ) 
    	{
    		if ( isJdbcBlob() )
    		{
    			return "setParamBlob" ;
    		}
    		return "setParamBytes" ;
    	}
    	if ( "Date".equals(_sType) ) 
    	{
    		if ( isJdbcTime())
    		{
    			return "setParamTime" ;
    		}
    		if ( isJdbcTimestamp() )
    		{
    			return "setParamTimestamp" ;
    		}
    	}
    	
    	if ( "Integer".equals(_sType) )     return "setParamInt" ; 
    	if ( "BigDecimal".equals(_sType) )  return "setParamBigDecimal" ; 
    	if ( "BigInteger".equals(_sType) )  return "setParamBigInteger" ; 
    	
		//--- Standard type name that can be used "as is" to buil the "setter"
		return "setParam" + firstCharUC(_sType) ;
    }
    
    public boolean getNeedsQuerySetterParams() // Velocity : $attrib.needsQuerySetterParams
    {
    	return needsParamsForBoolean();
    }
    
	public String getQuerySetterParams()
    {
    	if ( needsParamsForBoolean() )
    	{
    		//--- Specific storage type for boolean 
			// eg : ", 1, 0" or ", 'Yes', 'No'"
			String p1 = _sBooleanTrueValue ;
			String p2 = _sBooleanFalseValue ;
			if ( isJdbcString() )
			{
				p1 = addQuotes(p1);
				p2 = addQuotes(p2);
			}
			return p1 + ", " + p2 ;
    	}
		return "" ;
    }
    
    /**
     * Returns the "QueryContext getter" method name to use to get a query result <br>
     * Examples : setParamString, setParamByte, setParamBooleanAsInt, etc..
     * @return
     */
    public String getQueryGetter()
    {
		//--- There's a specific storage type for this field 
    	if ( "boolean".equals(_sType) || "Boolean".equals(_sType) ) 
    	{
    		if ( isJdbcInteger() )
    		{
    			return "getResultBooleanFromInt" ;
    		}
    		if ( isJdbcString() )
    		{
    			return "getResultBooleanFromString" ;
    		}
    	}
    	if ( "byte[]".equals(_sType) ) 
    	{
    		if ( isJdbcBlob() )
    		{
    			return "getResultBlobAsByteArray" ;
    		}
    	}
    	if ( "Date".equals(_sType) ) 
    	{
    		if ( isJdbcTime() )
    		{
    			return "getResultTimeAsDate" ;
    		}
    		if ( isJdbcTimestamp() )
    		{
    			return "getResultTimestampAsDate" ;
    		}
    	}
    	
    	//--- Standard Java "wrapper objects"
    	if ( "Boolean".equals(_sType) ) return "getResultBooleanObject" ;
    	if ( "Byte".equals(_sType) )    return "getResultByteObject" ;
    	if ( "Double".equals(_sType) )  return "getResultDoubleObject" ;
    	if ( "Float".equals(_sType) )   return "getResultFloatObject" ;
    	if ( "Integer".equals(_sType) ) return "getResultIntObject" ;
    	if ( "Long".equals(_sType) )    return "getResultLongObject" ;
    	if ( "Short".equals(_sType) )   return "getResultShortObject" ;
    	
    	//--- Date 
    	if ( "Date".equals(_sType) ) 
    	{
    		if ( "java.util.Date".equals(_sFullType) ) return "getResultDate" ;
    		if ( "java.sql.Date".equals(_sFullType) )  return "getResultDateSql" ;
    	}
    	
    	//--- Primitive types arrays
    	if ( "byte[]".equals(_sType) )      return "getResultBytes" ;
    	
    	if ( "BigDecimal".equals(_sType) )  return "getResultBigDecimal" ; 
    	if ( "BigInteger".equals(_sType) )  return "getResultBigInteger" ; 

		//--- No specific storage type : use the Java Type itself 
		return "getResult" + firstCharUC(_sType) ;
    }
    
    public boolean getNeedsQueryGetterParams() // Velocity : $attrib.needsQueryGetterParams
    {
    	return needsParamsForBoolean();
    }
    
    /**
     * Returns the "QueryContext getter" optional parameters <br>
     * The true value for booleans 
     * 
     * @return
     */
    public String getQueryGetterParams()
    {
    	if ( needsParamsForBoolean() )
    	{
    		//--- String or Integer storage for boolean 
			// eg : ", 1, 0" or ", 'Yes', 'No'"
			String p1 = _sBooleanTrueValue ;
			if ( isJdbcString() )
			{
				p1 = addQuotes(p1); // e.g. : "Yes" 
			}
			return p1 ;
    	}
		return "" ;
    }

    /**
     * Returns the GUI maximum input length if any ( else returns "" )
     * @return
     */
    public String getGuiMaxLength() 
    {
    	//--- Max length depending on the Java type
    	if ( "byte".equals(_sType)  || "Byte".equals(_sType)    ) return  "4" ; // -128 to +127
    	if ( "short".equals(_sType) || "Short".equals(_sType)   ) return  "6" ; // -32768 to +32767
    	if ( "int".equals(_sType)   || "Integer".equals(_sType) ) return "11" ; // -2147483648 to +2147483647
    	if ( "long".equals(_sType)  || "Long".equals(_sType)    ) return "20" ; // -9223372036854775808 to +9223372036854775807
    	
    	if ( "double".equals(_sType) || "Double".equals(_sType) ) return "20" ; // Arbitrary fixed value like long
    	if ( "float".equals(_sType)  || "Float".equals(_sType)  ) return "20" ; // Arbitrary fixed value like long
    	
    	if ( "BigDecimal".equals(_sType) ) return "20" ; // Arbitrary fixed value like long
    	if ( "BigInteger".equals(_sType) ) return "20" ; // Arbitrary fixed value like long
    	
    	if ( "Date".equals(_sType) ) return "10" ; // "YYYY-MM-DD", "DD/MM/YYYY", etc ...
    	if ( "Time".equals(_sType) ) return "8" ; // "HH:MM:SS"

    	//--- Max length from Database column size (only for String)
    	if ( "String".equals(_sType) )
    	{
    		return voidIfNull ( _sMaxLength ) ;
    	}
		return "";
    }
    
    /**
     * Shortcut for Velocity attribute syntax : $var.guiMaxLengthAttribute 
     * @return
     */
    public String getGuiMaxLengthAttribute() 
    {
    	return guiMaxLengthAttribute() ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * For Velocity function call syntax : $var.guiMaxLengthAttribute() 
     * @return
     */
    public String guiMaxLengthAttribute() 
    {
    	return guiMaxLengthAttribute("maxlength") ;
    }
    
    /**
     * For Velocity function call syntax : $var.guiMaxLengthAttribute('maxlength') 
     * @param attributeName
     * @return
     */
    public String guiMaxLengthAttribute(String attributeName) 
    {
    	if ( attributeName != null )
    	{
    		String s = getGuiMaxLength();
    		if ( ! StrUtil.nullOrVoid(s) )
    		{
    			return attributeName + "=\"" + s + "\"" ;
    		}
    	}
    	return "";
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "maximum" length if any, else returns "" 
     * @return
     */
    public String getMaxLength() 
    {
    	return voidIfNull(_sMaxLength) ;
    }
    /**
     * Returns the "minimum" length if any, else returns "" 
     * @return
     */
    public String getMinLength() 
    {
    	return voidIfNull(_sMinLength) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "pattern" (Reg Exp) if any, else returns "" 
     * @return
     */
    public String getPattern() 
    {
    	return voidIfNull(_sPattern) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "minimum" value if any, else returns "" 
     * @return
     */
    public String getMinValue() 
    {
    	return voidIfNull(_sMinValue) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "maximum" value if any, else returns "" 
     * @return
     */
    public String getMaxValue() 
    {
    	return voidIfNull(_sMaxValue) ;
    }
    //-------------------------------------------------------------------------------------------
    /**
     * Synonym for Velocity attribute syntax : $var.guiMinMaxAttributes 
     * @return
     */
    public String getGuiMinMaxAttributes() 
    {
    	return guiMinMaxAttributes() ;
    }
    
    /**
     * For Velocity function call syntax : $var.guiMinMaxAttributes() 
     * @return
     */
    public String guiMinMaxAttributes() 
    {
    	return guiMinMaxAttributes("min", "max") ;
    }

    //-------------------------------------------------------------------------------------------
    /**
     * For Velocity function call syntax : $var.guiMinMaxAttributes('min','max') 
     * @param attributeName
     * @return
     */
    public String guiMinMaxAttributes(String minAttributeName, String maxAttributeName  ) 
    {
    	if ( minAttributeName != null && maxAttributeName != null )
    	{
    		String sMin = getMinValue();
    		String sMinAttr = "" ;
    		if ( ! StrUtil.nullOrVoid(sMin) )
    		{
    			sMinAttr = minAttributeName + "=\"" + sMin + "\"" ;
    		}
    		
    		String sMax = getMaxValue();
    		String sMaxAttr = "" ;
    		if ( ! StrUtil.nullOrVoid(sMax) )
    		{
    			sMaxAttr = maxAttributeName + "=\"" + sMax + "\"" ;
    		}
    		return sMinAttr + " " + sMaxAttr ;
    	}
    	return "" ;
    }

    /**
     * Returns the GUI "type" value if any, else returns "" 
     * @return
     */
    public String getGuiType() 
    {
    	//--- type="int"
    	if ( "byte".equals(_sType)  || "Byte".equals(_sType)    ) return TYPE_INT ;
    	if ( "short".equals(_sType) || "Short".equals(_sType)   ) return TYPE_INT ; 
    	if ( "int".equals(_sType)   || "Integer".equals(_sType) ) return TYPE_INT ; 
    	if ( "long".equals(_sType)  || "Long".equals(_sType) )    return TYPE_INT ; 
    	if ( "BigInteger".equals(_sType) )   return TYPE_INT ;

    	//--- type="num"
    	if ( "float".equals(_sType)  || "Float".equals(_sType) )    return TYPE_NUM ; 
    	if ( "double".equals(_sType) || "Double".equals(_sType) )   return TYPE_NUM ; 
    	if ( "BigDecimal".equals(_sType) )   return TYPE_NUM ;
    	
    	//--- type="date"
    	if ( "Date".equals(_sType) )   return TYPE_DATE ;
    	
    	//--- type="time"
    	if ( "Time".equals(_sType) )   return TYPE_TIME ;
    	
    	return "" ;
    }
    
    /**
     * For Velocity attribute syntax : $var.guiTypeAttribute
     * @return
     */
    public String getGuiTypeAttribute() 
    {
    	return guiTypeAttribute() ;
    }
    /**
     * For Velocity function call syntax : $var.guiTypeAttribute() 
     * @return
     */
    public String guiTypeAttribute() 
    {
    	return guiTypeAttribute("type") ;
    }
    /**
     * For Velocity function call syntax : $var.guiTypeAttribute('type') 
     * @param attributeName
     * @return
     */
    public String guiTypeAttribute(String attributeName) 
    {
    	if ( attributeName != null )
    	{
    		String s = getGuiType();
    		if ( ! StrUtil.nullOrVoid(s) )
    		{
    			return attributeName + "=\"" + s + "\"" ;
    		}
    	}
    	return "";
    }
    
	//-----------------------------------------------------------------------------
	public boolean isDatePast() {
		return _bDatePast;
	}
	public boolean getIsDatePast() {
		return isDatePast();
	}

	public boolean isDateFuture() {
		return _bDateFuture;
	}
	public boolean getIsDateFuture() {
		return isDateFuture();
	}
	
	public boolean isDateBefore() {
		return _bDateBefore;
	}
	public boolean getIsDateBefore() {
		return isDateBefore();
	}
	
	public String getDateBeforeValue() {
		return _sDateBeforeValue;
	}
	
	public boolean isDateAfter() {
		return _bDateAfter;
	}
	public boolean getIsDateAfter() {
		return isDateAfter();
	}
	
	public String getDateAfterValue() {
		return _sDateAfterValue;
	}

	//-----------------------------------------------------------------------------
    
    /**
     * Returns true if the attribute is a long text that must be stored in a separate XML tag
     * @return 
     */
    public boolean isLongText()
    {
        return _bLongText;
    }
	/**
	 * Shortcut for usage without "()"
	 * @return
	 */
	public boolean getIsLongText() // Velocity : $attrib.isLongText
	{
		return isLongText();
	}

    public boolean isNotEmpty()
    {
        return _bNotEmpty;
    }
    public boolean getIsNotEmpty()
    {
        return isNotEmpty();
    }
    
    public boolean isNotBlank()
    {
        return _bNotBlank;
    }
    public boolean getIsNotBlank()
    {
        return isNotBlank();
    }
	//-----------------------------------------------------------------------------
    /**
     * Returns true if the attribute need a conversion to be stored as an XML attribute value<br>
     * ( true if the attribute needs a "attributeString(...)" processing )
     * @return
     */
    public boolean getNeedsXmlConversion() // Velocity : $attrib.needsXmlConversion
    {
    	// always true except if primitive type and not "boolean"
		if ( "boolean".equals(_sType)) return true ; // boolean needs convertion
    	if ( isPrimitiveType() ) 
		{
    		//--- Primitive types (except boolean ) : doesn't need convertion
    		return false ; 
		}
    	else
    	{
    		//--- If not a primitive type => need conversion 
        	return true ;
    	}
    }
    
    public boolean getNeedsXmlConversionParams() // Velocity : $attrib.needsXmlConversionParams
    {
		if ( "java.util.Date".equals(_sFullType) ) return true ;
		return false ;
    }

    /**
     * Returns the additional parameters required for the XML String conversion if any <br>
     * Returns 'params' for "attributeString(arg1, params )"
     * Useful for Date conversion attributeString(arg1, params )
     * @return
     */
    public String getXmlConversionParams() // Velocity : $attrib.xmlConversionParams
    {
		if ( "java.util.Date".equals(_sFullType) || "java.util.Calendar".equals(_sFullType) ) 
		{
			switch ( _iDateType )
			{
			case DATE_ONLY : return "DATE_ONLY" ;
			case TIME_ONLY : return "TIME_ONLY" ;
			case DATE_AND_TIME : return "DATE_AND_TIME" ;
			}
		}
		return "" ;
    }
    
    public boolean getHasDefaultValue() // Velocity : $attrib.hasDefaultValue
    {
    	return ( _sDefaultValue != null ) ;
    }
    public String getDefaultValue() // Velocity : ${attrib.defaultValue}
    {
    	return _sDefaultValue ;
    }

    public String getXmlGetter() // Velocity : $attrib.xmlGetter
    {
    	//--- Standard Java primitive types
    	if ( isPrimitiveType() ) //  "int", "long", ... 
    	{
    		return "get" + firstCharUC(_sType) ;
    	}
    	
    	// if ( "String".equals(_sType) )   // No getter for "String"
    	
    	//--- Standard Java "wrapper classes"
    	if ( "Boolean".equals(_sType) )   return "getBooleanObject" ;
    	if ( "Byte".equals(_sType) )      return "getByteObject" ;
    	if ( "Character".equals(_sType) ) return "getCharObject" ; // new : implemented in Telosys 1.0.2
    	if ( "Double".equals(_sType) )    return "getDoubleObject" ;
    	if ( "Float".equals(_sType) )     return "getFloatObject" ;
    	if ( "Integer".equals(_sType) )   return "getIntObject" ;
    	if ( "Long".equals(_sType) )      return "getLongObject" ;
    	if ( "Short".equals(_sType) )     return "getShortObject" ;
    	
    	//--- Date
		if ( "java.util.Date".equals(_sFullType) ) 
		{
			return "getUtilDate" ; // new : implemented in Telosys 1.0.2
			// New function ( check if length = 10, 8 or 19 ) : can be implemented in the template for old versions
			// "YYYY-MM-DD", "HH:mm:ss", "YYYY-MM-DD HH:MM:SS"
			// switch to getDateISO, getTimeISO or getDateTimeISO
		}
    	
		//--- "java.math" types 
    	if ( "BigDecimal".equals(_sType) )   return "getBigDecimal" ;
    	if ( "BigInteger".equals(_sType) )   return "getBigInteger" ;
    	
		//--- "java.sql" types 
		if ( "java.sql.Date".equals(_sFullType) )       return "getSqlDate" ;      // new : implemented in Telosys 1.0.2
		if ( "java.sql.Time".equals(_sFullType) )       return "getSqlTime" ;      // new : implemented in Telosys 1.0.2
		if ( "java.sql.Timestamp".equals(_sFullType) )  return "getSqlTimestamp" ; // new : implemented in Telosys 1.0.2

// OLD :
//		return "getDateISO" ;     // "YYYY-MM-DD"
//		return "getTimeISO" ;     // "HH:mm:ss"
//		return "getDateTimeISO" ; // "YYYY-MM-DD HH:MM:SS"
		
		//--- Others : not supported  
		return "getUnsupportedType_" + _sType  ;
    }
    
    /**
     * Returns the getter to retrieve a parameter held by a ScreenRequest or a ServiceRequest
     * @return
     */
    public String getReqParamGetter() // Velocity : $attrib.reqParamGetter
    {
    	if ( "String".equals(_sType) )
    	{
    		return "getParameter" ;
    	}

    	//--- Standard Java primitive types
    	if ( isPrimitiveType() ) //  "int", "long", ... 
    	{
    		return "getParamAs" + firstCharUC(_sType) ;
    	}

    	//--- Standard Java wrapper types
    	if ( "Integer".equals(_sType) )
    	{
    		// The method returns a primitive type, but it works thanks to "autoboxing" 
    		return "getParamAsInt" ;
    	}
    	if ( "Char".equals(_sType)  || "Byte".equals(_sType)  || "Short".equals(_sType) || "Long".equals(_sType) 
    	  || "Float".equals(_sType) || "Double".equals(_sType) 
    	  || "Boolean".equals(_sType) 
    	  || "Date".equals(_sType) 
    	  )
    	{
    		// The method returns a primitive type, but it works thanks to "autoboxing" 
    		return "getParamAs" + _sType ;
    	}

    	//--- Others : not supported  
		return "getUnsupportedParamType_" + _sType  ;
    }
    
	public boolean getIsPrimitiveType() // Velocity : $attrib.isPrimitiveType
	{
		return isPrimitiveType();
	}

	public boolean getIsJavaLangWrapperType() // Velocity : $attrib.isJavaLangWrapperType
	{
		return isJavaLangWrapperType();
	}

	public boolean getTypeIsUsableInXml() // Velocity : $attrib.typeIsUsableInXml
	{
		if ( isPrimitiveType() ) return true ;
		if ( isJavaLangWrapperType() ) return true ;		
		if ( "java.lang.String".equals(_sFullType) ) return true ;
		if ( "java.util.Date".equals(_sFullType) ) return true ;
		//--- "java.math" types 
    	if ( "java.math.BigDecimal".equals(_sFullType) )   return true ;
    	if ( "java.math.BigInteger".equals(_sFullType) )   return true ;
		//--- "java.sql" types 
		if ( "java.sql.Date".equals(_sFullType) )       return true ;  // new : implemented in Telosys 1.0.2
		if ( "java.sql.Time".equals(_sFullType) )       return true ;  // new : implemented in Telosys 1.0.2
		if ( "java.sql.Timestamp".equals(_sFullType) )  return true ;  // new : implemented in Telosys 1.0.2

		//--- Other types : not usable in XML
    	return false ;
	}
	
	public String toString()
	{
		String s =  _sInitialValue != null ? " = " + _sInitialValue : "" ;
		return _sType + " " + _sName + s + " ( " + _sGetter + "/" + _sSetter + " ) ";
	}

	//-------------------------------------------------------------------------------------------------------------
	// ANNOTATIONS : J.P.A.
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * Returns the JPA annotations without left margin
	 * Usage : $x.jpaAnnotations 
	 * @return
	 */
	public String getJpaAnnotations()
    {
		return jpaAnnotations(0);
    }
	
	/**
	 * Returns the JPA annotations without left margin 
	 * Usage : $x.jpaAnnotations() 
	 * @return
	 */
	public String jpaAnnotations()
    {
		return jpaAnnotations(0);
    }

	/**
	 * Returns the JPA annotations for EmbeddedID without left margin 
	 * Usage : $x.jpaAnnotationsEmbeddedID() 
	 * @return
	 */
	public String jpaAnnotationsEmbeddedID()
    {
		return jpaAnnotationsEmbeddedID(0);
    }
	
	/**
	 * Returns the JPA annotations with the given left margin 
	 * Usage : $x.jpaAnnotations(4) 
	 * @param iLeftMargin
	 * @return
	 */
	public String jpaAnnotations(int iLeftMargin )
    {
		if ( _annotationsJPA != null ) {
			return _annotationsJPA.getJpaAnnotations(iLeftMargin, AnnotationsForJPA.EMBEDDED_ID_FALSE );
		}
		return "// Generator error : JPA extension is null"  ;
    }

	/**
	 * Returns the JPA annotations with the given left margin for an "Embedded ID" <br>
	 * No "@Id" for an embedded id
	 * 
	 * Usage : $x.jpaAnnotationsEmbeddedID(4) 
	 * @param iLeftMargin
	 * @return
	 */
	public String jpaAnnotationsEmbeddedID(int iLeftMargin )
    {
		if ( _annotationsJPA != null ) {
			return _annotationsJPA.getJpaAnnotations(iLeftMargin, AnnotationsForJPA.EMBEDDED_ID_TRUE );
		}
		return "// Generator error : JPA extension is null"  ;
    }
	
	//-------------------------------------------------------------------------------------------------------------
	// ANNOTATIONS : BEAN VALIDATION (JSR303)
	//-------------------------------------------------------------------------------------------------------------
	private final static String ERR_BEAN_VALIDATION_EXTENSION = "// Generator error : bean validation extension is null" ;
	/**
	 * Returns the Bean Validation annotations ( JSR303 and Hibernate additional annotations ) 
	 * @param iLeftMargin
	 * @return
	 */
	public String beanValidationAnnotations(int iLeftMargin )
    {
		if ( _annotationsBeanValidation != null ) {
			return _annotationsBeanValidation.getValidationAnnotations(iLeftMargin, "");
		}
		return ERR_BEAN_VALIDATION_EXTENSION ;
    }
	/**
	 * Returns the JSR303 Bean Validation annotations ( only JSR303 annotations, without Hibernate annotations ) 
	 * @param iLeftMargin
	 * @return
	 */
	public String beanValidationAnnotationsJSR303(int iLeftMargin)
    {
		if ( _annotationsBeanValidation != null ) {
			return _annotationsBeanValidation.getValidationAnnotations(iLeftMargin, "JSR303");
		}
		return ERR_BEAN_VALIDATION_EXTENSION ;
    }
	
	
	// -------------------------------------------------------------------------------------------------------------

	//
	// TODO refactoring ---> classe utilitaire
	//
	
    /**
     * Returns the given string starting by an Upper Case <br>
     * @param s
     * @return
     */
    private String firstCharUC(String s)
    {
    	if ( s != null )
    	{
    		if ( s.length() > 1 )
    		{
                return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    		}
    		else if ( s.length() == 1 )
    		{
    			return s.substring(0, 1).toUpperCase() ;
    		}
    	}
   		return "" ;
    }
    
    /**
     * Returns true if the JDBC type is an "integer type" 
     * @return
     */
    private boolean isJdbcInteger()
    {
		if ( _iJdbcTypeCode == Types.TINYINT || _iJdbcTypeCode == Types.SMALLINT 
      		  || _iJdbcTypeCode == Types.INTEGER || _iJdbcTypeCode == Types.BIGINT )
		{
			return true ;
		}
		return false ;
    }

    /**
     * Returns true if the JDBC type is an "string type" ( CHAR, VARCHAR or LONGVARCHAR )
     * @return
     */
    private boolean isJdbcString()
    {
		if ( _iJdbcTypeCode == Types.CHAR || _iJdbcTypeCode == Types.VARCHAR 
                || _iJdbcTypeCode == Types.LONGVARCHAR )
		{
			return true ;
		}
		return false ;
    }

    private boolean isJdbcBlob()
    {
		if ( _iJdbcTypeCode == Types.BLOB )
		{
			return true ;
		}
		return false ;
    }

    private boolean isJdbcTime()
    {
		if ( _iJdbcTypeCode == Types.TIME )
		{
			return true ;
		}
		return false ;
    }

    private boolean isJdbcTimestamp()
    {
		if ( _iJdbcTypeCode == Types.TIMESTAMP )
		{
			return true ;
		}
		return false ;
    }

    private boolean needsParamsForBoolean() 
    {
    	if ( "boolean".equals(_sType) || "Boolean".equals(_sType) )
    	{
    		if ( _sBooleanTrueValue != null && _sBooleanFalseValue != null ) 
    		{
        		if ( isJdbcInteger() )  return true ;
        		if ( isJdbcString() )   return true ;
    		}
    	}
    	return false ;
    }

	private String voidIfNull ( String s ) {
		return s != null ? s : "" ;
	}
	
	private String addQuotes ( String s ) {
		if ( s == null ) return null ;
		if ( s.length() == 0 )
		{
			return "\"\"";
		}
		else
		{
			if ( s.charAt(0) != '"' && s.charAt(s.length()-1) != '"' )
			{
				return "\"" + s + "\"" ;
			}
			return s ;
		}
	}

	private boolean isPrimitiveType()
	{
		return JavaTypeUtil.isPrimitiveType( _sType );
	}

	private boolean isJavaLangWrapperType()
	{
    	if ( "Boolean".equals(_sType) )   return true ;
    	if ( "Byte".equals(_sType) )      return true ;
    	if ( "Character".equals(_sType) ) return true ;
    	if ( "Double".equals(_sType) )    return true ;
    	if ( "Float".equals(_sType) )     return true ;
    	if ( "Integer".equals(_sType) )   return true ;
    	if ( "Long".equals(_sType) )      return true ;
    	if ( "Short".equals(_sType) )     return true ;
		return false ;
	}

	//-----------------------------------------------------------------------------------------
	// JPA "@GeneratedValue"
	//-----------------------------------------------------------------------------------------
	public boolean isGeneratedValue() {
		return _bGeneratedValue;
	}

	/**
	 * Returns the GeneratedValue strategy : auto, identity, sequence, table
	 * or null if not defined
	 * @return
	 */
	public String getGeneratedValueStrategy() {
		return _sGeneratedValueStrategy;
	}

	/**
	 * Returns the GeneratedValue generator : the name of the primary key generator to use <br>
	 * The generator name referenced a "SequenceGenerator" or a "TableGenerator"
	 * @return
	 */
	public String getGeneratedValueGenerator() {
		return _sGeneratedValueGenerator;
	}

	//-----------------------------------------------------------------------------------------
	// JPA "@SequenceGenerator"
	//-----------------------------------------------------------------------------------------
	/**
	 * Returns true if this attribute is a "GeneratedValue" using a "SequenceGenerator"
	 * @return
	 */
	public boolean hasSequenceGenerator() {
		return _bSequenceGenerator;
	}

	/**
	 * Returns the "@SequenceGenerator" name
	 * @return
	 */
	public String getSequenceGeneratorName() {
		return _sSequenceGeneratorName;
	}

	/**
	 * Returns the "@SequenceGenerator" sequence name
	 * @return
	 */
	public String getSequenceGeneratorSequenceName() {
		return _sSequenceGeneratorSequenceName;
	}

	/**
	 * Returns the "@SequenceGenerator" sequence allocation size
	 * @return
	 */
	public int getSequenceGeneratorAllocationSize() {
		return _iSequenceGeneratorAllocationSize;
	}

	//-----------------------------------------------------------------------------------------
	// JPA "@TableGenerator"
	//-----------------------------------------------------------------------------------------
	public boolean hasTableGenerator() {
		return _bTableGenerator;
	}

	public String getTableGeneratorName() {
		return _sTableGeneratorName;
	}

	public String getTableGeneratorTable() {
		return _sTableGeneratorTable;
	}

	public String getTableGeneratorPkColumnName() {
		return _sTableGeneratorPkColumnName;
	}

	public String getTableGeneratorValueColumnName() {
		return _sTableGeneratorValueColumnName;
	}

	public String getTableGeneratorPkColumnValue() {
		return _sTableGeneratorPkColumnValue;
	}
	
}