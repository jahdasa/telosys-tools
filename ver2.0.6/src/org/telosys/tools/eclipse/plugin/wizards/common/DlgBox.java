package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;

/**
 * @author Laurent GUERIN
 *  
 */
public class DlgBox 
{
	//----------------------------------------------------------------------------------
	// 
	//----------------------------------------------------------------------------------
	public static IPackageFragment selectPackage(Shell shell, IJavaProject javaProject) 
	{
		try {
			SelectionDialog dialog = JavaUI.createPackageDialog(shell,
					javaProject,
					IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS);
			dialog.setTitle("Package selection");
			dialog.setMessage("Choose a package :");

			if (dialog.open() == Window.OK) {
				Object[] result = dialog.getResult();
				if ( result != null )
				{
					if ( result.length > 0 )
					{
						if ( result[0] instanceof IPackageFragment )
						{
							return (IPackageFragment) result[0] ;
						}
						else
						{
							MsgBox.error("Invalid result type ( IPackageFragment expected )\n");							
						}
					}
				}
			}
		} catch (Exception ex) {
			MsgBox
					.error("Cannot browse packages : \n"
							+ ex.getMessage() );
		}
		return null ;
	}
	
	//----------------------------------------------------------------------------------
	public static boolean isSourcePackageFragmentRoot(IPackageFragmentRoot pfr ) 
	{
		if ( pfr.isArchive() ) return false ; // binary archive ( JAR or ZIP file )
		if ( pfr.isExternal() ) return false ; // external to the workbench
		return true ;
	}
	//----------------------------------------------------------------------------------
	public static IPackageFragmentRoot[] getSourcePackageFragmentRoots(IJavaProject javaProject) throws JavaModelException
	{
		//--- Get all the "PackageFragmentRoots" ( SRC dir, JAR files, ZIP files, ... )
		IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
		int n = 0 ;
		for ( int i = 0 ; i < packageFragmentRoots.length ; i++)
		{
			if ( isSourcePackageFragmentRoot(packageFragmentRoots[i]) ) n++ ;				
		}
		IPackageFragmentRoot[] sourcePFR = new IPackageFragmentRoot[n];
		n = 0 ;
		for ( int i = 0 ; i < packageFragmentRoots.length ; i++)
		{
			if ( isSourcePackageFragmentRoot(packageFragmentRoots[i]) ) sourcePFR[n++] = packageFragmentRoots[i] ;				
		}
		return sourcePFR;
	}
	
	//----------------------------------------------------------------------------------
	public static IJavaSearchScope getJavaSearchScope(IJavaProject javaProject) throws JavaModelException
	{
		
//		IJavaElement javaElements[] = new IJavaElement[1];
//		javaElements[0] = javaProject ;
		IJavaElement[] javaElements = getSourcePackageFragmentRoots(javaProject);
		return SearchEngine.createJavaSearchScope(javaElements);
	}
	
	//----------------------------------------------------------------------------------
	// 
	//----------------------------------------------------------------------------------
	public static IType selectClass(Shell shell, IJavaProject javaProject) 
	{
		return selectType(shell, javaProject, "", IJavaElementSearchConstants.CONSIDER_CLASSES );
	}
	//----------------------------------------------------------------------------------
	public static IType selectClass(Shell shell, IJavaProject javaProject, String sFilter) 
	{
		return selectType(shell, javaProject, sFilter, IJavaElementSearchConstants.CONSIDER_CLASSES ) ;
	}
	//----------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------
	public static IType selectInterface(Shell shell, IJavaProject javaProject) 
	{
		return selectType(shell, javaProject, "", IJavaElementSearchConstants.CONSIDER_INTERFACES ) ;
	}
	//----------------------------------------------------------------------------------
	public static IType selectInterface(Shell shell, IJavaProject javaProject, String sFilter) 
	{
		return selectType(shell, javaProject, sFilter, IJavaElementSearchConstants.CONSIDER_INTERFACES ) ;
	}
	
	//----------------------------------------------------------------------------------
	public static IType selectType(Shell shell, IJavaProject javaProject, String sFilter, int style) 
	{		
		try {
			ProgressMonitorDialog pm = new ProgressMonitorDialog(shell);
			IJavaSearchScope scope = getJavaSearchScope(javaProject) ;
			SelectionDialog dialog = JavaUI.createTypeDialog(
					shell, 
					pm,
					scope,
					style, 
					false,
					sFilter );
			String sTitle = "Class/Interface selection";
			if ( style == IJavaElementSearchConstants.CONSIDER_INTERFACES )
			{
				sTitle = "Interface selection";
			}
			if ( style == IJavaElementSearchConstants.CONSIDER_CLASSES )
			{
				sTitle = "Class selection";
			}
			dialog.setTitle(sTitle);
			dialog.setMessage("Filter :");

			if (dialog.open() == Window.OK) {
				Object[] result = dialog.getResult();
				if ( result != null )
				{
					if ( result.length > 0 )
					{
						if ( result[0] instanceof IType )
						{
							return (IType) result[0] ;
						}
						else
						{
							MsgBox.error("Invalid result type ( IType expected )\n");							
						}
					}
				}
			}
		} catch (Exception ex) {
			MsgBox.error("Cannot browse classes : \n"
							+ ex.getMessage() );
		}
		return null ;
	}
}