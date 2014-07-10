package org.telosys.tools.commons.github;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telosys.tools.commons.http.HttpClient;
import org.telosys.tools.commons.http.HttpClientConfig;
import org.telosys.tools.commons.http.HttpResponse;

public class GitHubClient {

	private final static String GIT_HUB_HOST_URL = "https://api.github.com" ;
	
	private final Properties proxyProperties ;
	
	public GitHubClient(Properties proxyProperties) {
		super();
		this.proxyProperties = proxyProperties;
	}

	/**
	 * Returns the REST API response in JSON format
	 * @param userName
	 * @return
	 */
	public String getRepositoriesJSON( String userName ) {

		String urlString = GIT_HUB_HOST_URL + "/users/" + userName + "/repos" ;
		HttpClientConfig httpClientConfig = null ;
		if ( proxyProperties != null ) {
			httpClientConfig = new HttpClientConfig(proxyProperties);
		}
		HttpClient httpClient = new HttpClient(httpClientConfig);
		HttpResponse response;
		try {
			response = httpClient.get(urlString, null);
		} catch (Exception e) {
			throw new RuntimeException ("Http error", e);
		}
		return new String(response.getBodyContent());
	}

	/**
	 * Return the repositories for the given GitHub user name 
	 * @param userName
	 * @return
	 */
	public List<GitHubRepository> getRepositories( String userName ) {

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
					String name = getStringAttribute(repo, "name", "(#"+id+"-no-name)");
					String description = getStringAttribute(repo, "description", "(no-description)");
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
		GitHubUtil.sortByName(repositories);
		return repositories ;
	}

	/**
	 * Returns the String value of the given attribute name
	 * @param jsonObject
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	private String getStringAttribute( JSONObject jsonObject, String attributeName, String defaultValue ) {
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
			if ( defaultValue != null ) {
				return defaultValue ;
			}
			else {
				throw new RuntimeException ( "JSON error : attribute '" + attributeName + "' not found");
			}
		}
	}

	/**
	 * Returns the integer value of the given attribute name
	 * @param jsonObject
	 * @param attributeName
	 * @return
	 */
	private long getLongAttribute( JSONObject jsonObject, String attributeName ) {
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
