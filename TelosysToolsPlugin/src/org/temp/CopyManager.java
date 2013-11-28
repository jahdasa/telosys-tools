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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.target.TargetDefinition;


public class CopyManager {
	private final static IProgressMonitor NO_PROGRESS_MONITOR = null ;
	
	private final IProject           _eclipseProject ;
	private final ProjectConfig      _projectConfig ;
	private final String             _bundleName ;
	private final TelosysToolsLogger _logger;
    
	//----------------------------------------------------------------------------------------------------
	public CopyManager(IProject eclipseProject, String bundleName, ProjectConfig projectConfig, TelosysToolsLogger logger) {
		super();
		_eclipseProject = eclipseProject ;
		_projectConfig  = projectConfig ;
		_bundleName     = bundleName ;
		_logger = logger ;
		log("ResourcesManager created.");
	}
	
	//----------------------------------------------------------------------------------------------------
	private void log(String s) {
		if (_logger != null) {
			_logger.log( this.getClass().getSimpleName() + " : " + s);
		}
	}
    //----------------------------------------------------------------------------------------------------
	private IFolder getSubFolder( IFolder folder, String subfolder ) throws Exception {
		
		IResource resource = folder.findMember(subfolder);
		if ( resource == null ) {
			throw new Exception("Folder '" + folder + "' not found");
		}		
		if ( resource.getType() != IResource.FOLDER  ) {
			throw new Exception("'" + folder + "' is not a folder");
		}
		return folder.getFolder(subfolder);
	}
	//----------------------------------------------------------------------------------------------------
	/**
	 * @param targetsDefinitions
	 * @return
	 */
	private List<Target> getResourcesTargets(List<TargetDefinition> targetsDefinitions ) {
		log("ResourcesManager:getResourcesTargets()... " );
		Variable[] projectVariables = _projectConfig.getProjectVariables();
		LinkedList<Target> targets = new LinkedList<Target>();
		if ( targetsDefinitions != null ) {
			for ( TargetDefinition targetDefinition : targetsDefinitions ) {
				Target target = new Target ( targetDefinition, "", "", projectVariables );
				targets.add(target);
			}
		}
		log("ResourcesManager:getResourcesTargets() : return " + targets.size() + " target(s)");
		return targets ;
	}
	
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * @param targetsDefinitions
	 * @param overwrite
	 * @throws GeneratorException
	 */
	public void copyResourcesInProject( List<TargetDefinition> targetsDefinitions ) throws Exception {
		log("ResourcesManager:copyResourcesInProject()... " );
		//--- Build targets from targets definitions 
		List<Target> resourcesTargets = getResourcesTargets( targetsDefinitions ) ;
		//--- For each target 
		for ( Target target : resourcesTargets ) {
			copyResourceTargetInProject( target );
		}
	}

    //----------------------------------------------------------------------------------------------------
	private void copyResourceTargetInProject( Target target ) throws Exception {
		log("copyResourceTargetInProject() : " + target );

		//--- Original resource : file or folder to be copied
//		String templatesFolder = generatorConfig.getTemplatesFolderFullPath();
//		String resourcesFolder = FileUtil.buildFilePath(templatesFolder, "resources" );
		String projectTemplatesFolder = _projectConfig.getTemplatesFolder() ;
		IFolder templatesFolder = _eclipseProject.getFolder( projectTemplatesFolder ) ;
		if ( ! templatesFolder.exists() ) {
			throw new Exception("Templates folder '" + projectTemplatesFolder + "' not found");
		}
		log("Templates folder = " + templatesFolder.getLocation() );
		IFolder bundleFolder = getSubFolder(templatesFolder, _bundleName);
		log("Bundle folder = " + bundleFolder.getLocation() );
		IFolder resourcesFolder = getSubFolder(bundleFolder, "resources");
		log("Resources folder = " + resourcesFolder.getLocation() );

		String resourceName = target.getTemplate(); // Replaces the template file in .cfg file
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
				copyFileToFile(originalResource, targetFilePathInProject );
			}
			else {
				//--- The target file name is defined : use the target path
				String targetFilePathInProject = target.getOutputFileNameInProject();
				copyFileToFile(originalResource, targetFilePathInProject );
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
		
	}
	
	//----------------------------------------------------------------------------------------------------
	private void copyFileToFile( IResource resource, String destinationFile ) throws Exception {

		log("copyFileToFile(..,..)") ;
		log(" from : " + resource );
		log("   to : " + destinationFile );

		IFile destFile = _eclipseProject.getFile(destinationFile);
		//IPath destinationPath = destFile.getLocation() ; // absolute path in the local file system
		IPath destinationPath = destFile.getProjectRelativePath() ;
		log(" project relative path : " + destinationPath );
		
		
		boolean copy = true ;
		if ( destFile.exists() ) {
			copy = MsgBox.confirm("File '" + destinationFile + "' exists. Do you want to overwrite ? ");
		}
		if ( copy ) {
			log(" copying..." );
			resource.copy(destinationPath, IResource.FORCE, NO_PROGRESS_MONITOR);
			log(" copied." );
		}
		
//		
//		boolean copied = false ;
//		
//		File destFile = new File(destinationFile) ;
//		if ( ( destFile.exists() == false ) || ( destFile.exists() && overwrite ) ) {
//			try {
//				FileUtil.copy(originalFile, destinationFile, true);
//			} catch (Exception e) {
//				throw new GeneratorException("Cannot copy '" + originalFile + "' to '" + destinationFile + "'", e );
//			}
//			copied = true ;
//		}
//		log(" copied ? " + copied );
//		return copied ;
	}
	
}
