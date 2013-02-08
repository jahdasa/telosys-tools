package org.telosys.tools.generator.target;


/**
 * A generation target definition : <br>
 * what file to generate, where, with which template, etc...
 * 
 * @author Laurent Guerin
 *
 */
public class TargetDefinition 
{

	private final String  _sName  ;

	private final String  _sFile  ;
	
	private final String  _sFolder ;

	private final String  _sTemplate ;

	private final String   _sOnce ;
	private final boolean  _bOnce ;

	//-----------------------------------------------------------------------
	/**
	 * Constructor 
	 * @param name the target name (to be displayed in the UI)
	 * @param file the file to be generated ( ie "${BEANNAME}Data.java" )
	 * @param folder the folder where to generate the file ( ie "src/org/demo/screen/${BEANNAME_LC}" )
	 * @param template the template to use ( ie "vo_screen_data.vm" )
	 * @param once "ONCE" indicator : "1" for "ONCE", else standard entity target (can be VOID if none)
	 */
	public TargetDefinition(String name, String file, String folder, String template, String once ) 
	{
		super();
		_sName = name;
		_sFile = file;
		_sFolder = folder;
		_sTemplate = template;
		_sOnce = once ;
		_bOnce = getOnceFlag(once) ;
	}
	
	private boolean getOnceFlag(String sOnce) 
	{
		if ( sOnce != null ) {
			if ( sOnce.trim().equals("1") ) {
				return true ;
			}
		}
		return false ;
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns the target name ( the text displayed on the screen )
	 * @return
	 */
	public String getName()
	{
		return _sName ;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the target file name ( file to be generated )
	 * Can contains a generic variable BEANNAME, BEANNAME_UC, BEANNAME_LC
	 * if the target if generic and "applyBeanClassName" as not been called
	 * @return
	 */
	public String getFile()
	{
		return _sFile ;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the folder where to generate the file
	 * Can contains a generic variable BEANNAME, BEANNAME_UC, BEANNAME_LC
	 * if the target if generic and "applyBeanClassName" as not been called
	 * @return
	 */
	public String getFolder()
	{
		return _sFolder ;
	}
	
	//-----------------------------------------------------------------------
	public String getFullFileName()
	{
		if ( _sFolder.endsWith("/") || _sFolder.endsWith("\\") )
		{
			return _sFolder + _sFile ;
		}
		return _sFolder + "/" + _sFile ;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the template 
	 * @return
	 */
	public String getTemplate()
	{
		return _sTemplate ;
	}	
	
	/**
	 * Returns true if the target is for just "once" generation (not linked to an entity)
	 * @return
	 */
	public boolean isOnce()
	{
		return _bOnce ;
	}
	//-----------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return _sName + " : '" + _sFile  + "' in '" + _sFolder + "' ( " + _sTemplate + " " + _sOnce + " )" ;
	}
}
