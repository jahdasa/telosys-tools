package org.telosys.tools.eclipse.plugin.commons.test;

import org.telosys.tools.commons.StrUtil;

/**
 * @author L. Guerin
 *
 */
public class TestRepVar {

	public static void main(String[] args) {
		System.out.println(StrUtil.replaceVar("aaaaa$TOTObbb", "$TOTO", "MyValue"));
		System.out.println(StrUtil.replaceVar("aaaaa${TOTO}bbb", "${TOTO}", "MyValue"));
		System.out.println(StrUtil.replaceVar("aaaaa$bbb", "$", ""));
	}
}
