/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.generator.context;

import java.util.List;

import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.GeneratorVersion;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.RepositoryModel;

/**
 * Embedded generator stored in the Velocity Context and usable in the template.
 * 
 * @author Laurent GUERIN
 *
 */
public class EmbeddedGenerator {

	private final RepositoryModel    repositoryModel ;
	private final IGeneratorConfig   generatorConfig ;
	private final TelosysToolsLogger logger ;
	private final boolean            canGenerate ;
	private final List<Target>       generatedTargets ;
	
	/**
	 * Constructor for limited generator without generation capabilities
	 */
	public EmbeddedGenerator() {
		super();
		this.repositoryModel = null ;
		this.generatorConfig = null ;
		this.logger = null ;
		this.canGenerate = false ;
		this.generatedTargets = null ;
	}

	/**
	 * Constructor for real generator that can generate sub-targets from a template
	 * @param repositoryModel
	 * @param generatorConfig
	 * @param logger
	 */
	public EmbeddedGenerator(RepositoryModel repositoryModel,
			IGeneratorConfig generatorConfig, TelosysToolsLogger logger, List<Target> generatedTargets) {
		super();
		this.repositoryModel = repositoryModel;
		this.generatorConfig = generatorConfig;
		this.logger = logger;
		if ( repositoryModel != null && generatorConfig != null && logger != null ) {
			this.canGenerate = true ;
		}
		else {
			this.canGenerate = false ;
		}
		this.generatedTargets = generatedTargets ;
	}

	public String getName()
	{
		return "Telosys Tools Generator";
	}
	
	public String getVersion()
    {
        return GeneratorVersion.GENERATOR_VERSION ;
    }
	
	public void generate(String entityName, String outputFile, String outputFolder, String templateFile) throws GeneratorException
	{
		String err = "Cannot generate with embedded generator ";
		
		if ( canGenerate != true ) {
			throw new GeneratorException( err + "(environment not available)");
		}
		
		if ( null == entityName ) {
			throw new GeneratorException( err + "(entity name is null)");
		}
		if ( null == outputFile ) {
			throw new GeneratorException( err + "(output file is null)");
		}
		if ( null == outputFolder ) {
			throw new GeneratorException( err + "(output folder is null)");
		}
		if ( null == templateFile ) {
			throw new GeneratorException( err + "(template file is null)");
		}
		
		ProjectConfiguration projectConfiguration = generatorConfig.getProjectConfiguration();
		
		Entity entity = repositoryModel.getEntityByName(entityName.trim());
		if ( null == entity ) {
			throw new GeneratorException( err + "(entity '" + entityName + "' not found in repository)");
		}
		
		TargetDefinition genericTarget = new TargetDefinition("Dynamic target", outputFile, outputFolder, templateFile, "");
		
		Target target = new Target( genericTarget, entity.getName(), entity.getBeanJavaClass(), projectConfiguration.getVariables() );
		
		Generator generator = new Generator(target, generatorConfig, logger);
		generator.generateTarget(target, repositoryModel, this.generatedTargets);
		
	}
	
}
