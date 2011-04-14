package utilities;

import engine.DummyEngine;

public class TimeEvent extends InformationEvent {
	private static final long serialVersionUID = 1L;

	public TimeEvent(String message) {
		super("TIME: " + (System.nanoTime() - DummyEngine.startTime) + "  ; MESSAGE: " + message);
	}

	public TimeEvent(String message, Throwable cause) {
		super("TIME: " + (System.nanoTime() - DummyEngine.startTime) + "  ; MESSAGE: " + message, cause);
	}
}
