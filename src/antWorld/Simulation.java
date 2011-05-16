package antWorld;

import java.util.concurrent.Semaphore;

import utilities.Logger;
import utilities.WarningEvent;

import engine.DummyEngine;

import antBrain.Brain;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Simulation implements Runnable {
	private final DummyEngine dummyEngine;
	private final Brain blackBrain;
	private final Brain redBrain;
	private final Semaphore semaphore;
	private final int fitness;
	private final int rounds;
	private final int sleepDur;
	private int seed;
	private World world;
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param fitness
	 * @param rounds
	 * @param seed
	 */
	public Simulation(DummyEngine dummyEngine, Brain blackBrain, Brain redBrain,
		Semaphore semaphore, int sleepDur, int fitness, int rounds, int seed) {
		this.dummyEngine = dummyEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.seed = seed;
		this.rounds = rounds;
		this.sleepDur = sleepDur;
	}
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param fitness
	 * @param rounds
	 * @param world
	 */
	public Simulation(DummyEngine dummyEngine, Brain blackBrain, Brain redBrain,
		Semaphore semaphore, int sleepDur, int fitness, int rounds, World world) {
		this.dummyEngine = dummyEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.world = world;
		this.rounds = rounds;
		this.sleepDur = sleepDur;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		//World now has better brain at 0, GA brain at 1
		World world;
		if(this.world == null){
			world = this.dummyEngine.generateWorld(this.seed);
		}else{
			world = this.world;
		}
		
		world.setBrain(this.blackBrain, 0);
		world.setBrain(this.redBrain, 1);
		
		Ant[] ants = world.getAnts();
		
		//Run ants for all steps, serial / in this thread
		for(int i = 0; i < this.rounds; i++){
			for(Ant ant : ants){
				ant.step();
			}
			if(this.sleepDur > 0){
				try{
					Thread.sleep(this.sleepDur);
				}catch(InterruptedException e){
					Logger.log(new WarningEvent(e.getMessage(), e));
				}
			}
		}
		
		//Store the result as the fitness of the red (GA) brain
		int[] anthillFood = world.getFoodInAnthills();
		
		//Increment fitness by score
		this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
		
		//Let the main thread know that this simulation has completed
		if(this.semaphore != null){
			this.semaphore.release();
		}
	}
}
