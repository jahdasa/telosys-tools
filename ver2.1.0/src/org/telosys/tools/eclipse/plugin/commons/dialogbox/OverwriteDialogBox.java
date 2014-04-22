package org.telosys.tools.eclipse.plugin.commons.dialogbox;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class OverwriteDialogBox {

	public final static int YES        = 0 ;
	public final static int YES_TO_ALL = 1 ;
	public final static int NO         = 2 ;
	public final static int NO_TO_ALL  = 3 ;
	public final static int CANCEL     = 4 ;
	
	public static int confirm(String fileName ) 
	{
		String message = "Overwrite file \n"
			+ "'" + fileName + "' ?" ;
		return openDialogBox( message) ;
	}

	public static int confirm(String file, String folder ) 
	{
		String message = "Overwrite file '" + file + "' \n"
			+ "in folder '" + folder + "'  ? ";
		
		return openDialogBox( message) ;
	}
	
	private static int openDialogBox(String message) 
	{
		Shell shell = null ;
		MessageDialog dialog = new MessageDialog(
				shell, 
				"Question ", // Title
				null, // Image
				message, // Message in the dialog box body
			    MessageDialog.QUESTION, // Image type ( icon : ERROR, WARNING, ... )
			    new String[] { "Yes", "Yes To All", "No", "No To All", "Cancel" }, // Buttons labels
			    0); // Default index
			int choice = dialog.open();
			return choice ;
	}

}
