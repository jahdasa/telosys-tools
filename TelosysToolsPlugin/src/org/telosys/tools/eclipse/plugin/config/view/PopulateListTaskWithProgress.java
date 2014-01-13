package org.telosys.tools.eclipse.plugin.config.view;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.List;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.github.GitHubAPI;
import org.telosys.tools.eclipse.plugin.commons.github.GitHubRepository;

/**
 * Eclipse runnable task with a progress bar 
 *  
 * @author L. Guerin
 *
 */
public class PopulateListTaskWithProgress implements IRunnableWithProgress 
{
	private final String    _sGitHubUserName ;
	private final List      _listGitHubRepositories ;
	
	//--------------------------------------------------------------------------------------------------
	public PopulateListTaskWithProgress( String sGitHubUserName,List listGitHubRepositories )
	{
		super();		
		this._sGitHubUserName = sGitHubUserName ;
		this._listGitHubRepositories = listGitHubRepositories ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException 
	{
		// count = total number of work units into which the main task is been subdivided
		int totalWorkTasks = 3 ;
		progressMonitor.beginTask("Requesting GitHub server ...", totalWorkTasks ); 
		progressMonitor.worked(1);
		
		_listGitHubRepositories.removeAll();
		
		try {
			//--- Http REST request to GitHub 
			progressMonitor.subTask("Requesting GitHub server ...");
			java.util.List<GitHubRepository> repositories = GitHubAPI.getRepositories(_sGitHubUserName);
			progressMonitor.worked(1);
				
			//--- 
			progressMonitor.subTask("Populating the list ...");
			for ( GitHubRepository repo : repositories ) {
// Removed in ver 2.1.0 ( "size" is not reliable in the GitHub API ) 
//				if ( repo.getSize() > 0 ) {
//					_listGitHubRepositories.add( repo.getName() );
//				}
				_listGitHubRepositories.add( repo.getName() );
			}
			progressMonitor.worked(1);

		} catch (Exception e) {
			MsgBox.error("Cannot get information from GitHub", e);
		}
		finally {
			//--- Notifies that the work is done; that is, either the main task is completed or the user canceled it.
			progressMonitor.done();
		}
	}	
}
