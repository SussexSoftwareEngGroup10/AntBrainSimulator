package utilities;

/**
 * @title IllegalArgumentEvent
 * @purpose to log argument errors thrown in the program, in a similar way to
 * Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IllegalArgumentEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @title IllegalArgumentEvent
	 * @purpose to construct objects of type IllegalArgumentEvent
	 * @param message the message associated with this Event
	 */
	public IllegalArgumentEvent(String message) {
		super(message);
	}
	
	/**
	 * @title IllegalArgumentEvent
	 * @purpose to construct objects of type IllegalArgumentEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public IllegalArgumentEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
