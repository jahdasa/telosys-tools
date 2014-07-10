package org.telosys.tools.tests.commons.github;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.telosys.tools.commons.github.GitHubClient;
import org.telosys.tools.commons.github.GitHubRepository;
import org.telosys.tools.commons.github.GitHubUtil;
import org.telosys.tools.tests.commons.TestsFolders;
import org.telosys.tools.tests.commons.http.HttpTestConfig;

public class GitHubClientTest extends TestCase {

	private final static String GITHUB_USER = "telosys-tools" ;
	// "telosys-tools-beta" ;
		
	public void testGetRepositories() {
		
		System.out.println("Getting repositories... ");

		Properties properties = HttpTestConfig.getSpecificProxyProperties();
		
		GitHubClient gitHubClient = new GitHubClient(GitHubUtil.getDefaultGitHubURLPattern(), properties);
		String jsonResult = gitHubClient.getRepositoriesJSON(GITHUB_USER);
		System.out.println(jsonResult);
		
		List<GitHubRepository> repositories = gitHubClient.getRepositories(GITHUB_USER);
		System.out.println("Repositories (" + repositories.size() + ") : ");
		for ( GitHubRepository repo : repositories ) {
			System.out.println(" .  '" + repo.getName() + "' / " 
					+ repo.getId() + " / '" + repo.getDescription() + "' / " + repo.getSize() );
		}
	}
	
	public void testDownloadRepository() {
		
		Properties properties = HttpTestConfig.getSpecificProxyProperties();		
		GitHubClient gitHubClient = new GitHubClient(GitHubUtil.getDefaultGitHubURLPattern(), properties);
		
		String repoName = "basic-templates-TT210" ;
		String destinationFile = TestsFolders.getTestsDownloadFolder() + "/" + repoName + ".zip" ;
		System.out.println("Download repository " + repoName );
		System.out.println("                 to " + destinationFile );
		gitHubClient.downloadRepository(GITHUB_USER, repoName, destinationFile);
		System.out.println("Done.");		
	}
}