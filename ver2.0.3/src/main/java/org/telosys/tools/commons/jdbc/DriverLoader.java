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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Hashtable;

import org.telosys.tools.commons.GenericTool;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;

/**
 * Utility class used to load JDBC drivers
 * 
 * @author Laurent GUERIN *  */

/* package */ class DriverLoader extends GenericTool
{
    //-----------------------------------------------------------------------------
    // Specific Class Loader ( Inner class )
    //-----------------------------------------------------------------------------
    private static class MyClassLoader extends URLClassLoader
    {
        public MyClassLoader (URL[] urls, java.lang.ClassLoader parentLoader )
        {
            //--- Call the URLClassLoader constructor
            super(urls, parentLoader);
        }
    }

    //-----------------------------------------------------------------------------
    // Attributes
    //-----------------------------------------------------------------------------
    private MyClassLoader             _loader  = null; // Specific Class Loader instance

    private Hashtable<String,Driver>  _drivers = new Hashtable<String,Driver>(); // Loaded drivers
    
    //-----------------------------------------------------------------------------
    // The unique constructor
    //-----------------------------------------------------------------------------
    /**
     * Constructor 
     * 
     * @param paths
     * @param logger
     */
    public DriverLoader( String[] paths, TelosysToolsLogger logger ) throws TelosysToolsException
    {
    	super(logger);
    	
    	log ( "DriverLoader constructor ... " );
        if ( paths == null )
        {
        	throwException( "DriverLoader constructor : paths[] is null !" );
        }
        else if ( paths.length == 0 )
        {
        	throwException( "DriverLoader constructor : paths[] is void !" );
        }
        
        ClassLoader parentLoader = ClassLoader.getSystemClassLoader();
        //--- Convert String[] to URL[] (eliminate void and malformed urls )
        logPaths(paths);
        URL[] urls = new URL[paths.length];
        int n = 0 ;
        for ( int i = 0 ; i < paths.length ; i++ )
        {
            if ( paths[i] != null )
            {
                String sPath = paths[i].trim();
                if ( sPath.length() > 0 )
                {                
		            try
		            {
		                // urls[n] = new File(sPath).toURL(); // toURL deprecated since Java 5.0
		                // The recommended new code ( see JavaDoc )
		                URI uri = new File(sPath).toURI();
		                urls[n] = uri.toURL();
		                n++;
		            } 
		            catch (MalformedURLException e)
		            {
		            	throwException("DriverLoader : Cannot convert path '" + sPath + "' to URL (MalformedURLException)", e);
		            }
                }
            }
        }
        
        //--- Build an array with only the valid URLs 
        URL[] validURLs = new URL[n];
        System.arraycopy(urls, 0, validURLs, 0, n);
        logURLs(validURLs);
        if ( validURLs.length == 0 )
        {
        	throwException( "No valid URL" );
        }
        
        //--- Create the specific class loader
        _loader = new MyClassLoader ( validURLs, parentLoader );
        log  ( "Specific Class Loader created." );
    }
    
    //-----------------------------------------------------------------------------
    // The method to provide a driver (via the specific class loader)
    //-----------------------------------------------------------------------------
    public Driver getDriver(String sDriverClassName) throws TelosysToolsException
    {
        Class<?> driverClass = null;
        Driver driverInstance = null;

        //--- Try to find an existing instance of this type of driver 
        driverInstance = (Driver) _drivers.get(sDriverClassName);
        if ( driverInstance != null )
        {
            log ("Driver already loaded");
            return driverInstance ;
        }
                        
        //--- Try to load the driver class with the specific class loader
        if (_loader == null)
        {
        	throwException("Class loader is not initialized (_loader == null)");
        }
        try
        {
            log ("Loading the driver class '" + sDriverClassName  + "' ...");
            driverClass = _loader.loadClass(sDriverClassName);
        } catch (ClassNotFoundException e)
        {
        	throwException("Cannot load class '" + sDriverClassName + "' (ClassNotFoundException)", e);
        }

        //--- Try to create an instance of this driver
        if (driverClass != null)
        {
            log ("Driver class loaded.");
            try
            {
                driverInstance = (Driver) driverClass.newInstance();
            } 
            catch (InstantiationException e)
            {
            	throwException("Cannot create driver instance (InstantiationException)", e);
            } 
            catch (IllegalAccessException e)
            {
            	throwException("Cannot create driver instance (IllegalAccessException)", e);
            }


        }

        if ( driverInstance != null )
        {
            log ("Driver instance created.");
            //--- Store the driver instance ( for the future )
            _drivers.put(sDriverClassName, driverInstance);
        }
        else
        {
        	throwException("Cannot create driver instance (unknown reason)");
        }
        
        return driverInstance ;
    }
    
    //-----------------------------------------------------------------------------
    // Tools
    //-----------------------------------------------------------------------------
    private void logPaths(String[] paths)
    {
        log  ( "Paths length = " + paths.length );
        for ( int i = 0 ; i < paths.length ; i++ )
        {
            log  ( i + " : [" + paths[i] + "]");
        }
    }
    //-----------------------------------------------------------------------------
    private void logURLs(URL[] urls)
    {
        log  ( "URLs length = " + urls.length );
        for ( int i = 0 ; i < urls.length ; i++ )
        {
            log  ( i + " : [" + urls[i] + "]");
        }
    }
    //-----------------------------------------------------------------------------    
}