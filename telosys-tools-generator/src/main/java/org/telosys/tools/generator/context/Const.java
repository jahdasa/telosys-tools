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

/**
 * Constants usable in a Velocity template
 * 
 * @author Laurent GUERIN
 *
 */
public class Const {

	//--- Attributes filter criteria
	public final static int KEY      = 1 ;
	public final static int NOT_KEY  = 2 ;
	
	public final static int TEXT     = 4 ;
	public final static int NOT_TEXT = 8 ;
	
	public final static int IN_LINKS     = 16 ;
	public final static int NOT_IN_LINKS = 32 ;
	
	public final static int IN_SELECTED_LINKS      =  64 ;
	public final static int NOT_IN_SELECTED_LINKS  = 128 ;
	
	
	public int getKEY() {
		return KEY ;
	}
	public int getNOT_KEY() {
		return NOT_KEY ;
	}

	public int getTEXT() {
		return    TEXT ;
	}
	public int getNOT_TEXT() {
		return    NOT_TEXT ;
	}
	
	public int getIN_LINKS() {
		return    IN_LINKS ;
	}
	public int getNOT_IN_LINKS() {
		return    NOT_IN_LINKS ;
	}
	
	public int getIN_SELECTED_LINKS() {
		return    IN_SELECTED_LINKS ;
	}
	public int getNOT_IN_SELECTED_LINKS() {
		return    NOT_IN_SELECTED_LINKS ;
	}
	
	public Object getNULL() {
		return null ;
	}
}
