package org.telosys.tools.eclipse.plugin.config;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.generator.target.TargetsFile;

/**
 * Telosys Tools project configuration <br>
 * This class provides the configuration loaded from the properties file <br>
 * for a project of the current workspace
 *   
 * @author Laurent GUERIN
 *
 */
public class ProjectConfig 
{
	public final static String TEMPLATES_CFG = "templates.cfg" ;

	private final static List<TargetDefinition> VOID_LIST_OF_TARGETS = new LinkedList<TargetDefinition>() ;
		
	//----------------------------------------------------------------------------------------
	private String _sProjectName        = null ; 
	private String _sProjectLocation    = null ; 
	private String _sWorkspaceLocation  = null ;
	
	//----------------------------------------------------------------------------------------
	private final TelosysToolsCfg _telosysToolsCfg ;
	
	//----------------------------------------------------------------------------------------	
    /**
     * Constructor 
     * @param project the project associated with the given properties 
     */
    public ProjectConfig ( IProject project )
    {
    	if ( project != null) {
        	
    		_sProjectName = project.getName() ; 
        	
//        	//--- Project location
//        	if ( project.getLocation() != null ) {
//            	_sProjectLocation = project.getLocation().toOSString() ;     	
//        	}
//        	else {
//        		MsgBox.error("ProjectConfig constructor : project location is null");
//        		_sProjectLocation = "unknown" ; 
//        	}
//
//        	//--- Workspace location
//        	IWorkspace wks = project.getWorkspace();
//        	IWorkspaceRoot wksRoot = wks.getRoot();
//        	IPath wksLocation = wksRoot.getLocation();
//        	if ( wksLocation != null )
//        	{
//        		_sWorkspaceLocation = wksLocation.toOSString() ;
//        	}
//        	else
//        	{
//        		MsgBox.error("ProjectConfig constructor : workspace location is null");
//        		_sWorkspaceLocation = "unknown" ; 
//        	}
        	_sProjectLocation = getProjectLocation(project);
        	_sWorkspaceLocation = getWorkspaceLocation(project);
    	}
    	else {
    		MsgBox.error("ProjectConfig constructor : IProject is null");
        	_sProjectName = "unknown" ; 
        	_sProjectLocation = "unknown";
        	_sWorkspaceLocation = "unknown";
    	}
    	_telosysToolsCfg = new TelosysToolsCfg (_sProjectLocation );
    }
    
	public ProjectConfig (IProject project, TelosysToolsCfg cfg  )
	{
    	if ( project != null) {
    		_sProjectName = project.getName() ; 
        	_sProjectLocation = getProjectLocation(project);
        	_sWorkspaceLocation = getWorkspaceLocation(project);
    	}
    	else {
    		MsgBox.error("ProjectConfig constructor : IProject is null");
        	_sProjectName = "unknown" ; 
        	_sProjectLocation = "unknown";
        	_sWorkspaceLocation = "unknown";
    	}
		if ( cfg == null) {
			MsgBox.error("ProjectConfig constructor : TelosysToolsCfg is null");
        	_telosysToolsCfg = new TelosysToolsCfg (_sProjectLocation );
		}
		else {
			_telosysToolsCfg = cfg ;
		}
    }
	
	private String getProjectLocation ( IProject project )
	{
    	if ( project.getLocation() != null ) {
        	return project.getLocation().toOSString() ;     	
    	}
    	else {
    		MsgBox.error("ProjectConfig constructor : project location is null");
    		return "unknown" ; 
    	}
	}
	
	private String getWorkspaceLocation ( IProject project )
	{
    	IWorkspace wks = project.getWorkspace();
    	IWorkspaceRoot wksRoot = wks.getRoot();
    	IPath wksLocation = wksRoot.getLocation();
    	if ( wksLocation != null )
    	{
    		return wksLocation.toOSString() ;
    	}
    	else
    	{
    		MsgBox.error("ProjectConfig constructor : workspace location is null");
    		return "unknown" ; 
    	}
	}
	
	//------------------------------------------------------------------------------------------------------
    public String getProjectName()
	{
    	return _sProjectName ;
	}
    
	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the OS file system project location
     * @return
     */
    public String getProjectFolder()
	{
    	return _sProjectLocation ;
	}
    
	//------------------------------------------------------------------------------------------------------
    public String getWorkspaceFolder()
	{
    	return _sWorkspaceLocation ;
	}

    //------------------------------------------------------------------------------------------------------
    public TelosysToolsCfg getTelosysToolsCfg()
	{
    	return _telosysToolsCfg ;
	}

//	//==============================================================================
//    // Masks  ( used by WIZARDS )
//    //==============================================================================
//	public String getScreenDataClassMask() 
//	{
//		return _sScreenDataClassMask ;
//	}
//	
//	public String getVOClassMask() 
//	{
//		return _sVOClassMask ;
//	}
	
	
    //==============================================================================
    // Specific Templates  
    //==============================================================================
    /**
     * Reload the specific templates file "templates.cfg"
     */
    public void refreshTemplates()
	{
    	// Removed in v 2.0.7
    	//_templates = loadTemplates();
	}

	//------------------------------------------------------------------------------------------------------
    /**
     * Returns the list of targets defined in "templates.cfg" or null if none
     * @return
     */
    public List<TargetDefinition> getTemplates(String bundleName)
	{
    	//return _templates ;
    	return loadTemplates(bundleName) ; // v 2.0.7
	}

	//------------------------------------------------------------------------------------------------------
	private List<TargetDefinition> loadTemplates(String bundleName)
	{
		// Initial templates folder 
		//String sTemplatesFolder = getTemplatesFolderFullPath();
		String sTemplatesFolder = _telosysToolsCfg.getTemplatesFolderAbsolutePath();
		// Add bundle folder if any  ( v 2.0.7 )
		if ( bundleName != null ) {
			String bundleFolder = bundleName.trim() ;
			if ( bundleFolder.length() > 0 ) {
				sTemplatesFolder = FileUtil.buildFilePath(sTemplatesFolder, bundleFolder );
			}
		}
		// templates.cfg full path  
		String sFile = FileUtil.buildFilePath(sTemplatesFolder, TEMPLATES_CFG );
		
		TargetsFile targetsFile = new TargetsFile(sFile) ;
		if ( targetsFile.exists() ) {
			//--- Try to load the targets 
			List<TargetDefinition> list ;
			try {
				list = targetsFile.load();
			} catch (GeneratorException e) {
				MsgBox.error("Cannot load targets definition from file : \n" + sFile + "\n Exception : " + e.getMessage() );
				list = VOID_LIST_OF_TARGETS ;
			}
			return list ;
		}
		else {
			return VOID_LIST_OF_TARGETS ;
		}
	}	

	
}
