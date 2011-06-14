package utilities;

import java.util.Calendar;

/**
 * @title Event
 * @purpose to provide methods common to subclasses, such as toString.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public abstract class Event extends Throwable {
	private static final long serialVersionUID = 1L;
	private Calendar calendar;
	protected enum Severity { INFORMATION, WARNING, ERROR }
	protected Severity severity;
	
	/**
	 * @title Event
	 * @purpose to provide a constructor common to all subclasses 
	 * @param message the message to be stored in the Event
	 */
	public Event(String message) {
		super(message);
		setSeverity();
	}
	
	/**
	 * @title Event
	 * @purpose to provide a constructor common to all subclasses 
	 * @param message the message to be stored in the Event
	 * @param cause the cause of this Event, whose StackTrace will be appended
	 * to that of this Event
	 */
	public Event(String message, Throwable cause) {
		//Adds the stack trace from the cause to that of this instance
		super(message, cause);
		setSeverity();
	}
	
	/**
	 * @title setSeverity
	 * @purpose to enable concrete subclasses to set which severity to assign 
	 * the field, which allows the toString method to add an appropriate
	 * severity level to its return String
	 */
	protected abstract void setSeverity();
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 * 
	 * @title toString
	 * @purpose to provide a detailed explanation of the severity, class, time,
	 * description and cause of the throwing of this object
	 * @return a detailed String representation of the concrete subclass of this
	 * class and its attributes
	 */
	@Override
	public String toString() {
		String s = "";
		
		//Severity
		s += "SEVERITY: ";
		s += this.severity.toString().toLowerCase() + ";  ";
		//"SEVERITY: INFORMATION;  ".length == 24
		while(s.length() < 24){
			s += " ";
		}
		
		//Time
		s += "TIME: ";
		//I believe Calendar is more efficient than Date, and it's wrapper, Time
//		s += new Date();
//		s += new Time(System.currentTimeMillis());
		this.calendar = Calendar.getInstance();
		int hours = this.calendar.get(Calendar.HOUR_OF_DAY);
		if(hours < 10) s += "0";
		s += hours + ":";
		int minutes = this.calendar.get(Calendar.MINUTE);
		if(minutes < 10) s += "0";
		s += minutes + ":";
		int seconds = this.calendar.get(Calendar.SECOND);
		if(seconds < 10) s += "0";
		s += seconds;
		s += ";  ";
		
		//Class name
		s += "CLASS_NAME: ";
		String className = this.getClass().getName();
		//Remove package name from class name
		className = className.substring(className.lastIndexOf(".") + 1);
		s += className;
		s += ";  ";
		while(s.length() < 100){
			s += " ";
		}
		
		//Message/Description
		s += "DESCRIPTION: ";
		s += this.getMessage();
		s += ";  ";
		while(s.length() < 300){
			s += " ";
		}
		
		//Object throw location/cause
		s += "SOURCE: ";
		String cause = "";
		StackTraceElement[] trace = this.getStackTrace();
		for(int i = 0; i < trace.length; i++){
			cause += trace[i];
			if(i < trace.length - 1){
				cause += ", ";
			}
		}
		s += cause;
		
		return s;
	}
}
