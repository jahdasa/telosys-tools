package org.temp;

import java.io.File;
import java.util.List;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.context.Target;

public class ResourcesManager {

	private final IGeneratorConfig generatorConfig ;
	
	public ResourcesManager(IGeneratorConfig generatorConfig) {
		super();
		this.generatorConfig = generatorConfig;
	}

	public void copyResources( List<Target> resources ) {
		
		for ( Target target : resources ) {
//			target.getFile();
//			target.getFolder()
		}
	}

	/**
	 * Copy the given static resource (file or folder) in the project destination file or folder 
	 * @param target
	 * @throws GeneratorException
	 */
	public void copyResourceInProject( Target target, boolean overwrite ) throws GeneratorException {
		
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
	
	public boolean copyFileToFile( String originalFile, String destinationFile, boolean overwrite ) throws GeneratorException {

		boolean copied = false ;
		File destFile = new File(destinationFile) ;
		if ( destFile.exists() ) {
			if ( overwrite ) {
				try {
					FileUtil.copy(originalFile, destinationFile);
				} catch (Exception e) {
					throw new GeneratorException("Cannot copy '" + originalFile + "' to '" + destinationFile + "'", e );
				}
				copied = true ;
			}
		}
		return copied ;
	}

	public void buildListOfFiles ( final File folder, List<File> files )  {
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	buildListOfFiles(fileEntry, files);
	        } else {
	        	files.add(fileEntry);
	        }
	    }	
	}
	
	public void copyFolderToFolder( String sourceFolderName, String destinationFolderName, boolean overwrite ) throws GeneratorException {

		//boolean copied = false ;
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
