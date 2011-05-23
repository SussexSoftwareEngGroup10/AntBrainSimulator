package gUI;

import engine.GameEngine;
import engine.GameStats;
import antBrain.Brain;
import antWorld.World;

public class SimulationRunner extends Thread {
	private GameEngine gameEngine;
	private Brain blackBrain;
	private Brain redBrain;
	private World world;
	private MainWindow mainWindow;
	
	public SimulationRunner(
			GameEngine gameEngine, Brain blackBrain, Brain redBrain, 
			World world, MainWindow mainWindow) {
		this.gameEngine = gameEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.world = world;
		this.mainWindow = mainWindow;
	}
	
	/**
	 * Runs a standard simulation.
	 */
	public void run(){
		GameStats gameStats;
		gameStats = gameEngine.simulate(blackBrain, redBrain, world);
		mainWindow.notifyGameComplete(gameStats);
	}
}
