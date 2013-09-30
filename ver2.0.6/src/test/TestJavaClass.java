package test;

import org.telosys.tools.generator.context.JavaClass;

/**
 * @author l.guerin
 *
 */
public class TestJavaClass {

	public static void print ( JavaClass jc ) {
		System.out.println("-----" );
		System.out.println(" name    = '" + jc.getName() + "'");
		System.out.println(" package = '" + jc.getPackage() + "'");
		System.out.println(" SuperClass = '" + jc.getSuperClass() + "'");
	}
	
	public static void main(String[] args) {
		print( new JavaClass("MyClass", "package") ) ;
		print( new JavaClass("MyClass", "package1.pkg2") ) ;
		
		print( new JavaClass("package.MyClass") ) ;
		print( new JavaClass("pkg1.pkg2.MyClass") ) ;
		print( new JavaClass("MyClass") ) ;
	}
}
