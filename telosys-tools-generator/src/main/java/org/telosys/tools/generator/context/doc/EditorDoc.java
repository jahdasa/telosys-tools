package org.telosys.tools.generator.context.doc;

import java.util.HashMap;
import java.util.Map;

public class EditorDoc {

	Map<String,String> classDoc   = new HashMap<String,String>();
	Map<String,String> methodsDoc = new HashMap<String,String>();
	
	private String buildKey( ClassInfo classInfo, MethodInfo methodInfo ) {
		return classInfo.getContextName() + "." + methodInfo.getSignature() ;
	}
	
	public String getClassDoc( ClassInfo classInfo ) {
		String name = classInfo.getContextName() ;
		String doc = classDoc.get( name ) ;
		if ( null == doc ) {
			doc = buildHtmlClassDoc( classInfo ) ;
			classDoc.put(name, doc);
		}
		return doc ;
	}
	
	public String getMethodDoc( ClassInfo classInfo, MethodInfo methodInfo) {
		String key = buildKey( classInfo, methodInfo);
		String doc = methodsDoc.get(key) ;
		if ( null == doc ) {
			doc = buildHtmlMethodDoc( classInfo, methodInfo ) ;
			classDoc.put(key, doc);
		}
		return doc ;
	}

	private String buildHtmlClassDoc( ClassInfo classInfo ) {
		// TODO
		return "???" ;
	}
	
	private String buildHtmlMethodDoc( ClassInfo classInfo, MethodInfo methodInfo ) {
		// TODO
		return "???" ;
	}
	
}
