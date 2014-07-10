package org.telosys.tools.tests.commons.github;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.telosys.tools.commons.github.GitHubClient;
import org.telosys.tools.commons.github.GitHubRepository;
import org.telosys.tools.tests.commons.http.HttpTestConfig;

public class GitHubClientTest extends TestCase {

	public void testGetRepositories() {
		
		System.out.println("Getting repositories... ");

		String sGitHubUserName = "telosys-tools" ;
		//String sGitHubUserName = "telosys-tools-beta" ;
		
//		Properties properties = new Properties();
//		properties.setProperty("http.proxyHost",  "proxyitx.idf.fr.ad.sotranet.net");
//		properties.setProperty("http.proxyPort",  "8080");
//		properties.setProperty("https.proxyHost", "proxyitx.idf.fr.ad.sotranet.net");
//		properties.setProperty("https.proxyPort", "8080");
		Properties properties = HttpTestConfig.getSpecificProxyProperties();
		
		GitHubClient gitHubClient = new GitHubClient(properties);
		String jsonResult = gitHubClient.getRepositoriesJSON(sGitHubUserName);
		System.out.println(jsonResult);
		
		List<GitHubRepository> repositories = gitHubClient.getRepositories(sGitHubUserName);
		System.out.println("Repositories (" + repositories.size() + ") : ");
		for ( GitHubRepository repo : repositories ) {
			System.out.println(" .  '" + repo.getName() + "' / " 
					+ repo.getId() + " / '" + repo.getDescription() + "' / " + repo.getSize() );
		}
	}
}
