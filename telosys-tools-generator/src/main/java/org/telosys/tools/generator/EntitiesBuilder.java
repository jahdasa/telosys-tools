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
package org.telosys.tools.generator;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generator.config.GeneratorConfig;
import org.telosys.tools.generator.context.EntityInContext;
import org.telosys.tools.generator.context.EnvInContext;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.RepositoryModel;

public class EntitiesBuilder {

	private final EnvInContext _env ;
	
	public EntitiesBuilder() {
		_env = new EnvInContext() ; // Default environment
	}

	public EntitiesBuilder(EnvInContext env) {
		_env = env ; // Specific environment instance
	}
	
	
	/**
	 * Builds a context Entity instance from the repository (model definition)
	 * @param entityName the name of the entity to be built
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public EntityInContext buildEntity( 
			String entityName, 
			RepositoryModel repositoryModel, 
			GeneratorConfig generatorConfig ) throws GeneratorException
	{
		//--- Retrieve the entity from the repository model
		Entity entity = repositoryModel.getEntityByName(entityName);
		if ( null == entity ) 
		{
			throw new GeneratorException("Entity '" + entityName + "' not found in the repository");
		}
		if ( entityName.equals(entity.getName()) != true )
		{
			throw new GeneratorException("Repository corrupted : Entity name '" + entityName + "' != '" + entity.getName() +"'");
		}
		
//		//--- Java Bean Class name defined in the repository
//    	String beanClassName = entity.getBeanJavaClass();
    	
		//--- Java Bean Package name determined from the target folder
    	String beanPackage = generatorConfig.getTelosysToolsCfg().getEntityPackage(); 
    		
    	//--- New instance of JavaBeanClass
    	EntityInContext entityInContext = new EntityInContext(entity, repositoryModel, beanPackage, _env);    	
    	
    	return entityInContext ;
		
	}
	
	/**
	 * Builds a list of context Entities for each selected entity (defined by its name)
	 * @param entitiesNames list of selected entities names (or null if none)
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public List<EntityInContext> buildSelectedEntities( 
			List<String> entitiesNames, 
			RepositoryModel repositoryModel, 
			GeneratorConfig generatorConfig
			) throws GeneratorException
	{
		List<EntityInContext> selectedEntities = new LinkedList<EntityInContext>();
		if ( entitiesNames != null ) {
			for ( String entityName : entitiesNames ) {
				EntityInContext entityBeanClass = buildEntity( entityName, repositoryModel, generatorConfig );
				selectedEntities.add(entityBeanClass);
			}
		}
		return selectedEntities ;
	}	
	
	/**
	 * Builds a list of context Entities for all the entities defined in the model 
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public List<EntityInContext> buildAllEntities( RepositoryModel repositoryModel, GeneratorConfig generatorConfig) throws GeneratorException 
	{
		List<EntityInContext> javaBeanClasses = new LinkedList<EntityInContext>();
		//--- Get the names of all the entities defined in the model 
		String[] names = repositoryModel.getEntitiesNames();
		for ( String entityName : names ) {
			//--- Build an "entity BeanClass" for each
			EntityInContext entityBeanClass = buildEntity(entityName, repositoryModel, generatorConfig );
			javaBeanClasses.add(entityBeanClass);
		}
		return javaBeanClasses ;
	}
}
