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
package org.temp ;

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
import org.telosys.tools.commons.Variable;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.dialogbox.OverwriteDialogBox;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.target.TargetDefinition;


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
		
		// TODO : invalid variables
		Variable[] projectVariables = _projectConfig.getProjectVariables();
		log(" variables : " );
		for ( Variable v : projectVariables ) {
			log(" . " + v.getName() + " = " + v.getValue()  );
		}
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
	 * Copy all the given targets definitions
	 * @param targetsDefinitions
	 * @throws Exception
	 */
	public int copyResourcesInProject( List<TargetDefinition> targetsDefinitions ) throws Exception {
		log("copyResourcesInProject()... " );
		
		int count = 0 ;
		overwriteGlobalChoice = null ; // Reset 
		
		//--- Resources folder in the Eclipse workspace/project
		IFolder resourcesFolder = getResourcesFolder() ;
		//--- Build targets from targets definitions 
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
	 * Copy the given target definition in the project ( it can be a single file or a folder )
	 * @param resourcesFolder
	 * @param target
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
			
			//--- Destination 
			String targetFileName = target.getFile() ;
			if ( StrUtil.nullOrVoid(targetFileName) ) {
				//--- No target file name : use the "resource file name" to build the path
//				String targetFolderPath = target.getOutputFileNameInProject() ;
//				String targetFilePath = FileUtil.buildFilePath(targetFolderPath, resourceName ); 
				String targetFilePathInProject = FileUtil.buildFilePath(target.getFolder(), resourceName ); 
				count = count + copyFileToFile(originalResource, targetFilePathInProject );
			}
			else {
				//--- The target file name is defined : use the target path
				String targetFilePathInProject = target.getOutputFileNameInProject();
				count = count + copyFileToFile(originalResource, targetFilePathInProject );
			}
		}
		else if ( originalResource.getType() == IResource.FOLDER ) { //--- Resource is a FOLDER 
			//--- Always use the DESTINATION FOLDER
			// TODO
//			String targetFolderPath = target.getOutputFolderInFileSystem( generatorConfig.getProjectLocation() );
//			copyFolderToFolder(originalFileOrFolderPath, targetFolderPath, overwrite);
		}
		else {
			throw new GeneratorException("Resource '" + resourceName + "' is not a file or folder" );
		}
		return count ;
	}
	
	//----------------------------------------------------------------------------------------------------
	private int copyFileToFile( IResource resource, String destinationFileInProject ) throws Exception {

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
	
}
