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
package org.telosys.tools.commons.dbcfg;

import java.util.StringTokenizer;


/**
 * Class for a database configuration ( loaded from the "databases.dbcfg" XML file ) 
 * 
 * @author Laurent GUERIN
 * 
 */
public class DatabaseConfiguration
{	
    private int        id                       = 0 ;

    private String     name                     = "";

    private String     jdbcUrl                  = "";

    private String     driverClass              = "";

    private String     user                     = "";

    private String     password                 = "";

    private String     isolationLevel           = "";

    private int        poolSize                 = 1;
   
    //private Properties _properties                = null;

    private String     metadataCatalog          = null;

    private String     metadataSchema           = null;

    private String     metadataTableNamePattern = null;

    private String     metadataTableTypes       = null;

    //private String[]   _arrayMetadataTableTypes   = null;

    /**
     * 
     */
    public DatabaseConfiguration()
    {
    }

	//----------------------------------------------------------------------------------
    //  
    //----------------------------------------------------------------------------------
    public int getDatabaseId()
    {
        return id;
    }
    public void setDatabaseId(int id) {
		this.id = id;
	}

    public String getDatabaseName()
    {
        return name;
    }
	public void setDatabaseName(String databaseName) {
		this.name = databaseName;
	}

    public String getDriverClass()
    {
        return driverClass;
    }
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}


    public String getJdbcUrl()
    {
        return jdbcUrl;
    }
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

    public String getUser()
    {
    	return user ;
    }
	public void setUser(String user) {
		this.user = user;
	}
    
    public String getPassword()
    {
    	return password ;
    }
	public void setPassword(String password) {
		this.password = password;
	}

    //----------------------------------------------------------------------------------
    public String getIsolationLevel()
    {
        return isolationLevel;
    }
	public void setIsolationLevel(String isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

    public int getPoolSize()
    {
        return poolSize;
    }
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

    //----------------------------------------------------------------------------------
    // Metadata configuration ( tag <metadata ... /> )
    //----------------------------------------------------------------------------------
    public String getMetadataCatalog()
    {
        return metadataCatalog;
    }
	public void setMetadataCatalog(String metadataCatalog) {
		this.metadataCatalog = metadataCatalog;
	}

    public String getMetadataSchema()
    {
        return metadataSchema;
    }
	public void setMetadataSchema(String metadataSchema) {
		this.metadataSchema = metadataSchema;
	}

    public String getMetadataTableNamePattern()
    {
        return metadataTableNamePattern;
    }
	public void setMetadataTableNamePattern(String metadataTableNamePattern) {
		this.metadataTableNamePattern = metadataTableNamePattern;
	}

    public String getMetadataTableTypes()
    {
        return metadataTableTypes;
    }
	public void setMetadataTableTypes(String metadataTableTypes) {
		this.metadataTableTypes = metadataTableTypes;
	}
	public String[] getMetadataTableTypesArray() {
	    StringTokenizer st = new StringTokenizer(metadataTableTypes);
	    int iCount = st.countTokens();
	    String[] array = new String[iCount];
	    for ( int i = 0 ; i < iCount ; i++ )
	    {
	    	array[i] = st.nextToken();
	    }
	    return array ;
	}

    public String toString()
    {
        return "DatabaseConfiguration : "
        	+ " id = " + id 
        	+ " name = " + name 
        	+ " driver class = " + driverClass 
        	+ " URL = " + jdbcUrl 
        	+ " user = " + user ;
    }
}