package org.telosys.tools.eclipse.plugin.decorator;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.telosys.tools.eclipse.plugin.commons.PluginImages;

@SuppressWarnings("restriction")
public class FolderDecorator extends LabelProvider implements ILabelDecorator  {

	@Override
	public Image decorateImage(Image image, Object res) {
		Image telosysFolderImage = PluginImages.getImage(PluginImages.TELOSYS_FOLDER);
		if (res instanceof Folder) {

			// Apply decorator for "TelosysTools" (ignore case) or any "telosys*tools"
			Folder folder = (Folder) res;
			final String folderNameLowerCase = folder.getName().toLowerCase() ;
			if ( "telosystools".equalsIgnoreCase( folderNameLowerCase )  ) {
				return telosysFolderImage ;
			}
			else {
				if ( folderNameLowerCase.startsWith("telosys") 
						&& folderNameLowerCase.endsWith("tools") ) {
					return telosysFolderImage ;
				}
			}
		}
		return null;
	}
	
	@Override
	public String decorateText(String arg0, Object arg1) {
	// NOTHING
	return null;
	}
}
