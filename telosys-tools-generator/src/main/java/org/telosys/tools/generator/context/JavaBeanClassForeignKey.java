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

/**
 * Database Foreign Key exposed in the generator context
 * 
 * @author Laurent Guerin
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName = ContextName.FOREIGN_KEY ,
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
	
	//-------------------------------------------------------------------------------------
	public JavaBeanClassForeignKey(final String fkName, final String tableName, final String targetTableName, 
			List<JavaBeanClassForeignKeyColumn> fkColumns ) 
	{
		this.fkName = fkName ;
		this.tableName = tableName ;
		this.targetTableName = targetTableName ;
		if ( fkColumns != null ) {
			this.fkColumns = fkColumns ;
		}
		else {
			this.fkColumns = new LinkedList<JavaBeanClassForeignKeyColumn>() ;
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
			"Returns the name of the target table (table referenced by the foreign key)"
			}
	)
	public String getTargetTableName() {
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
}
