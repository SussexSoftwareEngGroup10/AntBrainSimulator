package utilities;

/**
 * Logs the current time since the start of the program, and a message,
 * e.g. a marker for the start or end of a method call
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class TimeEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;
	
	public TimeEvent(String message) {
		super("TIME: " + (Logger.getCurrentTime()) + "ns  ; MESSAGE: " + message);
	}

	public TimeEvent(String message, Throwable cause) {
		super("TIME: " + (Logger.getCurrentTime()) + "ns  ; MESSAGE: " + message, cause);
	}
	
	public TimeEvent(long time, String message) {
		super("TIME: " + time + "ns  ; MESSAGE: " + message);
	}
}
