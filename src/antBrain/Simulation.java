package antBrain;

import java.util.concurrent.Semaphore;

import antWorld.Ant;
import antWorld.World;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Simulation implements Runnable {
	private final Brain blackBrain;
	private final Brain redBrain;
	private final Semaphore semaphore;
	private final int fitness;
	private final World world;
	private final int rounds;
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param fitness
	 * @param seed
	 * @param rows
	 * @param cols
	 * @param rocks
	 * @param anthills
	 * @param anthillSideLength
	 * @param foodBlobCount
	 * @param foodBlobSideLength
	 * @param foodBlobCellFoodCount
	 * @param antInitialDirection
	 * @param gap
	 * @param rounds
	 */
	public Simulation(Brain blackBrain, Brain redBrain, Semaphore semaphore, int fitness,
		int seed, int rows, int cols, int rocks, int anthills, int anthillSideLength,
		int foodBlobCount, int foodBlobSideLength, int foodBlobCellFoodCount,
		int antInitialDirection, int gap, int rounds) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.world = new World(seed, rows, cols, rocks, anthills, anthillSideLength,
			foodBlobCount, foodBlobSideLength, foodBlobCellFoodCount,
			antInitialDirection, gap);
		this.rounds = rounds;
	}
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param fitness
	 * @param world
	 * @param rounds
	 */
	public Simulation(Brain blackBrain, Brain redBrain, Semaphore semaphore,
		int fitness, World world, int rounds) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.fitness = fitness;
		this.world = world;
		this.rounds = rounds;
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
		
		Ant[] ants = this.world.getAnts();
		
		//Run ants for all steps, serial / in this thread
		for(int i = 0; i < this.rounds; i++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		//Store the result as the fitness of the red (GA) brain
		int[] anthillFood = this.world.getFoodInAnthills();
		
		//Increment fitness by score
		this.redBrain.setFitness(this.fitness, anthillFood[1] - anthillFood[0]);
		
		//Let the main thread know that this simulation has completed
		if(this.semaphore != null){
			this.semaphore.release();
		}
	}
}
