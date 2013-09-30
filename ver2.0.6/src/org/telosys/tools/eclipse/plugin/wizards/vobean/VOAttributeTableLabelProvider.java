package org.telosys.tools.eclipse.plugin.wizards.vobean;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;

public class VOAttributeTableLabelProvider extends LabelProvider implements ITableLabelProvider {

//	 Names of images used to represent checkboxes
	public static final String CHECKED_IMAGE 	= "icoCheckBoxChecked";
	public static final String UNCHECKED_IMAGE  = "icoCheckBoxUnChecked";

	// For the checkbox images
	private static ImageRegistry imageRegistry = new ImageRegistry();

	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */ 
	static {
		String iconPath = "icons/"; 
		
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
				null, 
				iconPath + CHECKED_IMAGE + ".gif"
				)
			);
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
				null, 
				iconPath + UNCHECKED_IMAGE + ".gif"
				)
			);	
	}
	
	/**
	 * Returns the image with the given key, or <code>null</code> if not found.
	 */
	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		PluginLogger.log(key);
		return imageRegistry.get(key);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		switch(columnIndex) {
		case 4:
			return getImage(((VOAttributeTableItem)element).isGetter());
		case 5:
			return getImage(((VOAttributeTableItem)element).isSetter());
		default:
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
        switch (columnIndex) {
        case 0:
          return ((VOAttributeTableItem) element).sFirstCol;
        case 1:
          return ((VOAttributeTableItem) element).sAttributeName;
        case 2:
          //return VOConst.ARRAY_OF_JAVA_TYPES[((VOAttributeTableItem) element).iType];
        	return VOWizardUtil.getTypeText( ((VOAttributeTableItem) element).iType ) ;
        case 3:
          return ((VOAttributeTableItem) element).sInitialValue;
        case 4:
          return (((VOAttributeTableItem) element).bGetter) ? "X" : "";                         
        case 5:
          return (((VOAttributeTableItem) element).bSetter) ? "X" : "";  
        default:
          return "";
        }
	}
}

