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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The current system date and time
 *  
 * @author Laurent GUERIN
 *
 */
public class Today
{
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("d MMM yyyy");
    private static final SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public String getDate()
    {
        return defaultDateFormat.format( new Date() );
    }
    public String date( String sFormat )
    {
        SimpleDateFormat frm = new SimpleDateFormat(sFormat);
        return frm.format( new Date() );
    }

    public String getTime()
    {
        return defaultTimeFormat.format( new Date() );
    }
    
    public String time( String sFormat )
    {
        SimpleDateFormat frm = new SimpleDateFormat(sFormat);
        return frm.format( new Date() );
    }
}