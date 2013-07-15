package org.telosys.tools.eclipse.plugin.wizards.common;

import org.eclipse.swt.layout.GridData;

public class Cell {

	// --- The plug-in ID
	private static GridData H_SPAN_2 = null ;
	private static GridData H_SPAN_3 = null ;

	public static final GridData HSpan2()
	{
		if ( H_SPAN_2 == null )
		{
			H_SPAN_2 = new GridData();
			H_SPAN_2.horizontalSpan = 2;
		}
		return H_SPAN_2 ;
	}

	public static final GridData HSpan3()
	{
		if ( H_SPAN_3 == null )
		{
			H_SPAN_3 = new GridData();
			H_SPAN_3.horizontalSpan = 3;
		}
		return H_SPAN_3 ;
	}

}
