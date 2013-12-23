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
import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.GeneratorContextException;
import org.telosys.tools.generator.GeneratorUtil;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.JoinColumn;
import org.telosys.tools.repository.model.JoinColumns;
import org.telosys.tools.repository.model.JoinTable;
import org.telosys.tools.repository.model.Link;

/**
 * @author S.Labbe, L.Guerin
 *
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
				"    private $link.formatedType(10) $link.formatedName(12);",
				"#end"				
		}
		
 )
//-------------------------------------------------------------------------------------
public class JavaBeanClassLink {
	
//	private final static int ONE_TO_ONE   = 1 ;
//	private final static int MANY_TO_ONE  = 2 ;
//	private final static int ONE_TO_MANY  = 3 ;
//	private final static int MANY_TO_MANY = 4 ;
	
	private final Link    _link;
	private final Entity  _targetEntity;

	private final String  _sGetter;
	private final String  _sSetter;
	
	
	public JavaBeanClassLink(final Link link, final Entity currentEntity, final Entity targetEntity ) 
	{
		this._link = link;
		this._targetEntity  = targetEntity;
		
		_sGetter = Util.buildGetter(this.getJavaName(), this.getLinkType());
		_sSetter = Util.buildSetter(this.getJavaName());
	}

	//-------------------------------------------------------------------------------------
	protected Link getLink() {
		return this._link ;
	}
	//-------------------------------------------------------------------------------------
	protected Entity getTargetEntity() {
		return this._targetEntity ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the link's type with n trailing blanks "
			},
		parameters = { 
			"n : the number of blanks to be added at the end of the name" 
			}
	)
	public String formatedType(int iSize)
    {
		String currentType = getLinkType();
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
			"Returns the type of the link ",
			"eg : List, List<Person>, Person, ..."
			}
	)
	public String getLinkType() {
		String currentType = "";
		if (StrUtil.nullOrVoid(this.getJavaTypeShort())) {
			currentType = this._targetEntity.getBeanJavaClass();
		} else {
			// S'il s'agit de collection, on ajout la description du generic
			if (this.isCardinalityOneToMany()) {
				currentType = this.getJavaTypeShort() + "<" + this._targetEntity.getBeanJavaClass() + ">";
			} else if (this.isCardinalityManyToMany()) {
				currentType = this.getJavaTypeShort() + "<" + this._targetEntity.getBeanJavaClass() + ">";
			} else {
				currentType = this.getJavaTypeShort();
			}
		}
		return currentType;
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
	public String formatedName(int iSize)
    {
        String s = this.getJavaName() ;
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
	
//	/**
//	 * Returns the JPA annotations for the link attribute 
//	 * @param marginSize
//	 * @return
//	 */
//	@VelocityMethod(
//	text={	
//		"Returns all the JPA annotations for the link (with a left margin)"
//		},
//	parameters = "leftMargin : the left margin (number of blanks) "
//	)
//	public String jpaAnnotations(int marginSize)
//    {
//		AnnotationsBuilder annotations = new AnnotationsBuilder(marginSize);
//		
//		if ( _link.isOwningSide() ) 
//		{
//			if (_link.isTypeOneToOne()) 
//			{
//				// Examples :
//				//   @OneToOne 
//			    //   @JoinColumn(name="BADGE_NUMBER", referencedColumnName="BADGE_NUMBER")
//
//				annotations.addLine(getOwningSideCardinalityAnnotation( "OneToOne", null ) ); 
//				processJoinColumns(annotations, _link.getJoinColumns(), ONE_TO_ONE );
//			} 
//			else if (_link.isTypeManyToOne()) 
//			{
//				annotations.addLine(getOwningSideCardinalityAnnotation( "ManyToOne", null ) ); 
//				processJoinColumns(annotations, _link.getJoinColumns(), MANY_TO_ONE );
//			} 
//			else if (_link.isTypeManyToMany()) 
//			{
//				annotations.addLine(getOwningSideCardinalityAnnotation( "ManyToMany", _targetEntity.getBeanJavaClass() ) ); 
//				processJoinTable(annotations, _link.getJoinTable(), MANY_TO_MANY) ;
//			}
//			else if (_link.isTypeOneToMany()) 
//			{
//				//--- Possible for unidirectional "OneToMany" relationship ( whithout inverse side )
//				annotations.addLine(getOwningSideCardinalityAnnotation( "OneToMany", _targetEntity.getBeanJavaClass() ) ); 
//				processJoinTable(annotations, _link.getJoinTable(), ONE_TO_MANY) ;				
//			} 
//			else 
//			{
//				// Error 
//			}
//		} 
//		else 
//		{
//			//--- INVERSE SIDE
//			if (this.isCardinalityOneToOne()) 
//			{
//				annotations.addLine(getInverseSideCardinalityAnnotation( "OneToOne" ) ); 
//			} 
//			else if (this.isCardinalityOneToMany()) 
//			{
//				annotations.addLine(getInverseSideCardinalityAnnotation( "OneToMany" ) ); 
//			} 
//			else if (this.isCardinalityManyToMany()) 
//			{
//				annotations.addLine(getInverseSideCardinalityAnnotation( "ManyToMany" ) ); 
//			} 
//			else if (this.isCardinalityManyToOne()) 
//			{
//				// Not supposed to occur for an INVERSE SIDE !
//				annotations.addLine(getInverseSideCardinalityAnnotation( "ManyToOne" ) ); 
//			} 
//			else 
//			{
//				// Error 
//			}
//		}
//		
//		return annotations.getAnnotations();
//    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the Java getter for the link e.g. 'getPerson' for link 'person' "
			}
	)
	public String getGetter() {
		return _sGetter;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the Java setter for the link e.g. 'setPerson' for link 'person' "
			}
	)
	public String getSetter() {
		return _sSetter;
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
			"Returns the unique id of the link in the repository (id used by the tool)"
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
			"Returns the Java field name for the link"
			}
	)
	public String getJavaName() {
		return _link.getJavaFieldName();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the full Java type for the link ( eg 'java.util.List' ) "
			}
	)
	public String getJavaTypeFull() {
		return _link.getJavaFieldType();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the short Java type for the link ( eg 'List' ) "
			}
	)
	public String getJavaTypeShort() {
		return JavaClassUtil.shortName(_link.getJavaFieldType());
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
			"Returns the type of the entity referenced by the link ",
			""
			}
	)
	public String getTargetEntityType() {
		return _link.getTargetEntityJavaType();
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

//	//----------------------------------------------------------------
//	/**
//	 * Build an return the cardinality annotation for an "OWNING SIDE"
//	 * Example : "@ManyToOne ( cascade = CascadeType.ALL, fetch = FetchType.EAGER ) "
//	 * @param cardinality
//	 * @param targetEntity the target entity ( or null if none ) 
//	 * @return
//	 */
//	private String getOwningSideCardinalityAnnotation( String cardinality, String targetEntity ) 
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append( "@" + cardinality ) ;
//		if ( targetEntity != null ) {
//			sb.append( "(" );
//			//--- Common further information : cascade, fetch and optional
//			// ie "cascade = CascadeType.ALL, fetch = FetchType.EAGER"
//			String sCardinalityFurtherInformation = getCardinalityFurtherInformation(_link);
//			if ( ! StrUtil.nullOrVoid(sCardinalityFurtherInformation)) {
//				sb.append( sCardinalityFurtherInformation );
//				sb.append( ", " );
//			}
//			//--- targetEntity ( for ManyToMany and OneToMany )
//			sb.append( "targetEntity=" + targetEntity + ".class" ) ;			
//			sb.append( ")" );
//		}
//		else {
//			//--- Common further information : cascade, fetch and optional
//			// ie "cascade = CascadeType.ALL, fetch = FetchType.EAGER"
//			String sCardinalityFurtherInformation = getCardinalityFurtherInformation(_link);
//			if ( ! StrUtil.nullOrVoid(sCardinalityFurtherInformation)) {
//				sb.append( "(" );
//				sb.append( sCardinalityFurtherInformation );
//				sb.append( ")" );
//			}
//		}
//		return sb.toString();
//	}

//	/**
//	 * Build an return the cardinality annotation for an "INVERSE SIDE"
//	 * Example : "@OneToMany ( mappedBy="fieldName", targetEntity=TheClass.class ) "
//	 * @param cardinality
//	 * @return
//	 */
//	private String getInverseSideCardinalityAnnotation( String cardinality ) 
//	{
//		StringBuilder annotation = new StringBuilder();
//		annotation.append( "@" + cardinality ) ;
//		annotation.append( "(" );
//		//--- Common further information : cascade, fetch and optional
//		// ie "cascade = CascadeType.ALL, fetch = FetchType.EAGER"
//		String sCardinalityFurtherInformation = getCardinalityFurtherInformation(_link);
//		if ( ! StrUtil.nullOrVoid(sCardinalityFurtherInformation)) {
//			annotation.append( sCardinalityFurtherInformation );
//			annotation.append( ", " ); 
//		}
//		//--- mappedBy - NB : no "mappedBy" for ManyToOne (see JPA javadoc) ( cannot be an inverse side )
//		if ( ! _link.isTypeManyToOne() ) { 
//			annotation.append( "mappedBy=\"" + getMappedBy() + "\"" );
//			annotation.append( ", " ); 
//		}
//		//--- targetEntity ( always usable, even with ManyToOne )
//		annotation.append( "targetEntity=" + _targetEntity.getBeanJavaClass() + ".class" ); // No quote for "targetEntity"
//		//---
//		annotation.append( ")" );
//		return annotation.toString();
//	}
	
//	/**
//	 * Build and return a single "@JoinColumn" annotation 
//	 * @param joinColumn
//	 * @param linkCardinality
//	 * @return
//	 */
//	private String getJoinColumnAnnotation(JoinColumn joinColumn, int linkCardinality ) 
//	{
//		StringBuilder annotation = new StringBuilder();
//		annotation.append( "@JoinColumn(");
//		annotation.append( "name=\"" + joinColumn.getName()+"\"" );
//		annotation.append( ", " );
//		annotation.append( "referencedColumnName=\"" + joinColumn.getReferencedColumnName()+"\"" );
//		// TODO 
//		// columnDefinition
//		// nullable
//		// table
//		// unique
//		if ( linkCardinality == MANY_TO_ONE ) {
//			// Add insertable=false and updatable=false to avoid classical error 
//			// should be mapped with insert="false" update="false"
//			annotation.append( ", " );
//			annotation.append( "insertable=false" ); 
//			annotation.append( ", " );
//			annotation.append( "updatable=false" ); 
//		}
//		annotation.append( ")");
//		return annotation.toString();
//	}
	
//	/**
//	 * Generates a "@JoinColumn" (single column) or "@JoinColumns" (multiple columns) annotation
//	 * @param annotations
//	 * @param joinColumns
//	 * @param linkCardinality
//	 */
//	private void processJoinColumns(AnnotationsBuilder annotations, JoinColumns joinColumns, int linkCardinality ) 
//	{
//		if ( joinColumns != null ) 
//		{
//			String[] jc = getJoinColumnAnnotations( joinColumns.getAll(), linkCardinality );
//			if ( jc != null ) {
//				if ( jc.length == 1 ) 
//				{
//					// Single Join Column
//					// Example :
//					//   @JoinColumn(name="MGR_COUNTRY", referencedColumnName="COUNTRY") 
//					
//					annotations.addLine( jc[0] );
//				}
//				else 
//				{
//					// Multiple Join Columns
//					// Example :
//					// @JoinColumns( {
//					//   @JoinColumn(name="MGR_COUNTRY", referencedColumnName="COUNTRY") ,
//					//   @JoinColumn(name="MGR_ID", referencedColumnName="EMP_ID") } )
//					
//					annotations.addLine("@JoinColumns( { " );
//					for ( int i = 0 ; i < jc.length ; i++ ) {
//						String end = ( i < jc.length - 1) ? "," : " } )" ;
//						annotations.addLine("    " + jc[i] + end );
//					}
//				}
//			}
//		}
//	}
	
//	/**
//	 * Generates the join table annotation : "@JoinTable"
//	 * @param annotations
//	 * @param joinTable
//	 * @param linkCardinality
//	 */
//	private void processJoinTable(AnnotationsBuilder annotations, JoinTable joinTable, int linkCardinality) 	 
//	{
//		annotations.addLine("@JoinTable(name=\"" + joinTable.getName() + "\", " );
//		
//		JoinColumns joinColumns = joinTable.getJoinColumns();
//		if ( joinColumns != null ) 
//		{
//			processJoinTableColumns(annotations, "joinColumns", joinColumns.getAll(), ",", linkCardinality);
//		}
//		
//		InverseJoinColumns inverseJoinColumns = joinTable.getInverseJoinColumns();
//		if ( inverseJoinColumns != null ) 
//		{
//			processJoinTableColumns(annotations, "inverseJoinColumns", inverseJoinColumns.getAll(), "", linkCardinality);
//		}
//		annotations.addLine(" ) \n" );
//		
//	}

//	private void processJoinTableColumns( AnnotationsBuilder annotations, String name, JoinColumn[] joinColumns, String end, int linkCardinality ) 
//	{
//		String[] jc = getJoinColumnAnnotations( joinColumns, linkCardinality );
//		if ( jc != null ) {
//			if ( jc.length == 1 ) 
//			{
//				// Single Join Column
//				// Example :
//				//   joinColumns=@JoinColumn(name="MGR_COUNTRY", referencedColumnName="COUNTRY") 
//				
//				annotations.addLine("  " + name + "=" + jc[0] + end);
//			}
//			else 
//			{
//				// Multiple Join Columns
//				// Example :
//				//   joinColumns={
//				//     @JoinColumn(name="MGR_COUNTRY", referencedColumnName="COUNTRY") ,
//				//     @JoinColumn(name="MGR_ID", referencedColumnName="EMP_ID") }
//				
//				annotations.addLine("  " + name + "={" );
//				for ( int i = 0 ; i < jc.length ; i++ ) {
//					String jcEnd = ( i < jc.length - 1) ? "," : ( "}"+end ) ;
//					annotations.addLine("    " + jc[i] + jcEnd );
//				}
//			}
//		}
//	}
	
//	/**
//	 * Returns an array of string containing the annotations <br>
//	 * Example : <br>
//	 *  0 : "@JoinColumn(name="MGR_COUNTRY", referencedColumnName="COUNTRY")"
//	 *  1 : "@JoinColumn(name="MGR_ID", referencedColumnName="EMP_ID")"
//	 *  
//	 * @param joinColumns
//	 * @param linkCardinality
//	 * @return
//	 */
//	private String[] getJoinColumnAnnotations( JoinColumn[] joinColumns, int linkCardinality ) 
//	{
//		if ( null == joinColumns ) return null ;
//		if ( joinColumns.length == 0 ) return null ;
//		String[] annotations = new String[joinColumns.length];
//		for ( int i = 0 ; i < joinColumns.length ; i++ ) {
//			annotations[i] = getJoinColumnAnnotation(joinColumns[i], linkCardinality);
//		}
//		return annotations;
//	}
	
}
