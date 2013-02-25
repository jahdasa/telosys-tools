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
package org.telosys.tools.commons.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.telosys.tools.commons.GenericTool;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;

/**
 * JDBC connection manager to get and test a connection
 * 
 * @author Laurent GUERIN *  */

public class ConnectionManager extends GenericTool
{
    private DriverLoader _driverLoader = null;

    //-----------------------------------------------------------------------------
    /**
     * Constructor
     * @param paths array of paths where to search the JDBC driver 
     * @param logger the logger to use
     */
    public ConnectionManager ( String[] paths, TelosysToolsLogger logger ) throws TelosysToolsException
    {
    	super(logger);
    	
    	log ( "ConnectionManager constructor ... " );

        if ( paths == null )
        {
            throwException( "ConnectionManager constructor : paths[] is null !" );            
        }
        else if ( paths.length == 0 )
        {
        	throwException( "ConnectionManager constructor : paths[] is void !" );            
        }
        
        _driverLoader = new DriverLoader(paths, logger);     
        if ( _driverLoader == null )
        {
        	throwException( "ConnectionManager constructor : Cannot create the driver loader" ) ;
        }
        else
        {
        	log("Driver loader ready.");
        }
    }

    //-----------------------------------------------------------------------------
    /**
     * Creates a new connection using the given parameters 
     * 
     * @param sDriverClassName
     * @param sJdbcUrl
     * @param prop
     * @return
     */
    public Connection getConnection(String sDriverClassName, String sJdbcUrl, Properties prop ) throws TelosysToolsException
    {        	        
        //--- 1) Get the JDBC driver
        if ( _driverLoader == null )
        {
        	throwException( "getConnection : Driver loader is null " );
        }

        Driver driver = _driverLoader.getDriver(sDriverClassName);
        if ( driver == null )
        {
        	throwException( "getConnection : Cannot get driver from the driver loader " );
        }
        
        //--- 2) Create the connection
        Connection con = null ;
        try
        {
            con = driver.connect(sJdbcUrl, prop);
        } catch (SQLException e)
        {
        	logError("getConnection : Cannot connect to the database (SQLException)");
        	logError(e.getMessage() + " / ErrorCode = " + e.getErrorCode() + " / SQLState = " + e.getSQLState());
            throw new TelosysToolsException ( "Cannot connect to the database (SQLException)", e);
        }
        return con ;
    }

    //-----------------------------------------------------------------------------
    /**
     * Test the given connection by getting the current catalog
     * @param con
     * @return true if the test is OK
     */
    public boolean testConnection(Connection con)
    {
    	boolean ret = false ;
        if ( con != null )
        {
            try
            {
            	String catalog = con.getCatalog() ;
            	ret = true ;
                logInfo("Connection test OK : calalog = '" + catalog + "'");
            } catch (SQLException e)
            {
            	ret = false ;
                logError("Cannot get catalog - SQLException : " + e.getMessage() );
            }            
        } 
        else
        {
        	ret = false ;
        	logError("Connection is null !");
        }
        return ret ;
    }
    //-----------------------------------------------------------------------------
}