package org.telosys.tools.generator.context.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class DocGeneratorHTML {

	public void generateDocFile(ClassInfo classInfo, String filePath) {
		File file = new File(filePath);
		if ( file.exists() ) {
			file.delete();
		}
		
		PrintWriter writer ;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		generateDocFile( writer, classInfo);
		
		writer.close();
	}
	
	public void generateDocFile(PrintWriter writer, ClassInfo classInfo) {
		printBeginning(writer, classInfo );
		for ( MethodInfo methodInfo : classInfo.getMethodsInfo() ) {
			printMethodDoc(writer, methodInfo);
		}
		printEnd(writer);
	}
	
	private void printBeginning( PrintWriter writer, ClassInfo classInfo) {
		writer.println(	"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">		" );
		writer.println( "<html>								" );
		writer.println( "<head>								" );
		writer.println( "	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">	" );
		writer.println( "	<title> $" + classInfo.getContextName() + "</title>	");
		writer.println( "	<style type=\"text/css\">		");
		writer.println( "	h1 {							");
		writer.println( "		font-size:28px;				");
		writer.println( "		font-family: verdana		");
		writer.println( "	}								");
		writer.println( "	p.desc {						");
		writer.println( "		font-size:12px;				");
		writer.println( "		font-family: verdana;		");
		writer.println( "	}								");
		writer.println( "	p.doc {							");
		writer.println( "		margin-left:2cm;			");
		writer.println( "	}								");
		writer.println( "	td.doc {						");
		writer.println( "		font-size:12px;				");
		writer.println( "		font-family: verdana;		");
		writer.println( "		vertical-align:text-top;	");
		writer.println( "		padding-top: 6px;			");
		writer.println( "		padding-bottom: 12px;		");
		writer.println( "	}								");
		writer.println( "	tr.title {						");
		writer.println( "		font-family: verdana;		");
		writer.println( "		font-size:20px;				");
		writer.println( "		font-weight:bold;			");
		writer.println( "		background-color: #CCCCFF ;	");
		writer.println( "	}								");
		writer.println( "	code.simpledesc {				");
		writer.println( "		font-size:15px;				");
		writer.println( "		color: #000099; 			");
		writer.println( "	}								");
		writer.println( "	</style>						");
		writer.println( "</head>							");
		

		writer.println( "<body>											");
		writer.println( "<h1> $" + classInfo.getContextName() + "</h1>	");
		writer.println( "<p class=\"desc\">								");
		
		for ( String s : classInfo.getDocText()  ) {
			writer.println( s + "<br>" );
		}
		writer.println( "<br>" );
		if ( classInfo.getSince() != null ) {
			if ( classInfo.getSince().trim().length() > 0 ) {
				writer.println( "Since : " + classInfo.getSince() + "<br>" );
			}
		}
		if ( classInfo.isDeprecated()  ) {
			writer.println( "DEPRECATED (!) <br>" );
		}
		writer.println( "</p>		");

		writer.println( "<table width=\"100%\" border=\"1\" cellspacing=\"0\">		");		
		writer.println( "<TR class=\"title\">										");
		writer.println( "  <TD>Attributes and methods</TD>			");
		writer.println( "</TR>		");

	}
	
	private void printEnd(PrintWriter writer) {
		writer.println( "</table>" );
		writer.println( "</body>" );
		writer.println( "</html>" );
	}

	private void printMethodDoc(PrintWriter writer, MethodInfo methodInfo) {

		writer.println( "<TR>" );
		writer.println( "<TD class=\"doc\" ><CODE class=\"simpledesc\"> <B>." + methodInfo.getSimpleDescription() + "</B> </CODE>" );
		writer.println( "<p class=\"doc\">" );
		if ( methodInfo.isDeprecated() ) {
			writer.println( "<b>Deprecated.</b><br>" );
			writer.println( "<br>" );
		}
		for ( String s : methodInfo.getDocText()  ) {
			writer.println( s + "<br>" );
		}
		if ( methodInfo.hasParameters() ) {
			writer.println( "<br>" );
			writer.println( "<b>Parameters : </b><br>" );
			for ( MethodParameter p : methodInfo.getParameters() ) {
				writer.println("&nbsp;&nbsp;&nbsp;<b>" + p.getName() + "</b> : " + p.getDescription() + "<br>");
			}			
		}
		if ( methodInfo.hasExampleText() ) {
			writer.println( "<br>" );
			writer.println( "<b>Example : </b><br>" );
			writer.println( "<code>" );
			for ( String s : methodInfo.getExampleText() ) {
				writer.println( "&nbsp;&nbsp;&nbsp;" + s + "<br>" );
			}
			writer.println( "</code>" );
		}
		if ( methodInfo.hasSince() ) {
			writer.println( "<br>" );
			writer.println( "<b>Since : </b>" + methodInfo.getSince() + "<br>" );
		}
		writer.println( "</p>" );
		writer.println( "</TD>" );
		writer.println( "</TR>" );
	}
}
