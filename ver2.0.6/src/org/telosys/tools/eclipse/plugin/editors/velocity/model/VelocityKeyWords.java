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
	
	private static void initKeyWords() {
		
		// TODO Better on XML file for example
		keyWords = new ArrayList<VelocityKeyWord>();
		keyWords.add(new VelocityKeyWord("#break", "#break", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_BREAK_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#define", "#define()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_DEFINE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#else", "#else", null,PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#elseif", "#elseif()", null,PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#end", "#end", null,PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#evaluate", "#evaluate()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_EVALUATE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#foreach", "#foreach()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_FOREACH_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#if", "#if()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_IF_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#include", "#include()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_INCLUDE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#macro", "#macro()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_MACRO_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#parse", "#parse()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_PARSE_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#set", "#set()", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_SET_DOC),PluginImages.VELOCITY_DIRECTIVE));
		keyWords.add(new VelocityKeyWord("#stop", "#stop", ContentAssistMessages.getString(ContentAssistMessages.DIRECTIVE_STOP_DOC),PluginImages.VELOCITY_DIRECTIVE));
	}

}
