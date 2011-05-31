package gUI;

import java.util.Stack;

import utilities.ErrorEvent;
import utilities.IllegalArgumentEvent;
import engine.GameEngine;
import antBrain.Brain;
import antWorld.World;

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
	@Override
	public void run(){
		World world;
		try {
			gameEngine.contestSetup(brains);
			world = World.getContestWorld(1);
			Stack<World> worlds = new Stack<World>();
			for(int i = 0 ; i <= brains.length; i++) {
				while(worlds.size() < brains.length - 1) {
					worlds.push((World) world.clone());
				}
				contestWindow.setProgressBarVal(i);
				gameEngine.contestStep(worlds);
			}
			contestWindow.notifyContestComplete(brains);

		} catch (IllegalArgumentEvent iAE) {
			GUIErrorMsg.displayErrorMsg("A supplied brain is not valid!");
		} catch (ErrorEvent e) {
			GUIErrorMsg.displayErrorMsg("World generation failed!");
		}
	}
}
