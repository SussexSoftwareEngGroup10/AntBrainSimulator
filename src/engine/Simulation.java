package engine;

import java.util.concurrent.Semaphore;


import utilities.IllegalArgumentEvent;
import utilities.Logger;
import utilities.WarningEvent;

import antBrain.Brain;
import antWorld.World;

/**
 * @title Simulation
 * @purpose to, when given a World, and two Brain objects, allow the Ants in the
 * World to move, according to their Brains, and to record the result after
 * a number of steps have passed.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Simulation implements Runnable {
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
	 * @title Simulation
	 * @purpose to enable the construction of Simulation objects
	 * @param gameEngine the GameEngine to get the sleep duration from
	 * @param blackBrain the Brain the black Ants in the World will use
	 * @param redBrain the Brain the red Ants in the World will use
	 * @param semaphore the Semaphore to use to notify other objects of the
	 * completion of this Simulation by releasing a permit
	 * @param fitness the index of the fitness array in the Brains to increment
	 * with the result of the simulation
	 * @param useFitness if true, add the net food of the Brain's Ants to the
	 * fitness of the Brain, otherwise increment wins, losses or draws of the
	 * Brain
	 * @param rounds the number of steps that each Ant in the World will execute
	 * @param world the World to use to play the Brains against each other
	 * @throws IllegalArgumentEvent if the goal is not valid
	 */
	public Simulation(GameEngine gameEngine, Brain blackBrain, Brain redBrain,
		Semaphore semaphore, int fitness, boolean useFitness, int rounds,
		World world, String goal) throws IllegalArgumentEvent {
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
			throw new IllegalArgumentEvent("Illegal goal in Simulation constructor");
		}
	}
	
	/**
	 * @title getBlackBrain
	 * @purpose to get the Brain of the black Ants
	 * @return the Brain of the black Ants
	 */
	public final Brain getBlackBrain() {
		return this.blackBrain;
	}
	
	/**
	 * @title getRedBrain
	 * @purpose to get the Brain of the red Ants
	 * @return the Brain of the red Ants
	 */
	public final Brain getRedBrain() {
		return this.redBrain;
	}
	
	/**
	 * @title getWorld
	 * @purpose to get this Simulation's World
	 * @return the World that this Simulation will use or has used to play the
	 * given Brains against each other
	 */
	public final World getWorld() {
		return this.world;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 * @title run
	 * @purpose to execute the given World using the given Brains for the
	 * duration specified
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
