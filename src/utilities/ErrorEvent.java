package utilities;

/**
 * Logs errors thrown in the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class ErrorEvent extends Event {
	private static final long serialVersionUID = 1L;

	public ErrorEvent(String message) {
		super(message);
	}
	
	public ErrorEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	protected void setSeverity() {
		this.severity = Severity.ERROR;
	}
}
