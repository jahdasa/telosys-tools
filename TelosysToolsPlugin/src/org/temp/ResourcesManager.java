package org.temp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.Variable;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.target.TargetDefinition;

public class ResourcesManager {

	private final IGeneratorConfig   generatorConfig ;
	private final TelosysToolsLogger _logger;
	
	//----------------------------------------------------------------------------------------------------
	public ResourcesManager(IGeneratorConfig generatorConfig, TelosysToolsLogger logger) {
		super();
		this.generatorConfig = generatorConfig;
		this._logger = logger ;
		log("ResourcesManager created.");
	}
	
	//----------------------------------------------------------------------------------------------------
	private void log(String s) {
		if (_logger != null) {
			_logger.log(s);
		}
	}

	//----------------------------------------------------------------------------------------------------
	/**
	 * @param targetsDefinitions
	 * @param overwrite
	 * @throws GeneratorException
	 */
	public void copyResourcesInProject( List<TargetDefinition> targetsDefinitions, boolean overwrite ) throws GeneratorException {
		log("ResourcesManager:copyResourcesInProject()... " );
		//--- Build targets from targets definitions 
		List<Target> resourcesTargets = getResourcesTargets( targetsDefinitions ) ;
		//--- Copy the targets 
		copyResourcesTargetsInProject( resourcesTargets, overwrite );
	}

	//----------------------------------------------------------------------------------------------------
	/**
	 * @param targetsDefinitions
	 * @return
	 */
	private List<Target> getResourcesTargets(List<TargetDefinition> targetsDefinitions ) {
		log("ResourcesManager:getResourcesTargets()... " );
		Variable[] projectVariables = this.generatorConfig.getProjectConfiguration().getVariables() ;
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
	 * Copy the given static resources in the project destination file or folder 
	 * @param targets the list of resource targets 
	 * @param overwrite
	 * @throws GeneratorException
	 */
	private void copyResourcesTargetsInProject( List<Target> targets, boolean overwrite ) throws GeneratorException {
		log("ResourcesManager:copyResourcesTargetsInProject()... " );
		//--- For each target 
		for ( Target target : targets ) {
			copyResourceTargetInProject( target, overwrite );
		}
	}

	//----------------------------------------------------------------------------------------------------
	/**
	 * Copy the given static resource (file or folder) in the project destination file or folder 
	 * @param target the resource target
	 * @throws GeneratorException
	 */
	private void copyResourceTargetInProject( Target target, boolean overwrite ) throws GeneratorException {
		
		log("ResourcesManager:copyResourceTargetInProject() : " + target );

		//--- Original resource : file or folder to be copied
		String templatesFolder = generatorConfig.getTemplatesFolderFullPath();
		String resourcesFolder = FileUtil.buildFilePath(templatesFolder, "resources" );
		String resourceName = target.getTemplate(); // Replaces the template file in .cfg file
		String originalFileOrFolderPath = FileUtil.buildFilePath(resourcesFolder, resourceName );
		File originalFileOrFolder = new File(originalFileOrFolderPath) ;
		if ( ! originalFileOrFolder.exists() ) {
			throw new GeneratorException("File or folder '" + originalFileOrFolder.toString() + "' not found in resources" );
		}
		
		if ( originalFileOrFolder.isFile() ) { //--- Resource is a FILE 
			
			//--- Destination 
			String targetFileName = target.getFile() ;
			if ( StrUtil.nullOrVoid(targetFileName) ) {
				//--- No target file name : use the "resource file name" to build the path
				String targetFolderPath = target.getOutputFolderInFileSystem( generatorConfig.getProjectLocation() );
				String targetFilePath = FileUtil.buildFilePath(targetFolderPath, resourceName ); 
				copyFileToFile(originalFileOrFolderPath, targetFilePath, overwrite);
			}
			else {
				//--- The target file name is defined : use the target path
				String targetFilePath = target.getOutputFileNameInFileSystem( generatorConfig.getProjectLocation() );
				copyFileToFile(originalFileOrFolderPath, targetFilePath, overwrite);
			}
		}
		else if ( originalFileOrFolder.isDirectory() ) { //--- Resource is a FOLDER 
			//--- Always use the DESTINATION FOLDER
			String targetFolderPath = target.getOutputFolderInFileSystem( generatorConfig.getProjectLocation() );
			copyFolderToFolder(originalFileOrFolderPath, targetFolderPath, overwrite);
		}
		else {
			throw new GeneratorException("Resource '" + originalFileOrFolder.toString() + "' is not a file or folder" );
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * Copy a resource file to its destination file
	 * @param originalFile
	 * @param destinationFile
	 * @param overwrite
	 * @return
	 * @throws GeneratorException
	 */
	private boolean copyFileToFile( String originalFile, String destinationFile, boolean overwrite ) throws GeneratorException {

		log("ResourcesManager:copyFileToFile() : overwrite = " + overwrite );
		log(" from : " + originalFile );
		log("   to : " + destinationFile );

		boolean copied = false ;
		File destFile = new File(destinationFile) ;
		if ( ( destFile.exists() == false ) || ( destFile.exists() && overwrite ) ) {
			try {
				FileUtil.copy(originalFile, destinationFile, true);
			} catch (Exception e) {
				throw new GeneratorException("Cannot copy '" + originalFile + "' to '" + destinationFile + "'", e );
			}
			copied = true ;
		}
		log(" copied ? " + copied );
		return copied ;
	}

//	public void buildListOfFiles ( final File folder, List<File> files )  {
//		for (final File fileEntry : folder.listFiles()) {
//	        if (fileEntry.isDirectory()) {
//	        	buildListOfFiles(fileEntry, files);
//	        } else {
//	        	files.add(fileEntry);
//	        }
//	    }	
//	}
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * Copy a resource folder to its destination folder (including the content recursively)
	 * @param sourceFolderName
	 * @param destinationFolderName
	 * @param overwrite
	 * @throws GeneratorException
	 */
	private void copyFolderToFolder( String sourceFolderName, String destinationFolderName, boolean overwrite ) throws GeneratorException {

		File sourceFolder = new File(sourceFolderName) ;
		if ( sourceFolder.exists() ) {
			File destinationFolder = new File(destinationFolderName) ;
			try {
				FileUtil.copyFolder(sourceFolder, destinationFolder, overwrite);
			} catch (Exception e) {
				throw new GeneratorException("Cannot copy '" + sourceFolderName + "' to '" + destinationFolderName + "'", e );
			}
		}		
	}
}
