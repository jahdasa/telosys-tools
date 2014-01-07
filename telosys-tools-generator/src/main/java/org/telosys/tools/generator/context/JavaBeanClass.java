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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generator.GeneratorContextException;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityNoDoc;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.doc.VelocityReturnType;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.generator.context.tools.AmbiguousTypesDetector;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.ForeignKey;
import org.telosys.tools.repository.model.Link;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * Specific Java Class for an entity Java Bean with Object-Relational Mapping (ORM) <br>
 * This class provides the standard Java class informations plus : <br>
 * . the attributes of the class <br>
 * . the imports required by attributes types <br>
 * . the database table where the object is stored <br>
 * . the mapping for each attribute<br>
 * 
 * @author Laurent GUERIN
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName= ContextName.ENTITY ,
		otherContextNames=ContextName.BEAN_CLASS,
		text = { 
				"Entity class for the current generation ",
				"",
				"Provides all information available for an entity as defined in the model : ",
				" . attributes information (class fields) ",
				" . database mapping ",
				""
		},
		since = "2.0.0"
 )
//-------------------------------------------------------------------------------------
public class JavaBeanClass extends JavaClass
{
	
	private final static List<JavaBeanClassAttribute>  VOID_ATTRIBUTES_LIST    = new LinkedList<JavaBeanClassAttribute>();
	private final static List<JavaBeanClassForeignKey> VOID_FOREIGN_KEYS_LIST  = new LinkedList<JavaBeanClassForeignKey>();

//	private final static List<String>                 VOID_STRINGS_LIST    = new LinkedList<String>();
	//private final static Set<String>                  VOID_STRINGS_SET     = new LinkedHashSet<String>();
	
	private final static List<JavaBeanClassLink>      VOID_LINKS_LIST    = new LinkedList<JavaBeanClassLink>();
	
//	// The imports for all fields of this class ( list of "java.xx.Class" )
//	private List<String>                       _importsForAllFields = VOID_STRINGS_LIST ; 
//	// The imports required when using only the "key fields" of this class ( list of "java.xx.Class" )
//	private List<String>                       _importsForKeyFields = VOID_STRINGS_LIST ; 
	
	private LinkedList<JavaBeanClassAttribute> _attributes  = null ; // The attributes for this class ( ALL ATTRIBUTES )
	
    private String     _sDatabaseTable   = null ; // Table name this class is mapped with
    private String     _sDatabaseCatalog = null ; // The table's catalog 
    private String     _sDatabaseSchema  = null ; // The table's schema 
    private String     _sDatabaseType    = null ; // The table's type "table" or "view" 
    
	private LinkedList<JavaBeanClassAttribute>  _keyAttributes     = null ; // The KEY attributes for this class
	private LinkedList<JavaBeanClassAttribute>  _nonKeyAttributes  = null ; // The NON KEY attributes for this class
	private String     _sSqlKeyColumns = null ;
	private String     _sSqlNonKeyColumns = null ;
	
	private LinkedList<JavaBeanClassForeignKey>  _foreignKeys  = null ; // The database FOREIGN KEYS attributes for this entity ( v 2.0.7)
	
	//--- XML mapper infos
	private LinkedList<JavaBeanClassAttribute> _nonTextAttributes  = null ; // Standard attributes for this class ( not "long text" )
	private LinkedList<JavaBeanClassAttribute> _textAttributes     = null ; // Special "long text" attributes for this class

	//--- JPA specific
//	private Set<String>                   _importsJpa = null ; // The imports JPA for this class ( list of "java.xx.Class" )
	private LinkedList<JavaBeanClassLink> _links  = null ; // The links for this class ( ALL ATTRIBUTES )
//	private Entity                        _entite = null;
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Constructor based on Repository Entity
	 * @param entity
	 * @param model
	 * @param nameEntity
	 * @param sPackage
	 */
	public JavaBeanClass(final Entity entity, final RepositoryModel model, final String nameEntity, 
			final String sPackage ) 
	{
		super(entity.getBeanJavaClass(), sPackage);
		
//		this._entite = entity;
		this._sDatabaseTable   = entity.getName();
		this._sDatabaseCatalog = entity.getCatalog();
		this._sDatabaseSchema  = entity.getSchema();
		this._sDatabaseType    = entity.getDatabaseType(); // ver 2.0.7
		
		/*
		 * Set all the attributes of the current Java class
		 *  
		 */
		Collection<Column> entityColumns = entity.getColumnsCollection() ;
		for ( Column col : entityColumns ) {
			JavaBeanClassAttribute jca = new JavaBeanClassAttribute(col);
			this.addAttribute(jca);
		}

		/*
		 * Set all the links of the current Java class
		 */
		Collection<Link> entityLinks = entity.getLinksCollection() ;
		for ( Link link : entityLinks ) {
			// On va trouver le bean correspondant a ce lien dans le model
			Entity entityCible = model.getEntityByName(link.getTargetTableName());
//			JavaBeanClassLink jcl = new JavaBeanClassLink(link, this._entite , entityCible );
			JavaBeanClassLink jcl = new JavaBeanClassLink(link, entity , entityCible );
			
//			//ajouter import specifique
//			JavaBeanClassImports jbci = new JavaBeanClassImports();
//			jbci.declareType(jcl.getJavaTypeFull());
////			this.addImportsJpa(jbci);
			this.addLink(jcl);
		}
		
		/*
		 * DATABASE FOREIGN KEYS  ( v 2.0.7 )
		 */
		this._foreignKeys = new LinkedList<JavaBeanClassForeignKey>();
		Collection<ForeignKey> foreignKeys = entity.getForeignKeysCollection();
		for ( ForeignKey fk : foreignKeys ) {
			_foreignKeys.add( new JavaBeanClassForeignKey(fk ) );
		}
		
		// import resolution
		//this.endOfDefinition();
		endOfAttributesDefinition();
		
		// import JPA resolution
		//this.processJpaSpecificImport();
	}
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Constructor based on a list of attributes
	 * @param sFullClassName
	 * @param attributes
	 */
	public JavaBeanClass( String sFullClassName, JavaBeanClassAttribute[] attributes ) 
	{
		super( sFullClassName );
		initAttributes( attributes ); 
	}
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Constructor based on a list of attributes
	 * @param sShortClassName
	 * @param sPackage
	 * @param attributes
	 */
	public JavaBeanClass( String sShortClassName, String sPackage, JavaBeanClassAttribute[] attributes ) 
	{
		super( sShortClassName, sPackage);
		initAttributes( attributes ); 
	}
	
	//-----------------------------------------------------------------------------------------------
	private void initAttributes(JavaBeanClassAttribute[] attributes) 
	{
		//--- Add each attribute 
		if ( attributes != null ) 
		{
			for ( JavaBeanClassAttribute attribute : attributes )
			{
				addAttribute(attribute);
			}
			//endOfDefinition(); // close the class definition (prepares imports list)
			endOfAttributesDefinition();
		}		
	}
	//-----------------------------------------------------------------------------------------------
	private void addLink(JavaBeanClassLink jcl) {
		if ( _links == null )
		{
			_links = new LinkedList<JavaBeanClassLink>();
		}
		_links.add(jcl);
	}

//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a multiline String containing all the Java JPA annotations required for the current entity",
//			"without left marging before each line"
//		},
//		example={	
//			"$entity.jpaAnnotations"
//		}
//	)
//	public String getJpaAnnotations()
//    {
//		return jpaAnnotations(0);
//    }
	
//	//-------------------------------------------------------------------------------------
//	/**
//	 * Returns the JPA annotations without left margin 
//	 * Usage : $x.jpaAnnotations() 
//	 * @return
//	 */
//	@VelocityNoDoc
//	public String jpaAnnotations()
//    {
//		return jpaAnnotations(0);
//    }
//	
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a multiline String containing all the Java JPA annotations required for the current entity",
//			"with the given left marging before each line"
//		},
//		parameters = "leftMargin : number of blanks for the left margin",
//		example={	
//			"$entity.jpaAnnotations(4)"
//		}
//	)
//	public String jpaAnnotations(int iLeftMargin)
//    {
//		AnnotationsBuilder b = new AnnotationsBuilder(iLeftMargin);
//		
//		b.addLine("@Entity");
//		
//		String s = "@Table(name=\"" + _sDatabaseTable + "\"" ;
//		if ( ! StrUtil.nullOrVoid( _sDatabaseSchema ) ) {
//			s = s + ", schema=\"" + _sDatabaseSchema + "\"" ; 
//		}
//		if ( ! StrUtil.nullOrVoid( _sDatabaseCatalog ) ) {
//			s = s + ", catalog=\"" + _sDatabaseCatalog + "\"" ; 
//		}
//		s = s + " )" ;
//
//		b.addLine(s);
//		
//		return b.getAnnotations();
//    }
//	
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a list of all the Java imports required for all the fields of the current entity",
//			"For example, 'java.util.Date' if the entity uses Date objects, etc..."
//		},
//		example={	
//			"#foreach( $import in $entity.imports )",
//			"import $import;",
//			"#end" 
//		}
//	)
//	@VelocityReturnType("List of 'String'")
//	public List<String> getImports() 
//	{
////		if ( _importsForAllFields != null )
////		{
////			return _importsForAllFields ;
////		}
////		return VOID_STRINGS_LIST ;
//		return _importsForAllFields ;
//	}
	
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a list of all the Java imports required for the KEY fields of the current entity"
//		},
//		example={	
//			"#foreach( $import in $entity.importsForKeyFields )",
//			"import $import;",
//			"#end" 
//		}
//	)
//	@VelocityReturnType("List of 'String'")
//	public List<String> getImportsForKeyFields() 
//	{
//		return _importsForKeyFields ;
//	}
	
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a list of all the Java JPA imports required by the current entity"
//		},
//		example={	
//			"#foreach( $import in $entity.importsJpa )",
//			"import $import;",
//			"#end" 
//		}
//	)
//	@VelocityReturnType("List of 'String'")
//	public Set<String> getImportsJpa() 
//	{
//		if ( _importsJpa != null )
//		{
//			return _importsJpa ;
//		}
//		return VOID_STRINGS_SET ;
//	}

	//-------------------------------------------------------------------------------------
	/**
	 * Returns all the attributes defined for this class
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns all the attributes defined for this entity"
		},
		example="$entity.attributes"
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getAttributes() 
	{
		if ( _attributes != null )
		{
			return _attributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the number of attributes defined for this entity"
		},
		example="$entity.attributesCount",
		since="2.0.7"
	)
	public int getAttributesCount() 
	{
		if ( _attributes != null )
		{
			return _attributes.size() ;
		}
		return 0 ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns a list of all the links defined for the current entity"
		},
		example={	
			"#foreach( $link in $entity.links )",
			"...",
			"#end" 
		}
	)
	@VelocityReturnType("List of 'link' objects")
	public List<JavaBeanClassLink> getLinks() 
	{
		if ( _links != null )
		{
			if ( _links.size() > 0 ) {
				return _links ;
			}
		}
		return VOID_LINKS_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns a list of all the links selected in the model for the current entity"
		},
		example={	
			"#foreach( $link in $entity.selectedLinks )",
			"...",
			"#end" 
		}
	)
	@VelocityReturnType("List of 'link' objects")
	public List<JavaBeanClassLink> getSelectedLinks() 
	{
		if ( _links != null )
		{
			if ( _links.size() > 0 ) {
				LinkedList<JavaBeanClassLink> selectedLinks = new LinkedList<JavaBeanClassLink>();
				for ( JavaBeanClassLink link : _links ) {
					if ( link.isSelected() ) {
						selectedLinks.add(link) ;
					}
				}
				return selectedLinks ;
			}
		}
		return VOID_LINKS_LIST ;
	}

	private void checkCriterion ( int criterion ) {
		if ( criterion == Const.KEY || criterion == Const.NOT_KEY ) return ;
		if ( criterion == Const.TEXT || criterion == Const.NOT_TEXT ) return ;
		if ( criterion == Const.IN_LINKS || criterion == Const.NOT_IN_LINKS ) return ;
		if ( criterion == Const.IN_SELECTED_LINKS || criterion == Const.NOT_IN_SELECTED_LINKS ) return ;
		// else : invalid criterion
		throw new GeneratorContextException("Invalid criterion in getAttributesByCriteria argument(s)");
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityNoDoc
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1  ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + ")" );
		checkCriterion(c1);
		return getAttributesByAddedCriteria(c1);
	}
	//-------------------------------------------------------------------------------------
	@VelocityNoDoc
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		return getAttributesByAddedCriteria(c1 + c2);
	}
	//-------------------------------------------------------------------------------------
	@VelocityNoDoc
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2, int c3 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + "," + c3 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		checkCriterion(c3);
		return getAttributesByAddedCriteria(c1 + c2 + c3);
	}
	@VelocityMethod ( text= { 
			"Returns all the attributes of this entity matching the given criteria",
			"This method accepts 1 to 4 criteria",
			"The critera are combined using the 'AND' operator",
			"Usable criteria ( to be prefixed with '$const.' ) : ",
			"KEY,  NOT_KEY,  IN_LINKS,  NOT_IN_LINKS,  IN_SELECTED_LINKS,  NOT_IN_SELECTED_LINKS,  TEXT,  NOT_TEXT  "
	},
	parameters = {
			"crit1 : 1st criterion ",
			"crit2 : 2nd criterion (optional)",
			"crit3 : 3rd criterion (optional)",
			"crit4 : 4th criterion (optional)"
	},
	example = {
			"$entity.getAttributesByCriteria($const.NOT_KEY)",
			"$entity.getAttributesByCriteria($const.NOT_KEY, $const.NOT_IN_SELECTED_LINKS)"
	}
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2, int c3, int c4 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + "," + c3 + "," + c4 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		checkCriterion(c3);
		checkCriterion(c4);
		return getAttributesByAddedCriteria(c1 + c2 + c3 + c4);
	}
	
	//-------------------------------------------------------------------------------------
	private List<JavaBeanClassAttribute> getAttributesByAddedCriteria( int criteria ) 
	{
		ContextLogger.log("getAttributesByAddedCriteria(" + criteria + ")" );
		List<JavaBeanClassLink> allLinks = getLinks() ;
		List<JavaBeanClassLink> selectedLinks = getSelectedLinks() ;
		
		LinkedList<JavaBeanClassAttribute> selectedAttributes = new LinkedList<JavaBeanClassAttribute>();
		
		for ( JavaBeanClassAttribute attribute : _attributes ) {
			Boolean selectedByKey  = null ;
			Boolean selectedByText = null ;
			Boolean selectedByLink = null ;
			Boolean selectedBySelectedLink = null ;
			
			//--- IS KEY ?
			if ( ( criteria & Const.KEY ) != 0 ) {
				selectedByKey = attribute.isKeyElement();
			}
			if ( ( criteria & Const.NOT_KEY ) != 0 ) {
				selectedByKey = ! attribute.isKeyElement();
			}

			//--- IS TEXT ?
			if ( ( criteria & Const.TEXT ) != 0 ) {
				selectedByText = attribute.isLongText();
			}
			if ( ( criteria & Const.NOT_TEXT ) != 0 ) {
				selectedByText = ! attribute.isLongText();
			}
			
			//--- IS IN LINK ?
			if ( ( criteria & Const.IN_LINKS ) != 0 ) {
				selectedByLink = attribute.isUsedInLinkJoinColumn( allLinks ) ;
			}
			if ( ( criteria & Const.NOT_IN_LINKS ) != 0 ) {
				selectedByLink = ! attribute.isUsedInLinkJoinColumn( allLinks ) ;
			}
			
			//--- IS IN SELECTED LINK ?
			if ( ( criteria & Const.IN_SELECTED_LINKS ) != 0 ) {
				selectedBySelectedLink = attribute.isUsedInLinkJoinColumn( selectedLinks ) ;
			}			
			if ( ( criteria & Const.NOT_IN_SELECTED_LINKS ) != 0 ) {
				selectedBySelectedLink = ! attribute.isUsedInLinkJoinColumn( selectedLinks ) ;
			}
			
			int criteriaCount = 0 ;
			int selected = 0 ;
			if ( selectedByKey != null ) {
				criteriaCount++ ;
				if ( selectedByKey ) selected++ ;
			}
			if ( selectedByText != null ) {
				criteriaCount++ ;
				if ( selectedByText ) selected++ ;
			}
			if ( selectedByLink != null ) {
				criteriaCount++ ;
				if ( selectedByLink ) selected++ ;
			}
			if ( selectedBySelectedLink != null ) {
				criteriaCount++ ;
				if ( selectedBySelectedLink ) selected++ ;
			}

			ContextLogger.log("getAttributesByAddedCriteria(" + criteria + ") : " + attribute.getName() + " : " + criteriaCount + " :: " + selected );
			
			if ( ( criteriaCount > 0 ) && ( selected == criteriaCount ) ) {	
				// All criteria verified ( "AND" ) => keep this attribute
				selectedAttributes.add(attribute) ;
			}
			
		} // for each ...
		if ( selectedAttributes.size() > 0 ) {
			return selectedAttributes ;
		}
		
		return VOID_ATTRIBUTES_LIST ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the attributes used in the Primary Key for this entity"
		},
		example= {
			"#foreach( $attribute in $entity.keyAttributes )",
			"...",
			"#end"
		}
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getKeyAttributes() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the number of attributes used in the Primary Key for this entity"
		},
		example= {
			"$entity.keyAttributesCount"
		},
		since="2.0.7"
	)
	public int getKeyAttributesCount() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes.size() ;
		}
		return 0 ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the attributes NOT used in the Primary Key for this entity"
		},
		example= {
			"#foreach( $attribute in $entity.nonKeyAttributes )",
			"...",
			"#end"
		}
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getNonKeyAttributes() 
	{
		if ( _nonKeyAttributes != null ) {
			return _nonKeyAttributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the number of attributes NOT used in the Primary Key for this entity"
		},
		example= {
			"$entity.nonKeyAttributesCount"
		},
		since="2.0.7"
	)
	public int getNonKeyAttributesCount() 
	{
		if ( _nonKeyAttributes != null ) {
			return _nonKeyAttributes.size() ;
		}
		return 0 ;
	}

	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns the database table mapped with this entity",
//			"DEPRECATED : use 'databaseTable' instead "
//		},
//		example="$entity.sqlTable",
//		deprecated=true
//	)
//	@Deprecated
//	public String getSqlTable() 
//	{
//		return _sDatabaseTable ;
//	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the database table mapped with this entity"
		},
		example="$entity.databaseTable"
	)
	public String getDatabaseTable() 
	{
		return _sDatabaseTable ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the database catalog of the table mapped with this entity"
		},
		example="$entity.databaseCatalog"
	)
	public String getDatabaseCatalog() 
	{
		return _sDatabaseCatalog ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the database schema of the table mapped with this entity"
		},
		example="$entity.databaseSchema"
	)
	public String getDatabaseSchema() 
	{
		return _sDatabaseSchema ;
	}
	
	//-------------------------------------------------------------------------------------
	/**
	 * Returns all the database foreign keys defined for this entity
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns all the database foreign keys defined for this entity"
		},
		example="$entity.databaseForeignKeys",
		since="2.0.7"		
	)
	@VelocityReturnType("List of 'foreign keys' objects")
	public List<JavaBeanClassForeignKey> getDatabaseForeignKeys() 
	{
		if ( _foreignKeys != null )
		{
			return _foreignKeys ;
		}
		return VOID_FOREIGN_KEYS_LIST ;
	}

	//-------------------------------------------------------------------------------------
	/**
	 * Returns all the database foreign keys defined for this entity
	 * @return
	 */
	@VelocityMethod ( text= { 
			"Returns the number of database foreign keys defined for this entity"
		},
		example="$entity.databaseForeignKeysCount",
		since="2.0.7"		
	)
	public int getDatabaseForeignKeysCount() 
	{
		if ( _foreignKeys != null )
		{
			return _foreignKeys.size() ;
		}
		return 0 ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the database type of the table mapped with this entity <br>",
			"Type returned by the database meta-data ( 'TABLE', 'VIEW', ... ) "
		},
		example="$entity.databaseType",
		since="2.0.7"
	)
	public String getDatabaseType() 
	{
		return _sDatabaseType ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if the database type is 'TABLE' "
		},
		example="$entity.isTableType()",
		since="2.0.7"
	)
	public boolean isTableType() 
	{
		if ( _sDatabaseType != null ) {
			return "TABLE".equalsIgnoreCase( _sDatabaseType.trim() ) ;
		}
		return false;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if the database type is 'VIEW' "
		},
		example="$entity.isViewType()",
		since="2.0.7"
	)
	public boolean isViewType() 
	{
		if ( _sDatabaseType != null ) {
			return "VIEW".equalsIgnoreCase( _sDatabaseType.trim() ) ;
		}
		return false;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns a String containing all the columns of the Primary Key",
			"The returned column names are separated by a comma and have quotes characters",
			"i.e. : '\"code\", \"type\"' "
		},
		example={	
			"String KEY_COLUMNS[] = { $entity.sqlKeyColumns };"
		}
	)
	public String getSqlKeyColumns() 
	{
		if ( _sSqlKeyColumns == null ) // list not yet built
		{
			_sSqlKeyColumns = buildDbColumnsList( true ); 
		}
		return _sSqlKeyColumns ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns a String containing all the columns not used in the Primary Key ",
			"The returned column names are separated by a comma and have quotes characters",
			"i.e. : '\"code\", \"type\"' "
		},
		example={	
			"String DATA_COLUMNS[] = { $entity.sqlNonKeyColumns };"
		}
	)
	public String getSqlNonKeyColumns() 
	{
		if ( _sSqlNonKeyColumns == null ) // list not yet built
		{
			_sSqlNonKeyColumns = buildDbColumnsList( false ); 
		}
		return _sSqlNonKeyColumns ;
	}

//    /**
//     * Returns the Java line instruction for the toString() method
//     * @return
//     */
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns the Java line instruction for the toString() method",
//			"DEPRECATED : use 'toStringMethodCodeLines()' instead "
//		},
//		example="$entity.toStringInstruction",
//		deprecated=true
//	)
//	@Deprecated
//    public String getToStringInstruction()
//    {
//    	if ( _attributes != null )
//    	{
//    		int n = _attributes.size();
//    		if ( n > 1 )
//    		{
//                StringBuffer sb = new StringBuffer();
//            	for ( int i = 0 ; i < n ; i++ )        		
//            	{
//            		JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(i);
//                    if ( i > 0 ) // if it's not the first one
//                    {
//                        sb.append( " + \"|\" + " ) ;
//                    }        		
//                    sb.append( attribute.getName() ) ;
//            	}
//            	return sb.toString(); // example : 'aaa + "|" + bbb + "|" + ccc'
//    		}
//    		else 
//    		{
//    			// Single attribute => no automatic conversion to String
//    			JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(0);
//    			String sFullType = attribute.getFullType();
//    			if ( sFullType != null ) {
//    				if ( sFullType.startsWith("java.") ) {
//    					//--- Java object
//    					if ( sFullType.equals("java.lang.String") ) {
//        					return attribute.getName() ;
//    					}
//    					else {
//        					return attribute.getName() + ".toString()";
//    					}
//    				}
//    				else {
//    					//--- Primitive type
//    					return "\"\" + " + attribute.getName() ;
//    				}
//    			}
//    		}
//    	}
//    	return "\"Class " + getName() + " (no attributes) \"" ;
//    }

//    private boolean typeUsedInToString( String sType )
//    {
//    	if ( null == sType ) return false ;
//    	String s = sType.trim() ;
//    	if ( s.endsWith("]") ) return false ; // Array
//    	if ( s.endsWith("Blob") ) return false ; 
//    	if ( s.endsWith("Clob") ) return false ; 
//    	return true ;
//    }
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a multiline String containing all the Java instructions for the 'toString' method",
//			"Argument : number of spaces for the left margin ",
//			""
//		},
//		example={	
//			"$entity.toStringMethodCodeLines(4)"
//		},
//		parameters = "leftMargin : number of blanks for the left margin"
//	)
//    public String toStringMethodCodeLines( int iLeftMargin )
//    {
//    	return toStringMethodCodeLinesWithKey( iLeftMargin, null );
//    }
    
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod (
//		text= { 
//			"Returns a multiline String containing all the Java instructions for the 'toString' method",
//			"The primary key (composite or not) and all the 'non key' attributes are used"
//		},
//		example={	
//			"$entity.toStringMethodCodeLinesWithKey(8, \"compositePrimaryKey\" )"
//		},
//		parameters = {
//				"leftMargin : number of blanks for the left margin",
//				"keyVarName : variable name for the composite primary key embedded id (if any) "
//		}
//	)
//    public String toStringMethodCodeLinesWithKey( int iLeftMargin, String embeddedIdName )
//    {
//    	return toStringMethodCodeLinesWithKey( iLeftMargin, _nonKeyAttributes, embeddedIdName );
//    }
//    
//	//-------------------------------------------------------------------------------------
//	@VelocityMethod ( text= { 
//			"Returns a multiline String containing all the Java instructions for the 'toString' method",
//			"The primary key (composite or not) and given list of attributes are used"
//		},
//		example={	
//			"$entity.toStringMethodCodeLinesWithKey(8, $attributes, \"compositePrimaryKey\" )"
//		},
//		parameters = {
//			"leftMargin : number of blanks for the left margin",
//			"attributes : specific list of attributes to be used in the 'toString' method (except key attributes)",
//			"keyVarName : variable name for the composite primary key embedded id (if any) "
//	}
//	)
//    public String toStringMethodCodeLinesWithKey( int iLeftMargin, List<JavaBeanClassAttribute> specificNonKeyAttributes, String embeddedIdName )
//    {
//    	String leftMargin = GeneratorUtil.blanks(iLeftMargin);
//    	
//    	if ( _attributes != null )
//    	{
//    		int n = _attributes.size();
//    		if ( n > 0 )
//    		{
//    			int count = 0 ;
//                StringBuffer sb = new StringBuffer();
//    			sb.append(leftMargin);
//    			sb.append("StringBuffer sb = new StringBuffer(); \n");
//    			
//    			//--- PRIMARY KEY attributes ( composite key or not )
//    			if ( _keyAttributes != null ) {
//					if ( hasCompositePrimaryKey() && ( embeddedIdName != null ) ) {
//						// Embedded id 
//						count = count + toStringForEmbeddedId( leftMargin, sb, embeddedIdName );
//					}
//					else {
//						// No embedded id 
//						count = count + toStringForAttributes( leftMargin, sb, _keyAttributes );
//					}
//        		}
//
//    			if ( count > 0 ) {
//    				sb.append(leftMargin);
//    				sb.append("sb.append(\"|\"); \n");
//    			}
//    			
//    			//--- NON KEY attributes ( composite key or not )
//    			if ( specificNonKeyAttributes != null ) {
//    				count = count + toStringForAttributes( leftMargin, sb, specificNonKeyAttributes );
//    			}
//    			
//    			sb.append(leftMargin);
//    			sb.append("return sb.toString();"); // Last line => No EOL
//    			if ( count > 0 ) {
//                    return sb.toString() ;
//    			}
//    			else {
//    				return leftMargin + "return \"Instance of " + getName() + " (no usable attribute)\" ;" ;
//    			}
//    		}
//    		else {
//				return leftMargin + "return \"Instance of " + getName() + " (attributes.size = 0)\" ;" ;    			
//    		}
//    	}
//    	else {
//        	return leftMargin + "return \"Instance of " + getName() + " (attributes is null)\" ;" ;
//    	}
//    }
    
//    private int toStringForAttributes( String leftMargin, StringBuffer sb, List<JavaBeanClassAttribute> attributes )
//    {
//    	int count = 0 ;
//    	for ( JavaBeanClassAttribute attribute : attributes ) {
//    		if ( typeUsedInToString( attribute.getType() ) )
//    		{
//                if ( count > 0 ) // if it's not the first one
//                {
//        			sb.append(leftMargin); sb.append("sb.append( \"|\" ); \n");
//                }        		
//    			sb.append(leftMargin); sb.append("sb.append(" + attribute.getName() + "); \n" );
//    			count++ ;
//    		}
//    	}
//    	return count ;
//    }
//    private int toStringForEmbeddedId( String leftMargin, StringBuffer sb, String embeddedIdName )
//    {
//		sb.append(leftMargin); sb.append("if ( " + embeddedIdName + " != null ) {  \n");
//		sb.append(leftMargin); sb.append("    sb.append(" + embeddedIdName + ".toString());  \n");
//		sb.append(leftMargin); sb.append("}  \n");
//		sb.append(leftMargin); sb.append("else {  \n");
//		sb.append(leftMargin); sb.append("    sb.append( \"(null-key)\" );  \n");
//		sb.append(leftMargin); sb.append("}  \n");
//		return 1 ;
//    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the attributes NOT tagged as 'long text' for this entity",
			"( 'standard attributes' )"
		},
		example= {
			"#foreach( $attribute in $entity.nonTextAttributes )",
			"...",
			"#end"
		}
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getNonTextAttributes() 
	{
		if ( _nonTextAttributes == null ) // list not yet built
		{
			_nonTextAttributes = buildTextAttributesList ( false ); // NOT LONG TEXT
			if ( _nonTextAttributes != null ) {
				return _nonTextAttributes ;
			}
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the attributes tagged as 'long text' for this entity",
			"( specific attributes used to store long text )"
		},
		example= {
			"#foreach( $attribute in $entity.textAttributes )",
			"...",
			"#end"
		}
	)
	@VelocityReturnType("List of 'attribute' objects")
	public List<JavaBeanClassAttribute> getTextAttributes() 
	{
		if ( _textAttributes == null ) // list not yet built
		{
			_textAttributes = buildTextAttributesList ( true ); // Special "LONG TEXT"
			if ( _textAttributes != null ) {
				return _textAttributes ;
			}
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if this entity has at least one attribute tagged as 'long text'"
		},
		example= {
			"#if ( $entity.hasTextAttribute() )",
			"...",
			"#end"
		}
	)
	public boolean hasTextAttribute() 
	{
    	if ( _attributes != null )
    	{
    		int n = _attributes.size();
        	for ( int i = 0 ; i < n ; i++ )        		
        	{
        		JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(i);
                if ( attribute.isLongText() ) 
                {
                	return true ;
                }
        	}
    	}
    	return false ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if this entity has a composite primary key ",
			"( a primary key composed of 2 or more attributes )"
		},
		example= {
			"#if ( $entity.hasCompositePrimaryKey() )",
			"...",
			"#end"
		}
	)
	public boolean hasCompositePrimaryKey() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes.size() > 1 ;
		}
		return false ; // No key attributes
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if this entity has a primary key ",
			"( a primary key composed of one or more attributes )"
		},
		example= {
			"#if ( $entity.hasPrimaryKey() )",
			"...",
			"#end"
		},
		since="2.0.7"
	)
	public boolean hasPrimaryKey() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes.size() > 0 ;
		}
		return false ; // No key attributes
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns TRUE if this entity has an 'auto-incremented' key attribute ",
			"( a key based on a numeric value incremented by the database )"
		},
		example= {
			"#if ( $entity.hasAutoIncrementedKey() )",
			"...",
			"#end"
		}
	)
	public boolean hasAutoIncrementedKey() 
	{
		if ( _keyAttributes != null ) {
			for ( JavaBeanClassAttribute keyAttribute : _keyAttributes ) {
				if ( keyAttribute.isAutoIncremented() ) {
					return true ; 
				}
			}
		}
		return false ; 
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( text= { 
			"Returns the attribute used as the autoincremented key ",
			"or null if none",
			""
	},
	example = {
			"$entity.autoincrementedKeyAttribute"
	}
	)
	@VelocityReturnType("'attribute' object")
	public JavaBeanClassAttribute getAutoincrementedKeyAttribute() 
	{
		List<JavaBeanClassAttribute> keyAttributes = getKeyAttributes();
    	if ( keyAttributes != null )
    	{
        	if ( keyAttributes.size() == 1 ) 
    		{
    			// Only one attribute in the PK
    			JavaBeanClassAttribute attribute = keyAttributes.get(0);
    			if ( attribute != null ) {
                    if ( attribute.isAutoIncremented() ) 
                    {
                    	// This unique PK field is auto-incremented => return it
                    	return attribute ; 
                    }
    			}
    		}
    	}
    	return null ;
	}

	/* (non-Javadoc)
	 * Same as getName() 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return super.toString() ;
	}
	
	
	// -------------------------------------------------------------------------------------------------
	
	/**
	 * Add an attribute for this class
	 * @param attribute
	 */
    private void addAttribute(JavaBeanClassAttribute attribute) 
	{
		if ( _attributes == null )
		{
			_attributes = new LinkedList<JavaBeanClassAttribute>();
		}
		_attributes.add(attribute);
	}
	
//	/**
//	 * Initialize the imports required for all fields 
//	 * @param imports
//	 */
//    private void setImportsForAllFields(JavaBeanClassImports imports) 
//	{
//		// Reset ALL => create a new list
//		_importsForAllFields = new LinkedList<String>();
//		_importsForAllFields.addAll( imports.getList() );
//	}
    
//    /**
//	 * Initialize the imports required for the key fields of the class 
//     * @param imports
//     */
//    private void setImportsForKeyFields(JavaBeanClassImports imports) 
//	{
//		// Reset ALL => create a new list
//		_importsForKeyFields = new LinkedList<String>();
//		_importsForKeyFields.addAll( imports.getList() );
//	}    

//	/**
//	 * Init the Jpa imports list
//	 * @param imports
//	 */
//    private void addImportsJpa(JavaBeanClassImports imports) 
//	{
//		if ( imports != null )
//		{
//			// Reset ALL => create a new list
//			if (_importsJpa == null) {
//				_importsJpa = new LinkedHashSet<String>();
//			}
//			
//			_importsJpa.addAll( imports.getList() );
//		}
//	}
    
	private LinkedList<JavaBeanClassAttribute> buildAttributesList ( boolean bKeyAttribute ) 
	{
		LinkedList<JavaBeanClassAttribute> attributesList = new LinkedList<JavaBeanClassAttribute>();
    	if ( _attributes != null )
    	{
    		int n = _attributes.size();
        	for ( int i = 0 ; i < n ; i++ )        		
        	{
        		//JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(i);
        		JavaBeanClassAttribute attribute = _attributes.get(i);
                if ( attribute.isKeyElement() == bKeyAttribute ) 
                {
                	attributesList.add(attribute);
                }        		
        	}
    	}
		return attributesList ;
	}

	//-----------------------------------------------------------------------------------------------
	/**
	 * This method closes the definition of the class (when all the attributes have been added) <br>
	 * 
	 * It build the "KEY" and "NON KEY" attributes 
	 * 
	 * It determines if there is import types collision ( eg "java.util.Date" with "java.sql.Date" ) <br>
	 * and managed the imports list and attributes declarations types to avoid imports error
	 *  
	 */
	private void endOfAttributesDefinition() // v 2.1.0
	{
		if ( _attributes == null ) return ;
		
		//--- Build the list of the "KEY" attributes
		_keyAttributes = buildAttributesList ( true );
		
		//--- Build the list of the "NON KEY" attributes
		_nonKeyAttributes = buildAttributesList ( false ); 

		//--- Duplicated short types detection
		AmbiguousTypesDetector duplicatedTypesDetector = new AmbiguousTypesDetector(_attributes);
		List<String> ambiguousTypes = duplicatedTypesDetector.getAmbiguousTypes();
		for ( JavaBeanClassAttribute attribute : _attributes ) {
			//--- Is this attribute's type ambiguous ?
			if ( ambiguousTypes.contains( attribute.getFullType() ) ) {
				//--- Yes => force this attribute to use its "full type" for variable declaration
				attribute.useFullType() ; // v 2.0.7
			}
		}
	}
	
//	//-----------------------------------------------------------------------------------------------
//	/**
//	 * This method close the definition of the class (when all the attributes have been added) <br>
//	 * 
//	 * It build the "KEY" and "NON KEY" attributes 
//	 * 
//	 * It determines if there is import types collision ( eg "java.util.Date" with "java.sql.Date" ) <br>
//	 * and managed the imports list and attributes declarations types to avoid imports error
//	 *  
//	 */
//	private void endOfDefinition() 
//	{
//		if ( _attributes == null ) return ;
//		
//		//--- Build the list of the "KEY" attributes
//		_keyAttributes = buildAttributesList ( true );
//		
//		//--- Build the list of the "NON KEY" attributes
//		_nonKeyAttributes = buildAttributesList ( false ); 
//		
//		//--- Define the imports required for all the fields of this class 
//		JavaBeanClassImports javaImportsForAllFields = new JavaBeanClassImports();
//		//JavaBeanClassImports javaImportsForKeyFields = new JavaBeanClassImports();
//		
//		for ( JavaBeanClassAttribute attribute : _attributes ) {
//			javaImportsForAllFields.declareType( attribute.getFullType() ); // register the type to import if necessary
////			if ( attribute.isKeyElement() ) {
////				javaImportsForKeyFields.declareType( attribute.getFullType() );
////			}
//		}
//		
//		//--- Extract potential collided types ( and retrieve the list of collided full types )
//		LinkedList<String> collidedTypes = javaImportsForAllFields.extractDuplicatedShortNames();
////		javaImportsForKeyFields.extractDuplicatedShortNames();
//
////		//--- Set imports list for the current class
////		this.setImportsForAllFields(javaImportsForAllFields);
////		this.setImportsForKeyFields(javaImportsForKeyFields);
//		
//		//--- If there's collided types => Check each attribute type 
//		if ( collidedTypes != null )
//		{
//			//--- Some collided types have been extracted from imports 
//			for ( JavaBeanClassAttribute attr : _attributes ) {
//				String sFullType = attr.getFullType();
//				if ( collidedTypes.contains( sFullType ) ) // if this attribute is impacted
//				{
//					//--- force this attributes to use its "full type" for variable declaration
//					//attr.forceType ( sFullType );
//					attr.useFullType() ; // v 2.0.7
//				}
//			}
//		}
//	}

//	/**
//	 * This method process the jpa specific imports <br>
//	 */
//	private void processJpaSpecificImport() {
//		JavaBeanClassImports jpaImports = new JavaBeanClassImports();
//
//		// TODO a afiner
//		jpaImports.declareType("javax.persistence.*");
//		
//		this.addImportsJpa(jpaImports);
//		
//		/*
//		jpaImports.declareType("javax.persistence.Entity");
//		jpaImports.declareType("javax.persistence.Table");
//		jpaImports.declareType("javax.persistence.Id");
//		
//		jpaImports.declareType("javax.persistence.UniqueConstraint");
//		jpaImports.declareType("javax.persistence.EmbeddedId");
//		jpaImports.declareType("javax.persistence.Embeddable");
//		jpaImports.declareType("javax.persistence.AttributeOverride");
//		jpaImports.declareType("javax.persistence.AttributeOverrides");
//
//		jpaImports.declareType("javax.persistence.OneToOne");
//		jpaImports.declareType("javax.persistence.ManyToMany");
//		jpaImports.declareType("javax.persistence.ManyToOne");
//		jpaImports.declareType("javax.persistence.OneToMany");
//
//		jpaImports.declareType("javax.persistence.GeneratedValue");
//		jpaImports.declareType("javax.persistence.GenerationType");
//		jpaImports.declareType("javax.persistence.SequenceGenerator");
//		jpaImports.declareType("javax.persistence.TableGenerator");
//		*/
//	}

	private String buildDbColumnsList ( boolean bKeyAttribute ) 
	{
    	if ( _attributes != null )
    	{
            StringBuffer sb = new StringBuffer(60);
            int iCount = 0 ;
    		int n = _attributes.size();
        	for ( int i = 0 ; i < n ; i++ )        		
        	{
        		JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(i);
                if ( attribute.isKeyElement() == bKeyAttribute ) 
                {
                	if ( iCount > 0 ) // Not the first one
                	{
                        sb.append( ", " ) ;
                	}
                    sb.append( "\"" + attribute.getDatabaseName().trim() + "\"" ) ;
                    iCount++ ;
                }        		
        	}
    		return sb.toString() ;
    	}
    	return "" ;
	}

	/**
	 * "Text" or "non Text" attributes
	 * @param bLongText
	 * @return
	 */
	private LinkedList<JavaBeanClassAttribute> buildTextAttributesList ( boolean bLongText ) 
	{
    	if ( _attributes != null )
    	{
			LinkedList<JavaBeanClassAttribute> list = new LinkedList<JavaBeanClassAttribute>();
    		int n = _attributes.size();
        	for ( int i = 0 ; i < n ; i++ )        		
        	{
        		JavaBeanClassAttribute attribute = _attributes.get(i);
                if ( attribute.isLongText() == bLongText ) 
                {
                	list.add(attribute);
                }        		
        	}
    		return list ;
    	}
    	return null ;
	}

}
