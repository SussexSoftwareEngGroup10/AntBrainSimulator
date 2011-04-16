package utilities;

/**
 * Logs information about the state of the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationEvent extends Event {
	private static final long serialVersionUID = 1L;
	
	public InformationEvent(String message) {
		super(message);
	}
	
	public InformationEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	protected void setSeverity() {
		this.severity = Severity.INFORMATION;
	}
}
