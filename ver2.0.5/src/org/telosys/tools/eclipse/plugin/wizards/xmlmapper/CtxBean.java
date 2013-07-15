package org.telosys.tools.eclipse.plugin.wizards.xmlmapper;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.telosys.tools.commons.JavaTypeUtil;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.generator.context.JavaBeanClassAttribute;

/**
 * @author Laurent GUERIN
 * 
 * Context class for BEAN
 *  
 */
public class CtxBean {

	private Hashtable _htAttributes = new Hashtable();
	
//	private static final String INFO = "???";

	//-------------------------------------------------------------
	public CtxBean(IType type) {
		PluginLogger.log("=== CONSTRUCTOR : CtxBean() : "
				+ type.getElementName());

		//		IField[] fields = type.getFields() ;

		IMethod[] methods = getMethods(type);
		if ( methods != null )
		{
			build(methods);
		}
	}

	//-------------------------------------------------------------
	/**
	 * Returns the methods and constructors declared by this type
	 * @param type
	 * @return
	 */
	private IMethod[] getMethods(IType type) {
		IMethod[] methods = null;
		try {
			methods = type.getMethods();
		} catch (JavaModelException e) {
			MsgBox.error("Cannot get methods : Bean = "
							+ type.getElementName());
			e.printStackTrace();
			methods = null;
		}
		return methods;
	}

	//-------------------------------------------------------------
	private String primitiveArray(char c, String s) {
		String sType = primitiveType(c);
		if ( sType != null )
		{
			return sType + s ;
		}
		return null ;
	}
	//-------------------------------------------------------------
	private String primitiveType(char c) {
		if ( c == 'I') return "int" ;
		if ( c == 'S') return "short" ;
		if ( c == 'J') return "long" ;			
		if ( c == 'V') return "void" ;
		if ( c == 'C') return "char" ;
		if ( c == 'B') return "byte" ;
		if ( c == 'F') return "float" ;
		if ( c == 'D') return "double" ;
		if ( c == 'Z') return "boolean" ;
		return null ;
	}
	//-------------------------------------------------------------
	private String typeFromSignature(String sSignatureType) {
		if ( sSignatureType == null) return null ;
		String sTrim = sSignatureType.trim();
		int iLength = sTrim.length();
		final char cFirstChar = sTrim.charAt(0);
		if ( iLength == 1)
		{			
			//--- Primitive Types
			return primitiveType(cFirstChar);
		}
		
		//--- Object types : 
		// "Q...;" = resolved type (source code) 
		// "L...;" = unresolved type (compiled code)
		// e.g : "QString;" -> "String", "QDate;" -> "Date", "Qjava.sql.Date;" -> "java.sql.Date"
		if ( cFirstChar == 'Q' || cFirstChar == 'L' )
		{			
			int iLast = sTrim.length()-1 ;
			if ( sTrim.charAt(iLast) == ';') // last char is ";"
			{
				return sTrim.substring(1,iLast);
			}
		}
		
		//--- Array : "[I" -> "int[]", "[[I" -> "int[][]"
		if ( cFirstChar == '[' )
		{			
			if ( iLength == 2 ) // "[I" -> "int[]"
			{
				return primitiveArray(sTrim.charAt(1),"[]");
			}
			if ( iLength == 3 ) 
			{
				if ( sTrim.charAt(1) == '[' ) // "[[I" -> "int[][]"
				{
					return primitiveArray(sTrim.charAt(2),"[][]");
				}
			}
		}
		
		return null ;
	}
	//-------------------------------------------------------------
	private String getReturnType(IMethod method) {
    	String sType = null;
    	if ( method != null )
    	{
			try {
				sType = method.getReturnType();
			} catch (JavaModelException e) {
				MsgBox.error("Cannot get method type " + method.getElementName() );
				e.printStackTrace();
			}
    	}
		return sType ;
	}

	//-------------------------------------------------------------
	private void build(IMethod[] methods) {
		if ( methods == null )
		{
			MsgBox.error("IMethod[] parameter is null !");
			return ;
		}
        for ( int i = 0 ; i < methods.length ; i++ )
        {
        	IMethod method = methods[i];
        	String sMethodName = method.getElementName();
        	String sMethodTypeSign = getReturnType(method);
        	String sMethodType = typeFromSignature(sMethodTypeSign); // can be a "short type" or "full type"

        	PluginLogger.log("methods["+i+"] : name : " + sMethodName +", type : "+ sMethodTypeSign +" ->" + sMethodType  );
        	
        	processAttribute(sMethodName, sMethodType, methods);
//        	String sAttribute = getAttributeName( sMethodName ); 
//        	PluginLogger.log("Method [" + i + "] : name = " + sMethodName + " --> attribute " + sAttribute );
//        	if ( sAttribute != null )
//        	{
//                registerAttribute(sAttribute);
//        	}
        }
	}
	
	//-------------------------------------------------------------
	private void processAttribute( String sMethodName, String sMethodType, IMethod[] methods ) 
	{
    	PluginLogger.log("process (" + sMethodName +","+ sMethodType +",...)" );
		String sAttributePart = getAttributePart( sMethodName ); // returns "Xxx" for "getXxx" or "isXxx"
		if ( sAttributePart != null )
		{
			String sAttributeName = firstCharLowerCase( sAttributePart );
			
			String sAttributeShortType = JavaTypeUtil.shortType(sMethodType);
			String sAttributeFullType  = JavaTypeUtil.fullType(sMethodType);

			String sGetter = sMethodName ; // getAaaa or isAaaa
			String sSetter = findSetter(sAttributePart, methods );

			
			//--- Keep this attribute
			registerAttribute( sAttributeName, sAttributeShortType, sAttributeFullType, sGetter, sSetter) ;
		}
	}
	
	//-------------------------------------------------------------
	private String getAttributePart( String sMethodName ) 
	{
        if ( sMethodName == null )
        {
        	return null;
        }
		String s = sMethodName.trim();
		
		//--- "getAaaaaBbbb()"  -->  "aaaaBbbbb"
        if ( s.startsWith("get") )
        {
        	// "getClass" is not in the methods list 
        	// Eclipse Java Model : only the methods of current class ( IType ) 
        	if ( s.startsWith("getClass") ) 
        	{
        		return null ;
        	}
        	if ( s.length() > 3 )
        	{
        		return s.substring(3);
        		//return firstCharLowerCase( s.substring(3) );
        	}
    		return null ;
        }
        
		//--- "isAaaaaBbbb()"  -->  "aaaaBbbbb"
        if ( s.startsWith("is") )
        {
        	if ( s.length() > 2 )
        	{
        		return s.substring(2);
        		//return firstCharLowerCase( s.substring(2) );
        	}
        }
        return null;
	}
	
	//-------------------------------------------------------------
	/**
	 * Search the "setter" in the methods list for the given attribute part 
	 * 
	 * @param sAttributePart
	 * @param methods
	 * @return
	 */
	private String findSetter( String sAttributePart, IMethod[] methods ) 
	{
		//--- Search the "setter" in the methods list
		String sSetter = "set" + sAttributePart ;
		for ( int i=0 ; i < methods.length ; i++)
		{
        	IMethod method = methods[i];
        	if ( method != null )
        	{        		
	        	String s = method.getElementName();
	        	if ( s.equals(sSetter))
	        	{
	        		return s ;
	        	}
        	}
		}
		return null ; // Not found
	}
	
	//-------------------------------------------------------------
	private String firstCharLowerCase( String s ) 
	{
		if ( s == null )
		{
			return null ;
		}
		char firstChar = s.charAt(0);
		if ( firstChar >= 'A' && firstChar <= 'Z' )
		{
			String s1 = "" + firstChar ;
			return s1.toLowerCase() + s.substring(1) ;
		}
		return s ;
	}
	
	//-------------------------------------------------------------
	private void registerAttribute( String sName, String sType, String sFullType, String sGetter, String sSetter) {
    	PluginLogger.log("addAttribute (" + sName +","+ sType +"," + sFullType + "," + sGetter +","+ sSetter+")" );
    	
    	//--- Check if not yet registered ( if "getXxxxx" and "isXxxx" for the same attribute )
    	Object obj = _htAttributes.get(sName);
    	if ( obj != null )
    	{
    		// Can be already registered if there's 2 getters ( getAaa + isAAA )
        	//OldJavaClassAttribute attr = (OldJavaClassAttribute) obj ;
        	JavaBeanClassAttribute attr = (JavaBeanClassAttribute) obj ;
        	String sGetterRegistered = attr.getGetter();
        	if ( sGetterRegistered != null )
        	{
        		if ( sGetterRegistered.startsWith("get") && sGetter.startsWith("is") )
        		{
        			return ; // Keep the getter registered ( "getXxx" ) 
        		}
        	}
    	}
    	//--- Register 
		//OldJavaClassAttribute attr = new OldJavaClassAttribute(sName, sType, sFullType, null, sGetter, sSetter);
		JavaBeanClassAttribute attr = new JavaBeanClassAttribute(sName, sType, sFullType, null, sGetter, sSetter);
		_htAttributes.put(sName, attr);
	}
	
	//-------------------------------------------------------------
//	public OldJavaClassAttribute[] getAttributes() 
	public JavaBeanClassAttribute[] getAttributes() 
	{
		PluginLogger.log("=== CtxBean : getAttributes() : " );
		//--- Number of elements 
		int iCount = 0 ;
		Enumeration e = _htAttributes.elements();
		while ( e.hasMoreElements() )
		{
			if ( e.nextElement() != null )
			{
				iCount++;
			}
		}
		PluginLogger.log("=== CtxBean : getAttributes() : iCount = " + iCount );
		
		//--- Array of N elements 
		Object obj = null ;
//		OldJavaClassAttribute[] array = new OldJavaClassAttribute[iCount];
		JavaBeanClassAttribute[] array = new JavaBeanClassAttribute[iCount];
		int i = 0 ;
		e = _htAttributes.elements();
		while ( e.hasMoreElements() )
		{
			obj = e.nextElement();
			if ( obj != null )
			{
				if ( i < array.length )
				{
//					array[i++] = (OldJavaClassAttribute) obj;
					array[i++] = (JavaBeanClassAttribute) obj;
				}
			}
		}
		return array ;
	}
}