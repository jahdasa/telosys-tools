package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.telosys.tools.repository.model.Column;


/**
 * Value for "SpecialDialogCellEditor" ( for column "Special" ) 
 *  
 * @author Laurent GUERIN
 *
 */
public class SpecialValueForNumber extends SpecialValue 
{
//	private boolean _initialNotNull ;
	
	private String  _initialMinValue ;
	private String  _initialMaxValue ;
	
	private String  _initialFormat  ;
	
	public SpecialValueForNumber( Column modelColumn ) 
	{
		super(modelColumn) ;
		
		//--- Keep initial values to know if something has changed, and to cancel changes
//		_initialNotNull   = _modelColumn.getJavaNotNull();
		
		_initialMinValue  =  _modelColumn.getMinValue()  ;
		_initialMaxValue  =  _modelColumn.getMaxValue()  ;
		
		_initialFormat    =  _modelColumn.getFormat()  ;
	}

//	private void log(String s)
//	{
//		PluginLogger.log( this, s );
//	}
//	//---------------------------------------------------------------------
//	public boolean isNotNull()
//	{
//		return _modelColumn.getJavaNotNull() ;
//	}
//	public void setNotNull(boolean b)
//	{
//		log("setNotNull(" + b +")");
//		_modelColumn.setJavaNotNull(b);
//	}

	//---------------------------------------------------------------------
	public String getMinValue()
	{
		return emptyIfNull( _modelColumn.getMinValue() )  ;
	}
	public void setMinValue(String s)
	{
		log("setMin(" + s +")");
		_modelColumn.setMinValue(s);
	}
	
	//---------------------------------------------------------------------
	public String getMaxValue()
	{
		return emptyIfNull( _modelColumn.getMaxValue() ) ;
	}
	public void setMaxValue(String s)
	{
		log("setMax(" + s +")");
		_modelColumn.setMaxValue(s);
	}
	
	//---------------------------------------------------------------------
	public String getFormat()
	{
		return emptyIfNull ( _modelColumn.getFormat() ) ;
	}
	public void setFormat(String s)
	{
		log("setFormat(" + s +")");
		_modelColumn.setFormat(s);
	}
	
//	//---------------------------------------------------------------------
//	public boolean isPrimitiveType()
//	{
//		return JavaTypeUtil.isPrimitiveType( _modelColumn.getJavaType() );
//	}
	//---------------------------------------------------------------------

	public boolean hasChanged()
	{
		if ( super.hasChanged() ) return true ;
//		if ( _initialNotNull != _modelColumn.getJavaNotNull() ) return true ;
		
		if ( ! sameValue ( _initialMinValue, _modelColumn.getMinValue() ) ) return true ;
		if ( ! sameValue ( _initialMaxValue, _modelColumn.getMaxValue() ) ) return true ;

		if ( ! sameValue ( _initialFormat,   _modelColumn.getFormat() ) ) return true ;

		return false ; // No change
	}

	public void cancelChanges()
	{
		super.cancelChanges();
//		_modelColumn.setJavaNotNull(_initialNotNull);
		
		_modelColumn.setMinValue(_initialMinValue);
		_modelColumn.setMaxValue(_initialMaxValue);
		
		_modelColumn.setFormat (_initialFormat );
	}
}
