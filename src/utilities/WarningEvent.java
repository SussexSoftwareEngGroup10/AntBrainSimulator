package utilities;

/**
 * @title WarningEvent
 * @purpose to log potential problems or errors thrown in the program, in a
 * similar way to Exceptions.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WarningEvent extends Event {
	private static final long serialVersionUID = 1L;

	/**
	 * @title WarningEvent
	 * @purpose to construct objects of type WarningEvent
	 * @param message the message associated with this Event
	 */
	public WarningEvent(String message) {
		super(message);
	}
	
	/**
	 * @title WarningEvent
	 * @purpose to construct objects of type WarningEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public WarningEvent(String message, Throwable cause) {
		super(message, cause);
	}
	

	/* (non-Javadoc)
	 * @see utilities.Event#setSeverity()
	 * 
	 * @title setSeverity
	 * @purpose to allow the Event class's constructor to set the severity of
	 * an Event, dependent on the severity this class declares
	 */
	@Override
	protected void setSeverity() {
		this.severity = Severity.WARNING;
	}
}
