package utilities;

/**
 * @title InformationEvent
 * @purpose to log information and significant events which happen during the 
 * running of the program.
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public abstract class InformationEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public InformationEvent(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public InformationEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	/* (non-Javadoc)
	 * @see utilities.Event#setSeverity()
	 */
	@Override
	protected void setSeverity() {
		this.severity = Severity.INFORMATION;
	}
}
