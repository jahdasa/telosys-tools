package org.telosys.tools.eclipse.plugin.editors.dbrep;

import org.telosys.tools.repository.model.Column;

/**
 * Value for "SpecialDialogCellEditor" ( for column "Special" ) 
 *  
 * @author Laurent GUERIN
 *
 */
public class SpecialValueForString extends SpecialValue 
{
	
	private boolean _initialLongText = false ;

	private boolean _initialNotEmpty = false ;
	private boolean _initialNotBlank = false ;
	private String  _initialMinLength ;
	private String  _initialMaxLength ;
	private String  _initialPattern  ;
	
	public SpecialValueForString( Column modelColumn ) 
	{
		super(modelColumn) ;
		_initialLongText = _modelColumn.getLongText();
		_initialNotEmpty = _modelColumn.getNotEmpty();
		_initialNotBlank = _modelColumn.getNotBlank();
		_initialMinLength = _modelColumn.getMinLength();
		_initialMaxLength = _modelColumn.getMaxLength();
		_initialPattern   = _modelColumn.getPattern();
	}
	
	//-------------------------------------------------------------------------------
	public boolean isLongText()
	{
		return _modelColumn.getLongText() ;
	}
	public void setLongText(boolean b)
	{
		_modelColumn.setLongText(b) ;
	}
	
	//-------------------------------------------------------------------------------
	public boolean isNotEmpty()
	{
		return _modelColumn.getNotEmpty() ;
	}
	public void setNotEmpty(boolean b)
	{
		_modelColumn.setNotEmpty(b) ;
	}
	
	//-------------------------------------------------------------------------------
	public boolean isNotBlank()
	{
		return _modelColumn.getNotBlank() ;
	}
	public void setNotBlank(boolean b)
	{
		_modelColumn.setNotBlank(b) ;
	}
	
	//-------------------------------------------------------------------------------
	public String getMinLength()
	{
		return emptyIfNull( _modelColumn.getMinLength() )  ;
	}
	public void setMinLength(String s)
	{
		_modelColumn.setMinLength(s);
	}
	
	//-------------------------------------------------------------------------------
	public String getMaxLength()
	{
		return emptyIfNull( _modelColumn.getMaxLength() ) ;
	}
	public void setMaxLength(String s)
	{
		_modelColumn.setMaxLength(s);
	}
	
	//-------------------------------------------------------------------------------
	public String getPattern()
	{
		return emptyIfNull( _modelColumn.getPattern() ) ;
	}
	public void setPattern(String s)
	{
		_modelColumn.setPattern(s);
	}
	
	//-------------------------------------------------------------------------------
	public boolean hasChanged()
	{
		if ( super.hasChanged() ) return true ;
		
		if ( _modelColumn.getLongText() != _initialLongText ) return true ;
		if ( _modelColumn.getNotEmpty() != _initialNotEmpty ) return true ;
		if ( _modelColumn.getNotBlank() != _initialNotBlank ) return true ;
		
		if ( ! sameValue( _modelColumn.getMinLength() , _initialMinLength ) ) return true ;
		if ( ! sameValue( _modelColumn.getMaxLength() , _initialMaxLength ) ) return true ;
		if ( ! sameValue( _modelColumn.getPattern() ,   _initialPattern ) ) return true ;
		
		return false ; // No change

	}
	
	//-------------------------------------------------------------------------------
	public void cancelChanges()
	{
		super.cancelChanges();
		
		_modelColumn.setLongText(_initialLongText);
		_modelColumn.setNotEmpty(_initialNotEmpty);
		_modelColumn.setNotBlank(_initialNotBlank);

		_modelColumn.setMinLength(_initialMinLength);
		_modelColumn.setMaxLength(_initialMaxLength);
		_modelColumn.setPattern(_initialPattern);
	}

}
