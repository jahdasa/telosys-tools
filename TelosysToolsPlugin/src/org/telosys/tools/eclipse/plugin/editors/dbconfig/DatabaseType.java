package org.telosys.tools.eclipse.plugin.editors.dbconfig;

public class DatabaseType {

	private final String typeName ;
	private final String driver ;
	private final String url ;
	private final String metadataCatalog ;
	
	public DatabaseType(String typeName, String driver, String url, String metadataCatalog) {
		super();
		this.typeName = typeName;
		this.driver = driver;
		this.url = url;
		this.metadataCatalog = metadataCatalog ;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getMetadataCatalog() {
		return metadataCatalog;
	}

	@Override
	public String toString() {
		return "[typeName=" + typeName + ", driver=" + driver
				+ ", url=" + url + ", metadataCatalog=" + metadataCatalog + "]";
	}
	
}
