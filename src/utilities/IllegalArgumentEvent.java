package utilities;

/**
 * Thrown when a value is passed in place of an enum ordinal,
 * which does not have a corresponding enum value, and is not -1 (null)
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
