package utilities;

/**
 * @title InformationHighEvent
 * @purpose to log information of high importance, in a similar way to Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationHighEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @title InformationHighEvent
	 * @purpose to construct objects of type InformationHighEvent
	 * @param message the message associated with this Event
	 */
	public InformationHighEvent(String message) {
		super(message);
	}

	/**
	 * @title InformationHighEvent
	 * @purpose to construct objects of type InformationHighEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public InformationHighEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
