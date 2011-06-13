package utilities;

/**
 * @title InformationLowEvent
 * @purpose to log information of low importance, in a similar way to Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationLowEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InformationLowEvent(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InformationLowEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
