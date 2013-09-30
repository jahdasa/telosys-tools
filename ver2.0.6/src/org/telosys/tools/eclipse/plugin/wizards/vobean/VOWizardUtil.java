package org.telosys.tools.eclipse.plugin.wizards.vobean;

import org.telosys.tools.commons.javatypes.JavaTypes;
import org.telosys.tools.commons.javatypes.JavaTypesManager;

public class VOWizardUtil {

	protected static String getTypeText(int i) 
	{
		JavaTypes javaTypes = JavaTypesManager.getJavaTypes();
		return javaTypes.getText(i);
	}

	protected static String[] getJavaTypeTexts() 
	{
		JavaTypes javaTypes = JavaTypesManager.getJavaTypes();
		return javaTypes.getTexts() ;
	}
}
