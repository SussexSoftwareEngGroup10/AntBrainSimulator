package utilities;

/**
 * Logs low priority information about the state of the program
 * 
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
