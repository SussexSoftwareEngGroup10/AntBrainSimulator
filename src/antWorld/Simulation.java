package antWorld;

import java.util.concurrent.Semaphore;

import engine.GameEngine;

import utilities.IllegalArgumentEvent;
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
	private final int instance;
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
	 * @throws IllegalArgumentEvent 
	 */
	public Simulation(GameEngine gameEngine, Brain blackBrain, Brain redBrain, Semaphore semaphore,
		int fitness, boolean useFitness, int rounds, World world, String goal) throws IllegalArgumentEvent {
		this.gameEngine = gameEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.useFitness = useFitness;
		this.world = world;
		this.rounds = rounds;
		if(goal.equals("kills")){
			this.instance = 0;
		}else if(goal.equals("food")){
			this.instance = 1;
		}else if(goal.equals("surround")){
			this.instance = 2;
		}else{
			throw new IllegalArgumentEvent("Illegal type in Simulation constructor");
		}
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
		for(int i = this.rounds - 1; i >= 0; i--){
			this.world.step();
			try{
				Thread.sleep(this.gameEngine.getSleepDur());
			}catch(InterruptedException e){
				Logger.log(new WarningEvent(e.getMessage(), e));
			}
		}
		
		//Store the result as the fitness of the red (GA) brain
		
		if(this.useFitness){
			//Increment fitness by score
			if(this.instance == 0){			//kills
				int[] ants = this.world.survivingAntsBySpecies();
				this.blackBrain.setFitness(this.fitness, ants[0] - ants[1]);
				this.redBrain.setFitness(this.fitness, ants[1] - ants[0]);
			}else if(this.instance == 1){	//food
				int[] anthillFood = this.world.getFoodInAnthills();
				this.blackBrain.setFitness(this.fitness, anthillFood[0] - anthillFood[1]);
				this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
			}else if(this.instance == 2){	//surround
				int[] anthillFood = this.world.getFoodInAnthills();
				this.blackBrain.setFitness(this.fitness, anthillFood[0] - anthillFood[1]);
				this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
			}else{
				Logger.log(new IllegalArgumentEvent("Illegal type in Simulation.run()"));
			}
		}else{
			//set wins, losses and draws
			int[] anthillFood = this.world.getFoodInAnthills();
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
