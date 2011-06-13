package utilities;

import java.util.concurrent.TimeUnit;

/**
 * @title ErrorEvent
 * @purpose to log the current time since the start of timing, and a message,
 * e.g. a marker for the start or end of a method call, in a similar way to
 * Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class TimeEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public TimeEvent(String message) {
		super("TIME: " + (Logger.getCurrentTime()) + " " +
			TimeUnit.NANOSECONDS.toString().toLowerCase() + ";  MESSAGE: " + message);
	}
	
	/**
	 * @param message
	 * @param timeUnit
	 */
	public TimeEvent(String message, TimeUnit timeUnit) {
		super("TIME: " + timeUnit.convert(Logger.getCurrentTime(), TimeUnit.NANOSECONDS) + " " +
			timeUnit.toString().toLowerCase() + ";  MESSAGE: " + message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TimeEvent(String message, Throwable cause) {
		super("TIME: " + (Logger.getCurrentTime()) + " " +
			TimeUnit.NANOSECONDS.toString().toLowerCase() + ";  MESSAGE: " + message, cause);
	}
	
	/**
	 * @param message
	 * @param timeUnit
	 * @param cause
	 */
	public TimeEvent(String message, TimeUnit timeUnit, Throwable cause) {
		super("TIME: " + timeUnit.convert(Logger.getCurrentTime(), TimeUnit.NANOSECONDS) + " " +
			timeUnit.toString().toLowerCase() + ";  MESSAGE: " + message, cause);
	}
	
	/**
	 * @param message
	 * @param time
	 * @param timeUnit
	 */
	public TimeEvent(String message, long time, TimeUnit timeUnit) {
		super("TIME: " + time + " " +
			timeUnit.toString().toLowerCase()  + ";  MESSAGE: " + message);
	}
	
	/**
	 * @param message
	 * @param time
	 * @param timeUnit
	 * @param cause
	 */
	public TimeEvent(String message, long time, TimeUnit timeUnit, Throwable cause) {
		super("TIME: " + time + " " +
			timeUnit.toString().toLowerCase()  + ";  MESSAGE: " + message, cause);
	}
}
