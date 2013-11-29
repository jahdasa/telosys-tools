package org.telosys.tools.eclipse.plugin.editors.dbrep;

public class GenerationTaskResult {

	private final int numberOfResourcesCopied ;
	private final int numberOfFilesGenerated ;
		
	/**
	 * Constructor
	 * @param numberOfResourcesCopied
	 * @param numberOfFilesGenerated
	 */
	public GenerationTaskResult(int numberOfResourcesCopied,
			int numberOfFilesGenerated) {
		super();
		this.numberOfResourcesCopied = numberOfResourcesCopied;
		this.numberOfFilesGenerated = numberOfFilesGenerated;
	}

	/**
	 * Default constructor with 0 for all values
	 */
	public GenerationTaskResult() {
		super();
		this.numberOfResourcesCopied = 0;
		this.numberOfFilesGenerated  = 0;
	}

	public int getNumberOfResourcesCopied() {
		return numberOfResourcesCopied;
	}

	public int getNumberOfFilesGenerated() {
		return numberOfFilesGenerated;
	}
	
}
