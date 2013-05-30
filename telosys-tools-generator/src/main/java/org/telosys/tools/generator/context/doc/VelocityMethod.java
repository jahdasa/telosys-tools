package org.telosys.tools.generator.context.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VelocityMethod {
	
	String[] text() ;      // Mandatory (no default value)
	String[] example()     default {} ;
	String[] parameters()  default {} ; 
	
	String   since()       default "" ; 
	boolean  deprecated()  default false ;
}
