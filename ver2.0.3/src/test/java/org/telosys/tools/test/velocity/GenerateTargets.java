package org.telosys.tools.test.velocity;

import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.util.GeneratorRunner;

public class GenerateTargets {

	private final static String OUTPUT_FOLDER = "GENERATED_FILES" ; // output folder in the project location
	
	public static void main(String[] args) {

		TelosysToolsLogger logger = LoggerProvider.getLogger();
		
		GeneratorRunner generatorRunner = null ;
		try {
			generatorRunner = new GeneratorRunner(Const.REPOSITORY_FILE, Const.PROJECT_LOCATION, logger);
		} catch (GeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//--- The project folder must be set in the project configuration
		generatorRunner.generateEntity("AUTHOR",   "Author.java",   OUTPUT_FOLDER, "bean_jpa.vm" );
		generatorRunner.generateEntity("BOOK",     "Book.java",     OUTPUT_FOLDER, "bean_jpa.vm" );
		generatorRunner.generateEntity("EMPLOYEE", "Employee.java", OUTPUT_FOLDER, "bean_jpa.vm" );
		generatorRunner.generateEntity("REVIEW",   "Review.java",   OUTPUT_FOLDER, "bean_jpa.vm" );
	}
	
	
}
