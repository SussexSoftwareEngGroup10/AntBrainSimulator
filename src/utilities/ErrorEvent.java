package utilities;

/**
 * Logs errors thrown in the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class ErrorEvent extends Event {
	private static final long serialVersionUID = -2120678710033311329L;

	public ErrorEvent(String message) {
		super(message);
	}
	
	public ErrorEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	protected void setSeverity() {
		severity = Severity.ERROR;
	}
}
