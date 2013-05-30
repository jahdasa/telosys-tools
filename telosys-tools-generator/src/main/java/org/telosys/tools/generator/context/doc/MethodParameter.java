package org.telosys.tools.generator.context.doc;

public class MethodParameter {

	private final String type ;
	private final String name ;
	private final String description ;
	
	public MethodParameter( String type, String name, String description) {
		super();
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public MethodParameter( String type, String doc) {
		super();
		this.type = type;
		String[] parts = doc.split(":");
		if ( parts.length == 1 ) {
			this.name = parts[0].trim();
			this.description = "";
		}
		else if ( parts.length > 1 ) {
			this.name = parts[0].trim();
			this.description = parts[1].trim();
		}
		else {
			this.name = "?";
			this.description = "";
		}
	}

	public MethodParameter( String type ) {
		super();
		this.type = type;
		this.name = "?";
		this.description = "";
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	
}
