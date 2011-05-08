package utilities;

/**
 * Logs information about the state of the program
 * 
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
