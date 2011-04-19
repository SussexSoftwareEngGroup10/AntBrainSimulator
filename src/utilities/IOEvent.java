package utilities;

/**
 * Thrown in place of an IOException
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IOEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;

	public IOEvent(String message) {
		super(message);
	}
	
	public IOEvent(String message, Throwable cause) {
		super(message, cause);
	}
}