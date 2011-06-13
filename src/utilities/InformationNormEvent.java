package utilities;

/**
 * @title InformationNormEvent
 * @purpose to log information of normal importance, in a similar way to
 * Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationNormEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InformationNormEvent(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InformationNormEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
