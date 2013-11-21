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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generator.ContextName;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.repository.model.ForeignKey;
import org.telosys.tools.repository.model.ForeignKeyColumn;

/**
 * Database Foreign Key exposed in the generator context
 * 
 * @author Laurent Guerin
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName = ContextName.FK ,
		text = {
				"This object provides all information about a database foreign key",
				"Each foreign key is retrieved from the entity class ",
				""
		},
		since = "2.0.7",
		example= {
				"",
				"#foreach( $fk in $entity.databaseForeignKeys )",
				"    $fk.name ",
				"#end"				
		}
 )
//-------------------------------------------------------------------------------------
public class JavaBeanClassForeignKey {
	
	private final String  fkName ;
	private final String  tableName ;
	private final String  targetTableName  ;
	private final List<JavaBeanClassForeignKeyColumn> fkColumns ;
	
	private String _updateRule = "" ;
	private String _deleteRule = "" ;
	private String _deferrable = "" ;

//	//-------------------------------------------------------------------------------------
//	public JavaBeanClassForeignKey(final String fkName, 
//			final String tableName, final String targetTableName, 
//			List<JavaBeanClassForeignKeyColumn> fkColumns,
//			final String updateRule, final String deleteRule, final String deferrable  ) 
//	{
//		this.fkName = fkName ;
//		this.tableName = tableName ;
//		this.targetTableName = targetTableName ;
//		if ( fkColumns != null ) {
//			this.fkColumns = fkColumns ;
//		}
//		else {
//			this.fkColumns = new LinkedList<JavaBeanClassForeignKeyColumn>() ;
//		}
//		
//		this._updateRule = updateRule ;
//		this._deleteRule = deleteRule ;
//		this._deferrable = deferrable ;
//	}

	//-------------------------------------------------------------------------------------
	public JavaBeanClassForeignKey(final ForeignKey metadataFK ) 
	{
		this.fkName = metadataFK.getName() ;
		this.tableName = metadataFK.getTableName() ;
		this.targetTableName = metadataFK.getTableRef() ;
		
		this._updateRule = "" ;
		this._deleteRule = "" ;
		this._deferrable = "" ;
		this.fkColumns = new LinkedList<JavaBeanClassForeignKeyColumn>() ;
		if ( metadataFK.getForeignKeyColumnsCollection().size() > 0 ) {
			for ( ForeignKeyColumn metadataFKColumn : metadataFK.getForeignKeyColumnsCollection() ) {
				int    sequence = metadataFKColumn.getSequence();
				String columnName = metadataFKColumn.getColumnName();
				String referencedColumnName = metadataFKColumn.getColumnRef();
				fkColumns.add( new JavaBeanClassForeignKeyColumn(sequence, columnName, referencedColumnName ) ) ;
				//--- ON UPDATE, ON DELETE and DEFERRABLE (stored in each column in meta-data, keep the last one)
				this._updateRule = metadataFKColumn.getUpdateRule() ;
				this._deleteRule = metadataFKColumn.getDeleteRule() ;
				this._deferrable = metadataFKColumn.getDeferrable() ;
			}
		}
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the Foreign Key"
			}
	)
	public String getName()
    {
		return this.fkName;
    }

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the table holding the foreign key"
			}
	)
	public String getTableName() {
		return this.tableName ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the name of the referenced table (the table referenced by the foreign key)"
			}
	)
	public String getReferencedTableName() {
		return this.targetTableName ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns all the columns composing the foreign key",
			"(sorted in the original database order)"
			}
	)
	public List<JavaBeanClassForeignKeyColumn> getColumns() {
		return this.fkColumns ;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the number of columns composing the foreign key"
			}
	)
	public int getColumnsCount() {
		return this.fkColumns.size() ;
	}

	//-------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'DEFERRABILITY' status ( 'DEFERRABLE', 'NOT DEFERRABLE' ) "
			}
	)
	public String getDeferrable() {
		return _deferrable;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'ON DELETE' rule ( 'NO ACTION', 'RESTRICT', 'SET NULL', 'SET DEFAULT', 'CASCADE'  ) "
			}
	)
	public String getDeleteRule() {
		return _deleteRule;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the 'ON UPDATE' rule ( 'NO ACTION', 'RESTRICT', 'SET NULL', 'SET DEFAULT', 'CASCADE' ) "
			}
	)
	public String getUpdateRule() {
		return _updateRule;
	}
}
