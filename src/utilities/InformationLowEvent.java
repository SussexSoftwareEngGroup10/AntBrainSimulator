package utilities;

/**
 * @title InformationLowEvent
 * @purpose to log information of low importance, in a similar way to Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationLowEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @title InformationLowEvent
	 * @purpose to construct objects of type InformationLowEvent
	 * @param message the message associated with this Event
	 */
	public InformationLowEvent(String message) {
		super(message);
	}

	/**
	 * @title InformationLowEvent
	 * @purpose to construct objects of type InformationLowEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public InformationLowEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
