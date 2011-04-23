package utilities;

public class InformationHighEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	public InformationHighEvent(String message) {
		super(message);
	}

	public InformationHighEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
