package org.telosys.tools.eclipse.plugin.wizards.dataset;

import org.eclipse.core.resources.IFile;
import org.telosys.tools.eclipse.plugin.commons.MsgBox;
import org.telosys.tools.eclipse.plugin.commons.PluginLogger;
import org.telosys.tools.eclipse.plugin.wizards.common.StandardNewJavaClassWizard;
import org.telosys.tools.eclipse.plugin.wizards.common.WizardTools;
import org.telosys.tools.generator.Generator;
import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.JavaClass;

/**
 * Wizard class to create a new dataset
 *
 */
public class NewDataSetWizard extends StandardNewJavaClassWizard {

	private final static String TITLE = "New SQL Dataset" ;
	
	private NewDataSetWizardPage _wizardPage = null ;

	public NewDataSetWizard() {
		super(TITLE);
	}
	
	//----------------------------------------------------------------------------------------
	/* Method called by Eclipse to add the 1..N pages of the wizard 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		PluginLogger.log(getClass().getName() + " : addPages()...");
		_wizardPage = new NewDataSetWizardPage(selection);
		addPage(this._wizardPage);
	}


	//S'execute lorsque l'utilisateur termine le wizard
	public boolean performFinish() {
		PluginLogger.log(getClass().getName() + " : performFinish()...");
		
		//Génération de la classe de Dataset
		try {
			//--- Java Class to generate
			String sClassName = _wizardPage.getTypeName();
			String sPackage   = _wizardPage.getPackageText();

			//Initialisation des générateurs
			Generator generator = getJavaClassGenerator( _wizardPage.getSelectedTemplate(), sPackage, sClassName );
			
			//--- Source directory
			String sSourceDir = _wizardPage.getPackageFragmentRootText();
			
			//--- Populate the context
			PluginLogger.log("Populate the context... " );
			
			String requete = requestTreatment(_wizardPage.getRequest());
			
			DatasetGeneratorContext generatorContext = new DatasetGeneratorContext();
			
			//Découpage de la requête
			if(!requete.equals("")) {
				String select = "";
				String from = "";
				String where = null;
				String option = "";
				
				if (requete.indexOf("select") != -1) {
					if(requete.indexOf("from") != -1) {
						select = requete.split("from")[0];
						if(requete.indexOf("where") != -1) {
							from = "from"+requete.split("from")[1].split("where")[0];
							if(requete.indexOf("order by") != -1){
								where = "where"+requete.split("where")[1].split("order by")[0];
								option = "order by"+requete.split("order by")[1];	
							} else {
								if(requete.indexOf("group by") != -1) {
									where = "where"+requete.split("where")[1].split("group by")[0];
									option = "group by"+requete.split("order by")[1];	
								} else {
									where = "where"+requete.split("where")[1];
								}
							}
						} else {
							from = "from"+requete.split("from")[1];
						}
					}
				}
				
				//Renseignement du context spécifique au Dataset pour le générateur Velocity
				generatorContext.setSelect(select);
				generatorContext.setFrom(from);
				generatorContext.setWhere(where);
				generatorContext.setOption(option);
				
				String paramTypes = "";
				DatasetStaticCritTableItem[] arrayCrit = _wizardPage.getArrayCrit();
				if(arrayCrit.length > 0) {
					
					paramTypes += "{";
					DatasetStaticCritTableItem item = (DatasetStaticCritTableItem)
						arrayCrit[0];
					paramTypes += getType(item.iType);
					if(arrayCrit.length > 1) paramTypes += ", ";
					for(int i=1; i <arrayCrit.length; i++) {
						item = (DatasetStaticCritTableItem)arrayCrit[i];
						paramTypes += getType(item.iType);
						if(arrayCrit.length > i+1) paramTypes += ", ";
					}
					paramTypes += "}";
					generatorContext.setStaticCrit(true);
				}
				generatorContext.setParamTypes(paramTypes);
				
				String paramLoad = "";
				String paramsLoad = "";
				if(arrayCrit.length > 0) {
					
					DatasetStaticCritTableItem item = (DatasetStaticCritTableItem)
						arrayCrit[0];
					paramLoad += getJavaType(item.iType)+" p1";
					paramsLoad += "\"\"+p1";
					if(arrayCrit.length > 1) {
						paramLoad += ", ";
						paramsLoad += ", ";
					}
					
					for(int i=1; i <arrayCrit.length; i++) {
						item = (DatasetStaticCritTableItem)arrayCrit[i];
						paramLoad += getJavaType(item.iType)+" p"+(i+1);
						paramsLoad += "\"\"+p"+(i+1);
						if(arrayCrit.length > i+1) {
							paramLoad += ", ";
							paramsLoad += ", ";
						}
					}
				}
				
				generatorContext.setParamLoad(paramLoad);
				generatorContext.setParamsLoad(paramsLoad);
				
				generator.setContextAttribute("context", generatorContext);
				
				
			}
			
			final IFile file = WizardTools.generateJavaClass(sSourceDir,
					sPackage, sClassName, generator);
			
			if (file != null) {	
				// Open the generated file
				WizardTools.openFileEditor(file, getShell());
			}
			
			
			//Test class generation
			if (!_wizardPage.getSelectedTemplateTest().equals("none")) {
				String sClassNameTest = sClassName+"Test";
				Generator generatorTest = getJavaClassGenerator( 
						_wizardPage.getSelectedTemplateTest(), sPackage, sClassNameTest );
				//generatorTest.setContextAttribute("context", generatorContext);
				
//				OldJavaClass datasetClass = new OldJavaClass(sClassName);
				JavaClass datasetClass = new JavaClass(sClassName, sPackage);
				generatorTest.setContextAttribute("datasetClass", datasetClass); // DataSet class to TEST 
	
				final IFile fileTest = WizardTools.generateJavaClass(sSourceDir,
						sPackage, sClassNameTest, generatorTest);
				if (file != null) {
					
					// Open the generated file
					WizardTools.openFileEditor(fileTest, getShell());
				}
			}
			
		} catch (GeneratorException e) {
			MsgBox.error("GeneratorException : " + e.toString());
			e.printStackTrace();
		} catch (RuntimeException e) {
			MsgBox.error("RuntimeException : " + e.toString());
			e.printStackTrace();
		} catch (Throwable e) {
			MsgBox.error("Throwable : " + e.toString());
			e.printStackTrace();
		}
		return true;
	}

	private String requestTreatment(String sString) {
		
		//Si l'utilisateur place un point virgule à la fin de sa requête on le supprime
		if(sString.trim().lastIndexOf(";") == sString.trim().length()-1) {
			sString = sString.substring(0, sString.lastIndexOf(";"));
		}
		
		//On supprime les retours chariots
		String sResult ="";
		sString = sString.replaceAll("[\n\r]+"," ");
		
		//On passe la requete en bas de casse sauf les élément entre ''
		String[] splitedString = sString.split("'");
		for(int i = 0; i < splitedString.length; i++) {
			if(i%2 == 0) {
				sResult += splitedString[i].toLowerCase();
			} else {
				sResult += "'"+splitedString[i]+"'";
			}
		}
		return sResult;
	}
	
	private String getType(int iType) {
		int i = iType + 1;
		switch (i) {
		case 1 : return "ParamType.STRING";
		case 2 : return "ParamType.INTEGER";
		case 3 : return "ParamType.DATE";
		case 4 : return "ParamType.TIMESTAMP"; 
		case 5 : return "ParamType.BOOLEAN";
		case 6 : return "ParamType.SHORT";
		case 7 : return "ParamType.LONG";
		case 8 : return "ParamType.DOUBLE";
		case 9 : return "ParamType.FLOAT";
		case 10 :return "ParamType.BYTE";
		default:
			break;
		}
		return "";
	}
	
	private String getJavaType(int iType) {
		int i = iType + 1;
		switch (i) {
		case 1 : return "String";
		case 2 : return "int";
		case 3 : return "Date";
		case 4 : return "Date"; 
		case 5 : return "boolean";
		case 6 : return "short";
		case 7 : return "long";
		case 8 : return "double";
		case 9 : return "float";
		case 10 :return "byte";
		default:
			break;
		}
		return "";
	}
	
}
