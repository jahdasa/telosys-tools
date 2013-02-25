package org.telosys.tools.eclipse.plugin.wizards.dataset;

public class DatasetConst {
	
	//static final String TEMPLATE_DATASET = "dataset.vm";
	//static final String TEMPLATE_DATASET_TEST = "dataset_test.vm";
	
	static final String[] ARRAY_TEMPLATES = new String[] {
		"wizard/wizard_dataset_simple.vm",
		"wizard/wizard_dataset_parameter.vm",
		"wizard/wizard_dataset_criteria.vm"
	};
	
	static final String[] ARRAY_TEMPLATES_TEST = new String[] {
		"none",
		"wizard/wizard_test_dataset_simple.vm",
		"wizard/wizard_test_dataset_parameter.vm",
		"wizard/wizard_test_dataset_criteria.vm"
	};
	
	static final String[] ARRAY_TYPES = new String[] {
		"String",
		"Integer",
		"Date",
		"TimeStamp",
		"Boolean",
		"Short",
		"Long",
		"Float",
		"Double",
		"Byte"
	};
	
}
