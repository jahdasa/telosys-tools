package org.telosys.tools.eclipse.plugin.wizards.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.telosys.tools.eclipse.plugin.MyPlugin;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;


public class WizardTools 
{
	public static IFile generateJavaClass(String sSourceDir, String sPackage, String sClassName, 
			Generator generator ) 
	{
		IFile fileGenerated = null ;
		
		PluginLogger.log("================================" );
		PluginLogger.log("generate( " + sSourceDir + ", " + sPackage + ", " + sClassName +")..." );
		
		// --- Get the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		PluginLogger.log("IWorkspace : " + workspace );
		IWorkspaceDescription workspaceDescription = workspace.getDescription();
		PluginLogger.log("IWorkspaceDescription : " + workspaceDescription );
		
		// --- Get the root path of the project
		IWorkspaceRoot root = workspace.getRoot();
		PluginLogger.log("IWorkspaceRoot : " + root );
		
//		IFile file = container.getFile(filePath);
//		String sFullPathFile = file.getFullPath().toString() ;
		
		// --- Build the file name
		String sFileName = sClassName + ".java" ;
		PluginLogger.log("File name = " + sFileName );

		Path filePath = new Path(sFileName);
		PluginLogger.log("File Path = " + filePath ); // MyClass.java

		// --- Build the file dir
		String sFileDir = getDestPackageDir(sSourceDir, sPackage);
		PluginLogger.log("File dir = " + sFileDir );
		
		Path containerPath = new Path(sFileDir);
		PluginLogger.log("Container path = " + containerPath );
		
		//--- Find the FOLDER ( resource )
		IResource resource = root.findMember(containerPath);
		PluginLogger.log("Container resource : " + resource );			
		if ( resource.exists() )
		{
			PluginLogger.log("Resource '" + containerPath + "' exists ");			
		}
		else
		{
			PluginLogger.log("Resource '" + containerPath + "' doesn't exist ");						
		}
		IPath resourceFullPath = resource.getFullPath();
		PluginLogger.log("Resource full path : " + resourceFullPath );	
		
		//--- Get the container
		int iResourceType = resource.getType();
		PluginLogger.log("Resource type : " + iResourceType );						
		if ( iResourceType == IResource.FOLDER || iResourceType == IResource.PROJECT )
		{
			PluginLogger.log("Resource type is FOLDER or PROJECT" );									
			IContainer container = (IContainer) resource;
			PluginLogger.log("Container : " + container );
			
			//--- File to create
			fileGenerated = container.getFile(new Path(sFileName));
			
			//--- Create the file FOR TEST ...
			createFile( fileGenerated, getFileContent() );
			
			//--- Generate the file ...
			generateFile( fileGenerated, generator );

		}
		else
		{
			PluginLogger.log("Resource is NOT A FOLDER !" );		
			MsgBox.error("Target directory '" + resourceFullPath + "' \n"
					+ " is not a FOLDER or PROJECT ( type = " + iResourceType + ") " );
		}

		
		PluginLogger.log("================================" );
		
		return fileGenerated;
	}

	/**
	 * @param sDestDir
	 *            Project Name of the project
	 * @param sPackage
	 *            package Name of the package
	 * @return full path to the class which will generate
	 */
	//private
	private static String getDestPackageDir(String sDestDir, String sPackage) {
		// --- Replace . by / in the path
		String sPackageDir = sPackage.replace('.', '/');
		return sDestDir + "/" + sPackageDir;
	}

	public static IFile createFile( IContainer container, String sFileName ) 
	{
		IFile file = container.getFile(new Path(sFileName));
		if ( createFile( file, getFileContent()  ) == true )
		{
			return file ;
		}
		else
		{
			return null ;			
		}
	}
	
	
	public static boolean generateFile( IFile file, Generator generator ) 
	{
		if ( file == null )
		{
			MsgBox.error("generateFile() : file is null !");
		}
		if ( generator == null )
		{
			MsgBox.error("generateFile() : generator is null !");			
		}
		PluginLogger.log("generateFile( IFile ) : file name = " + file.getName());
		try {
			//Generator generator = new Generator( sTemplateFileName, new SysLogWriter() );
			
			InputStream is = generator.generateInMemory();
			createFile(file, is);
		} catch (GeneratorException e) {
			
			MsgBox.error("generateFile() : generator exception : \n" 
					+ "\n Class : " + e.getClass()
					+ "\n Message : " + e.getMessage()
					+ "\n Cause : " + e.getCause()
					);			
		}
		return true;
	}
	
	//public static boolean createFile( IContainer container, String sFileName, IProgressMonitor monitor) 
	public static boolean createFile( IFile file, InputStream stream ) 
	{
		PluginLogger.log("createFile( IFile ) : file name = " + file.getName());
		NullProgressMonitor monitor = new NullProgressMonitor();
		try {
//			InputStream stream = getFileContent();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (CoreException ex) {
			MsgBox.error("ERROR : Cannot create file " + file.getName() + "\n"
					+ "CoreException : \n" + ex.getMessage() );
		} catch (IOException ex) {
			MsgBox.error("ERROR : Cannot create file " + file.getName() + "\n"
					+ "IOException : \n" + ex.getMessage() );
		}
		return true;
	}
	
	/**
	 * ONLY FOR TEST 
	 * @return
	 */
	private static InputStream getFileContent() 
	{
		String contents =
			"/* My generated file */";
		return new ByteArrayInputStream(contents.getBytes());
	}

	public static boolean openFileEditor( final IFile file, Shell shell) 
	{		
		if ( file == null )
		{
			MsgBox.error("ERROR : openFileEditor : file parameter is null !"  );
			return false ;
		}
		if ( shell == null )
		{
			MsgBox.error("ERROR : openFileEditor : shell parameter is null !"  );						
			return false ;
		}
		
		PluginLogger.log("createFile( IFile ) : file name = " + file.getName());
		
//		IWorkbench workbench = PlatformUI.getWorkbench();
//		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//		ISelection selection = workbenchPage.getSelection();
		
		Display display = shell.getDisplay();
		
		Runnable runnable = new Runnable() 
		{
			public void run() 
			{
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					MsgBox.error("ERROR : openFileEditor : IDE.openEditor exception : \n"
							+ e.getMessage() );						
				}
			}
		};
		
		display.asyncExec( runnable );
		
		return true;
	}
	
	private static Status $statusOK = new Status(Status.OK, MyPlugin.getId(), Status.OK, "", null)  ;
	public static Status getStatusOK()
	{
		return $statusOK ;
	}

	public static Status getStatusError(String sErrorMsg)
	{
		return new Status(Status.ERROR, MyPlugin.getId(), Status.ERROR, sErrorMsg, null) ;
	}
	
}
