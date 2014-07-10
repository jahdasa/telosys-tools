package org.telosys.tools.commons.github;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for GitHub
 * 
 * @author L. Guerin
 *
 */
public class GitHubUtil {

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
}
