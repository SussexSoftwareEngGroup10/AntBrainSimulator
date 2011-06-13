package utilities;

/**
 * @title IllegalArgumentEvent
 * @purpose to log argument errors thrown in the program, in a similar way to
 * Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IllegalArgumentEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public IllegalArgumentEvent(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public IllegalArgumentEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
