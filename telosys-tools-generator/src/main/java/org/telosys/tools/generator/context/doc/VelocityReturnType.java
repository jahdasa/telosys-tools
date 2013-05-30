package org.telosys.tools.generator.context.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VelocityReturnType {
	String value() default "" ;
}
