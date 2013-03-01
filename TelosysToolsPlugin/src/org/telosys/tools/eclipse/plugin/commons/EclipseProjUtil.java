package org.telosys.tools.eclipse.plugin.commons;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.telosys.tools.commons.StrUtil;


/**
 * Utility class to manage Eclipse PROJECT RESOURCES
 * 
 * 
 * @author Laurent Guerin
 *
 */
public class EclipseProjUtil {

	/**
	 * Static class : no constructor 
	 */
	private EclipseProjUtil() {
	}
	
	private static void log(String s) 
	{
		PluginLogger.log( EclipseProjUtil.class.getName() + " : " + s );
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns the "full path" of the given project ( i.e. "C:/aaa/bbb/workspace/project" )
	 * 
	 * @param project
	 * @return
	 */
	public static String getProjectDir( IProject project ) 
	{
		if ( project != null )
		{
			IPath path = project.getLocation();
			if ( path != null )
			{
				return path.toString();			
			}
			else
			{
				MsgBox.error("getProjectDir() : Project location is null " );
				return null ;
			}
		}
		else
		{
			MsgBox.error("getProjectDir() : Project is null " );
			return null ;
		}
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns the project resource for the given relative path <br>
	 * or null if the resource doesn't exist
	 * @param project
	 * @param sPath ( '/aaa/bbb' or 'aaa/bbb' in the project ) 
	 * @return the resource ( or null if the resource doesn't exists )
	 */
	public static IResource getResource(IProject project, String sPath) 
	{
		log("getResource(String : '" + sPath + "')");	
		IResource resource = getResource( project, new Path(sPath) );
		log("getResource(String : '" + sPath + "') : return " + resource );
		return resource ;
	}
	
	/**
	 * Returns the project resource for the given relative path <br>
	 * or null if the resource doesn't exist
	 * @param project
	 * @param path ( '/aaa/bbb' or 'aaa/bbb' in the project ) 
	 * @return the resource ( or null if the resource doesn't exists )
	 */
	public static IResource getResource(IProject project, Path path) 
	{
		log("getResource(Path : '" + path + "')");	
		IResource resource = project.getFile(path) ; // Path implements IPath
		if ( ! resource.exists() )
		{
			resource = null ;
		}
		log("getResource(Path : '" + path + "') : return " + resource );
		return resource ;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns the project absolute path relative to the containing workspace <br>
	 * Example : returns "/myproject/aa/bb/file" for "aa/bb/file"
	 * @param project
	 * @param sPath the path in the project 
	 * @return
	 */
	public static String getAbsolutePathInWorkspace(IProject project, String sPath) 
	{
		IFile file = project.getFile( new Path(sPath) ) ; 
		IPath p = file.getFullPath();
		return p.toString() ;
	}

	/**
	 * Returns the filesystem absolute path <br>
	 * Example : returns "D:/aaa/bbbb/workspace/myproject/aa/bb/file" for "aa/bb/file"
	 * @param project
	 * @param sPath
	 * @return
	 */
	public static String getAbsolutePathInFileSystem(IProject project, String sPath) 
	{
		IResource resource = project.getFile( new Path(sPath) ) ;
		IPath ipath = resource.getLocation(); // OS Path ( D:/aaa/bbbb/workspace/myproject/aa/bb/file )
		File file = ipath.toFile();
		return file.getAbsolutePath();
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Returns a standard file instance for the given relative path in the project <br>
	 * or null if the file doesn't exist
	 * @param project
	 * @param sPath ( '/aaa/bbb' or 'aaa/bbb' ) 
	 * @return the file, or null 
	 */
	public static File getResourceAsFile(IProject project, String sPath) 
	{
		log("getResourceAsFile(String : '" + sPath + "')");	
		File file = getResourceAsFile( project, new Path(sPath) );
		log("getResourceAsFile(String : '" + sPath + "') : return " + file );
		return file ;
	}
	
	/**
	 * Returns a standard file instance for the given relative path in the project <br>
	 * or null if the file doesn't exist
	 * @param project
	 * @param path ( '/aaa/bbb' or 'aaa/bbb' ) 
	 * @return 
	 */
	private static File getResourceAsFile(IProject project, Path path) 
	{
		File file = null ;
		log("getResourceAsFile(Path : '" + path + "')");	
		IResource resource = getResource(project, path);
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
		log("getResourceAsFile(Path : '" + path + "') : return " + file );
		return file ;
	}
	
	//----------------------------------------------------------------------------------
	/**
	 * Refresh the given project resource 
	 * @param project
	 * @param sPath resource path ( '/aaa/bbb' or 'aaa/bbb' ) 
	 */
	public static void refreshResource(IProject project, String sPath) 
	{
		refreshResource( project, new Path(sPath) );
	}
	
	/**
	 * Refresh the given project resource 
	 * @param project
	 * @param path resource path ( '/aaa/bbb' or 'aaa/bbb' ) 
	 */
	public static void refreshResource(IProject project, Path path) 
	{
//		IResource resource = getResource(project, path);
//		if ( resource != null )
//		{
//			try {
//				resource.refreshLocal(IResource.DEPTH_ZERO, null);
//			} catch (CoreException e) {
//				MsgBox.error("Cannot refresh file '" + path + "' !\n" 
//						+ "CoreException \n"
//						+ e.getMessage() );
//			}
//		}
//		else
//		{
//			MsgBox.error("Cannot refresh file '" + path + "' !\n" 
//					+ "File not found." );
//		}

		IResource resource = project.getFile( path ) ;
		try {
			resource.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			MsgBox.error("Cannot refresh file '" + path + "' !\n" 
			+ "CoreException \n" + e.getMessage() );
		}
		
	}
	
    //------------------------------------------------------------------------------------------------
    /**
     * Return the "Eclipse Java Model" for the "Eclipse Workspace"
     * @return
     */
    public static IJavaModel getJavaModel()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if ( workspace != null )
        {
            IJavaModel javaModel = JavaCore.create(workspace.getRoot());
            return javaModel;
        }
        else
        {
        	MsgBox.error("Cannot get JavaModel because workspace is null !");
        	return null ;
        }
    }
    
    //------------------------------------------------------------------------------------------------
    /**
     * Return the "Eclipse Java Project" (interface) for a given project name
     * @param sProjectName
     * @return IJavaProject object 
     */
    public static IJavaProject getJavaProject(String sProjectName)
    {
        IJavaModel javaModel = getJavaModel() ;
        if ( javaModel != null )
        {
            return javaModel.getJavaProject(sProjectName);
        }
        else
        {
        	MsgBox.error("Cannot get JavaProject because JavaModel is null !");
        	return null ;
        }
    }

    //------------------------------------------------------------------------------------------------
    public static IJavaProject getJavaProject(IProject p)
    {
    	return JavaCore.create(p);
    }
	
    //------------------------------------------------------------------------------------------------
    /**
     * Returns the list of JARs for the current project 
     * @return array of JAR URLs 
     */
    public static String[] getClassPath( IProject project )
    {
    	IJavaProject javaProject = getJavaProject(project);
        return getClassPath( javaProject );
    }

    //------------------------------------------------------------------------------------------------
    /**
     * Returns the list of JAR URLs find in the Java Project PATH
     * 
     * @return
     */
    public static String[] getClassPath( IJavaProject javaProject )
    {
        String[] urls = null;
        
        try 
        {
            List<URL> maListe = getProjectClassPathURLs(javaProject);
            int nbUrls = maListe.size();
            urls = new String[nbUrls];
            int cpt = 0;
            for ( int j = 0 ; j < nbUrls ; j++ )
            {
                URL url = (URL)maListe.get(j);
                File f = new File(url.getFile());
                urls[cpt] = f.getAbsolutePath();
                cpt++;
            }
        }
        catch (JavaModelException e) 
        {
			PluginLogger.error("JavaModelException : \n" + e.getMessage() );
			MsgBox.error("JavaModelException : \n" + e.getMessage() );
            urls = null;
        }

        return urls;            
    }


    //------------------------------------------------------------------------------------------------
    /**
     * Permet de créer la liste des URL du classpath pour un projet Eclipse
     * 
     * @param project : l'objet project sur lequel on souhaite récupérer le classpath
     * @return une liste d'objets URL
     * @throws JavaModelException
     * @throws MalformedURLException
     */
    private static List<URL> getProjectClassPathURLs(IJavaProject project) throws JavaModelException //, MalformedURLException 
    {
        List<URL> paths = new ArrayList<URL>();
    
        IClasspathEntry classpathEntries[] = project.getResolvedClasspath(false);
        for (int i = 0; i < classpathEntries.length; i++) 
        {
            IClasspathEntry entry = classpathEntries[i];
            //----- New 
            IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(entry.getPath());
            if (res != null) 
            {
            	PluginLogger.log("Project resource : '"+entry.getPath()+"'");
            	java.io.File file = new java.io.File( res.getLocation().toPortableString() ) ;
            	//paths.add( file.toURL() );
            	paths.add( getURL(file) );
            } 
            else 
            {
            	//--- External resource : Java JRE, JAR added with "Add external JAR"
            	PluginLogger.log("External resource : '"+entry.getPath()+"'");
            	java.io.File file = entry.getPath().toFile() ;
            	//paths.add( file.toURL() );
            	paths.add( getURL(file) );
            }
            
        }
        return paths;
    }
    
    private static URL getURL(File file) //throws MalformedURLException
    {
        URI uri = file.toURI();
    	URL url;
		try {
			url = uri.toURL();
	    	return url ;
		} catch (MalformedURLException e) {
			PluginLogger.error("MalformedURLException : \n" 
					+ e.getMessage() + "\n"
					+ "File path : " + file.getPath() );
			MsgBox.error("MalformedURLException : \n" + file.getPath() );
		}
    	return null ;
    }

    /**
     * Returns all the source folders for the given project 
     * @param project
     * @return
     */
    public static String[] getSrcFolders(IProject project) {
    	LinkedList<String> srcFolders = new LinkedList<String>();
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry[] entries;
		try {
			entries = javaProject.getRawClasspath();
		} catch (Exception e) {
			MsgBox.error("Cannot get JavaProject raw class path !");
			return new String[0];
		}
        for (IClasspathEntry classPathEntry : entries ) {
            if (classPathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                IPath path = classPathEntry.getPath();
                IFolder srcFolder = project.getWorkspace().getRoot().getFolder(path);
    			IPath relativePath = srcFolder.getProjectRelativePath() ; // "src", "src/main/java", ... 
    			srcFolders.add(relativePath.toString());
            }
        }        
        return srcFolders.toArray(new String[0]);
    }	
    
    /**
     * Returns true if the folder exists
     * @param project
     * @param folderName
     * @return
     */
    public static boolean folderExists(IProject project, String folderName ) {
        IFolder folder = project.getFolder(folderName);
        return folder.exists() ;
    }
    
//    /**
//     * Tries to create the given folder 
//     * @param project
//     * @param folderName
//     * @return true if created, false if already exists (or if an error occurs )
//     */
//    public static boolean createFolder(IProject project, String folderName ) {
//    	boolean created = false ;
//        IFolder folder = project.getFolder(folderName);
//        if (!folder.exists())  {
//    		try {
//                folder.create(IResource.NONE, true, null);
//                created = true ;
//    		} catch (CoreException e) {
//    			MsgBox.error("Cannot create folder '" + folderName + "' !\n" 
//    					+ "CoreException \n"
//    					+ e.getMessage() );
//    		}
//        }
//        return created ;
//    }
    
    /**
     * Tries to create the given folder ( with all sub-folders if any )
     * @param project
     * @param folderName
     * @return true if created, false if already exists (or if an error occurs )
     */
    public static boolean createFolder(IProject project, String folderName ) {
        if ( project.getFolder(folderName).exists() )  {
        	// Already exists
        	return false ;
        }
        else {
        	StringBuffer sb = new StringBuffer();
        	String[] parts = StrUtil.split(folderName, '/') ;
        	for ( int i = 0 ; i < parts.length ; i++ ) {
        		if ( i > 0 ) {
        			sb.append("/");
        		}
        		sb.append(parts[i]);
                IFolder folder = project.getFolder( sb.toString() );
                if (!folder.exists())  {
            		try {
                        folder.create(IResource.NONE, true, null);
            		} catch (CoreException e) {
            			MsgBox.error("Cannot create folder '" + folder.getName() + "' !\n" 
            					+ "CoreException \n"
            					+ e.getMessage() );
            			return false ;
            		}
                }
        	}
            return true ;
        }
    }
}
