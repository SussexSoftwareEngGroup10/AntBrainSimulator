package gUI;

import utilities.IllegalArgumentEvent;
import engine.GameEngine;
import antBrain.Brain;

public class ContestRunner extends Thread {
	private GameEngine gameEngine;
	private ContestWindow contestWindow;
	private Brain[] brains;
	
	public ContestRunner(
			GameEngine gameEngine, Brain[] brains, 
			ContestWindow contestWindow) {
		this.gameEngine = gameEngine;
		this.contestWindow = contestWindow;
		this.brains = brains;
	}
	
	/**
	 * Runs a standard simulation.
	 */
	public void run(){
		try {
			gameEngine.contestSetup(brains);
		} catch (IllegalArgumentEvent iAE) {
			GUIErrorMsg.displayErrorMsg("A supplied brain is not valid!");
		}
		gameEngine.contestStepAll();
		contestWindow.notifyContestComplete(brains);
	}
}
