package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.telosys.tools.eclipse.plugin.commons.JModel;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;

/**
 * @author Laurent GUERIN
 *
 */
public class WizardSelectionUtil {

	private final static String MSG = "Cannot get project directory ! \n";
	
	//----------------------------------------------------------------------------------------
	private final static Object getFirstElement( IStructuredSelection selection )
	{
		if ( selection == null )
		{
			MsgBox.info( MSG + "Selection is null !") ;
			return null ;
		}
		
		if ( selection.isEmpty() )
		{
			MsgBox.info( MSG + "Selection is empty !") ;
			return null ;
		}
		
		Object oFirstElement = selection.getFirstElement();
		if ( oFirstElement == null )
		{			
			MsgBox.error( MSG + "First element of the selection is null ! " );
			return null ;
		}
		//MsgBox.info( "First element of the selection : " + oFirstElement.getClass() );
		return oFirstElement ;
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns the first element of the selection as an instance of IResource
	 * @param selection
	 * @return
	 */
	public final static IResource getFirstResource( IStructuredSelection selection )
	{
		Object obj = getFirstElement(selection);
		if ( obj != null ) 
		{			
			if ( obj instanceof IResource )
			{
				return (IResource) obj ;
			}
			else if ( obj instanceof IJavaElement )
			{
				IJavaElement javaElement = (IJavaElement) obj ;
				return javaElement.getResource() ;
			}
			else
			{
				MsgBox.error( MSG + "First element of the selection is not IResource/IJavaElement ! " );
				return null ;
			}
		}
		return null ;
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns the 'resource project' for the first element of the given selection 
	 * @param selection
	 * @return the project as a resource  ( IProject / IResource )
	 */
	public final static IProject getResourceProject( IStructuredSelection selection )
	{
		IResource resource = getFirstResource(selection);
		if ( resource != null )
		{
			IProject project = resource.getProject(); 
			if ( project == null )
			{
				MsgBox.error( MSG + "Cannot get project from IResource ! " );
				return null ;
			}
			return project ;
		}
		return null ;
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns the 'resource project' for the first element of the given selection 
	 * @param selection
	 * @return
	 */
	public final static IJavaProject getJavaProject( IStructuredSelection selection )
	{
		IProject project = getResourceProject( selection ) ;
		if ( project != null )
		{
			return JModel.toJavaProject(project);			
		}
		return null ;
	}
	
	//----------------------------------------------------------------------------------------
	/**
	 * Returns the absolute path in the local file system for the project containing 
	 * the first element of the given selection 
	 * @param selection
	 * @return
	 */
	public final static String getProjectDir( IStructuredSelection selection )
	{
		IProject project = getResourceProject( selection );
		if ( project != null )
		{			
			IPath path = project.getLocation(); // absolute path in the local file system			
			if ( path == null )
			{
				MsgBox.error( MSG + "Cannot get project location for the first element of the selection ! " );
				return null ;
			}
			else
			{
				return path.toString();
			}
		}
		return null ;
	}
}
