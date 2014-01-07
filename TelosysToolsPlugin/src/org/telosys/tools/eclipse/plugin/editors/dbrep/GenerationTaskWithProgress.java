package org.telosys.tools.eclipse.plugin.editors.dbrep;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.variables.Variable;
import org.telosys.tools.eclipse.plugin.commons.BundleResourcesManager;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.TelosysPluginException;
import org.telosys.tools.eclipse.plugin.config.ProjectConfig;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.RepositoryModelUtil;
import org.telosys.tools.generator.config.IGeneratorConfig;
import org.telosys.tools.generator.context.JavaBeanClass;
import org.telosys.tools.generator.context.Target;
import org.telosys.tools.generator.target.TargetDefinition;
import org.telosys.tools.repository.model.Entity;
import org.telosys.tools.repository.model.RepositoryModel;


/**
 * Eclipse runnable task with a progress bar 
 * for code generation 
 *  
 * @author Laurent Guerin
 *
 */
public class GenerationTaskWithProgress implements IRunnableWithProgress 
{
	private final static String ENTITY_NONE = "(no entity)" ;
	private final static String NO_TEMPLATE = "(no template)" ;
	
	private final LinkedList<String>            _selectedEntities ;
	private final LinkedList<TargetDefinition>  _selectedTargets ;
	private final List<TargetDefinition>        _resourcesTargets ;
	private final RepositoryModel      _repositoryModel ;
	private final IGeneratorConfig     _generatorConfig ;
	private final IProject             _project ;
	private final String               _bundleName ;
	private final ProjectConfig        _projectConfig ;
	private final TelosysToolsLogger   _logger ;

	//private String _currentEntityName = ENTITY_NONE ;
	private Target _currentTarget = null ;
	
	//private int    _result = 0 ;
	private GenerationTaskResult _result = null ;
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * @param selectedEntities
	 * @param selectedTargets
	 * @param resourcesTargets
	 * @param repositoryModel
	 * @param generatorConfig
	 * @param project
	 * @param logger
	 * @throws TelosysPluginException
	 */
	public GenerationTaskWithProgress(
			RepositoryEditor             editor,
			LinkedList<String>           selectedEntities, 
			LinkedList<TargetDefinition> selectedTargets,
			List<TargetDefinition>       resourcesTargets,
//			RepositoryModel              repositoryModel, 
			IGeneratorConfig             generatorConfig, 
//			IProject                     project,
			TelosysToolsLogger           logger
			) throws TelosysPluginException
	{
		super();
		
		
		_selectedEntities = selectedEntities ;
		_selectedTargets  = selectedTargets ;
		_resourcesTargets = resourcesTargets ; // can be null
		
//		_repositoryModel  = repositoryModel ;
		_generatorConfig  = generatorConfig ;
//		_project  = project ;

		_bundleName       = editor.getCurrentBundleName() ;
		_projectConfig    = editor.getProjectConfig();
		_project          = editor.getProject();
		_repositoryModel  = editor.getDatabaseRepository();

		_logger   = logger ;
		
		if ( _selectedEntities == null ) throw new TelosysPluginException("_selectedEntities is null ");
		if ( _selectedTargets  == null ) throw new TelosysPluginException("_selectedTargets is null ");
		if ( _repositoryModel  == null ) throw new TelosysPluginException("_repositoryModel is null ");
		if ( _generatorConfig  == null ) throw new TelosysPluginException("_generatorConfig is null ");
		if ( _project == null )  throw new TelosysPluginException("_project is null ");
		if ( _logger  == null )  throw new TelosysPluginException("_logger is null ");
		
		_logger.log(this, "Task created");
		
	}
	
	//--------------------------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException 
	{
		_logger.log(this, "run");

	/***
		//--- Build the list of "ONCE" targets ( NEW in version 2.0.3 / Feb 2013 )
		List<TargetDefinition> onceTargets   = new LinkedList<TargetDefinition>() ; 
		List<TargetDefinition> entityTargets = new LinkedList<TargetDefinition>() ; 
		for ( TargetDefinition targetDefinition : _selectedTargets ) {
			if ( targetDefinition.isOnce() ) {
				onceTargets.add(targetDefinition);
			}
			else {
				entityTargets.add(targetDefinition);
			}
		}
	***/
		
		//_result = 0 ;
		_result = null ;
	/***
		//--- Number of generations expected
		int totalWorkTasks = ( _selectedEntities.size() * entityTargets.size() ) + onceTargets.size() ;
		//--- Build the selected entities list 
		List<JavaBeanClass> selectedEntities;
		try {
			selectedEntities = RepositoryModelUtil.buildJavaBeanClasses(_selectedEntities, 
														_repositoryModel, _generatorConfig.getProjectConfiguration() );
		} catch (GeneratorException e1) {
			MsgBox.error("Cannot build selected entities ", e1);
			return ;
		}
	***/
		
		//Variable[] projectVariables = _generatorConfig.getProjectConfiguration().getVariables();
		// From _projectConfig.getProjectVariables : invalid variables $WEB, $SRC ... not in the list !!!
		// Variable[] projectVariables = _projectConfig.getProjectVariables();
		Variable[] projectVariables = _generatorConfig.getProjectConfiguration().getAllVariables();
		
		//--- 1) Copy the given resources (or do nothing if null)
		int numberOfResourcesCopied = copyResourcesIfAny(_resourcesTargets);

		//--- 2) Launch the generation
		int numberOfFilesGenerated = generateSelectedTargets(progressMonitor, projectVariables);
	/***
		// count = total number of work units into which the main task is been subdivided
		progressMonitor.beginTask("Bulk generation in progress", totalWorkTasks ); 
		

		int numberOfFilesGenerated = 0 ; 
		//--- For each entity
		for ( String entityName : _selectedEntities ) {
			
			_logger.log(this, "run : entity " + entityName );
			Entity entity = _repositoryModel.getEntityByName(entityName);
			if ( entity != null )
			{
				//--- For each "entity target" 
				for ( TargetDefinition targetDefinition : entityTargets ) {
					
					//--- Get a specialized target for the current entity
					Target target = new Target( targetDefinition, entity.getName(), 
							entity.getBeanJavaClass(), projectVariables );
					
					numberOfFilesGenerated = numberOfFilesGenerated + generateTarget(progressMonitor, target, selectedEntities); 
					
				}
				//--- One TARGET done
			}
			else
			{
				_logger.error("Entity '" + entityName + "' not found in the repository") ;
			}
			
			//--- One ENTITY done
		} // end of "For each entity"
		
		//--- Finally, generate the "ONCE" targets ( NEW in version 2.0.3 / Feb 2013 )
		for ( TargetDefinition targetDefinition : onceTargets ) {
			Target target = new Target( targetDefinition, "", "", projectVariables );
			numberOfFilesGenerated = numberOfFilesGenerated + generateTarget(progressMonitor, target, selectedEntities); 
		}
		
		//--- Task result
		_result = new GenerationTaskResult(numberOfResourcesCopied, numberOfFilesGenerated);
		
		//--- Notifies that the work is done; that is, either the main task is completed or the user canceled it.
		progressMonitor.done();
		
		if (progressMonitor.isCanceled()) // Returns whether cancellation of current operation has been requested
		{
			throw new InterruptedException("The bulk generation was cancelled");
		}
	***/
		//--- Task result
		_result = new GenerationTaskResult(numberOfResourcesCopied, numberOfFilesGenerated);
		
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * Copy the static resources if any 
	 * @param resourcesTargetsDefinitions
	 * @return
	 * @throws InvocationTargetException
	 */
	private int copyResourcesIfAny( List<TargetDefinition> resourcesTargetsDefinitions ) throws InvocationTargetException {
		int count = 0 ;
		if ( resourcesTargetsDefinitions != null ) {
			_logger.log(this, "run : copy resources " );
			
			BundleResourcesManager resourcesManager = new BundleResourcesManager(_project, _bundleName, _projectConfig, _logger);
			try {
				count = resourcesManager.copyResourcesInProject(resourcesTargetsDefinitions);
			} catch (Exception e) {
				throw new InvocationTargetException(e);
			}
		}
		else {
			_logger.log(this, "run : no resources to be copied" );
		}
		return count ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Generates all the "selected targets" ( once or for each entity depending on the target's type ) 
	 * @param progressMonitor
	 * @param variables
	 * @return
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private int generateSelectedTargets(IProgressMonitor progressMonitor, Variable[] variables ) 
				throws InvocationTargetException, InterruptedException 
	{
		//--- Separate targets in 2 list : "ONCE" and "ENTITY"
		List<TargetDefinition> onceTargets   = new LinkedList<TargetDefinition>() ; 
		List<TargetDefinition> entityTargets = new LinkedList<TargetDefinition>() ; 
		for ( TargetDefinition targetDefinition : _selectedTargets ) {
			if ( targetDefinition.isOnce() ) {
				onceTargets.add(targetDefinition); 
			}
			else {
				entityTargets.add(targetDefinition);
			}
		}
		
		//--- Number of generations expected
		int totalWorkTasks = ( _selectedEntities.size() * entityTargets.size() ) + onceTargets.size() ;

//		if ( totalWorkTasks > 0 ) {
//			//--- Dialog box for confirmation
//			if ( confirmBulkGeneration(totalWorkTasks) == false ) {
//				return 0;
//			}
//		}
		progressMonitor.beginTask("Generation in progress", totalWorkTasks ); 
		
		//--- Build the selected entities list (to be stored in the Velocity context)
		List<JavaBeanClass> selectedEntities;
		try {
			selectedEntities = RepositoryModelUtil.buildJavaBeanClasses(_selectedEntities, 
														_repositoryModel, _generatorConfig.getProjectConfiguration() );
		} catch (GeneratorException e1) {
			MsgBox.error("Cannot build selected entities ", e1);
			return 0 ;
		}
		
		
		int numberOfFilesGenerated = 0 ; 
		//--- For each entity
		for ( String entityName : _selectedEntities ) {
			
			_logger.log(this, "run : entity " + entityName );
			Entity entity = _repositoryModel.getEntityByName(entityName);
			if ( entity != null )
			{
				//--- For each "entity target" 
				for ( TargetDefinition targetDefinition : entityTargets ) {
					
					//--- Get a specialized target for the current entity
					Target target = new Target( targetDefinition, entity.getName(), 
							entity.getBeanJavaClass(), variables );
					
					numberOfFilesGenerated = numberOfFilesGenerated + generateTarget(progressMonitor, target, selectedEntities); 
					
				}
				//--- One TARGET done
			}
			else
			{
				_logger.error("Entity '" + entityName + "' not found in the repository") ;
			}
			
			//--- One ENTITY done
		} // end of "For each entity"
		
		//--- Finally, generate the "ONCE" targets ( NEW in version 2.0.3 / Feb 2013 )
		for ( TargetDefinition targetDefinition : onceTargets ) {
			Target target = new Target( targetDefinition, "", "", variables );
			numberOfFilesGenerated = numberOfFilesGenerated + generateTarget(progressMonitor, target, selectedEntities); 
		}
		
		//--- Notifies that the work is done; that is, either the main task is completed or the user canceled it.
		progressMonitor.done();
		
		if (progressMonitor.isCanceled()) // Returns whether cancellation of current operation has been requested
		{
			throw new InterruptedException("The bulk generation was cancelled");
		}
		
		return numberOfFilesGenerated ;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * Generates the given target. <br>
	 * More than one file can be generated if the embedded generator is used in the template.
	 * @param progressMonitor
	 * @param target
	 * @param selectedEntities
	 * @return
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private int generateTarget(IProgressMonitor progressMonitor, Target target, List<JavaBeanClass> selectedEntities) 
					throws InvocationTargetException, InterruptedException 
	{

		int count = 0 ;
		_logger.log(this, "Generate TARGET : entity name '" + target.getEntityName() + "' - target file '" + target.getFile() + "' ");
		
		_currentTarget = target ;
		
		progressMonitor.subTask("Entity '" + target.getEntityName() + "' : target file '" + target.getFile() + "' ");
		
		//--- Possible multiple generated targets for one main target (with embedded generator)
		LinkedList<Target> generatedTargets = new LinkedList<Target>();
		try {
			//Generator generator = new Generator(target, _generatorConfig, _logger);
			Generator generator = new Generator(target, _generatorConfig, _repositoryModel, _logger); // v 2.0.7
			generator.setSelectedEntitiesInContext(selectedEntities); // New [2013-02-04]
			generator.generateTarget(target, _repositoryModel, generatedTargets);						
			
		} catch (GeneratorException e) {
			// if the "run" method must propagate a checked exception, 
			// it should wrap it inside an InvocationTargetException; 
			throw new InvocationTargetException(e);
		}

		//--- Refresh the generated files
		for ( Target generatedTarget : generatedTargets ) {
			_logger.log(this, "Refresh generated target : " + generatedTarget.getFile() );

			String outputFileNameInProject = generatedTarget.getOutputFileNameInProject() ;
			IFile iFile = _project.getFile( outputFileNameInProject );
			try {
				iFile.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				MsgBox.error("Cannot refresh file \n " + outputFileNameInProject );
				throw new InterruptedException("Cannot refresh file.");
			}
			
			//--- One more file : increment result count
			//_result++ ;
			count++ ;
		}
		
		//--- One TARGET done
		// Notifies that a given number of work unit of the main task has been completed. 
		// Note that this amount represents an installment, as opposed to a cumulative amount of work done to date.
		progressMonitor.worked(1); // One unit done (not cumulative)
		
		return count ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the name of the entity currently under generation 
	 * @return
	 */
	public String getCurrentEntityName() {
		if ( _currentTarget == null ) return ENTITY_NONE ;
		String entityName = _currentTarget.getEntityName() ;
		if ( entityName == null ) {
			return ENTITY_NONE ;
		}
		else if ( entityName.trim().length() == 0 ) {
			return ENTITY_NONE ;
		}
		else {
			return entityName ;
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the name of the template currently in use for generation 
	 * @return
	 */
	public String getCurrentTemplateName() {
		if ( _currentTarget == null ) return NO_TEMPLATE ;
		return _currentTarget.getTemplate() ;
	}
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Returns the operation result : number of files generated
	 * @return
	 */
	//public int getResult()
	public GenerationTaskResult getResult()
	{
		//return _result ;
		return _result != null ? _result : new GenerationTaskResult() ;
	}

	//--------------------------------------------------------------------------------------------------
    private boolean confirmBulkGeneration(int numberOfFilesToBeGenerated)
    {
		String sMsg = "The generation will overwrite existing files if they exist." 
			+ "\n\n" + "At least " + numberOfFilesToBeGenerated + " file(s) will be generated."
			+ "\n\n" + "Launch generation ?";
		return MsgBox.confirm(" Confirm generation", sMsg) ;
    }
	
}
