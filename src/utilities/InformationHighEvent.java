package utilities;

/**
 * Logs high priority information about the state of the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationHighEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InformationHighEvent(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InformationHighEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
