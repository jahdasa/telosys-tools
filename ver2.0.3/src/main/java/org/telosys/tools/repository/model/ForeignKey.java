/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.repository.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;

public class ForeignKey implements Comparable<ForeignKey>
{
	private String name ;
	
	private Hashtable<String, ForeignKeyColumn> foreignKeyColumns = new Hashtable<String,ForeignKeyColumn>() ;

	//-------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String v) {
		this.name = v;
	}
	
	public String getTableName() throws TelosysToolsException {
		// The table name must be not void and identical for all foreignKeyColumns
		if (foreignKeyColumns.isEmpty()) {
			throw new TelosysToolsException("aucune colonne pour la foreignkey " + this.name);
		} else {
			String tableName = null;
			for (ForeignKeyColumn fkc : foreignKeyColumns.values()) {
				if (StrUtil.nullOrVoid(fkc.getTableName())) {
					throw new TelosysToolsException("TableName vide pour une ForeignKeyColumn de " + this.name);
				} else {
					if (StrUtil.nullOrVoid(tableName)) {
						tableName = fkc.getTableName();
					} else if (StrUtil.identical(fkc.getTableName(), tableName) == false) {
						throw new TelosysToolsException("TableName different pour la foreignkey " + this.name);
					}
				}
			}
			return tableName;
		}
	}
	
	public String getTableRef() throws TelosysToolsException {
		// The table name ref must be not void and identical for all foreignKeyColumns
		if (foreignKeyColumns.isEmpty()) {
			throw new TelosysToolsException("aucune colonne pour la foreignkey " + this.name);
		} else {
			String tableref = null;
			for (ForeignKeyColumn fkc : foreignKeyColumns.values()) {
				if (StrUtil.nullOrVoid(fkc.getTableRef())) {
					throw new TelosysToolsException("Tableref vide pour une ForeignKeyColumn de " + this.name);
				} else {
					if (StrUtil.nullOrVoid(tableref)) {
						tableref = fkc.getTableRef();
					} else if (StrUtil.identical(fkc.getTableRef(), tableref) == false) {
						throw new TelosysToolsException("Tableref different pour la foreignkey " + this.name);
					}
				}
			}
			return tableref;
		}
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * Returns an array containing all the columns of the foreign key<br>
	 * The columns are sorted by sequence (the original database order).
	 * @return
	 */
	public ForeignKeyColumn[] getForeignKeyColumns()
	{
		//return (ForeignKeyColumn[]) foreignKeyColumns.values().toArray(new ForeignKeyColumn[foreignKeyColumns.size()]);
		ForeignKeyColumn[] array = (ForeignKeyColumn[]) foreignKeyColumns.values().toArray(new ForeignKeyColumn[foreignKeyColumns.size()]);
		Arrays.sort(array);
		return array ;
		
	}
	
	/**
	 * Returns a collection of all the columns of the foreign key.<br>
	 * The columns are sorted by sequence (the original database order).
	 * @return
	 */
	public Collection<ForeignKeyColumn> getForeignKeyColumnsCollection()
	{
		//return foreignKeyColumns.values() ;
		ForeignKeyColumn[] array = getForeignKeyColumns();
		return Arrays.asList(array);
	}
	
	public void storeForeignKeyColumn(ForeignKeyColumn fkColumn)
	{
		foreignKeyColumns.put(fkColumn.getColumnName(), fkColumn);
	}

	public ForeignKeyColumn getForeignKeyColumn(String columnName)
	{
		return (ForeignKeyColumn) foreignKeyColumns.get(columnName);
	}
	
	public void removeForeignKeyColumn(ForeignKeyColumn fkColumn)
	{
		foreignKeyColumns.remove(fkColumn.getColumnName() );
	}
	
	//-------------------------------------------------------------------------------
	public boolean equals(Object o) 
	{
		if ( null == o ) return false ;
		if ( this == o ) return true ;
		if ( o.getClass() != this.getClass() ) return false ;
		
		ForeignKey fk = (ForeignKey) o ;
		
		//--- Not the same name
		if ( ! StrUtil.identical(this.name, fk.getName() ) ) return false ;
		
		ForeignKeyColumn[] thisColumns = this.getForeignKeyColumns();
		ForeignKeyColumn[] otherColumns = fk.getForeignKeyColumns();
		
		//--- Not the same number of fk colums
		if ( thisColumns.length != otherColumns.length ) return false ;
		
		for ( int i = 0 ; i < thisColumns.length ; i++ )
		{
			ForeignKeyColumn c1 = thisColumns[i];
			ForeignKeyColumn c2 = otherColumns[i];
			//--- Not the same fk colum 
			if ( ! c1.equals(c2) ) return false ;
		}
		
		//--- No difference
		return true ;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	//public int compareTo(Object o) {
	public int compareTo(ForeignKey other) {
		if ( other != null )
		{
			// ForeignKey other = (ForeignKey) o;
			String sThisName  = this.getName() ;
			String sOtherName = other.getName();
			if ( sThisName != null && sOtherName != null )
			{
				return sThisName.compareTo(sOtherName);
			}
		}
		return 0;
	}

}
