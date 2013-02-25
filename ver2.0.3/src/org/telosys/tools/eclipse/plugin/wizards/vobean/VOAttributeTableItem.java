package org.telosys.tools.eclipse.plugin.wizards.vobean;

public class VOAttributeTableItem 
{
	public String sFirstCol = "¤";

	public int iId;

	public String sAttributeName;

	public int iType;

	public String sInitialValue;

	public boolean bGetter;

	public boolean bSetter;

	/**
	 * @param id
	 * @param attributeName
	 * @param type
	 * @param initialValue
	 * @param getter
	 * @param setter
	 */
	public VOAttributeTableItem(int id, String attributeName, int type,
			String initialValue, boolean getter, boolean setter) {
		super();
		iId = id;
		sAttributeName = attributeName;
		iType = type;
		sInitialValue = initialValue;
		bGetter = getter;
		bSetter = setter;
	}

	/**
	 * @return the bGetter
	 */
	public boolean isGetter() {
		return bGetter;
	}

	/**
	 * @param getter
	 *            the bGetter to set
	 */
	public void setGetter(boolean getter) {
		bGetter = getter;
	}

	/**
	 * @return the bSetter
	 */
	public boolean isSetter() {
		return bSetter;
	}

	/**
	 * @param setter
	 *            the bSetter to set
	 */
	public void setSetter(boolean setter) {
		bSetter = setter;
	}

	/**
	 * @return the iId
	 */
	public int getId() {
		return iId;
	}

	/**
	 * @param id
	 *            the iId to set
	 */
	public void setId(int id) {
		iId = id;
	}

	/**
	 * @return the iType
	 */
	public int getType() {
		return iType;
	}

	/**
	 * @param type
	 *            the iType to set
	 */
	public void setType(int type) {
		iType = type;
	}

	/**
	 * @return the sAttributeName
	 */
	public String getAttributeName() {
		return sAttributeName;
	}

	/**
	 * @param attributeName
	 *            the sAttributeName to set
	 */
	public void setAttributeName(String attributeName) {
		sAttributeName = attributeName;
	}

	/**
	 * @return the sInitialValue
	 */
	public String getInitialValue() {
		return sInitialValue;
	}

	/**
	 * @param initialValue
	 *            the sInitialValue to set
	 */
	public void setInitialValue(String initialValue) {
		sInitialValue = initialValue;
	}

}