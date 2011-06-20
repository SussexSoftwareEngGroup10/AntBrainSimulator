package gUI;

import engine.GameEngine;
import engine.GameStats;
import antBrain.Brain;
import antWorld.World;

/**
 * Used for running a new simulation in a new thread.
 * 
 * @author wjs25
 */
public class SimulationRunner extends Thread {
	private GameEngine gameEngine;
	private Brain blackBrain;
	private Brain redBrain;
	private World world;
	private MainWindow mainWindow;
	
	/**
	 * Constructs a new simulation runner.
	 * 
	 * @param gameEngine The game engine to run the simulation with.
	 * @param blackBrain The black brain to use.
	 * @param redBrain The read brain to use.
	 * @param world The world to run the simulation on.
	 * @param mainWindow The main window where the simulation will be displayed.
	 */
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
	@Override
	public void run(){
		//Run the simulation and pass back the stats from it to the main window
		GameStats gameStats;
		gameStats = this.gameEngine.simulate(
				this.blackBrain, this.redBrain, this.world);
		this.mainWindow.notifyGameComplete(gameStats);
	}
}
