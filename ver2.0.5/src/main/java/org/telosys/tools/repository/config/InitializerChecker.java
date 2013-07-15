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
package org.telosys.tools.repository.config;

/**
 * @author L.GUERIN
 * 
 */
public interface InitializerChecker
{
    /**
     * Returns the Java bean class name for the given table name
     * @param sTableName
     * @return
     */
    public String getJavaBeanClassName(String sTableName);
    

    /**
     * Returns the Java attribute name for the given column 
     * @param sColumnName the name of the column in the database
     * @param sColumnTypeName the native database type
     * @param iJdbcTypeCode the JDBC type code
     * @return the attribute name
     */
    public String getAttributeName(String sColumnName, String sColumnTypeName, int iJdbcTypeCode);

    
    /**
     * Returns the Java attribute type for the given column 
     * @param sColumnTypeName the native database type
     * @param iJdbcTypeCode the JDBC type code
     * @param bColumnNotNull flag "NOT NULL" column
     * @return the attribute type
     */
    public String getAttributeType(String sColumnTypeName, int iJdbcTypeCode, boolean bColumnNotNull );


    public String getAttributeLongTextFlag(String sColumnType, int iColumnTypeCode, String sJavaType );

    
    public String getAttributeDateType(String sColumnType, int iColumnTypeCode, String sJavaType );

    /**
     * Returns the Java attribute label for the given column 
     * @param sColumnName the name of the column in the database
     * @param sColumnTypeName the native database type
     * @param iJdbcTypeCode the JDBC type code
     * @return the attribute name
     */
    public String getAttributeLabel(String sColumnName, String sColumnTypeName, int iJdbcTypeCode);

    /**
     * Returns the Java attribute input type for the given column 
     * @param sColumnName the name of the column in the database
     * @param sColumnTypeName the native database type
     * @param iJdbcTypeCode the JDBC type code
     * @return the attribute name
     */
    public String getAttributeInputType(String sColumnName, String sColumnTypeName, int iJdbcTypeCode, String sJavaType);

}