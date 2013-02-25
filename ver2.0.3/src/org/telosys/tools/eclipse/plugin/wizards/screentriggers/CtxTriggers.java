package org.telosys.tools.eclipse.plugin.wizards.screentriggers;

import org.telosys.tools.eclipse.plugin.commons.PluginLogger;


/**
 * @author Laurent GUERIN
 *
 * Context class for TRIGGERS 
 * 
 */
public class CtxTriggers {

	private static final String INFO = "CtxTriggers class" ;
	
	private static final String BEFORE_SET = "beforeSet" ;
	private static final String AFTER_SET = "afterSet" ;

	private static final String BEFORE_GET = "beforeGet" ;
	private static final String AFTER_GET = "afterGet" ;
	
	private static final String BEFORE_CLEAR = "beforeClear" ;
	private static final String AFTER_CLEAR = "afterClear" ;
	
	private static final String BEFORE_LOAD = "beforeLoad" ;
	private static final String AFTER_LOAD = "afterLoad" ;
	
	private static final String BEFORE_SAVE = "beforeSave" ;
	private static final String AFTER_SAVE = "afterSave" ;
	
	private static final String BEFORE_INSERT = "beforeInsert" ;
	private static final String AFTER_INSERT = "afterInsert" ;
	
	private static final String BEFORE_UPDATE = "beforeUpdate" ;
	private static final String AFTER_UPDATE = "afterUpdate" ;
	
	private static final String BEFORE_DELETE = "beforeDelete" ;
	private static final String AFTER_DELETE = "afterDelete" ;
	
//	private static final String TRIGGERS []  = { 
//			"beforeSet",   "afterSet",
//			"beforeGet",   "afterGet",
//			"beforeClear", "afterClear",
//			
//			"beforeLoad",   "afterLoad",
//			"beforeSave",   "afterSave",
//			"beforeInsert", "afterInsert",
//			"beforeUpdate", "afterUpdate",
//			"beforeDelete", "afterDelete"
//		} ;
	private static final String TRIGGERS []  = {
			BEFORE_SET, AFTER_SET,
			BEFORE_GET, AFTER_GET,
			BEFORE_CLEAR, AFTER_CLEAR,
			
			BEFORE_LOAD, AFTER_LOAD,
			BEFORE_SAVE, AFTER_SAVE,
			BEFORE_INSERT, AFTER_INSERT,
			BEFORE_UPDATE, AFTER_UPDATE,
			BEFORE_DELETE, AFTER_DELETE			
		} ;
												  
	private boolean[] _selected = null ;

	private boolean _bImportDatabaseSession = false ;
	
	private boolean _bImportScreenData = false ;
		
//	Hashtable htTriggers = new Hashtable(16);
//	
//	private boolean _bBeforeGet = false ;
//	private boolean _bAfterGet = false ;
//	
//	private boolean _bBeforeSet = false ;
//	private boolean _bAfterSet = false ;
//	
//	private boolean _bBeforeClear = false ;
//	private boolean _bAfterClear = false ;
//	
//	private boolean _bBeforeLoad = false ;
//	private boolean _bAfterLoad = false ;
//
//	private boolean _bBeforeSave = false ;
//	private boolean _bAfterSave = false ;
//	
//	private boolean _bBeforeInsert = false ;
//	private boolean _bAfterInsert = false ;
//	
//	private boolean _bBeforeUpdate = false ;
//	private boolean _bAfterUpdate = false ;
//	
//	private boolean _bBeforeDelete = false ;
//	private boolean _bAfterDelete = false ;
	
	//-------------------------------------------------------------
	public static String[] getTriggers() {
		return TRIGGERS;
	}
	//-------------------------------------------------------------
	
	private void init() 
	{
		_selected = new boolean[TRIGGERS.length] ;		
		for ( int i = 0 ; i < _selected.length ; i++ )
		{
			_selected[i] = false ;
		}
	}
//	public CtxTriggers() {
//		_selected = new boolean[TRIGGERS.length] ;		
//		for ( int i = 0 ; i < _selected.length ; i++ )
//		{
//			_selected[i] = false ;
//		}
//	}
	
	public CtxTriggers( boolean[] selected ) {
		PluginLogger.log("=== CONSTRUCTOR : CtxTriggers() ....");
		init();
		String[] dbKeyWords = { "Load", "Save", "Insert", "Update", "Delete" } ;
		String s = null ;
		if ( selected.length == TRIGGERS.length )
		{
			for ( int i = 0 ; i < _selected.length ; i++ )
			{
				_selected[i] = selected[i] ;
				if ( selected[i] )
				{					
					s = TRIGGERS[i] ;
					if ( contains(s, dbKeyWords ) )
					{
						_bImportDatabaseSession = true ;
						_bImportScreenData = true ;
					}
					else
					{
						if ( s.indexOf("Set") >= 0 )
						{
							_bImportScreenData = true ;
						}
					}
				}
			}
		}
		else
		{
			// ERROR 
		}
	}
	private boolean contains(String s, String[] a) {
		PluginLogger.log("contains("+s+",array)");
		for ( int i=0 ; i < a.length ; i++)
		{
			if ( s.indexOf(a[i]) >= 0 )
			{
				PluginLogger.log("  contains("+s+",array) --> TRUE");
				return true ;
			}
		}
		return false ;
	}
	
	private boolean isSelected(String s) {
		if ( s != null )
		{
			//--- Search index
			for ( int i = 0 ; i < TRIGGERS.length ; i++ )
			{
				if ( s.equals(TRIGGERS[i]) ) // found
				{
					return _selected[i] ;
				}
			}
		}
		return false ;
	}
	
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public String getInfo() {
		PluginLogger.log("getInfo()" );
		return INFO ;
	}
	
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public boolean isImportDatabaseSession() {
		PluginLogger.log("importDatabaseSession() --> " + _bImportDatabaseSession );
		return _bImportDatabaseSession;
	}
//	public void setImportDatabaseSession(boolean b) {
//		_bImportDatabaseSession = b;
//	}
	//-------------------------------------------------------------
	public boolean isImportScreenData() {
		PluginLogger.log("importScreenData() --> " + _bImportScreenData );
		return _bImportScreenData;
	}
//	public void setImportScreenData(boolean b) {
//		_bImportScreenData = b;
//	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------

	
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public boolean isBeforeGet() {
		return isSelected(BEFORE_GET);
		//return _bBeforeGet;
	}
//	public void setBeforeGet(boolean b) {
//		_bBeforeGet = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterGet() {
		return isSelected(AFTER_GET);
//		return _bAfterGet;
	}
//	public void setAfterGet(boolean b) {
//		_bAfterGet = b;
//	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public boolean isBeforeSet() {
//		return _bBeforeSet;
		return isSelected(BEFORE_SET);
	}
//	public void setBeforeSet(boolean b) {
//		_bBeforeSet = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterSet() {
//		return _bAfterSet;
		return isSelected(AFTER_SET);
	}
//	public void setAfterSet(boolean b) {
//		_bAfterSet = b;
//	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public boolean isBeforeClear() {
//		return _bBeforeClear;
		return isSelected(BEFORE_CLEAR);
	}
//	public void setBeforeClear(boolean b) {
//		_bBeforeClear = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterClear() {
//		return _bAfterClear;
		return isSelected(AFTER_CLEAR);		
	}
//	public void setAfterClear(boolean b) {
//		_bAfterClear = b;
//	}
	//-------------------------------------------------------------
	
	
	//-------------------------------------------------------------
	public boolean isAfterDelete() {
//		return _bAfterDelete;
		return isSelected(AFTER_DELETE);
		
	}
//	public void setAfterDelete(boolean b) {
//		_bAfterDelete = b;
//	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public boolean isAfterInsert() {
//		return _bAfterInsert;
		return isSelected(AFTER_INSERT);
	}
//	public void setAfterInsert(boolean b) {
//		_bAfterInsert = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterLoad() {
//		return _bAfterLoad;
		return isSelected(AFTER_LOAD);		
	}
//	public void afterLoad(boolean b) {
//		_bAfterLoad = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterSave() {
//		return _bAfterSave;
		return isSelected(AFTER_SAVE);
	}
//	public void afterSave(boolean b) {
//		_bAfterSave = b;
//	}
	//-------------------------------------------------------------
	public boolean isAfterUpdate() {
//		return _bAfterUpdate;
		return isSelected(AFTER_UPDATE);
	}
//	public void afterUpdate(boolean b) {
//		_bAfterUpdate = b;
//	}
	
	
	//-------------------------------------------------------------
	public boolean isBeforeDelete() {
//		return _bBeforeDelete;
		return isSelected(BEFORE_DELETE);
	}
//	public void set_bBeforeDelete(boolean b) {
//		_bBeforeDelete = b;
//	}
	//-------------------------------------------------------------
	public boolean isBeforeInsert() {
//		return _bBeforeInsert;
		return isSelected(BEFORE_INSERT);
	}
//	public void set_bBeforeInsert(boolean b) {
//		_bBeforeInsert = b;
//	}
	//-------------------------------------------------------------
	public boolean isBeforeLoad() {
//		return _bBeforeLoad;
		return isSelected(BEFORE_LOAD);
	}
//	public void set_bBeforeLoad(boolean b) {
//		_bBeforeLoad = b;
//	}
	//-------------------------------------------------------------
	public boolean isBeforeSave() {
//		return _bBeforeSave;
		return isSelected(BEFORE_SAVE);		
	}
//	public void set_bBeforeSave(boolean b) {
//		_bBeforeSave = b;
//	}
	//-------------------------------------------------------------
	public boolean isBeforeUpdate() {
//		return _bBeforeUpdate;
		return isSelected(BEFORE_UPDATE);
	}
//	public void set_bBeforeUpdate(boolean b) {
//		_bBeforeUpdate = b;
//	}
}
