package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.repository.model.Column;


/**
 * Value for "SpecialDialogCellEditor" ( for column "Special" ) 
 *  
 * @author Laurent GUERIN
 *
 */
public abstract class SpecialValue 
{

	protected Column _modelColumn = null ;
	
	private boolean  _initialNotNull = false ;
	
	private String   _initialDefaultValue = null ;


	//---------------------------------------------------------------------
	/**
	 * Constructor
	 * @param modelColumn
	 */
	protected SpecialValue( Column modelColumn ) 
	{
		_modelColumn    = modelColumn ;
		
		_initialNotNull      = _modelColumn.getJavaNotNull();
		
		_initialDefaultValue = _modelColumn.getJavaDefaultValue(); 
	}
	
	//---------------------------------------------------------------------
	protected void log(String s)
	{
		PluginLogger.log( this, s );
	}

	//---------------------------------------------------------------------
	public boolean isPrimitiveType()
	{
		return _modelColumn.isJavaPrimitiveType() ;
		//return JavaTypeUtil.isPrimitiveType( _modelColumn.getJavaType() );
	}

	//---------------------------------------------------------------------
	public String getJavaType() 
	{
		return _modelColumn.getJavaType() ;
	}
	
	//---------------------------------------------------------------------
	public boolean isNotNull()
	{
		return _modelColumn.getJavaNotNull() ;
	}
	public void setNotNull(boolean b)
	{
		//log("setNotNull(" + b +")");
		_modelColumn.setJavaNotNull(b);
	}
	
	//---------------------------------------------------------------------
	public String getDefaultValue()
	{
		//return _modelColumn.getJavaDefaultValue() ;
		return emptyIfNull( _modelColumn.getJavaDefaultValue() ) ;
	}
	public void setDefaultValue(String v)
	{
		_modelColumn.setJavaDefaultValue(v);
	}


	//---------------------------------------------------------------------
	/**
	 * Returns true if at least one of the values hold by this class has changed
	 * @return
	 */
	protected boolean hasChanged()
	{
		if ( _initialNotNull      != _modelColumn.getJavaNotNull() ) return true ;
		if ( _initialDefaultValue != _modelColumn.getJavaDefaultValue() ) return true ;
		return false ;
	}
	
	//---------------------------------------------------------------------
	/**
	 * Cancel the changes : restore the initial values hold by this class
	 */
	protected void cancelChanges()
	{
		_modelColumn.setJavaNotNull(_initialNotNull);
		_modelColumn.setJavaDefaultValue(_initialDefaultValue);
	}
	
	//---------------------------------------------------------------------
	/**
	 * Returns the given string or "" if the string is null
	 * @param s
	 * @return
	 */
	protected String emptyIfNull(String s)
	{
		return s != null ? s : "" ;
	}
	
	//---------------------------------------------------------------------
	/**
	 * Return true if the significant values are identical <br>
	 * A null string is considered as a void string
	 * @param s1
	 * @param s2
	 * @return
	 */
	protected boolean sameValue(String s1, String s2 )
	{
		String s1bis = emptyIfNull(s1);
		String s2bis = emptyIfNull(s2);
		return s1bis.equals(s2bis);
	}
	

	//---------------------------------------------------------------------
	public String toString() 
	{
		return _modelColumn.getSpecialTypeInfo();
	}	

}
