package org.telosys.tools.eclipse.plugin.commons.test;

import java.util.HashMap;

import org.telosys.tools.commons.VariablesManager;

public class TestVariablesManager {

	private static HashMap<String,String> hm = new HashMap<String,String>();
		
	private static VariablesManager varmanager = null ;
	
	
	protected static void test(String s)
	{
		System.out.println("==========");
		String s2 = varmanager.replaceVariables(s);
		System.out.println("'" + s + "' --> '" + s2 + "'");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		hm.put("${VAR1}", "VALUE1");
		hm.put("${VAR2}", "VALUE2");
		hm.put("${VAR3}", "VALUE3");
		
		varmanager = new VariablesManager(hm) ;
		
		
		test(null);
		test("");
		test("aa");
		
		test(" aa ${VAR1} ");
		test("${VAR1}");
		test("${VAR1}zzz");
		test("aaa${VAR1}");
		test("aaa${VAR1}zzz");
		
		test("aaa${}zzz");

		test("aaa${VAR1}bbb${VAR2}zzz");
	}

}
