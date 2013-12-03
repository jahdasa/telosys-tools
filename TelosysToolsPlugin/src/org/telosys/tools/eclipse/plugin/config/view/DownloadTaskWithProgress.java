package org.telosys.tools.eclipse.plugin.config.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Text;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.eclipse.plugin.commons.EclipseProjUtil;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
import org.telosys.tools.eclipse.plugin.commons.HttpDownloader;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.commons.ZipUtil;
import org.telosys.tools.generator.variables.VariablesManager;

/**
 * Eclipse runnable task with a progress bar 
 *  
 * @author L. Guerin
 *
 */
public class DownloadTaskWithProgress implements IRunnableWithProgress 
{
	private final IProject  project ;
	private final String    user ;
	private final String[]  repoNames ;
	private final String    sDownloadFolder ;
	private final String    sBundlesFolder ;
	private final String    sGitHubUrlPattern ;
	private final Text      loggerTextArea ;
	private final boolean   bInstall ;
	
	private int   _result = 0 ;
	
	//--------------------------------------------------------------------------------------------------
	public DownloadTaskWithProgress(IProject project, 
			String   user, 
			String[] repoNames, 
			String   sDownloadFolder, 
			String   sGitHubUrlPattern,
			boolean  bInstall,
			String   sBundlesFolder, 
			Text     loggerTextArea 
			) throws TelosysPluginException
	{
		super();
		
		this.project = project ;
		this.user = user ;
		this.repoNames = repoNames ;
		this.sDownloadFolder = sDownloadFolder ;
		this.sGitHubUrlPattern = sGitHubUrlPattern ;
		this.bInstall = bInstall ;
		this.sBundlesFolder = sBundlesFolder ;

		this.loggerTextArea = loggerTextArea ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException 
	{
		int count = 0 ;
		if ( repoNames.length > 0 ) {
			
			// count = total number of work units into which the main task is been subdivided
			int totalWorkTasks = repoNames.length ;
			if ( bInstall ) {
				totalWorkTasks = repoNames.length * 2;
			}			
			progressMonitor.beginTask("Download in progress", totalWorkTasks + 1); 
			progressMonitor.worked(1);

			loggerTextArea.setText("");
			for ( String githubRepoName : repoNames ) {
				String sFileURL = buildFileURL(githubRepoName, sGitHubUrlPattern);
				if ( sFileURL != null ) {
					String sZipFileName = buildDestinationFileName(githubRepoName, this.sDownloadFolder);
					count++;
					
					progressMonitor.subTask("Download #" + count + " '" + githubRepoName + "'");
					
					loggerTextArea.append("-> Download #" + count + " '" + githubRepoName + "' ... \n");
					loggerTextArea.append("  " + sFileURL + "\n");
					loggerTextArea.append("  " + sZipFileName + "\n");
					long r = 0;
					try {
						
						//--- Download the file
						r = HttpDownloader.download(sFileURL, sZipFileName);
						loggerTextArea.append("  done (" + r + " bytes).\n");
						EclipseWksUtil.refresh( new File(sZipFileName) );
						//--- One TARGET done
						// Notifies that a given number of work unit of the main task has been completed. 
						// Note that this amount represents an installment, as opposed to a cumulative amount of work done to date.
						progressMonitor.worked(1); // One unit done (not cumulative)
						
						//--- Unzip the downloaded file
						if ( bInstall ) {
//							String filesystemFolder = buildDestinationFolder(this.sDownloadFolder) ;
//							loggerTextArea.append("-> Installing " + sZipFileName + "\n");
//							loggerTextArea.append("    in folder " + filesystemFolder + "\n");
//							progressMonitor.subTask("Unzip #" + count + " '" + githubRepoName + "'");
//							try {
//								// ZipUtil.unzip(sDestinationFile, filesystemFolder, true ) ;
//								installBundle( sZipFileName, githubRepoName );
//							} catch (Exception e) {
//								String msg = "Cannot unzip file \n" 
//									+ sZipFileName + "\n\n"
//									+ ( e.getCause() != null ? e.getCause().getMessage() : "") ;
//								//MsgBox.error(msg );
//								loggerTextArea.append("ERROR \n");
//								loggerTextArea.append(msg);
//							}
//							EclipseWksUtil.refresh(new File(filesystemFolder));

							installBundle( sZipFileName, githubRepoName );
							progressMonitor.worked(1); // One unit done (not cumulative)
						}
					}
					catch (Exception e) {
						String msg = "Cannot download file \n" 
							+ sFileURL + "\n\n"
							+ ( e.getCause() != null ? e.getCause().getMessage() : "") ;
						//MsgBox.error(msg );
						loggerTextArea.append("ERROR \n");
						loggerTextArea.append(msg);
					}
					
				}
			}
			
			//--- Notifies that the work is done; that is, either the main task is completed or the user canceled it.
			progressMonitor.done();
			
			if (progressMonitor.isCanceled()) // Returns whether cancellation of current operation has been requested
			{
				throw new InterruptedException("The download was cancelled");
			}
			_result = count ;
		}
		else {
			MsgBox.error("Selection is void !");
			_result = 0 ;
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	private boolean installBundle( String zipFileName, String githubRepositoryName ) {
		// Destination folder in project : "TelosysTools/templates/bundle-name"
		String bundleFolderInProject = FileUtil.buildFilePath(this.sBundlesFolder, githubRepositoryName);
		String filesystemFolder = buildDestinationFolder(bundleFolderInProject) ;
		
		if ( alreadyInstalled( filesystemFolder, githubRepositoryName, bundleFolderInProject ) ) {
			return false ;
		}
		else {
			loggerTextArea.append("-> Install '" + zipFileName + "'v\n");
			loggerTextArea.append("   in '" + bundleFolderInProject + "' \n");
			try {
				ZipUtil.unzip(zipFileName, filesystemFolder, true ) ;
			} catch (Exception e) {
				String msg = "Cannot unzip file \n" 
					+ zipFileName + "\n\n"
					+ ( e.getCause() != null ? e.getCause().getMessage() : "") ;
				//MsgBox.error(msg );
				loggerTextArea.append("ERROR \n");
				loggerTextArea.append(msg);
				return false ;
			}
			EclipseWksUtil.refresh(new File(filesystemFolder));
			return true ;
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	private boolean alreadyInstalled( String fileName, String githubRepositoryName, String bundleFolderInProject ) {
		File file = new File(fileName) ;
		if ( file.exists() ) {
			MsgBox.warning(
					"Bundle '" + githubRepositoryName + "' is already installed. \n\n"
					+ "( see '" + bundleFolderInProject + "' )");
			return true ;
		}
		else {
			return false ;
		}
	}
	//--------------------------------------------------------------------------------------------------
	private String buildFileURL( String repoName, String sGitHubURLPattern ) {
		
		String repo = repoName.trim();
		if ( repo.length() == 0 ) {
			MsgBox.warning("GitHub repository name is void");
			return null ;
		}
		HashMap<String,String> hmVariables = new HashMap<String,String>();
		hmVariables.put("${USER}", this.user);
		hmVariables.put("${REPO}", repo);
		VariablesManager variablesManager = new VariablesManager(hmVariables);
		String sFileURL = variablesManager.replaceVariables(sGitHubURLPattern);
		// MsgBox.info("File URL : " + sFileURL);
		return sFileURL ;
	}

	//--------------------------------------------------------------------------------------------------
	/**
	 * Build the filesystem full path for the given Eclipse project folder
	 * @param sFolderInEclipseProject
	 * @return
	 */
	private String buildDestinationFolder(String sFolderInEclipseProject) {
		// folder path in Operating System 
		String projectDir = EclipseProjUtil.getProjectDir(this.project);
		String fullPath = FileUtil.buildFilePath(projectDir, sFolderInEclipseProject);
		return fullPath;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Build the filesystem full path for the given repository name and destination folder
	 * @param repoName GitHub repository name
	 * @param sDownloadFolder
	 * @return
	 */
	private String buildDestinationFileName(String repoName, String sDownloadFolder) {
		// file path in project
		String sFile = repoName + ".zip" ;
		String pathInProject = FileUtil.buildFilePath(sDownloadFolder, sFile);
		// file path in Operating System 
		String projectDir = EclipseProjUtil.getProjectDir(this.project);
		String fullPath = FileUtil.buildFilePath(projectDir, pathInProject);
		return fullPath;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the operation result : number of files generated
	 * @return
	 */
	public int getResult()
	{
		return _result ;
	}
}
