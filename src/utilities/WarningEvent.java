package utilities;

/**
 * Logs warnings thrown by the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WarningEvent extends Event {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public WarningEvent(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public WarningEvent(String message, Throwable cause) {
		super(message, cause);
	}
	

	/* (non-Javadoc)
	 * @see utilities.Event#setSeverity()
	 */
	@Override
	protected void setSeverity() {
		this.severity = Severity.WARNING;
	}
}
