package utilities;

/**
 * Logs information about the state of the program
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InformationEvent extends Event {
	private static final long serialVersionUID = -7156111156079606305L;
	
	public InformationEvent(String message) {
		super(message);
	}
	
	public InformationEvent(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	protected void setSeverity() {
		severity = Severity.INFORMATION;
	}
}
