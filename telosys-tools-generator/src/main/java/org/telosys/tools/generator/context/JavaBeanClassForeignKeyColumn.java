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
package org.telosys.tools.generator.context;

import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.repository.model.ForeignKeyColumn;

/**
 * Database Foreign Key Column exposed in the generator context
 * 
 * @author Laurent Guerin
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName = ContextName.FKCOL ,
		text = {
				"This object provides all information about a database foreign key column",
				""
		},
		since = "2.0.7",
		example= {
				"",
				"#foreach( $fkcol in $fk.columns )",
				"    $fkcol.columnName - $fkcol.targetColumnName ",
				"#end"				
		}
 )
//-------------------------------------------------------------------------------------
public class JavaBeanClassForeignKeyColumn implements Comparable<JavaBeanClassForeignKeyColumn>
{
	private final int    _sequence ;

	private final String _columnName ;
	
	private final String _columnRef ;
	
	private final String _updateRule ;
	
	private final String _deleteRule ;
	
	private final String _deferrable ;

	//-------------------------------------------------------------------------------------
//	public JavaBeanClassForeignKeyColumn(int sequence, String columnName, String columnRef, 
//			String updateRule, String deleteRule, String deferrable) {
//		super();
//		this._sequence = sequence;
//		this._columnName = columnName;
//		this._columnRef = columnRef;
//		this._updateRule = updateRule;
//		this._deleteRule = deleteRule;
//		this._deferrable = deferrable;
//	}
	public JavaBeanClassForeignKeyColumn( ForeignKeyColumn fkColumn ) {
		super();
		this._sequence   = fkColumn.getSequence();
		this._columnName = fkColumn.getColumnName();
		this._columnRef  = fkColumn.getColumnRef();
		this._updateRule = fkColumn.getUpdateRule();
		this._deleteRule = fkColumn.getDeleteRule();
		this._deferrable = fkColumn.getDeferrable();
	}
	 
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the column"
			}
	)
	public String getColumnName() {
		return _columnName;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the sequence of the column (position in the foreign key)"
			}
	)
	public int getSequence() {
		return _sequence;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the target column (column referenced in the target table)"
			}
	)
	public String getTargetColumnName() {
		return _columnRef;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'DEFERABLE' status "
			}
	)
	public String getDeferrable() {
		return _deferrable;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'ON DELETE' rule "
			}
	)
	public String getDeleteRule() {
		return _deleteRule;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'ON UPDATE' rule "
			}
	)
	public String getUpdateRule() {
		return _updateRule;
	}

	//-------------------------------------------------------------------------------
	//public int compareTo(Object o) {
	public int compareTo(JavaBeanClassForeignKeyColumn other) {
		if ( other != null )
		{
			//ForeignKeyColumn other = (ForeignKeyColumn) o;
			return ( this.getSequence() - other.getSequence() );
		}
		return 0;
	}
	
}
