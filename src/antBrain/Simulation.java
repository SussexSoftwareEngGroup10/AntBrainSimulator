package antBrain;

import java.util.concurrent.Semaphore;

import antWorld.Ant;
import antWorld.World;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Simulation implements Runnable {
	private static int seed;
	private static int rows;
	private static int cols;
	private static int rocks;
	private static int anthills;
	private static int anthillSideLength;
	private static int foodBlobCount;
	private static int foodBlobSideLength;
	private static int foodBlobCellFoodCount;
	private static int antInitialDirection;
	private static int rounds;
	
	private final Brain blackBrain;
	private final Brain redBrain;
	private final Semaphore semaphore;
	private final boolean isAbsolute;
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param semaphore
	 * @param isAbsolute
	 */
	public Simulation(Brain blackBrain, Brain redBrain, Semaphore semaphore, boolean isAbsolute) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.semaphore = semaphore;
		this.isAbsolute = isAbsolute;
	}
	
	/**
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
	 * @param rounds
	 */
	public static final void setValues(int seed, int rows, int cols, int rocks, 
		int anthills, int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
		int foodBlobCellFoodCount, int antInitialDirection, int rounds) {
		Simulation.seed = seed;
		Simulation.rows = rows;
		Simulation.cols = cols;
		Simulation.rocks = rocks;
		Simulation.anthills = anthills;
		Simulation.anthillSideLength = anthillSideLength;
		Simulation.foodBlobCount = foodBlobCount;
		Simulation.foodBlobSideLength = foodBlobSideLength;
		Simulation.foodBlobCellFoodCount = foodBlobCellFoodCount;
		Simulation.antInitialDirection = antInitialDirection;
		Simulation.rounds = rounds;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		World world = new World(seed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength,
			foodBlobCellFoodCount, antInitialDirection);
		//World now has better brain at 0, GA brain at 1
		world.setBrain(this.blackBrain, 0);
		world.setBrain(this.redBrain, 1);
		
		Ant[] ants = world.getAnts();
		
		//Run ants for all steps, serial / in this thread
		for(int i = 0; i < rounds; i++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		//Store the result as the fitness of the red (GA) brain
		int[] anthillFood = world.getFoodInAnthills();
		
		if(this.isAbsolute){
			//Increment fitness by score
			this.redBrain.setAbsoluteFitness(anthillFood[1] - anthillFood[0]);
		}else{
			this.redBrain.setRelativeFitness(anthillFood[1] - anthillFood[0]);
		}
		
		//Let the main thread know that this simulation has completed
		this.semaphore.release();
	}
}
