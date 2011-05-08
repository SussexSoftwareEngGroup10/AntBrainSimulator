package utilities;

/**
 * Logs normal priority information about the state of the program
 * 
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
