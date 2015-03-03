package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.util.Collections;
import java.util.LinkedList;

import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.ForeignKey;
import org.telosys.tools.repository.model.JoinTable;
import org.telosys.tools.repository.model.Link;
import org.telosys.tools.repository.model.RelationLinks;
import org.telosys.tools.repository.model.RepositoryModel;

public class LinksManager {

	private RepositoryModel repositoryModel ;
	
	//----------------------------------------------------------------------------------------
	public LinksManager(RepositoryModel repositoryModel) {
		super();
		this.repositoryModel = repositoryModel;
	}

	//----------------------------------------------------------------------------------------
	public Link getLinkById(String id)
	{
		return repositoryModel.getLinkById(id);
	}
	
	//----------------------------------------------------------------------------------------
	public void removeLink(String id)
	{
		repositoryModel.removeLinkById(id);
	}
	//----------------------------------------------------------------------------------------
	public void removeRelation(RelationLinks relation)
	{
		Link link = relation.getInverseSideLink();
		if ( link != null ) {
			repositoryModel.removeLinkById( link.getId() );
		}
		link = relation.getOwningSideLink();
		if ( link != null ) {
			repositoryModel.removeLinkById( link.getId() );
		}		
	}
	//----------------------------------------------------------------------------------------
	public RelationLinks getRelationByLinkId(String id)
	{
		return repositoryModel.getRelationByLinkId(id);
	}
	
	//----------------------------------------------------------------------------------------
	private void sortLinks( LinkedList<Link> linksList )
	{
        Collections.sort(linksList, new LinksComparer( LinksComparer.ASC ) );
	}
	//----------------------------------------------------------------------------------------
	public LinkedList<Link> getAllLinks()
	{
		//log("getAllLinks()");
		LinkedList<Link> linksList = new LinkedList<Link>();
		Entity [] entities = repositoryModel.getEntities();
		for ( int i = 0 ; i < entities.length ; i++ ) {
			Entity entity = entities[i];
			Link [] links = entity.getLinks();
			for ( int j = 0 ; j < links.length ; j++ ) {
				linksList.add(links[j]);
			}
		}
		//log("getAllLinks() : list size = " + linksList.size());
		
		sortLinks( linksList );

		return linksList ;
	}
	
	//----------------------------------------------------------------------------------------
	public int countAllLinks()
	{
		int count = 0 ;
		Entity [] entities = repositoryModel.getEntities();
		for ( int i = 0 ; i < entities.length ; i++ ) {
			Entity entity = entities[i];
			Link [] links = entity.getLinks();
			for ( int j = 0 ; j < links.length ; j++ ) {
				count++;
			}
		}
		return count ;
	}
	
	//----------------------------------------------------------------------------------------
	public LinkedList<Link> getLinks(LinksCriteria criteria)
	{
		LinkedList<Link> linksList = new LinkedList<Link>();
		Entity [] entities = repositoryModel.getEntities();
		for ( int i = 0 ; i < entities.length ; i++ ) {
			Entity entity = entities[i];
			Link [] links = entity.getLinks();
			for ( int j = 0 ; j < links.length ; j++ ) {
				Link link = links[j];
				if ( checkCriteria(link, criteria ) ) {
					linksList.add(link);
				}
			}
		}

		sortLinks( linksList );
		
		return linksList ;
	}
	
	//----------------------------------------------------------------------------------------
	private boolean checkCardinalityCriteria(Link link, LinksCriteria criteria ) 
	{
		if ( criteria.isTypeManyToMany() && link.isTypeManyToMany() ) return true ;
		if ( criteria.isTypeManyToOne()  && link.isTypeManyToOne()  ) return true ;
		if ( criteria.isTypeOneToMany()  && link.isTypeOneToMany()  ) return true ;
		if ( criteria.isTypeOneToOne()   && link.isTypeOneToOne()   ) return true ;
		return false ;
	}
	
	//----------------------------------------------------------------------------------------
	private boolean checkCriteria(Link link, LinksCriteria criteria ) 
	{
		if ( criteria != null ) 
		{
			if ( criteria.isOwningSide() && link.isOwningSide() ) {
				return checkCardinalityCriteria(link, criteria )  ;
			}
			if ( criteria.isInverseSide() && ( ! link.isOwningSide() ) ) {
				return checkCardinalityCriteria(link, criteria )  ;
			}
			return false ;
		}
		return true ; // No criteria 
	}
	
	//----------------------------------------------------------------------------------------
	public ForeignKey getForeignKey(String fkName)
	{
//		Entity [] entities = repositoryModel.getEntities();
//		for ( int i = 0 ; i < entities.length ; i++ ) {
//			Entity entity = entities[i];
//			ForeignKey fk = entity.getForeignKey(fkName);
//			if ( fk != null ) {
//				return fk ; // FOUND 
//			}
//		}
//		return null ;
		return repositoryModel.getForeignKeyByName(fkName);
	}
	//----------------------------------------------------------------------------------------
	public Entity getEntity(String name)
	{
		return repositoryModel.getEntityByName(name);
	}
	//----------------------------------------------------------------------------------------
	public final static int NO_BASIS                = 0 ;
	public final static int EXISTING_FOREIGN_KEY    = 1 ;
	public final static int NONEXISTENT_FOREIGN_KEY = 2 ;
	public final static int EXISTING_JOIN_TABLE     = 3 ;
	public final static int NONEXISTENT_JOIN_TABLE  = 4 ;
	
	//----------------------------------------------------------------------------------------
	public int getLinkBasis(Link link)
	{
		if ( link.isBasedOnForeignKey() ) 
		{
			String fkName = link.getForeignKeyName();
			ForeignKey fk = getForeignKey(fkName);
			if ( fk != null ) {
				return EXISTING_FOREIGN_KEY ;
			}
			else {
				return NONEXISTENT_FOREIGN_KEY ;
			}
		}
		else if ( link.isBasedOnJoinTable() ) {
			JoinTable joinTable = link.getJoinTable();
			if ( joinTable != null ) {
				return EXISTING_JOIN_TABLE ;
			}
			else {
				return NONEXISTENT_JOIN_TABLE ;
			}
		}
		return NO_BASIS ;
	}
	
}
