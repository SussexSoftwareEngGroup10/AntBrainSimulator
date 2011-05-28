package antWorld;

import java.util.concurrent.Semaphore;

import engine.GameEngine;

import utilities.Logger;
import utilities.WarningEvent;

import antBrain.Brain;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Simulation extends Thread {
	private final GameEngine gameEngine;
	private final Brain blackBrain;
	private final Brain redBrain;
	private final Semaphore semaphore;
	private final int fitness;
	private final boolean useFitness;
	private final int rounds;
	private World world;
	
	/**
	 * @param gameEngine
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param fitness
	 * @param useFitness
	 * @param rounds
	 * @param world
	 */
	public Simulation(GameEngine gameEngine, Brain blackBrain, Brain redBrain, Semaphore semaphore,
		int fitness, boolean useFitness, int rounds, World world) {
		this.gameEngine = gameEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.useFitness = useFitness;
		this.world = world;
		this.rounds = rounds;
	}
	
	/**
	 * @return
	 */
	public final Brain getBlackBrain() {
		return this.blackBrain;
	}
	
	/**
	 * @return
	 */
	public final Brain getRedBrain() {
		return this.redBrain;
	}
	
	/**
	 * @return
	 */
	public final World getWorld() {
		return this.world;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		//World now has better brain at 0, GA brain at 1
		this.world.setBrain(this.blackBrain, 0);
		this.world.setBrain(this.redBrain, 1);
		
		//Run ants for all steps, serial / in this thread
		for(int i = this.rounds; i > 0; i--){
			this.world.step();
			try{
				Thread.sleep(this.gameEngine.getSleepDur());
			}catch(InterruptedException e){
				Logger.log(new WarningEvent(e.getMessage(), e));
			}
		}
		
		//Store the result as the fitness of the red (GA) brain
		int[] anthillFood = this.world.getFoodInAnthills();
//		System.out.println(anthillFood[0]);
//		System.out.println(anthillFood[1]);
//		System.out.println(this.world);
		
		if(this.useFitness){
			//Increment fitness by score
			this.blackBrain.setFitness(this.fitness, anthillFood[0] - anthillFood[1]);
			this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
		}else{
			//set wins, losses and draws
			if(anthillFood[0] > anthillFood[1]){
				this.blackBrain.incrementWins();
				this.redBrain.incrementLosses();
			}else if(anthillFood[0] < anthillFood[1]){
				this.blackBrain.incrementLosses();
				this.redBrain.incrementWins();
			}else{
				this.blackBrain.incrementDraws();
				this.redBrain.incrementDraws();
			}
		}
		
		//Let the GameEngine thread know that this simulation has completed
		if(this.semaphore != null){
			this.semaphore.release();
		}
	}
}
