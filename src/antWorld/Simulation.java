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
public final class Simulation extends Thread {
	private final DummyEngine dummyEngine;
	private final Brain blackBrain;
	private final Brain redBrain;
	private final Semaphore semaphore;
	private final int fitness;
	private final boolean useFitness;
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
		Semaphore semaphore, int sleepDur, int fitness, boolean useFitness, int rounds, int seed) {
		this.dummyEngine = dummyEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.useFitness = useFitness;
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
	public Simulation(DummyEngine dummyEngine, Brain blackBrain, Brain redBrain, Semaphore semaphore,
		int sleepDur, int fitness, boolean useFitness, int rounds, World world) {
		this.dummyEngine = dummyEngine;
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.useFitness = useFitness;
		this.world = world;
		this.rounds = rounds;
		this.sleepDur = sleepDur;
	}
	
	/**
	 * @return
	 */
	public Brain getBlackBrain() {
		return this.blackBrain;
	}
	
	/**
	 * @return
	 */
	public Brain getRedBrain() {
		return this.redBrain;
	}
	
	/**
	 * @return
	 */
	public World getWorld() {
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
		if(this.world == null){
			this.world = this.dummyEngine.generateWorld(this.seed);
		}
		
		this.world.setBrain(this.blackBrain, 0);
		this.world.setBrain(this.redBrain, 1);
		
		Ant[] ants = this.world.getAnts();
		
		//Run ants for all steps, serial / in this thread
		for(int i = this.rounds; i > 0; i--){
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
		int[] anthillFood = this.world.getFoodInAnthills();
		
		if(this.useFitness){
			//Increment fitness by score
			this.blackBrain.setFitness(this.fitness, anthillFood[0] - anthillFood[1]);
			this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
		}else{
			this.blackBrain.setFitness(1, 0);
			this.redBrain.setFitness(1, 0);
			this.blackBrain.setFitness(2, 0);
			this.redBrain.setFitness(2, 0);
			this.blackBrain.setFitness(3, 0);
			this.redBrain.setFitness(3, 0);
			if(anthillFood[0] > anthillFood[1]){
				this.blackBrain.setFitness(0, this.blackBrain.getFitness() + 1);
				this.redBrain.setFitness(0, 0);
			}else if(anthillFood[1] > anthillFood[0]){
				this.blackBrain.setFitness(0, 0);
				this.redBrain.setFitness(0, this.redBrain.getFitness() + 1);
			}else{
				this.blackBrain.setFitness(0, 0);
				this.redBrain.setFitness(0, 0);
			}
		}
		
		//Let the main thread know that this simulation has completed
		if(this.semaphore != null){
			this.semaphore.release();
		}
	}
}
