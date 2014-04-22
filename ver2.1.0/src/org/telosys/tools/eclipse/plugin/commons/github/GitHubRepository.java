package org.telosys.tools.eclipse.plugin.commons.github;

/**
 * This class holds few information about GitHub repository
 * 
 * @author L. Guerin
 *
 */
public class GitHubRepository {

	private final long   id ;
	
	private final String name ;
	
	private final String description ;

	private final long   size ;
	
	
	public GitHubRepository(long id, String name, String description, long size) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.size = size ;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public long getSize() {
		return size;
	}

}
