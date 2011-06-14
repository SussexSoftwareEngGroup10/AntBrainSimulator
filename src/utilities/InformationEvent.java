package utilities;

/**
 * @title InformationEvent
 * @purpose to log information and significant events which happen during the 
 * running of the program.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public abstract class InformationEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @title InformationEvent
	 * @purpose to construct objects of type InformationEvent
	 * @param message the message associated with this Event
	 */
	public InformationEvent(String message) {
		super(message);
	}
	
	/**
	 * @title InformationEvent
	 * @purpose to construct objects of type InformationEvent
	 * @param message the message associated with this Event
	 * @param cause the Throwable that caused this Event's throw, the cause's
	 * StackTrace will be added to that of this Event
	 */
	public InformationEvent(String message, Throwable cause) {
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
		this.severity = Severity.INFORMATION;
	}
}
