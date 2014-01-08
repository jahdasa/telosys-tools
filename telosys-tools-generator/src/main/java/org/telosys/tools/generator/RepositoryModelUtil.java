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
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.RepositoryModel;

public class RepositoryModelUtil {

	/**
	 * Builds a context Entity instance from the repository (model definition)
	 * @param entityName
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public static JavaBeanClass buildJavaBeanClass( 
			// Target target, // 2013-02-04
			String entityName, // 2013-02-04
			RepositoryModel repositoryModel, 
			GeneratorConfig generatorConfig ) throws GeneratorException
	{
		//String entityName = target.getEntityName() ;
		
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
		
		//--- Java Bean Class name defined in the repository
    	String beanClassName = entity.getBeanJavaClass();
    	
		//--- Java Bean Package name determined from the target folder
    	//String beanPackage = projectConfiguration.getPackageForBean();
    	String beanPackage = generatorConfig.getTelosysToolsCfg().getEntityPackage(); // v 2.1.0
    		
    	//--- New instance of JavaBeanClass
    	JavaBeanClass beanClass = new JavaBeanClass(entity, repositoryModel, beanClassName, beanPackage);    	
    	
    	return beanClass ;
		
	}
	
	/**
	 * Builds a list of context Entities for each given entity name 
	 * @param entitiesNames
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public static List<JavaBeanClass> buildJavaBeanClasses( 
			List<String> entitiesNames, 
			RepositoryModel repositoryModel, 
			//ProjectConfiguration projectConfiguration 
			GeneratorConfig generatorConfig // v 2.1.0
			) throws GeneratorException
	{
		List<JavaBeanClass> javaBeanClasses = new LinkedList<JavaBeanClass>();
		for ( String entityName : entitiesNames ) {
			JavaBeanClass entityBeanClass = buildJavaBeanClass( 
												entityName, 
												repositoryModel, 
												generatorConfig );
			javaBeanClasses.add(entityBeanClass);
		}
		return javaBeanClasses ;
	}	

	/**
	 * Build a list of context Entities for all entities defined in the model 
	 * @param repositoryModel
	 * @param generatorConfig
	 * @return
	 * @throws GeneratorException
	 */
	public static List<JavaBeanClass> buildAllJavaBeanClasses( RepositoryModel repositoryModel, 
			//ProjectConfiguration projectConfiguration 
			GeneratorConfig generatorConfig // v 2.1.0
			) throws GeneratorException 
	{
		List<JavaBeanClass> javaBeanClasses = new LinkedList<JavaBeanClass>();
		//--- Get the names of all the entities defined in the model 
		String[] names = repositoryModel.getEntitiesNames();
		for ( String entityName : names ) {
			//--- Build an "entity BeanClass" for each
			//JavaBeanClass entityBeanClass = buildJavaBeanClass(entityName, repositoryModel, projectConfiguration );
			JavaBeanClass entityBeanClass = buildJavaBeanClass(entityName, repositoryModel, generatorConfig );
			javaBeanClasses.add(entityBeanClass);
		}
		return javaBeanClasses ;
	}
}
