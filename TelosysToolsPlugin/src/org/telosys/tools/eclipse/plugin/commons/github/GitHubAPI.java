package org.telosys.tools.eclipse.plugin.commons.github;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GitHubAPI {

	private final static String GIT_HUB_HOST_URL = "https://api.github.com" ;
	
	/**
	 * Returns the REST API response in JSON format
	 * @param userName
	 * @return
	 */
	public static String getRepositoriesJSON( String userName ) {

		String urlString = GIT_HUB_HOST_URL + "/users/" + userName + "/repos" ;
 
        URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException ("MalformedURLException", e);
		}
		
		String result = null ;
        try {
			url.openConnection();
			InputStream reader = url.openStream();
	        int BUFFER_SIZE = 128 * 1024 ;
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int totalBytesRead = 0;
	        int bytesRead = 0;

			ByteArrayOutputStream writer = new ByteArrayOutputStream();
	 
	        while ((bytesRead = reader.read(buffer)) > 0)
	        {  
	           writer.write(buffer, 0, bytesRead);
	           buffer = new byte[BUFFER_SIZE];
	           totalBytesRead += bytesRead;
	        }

	        writer.close();
	        reader.close();
	        result = writer.toString() ;
	        
		} catch (IOException e) {
			throw new RuntimeException ("IOException", e);
		}
		return result ;
	}

	/**
	 * Return the repositories for the given GitHub user name 
	 * @param userName
	 * @return
	 */
	public static List<GitHubRepository> getRepositories( String userName ) {

		List<GitHubRepository> repositories = new LinkedList<GitHubRepository>();
		String json = getRepositoriesJSON( userName );
		JSONParser parser = new JSONParser();
		try {
			Object oList = parser.parse(json);
			if ( oList instanceof JSONArray ) {
				JSONArray repositoriesArray = (JSONArray) oList ;
				for ( Object repositoryObject: repositoriesArray ) {
					JSONObject repo = (JSONObject) repositoryObject ; 
					long   id   = getLongAttribute(repo, "id");
					String name = getStringAttribute(repo, "name");
					String description = getStringAttribute(repo, "description");
					long   size = getLongAttribute(repo, "size");
					// Add the repository in the list
					repositories.add( new GitHubRepository(id, name, description, size ) );
				}
			}
			else {
				throw new RuntimeException ( "JSON error : array expected as root");
			}
		} catch (ParseException e) {
			throw new RuntimeException ( "JSON error : cannot parse the JSON response.");
		}
		sortByName(repositories);
		return repositories ;
	}

	public static void sortByName( List<GitHubRepository> list ) {
		Collections.sort(list, new Comparator<GitHubRepository>() {

			@Override
			public int compare(GitHubRepository repo1, GitHubRepository repo2) {
				String name1 = repo1.getName();
				String name2 = repo2.getName();
				return name1.compareTo(name2);
			}
			
		});
	}
	
	/**
	 * Returns the String value of the given attribute name
	 * @param jsonObject
	 * @param attributeName
	 * @return
	 */
	private static String getStringAttribute( JSONObject jsonObject, String attributeName ) {
		Object oAttributeValue = jsonObject.get( attributeName );
		if ( oAttributeValue != null ) {
			if ( oAttributeValue instanceof String) {
				return (String)oAttributeValue ;
			}
			else {
				throw new RuntimeException ( "JSON error : attribute '" + attributeName + "' is not a String");
			}
		}
		else {
			throw new RuntimeException ( "JSON error : attribute '" + attributeName + "' not found");
		}
	}

	/**
	 * Returns the integer value of the given attribute name
	 * @param jsonObject
	 * @param attributeName
	 * @return
	 */
	private static long getLongAttribute( JSONObject jsonObject, String attributeName ) {
		Object oAttributeValue = jsonObject.get( attributeName );
		if ( oAttributeValue != null ) {
			if ( oAttributeValue instanceof Long) {
				return ((Long)oAttributeValue).longValue();
			}
			else {
				throw new RuntimeException ( "JSON error : attribute '" + attributeName 
						+ "' is not a Integer ("+oAttributeValue.getClass().getCanonicalName()+")");
			}
		}
		else {
			throw new RuntimeException ( "JSON error : attribute '" + attributeName + "' not found");
		}
	}
}
