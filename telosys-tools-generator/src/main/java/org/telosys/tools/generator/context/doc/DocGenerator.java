package org.telosys.tools.generator.context.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DocGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DocBuilder docBuilder = new DocBuilder();
		
		Map<String,ClassInfo> classesInfo = docBuilder.getVelocityClassesInfo() ;

		Set<String> names = classesInfo.keySet();
		
		List<String>sortedNames = sortList(names);
		
		DocGeneratorHTML htmlGenerator = new DocGeneratorHTML();
		
		System.out.println("Sorted context names : " );
		for ( String name : sortedNames ) {
			ClassInfo classInfo = classesInfo.get(name);
					//+ classInfo.getJavaClassName() + " " + classInfo.getMethodsCount() + " methods");
			String fileName = "D:/TMP/HTML/" + classInfo.getContextName() + ".html" ;
			System.out.println(" . " + classInfo.getContextName() + " --> " + fileName );
			htmlGenerator.generateDocFile(classInfo, fileName);
		}
	}
	
	public static <T extends Comparable<? super T>> List<T> sortList(Collection<T> c) {
		  List<T> list = new ArrayList<T>(c);
		  java.util.Collections.sort(list);
		  return list;
	}
}
