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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generator.GeneratorContextException;
import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.tools.AnnotationsBuilder;
import org.telosys.tools.repository.model.Column;
import org.telosys.tools.repository.model.Entity;
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
public class JavaBeanClass extends JavaClass
{
	
	private final static List<JavaBeanClassAttribute> VOID_ATTRIBUTES_LIST  = new LinkedList<JavaBeanClassAttribute>();

	private final static List<String>                 VOID_STRINGS_LIST    = new LinkedList<String>();
	private final static Set<String>                  VOID_STRINGS_SET     = new LinkedHashSet<String>();
	
	private final static List<JavaBeanClassLink>      VOID_LINKS_LIST    = new LinkedList<JavaBeanClassLink>();
	
	private List<String>                       _imports     = null ; // The imports for this class ( list of "java.xx.Class" )
	private LinkedList<JavaBeanClassAttribute> _attributes  = null ; // The attributes for this class ( ALL ATTRIBUTES )
	
    private String     _sDatabaseTable   = null ; // Table name this class is mapped with
    private String     _sDatabaseCatalog = null ; // The table's catalog 
    private String     _sDatabaseSchema  = null ; // The table's schema 
    
	private LinkedList<JavaBeanClassAttribute>  _keyAttributes     = null ; // The KEY attributes for this class
	private LinkedList<JavaBeanClassAttribute>  _nonKeyAttributes  = null ; // The NON KEY attributes for this class
	private String     _sSqlKeyColumns = null ;
	private String     _sSqlNonKeyColumns = null ;
	
	//--- XML mapper infos
	private LinkedList<JavaBeanClassAttribute> _nonTextAttributes  = null ; // Standard attributes for this class ( not "long text" )
	private LinkedList<JavaBeanClassAttribute> _textAttributes     = null ; // Special "long text" attributes for this class

	//--- JPA specific
	private Set<String>                   _importsJpa = null ; // The imports JPA for this class ( list of "java.xx.Class" )
	private LinkedList<JavaBeanClassLink> _links  = null ; // The links for this class ( ALL ATTRIBUTES )
	private Entity                        _entite = null;
	
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
		
		this._entite = entity;
		this._sDatabaseTable = entity.getName();
		_sDatabaseCatalog = entity.getCatalog();
		_sDatabaseSchema  = entity.getSchema();
		
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
			JavaBeanClassLink jcl = new JavaBeanClassLink(link, this._entite , entityCible );
			
			//ajouter import specifique
			JavaBeanClassImports jbci = new JavaBeanClassImports();
			jbci.declareType(jcl.getJavaTypeFull());
			this.addImportsJpa(jbci);
			this.addLink(jcl);
		}
		
		// import resolution
		this.endOfDefinition();
		
		// import JPA resolution
		this.processJpaSpecificImport();
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
			endOfDefinition(); // close the class definition (prepares imports list)
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
	 * Returns the JPA annotations with the given left margin 
	 * Usage : $x.jpaAnnotations(4) 
	 * @param iLeftMargin
	 * @return
	 */
	public String jpaAnnotations(int iLeftMargin)
    {
		AnnotationsBuilder b = new AnnotationsBuilder(iLeftMargin);
		
		b.addLine("@Entity");
		
		String s = "@Table(name=\"" + _sDatabaseTable + "\"" ;
		if ( ! StrUtil.nullOrVoid( _sDatabaseSchema ) ) {
			s = s + ", schema=\"" + _sDatabaseSchema + "\"" ; 
		}
		if ( ! StrUtil.nullOrVoid( _sDatabaseCatalog ) ) {
			s = s + ", catalog=\"" + _sDatabaseCatalog + "\"" ; 
		}
		s = s + " )" ;

		b.addLine(s);
		
		return b.getAnnotations();
    }
	
	/**
	 * Returns an array of imports
	 * @return
	 */
	public List<String> getImports() 
	{
		if ( _imports != null )
		{
			return _imports ;
		}
		return VOID_STRINGS_LIST ;
	}

	/**
	 * Returns an array of imports JPA
	 * @return
	 */
	public Set<String> getImportsJpa() 
	{
		if ( _importsJpa != null )
		{
			return _importsJpa ;
		}
		return VOID_STRINGS_SET ;
	}

	/**
	 * Returns all the attributes defined for this class
	 * @return
	 */
	public List<JavaBeanClassAttribute> getAttributes() 
	{
		if ( _attributes != null )
		{
			return _attributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	/**
	 * Returns all the link defined for this class
	 * @return
	 */
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
	
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1  ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + ")" );
		checkCriterion(c1);
		return getAttributesByAddedCriteria(c1);
	}
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		return getAttributesByAddedCriteria(c1 + c2);
	}
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2, int c3 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + "," + c3 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		checkCriterion(c3);
		return getAttributesByAddedCriteria(c1 + c2 + c3);
	}
	public List<JavaBeanClassAttribute> getAttributesByCriteria( int c1, int c2, int c3, int c4 ) 
	{
		ContextLogger.log("getAttributesByCriteria(" + c1 + "," + c2 + "," + c3 + "," + c4 + ")" );
		checkCriterion(c1);
		checkCriterion(c2);
		checkCriterion(c3);
		checkCriterion(c4);
		return getAttributesByAddedCriteria(c1 + c2 + c3 + c4);
	}
	
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
	
	/**
	 * Returns the list of "key" attributes ( PRIMARY KEY ELEMENTS )
	 * @return
	 */
	public List<JavaBeanClassAttribute> getKeyAttributes() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	/**
	 * Returns the list of "non key" attributes ( NON PRIMARY KEY ELEMENTS )
	 * @return
	 */
	public List<JavaBeanClassAttribute> getNonKeyAttributes() 
	{
		if ( _nonKeyAttributes != null ) {
			return _nonKeyAttributes ;
		}
		return VOID_ATTRIBUTES_LIST ;
	}

	public String getSqlTable() 
	{
		return _sDatabaseTable ;
	}
	
	public String getDatabaseCatalog() 
	{
		return _sDatabaseCatalog ;
	}
	
	public String getDatabaseSchema() 
	{
		return _sDatabaseSchema ;
	}
	
	/**
	 * Returns a string containing all the colums of the Primary Key separated by a comma <br>
	 * Example : '"code", "type"' 
	 * @return
	 */
	public String getSqlKeyColumns() 
	{
		if ( _sSqlKeyColumns == null ) // list not yet built
		{
			_sSqlKeyColumns = buildDbColumnsList( true ); 
		}
		return _sSqlKeyColumns ;
	}
	
	/**
	 * Returns a string containing all the colums that are not in the Primary Key separated by a comma <br>
	 * Example : '"first_name", "age", "email"' 
	 * @return
	 */
	public String getSqlNonKeyColumns() 
	{
		if ( _sSqlNonKeyColumns == null ) // list not yet built
		{
			_sSqlNonKeyColumns = buildDbColumnsList( false ); 
		}
		return _sSqlNonKeyColumns ;
	}

    /**
     * Returns the Java line instruction for the toString() method
     * @return
     */
    public String getToStringInstruction()
    {
    	if ( _attributes != null )
    	{
    		int n = _attributes.size();
    		if ( n > 1 )
    		{
                StringBuffer sb = new StringBuffer();
            	for ( int i = 0 ; i < n ; i++ )        		
            	{
            		JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(i);
                    if ( i > 0 ) // if it's not the first one
                    {
                        sb.append( " + \"|\" + " ) ;
                    }        		
                    sb.append( attribute.getName() ) ;
            	}
            	return sb.toString(); // example : 'aaa + "|" + bbb + "|" + ccc'
    		}
    		else 
    		{
    			// Single attribute => no automatic conversion to String
    			JavaBeanClassAttribute attribute = (JavaBeanClassAttribute) _attributes.get(0);
    			String sFullType = attribute.getFullType();
    			if ( sFullType != null ) {
    				if ( sFullType.startsWith("java.") ) {
    					//--- Java object
    					if ( sFullType.equals("java.lang.String") ) {
        					return attribute.getName() ;
    					}
    					else {
        					return attribute.getName() + ".toString()";
    					}
    				}
    				else {
    					//--- Primitive type
    					return "\"\" + " + attribute.getName() ;
    				}
    			}
    		}
    	}
    	return "\"Class " + getName() + " (no attributes) \"" ;
    }

    private boolean typeUsedInToString( String sType )
    {
    	if ( null == sType ) return false ;
    	String s = sType.trim() ;
    	if ( s.endsWith("]") ) return false ; // Array
    	if ( s.endsWith("Blob") ) return false ; 
    	if ( s.endsWith("Clob") ) return false ; 
    	return true ;
    }
    public String toStringMethodCodeLines( int iLeftMargin )
    {
    	return toStringMethodCodeLinesWithKey( iLeftMargin, null );
    }
    
    public String toStringMethodCodeLinesWithKey( int iLeftMargin, String embeddedIdName )
    {
    	return toStringMethodCodeLinesWithKey( iLeftMargin, _nonKeyAttributes, embeddedIdName );
    }
    
    public String toStringMethodCodeLinesWithKey( int iLeftMargin, List<JavaBeanClassAttribute> specificNonKeyAttributes, String embeddedIdName )
    {
    	String leftMargin = GeneratorUtil.blanks(iLeftMargin);
    	
    	if ( _attributes != null )
    	{
    		int n = _attributes.size();
    		if ( n > 0 )
    		{
    			int count = 0 ;
                StringBuffer sb = new StringBuffer();
    			sb.append(leftMargin);
    			sb.append("StringBuffer sb = new StringBuffer(); \n");
    			
    			//--- PRIMARY KEY attributes ( composite key or not )
    			if ( _keyAttributes != null ) {
					if ( hasCompositePrimaryKey() && ( embeddedIdName != null ) ) {
						// Embedded id 
						count = count + toStringForEmbeddedId( leftMargin, sb, embeddedIdName );
					}
					else {
						// No embedded id 
						count = count + toStringForAttributes( leftMargin, sb, _keyAttributes );
					}
        		}

    			if ( count > 0 ) {
    				sb.append(leftMargin);
    				sb.append("sb.append(\"|\"); \n");
    			}
    			
    			//--- NON KEY attributes ( composite key or not )
    			if ( specificNonKeyAttributes != null ) {
    				count = count + toStringForAttributes( leftMargin, sb, specificNonKeyAttributes );
    			}
    			
    			sb.append(leftMargin);
    			sb.append("return sb.toString();"); // Last line => No EOL
    			if ( count > 0 ) {
                    return sb.toString() ;
    			}
    			else {
    				return leftMargin + "return \"Instance of " + getName() + " (no usable attribute)\" ;" ;
    			}
    		}
    		else {
				return leftMargin + "return \"Instance of " + getName() + " (attributes.size = 0)\" ;" ;    			
    		}
    	}
    	else {
        	return leftMargin + "return \"Instance of " + getName() + " (attributes is null)\" ;" ;
    	}
    }
    
    private int toStringForAttributes( String leftMargin, StringBuffer sb, List<JavaBeanClassAttribute> attributes )
    {
    	int count = 0 ;
    	for ( JavaBeanClassAttribute attribute : attributes ) {
    		if ( typeUsedInToString( attribute.getType() ) )
    		{
                if ( count > 0 ) // if it's not the first one
                {
        			sb.append(leftMargin); sb.append("sb.append( \"|\" ); \n");
                }        		
    			sb.append(leftMargin); sb.append("sb.append(" + attribute.getName() + "); \n" );
    			count++ ;
    		}
    	}
    	return count ;
    }
    private int toStringForEmbeddedId( String leftMargin, StringBuffer sb, String embeddedIdName )
    {
		sb.append(leftMargin); sb.append("if ( " + embeddedIdName + " != null ) {  \n");
		sb.append(leftMargin); sb.append("    sb.append(" + embeddedIdName + ".toString());  \n");
		sb.append(leftMargin); sb.append("}  \n");
		sb.append(leftMargin); sb.append("else {  \n");
		sb.append(leftMargin); sb.append("    sb.append( \"(null-key)\" );  \n");
		sb.append(leftMargin); sb.append("}  \n");
		return 1 ;
    }

	/**
	 * Returns an array containing all the "NON long text" attributes of this class (standard attributes)<br>
	 * ( returns a void array if no attributes )
	 * @return
	 */
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

	/**
	 * Returns an array containing all the "long text" attributes of this class<br>
	 * ( returns a void array if no "long text" attributes )
	 * @return
	 */
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

	/**
	 * Returns true if this class has at least one "long text" attribute
	 * @return
	 */
	public boolean getHasTextAttribute() 
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
	
	public boolean hasCompositePrimaryKey() 
	{
		if ( _keyAttributes != null ) {
			return _keyAttributes.size() > 1 ;
		}
		return false ; // No key attributes
	}
	
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
	
	/**
	 * Init the imports list
	 * @param imports
	 */
    private void setImports(JavaBeanClassImports imports) 
	{
		if ( imports != null )
		{
			// Reset ALL => create a new list
			_imports = new LinkedList<String>();
			_imports.addAll( imports.getList() );
		}
	}
    

	/**
	 * Init the Jpa imports list
	 * @param imports
	 */
    private void addImportsJpa(JavaBeanClassImports imports) 
	{
		if ( imports != null )
		{
			// Reset ALL => create a new list
			if (_importsJpa == null) {
				_importsJpa = new LinkedHashSet<String>();
			}
			
			_importsJpa.addAll( imports.getList() );
		}
	}
    
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

	/**
	 * This method close the definition of the class (when all the attributes have been added) <br>
	 * 
	 * It build the "KEY" and "NON KEY" attributes 
	 * 
	 * It determines if there is import types collision ( eg "java.util.Date" with "java.sql.Date" ) <br>
	 * and managed the imports list and attributes declarations types to avoid imports error
	 *  
	 */
	private void endOfDefinition() 
	{
		if ( _attributes == null ) return ;
		
		//--- Build the list of the "KEY" attributes
		_keyAttributes = buildAttributesList ( true );
		
		//--- Build the list of the "NON KEY" attributes
		_nonKeyAttributes = buildAttributesList ( false ); 
		
		//--- Define the imports required for this class 
		JavaBeanClassImports javaImports = new JavaBeanClassImports();
		
		for ( JavaBeanClassAttribute attribute : _attributes ) {
			javaImports.declareType( attribute.getFullType() ); // register the type to import if necessary
		}
		
		//--- Extract potential collided types ( and retrieve the list of collided full types )
		LinkedList<String> collidedTypes = javaImports.extractDuplicatedShortNames();

		//--- Set imports list for the current class
		this.setImports(javaImports);

		//--- If there's collided types => Chech each attribute type 
		if ( collidedTypes != null )
		{
			//--- Some collided types have been extracted from imports 
			for ( JavaBeanClassAttribute attr : _attributes ) {
				String sFullType = attr.getFullType();
				if ( collidedTypes.contains( sFullType ) ) // if this attribute is impacted
				{
					//--- force this attributes to use its "full type" for variable declaration
					attr.forceType ( sFullType );
				}
			}
		}
	}

	/**
	 * This method process the jpa specific imports <br>
	 */
	private void processJpaSpecificImport() {
		JavaBeanClassImports jpaImports = new JavaBeanClassImports();

		// TODO a afiner
		jpaImports.declareType("javax.persistence.*");
		
		this.addImportsJpa(jpaImports);
		
		/*
		jpaImports.declareType("javax.persistence.Entity");
		jpaImports.declareType("javax.persistence.Table");
		jpaImports.declareType("javax.persistence.Id");
		
		jpaImports.declareType("javax.persistence.UniqueConstraint");
		jpaImports.declareType("javax.persistence.EmbeddedId");
		jpaImports.declareType("javax.persistence.Embeddable");
		jpaImports.declareType("javax.persistence.AttributeOverride");
		jpaImports.declareType("javax.persistence.AttributeOverrides");

		jpaImports.declareType("javax.persistence.OneToOne");
		jpaImports.declareType("javax.persistence.ManyToMany");
		jpaImports.declareType("javax.persistence.ManyToOne");
		jpaImports.declareType("javax.persistence.OneToMany");

		jpaImports.declareType("javax.persistence.GeneratedValue");
		jpaImports.declareType("javax.persistence.GenerationType");
		jpaImports.declareType("javax.persistence.SequenceGenerator");
		jpaImports.declareType("javax.persistence.TableGenerator");
		*/
	}

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
