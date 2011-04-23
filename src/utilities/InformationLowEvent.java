package utilities;

public class InformationLowEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	public InformationLowEvent(String message) {
		super(message);
	}

	public InformationLowEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
