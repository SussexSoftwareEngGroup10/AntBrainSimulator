package utilities;

/**
 *  InformationNormEvent
 *  to log information of normal importance, in a similar way to
 * Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationNormEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 *  InformationNormEvent
	 *  to construct objects of type InformationNormEvent
	 * @param message the message associated with this Event
	 */
	public InformationNormEvent(String message) {
		super(message);
	}

	/**
	 *  InformationNormEvent
	 *  to construct objects of type InformationNormEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public InformationNormEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
