package org.telosys.tools.generator.context.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VelocityObject {
	String   contextName() ;  // Mandatory
	String[] text() ; // Mandatory
	String   since()       default "" ; 
	boolean  deprecated()  default false ;
}
