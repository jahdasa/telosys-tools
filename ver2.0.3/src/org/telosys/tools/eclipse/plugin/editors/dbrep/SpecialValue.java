package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.telosys.tools.commons.StrUtil;
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
	
	//--- Initial values 
	private final boolean  _initialNotNull ;
	
	private final String   _initialDefaultValue ;

	private final String   _initialLabel ;

	private final String   _initialInputType ;


	//---------------------------------------------------------------------
	/**
	 * Constructor
	 * @param modelColumn
	 */
	protected SpecialValue( Column modelColumn ) 
	{
		_modelColumn    = modelColumn ;
		//--- Keep initial values 
		_initialNotNull      = _modelColumn.getJavaNotNull();
		_initialDefaultValue = _modelColumn.getJavaDefaultValue(); 
		_initialLabel        = _modelColumn.getLabel() ;
		_initialInputType    = _modelColumn.getInputType() ;
		
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
	// NOT NULL
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
	// DEFAULT VALUE
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
	// LABEL
	//---------------------------------------------------------------------
	public String getLabel()
	{
		return emptyIfNull( _modelColumn.getLabel() ) ;
	}
	public void setLabel(String v)
	{
		_modelColumn.setLabel(v);
	}

	//---------------------------------------------------------------------
	// INPUT TYPE
	//---------------------------------------------------------------------
	public String getInputType()
	{
		return emptyIfNull( _modelColumn.getInputType() ) ;
	}
	public void setInputType(String v)
	{
		_modelColumn.setInputType(v);
	}

	//---------------------------------------------------------------------
	/**
	 * Returns true if at least one of the values hold by this class has changed
	 * @return
	 */
	protected boolean hasChanged()
	{
		if ( _initialNotNull      != _modelColumn.getJavaNotNull() ) return true ;
		if ( StrUtil.different(_initialDefaultValue, _modelColumn.getJavaDefaultValue() ) ) return true ;
		if ( StrUtil.different(_initialLabel,        _modelColumn.getLabel()            ) ) return true ;
		if ( StrUtil.different(_initialInputType,    _modelColumn.getInputType()        ) ) return true ;
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
		_modelColumn.setLabel(_initialLabel );
		_modelColumn.setInputType(_initialInputType);
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
