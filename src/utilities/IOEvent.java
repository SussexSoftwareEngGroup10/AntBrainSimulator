package utilities;

/**
 * @title IOEvent
 * @purpose to log IO errors thrown in the program, in a similar way to
 * Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IOEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @title IOEvent
	 * @purpose to construct objects of type IOEvent
	 * @param message the message associated with this Event
	 */
	public IOEvent(String message) {
		super(message);
	}
	
	/**
	 * @title IOEvent
	 * @purpose to construct objects of type IOEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public IOEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
