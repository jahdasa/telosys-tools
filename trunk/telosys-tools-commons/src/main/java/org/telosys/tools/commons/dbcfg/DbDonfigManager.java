package org.telosys.tools.commons.dbcfg;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.XmlFileUtil;
import org.telosys.tools.commons.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DbDonfigManager {

	private final File file ;

	public DbDonfigManager(File file) {
		super();
		if ( file != null ) {
			this.file = file;
		}
		else {
			throw new IllegalArgumentException("File is null");
		}
	}

	public DatabasesConfigurations load() throws TelosysToolsException {
		
		DatabasesConfigurations databasesConfigurations = new DatabasesConfigurations();
		
		Document document = XmlFileUtil.load( file );
		
        //--- Root element "<databases>"
        Element root = document.getDocumentElement();
        if (root == null)
        {
        	return databasesConfigurations ;
        }
        
        //--- Root element "<databases>" attributes
        databasesConfigurations.setDatabaseDefaultId(
        		XmlUtil.getNodeAttributeAsInt(root, ConstXML.DATABASES_DEFAULT_ID_ATTRIBUTE, 0) );
        
        databasesConfigurations.setDatabaseMaxId(
        		XmlUtil.getNodeAttributeAsInt(root, ConstXML.DATABASES_MAX_ID_ATTRIBUTE, 0) );
        
        //--- List of "<db>" elements
        NodeList dbList = root.getElementsByTagName(ConstXML.DB_ELEMENT);
        if (dbList == null)
        {
        	return databasesConfigurations ;
        }

        
        //--- For each database node in the XML file
        for ( int i = 0 ; i < dbList.getLength() ; i++ )
        {
        	//--- Parse the Database Node
            Node dbNode = dbList.item(i);
            DatabaseConfiguration databaseConfiguration = xmlNodeToDatabaseConfiguration(dbNode) ;
            databasesConfigurations.storeDatabaseConfiguration(databaseConfiguration);
        }
        
        return databasesConfigurations ;
	}

	public void save(DatabasesConfigurations databasesConfigurations) throws TelosysToolsException {

		Document document = XmlUtil.createDomDocument();
		
		//--- XML root element
		Element rootElement = document.createElement(ConstXML.DATABASES_ROOT_ELEMENT);
		rootElement.setAttribute(ConstXML.DATABASES_MAX_ID_ATTRIBUTE,     ""+databasesConfigurations.getDatabaseMaxId() );
		rootElement.setAttribute(ConstXML.DATABASES_DEFAULT_ID_ATTRIBUTE, ""+databasesConfigurations.getDatabaseDefaultId() );
		document.appendChild(rootElement);		
		
		//--- XML "db" elements
		List<DatabaseConfiguration> list = databasesConfigurations.getDatabaseConfigurationsList();
		for ( DatabaseConfiguration databaseConfiguration : list ) {
			
			//--- Create Element
			Element dbElement = document.createElement(ConstXML.DB_ELEMENT);
			//--- Populate Element
			databaseConfigurationToXmlElement(databaseConfiguration, document, dbElement);
			//--- Add Element in root
			rootElement.appendChild(dbElement);
		}
		
		//--- Save in file
		XmlFileUtil.save( document, file );
		
	}
	
	private DatabaseConfiguration xmlNodeToDatabaseConfiguration(Node dbNode) throws TelosysToolsException {
		

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        
        databaseConfiguration.setDatabaseId    ( XmlUtil.getNodeAttributeAsInt(dbNode, ConstXML.DB_ID_ATTRIBUTE));
        databaseConfiguration.setDatabaseName  ( XmlUtil.getNodeAttribute(dbNode, ConstXML.DB_NAME_ATTRIBUTE) );
        databaseConfiguration.setJdbcUrl       ( XmlUtil.getNodeAttribute(dbNode, ConstXML.DB_URL_ATTRIBUTE));
        databaseConfiguration.setDriverClass   ( XmlUtil.getNodeAttribute(dbNode, ConstXML.DB_DRIVER_ATTRIBUTE));
        
        databaseConfiguration.setIsolationLevel( XmlUtil.getNodeAttribute(dbNode, ConstXML.DB_ISOLATION_LEVEL_ATTRIBUTE));
        databaseConfiguration.setPoolSize      ( XmlUtil.getNodeAttributeAsInt(dbNode, ConstXML.DB_POOLSIZE_ATTRIBUTE));
        
        Element dbElem = (Element) dbNode;

        //--- Database properties : n sub-elements "<property name="xxx" value="yyy" />
        NodeList propertyTags = dbElem.getElementsByTagName(ConstXML.DB_PROPERTY_ELEMENT);
        Properties properties = new Properties();
        int iPropCount = propertyTags.getLength();
        for ( int i = 0 ; i < iPropCount ; i++ )
        {
            Node node = propertyTags.item(i);
            if (node != null)
            {
                if (node instanceof Element)
                {
                    Element elemProperty = (Element) node;

                    String sName  = elemProperty.getAttribute(ConstXML.DB_PROPERTY_NAME_ATTRIBUTE);
                    String sValue = elemProperty.getAttribute(ConstXML.DB_PROPERTY_VALUE_ATTRIBUTE);
                    properties.setProperty(sName, sValue);
                    
                    if ( "user".equals(sName) ) {
                    	databaseConfiguration.setUser(sValue);
                    }
                    if ( "password".equals(sName) ) {
                    	databaseConfiguration.setPassword(sValue);
                    }
                }
            }
        }
        return databaseConfiguration ;
	}

	private void databaseConfigurationToXmlElement(DatabaseConfiguration databaseConfiguration, Document document, Element dbElement) throws TelosysToolsException {
		
		dbElement.setAttribute(ConstXML.DB_ID_ATTRIBUTE,     ""+databaseConfiguration.getDatabaseId());
		dbElement.setAttribute(ConstXML.DB_NAME_ATTRIBUTE,   databaseConfiguration.getDatabaseName());
		dbElement.setAttribute(ConstXML.DB_URL_ATTRIBUTE,    databaseConfiguration.getJdbcUrl());
		dbElement.setAttribute(ConstXML.DB_DRIVER_ATTRIBUTE, databaseConfiguration.getDriverClass() );
        
		dbElement.setAttribute(ConstXML.DB_ISOLATION_LEVEL_ATTRIBUTE, databaseConfiguration.getIsolationLevel() );
		dbElement.setAttribute(ConstXML.DB_POOLSIZE_ATTRIBUTE,        ""+databaseConfiguration.getPoolSize() );
        
		//--- <property name="user"      value="xxx" />
		dbElement.appendChild( createPropertyElement(document, "user",     databaseConfiguration.getUser() ) );
		//--- <property name="password"  value="xxx" />
		dbElement.appendChild( createPropertyElement(document, "password", databaseConfiguration.getPassword() ) );
	}
	
	private Element createPropertyElement(Document document, String name, String value) throws TelosysToolsException {
		Element propertyElement = document.createElement(ConstXML.DB_PROPERTY_ELEMENT);
		propertyElement.setAttribute(ConstXML.DB_PROPERTY_NAME_ATTRIBUTE,  name );
		propertyElement.setAttribute(ConstXML.DB_PROPERTY_VALUE_ATTRIBUTE, value );
		return propertyElement ;
	}
}