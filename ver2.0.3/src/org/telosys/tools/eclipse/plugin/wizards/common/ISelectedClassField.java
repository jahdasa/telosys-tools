package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Laurent GUERIN
 *
 */
public interface ISelectedClassField {
	
	public void setInComposite(Composite composite, int nColumns);

	public String getFieldValue() ;

	public void setFieldValue(String s) ;

}
