package utilities;

/**
 * Thrown in place of an IOException
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IOWarningEvent extends WarningEvent {
	private static final long serialVersionUID = 1L;

	public IOWarningEvent(String message) {
		super(message);
	}
	
	public IOWarningEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
