package antBrain;

import java.util.concurrent.Semaphore;

import antWorld.Ant;
import antWorld.World;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Simulation implements Runnable {
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
	
	private Brain blackBrain;
	private Brain redBrain;
	private Semaphore sem;
	
	public Simulation(Brain blackBrain, Brain redBrain, Semaphore sem) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.sem = sem;
	}
	
	public static void setValues(int seed, int rows, int cols, int rocks, 
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
	
	@Override
	public void run() {
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
		
		//Increment fitness by score
		this.redBrain.setFitness(this.redBrain.getFitness() + anthillFood[1] - anthillFood[0]);
		
		//Let the main thread know that this simulation has completed
		this.sem.release();
	}
}
