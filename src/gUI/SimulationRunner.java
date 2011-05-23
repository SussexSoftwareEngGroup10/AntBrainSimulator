package gUI;

import engine.GameEngine;
import antBrain.Brain;
import antWorld.World;

public class SimulationRunner extends Thread {
	private GameEngine gameEngine;
	private Brain blackBrain;
	private Brain redBrain;
	private World world;
	
	public SimulationRunner(
			GameEngine gameEngine, Brain blackBrain, Brain redBrain, 
			World world) {
		this.gameEngine = gameEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.world = world;
	}
	
	/**
	 * Runs a standard simulation.
	 */
	public void run(){
		gameEngine.simulate(blackBrain, redBrain, world);
	}
}
