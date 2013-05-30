package org.telosys.tools.generator.context.doc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.telosys.tools.generator.context.Const;
import org.telosys.tools.generator.context.EmbeddedGenerator;
import org.telosys.tools.generator.context.Fn;
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.generator.context.JavaBeanClassAttribute;
import org.telosys.tools.generator.context.Loader;
import org.telosys.tools.generator.context.ProjectConfiguration;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.context.Today;

public class DocBuilder {

	private static List<String> objectClassMethods = new LinkedList<String>() ;
	{
		Method[] methods = Object.class.getMethods();
		for ( Method m : methods ) {
			objectClassMethods.add(m.getName());
		}
	}
	
	private boolean isObjectClassMethod(Method method) {
		String name = method.getName() ;
		for ( String s : objectClassMethods ) {
			if ( s.equals(name) ) {
				return true ; 
			}
		}
		return false ;
	}
	
	/**
	 * Returns the Velocity name for the given method <br>
	 * ie : "tab" for "getTab"
	 * @param method
	 * @return
	 */
	private String getVelocityMethodName(Method method) {
		String name = method.getName() ;
		if ( name.startsWith("get") ) {
			if ( method.getParameterTypes().length == 0 ) {
				String s = name.substring(3);
				if ( method.getAnnotation(VelocityConstant.class) != null ) {
					// Annotated as a constant : keep the name as is
					return s ;
				}
				else {
					// Converts first char to lower case ( ie "Tab" to "tab" )
					byte[] bytes = s.getBytes();
					byte first = bytes[0];
					if ( first >= 'A' && first <= 'Z' ) {
						bytes[0] = (byte) ( first + 'a' - 'A' ) ;
					}
					return new String(bytes);
				}
			}
		}
		return name ;
	}
	
	
	private String getVelocityReturnType(Method method) {
		
		VelocityReturnType velocityReturnType = method.getAnnotation(VelocityReturnType.class);
		if ( velocityReturnType != null ) {
			//--- There's an annotation for a specific return type => use it
			return velocityReturnType.value() ;
		}
		else {
			//--- Get the Java return type
			Type type = method.getGenericReturnType();
			return TypeUtil.typeToString(type);
		}
	}
	
	private MethodParameter buildParameter(int i, Class<?> paramClass, VelocityMethod docAnnotation) {
		String paramType = paramClass.getSimpleName();
		String paramDoc  = null ;
		if ( docAnnotation != null ) {
			String[] parametersDoc = docAnnotation.parameters();
			if ( parametersDoc != null && i < parametersDoc.length ) {
				paramDoc = parametersDoc[i] ;
			}
		}
		if ( paramDoc != null ) {
			return new MethodParameter(paramType, paramDoc );
		}
		else {
			return new MethodParameter(paramType);
		}
	}
	
	private MethodInfo getMethodInfo(Method method) {
		
		if ( method.getAnnotation(VelocityNoDoc.class) != null ) {
			// No documentation for this method 
			return null ;
		}
		
		MethodInfo methodInfo = new MethodInfo();
		
		//--- Original Java method name 
		methodInfo.setJavaName( method.getName() );
		
		//--- Velocity method name 
		methodInfo.setVelocityName( getVelocityMethodName(method) );
		
		//--- Return type  
		methodInfo.setReturnType( getVelocityReturnType(method) );
		
		//methodInfo.setParamTypes( paramTypesList.toArray(new String[0]) );

		LinkedList<MethodParameter> parameters = new LinkedList<MethodParameter>();
		//--- Documentation  
		VelocityMethod docAnnotation = method.getAnnotation(VelocityMethod.class);
		if ( docAnnotation != null ) {
			methodInfo.setDocText( docAnnotation.text() );
			methodInfo.setExampleText( docAnnotation.example() );
			methodInfo.setSince( docAnnotation.since() );
			methodInfo.setDeprecated( docAnnotation.deprecated() );	
			//methodInfo.setParamNames( docAnnotation.parameters() );
//			//--- Parameters
//			String[] parametersDoc = docAnnotation.parameters();
//			int i = 0 ;
//			for ( Class<?> c : paramTypes ) {
//				String paramDoc = "" ;
//				if ( i < parametersDoc.length ) {
//					paramDoc = parametersDoc[i] ;
//				}
//				//--- Parameter type + parameter documentation 
//				parameters.add( new MethodParameter(c.getSimpleName(), paramDoc ) );
//				i++;
//			}
		}
		
		//--- Parameters types
		//LinkedList<String> paramTypesList = new LinkedList<String>();
		Class<?>[] paramTypes = method.getParameterTypes();
		if ( paramTypes != null ) {
			int i = 0 ;
			for ( Class<?> paramClass : paramTypes ) {
				parameters.add( buildParameter(i, paramClass, docAnnotation) );
				i++ ;
			}
		}
		methodInfo.setParameters(parameters);
		
		return methodInfo ;
	}
	
	public ClassInfo getClassInfo(Class<?> clazz) {
		
		ClassInfo classInfo = new ClassInfo();
		
		//--- Class name 
		classInfo.setJavaClassName( clazz.getSimpleName() );
		
		//--- Documentation  
		VelocityObject docAnnotation = clazz.getAnnotation(VelocityObject.class);
		if ( docAnnotation != null ) {
			
			//--- Class 
			classInfo.setContextName( docAnnotation.contextName() );
			classInfo.setDocText( docAnnotation.text() );
			classInfo.setSince( docAnnotation.since() );
			classInfo.setDeprecated( docAnnotation.deprecated() );			

			//--- Methods
			Method[] methods = clazz.getMethods() ;
			for ( Method method : methods ) {
				int modifiers = method.getModifiers();
				if ( Modifier.isPublic(modifiers) ) {
					if ( false == isObjectClassMethod(method) ) {
						MethodInfo methodInfo = getMethodInfo(method);
						if ( methodInfo != null ) {
							classInfo.addMethodInfo(methodInfo);
						}
					}
				}
			}
		}
		else {
			throw new RuntimeException("No documentation annotation for class '" + clazz.getSimpleName() + "'");
		}
		
		return classInfo ;
	}

	private final static Class<?>[] velocityClasses = new Class<?>[] {
		Const.class,
		Fn.class,
		JavaBeanClass.class,
		JavaBeanClassAttribute.class,
		EmbeddedGenerator.class,
		Loader.class,
		ProjectConfiguration.class,
		Target.class,
		Today.class
	};
	
	public Map<String,ClassInfo> getVelocityClassesInfo() {
		
		Map<String,ClassInfo> map = new Hashtable<String, ClassInfo>();
		
		//--- Build class information for each Java class used in the Velocity context
		for ( Class<?> clazz : velocityClasses ) {
			ClassInfo classInfo = getClassInfo(clazz);
			map.put(classInfo.getContextName(), classInfo);
		}
		
		return map ;
	}
}
