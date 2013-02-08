package org.telosys.tools.eclipse.plugin.commons;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Utility class to manage WORKSPACE RESOURCES
 * 
 * Conversion to standard Java classes :
 * . IPath     : File file = path.toFile()
 * . IResource : File file = res.getLocation().toFile()
 * . IFolder   : File file = folder.getLocation().toFile() 
 * 
 * @author Laurent Guerin
 *
 */
public class EclipseWksUtil {

	/**
	 * Static class : no constructor 
	 */
	private EclipseWksUtil() {
	}
	
	private static void log(String s) 
	{
		PluginLogger.log( EclipseWksUtil.class.getName() + " : " + s );
	}
	
	/**
	 * @return the current workspace
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/**
	 * @return the current workspace root
	 */
	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns the full path for the given resource path in the workspace <br>
	 * 
	 * @param sPath ( 'myproject/folder/xxx' )
	 * @return the full path if given resource exists, else null ( '/myproject/folder/xxx' )
	 */
	public static IPath getFullPath(String sPath) 
	{
		log("getFullPath(String : '" + sPath + "')");	
		IPath p = getFullPath( new Path(sPath) );
		log("getFullPath(String : '" + sPath + "') : return '" + p + "'");	
		return p ;
	}
	
	/**
	 * Returns the full path for the given resource path in the workspace <br>
	 * 
	 * @param path the resource path in the workspace ( 'myproject/folder/xxx' )
	 * @return the full path if given resource exists, else null ( '/myproject/folder/xxx' )
	 */
	public static IPath getFullPath(Path path) 
	{
		IPath p = null ;
		log("getFullPath(Path : '" + path + "')");	
		IResource resource = getWorkspaceRoot().findMember(path);
		if ( resource == null )
		{
			MsgBox.error("Cannot find '" + path + "' in the workspace !");
			p = null ;
		}
		else
		{
			if ( resource.exists() )
			{
				log("Resource '" + path + "' exists ");	
				p = resource.getFullPath();
			}
			else
			{
				log("Resource '" + path + "' doesn't exist ");						
				p = null ;
			}
		}
		log("getFullPath(Path : '" + path + "') : return '" + p + "'");	
		return p ;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns the workspace resource for the given workspace path <br>
	 * or null if the resource doesn't exist
	 * @param sPath ( 'myproject/aaa/bbb' or '/myproject/aaa/bbb' ) 
	 * @return
	 */
	public static IResource getResource(String sPath) 
	{
		log("getResource(String : '" + sPath + "')");	
		IResource resource = getResource( new Path(sPath) );
		log("getResource(String : '" + sPath + "') : return " + resource );
		return resource ;
	}
	
	/**
	 * Returns the workspace resource for the given workspace path <br>
	 * or null if the resource doesn't exist
	 * @param path ( 'myproject/aaa/bbb' or '/myproject/aaa/bbb' ) 
	 * @return
	 */
	public static IResource getResource(Path path) 
	{
		log("getResource(Path : '" + path + "')");	
		IResource resource = getWorkspaceRoot().findMember(path);
		log("getResource(Path : '" + path + "') : return " + resource );
		return resource ;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns a standard file instance for the given workspace path <br>
	 * or null if the file doesn't exist
	 * @param sPath ( 'myproject/aaa/bbb' or '/myproject/aaa/bbb' ) 
	 * @return
	 */
	public static File getResourceAsFile(String sPath) 
	{
		log("getResourceAsFile(String : '" + sPath + "')");	
		File file = getResourceAsFile( new Path(sPath) );
		log("getResourceAsFile(String : '" + sPath + "') : return " + file );
		return file ;
	}
	
	/**
	 * Returns a standard file instance for the given workspace path <br>
	 * or null if the file doesn't exist
	 * @param path ( 'myproject/aaa/bbb' or '/myproject/aaa/bbb' ) 
	 * @return 
	 */
	public static File getResourceAsFile(Path path) 
	{
		File file = null ;
		log("getResourceAsFile(Path : '" + path + "')");	
		IResource resource = getResource(path);
		if ( resource != null )
		{
			log("getResourceAsFile() : resource exists ? " + resource.exists() );	
			log("getResourceAsFile() : resource getLocation() : " + resource.getLocation() );	
			log("getResourceAsFile() : resource getFullPath() : " + resource.getFullPath() );	
			IPath ipath = resource.getLocation(); // OS Path ( D:/aaa/bbbb/workspace/myproject/aa/bb/file )
			//IPath ipath = resource.getFullPath();
			file = ipath.toFile();
			if ( ! file.exists() ) // never happends
			{
				file = null ;
			}
		}
		else {
			log("Cannot get resource : getResource('" + path + "') return null ");	
		}
		log("getResourceAsFile(Path : '" + path + "') : return " + file );
		return file ;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Creates the given folder in the current workspace root
	 * @param sFolderPath
	 * @return
	 */
	public static IFolder createFolder(String sFolderPath)
	{
		return createFolder( new Path(sFolderPath) );
	}

	/**
	 * Creates the given folder in the current workspace root
	 * @param path : absolute path in the WORKSPACE including the project folder ( "myproject/src", "myproject/doc/foo" ) 
	 * @return
	 */
	public static IFolder createFolder(IPath path)
	{
		log("createFolder( IPath ) : path = " + path );

		IFolder folder = getWorkspaceRoot().getFolder(path);
		IContainer parent = folder.getParent();
		if ( parent != null )
		{
			log("createFolder( IPath ) : parent path = " + parent.getFullPath() );
			//--- Create parent folder first if necessary (recursivly)
			if ( parent instanceof IFolder && !parent.exists()) 
			{
				createFolder(parent.getFullPath());
			} 
			//--- Create the folder itself
			try {
				folder.create(true, true, null);
			} catch (CoreException e) {
				MsgBox.error("Cannot create folder '" + folder.getFullPath() + "'", e );
				return null ;
			}
			return folder ;
		}
		else
		{
			MsgBox.error("Folder '" + folder.getFullPath() + "' parent is null " );
			return null ;
		}
	}
	
	//----------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------
	/**
	 * Returns the Eclipse workspace "IFile" object for the given
	 * standard "File" object
	 * Returns null if the given file is not under the location of the workspace
	 * @param file
	 * @return 
	 */
	public static IFile toIFile(File file)
	{
		log("toIFile( File ) : file = " + file.getAbsolutePath() );
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		if ( root != null )
		{
			//String sAbsolutePath = file.getPath();
			String sAbsolutePath = file.getAbsolutePath();
			IPath path = new Path( sAbsolutePath );
			/*
			 * getFileForLocation(path) :
			 *  The path should be absolute; a relative path will be treated as absolute. 
			 *  The path segments need not be valid names. 
			 *  The resulting file need not exist in the workspace.
			 *  This method returns null when the given file system location 
			 *  is not under the location of any existing project in the workspace.
			 */
			IFile iFile = root.getFileForLocation(path);
			if ( iFile != null )
			{
				PluginLogger.log("");
			}
			else
			{
				PluginLogger.log("");
			}
			return iFile ;
		}
		else
		{
			MsgBox.error("toIFile(file) : Cannot get workspace root !" );
			return null ;
		}
	}
	
	public static File toFile(IFile iFile)
	{
		log("toFile( IFile iFile ) : iFile.getLocationURI() = " + iFile.getLocationURI() );
    	URI uri = iFile.getLocationURI();
    	File file = new File(uri);
		log("toFile( IFile iFile ) : return : file.getAbsolutePath() = " + file.getAbsolutePath() );
    	return file ;
	}
	
	//----------------------------------------------------------------------------------
	public static void refresh(File file)
	{
		log("refresh( File ) " );
		if ( null == file ) {
			MsgBox.error("refresh(File) : parameter is null !" );
			return ;
		}
		IFile iFile = toIFile(file);
		if ( null == iFile )
		{
			MsgBox.error("refresh(File) : cannot convert File to IFile !"
					+ "\n " + file.getAbsolutePath() );
			return ;
		}
		refresh(iFile);
	}
	//----------------------------------------------------------------------------------
	public static void refresh(IResource resource)
	{
		log("refresh( IResource )..."  );
		if ( resource == null )
		{
			MsgBox.error("refresh(resource) : parameter is null !" );
		}
		log("refresh( IResource ) : resource = " + resource.getFullPath() );
		try {
			resource.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			MsgBox.error("Cannot refresh resource '" + resource, e );
		}
	}
}
