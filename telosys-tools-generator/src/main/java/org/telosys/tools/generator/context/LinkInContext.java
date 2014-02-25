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

import org.telosys.tools.commons.JavaClassUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generator.EntitiesManager;
import org.telosys.tools.generator.GeneratorContextException;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.repository.model.JoinColumn;
import org.telosys.tools.repository.model.JoinColumns;
import org.telosys.tools.repository.model.JoinTable;
import org.telosys.tools.repository.model.Link;

/**
 * Link exposed in the Velocity Context 
 *  
 * @author S.Labbe, L.Guerin
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName = ContextName.LINK ,
		text = {
				"This object provides all information about an entity link",
				"Each link is retrieved from the entity class ",
				""
		},
		since = "",
		example= {
				"",
				"#foreach( $link in $entity.selectedLinks )",
				"    private $link.formattedFieldType(10) $link.formattedFieldName(12);",
				"#end"				
		}
		
 )
//-------------------------------------------------------------------------------------
public class LinkInContext {
	
	private final Link    _link;
	//private final Entity  _targetEntity;
	//private final EntityInContext  _targetEntity;
	private final EntitiesManager  _entitiesManager;

//	private final String  _sGetter;
//	private final String  _sSetter;
	
	//-------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * @param link link in the repository 
	 * @param targetEntity targeted entity in the repository 
	 */
	//public LinkInContext(final Link link, final Entity targetEntity ) 
	//public LinkInContext(final Link link, final EntityInContext targetEntity ) 
	public LinkInContext(final Link link, final EntitiesManager entitiesManager ) 
	{
		this._link = link;
//		this._targetEntity  = targetEntity;
		this._entitiesManager  = entitiesManager;
		
////		_sGetter = Util.buildGetter(this.getJavaName(), this.getLinkType());
////		_sSetter = Util.buildSetter(this.getJavaName());		
//		_sGetter = Util.buildGetter(this.getFieldName(), this.getFieldType());
//		_sSetter = Util.buildSetter(this.getFieldName());
		
	}

	//-------------------------------------------------------------------------------------
	protected Link getLink() {
		return this._link ;
	}
	//-------------------------------------------------------------------------------------
//	protected Entity getTargetEntity() {
//		return this._targetEntity ;
//	}
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the link's type with n trailing blanks ",
			"eg : List, List<Person>, Person, ..."
			},
		parameters = { 
			"n : the number of blanks to be added at the end of the name" 
			}
	)
	public String formattedFieldType(int iSize) throws GeneratorException
    {
		String currentType = getFieldType();
		String sTrailingBlanks = "";
        int iDelta = iSize - currentType.length();
        if (iDelta > 0) // if needs trailing blanks
        {
            sTrailingBlanks = GeneratorUtil.blanks(iDelta);
        }
        return currentType + sTrailingBlanks;
    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the link's name with n trailing blanks "
			},
		parameters = { 
			"n : the number of blanks to be added at the end of the name" 
			}
	)
	public String formattedFieldName(int iSize)
    {
        //String s = this.getJavaName() ;
        String s = this.getFieldName();
        String sTrailingBlanks = "";
        int iDelta = iSize - s.length();
        if (iDelta > 0) // if needs trailing blanks
        {
            sTrailingBlanks = GeneratorUtil.blanks(iDelta);
        }
        return s + sTrailingBlanks;
    }

//	/**
//	 * Builds a string with the cascade attribute <br>
//	 * ie : "", "cascade = CascadeType.ALL", "cascade = CascadeType.PERSIST", "cascade = { CascadeType.PERSIST, CascadeType.REMOVE }"
//	 * @param link
//	 * @return
//	 */
//	private String buildCascade(Link link)
//	{
//		// JPA doc : By default no operations are cascaded
//		if ( link.isCascadeALL() ) { 
//			return "cascade = CascadeType.ALL" ; 
//		}
//		else {
//			int n = 0 ;
//			if ( link.isCascadeMERGE() ) n++ ;
//			if ( link.isCascadePERSIST() ) n++ ;
//			if ( link.isCascadeREFRESH() ) n++ ;
//			if ( link.isCascadeREMOVE() ) n++ ;
//			if ( n == 0 ) {
//				return "" ;
//			}
//			else {
//				StringBuilder sb = new StringBuilder();
//				sb.append("cascade = ");
//				if ( n > 1 ) {
//					sb.append("{ ");
//				}
//				int c = 0 ;
//				if ( link.isCascadeMERGE()  ) { 
//					if ( c > 0 ) { sb.append(", "); } 
//					sb.append("CascadeType.MERGE"  ); 
//					c++; 
//				}
//				if ( link.isCascadePERSIST()) { 
//					if ( c > 0 ) { sb.append(", "); } 
//					sb.append("CascadeType.PERSIST"); 
//					c++; 
//				}
//				if ( link.isCascadeREFRESH()) { 
//					if ( c > 0 ) { sb.append(", "); } 
//					sb.append("CascadeType.REFRESH"); 
//					c++; 
//				}
//				if ( link.isCascadeREMOVE() ) { 
//					if ( c > 0 ) { sb.append(", "); } 
//					sb.append("CascadeType.REMOVE" ); 
//					c++; 
//				}
//				if ( n > 1 ) {
//					sb.append(" }");
//				}
//				return sb.toString();
//			}
//		}
//	}

//	private String buildFetch(Link link)
//	{
//		// JPA doc : default = EAGER
//		if ( link.isFetchEAGER() ) { 
//			return "fetch = FetchType.EAGER" ; 
//		}
//		if ( link.isFetchLAZY()  ) { 
//			return "fetch = FetchType.LAZY" ;
//		}
//		return "";
//	}
	
//	private String buildOptional(Link link)
//	{
//		// JPA doc : default = true
//		if ( link.isOptionalTrue() ) { 
//			return "optional = true" ; 
//		}
//		if ( link.isOptionalFalse() ) { 
//			return "optional = false" ; 
//		}
//		return "";
//	}
	
//	/**
//	 * Return the further information for the cardinality annotation ( cascade, fetch, optional ) <br>
//	 * ie : "cascade = CascadeType.ALL, fetch = FetchType.EAGER"
//	 * @param link
//	 * @return
//	 */
//	private String getCardinalityFurtherInformation(Link link)
//	{
//		/*
//		 * JPA documentation
//		 * OneToOne   : cascade + fecth + optional
//		 * ManyToOne  : cascade + fecth + optional
//		 * OneToMany  : cascade + fecth 
//		 * ManyToMany : cascade + fecth 
//		 */
//		int n = 0 ;
//		StringBuilder sb = new StringBuilder();
//
//		//--- CASCADE 
//		String sCascade = buildCascade(link); // "cascade = ..." 
//		if ( ! StrUtil.nullOrVoid( sCascade ) ) {
//			if ( n > 0 ) sb.append(", ");
//			sb.append(sCascade);
//			n++ ;
//		}
//
//		//--- FETCH 
//		String sFetch = buildFetch(link); // "fetch = ..." 
//		if ( ! StrUtil.nullOrVoid( sFetch ) ) {
//			if ( n > 0 ) sb.append(", ");
//			sb.append(sFetch);
//			n++ ;
//		}
//		
//		//--- OPTIONAL ( only for OneToOne and ManyToOne )
//		if ( link.isTypeOneToOne() || link.isTypeManyToOne() ) {
//			String sOptional = buildOptional(link); // "optional=true|false" 
//			if ( ! StrUtil.nullOrVoid( sOptional ) ) {
//				if ( n > 0 ) sb.append(", ");
//				sb.append(sOptional);
//				n++ ;
//			}
//		}
//		
//		return sb.toString();
//	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the Java getter for the link e.g. 'getPerson' for link 'person' "
			}
	)
	public String getGetter() throws GeneratorException  {
		//return _sGetter;
		return Util.buildGetter(this.getFieldName(), this.getFieldType());
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the Java setter for the link e.g. 'setPerson' for link 'person' "
			}
	)
	public String getSetter() {
		//return _sSetter;
		return Util.buildSetter(this.getFieldName());
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has a 'join table'"
			}
	)
	public boolean hasJoinTable() {
		return _link.getJoinTable() != null ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'join table' for the link ",
			"Typically for JPA '@JoinTable'"
			}
	)
	public String getJoinTable() {
		JoinTable joinTable = _link.getJoinTable();
		if ( joinTable != null ) {
			return joinTable.getName();
		}
		else {
			throw new GeneratorContextException("No 'Join Table' for this link");
		}
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has 'join columns' (at least one)"
			}
	)
	public boolean hasJoinColumns() {
		//return _link.getJoinColumns() != null ;
		JoinColumns joinColumns = _link.getJoinColumns() ;
		if ( joinColumns != null ) {
			return ( joinColumns.size() > 0 ) ; 
		}
		return false ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'join columns' for the link "
			}
	)
	public String[] getJoinColumns() {
		JoinColumns joinColumns = _link.getJoinColumns() ;
		if ( joinColumns != null ) {
			JoinColumn[] columns = joinColumns.getAll();
			String[] colNames = new String[columns.length] ;
			for ( int i = 0 ; i < columns.length ; i++ ) {
				JoinColumn col = columns[i] ;
				if ( col != null ) {
					colNames[i] = columns[i].getName();
				}
				else {
					throw new GeneratorContextException("Invalid link : null 'Join Column' in 'Join Columns' collection");
				}
			}
			return colNames ;
		}
		else {
			throw new GeneratorContextException("No 'Join Columns' for this link");
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the unique id of the link in the repository (id used by the tool)",
			"(not supposed to be used in a generated file)"
			}
	)
	public String getId() {
		return _link.getId();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link is selected (ckeckbox ckecked in the GUI)"
			}
	)
	public boolean isSelected() {
		return _link.isUsed();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the target table (table referenced by the link)"
			}
	)
	public String getTargetTableName() {
		return _link.getTargetTableName();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the field name for the link (attribute name in the entity class)"
			}
	)
	public String getFieldName() {
		return _link.getJavaFieldName();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the field 'full type' for the link ( eg 'java.util.List' ) "
			}
	)
	public String getFieldFullType() {
		return _link.getJavaFieldType();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the field 'simple type' for the link ( eg 'List' ) "
			}
	)
	public String getFieldSimpleType() {
		return JavaClassUtil.shortName(_link.getJavaFieldType());
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the type of the link ",
			"eg : List, List<Person>, Person, ..."
			}
	)
	public String getFieldType() throws GeneratorException {
		String type = "";
		String targetEntityClassName = this.getTargetEntitySimpleType() ; // v 2.1.0
		String simpleType = this.getFieldSimpleType();
		
		if ( StrUtil.nullOrVoid(simpleType) ) {
			type = targetEntityClassName ; // this._targetEntity.getBeanJavaClass();
		} else {
			// S'il s'agit de collection, on ajout la description du generic
			if (this.isCardinalityOneToMany()) {
				//currentType = this.getJavaTypeShort() + "<" + this._targetEntity.getBeanJavaClass() + ">";
				type = simpleType + "<" + targetEntityClassName + ">";
			} else if (this.isCardinalityManyToMany()) {
				//currentType = this.getJavaTypeShort() + "<" + this._targetEntity.getBeanJavaClass() + ">";
				type = simpleType + "<" + targetEntityClassName + ">";
			} else {
				type = simpleType ;
			}
		}
		return type;
	}	
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link is the 'Owning Side' of the relationship between 2 entities"
			}
	)
	public boolean isOwningSide() {
		return _link.isOwningSide();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the link in the 'owning side' ",
			"Typically for JPA 'mappedBy'"
			}
	)
	public String getMappedBy() {
		return _link.getMappedBy();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the entity referenced by the link ",
			"eg : 'Book', 'Customer', ...",
			""
			},
		since = "2.1.0"
	)
	public EntityInContext getTargetEntity() throws GeneratorException {
		return _entitiesManager.getEntity( getTargetTableName() );
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the type of the entity referenced by the link ",
			"eg : 'Book', 'Customer', ...",
			""
			}
	)
	public String getTargetEntitySimpleType() throws GeneratorException {
//		//return _link.getTargetEntityJavaType();
//		//return _targetEntity.getName(); // v 2.1.0
//		EntityInContext entity = _entitiesBuilder.getEntity( getTargetTableName() );
//		return entity.getName();
		return this.getTargetEntity().getName();
	}
	//-------------------------------------------------------------------------------------
//	protected String getTargetEntityClassName() {
//		// TODO : $env Prefix & Suffix
//		//return _targetEntity.getBeanJavaClass() ;
//		return _targetEntity.getName();
//	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the type of the entity referenced by the link ",
			"eg : 'my.package.Book', 'my.package.Customer', ...",
			""
			}
	)
	public String getTargetEntityFullType() throws GeneratorException {
////		return _targetEntity.getFullName(); // v 2.1.0
//		EntityInContext entity = _entitiesBuilder.getEntity( getTargetTableName() );
		return this.getTargetEntity().getFullName();	
	}


	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the cardinality of the link ",
			"eg : 'OneToMany', 'ManyToOne', 'OneToOne', 'ManyToMany'"
			}
	)
	//public String getType() { 
	public String getCardinality() { // v 2.0.5
		return _link.getCardinality();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has a 'OneToOne' cardinality"
			}
	)
	public boolean isCardinalityOneToOne() {
		return _link.isTypeOneToOne();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has a 'OneToMany' cardinality"
			}
	)
	public boolean isCardinalityOneToMany() {
		return _link.isTypeOneToMany();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has a 'ManyToOne' cardinality"
			}
	)
	public boolean isCardinalityManyToOne() {
		return _link.isTypeManyToOne();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns TRUE if the link has a 'ManyToMany' cardinality"
			}
	)
	public boolean isCardinalityManyToMany() {
		return _link.isTypeManyToMany();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'cascade type' ( 'ALL', 'MERGE', 'PERSIST', 'REFRESH', 'REMOVE' )"
			}
	)
	public String getCascade() {
		return _link.getCascade();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'cascade type' is 'ALL' "
			}
	)
	public boolean isCascadeALL() {
		return _link.isCascadeALL();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'cascade type' is 'MERGE' "
			}
	)
	public boolean isCascadeMERGE() {
		return _link.isCascadeMERGE();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'cascade type' is 'PERSIST' "
			}
	)
	public boolean isCascadePERSIST() {
		return _link.isCascadePERSIST();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'cascade type' is 'REFRESH' "
			}
	)
	public boolean isCascadeREFRESH() {
		return _link.isCascadeREFRESH();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'cascade type' is 'REMOVE' "
			}
	)
	public boolean isCascadeREMOVE() {
		return _link.isCascadeREMOVE();
	}

	//-------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'fetch type' ( 'DEFAULT' or 'EAGER' or 'LAZY' )"
			}
	)
	public String getFetch() {
		return _link.getFetch();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'fetch type' is 'DEFAULT' "
			}
	)
	public boolean isFetchDEFAULT() {
		return _link.isFetchDEFAULT();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'fetch type' is 'EAGER' "
			}
	)
	public boolean isFetchEAGER() {
		return _link.isFetchEAGER();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'fetch type' is 'LAZY' "
			}
	)
	public boolean isFetchLAZY() {
		return _link.isFetchLAZY();
	}

	//-------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'optional status' for the link  ( 'TRUE', 'FALSE' or 'UNDEFINED' ) ",
			"Typically for JPA 'optional=true/false'"
			}
	)
	public String getOptional() {
		return _link.getOptional();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'optional status' is 'UNDEFINED' "
			}
	)
	public boolean isOptionalUndefined() {
		return _link.isOptionalUndefined();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'optional status' is 'FALSE' "
			}
	)
	public boolean isOptionalFalse() {
		return _link.isOptionalFalse();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns true if the 'optional status' is 'TRUE' "
			}
	)
	public boolean isOptionalTrue() {
		return _link.isOptionalTrue();
	}

}
