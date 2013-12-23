/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.eclipse.plugin.commons ;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.eclipse.plugin.commons.dialogbox.OverwriteDialogBox;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.generator.variables.Variable;


public class BundleResourcesManager {
	private final static IProgressMonitor NO_PROGRESS_MONITOR = null ;
	
	private final IProject           _eclipseProject ;
	private final ProjectConfig      _projectConfig ;
	private final String             _bundleName ;
	private final TelosysToolsLogger _logger;
	
	private Boolean overwriteGlobalChoice = null ;
	private boolean taskCanceled = false ;

	//----------------------------------------------------------------------------------------------------
	public BundleResourcesManager(IProject eclipseProject, String bundleName, ProjectConfig projectConfig, TelosysToolsLogger logger) {
		super();
		_eclipseProject = eclipseProject ;
		_projectConfig  = projectConfig ;
		_bundleName     = bundleName ;
		_logger         = logger ;
		log("created.");
	}
	
	//----------------------------------------------------------------------------------------------------
	private void log(String s) {
		if (_logger != null) {
			_logger.log( this.getClass().getSimpleName() + " : " + s);
		}
	}
    //----------------------------------------------------------------------------------------------------
	/**
	 * Returns the IFolder instance for the given subfolder name located in the given folder<br>
	 * Throws an exception if it doesn't exist.
	 * @param folder
	 * @param subfolder the name of the subfolder  
	 * @return
	 * @throws Exception
	 */
	private IFolder getSubFolder( IFolder folder, String subfolder ) throws Exception {
		
//		IResource resource = folder.findMember(subfolder);
//		if ( resource == null ) {
//			throw new Exception("Folder '" + subfolder + "' not found in '"+ folder.getFullPath() + "'");
//		}		
//		if ( resource.getType() != IResource.FOLDER  ) {
//			throw new Exception("'" + folder + "' is not a folder");
//		}
		IFolder sub = folder.getFolder(subfolder);
		if ( sub.exists() ) {
			return sub ;
		}
		else {
			throw new Exception("Folder '" + sub.getFullPath() + "' doesn't exist." );
		}
	}
	//----------------------------------------------------------------------------------------------------
	/**
	 * Returns the IFolder instance where the static resources are located<br>
	 * @return
	 * @throws Exception
	 */
	private IFolder getResourcesFolder() throws Exception {
		String projectTemplatesFolder = _projectConfig.getTemplatesFolder() ;
		IFolder templatesFolder = _eclipseProject.getFolder( projectTemplatesFolder ) ;
		if ( ! templatesFolder.exists() ) {
			throw new Exception("Templates folder '" + templatesFolder.getFullPath() + "' not found");
		}
		log("Templates folder = " + templatesFolder.getLocation() );
		IFolder bundleFolder = getSubFolder(templatesFolder, _bundleName);
		log("Bundle folder    = " + bundleFolder.getLocation() );
		IFolder resourcesFolder = getSubFolder(bundleFolder, "resources");
		log("Resources folder = " + resourcesFolder.getLocation() );
		return resourcesFolder ;
	}
	//----------------------------------------------------------------------------------------------------
	/**
	 * @param targetsDefinitions
	 * @return
	 */
	private List<Target> getResourcesTargets(List<TargetDefinition> targetsDefinitions ) {
		log("getResourcesTargets()... " );
		
		Variable[] projectVariables = _projectConfig.getAllVariables();
//		log(" variables : " );
//		for ( Variable v : projectVariables ) {
//			log(" . " + v.getName() + " = " + v.getValue()  );
//		}
		LinkedList<Target> targets = new LinkedList<Target>();
		if ( targetsDefinitions != null ) {
			for ( TargetDefinition targetDefinition : targetsDefinitions ) {
				Target target = new Target ( targetDefinition, "", "", projectVariables );
				targets.add(target);
			}
		}
		log("getResourcesTargets() : return " + targets.size() + " target(s)");
		return targets ;
	}
	
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * Copy all the given resources targets definitions
	 * @param targetsDefinitions list of the resources targets definition
	 * @throws Exception
	 */
	public int copyResourcesInProject( List<TargetDefinition> targetsDefinitions ) throws Exception {
		log("copyResourcesInProject()... " );
		
		int count = 0 ;
		overwriteGlobalChoice = null ; // Reset 
		
		//--- Resources folder in the Eclipse workspace/project
		IFolder resourcesFolder = getResourcesFolder() ;
		//--- Build the real resources targets from the targets definitions 
		List<Target> resourcesTargets = getResourcesTargets( targetsDefinitions ) ;
		//--- For each target 
		for ( Target target : resourcesTargets ) {
			count = count + copyResourceTargetInProject(resourcesFolder, target );
			if ( taskCanceled ) {
				break ;
			}
		}
		return count ;
	}

    //----------------------------------------------------------------------------------------------------
	/**
	 * Copy the given target in the project ( it can be a single file or a folder )
	 * @param resourcesFolder
	 * @param target
	 * @throws Exception
	 */
	/**
	 * @param resourcesFolder
	 * @param target
	 * @return
	 * @throws Exception
	 */
	private int copyResourceTargetInProject( IFolder resourcesFolder, Target target ) throws Exception {
		log("copyResourceTargetInProject() : " + target );

		int count = 0 ;
		String resourceName = target.getTemplate(); // "template file" in .cfg file is the "resource to be copied"
		IResource originalResource = resourcesFolder.findMember(resourceName);
		if ( originalResource == null ) {
			throw new GeneratorException("File or folder '" + resourceName + "' not found in resources" );
		}
		
		if ( originalResource.getType() == IResource.FILE  ) { //--- Resource is a FILE 
			
			IFile originalResourceFile = (IFile) originalResource ;
			//--- Destination 
			String targetFileName = target.getFile() ;
			String targetFilePathInProject = null ;
			if ( StrUtil.nullOrVoid(targetFileName) ) {
				//--- No target file name : use the "resource file name" to build the path
//				String targetFolderPath = target.getOutputFileNameInProject() ;
//				String targetFilePath = FileUtil.buildFilePath(targetFolderPath, resourceName ); 
				targetFilePathInProject = FileUtil.buildFilePath(target.getFolder(), resourceName ); 
			}
			else {
				//--- The target file name is defined : use the target path
				targetFilePathInProject = target.getOutputFileNameInProject();
			}
			//--- Copy the single resource file in the target's destination file
			count = count + copyFileToFile(originalResourceFile, targetFilePathInProject );
		}
		else if ( originalResource.getType() == IResource.FOLDER ) { //--- Resource is a FOLDER 

			IFolder originalResourceFolder = (IFolder) originalResource ;
			//--- Always use the DESTINATION FOLDER
			String targetFolderPathInProject = target.getFolder() ; 
			if ( StrUtil.nullOrVoid(targetFolderPathInProject) ) {
				targetFolderPathInProject = "";
			}
			//--- Copy the resource folder in the target's destination folder
			count = count + copyFolderToFolder(originalResourceFolder, targetFolderPathInProject );
		}
		else {
			throw new GeneratorException("Resource '" + resourceName + "' is not a file or folder" );
		}
		return count ;
	}
	
	//----------------------------------------------------------------------------------------------------
	private int copyFileToFile( IFile resource, String destinationFileInProject ) throws Exception {

		log("copyFileToFile(..,..)") ;
		log(" from : " + resource.getFullPath() );
		log("   to : " + destinationFileInProject );

		IFile destFile = _eclipseProject.getFile(destinationFileInProject);
		//IPath destinationPath = destFile.getLocation() ; // absolute path in the local file system
		//IPath destinationPath = destFile.getProjectRelativePath() ; // eg : ${WEB}/mytestfile1.css
		IPath destinationPath = destFile.getFullPath() ; // Returns the full, absolute path of this resource relative to the workspace
		//destFile.getRawLocation(); //  Returns the file system location of this resource, or null if no path can be determined.
		log(" path = " + destinationPath );
		
		int copyCount = 0 ;
		boolean copy = true ;
		if ( destFile.exists() ) {
			log("destination file exists ") ;
			if ( overwriteGlobalChoice != null ) {
				copy = overwriteGlobalChoice ;
				log("destination file exists : global choice set : " + overwriteGlobalChoice ) ;
			}
			else {
				log("destination file exists : global choice not set => message box for confirmation") ;
				//copy = MsgBox.confirm("File '" + destinationFileInProject + "' exists. \nDo you want to overwrite ? ");
				String fileFolder = destFile.getParent().getFullPath().toString() ;
				int choice = OverwriteDialogBox.confirm(destFile.getName(), fileFolder );
				log(" choice = " + choice) ;
				switch (choice) {
				
				case OverwriteDialogBox.YES_TO_ALL :
					overwriteGlobalChoice = true ;
				case OverwriteDialogBox.YES :
					copy = true ;
					break;

				case OverwriteDialogBox.NO_TO_ALL :
					overwriteGlobalChoice = false ;
				case OverwriteDialogBox.NO :
					copy = false ;
					break;

				default: // CANCEL
					taskCanceled = true ;
					copy = false ;
					break;
				}
			}
		}
		
		if ( copy ) {
			// Here destination file doesn't exist or overwrite has been confirmed 
			if ( destFile.exists() ) {
				// Overwrite = remove before copy 
				log("confirmed => delete existing file") ;
				destFile.delete(IResource.FORCE, NO_PROGRESS_MONITOR);
			}
			
			// Check destination folder existence ( create if necessary )
			IContainer parent = destFile.getParent() ;
			log(" check parent : " + parent.getFullPath() );
			if ( parent.exists() == false ) {
				log(" parent doesn't exist => creation..." );
				EclipseWksUtil.createFolder(parent.getFullPath());
				log(" folder '" + parent.getFullPath() + "' created");
			}
			else {
				log(" parent exists " );
			}
			
			// Copy in destination folder (the workspace is refreshed automatically by Eclipse)
			log(" copying..." );
			try {
				resource.copy(destinationPath, IResource.FORCE, NO_PROGRESS_MONITOR);
			} catch (CoreException e) {
				MsgBox.error("Eclipse CoreException", e.getMessage());
			}
			log(" copied." );
			copyCount++ ;
		}
		return copyCount ;
	}
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * Returns the Eclipse IContainer corresponding to the given destination in the current project
	 * @param destinationFolderInProject
	 * @return the IContainer ( IFolder or IProject )
	 * @throws Exception
	 */
	private IContainer getDestinationContainer( String destinationFolderInProject ) throws Exception {
				
		if ( StrUtil.nullOrVoid(destinationFolderInProject) ) {
			return _eclipseProject ; // "" = "project root" => The container is the project itself
		}
		else if ( destinationFolderInProject.trim().equals("/") ) {
			return _eclipseProject ; // "/" = "project root" => The container is the project itself
		}
		else if ( destinationFolderInProject.trim().equals(".") ) {
			return _eclipseProject ; // "." = "current project" => The container is the project itself
		}
		else {
			// Return the folder in the project ( existing or non existing folder )
			return _eclipseProject.getFolder(destinationFolderInProject);
		}
	}
	//----------------------------------------------------------------------------------------------------
	/**
	 * Recursive method copying the given "resource folder" in the given "destination folder"
	 * @param resourceFolder folder containing the resources to be copied
	 * @param destinationFolderInProject the location where to copy the resources
	 * @return
	 * @throws Exception
	 */
	private int copyFolderToFolder( IFolder resourceFolder, String destinationFolderInProject ) throws Exception {

		log("copyFolderToFolder(..,..)") ;
		log(" from : " + resourceFolder.getFullPath() );
		log("   to : " + destinationFolderInProject );

		int count = 0 ;
		//IFolder destFolder = _eclipseProject.getFolder(destinationFolderInProject); // Throws IllegalArgumentException if ( "" or "/" )
		IContainer destContainer = getDestinationContainer(destinationFolderInProject);
		int containerType = destContainer.getType() ;
		if ( destContainer.exists() ) {
			log ( " folder or project '" + destContainer.getName() + "' exists ") ;
			//--- Check it's a folder or a project
			if ( containerType != IResource.FOLDER && containerType != IResource.PROJECT ) {
				throw new GeneratorException("Cannot copy resource. Destination '" + destContainer.getName() 
						+ "' is not a folder nor a project" );
			}
		}
		else {
			if ( containerType == IResource.FOLDER ) {
				log ( " container '" + destContainer.getName() + "' doesn't exist => cast to folder for creation...") ;
				IFolder destFolder = (IFolder) destContainer ;
				log ( " folder '" + destFolder.getName() + "' => creation") ;
				//--- Create it (even if void, to build the same structure as the original folder)
				destFolder.create(true, // force - a flag controlling how to deal with resources that are not in sync with the local file system
						true, // local - a flag controlling whether or not the folder will be local after the creation
						null); // monitor - a progress monitor, or null if progress reporting is not desired
				log ( " folder '" + destFolder.getName() + "' created.") ;
			} else {
				throw new GeneratorException("Cannot create destination '" + destContainer.getName() 
						+ "' (not a folder)" );
			}
		}
		
		IResource[] folderMembers = resourceFolder.members();
		
		//--- Copy each member of the folder
		for ( IResource r : folderMembers ) {
			if (r instanceof IFile) {
				log ( " - member : " + r.getName() + " (IFile) ") ;
				// Build destination file
				String destFile = FileUtil.buildFilePath(destinationFolderInProject, r.getName() );
				// Copy from file to file
				count = count + copyFileToFile((IFile) r, destFile);
			}
			else if (r instanceof IFolder) {
				log ( " - member : " + r.getName() + " (IFolder) ") ;
				// Build destination sub-folder
				String destSubFolder = FileUtil.buildFilePath(destinationFolderInProject, r.getName() );
				// Copy recursively
				count = count + copyFolderToFolder((IFolder) r, destSubFolder) ;
			}
		}
		return count ;
	}
}
