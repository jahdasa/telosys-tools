package org.telosys.tools.eclipse.plugin.editors.velocity.model;

import java.util.ArrayList;
import java.util.List;

import org.telosys.tools.eclipse.plugin.commons.PluginImages;
import org.telosys.tools.eclipse.plugin.editors.velocity.contentassist.ContentAssistMessages;

public class VelocityKeyWords {

	private static List<VelocityKeyWord> keyWords;
	private static String directives[] = null;
	
	/**
	 * Return all Velocity Directives.
	 * @return
	 */
	public static String[] getAllDirectives() {
		if (directives == null) {
			List<String> list = new ArrayList<String>();
			for (VelocityKeyWord keyWord : getKeyWords()) {
				list.add(keyWord.getValue());
			}
			directives = new String[list.size()];
			directives = list.toArray(directives);
		} 
		return directives;
	}
	
	public static List<VelocityKeyWord> getKeyWords() {
		if (keyWords == null) {
			initKeyWords();
		} 
		return keyWords;
	}
	
	private static String docHTML(String docId) {
		return ContentAssistMessages.getString(docId);
	}
	
	private static void initKeyWords() {
		
		// TODO Better on XML file for example
		// Keep alphabetical order
		keyWords = new ArrayList<VelocityKeyWord>();

		// Telosys specific directives (added in ver 2.0.7 )
		keyWords.add(new VelocityKeyWord("#assertFalse", "#assertFalse()", docHTML(ContentAssistMessages.DIRECTIVE_ASSERTFALSE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#assertTrue",  "#assertTrue()", docHTML(ContentAssistMessages.DIRECTIVE_ASSERTTRUE_DOC),PluginImages.VELOCITY_DIRECTIVE));

		keyWords.add(new VelocityKeyWord("#break",  "#break",    docHTML(ContentAssistMessages.DIRECTIVE_BREAK_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#define", "#define()", docHTML(ContentAssistMessages.DIRECTIVE_DEFINE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#else",   "#else",     null,PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#elseif", "#elseif()", null,PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#end",    "#end",      null,PluginImages.VELOCITY_DIRECTIVE));
		
		// Telosys specific directives (added in ver 2.0.7 )
		keyWords.add(new VelocityKeyWord("#error",  "#error()", docHTML(ContentAssistMessages.DIRECTIVE_ERROR_DOC),PluginImages.VELOCITY_DIRECTIVE));

		keyWords.add(new VelocityKeyWord("#evaluate", "#evaluate()", docHTML(ContentAssistMessages.DIRECTIVE_EVALUATE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#foreach",  "#foreach()", docHTML(ContentAssistMessages.DIRECTIVE_FOREACH_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#if",       "#if()", docHTML(ContentAssistMessages.DIRECTIVE_IF_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#include",  "#include()", docHTML(ContentAssistMessages.DIRECTIVE_INCLUDE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#macro",    "#macro()", docHTML(ContentAssistMessages.DIRECTIVE_MACRO_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#parse",    "#parse()", docHTML(ContentAssistMessages.DIRECTIVE_PARSE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#set",      "#set()", docHTML(ContentAssistMessages.DIRECTIVE_SET_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#stop",     "#stop", docHTML(ContentAssistMessages.DIRECTIVE_STOP_DOC),PluginImages.VELOCITY_DIRECTIVE));
		
		// Telosys specific directives (added in ver 2.0.7 )
		keyWords.add(new VelocityKeyWord("#using",  "#using()", docHTML(ContentAssistMessages.DIRECTIVE_USING_DOC),PluginImages.VELOCITY_DIRECTIVE));
		
	}

}
