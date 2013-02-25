package org.telosys.tools.eclipse.plugin.editors.dbconfig;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.XmlDbConfig;
import org.telosys.tools.eclipse.plugin.commons.EclipseWksUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.commons.TextWidgetLogger;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.eclipse.plugin.config.ProjectConfigManager;

/**
 * Main entry point for the editor <br>
 * This editor contains x pages : <br>
 * . 1 : the table view with the mapping table<br>
 * . 2 : the log view <br>
 * 
 */
public class DbConfigEditor extends FormEditor 
{
	/** The dirty flag : see isDirty() */
    private boolean           _dirty = false;
	private String            _fileName = "???" ;
	private IFile             _file = null ;
	private XmlDbConfig       _xmlDbConfig = null ;
	
	private TextWidgetLogger  _logger = new TextWidgetLogger() ;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {
		PluginLogger.log(this, "addPages()..." );
		DbConfigEditorPage1 page1 = new DbConfigEditorPage1(this, "DbConfigEditorPage1", " Database ");
		DbConfigEditorPage2 page2 = new DbConfigEditorPage2(this, "DbConfigEditorPage2", " Log viewer");
		try {
			addPage(page1);
			addPage(page2);
		} catch ( Exception e ) {
			MsgBox.error("addPage(page) Exception ", e);
		}
	}

	public boolean isDirty()
	{
		return _dirty;
	}

	public void setDirty()
	{
		setDirty(true);
	}
	
	private void setDirty(boolean flag)
	{
		_dirty = flag ;
		editorDirtyStateChanged(); // Notify the editor 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		PluginLogger.log(this, "doSave()..." );

		monitor.beginTask( "Saving the repository...", IProgressMonitor.UNKNOWN );

		try {
			_xmlDbConfig.save();
		} catch (TelosysToolsException e) {
			MsgBox.error("Cannot save XML file." + e);
		}
		
		setDirty(false);
		
//		IEditorInput input = getEditorInput();
//		IPersistableElement e = input.getPersistable();
		
		try {
			_file.refreshLocal(IResource.DEPTH_ZERO, monitor);
		} catch (CoreException e) {
			MsgBox.error("Cannot refresh the XML file after save\n Exception : " + e.getMessage() );
		}
		
		monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		PluginLogger.log(this, "doSaveAs()..." );
		// Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	/*
	 * Allow the "Save as"
	 */
	public boolean isSaveAsAllowed() {
		PluginLogger.log(this, "isSaveAsAllowed()..." );
		// Auto-generated method stub
		return false ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		// MsgBox.debug("DbConfig editor : init ");

		PluginLogger.log(this, "init(..,..)..." );
		PluginLogger.log(this, "init(..,..) : site id = '" + site.getId() + "'" );
		PluginLogger.log(this, "init(..,..) : input name = '" + input.getName() + "'" );
		setPartName(input.getName());
		
		_fileName = input.getName() ;

		if ( input instanceof IFileEditorInput )
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			_file = fileInput.getFile();

			PluginLogger.log(this, "init(..,..) : parse XML file '" + _file.getName() + "'" );
			try {
				//_xmlDbConfig = new XmlDbConfig(_file) ;
				_xmlDbConfig = new XmlDbConfig( EclipseWksUtil.toFile(_file) );
			} catch (TelosysToolsException e) {
				MsgBox.error("Cannot load repository : \nXML error \n" + e.getMessage() );
			}		
		}
		else // never happends
		{
			MsgBox.error("The editor input '" + input.getName() + "' is not a File ! ");
		}
	}
	
	public TelosysToolsLogger getLogger ()
	{		
		return _logger ;
	}

	public String getFileName ()
	{
		return _fileName ;
	}
	
	public IFile getFile ()
	{
		return _file ;
	}
	
	public IProject getProject ()
	{		
		return _file.getProject() ;
	}
	
	public ProjectConfig getProjectConfig ()
	{
		PluginLogger.log(this, "getProjectConfig()..." );
		return ProjectConfigManager.getProjectConfig( getProject() );
	}
	
//	public Document getXmlDocument ()
//	{
//		return _xmlDocument ;
//	}
	
	public XmlDbConfig getXmlDbConfig()
	{
		return _xmlDbConfig ;
	}

	public TextWidgetLogger getTextWidgetLogger()
	{
		return _logger ;
	}

	public void addPageChangedListener(IPageChangedListener listener) {
		// TODO Auto-generated method stub
		super.addPageChangedListener(listener);
	}

}