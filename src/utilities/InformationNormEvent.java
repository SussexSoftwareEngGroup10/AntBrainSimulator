package utilities;

public class InformationNormEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	public InformationNormEvent(String message) {
		super(message);
	}

	public InformationNormEvent(String message, Throwable cause) {
		super(message, cause);
	}
}
