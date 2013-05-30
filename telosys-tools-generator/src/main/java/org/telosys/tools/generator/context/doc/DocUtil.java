package org.telosys.tools.generator.context.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DocUtil {
	
	public static <T extends Comparable<? super T>> List<T> sortList(Collection<T> c) {
		  List<T> list = new ArrayList<T>(c);
		  java.util.Collections.sort(list);
		  return list;
	}

}
