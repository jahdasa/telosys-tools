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
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.tools.AnnotationsForBeanValidation;
import org.telosys.tools.generator.context.tools.AnnotationsForJPA;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.persistence.util.RepositoryConst;


/**
 * Context class for a BEAN ATTRIBUTE ( with or without database mapping )
 *  
 * @author Laurent GUERIN
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName = ContextName.ATTRIBUTE ,
		otherContextNames= { ContextName.ATTRIB, ContextName.FIELD },		
		text = {
				"This object provides all information about an entity attribute",
				"Each attribute is retrieved from the entity class ",
				""
		},
		since = "",
		example= {
				"",
				"#foreach( $attribute in $entity.attributes )",
				"    $attribute.name : $attribute.type",
				"#end"
		}
		
 )
//-------------------------------------------------------------------------------------
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
    
	//--- Basic minimal attribute info -------------------------------------------------
	private final String  _sName ;  // attribute name 
	private String        _sType = "" ;  // Short java type without package, without blank, eg : "int", "BigDecimal", "Date"
	private final String  _sFullType ;  // Full java type with package, : "java.math.BigDecimal", "java.util.Date"
	
	private final String  _sInitialValue ; // can be null 
	private final String  _sGetter ;
	private final String  _sSetter ;
	
	private final String  _sDefaultValue ; // can be null 
	
	//--- Database info -------------------------------------------------
    private boolean _bKeyElement       = false ;  // True if primary key
    private boolean _bUsedInForeignKey = false ;
    private boolean _bAutoIncremented  = false ;  // True if auto-incremented by the database
    private String  _sDataBaseName     = null ;  // Column name in the DB table
    private String  _sDataBaseType     = null ;  // Column type in the DB table
    private int     _iJdbcTypeCode     = 0 ;     // JDBC type for this column
    private int     _iDatabaseSize     = 0 ;     // Size of this column (if Varchar ) etc..
    private String  _sDatabaseDefaultValue = null ; // keep null (do not initialize to "" )  
    private boolean _bDatabaseNotNull  = false ;  // True if "not null" in the database
    
    //--- Further info for ALL ---------------------------------------
    private boolean _bNotNull   = false ;
    private String  _sLabel     = "" ; // v 2.0.3
    private String  _sInputType = "" ; // v 2.0.3

    //--- Further info for BOOLEAN -----------------------------------
    private String  _sBooleanTrueValue  = "" ; // eg "1", ""Yes"", ""true""
    private String  _sBooleanFalseValue = "" ; // eg "0", ""No"",  ""false""
    
    //--- Further info for DATE/TIME ---------------------------------
    private int     _iDateType        = DATE_ONLY ;  // By default only DATE
    private boolean _bDatePast        = false ;
    private boolean _bDateFuture      = false ;
    private boolean _bDateBefore      = false ;
    private String  _sDateBeforeValue = "" ;
    private boolean _bDateAfter       = false ;
    private String  _sDateAfterValue  = "" ;

    //--- Further info for NUMBER ------------------------------------
    private String  _sMinValue = "" ; 
    private String  _sMaxValue = "" ; 

    //--- Further info for STRING ------------------------------------
    private boolean _bLongText  = false ;  // True if must be stored as a separate tag in the XML flow
    private boolean _bNotEmpty  = false ;
    private boolean _bNotBlank  = false ;
    private String  _sMinLength = "" ; 
    private String  _sMaxLength = "" ; 
    private String  _sPattern   = "" ; 
    
    
	//--- JPA KEY Generation infos -------------------------------------------------
    private boolean _bGeneratedValue = false ;  // True if GeneratedValue ( annotation "@GeneratedValue" )
	private String  _sGeneratedValueStrategy  = null ; // "AUTO", "IDENTITY", "SEQUENCE", "TABLE" 
	private String  _sGeneratedValueGenerator = null ;
	
    private boolean _bSequenceGenerator = false ;  // True if SequenceGenerator ( annotation "@SequenceGenerator" )
	private String  _sSequenceGeneratorName           = null ;
	private String  _sSequenceGeneratorSequenceName   = null ;
	private int     _iSequenceGeneratorAllocationSize = -1;

    private boolean _bTableGenerator = false ;  // True if TableGenerator ( annotation "@TableGenerator" )
	private String  _sTableGeneratorName            = null ;
	private String  _sTableGeneratorTable           = null ;
	private String  _sTableGeneratorPkColumnName    = null ;
	private String  _sTableGeneratorValueColumnName = null ;
	private String  _sTableGeneratorPkColumnValue   = null ;

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
		_sInitialValue = sInitialValue; // can be null 
		_sDefaultValue = null ; // keep null ( for hasDefaultValue )
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
		_sName   = column.getJavaName();
		_sGetter = Util.buildGetter(_sName, _sType);
		_sSetter = Util.buildSetter(_sName);
		
		// TODO gerer les duplicatedShortNames dans Util.shortestType mais dans ce cas a quoi cela peut servir ????
		_sType     = StrUtil.removeAllBlanks(Util.shortestType(column.getJavaType(), new LinkedList<String>()));
		_sFullType = StrUtil.removeAllBlanks(column.getJavaType());
				
		_sInitialValue    = null ; // TODO : column.getJavaInitialValue()  ???
		_sDefaultValue    = column.getJavaDefaultValue();
		
		_sDataBaseName     = column.getDatabaseName() ;
        _sDataBaseType     = column.getDatabaseTypeName() ;
        _iJdbcTypeCode     = column.getJdbcTypeCode() ;
        _bKeyElement       = column.isPrimaryKey() ;
        _bUsedInForeignKey = column.isForeignKey();
        _bAutoIncremented  = column.isAutoIncremented();
        _iDatabaseSize     = column.getDatabaseSize() ;
        _sDatabaseDefaultValue = column.getDatabaseDefaultValue(); 
        _bDatabaseNotNull  = column.isDatabaseNotNull();
        
		//--- Further info for ALL
        _bNotNull   = column.getJavaNotNull();
        _sLabel     = column.getLabel();
        _sInputType = column.getInputType();
        
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

	}
    
	//-----------------------------------------------------------------------------------------------
	protected void forceType ( String sTypeToUse )
	{
		if ( sTypeToUse != null )
		{
			_sType = sTypeToUse ;
		}
	}
	
	@VelocityMethod(
			text={	
				"Returns the name of the attribute "
				}
		)
	public String getName()
	{
		return _sName;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the attribute's name with n trailing blanks "
			},
		parameters = { 
			"n : the number of blanks to be added at the end of the name" 
			}
	)
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

	//-------------------------------------------------------------------------------------
	/**
	 * Returns the "short java type" without package, without blank, eg : "int", "BigDecimal", "Date"
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the 'short type' for the attribute without package and without blank",
			"Examples for Java : 'int', 'BigDecimal', 'Date' "
			}
	)
	public String getType()
	{
		return _sType;
	}

	/**
	 * Returns the "java wrapper type" ie "Float" for "float" type, "Boolean" for "boolean" type
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the Java wrapper type corresponding to the attribute's primitive type",
			"Examples : 'Float' for 'float', 'Integer' for 'int', 'Boolean' for 'boolean', ... ",
			"If the attribute's type is retuned as is if it's not a primitive type"
			}
	)
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


	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the type of the date : $const.DATE_ONLY, $const.TIME_ONLY, $const.DATE_AND_TIME"
			}
	)
	public int getDateType()
	{
		return _iDateType ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the attribute's type with n trailing blanks "
			},
		parameters = { 
			"n : the number of blanks to be added at the end of the name" 
			}
	)
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
    
	/**
	 * Returns the full java type with package, ie : "java.math.BigDecimal", "java.util.Date"
	 * @return
	 */
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the full type of the attribute ( java.math.BigDecimal, java.util.Date, .. )"
			}
	)
	public String getFullType()
	{
		return _sFullType;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if there's an initial value for the attribute"
			}
	)
	public boolean hasInitialValue()
	{
		return _sInitialValue != null ;
	}
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the initial value for the attribute"
			}
	)
	public String getInitialValue()
	{
		return _sInitialValue;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
				"Returns the getter for the attribute",
				"e.g : 'getFoo' for 'foo' "
					}
	)
	public String getGetter()
	{
		return _sGetter;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
				"Returns the setter for the attribute",
				"e.g : 'setFoo' for 'foo' "
				}
	)
	public String getSetter()
	{
		return _sSetter;
	}

	//----------------------------------------------------------------------
	// Database 
	//----------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the database name for the attribute",
			"Typically the column name for a relational database"
			}
	)
    public String getDatabaseName()
    {
        return _sDataBaseName;
    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the database native type for the attribute",
			"For example : INTEGER, VARCHAR, etc..."
			}
	)
    public String getDatabaseType()
    {
        return _sDataBaseType;
    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the database size for the attribute"
			}
	)
    public int getDatabaseSize()
    {
        return _iDatabaseSize ;
    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute has a database default value"
			}
	)
    public boolean hasDatabaseDefaultValue()
    {
    	if ( _bAutoIncremented ) return false ; // No default value for auto-incremented fields
        if ( _sDatabaseDefaultValue != null )
        {
        	if ( _sDatabaseDefaultValue.length() > 0 ) return true ;
        }
        return false ;
    }
    
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns the database default value for the attribute (or a void string if none)"
		}
	)
    public String getDatabaseDefaultValue()
    {
    	if ( hasDatabaseDefaultValue() ) return _sDatabaseDefaultValue ;
        return "" ;
    }
    
	//----------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute must be NOT NULL when stored in the database"
		}
	)
    public boolean isDatabaseNotNull()
    {
        return _bDatabaseNotNull;
    }
//    /**
//     * Synonym for usage without "()"
//     * @return
//     */
//    public boolean getIsDatabaseNotNull()
//    {
//        return isDatabaseNotNull();
//    }
    
	//----------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the JDBC type of the attribute (the type code)"
			}
		)
    public int getJdbcTypeCode()
    {
        return _iJdbcTypeCode ;
    }

	//----------------------------------------------------------------------
    /**
     * Returns the recommended Java type for the JDBC type 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the recommended Java type for the JDBC type of the attribute"
				}
		)
    public String getJdbcRecommendedJavaType()
    {
    	JdbcTypes types = JdbcTypesManager.getJdbcTypes();
    	return types.getJavaTypeForCode(_iJdbcTypeCode, _bDatabaseNotNull );
    }

	//----------------------------------------------------------------------
    /**
     * Returns TRUE if the attribute is a Database Primary Key element
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute is the Primary Key or a part of the Primary Key in the database"
		}
	)
    public boolean isKeyElement()
    {
        return _bKeyElement;
    }
//    /**
//     * Synonym for usage without "()"
//     * @return
//     */
//    public boolean getIsKeyElement()
//    {
//        return isKeyElement();
//    }

	//----------------------------------------------------------------------
    /**
     * Returns TRUE if the attribute is used in (at least) one Foreign Key 
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute is used in (at least) one Foreign Key"
		}
	)
    public boolean isUsedInForeignKey()
    {
        return _bUsedInForeignKey ;
    }
//    /**
//     * Synonym for usage without "()"
//     * @return
//     */
//    public boolean getIsUsedInForeignKey()
//    {
//        return isKeyElement();
//    }

    /**
     * Returns TRUE if the attribute is involved in a link Foreign Key <br>
     * Useful for JPA, to avoid double mapping ( FK field and owning side link )
     * @param linksArray - list of the links to be checked 
     * @return
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute is involved in a link Foreign Key",
		"Useful for JPA, to avoid double mapping ( FK field and owning side link )"
		},
	parameters="links : list of links where to search the attribute"
	)
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

	//-------------------------------------------------------------------------------------
    /**
     * Returns TRUE if the attribute is auto-incremented by the Database engine
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute is 'auto-incremented' by the database",
		"when a new entity is inserted in the database"
		}
	)
    public boolean isAutoIncremented()
    {
        return _bAutoIncremented;
    }
    
//    /**
//     * Synonym for usage without "()"
//     * @return
//     */
//    public boolean getIsAutoIncremented()
//    {
//        return isAutoIncremented();
//    }

	//----------------------------------------------------------------------
    /**
     * Returns TRUE if the attribute has a "Not Null" constraint at the Java level
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute has a 'Not Null' validation rule "
		}
	)
    public boolean isNotNull()
    {
        return _bNotNull;
    }
//    /**
//     * Synonym for usage without "()"
//     * @return
//     */
//    public boolean getIsNotNull()
//    {
//        return isNotNull();
//    }

	//----------------------------------------------------------------------
    /**
     * Returns the label defined for the attribute 
     * @since v 2.0.3
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns the label for the attribute "
			}
	)
    public String getLabel()
    {
        return _sLabel ;
    }
    
	//----------------------------------------------------------------------
    /**
     * Returns the "input type" defined for this attribute 
     * @since v 2.0.3
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns the 'input type' defined for the attribute",
			"Typically for HTML 5 : 'number', 'date', ..."
			},
		since="2.0.3"
	)
    public String getInputType()
    {
        return _sInputType ;
    }
    
	//----------------------------------------------------------------------
    /**
     * Returns the java type starting by an Upper Case <br>
     * eg : returns "Double" for "double"/"Double", "Int" for "int", ...
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns the java type starting by an Upper Case",
		"Examples : 'Double' for 'double'/'Double', 'Int' for 'int', ..."
		}
	)
    public String javaTypeStartingByUC()
    {
    	return firstCharUC(_sType);
    }
    
    /**
     * Returns the "QueryContext setter" method name to use to set a query parameter <br>
     * Examples : setParamString, setParamByte, setParamBooleanAsInt, etc..
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the 'QueryContext setter' method name to use to set a query result",
				" eg 'setParamBooleanAsInt', 'setParamBlob', 'setParamBigDecimal', etc ) "
				}
		)
		@Deprecated
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
    
	@VelocityMethod(
			text={	
				"Returns TRUE if the 'QueryContext setter' needs parameters "
				}
		)
	@Deprecated
    public boolean getNeedsQuerySetterParams() // Velocity : $attrib.needsQuerySetterParams
    {
    	return needsParamsForBoolean();
    }
    
	@VelocityMethod(
			text={	
				"Returns 'QueryContext setter' optional parameters ( the true/false values for booleans ) "
				}
		)
	@Deprecated
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
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns the 'QueryContext getter' method name to use to get a query result",
			" eg 'getResultLongObject', 'getResultBooleanFromInt', 'getResultTimeAsDate', etc ) "
			}
	)
	@Deprecated
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
    
	@VelocityMethod(
		text={	
			"Returns TRUE if the 'QueryContext getter' needs parameters "
			}
	)
	@Deprecated
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
	@VelocityMethod(
			text={	
				"Returns 'QueryContext getter' optional parameters ( the true value for booleans ) "
				}
		)
	@Deprecated
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
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns maximum input length to be used in the GUI ",
			"For string types the specific maximum lenght is returned ( or void if not defined )",
			"For numeric types the maximum lenght depends on the type ( 4 for 'byte', 11 for 'int', etc... ) ",
			"For 'date' 10, for 'time' 8"
			}
	)
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
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the GUI 'maxlength' attribute (or void if none) ",
			"e.g 'maxlength=12' "
			}
	)
    public String getGuiMaxLengthAttribute() 
    {
    	return guiMaxLengthAttribute() ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * For Velocity function call syntax : $var.guiMaxLengthAttribute() 
     * @return
     */
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the GUI 'maxlength' attribute (or void if none) ",
			"e.g 'maxlength=12' "
			}
	)
    public String guiMaxLengthAttribute() 
    {
    	return guiMaxLengthAttribute("maxlength") ;
    }
    
    /**
     * For Velocity function call syntax : $var.guiMaxLengthAttribute('maxlength') 
     * @param attributeName
     * @return
     */
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the GUI specific attribute for maximum length (or void if none) ",
			"e.g 'myattribute=12' for guiMaxLengthAttribute('myattribute') "
			},
		parameters = "guiAttributeName : the name of the attribute to be set in the GUI"
	)
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
	@VelocityMethod(
			text={	
				"Returns the maximum length for the attribute (if any, else returns void) "
				}
		)
    public String getMaxLength() 
    {
    	return voidIfNull(_sMaxLength) ;
    }
    /**
     * Returns the "minimum" length if any, else returns "" 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the minimum length for the attribute (if any, else returns void) "
				}
		)
    public String getMinLength() 
    {
    	return voidIfNull(_sMinLength) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "pattern" (Reg Exp) if any, else returns "" 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the Reg Exp pattern defined for the attribute (if any, else returns void) "
				}
		)
    public String getPattern() 
    {
    	return voidIfNull(_sPattern) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "minimum" value if any, else returns "" 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the minimum value for the attribute (if any, else returns void) "
				}
		)
    public String getMinValue() 
    {
    	return voidIfNull(_sMinValue) ;
    }
    
    //-------------------------------------------------------------------------------------------
    /**
     * Returns the "maximum" value if any, else returns "" 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the maximum value for the attribute (if any, else returns void) "
				}
		)
    public String getMaxValue() 
    {
    	return voidIfNull(_sMaxValue) ;
    }
    //-------------------------------------------------------------------------------------------
    /**
     * Synonym for Velocity attribute syntax : $var.guiMinMaxAttributes 
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns the GUI attributes for minimum and maximum values (or void if none)",
			"e.g 'min=10 max=20' "
			}
	)
    public String getGuiMinMaxAttributes() 
    {
    	return guiMinMaxAttributes() ;
    }
    
	//-------------------------------------------------------------------------------------
    /**
     * For Velocity function call syntax : $var.guiMinMaxAttributes() 
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns the GUI attributes for minimum and maximum values (or void if none)",
			"e.g 'min=10 max=20' "
			}
	)
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
	@VelocityMethod(
		text={	
			"Returns the GUI specific attribute for minimum and maximum values (or void if none) ",
			"e.g 'mini=10 maxi=20' for guiMaxLengthAttribute('mini', 'maxi') "
			},
		parameters = {
			"guiMinAttributeName : the name of the MIN attribute to be set in the GUI",
			"guiMaxAttributeName : the name of the MAX attribute to be set in the GUI"
		}
	)
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
     * Returns the GUI "type" if any, else returns "" 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the GUI type if any (else returns a void string)",
				"e.g 'int', 'num', 'date', 'time', '' "
				}
		)
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
	@VelocityMethod(
			text={	
				"Returns the GUI type attribute ",
				"e.g : type='int' "
				}
				)
    public String getGuiTypeAttribute() 
    {
    	return guiTypeAttribute() ;
    }
    /**
     * For Velocity function call syntax : $var.guiTypeAttribute() 
     * @return
     */
	@VelocityMethod(
			text={	
				"Returns the GUI type attribute ",
				"e.g : type='int' "
				}
				)
    public String guiTypeAttribute() 
    {
    	return guiTypeAttribute("type") ;
    }
    /**
     * For Velocity function call syntax : $var.guiTypeAttribute('type') 
     * @param attributeName
     * @return
     */
	@VelocityMethod(
	text={	
		"Returns the GUI type attribute ",
		"e.g : type='int' "
		},
	parameters={
			"guiTypeAttributeName : name of the TYPE attribute to be set in the GUI "
		}
		)
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
    
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute must be validated as a date in the past"
			}
	)
	public boolean hasDatePastValidation() {
		return _bDatePast;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute must be validated as a date in the future"
			}
	)
	public boolean hasDateFutureValidation() {
		return _bDateFuture;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute must be validated as a date before a given date value"
			}
	)
	public boolean hasDateBeforeValidation() {
		return _bDateBefore;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'date before' value (for date validation)"
			}
	)
	public String getDateBeforeValue() {
		return _sDateBeforeValue;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute must be validated as a date after a given date value"
			}
	)
	public boolean hasDateAfterValidation() {
		return _bDateAfter;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'date after' value (for date validation)"
			}
	)
	public String getDateAfterValue() {
		return _sDateAfterValue;
	}

	//-----------------------------------------------------------------------------
    
    /**
     * Returns true if the attribute is a long text that must be stored in a separate XML tag
     * @return 
     */
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute is a 'Long Text' ",
		"i.e. that cannot be transported in a classical string",
		"Typically a text stored as a CLOB or a BLOB"
		}
	)
    public boolean isLongText()
    {
        return _bLongText;
    }
//	/**
//	 * Shortcut for usage without "()"
//	 * @return
//	 */
//	public boolean getIsLongText() // Velocity : $attrib.isLongText
//	{
//		return isLongText();
//	}

	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute has a 'Not Empty' validation rule "
		}
	)
    public boolean isNotEmpty()
    {
        return _bNotEmpty;
    }
//    public boolean getIsNotEmpty()
//    {
//        return isNotEmpty();
//    }
    
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute has a 'Not Blank' validation rule "
		}
	)
    public boolean isNotBlank()
    {
        return _bNotBlank;
    }
	//-----------------------------------------------------------------------------
    /**
     * Returns true if the attribute need a conversion to be stored as an XML attribute value<br>
     * ( true if the attribute needs a "attributeString(...)" processing )
     * @return
     */
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute needs a conversion to be stored as an XML attribute value "
			}
		)
	@Deprecated
    public boolean getNeedsXmlConversion() // Velocity : $attrib.needsXmlConversion
    {
    	// always true except if primitive type and not "boolean"
		if ( "boolean".equals(_sType)) return true ; // boolean needs conversion
    	if ( isPrimitiveType() ) 
		{
    		//--- Primitive types (except boolean ) : doesn't need conversion
    		return false ; 
		}
    	else
    	{
    		//--- If not a primitive type => need conversion 
        	return true ;
    	}
    }
    
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute needs a conversion to be stored as an XML attribute value "
			}
		)
	@Deprecated
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
	@VelocityMethod(
		text={	
			"Returns the additional parameters required for the XML String conversion if any "
			}
	)
	@Deprecated
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
    
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if there's a default value for the attribute"
			}
	)
    public boolean hasDefaultValue() // Velocity : $attrib.hasDefaultValue()
    {
    	return ( _sDefaultValue != null ) ;
    }
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the default value for the attribute"
			}
	)
    public String getDefaultValue() // Velocity : ${attrib.defaultValue}
    {
    	return _sDefaultValue ;
    }

	@VelocityMethod(
	text={	
		"Returns the specific XML getter  ",
		"Exampe : getBooleanObject, getLongObject, getBigInteger, etc..."
		}
	)
	@Deprecated
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
	@VelocityMethod(
			text={	
				"Returns the getter to retrieve a parameter held by a ScreenRequest or a ServiceRequest "
				}
		)
	@Deprecated
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
    
//	public boolean getIsPrimitiveType() // Velocity : $attrib.isPrimitiveType
//	{
//		return isPrimitiveType();
//	}

//	public boolean getIsJavaLangWrapperType() // Velocity : $attrib.isJavaLangWrapperType
//	{
//		return isJavaLangWrapperType();
//	}

	@VelocityMethod(
		text={	
			"Returns TRUE if usable in XML "
			}
	)
	@Deprecated
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
	@VelocityMethod(
			text={	
				"Returns the JPA annotations for the attribute (without left margin)"
				}
			)
	public String getJpaAnnotations()
    {
		return jpaAnnotations(0);
    }
	
	/**
	 * Returns the JPA annotations without left margin 
	 * Usage : $x.jpaAnnotations() 
	 * @return
	 */
	@VelocityMethod(
			text={	
				"Returns the JPA annotations for the attribute (without left margin)"
				}
			)
	public String jpaAnnotations()
    {
		return jpaAnnotations(0);
    }

	/**
	 * Returns the JPA annotations for EmbeddedID without left margin 
	 * Usage : $x.jpaAnnotationsEmbeddedID() 
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the JPA annotations for EmbeddedID (without left margin)"
			}
		)
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
	@VelocityMethod(
	text={	
		"Returns the JPA annotations for the attribute (with a left margin)"
		},
	parameters = "leftMargin : the left margin (number of blanks) "
	)
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
	@VelocityMethod(
			text={	
				"Returns the JPA annotations for an 'embedded id' (with a left margin)"
				},
			parameters = "leftMargin : the left margin (number of blanks) "
			)
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
	@VelocityMethod(
		text={	
			"Returns the Java 'Bean Validation' annotations",
			"( JSR303 and Hibernate additional annotations ) "
			},
		parameters = "leftMargin : the left margin (number of blanks) "
	)
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
	@VelocityMethod(
		text={	
			"Returns the Java 'JSR-303 Bean Validation' annotations ",
			"( only JSR-303 annotations, without Hibernate additional annotations ) "
			},
		parameters = "leftMargin : the left margin (number of blanks) "
	)
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
     * Returns TRUE if the JDBC type is an "integer type" 
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
     * Returns true if the JDBC type is a "string type" ( CHAR, VARCHAR or LONGVARCHAR )
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

	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java primitive type",
		"i.e. int, float, boolean, ..."
		}
	)
	public boolean isPrimitiveType()
	{
		return JavaTypeUtil.isPrimitiveType( _sType );
	}

	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java array ( byte[], String[], ... )"
		}
	)
	public boolean isArrayType()
	{
		String s = _sType ;
		if ( s != null ) {
			if ( s.trim().endsWith("]")) {
				return true ;
			}
		}
    	return false ;
	}

	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java boolean/Boolean type"
		}
	)
	public boolean isBooleanType()
	{
    	if ( "boolean".equals(_sType) )   return true ;
    	if ( "Boolean".equals(_sType) )   return true ;
    	return false ;
	}

	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java long/Long type"
		}
	)
	public boolean isLongType()
	{
    	if ( "long".equals(_sType) )   return true ;
    	if ( "Long".equals(_sType) )   return true ;
    	return false ;
	}

	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java float/Float type"
		}
	)
	public boolean isFloatType()
	{
    	if ( "float".equals(_sType) )   return true ;
    	if ( "Float".equals(_sType) )   return true ;
    	return false ;
	}
	
	//------------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's type is a Java double/Double type"
		}
	)
	public boolean isDoubleType()
	{
    	if ( "double".equals(_sType) )   return true ;
    	if ( "Double".equals(_sType) )   return true ;
    	return false ;
	}

	//------------------------------------------------------------------------------------------
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
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
	text={	
		"Returns TRUE if the attribute's value is generated when a new entity is inserted in the database",
		"It can be generated by the database ('auto-incremented') ",
		"or generated by the persistence layer (typically by JPA)"
		}
	)
	public boolean isGeneratedValue() {
		return _bGeneratedValue;
	}

	/**
	 * Returns the GeneratedValue strategy : auto, identity, sequence, table
	 * or null if not defined
	 * @return
	 */
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the strategy for a 'generated value' (or null if none)",
			"e.g : 'auto', 'identity', 'sequence', 'table' "
			}
	)
	public String getGeneratedValueStrategy() {
		return _sGeneratedValueStrategy;
	}

	//-------------------------------------------------------------------------------------
	/**
	 * Returns the GeneratedValue generator : the name of the primary key generator to use <br>
	 * The generator name referenced a "SequenceGenerator" or a "TableGenerator"
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the generator for a 'generated value' ",
			"Typically for JPA : 'SequenceGenerator' or 'TableGenerator' "
			}
	)
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
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute is a 'generated value' using a 'sequence generator' ",
			"Typically for JPA '@SequenceGenerator'  "
			}
	)
	public boolean hasSequenceGenerator() {
		return _bSequenceGenerator;
	}

	//-----------------------------------------------------------------------------------------
	/**
	 * Returns the "@SequenceGenerator" name
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the name of the 'sequence generator' ",
			"Typically for JPA '@SequenceGenerator/name'  "
			}
	)
	public String getSequenceGeneratorName() {
		return _sSequenceGeneratorName;
	}

	//-----------------------------------------------------------------------------------------
	/**
	 * Returns the "@SequenceGenerator" sequence name
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the 'sequence name' to be used in the 'sequence generator' definition",
			"Typically for JPA '@SequenceGenerator/sequenceName'  "
			}
	)
	public String getSequenceGeneratorSequenceName() {
		return _sSequenceGeneratorSequenceName;
	}

	//-----------------------------------------------------------------------------------------
	/**
	 * Returns the "@SequenceGenerator" sequence allocation size
	 * @return
	 */
	@VelocityMethod(
		text={	
			"Returns the 'sequence allocation size' to be used in the 'sequence generator' definition",
			"Typically for JPA '@SequenceGenerator/allocationSize'  "
			}
	)
	public int getSequenceGeneratorAllocationSize() {
		return _iSequenceGeneratorAllocationSize;
	}

	//-----------------------------------------------------------------------------------------
	// JPA "@TableGenerator"
	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the attribute is a 'generated value' using a 'table generator' ",
			"Typically for JPA '@TableGenerator'  "
			}
	)
	public boolean hasTableGenerator() {
		return _bTableGenerator;
	}

	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the 'table generator' ",
			"Typically for JPA '@TableGenerator/name'  "
			}
	)
	public String getTableGeneratorName() {
		return _sTableGeneratorName;
	}

	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the table used in the 'table generator' ",
			"Typically for JPA '@TableGenerator/table'  "
			}
	)
	public String getTableGeneratorTable() {
		return _sTableGeneratorTable;
	}

	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the Primary Key column used in the 'table generator' ",
			"Typically for JPA '@TableGenerator/pkColumnName'  "
			}
	)
	public String getTableGeneratorPkColumnName() {
		return _sTableGeneratorPkColumnName;
	}

	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the column that stores the last value generated by the 'table generator' ",
			"Typically for JPA '@TableGenerator/valueColumnName'  "
			}
	)
	public String getTableGeneratorValueColumnName() {
		return _sTableGeneratorValueColumnName;
	}

	//-----------------------------------------------------------------------------------------
	@VelocityMethod(
	text={
		"Returns the primary key value in the generator table that distinguishes this set of generated values",
		"from others that may be stored in the table",
		"Typically for JPA '@TableGenerator/pkColumnValue'  "
		}
	)
	public String getTableGeneratorPkColumnValue() {
		return _sTableGeneratorPkColumnValue;
	}
	
}