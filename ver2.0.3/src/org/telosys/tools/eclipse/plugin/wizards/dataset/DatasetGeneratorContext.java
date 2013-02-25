package org.telosys.tools.eclipse.plugin.wizards.dataset;

public class DatasetGeneratorContext {

	private final static String NONE = "" ;
	
	private String sSelect = NONE;
	
	private String sFrom = NONE;
	
	private String sWhere = NONE;
	
	private String SOption = NONE;
	
	private String sDatasetType = NONE;
	
	private String sParamTypes = NONE;
	
	private String sParamLoad = NONE;
	
	private String sParamsLoad = NONE;
	
	private boolean bStatic = false;
	
	private boolean bStaticCrit = false;
	
	public DatasetGeneratorContext() {
		
	}

	public boolean isStatic() {
		return bStatic;
	}

	public void setStatic(boolean static1) {
		bStatic = static1;
	}

	public boolean isStaticCrit() {
		return bStaticCrit;
	}

	public void setStaticCrit(boolean staticCrit) {
		bStaticCrit = staticCrit;
	}

	public String getParamTypes() {
		return sParamTypes;
	}

	public void setParamTypes(String paramTypes) {
		this.sParamTypes = paramTypes;
	}

	public String getDatasetType() {
		return sDatasetType;
	}

	public void setDatasetType(String datasetType) {
		sDatasetType = datasetType;
	}

	public String getFrom() {
		return sFrom;
	}

	public void setFrom(String from) {
		sFrom = from;
	}

	public String getOption() {
		return SOption;
	}

	public void setOption(String option) {
		SOption = option;
	}

	public String getSelect() {
		return sSelect;
	}

	public void setSelect(String select) {
		sSelect = select;
	}

	public String getWhere() {
		return sWhere;
	}

	public void setWhere(String where) {
		sWhere = where;
	}

	/**
	 * @return the sParamLoad
	 */
	public String getParamLoad() {
		return sParamLoad;
	}

	/**
	 * @param paramLoad the sParamLoad to set
	 */
	public void setParamLoad(String paramLoad) {
		sParamLoad = paramLoad;
	}

	/**
	 * @return the sParamsLoad
	 */
	public String getParamsLoad() {
		return sParamsLoad;
	}

	/**
	 * @param paramsLoad the sParamsLoad to set
	 */
	public void setParamsLoad(String paramsLoad) {
		sParamsLoad = paramsLoad;
	}	
}
