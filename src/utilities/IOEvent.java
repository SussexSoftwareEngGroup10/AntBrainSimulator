package utilities;

/**
 * @title IOEvent
 * @purpose to log IO errors thrown in the program, in a similar way to
 * Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IOEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public IOEvent(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public IOEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
