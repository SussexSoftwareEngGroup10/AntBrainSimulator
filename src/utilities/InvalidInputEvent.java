package utilities;

/**
 * Thrown when a value is passed in place of an enum ordinal,
 * which does not have a corresponding enum value, and is not -1 (null)
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class InvalidInputEvent extends Event {
	private static final long serialVersionUID = 7577003793013502978L;
	
	public InvalidInputEvent(String message) {
		super(message);
	}
	
	@Override
	protected void setSeverity() {
		severity = Severity.WARNING;
	}
}
