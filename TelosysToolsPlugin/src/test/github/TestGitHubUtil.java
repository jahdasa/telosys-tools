package test.github;

import java.util.List;

import org.telosys.tools.eclipse.plugin.commons.github.GitHubAPI;
import org.telosys.tools.eclipse.plugin.commons.github.GitHubRepository;

public class TestGitHubUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		Properties systemProperties = System.getProperties();
//		systemProperties.setProperty( "http.proxyHost", "proxyitx.idf.fr.ad.sotranet.net" );
//		systemProperties.setProperty( "http.proxyPort", "8080" );
//		systemProperties.setProperty( "https.proxyHost", "proxyitx.idf.fr.ad.sotranet.net" );
//		systemProperties.setProperty( "https.proxyPort", "8080" );
		
		System.out.println("Getting repositories... ");
		String result ;
		
//		result = service.getRepositories("l-gu");
//		System.out.println(result);

		String sGitHubUserName = "telosys-tools-community" ;
		
		result = GitHubAPI.getRepositoriesJSON(sGitHubUserName);
		System.out.println(result);
		
		List<GitHubRepository> repositories = GitHubAPI.getRepositories(sGitHubUserName);
		System.out.println("Repositories (" + repositories.size() + ") : ");
		for ( GitHubRepository repo : repositories ) {
			System.out.println(" .  '" + repo.getName() + "' / " 
					+ repo.getId() + " / '" + repo.getDescription() + "' / " + repo.getSize() );
		}
		
	}

}
