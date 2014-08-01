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
package org.telosys.tools.commons.io ;

import java.io.File;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsLogger;


public class ResourcesCopier {
	
	private final TelosysToolsLogger _logger;
	private final OverwriteChooser   _overwriteChooser;
	private Boolean overwriteGlobalChoice = null ;

	public ResourcesCopier(OverwriteChooser overwriteChooser) {
		super();
		this._logger = null ;
		this._overwriteChooser = overwriteChooser ;
	}
	
	public ResourcesCopier(OverwriteChooser overwriteChooser, TelosysToolsLogger _logger) {
		super();
		this._logger = _logger;
		this._overwriteChooser = overwriteChooser ;
	}

	//----------------------------------------------------------------------------------------------------
	private void log(String s) {
		if (_logger != null) {
			_logger.log( this.getClass().getSimpleName() + " : " + s);
		}
	}

	//----------------------------------------------------------------------------------------------------
	/**
	 * Copies a file to another one, or a file to a directory, or a directory in another one <br>
	 * @param origin original file or folder
	 * @param destination  destination file or folder
	 * @return
	 * @throws Exception
	 */
	public int copy(File origin , File destination) throws Exception {
		if ( origin == null ) {
			throw new IllegalArgumentException("origin is null");
		}
		if ( destination == null ) {
			throw new IllegalArgumentException("destination is null");
		}
		int n = 0 ; 
		if ( origin.isFile() && destination.exists() && destination.isDirectory() ) {
			// Copy single file to directory
			FileUtil.copyToDirectory(origin, destination, true);
			n = 1 ;
		}
		else {
			n = recursiveCopy(origin, destination); 
		}
		return n ;
	}
	
	//----------------------------------------------------------------------------------------------------
	/**
	 * Copies a file to another one, or a directory in another one <br>
	 * @param origin original file or folder
	 * @param destination  destination file or folder
	 * @return
	 * @throws Exception
	 */
	private int recursiveCopy(File origin , File destination) throws Exception {
		int count = 0 ;
	    if (origin.isDirectory()) {
	    	// Source is a directory => destination is supposed to be a directory 
	        if ( destination.exists() ) {
	        	if ( ! destination.isDirectory() ) {
	        		throw new Exception("'" + destination + "' is not a directory");
	        	}
	        }
	        else {
	            destination.mkdir();
	        }
	        // Copy recursively the content 
	        String[] children = origin.list();
	        for (String child : children ) {
	        	count = count + recursiveCopy( new File(origin, child), new File(destination, child) );
	        }
	    } else {
	    	// Source is a file 
    		if ( destination.exists() ) {
    			// Destination exists 
    			if ( destination.isFile() ) {
        			// Destination exists and is a file
    				if ( getOverwriteChoice(destination) ) {
    					// overwrite the existing file
    		    		FileUtil.copy(origin, destination, true);
    	        		return 1 ;
    				}
    			}
    			else {
        			// Destination exists and is NOT a file => error
	        		throw new Exception("'" + destination + "' already exists and is not a file");
    			}
    		}
    		else {
    			// Destination doesn't exist => copy 
        		FileUtil.copy(origin, destination, true);
        		return 1 ;
    		}
	    }
	    return count ;
	}
	
	private boolean getOverwriteChoice(File file) throws Exception {
		if ( overwriteGlobalChoice != null ) {
			log("destination file exists : global choice set : " + overwriteGlobalChoice ) ;
			return overwriteGlobalChoice ;
		}
		else {
			log("destination file exists : global choice not set => message box for confirmation") ;
//			String fileFolder = destFile.getParent().getFullPath().toString() ;
//			int choice = OverwriteDialogBox.confirm(destFile.getName(), fileFolder );
			int choice = _overwriteChooser.choose(file.getName(), file.getParent() );
			log(" choice = " + choice) ;
			switch (choice) {
			
			case OverwriteChooser.YES_TO_ALL :
				overwriteGlobalChoice = true ;
			case OverwriteChooser.YES :
				return true ;

			case OverwriteChooser.NO_TO_ALL :
				overwriteGlobalChoice = false ;
			case OverwriteChooser.NO :
				return false ;

			default: // CANCEL
				throw new Exception("CANCEL");
//				taskCanceled = true ;
//				copy = false ;
//				break;
			}
		}
		
	}
}
