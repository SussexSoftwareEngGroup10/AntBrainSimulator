package utilities;

import java.util.concurrent.TimeUnit;

/**
 *  ErrorEvent
 *  to log the current time since the start of timing, and a message,
 * e.g. a marker for the start or end of a method call, in a similar way to
 * Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class TimeEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 */
	public TimeEvent(String message) {
		super("TIME: " + (Logger.getCurrentTime()) + " " +
			TimeUnit.NANOSECONDS.toString().toLowerCase() + ";  MESSAGE: " + message);
	}
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param timeUnit the unit of time that will the stored time will take
	 */
	public TimeEvent(String message, TimeUnit timeUnit) {
		super("TIME: " + timeUnit.convert(Logger.getCurrentTime(), TimeUnit.NANOSECONDS) + " " +
			timeUnit.toString().toLowerCase() + ";  MESSAGE: " + message);
	}

	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public TimeEvent(String message, Throwable cause) {
		super("TIME: " + (Logger.getCurrentTime()) + " " +
			TimeUnit.NANOSECONDS.toString().toLowerCase() + ";  MESSAGE: " + message, cause);
	}
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param timeUnit the unit of time that will the stored time will take
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public TimeEvent(String message, TimeUnit timeUnit, Throwable cause) {
		super("TIME: " + timeUnit.convert(Logger.getCurrentTime(), TimeUnit.NANOSECONDS) + " " +
			timeUnit.toString().toLowerCase() + ";  MESSAGE: " + message, cause);
	}
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param time the amount of time, with the unit timeUnit, to be stored in the message
	 * @param timeUnit the unit of time that will the stored time will take
	 */
	public TimeEvent(String message, long time, TimeUnit timeUnit) {
		super("TIME: " + time + " " +
			timeUnit.toString().toLowerCase()  + ";  MESSAGE: " + message);
	}
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param time the amount of time, with the unit timeUnit, to be stored in the message
	 * @param timeUnit the unit of time that will the stored time will take
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public TimeEvent(String message, long time, TimeUnit timeUnit, Throwable cause) {
		super("TIME: " + time + " " +
			timeUnit.toString().toLowerCase()  + ";  MESSAGE: " + message, cause);
	}
}
