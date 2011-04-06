package utilities;

/**
 * Logs warnings thrown by the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WarningEvent extends Event {
	private static final long serialVersionUID = 8018666895851115111L;

	public WarningEvent(String message) {
		super(message);
	}
	
	public WarningEvent(String message, Throwable cause) {
		super(message, cause);
	}
	

	@Override
	protected void setSeverity() {
		severity = Severity.WARNING;
	}
}
