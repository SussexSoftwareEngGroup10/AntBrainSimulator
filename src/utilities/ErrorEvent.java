package utilities;

/**
 *  ErrorEvent
 *  to log errors thrown in the program, in a similar way to Exceptions.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class ErrorEvent extends Event {
	private static final long serialVersionUID = 1L;

	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 */
	public ErrorEvent(String message) {
		super(message);
	}
	
	/**
	 *  ErrorEvent
	 *  to construct objects of type ErrorEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public ErrorEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	/* (non-Javadoc)
	 * @see utilities.Event#setSeverity()
	 * 
	 *  setSeverity
	 *  to allow the Event class's constructor to set the severity of
	 * an Event, dependent on the severity this class declares
	 */
	@Override
	protected void setSeverity() {
		this.severity = Severity.ERROR;
	}
}
