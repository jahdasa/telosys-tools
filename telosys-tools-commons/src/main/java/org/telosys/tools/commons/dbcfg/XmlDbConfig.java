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

import java.io.File;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.XmlDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class holding all the databases configurations loaded from a "databases.dbcfg" XML file.
 * 
 * @author Laurent GUERIN
 *  
 */
@Deprecated
public class XmlDbConfig
{
	private XmlDocument _xmlDoc = new XmlDocument(null);
	
	//private String   _filename = null ;
	//private IFile      _file = null ;
	private File       _file = null ;
    private Document   _doc = null;

    //------------------------------------------------------------------------------------------------------

//    public XmlDbConfig(IFile file) throws TelosysToolsException
    public XmlDbConfig(File file) throws TelosysToolsException
    {
        //File fileDbConfig = new File(sFileName);
        if ( file != null )
        {
        	_file = file ;
//            _doc = Xml.load(_file);
//            _doc = _xmlDoc.load( EclipseWksUtil.toFile(_file) );
            _doc = _xmlDoc.load( _file );
        }
        else
        {
            //MsgBox.error("XmlDbConfig() [Constructor] : The file " + sFileName + " doesn't exist ! ");
            throw new TelosysToolsException ("XmlDbConfig() [Constructor] : The file paramater is null ");
        }
    }

    //------------------------------------------------------------------------------------------------------
//    public String getFileName()
//    {
//        return _filename ;
//    }

    //------------------------------------------------------------------------------------------------------
    public Document getDocument()
    {
        return _doc ;
    }

    //------------------------------------------------------------------------------------------------------
    public void save() throws TelosysToolsException
    {
        if ( _doc != null )
        {
    		//Xml.save( _doc, _file);
//    		_xmlDoc.save(_doc, EclipseWksUtil.toFile(_file) );
    		_xmlDoc.save(_doc, _file );
        }
        else
        {
			throw new TelosysToolsException ("save : The XML document is null ");
        }
    }
    
    //------------------------------------------------------------------------------------------------------
    public boolean isLoaded()
    {
        return (_doc != null ? true : false);
    }

    //------------------------------------------------------------------------------------------------------
    public XmlDatabase[] getDatabases()
    {
        Element root = _doc.getDocumentElement();
        if (root != null)
        {
            NodeList basesList = root.getElementsByTagName(ConstXML.DB_ELEMENT);
            if (basesList != null)
            {
            	int nbDatabases = basesList.getLength() ;
            	XmlDatabase[] databases = new XmlDatabase[nbDatabases];
                for ( int i = 0 ; i < nbDatabases ; i++ )
                {
                	Node node = basesList.item(i);
                	XmlDatabase db = new XmlDatabase(node);
                	databases[i] = db ;
                }
                return databases ;
            }
        }
    	return new XmlDatabase[0];
    }
//    //------------------------------------------------------------------------------------------------------
//    public void loadComboBases(Combo comboBases)
//    {
//        comboBases.removeAll(); 
//        Element root = _doc.getDocumentElement();
//        if (root == null)
//        {
//            MsgBox.error("loadComboBase() : No XML document ( DOM root is null ) ");
//        }
//        else
//        {
//            NodeList basesList = root.getElementsByTagName(ConstXML.DB_ELEMENT);
//            if (basesList != null)
//            {
//                for ( int i = 0 ; i < basesList.getLength() ; i++ )
//                {
//                    Element base = (Element) basesList.item(i);
//
////                    String sDbId = Xml.getNodeAttribute(base, ConstXML.DB_ID_ATTRIBUTE);
////                    String sDbName = Xml.getNodeAttribute(base, ConstXML.DB_NAME_ATTRIBUTE);
//                    
//                    String sDbId   = _xmlDoc.getNodeAttribute(base, ConstXML.DB_ID_ATTRIBUTE);
//                    String sDbName = _xmlDoc.getNodeAttribute(base, ConstXML.DB_NAME_ATTRIBUTE);
//                    
//                    if (sDbId == null)
//                        sDbId = "?";
//                    if (sDbName == null)
//                        sDbName = "?????";
//                    String sItem = sDbId + " - " + sDbName;
//                    comboBases.add(sItem);
//                }
//            }
//        }
//        return;
//    }

//    //------------------------------------------------------------------------------------------------------
//    public NodeList getDatabaseNodes() 
//    {
//    	if ( _doc != null )
//    	{
//    		return _doc.getElementsByTagName(ConstXML.DB_ELEMENT);
//    	}
//    	else
//    	{
//    		MsgBox.error("populateDatabases : XML document is null !");
//    		return null ;
//    	}
//    	
//    }
//
    //------------------------------------------------------------------------------------------------------
    public XmlDatabase getDatabaseConfig(String sDatabaseId) //throws TelosysToolsException
    {
        Element root = _doc.getDocumentElement();
        if (root == null)
        {
//            MsgBox.error("getDatabaseConfig() : No XML document ( DOM root is null ) ");
//            return null;
//            throw new TelosysToolsException("No XML document ( DOM root is null )" );
        	return null ;
        }
        NodeList dbList = root.getElementsByTagName(ConstXML.DB_ELEMENT);
        if (dbList == null)
        {
//            MsgBox.warning("getDatabaseConfig() : No database in the XML document !");
//            return null;
//            throw new TelosysToolsException("No " + ConstXML.DB_ELEMENT + " in the DOM");
        	return null ;
        }
        //--- Search the database with the good ID in the list
        for ( int i = 0 ; i < dbList.getLength() ; i++ )
        {
            Node dbNode = dbList.item(i);
            //String sAttrId = Xml.getNodeAttribute(dbNode, ConstXML.DB_ID_ATTRIBUTE);
            String sAttrId = _xmlDoc.getNodeAttribute(dbNode, ConstXML.DB_ID_ATTRIBUTE);
            
            if (sAttrId != null)
            {
                if (sAttrId.trim().equals(sDatabaseId.trim()))
                {
                    //--- Build a Database Config object
                    return new XmlDatabase(dbNode);
                }
            }
        }
        return null;
    }

}