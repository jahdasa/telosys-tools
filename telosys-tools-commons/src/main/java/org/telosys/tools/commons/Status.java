package org.telosys.tools.commons;

public class Status {

	private boolean       done = false ;
	private int           code = 0 ;
	private String        message = "" ;
	private Exception     exception = null ;
	private StringBuffer  log = new StringBuffer() ;

	//----------------------------------------------------------------
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean flag) {
		this.done = flag;
	}
	//----------------------------------------------------------------
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	//----------------------------------------------------------------
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	//----------------------------------------------------------------
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	//----------------------------------------------------------------
	public String getLog() {
		return log.toString();
	}
	public void log(String line) {
		this.log.append(line);
		this.log.append("\n");
	}
	
	
}
