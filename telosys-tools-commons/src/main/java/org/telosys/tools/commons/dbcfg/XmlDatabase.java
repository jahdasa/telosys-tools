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

import java.util.Properties;
import java.util.StringTokenizer;

import org.telosys.tools.commons.XmlDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for a database configuration ( loaded from the "databases.dbcfg" XML file ) 
 * 
 * @author Laurent GUERIN
 * 
 */
@Deprecated
public class XmlDatabase
{
	public final static String PROPERTY_USER     = "user";
	public final static String PROPERTY_PASSWORD = "password";
	 
	
    private String     _sDatabaseId               = null;

    private String     _sDatabaseName             = null;

    private String     _sJdbcUrl                  = null;

    private String     _sDriverClass              = null;

    private String     _sIsolationLevel           = null;

    private String     _sPoolSize                 = null;

    
    
    private Properties _properties                = null;

    private String     _sMetadataCatalog          = null;

    private String     _sMetadataSchema           = null;

    private String     _sMetadataTableNamePattern = null;

    private String     _sMetadataTableTypes       = null;

    private String[]   _arrayMetadataTableTypes   = null;

    /**
     * 
     * @param dbNode
     */
    public XmlDatabase(Node dbNode)
    {
    	XmlDocument xmlDoc = new XmlDocument(null);
    	
        //--- Database attributes
//        _sDatabaseId     = Xml.getNodeAttribute(dbNode, ConstXML.DB_ID_ATTRIBUTE);
//        _sDatabaseName   = Xml.getNodeAttribute(dbNode, ConstXML.DB_NAME_ATTRIBUTE);
//        _sJdbcUrl        = Xml.getNodeAttribute(dbNode, ConstXML.DB_URL_ATTRIBUTE);
//        _sDriverClass    = Xml.getNodeAttribute(dbNode, ConstXML.DB_DRIVER_ATTRIBUTE);
//        
//		_sIsolationLevel = Xml.getNodeAttribute(dbNode, ConstXML.DB_ISOLATION_LEVEL_ATTRIBUTE );
//		_sPoolSize       = Xml.getNodeAttribute(dbNode, ConstXML.DB_POOLSIZE_ATTRIBUTE  );

        _sDatabaseId     = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_ID_ATTRIBUTE);
        _sDatabaseName   = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_NAME_ATTRIBUTE);
        _sJdbcUrl        = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_URL_ATTRIBUTE);
        _sDriverClass    = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_DRIVER_ATTRIBUTE);
        
		_sIsolationLevel = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_ISOLATION_LEVEL_ATTRIBUTE );
		_sPoolSize       = xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_POOLSIZE_ATTRIBUTE  );

        if (dbNode instanceof Element)
        {
            Element dbElem = (Element) dbNode;
            //--- Database properties
            NodeList configProperties = dbElem.getElementsByTagName(ConstXML.DB_PROPERTY_ELEMENT);
            _properties = new Properties();
            int iPropCount = configProperties.getLength();
            for ( int i = 0 ; i < iPropCount ; i++ )
            {
                Node node = configProperties.item(i);
                if (node != null)
                {
                    if (node instanceof Element)
                    {
                        Element elemProperty = (Element) node;
  
                        String sName = elemProperty.getAttribute(ConstXML.DB_PROPERTY_NAME_ATTRIBUTE);
                        String sValue = elemProperty.getAttribute(ConstXML.DB_PROPERTY_VALUE_ATTRIBUTE);
                        _properties.setProperty(sName, sValue);
                    }
                }
            }
            
            //--- Database metadata
            NodeList metadataList = dbElem.getElementsByTagName(ConstXML.DB_METADATA_ELEMENT);
            if (metadataList.getLength() > 0)
            {
                if (metadataList.item(0) instanceof Element)
                {
                    Element elemMetadata = (Element) metadataList.item(0);
                    _sMetadataCatalog = elemMetadata.getAttribute(ConstXML.DB_METADATA_ATTR_CATALOG);
                    _sMetadataSchema = elemMetadata.getAttribute(ConstXML.DB_METADATA_ATTR_SCHEMA);
                    _sMetadataTableNamePattern = elemMetadata.getAttribute(ConstXML.DB_METADATA_ATTR_TABLE_NAME_PATTERN);
                    _sMetadataTableTypes = elemMetadata.getAttribute(ConstXML.DB_METADATA_ATTR_TABLE_TYPES);
                    StringTokenizer st = new StringTokenizer(_sMetadataTableTypes);
                    int iCount = st.countTokens();
                    _arrayMetadataTableTypes = new String[iCount];
                    for ( int i = 0 ; i < iCount ; i++ )
                    {
                        _arrayMetadataTableTypes[i] = st.nextToken();
                    }
                }
            }
            else
            {
            	//MsgBox.error("Metadata not found in file dbconfig.xml");
            	_arrayMetadataTableTypes = new String[0];
            }
        }
    }

    //----------------------------------------------------------------------------------
    // TAG Attributes 
    //----------------------------------------------------------------------------------
    public String getDatabaseId()
    {
        return _sDatabaseId;
    }

    public String getDatabaseName()
    {
        return _sDatabaseName;
    }

    public String getDriverClass()
    {
        return _sDriverClass;
    }

    public String getJdbcUrl()
    {
        return _sJdbcUrl;
    }

    public String getIsolationLevel()
    {
        return _sIsolationLevel;
    }

    public String getPoolSize()
    {
        return _sPoolSize;
    }

    //----------------------------------------------------------------------------------
    // Properties ( tags <property name="..." value="..." />
    //----------------------------------------------------------------------------------
    public Properties getProperties()
    {
        return _properties;
    }

    private String getProperty( String sKey )
    {
        if ( _properties != null )
        {
        	return (String) _properties.get( sKey );
        }
        return null ;
    }

    public String getUser()
    {
    	return getProperty( PROPERTY_USER );
    }
    
    public String getPassword()
    {
    	return getProperty( PROPERTY_PASSWORD );
    }

    //----------------------------------------------------------------------------------
    // Metadata configuration ( tag <metadata ... /> )
    //----------------------------------------------------------------------------------
    public String getMetadataCatalog()
    {
        return _sMetadataCatalog;
    }

    public String getMetadataSchema()
    {
        return _sMetadataSchema;
    }

    public String getMetadataTableNamePattern()
    {
        return _sMetadataTableNamePattern;
    }

    public String getOriginalMetadataTableTypes()
    {
        return _sMetadataTableTypes;
    }

    public String[] getMetadataTableTypes()
    {
        return _arrayMetadataTableTypes;
    }

    public String toString()
    {
        String sUser = (String) _properties.get("user");
        return "XmlDatabase : Id = " + _sDatabaseId + " Name = " + _sDatabaseName + " Driver class = " + _sDriverClass + " URL = " + _sJdbcUrl + " User (in properties) = " + sUser + " Properties size = " + _properties.size();
    }
}