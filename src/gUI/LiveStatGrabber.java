package gUI;

import utilities.ErrorEvent;
import utilities.Logger;
import antWorld.World;

/**
 * A class for retrieving statistics from the current game and updating the
 * main window with them.
 * 
 * @author wjs25
 */
public class LiveStatGrabber extends Thread {
	private MainWindow mainWindow;
	private World world;
	
	private int round;
	private int blackAnthillFood;
	private int redAnthillFood;
	
	/**
	 * Constructs the LiveStatGrabber.
	 * 
	 * @param mainWindow The main window of the program.
	 * @param world The world currently being used.
	 */
	public LiveStatGrabber(MainWindow mainWindow, World world) {
		this.mainWindow = mainWindow;
		this.world = world;
	}
	
	/**
	 * Update the world to retrieve stats from.
	 * 
	 * @param world The world.
	 */
	protected void updateWorld(World world) {
		this.world = world;
	}
	
	/**
	 * Grabs the stats from the game in progress (current round and food in
	 * each ant hill) and updates the main window with it.
	 */
	@Override
	public void run() {
		//Keep updating the stats every 1 second
		while (true) {
			pause();
			try {
				this.round = this.world.getRound();
				this.blackAnthillFood = this.world.getFoodInAnthills()[0];
				this.redAnthillFood = this.world.getFoodInAnthills()[1];
				this.mainWindow.updateLiveStats(
						this.round, this.blackAnthillFood, this.redAnthillFood);
			} catch (NullPointerException nPE) { /**/ }
		}
	}
	
	/*
	 * A synchronised method for making the thread wait 1 second.  This is so
	 * that the stats only update once per second, meaning that this thread
	 * doesn't use up unnecessary amounts of CPU time
	 */
	private synchronized void pause() {
		try {
			wait(1000);
		} catch (InterruptedException e) {
			Logger.log(new ErrorEvent(
					"LiveStatGrabber thread unexpectedly interrupted!"));
		}
	}
}
