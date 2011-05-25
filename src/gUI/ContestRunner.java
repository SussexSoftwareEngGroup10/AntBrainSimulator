package gUI;

import engine.GameEngine;
import antBrain.Brain;
import antWorld.World;

public class ContestRunner extends Thread {
	private GameEngine gameEngine;
	private Brain[] brains;
	
	public ContestRunner(
			GameEngine gameEngine, Brain[] brains, 
			ContestWindow contestWindow) {
		this.gameEngine = gameEngine;
		this.brains = brains;
	}
	
	/**
	 * Runs a standard simulation.
	 */
	public void run(){
		gameEngine.contestSetup(brains);
		gameEngine.contestStepAll();
	}
}
