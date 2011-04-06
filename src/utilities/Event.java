package utilities;

import java.util.Date;

public abstract class Event extends Throwable {
	private static final long serialVersionUID = -4971928423959892927L;
	enum Severity { INFORMATION, WARNING, ERROR };
	protected Severity severity;
	
	public Event(String message) {
		super(message);
		setSeverity();
	}
	
	protected abstract void setSeverity();
	
	public String toString() {
		String s = "";
		
		//Severity
		s += "SEVERITY: ";
		s += severity + ";  ";
		//"SEVERITY: INFORMATION;  ".length == 24
		while(s.length() < 24){
			s += " ";
		}
		
		//Time
		s += "TIME: ";
		s += new Date();
		
//		//DummyEngine
//		//TODO
		
		//Class name
		s += ";  CLASS_NAME: ";
		String className = this.getClass().getName();
		//Remove package name from class name
		className = className.substring(className.lastIndexOf(".") + 1);
		s += className;
		
		//Object throw location/cause
		s += ";  SOURCE: ";
		String cause = "";
		StackTraceElement[] trace = this.getStackTrace();
		int i = 0;
		for(i = 0; i < trace.length; i++){
			cause += trace[i];
		}
		s += cause;
		
		//Message/Description
		s += ";  DESCRIPTION: ";
		s += this.getMessage();
		
		return s;
	}
}
