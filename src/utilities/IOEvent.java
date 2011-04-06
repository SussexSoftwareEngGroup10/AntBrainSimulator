package utilities;

/**
 * Thrown in place of an IOException
 * 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class IOEvent extends Event {
	private static final long serialVersionUID = 7107071594211664095L;

	public IOEvent(String message) {
		super(message);
	}

	@Override
	protected void setSeverity() {
		severity = Severity.WARNING;
	}
}
