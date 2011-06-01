package gUI;

import java.util.Stack;
import utilities.ErrorEvent;
import utilities.IllegalArgumentEvent;
import engine.GameEngine;
import antBrain.Brain;
import antWorld.World;

/**
 * A class used for running a new contest in a new thread.
 * 
 * @author wjs25
 */
public class ContestRunner extends Thread {
	private GameEngine gameEngine;
	private ContestWindow contestWindow;
	private Brain[] brains;
	
	/**
	 * Constructs a new ContestRunnner.
	 * 
	 * @param gameEngine The game engine to run the contest with.
	 * @param brains The array of brains to run the contest between.
	 * @param contestWindow The contest window where the contest was started 
	 * 						from.
	 */
	public ContestRunner(
			GameEngine gameEngine, Brain[] brains, 
			ContestWindow contestWindow) {
		this.gameEngine = gameEngine;
		this.contestWindow = contestWindow;
		this.brains = brains;
	}
	
	/**
	 * Runs a standard contest.
	 */
	@Override
	public void run(){
		World world;
		try {
			gameEngine.contestSetup(brains);
			//Create a world to use
			world = World.getContestWorld(1);
			Stack<World> worlds = new Stack<World>();
			for(int i = 0 ; i <= brains.length; i++) {
				//Create a new clone of the world for all the matches in this 
				//step
				while(worlds.size() < brains.length - 1) {
					worlds.push((World) world.clone());
				}
				//increase the progress bar for each iteration
				contestWindow.setProgressBarVal(i);
				//Runs a step of the contest
				gameEngine.contestStep(worlds);
			}
			//Tell the contest window that the contest is complete
			contestWindow.notifyContestComplete(brains);
		} catch (IllegalArgumentEvent iAE) {
			GUIErrorMsg.displayErrorMsg("A supplied brain is not valid!");
		} catch (ErrorEvent e) {
			GUIErrorMsg.displayErrorMsg("World generation failed!");
		}
	}
}
