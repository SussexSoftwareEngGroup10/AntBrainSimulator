package antBrain;

import java.util.concurrent.Semaphore;

import antWorld.Ant;
import antWorld.World;

public class EvaluateFitnessContestSimulation implements Runnable {
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
	private int result;
	
	public EvaluateFitnessContestSimulation(Brain blackBrain, Brain redBrain, Semaphore sem) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.sem = sem;
	}
	
	public static void setValues(int seed, int rows, int cols, int rocks, 
		int anthills, int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
		int foodBlobCellFoodCount, int antInitialDirection, int rounds) {
		EvaluateFitnessContestSimulation.seed = seed;
		EvaluateFitnessContestSimulation.rows = rows;
		EvaluateFitnessContestSimulation.cols = cols;
		EvaluateFitnessContestSimulation.rocks = rocks;
		EvaluateFitnessContestSimulation.anthills = anthills;
		EvaluateFitnessContestSimulation.anthillSideLength = anthillSideLength;
		EvaluateFitnessContestSimulation.foodBlobCount = foodBlobCount;
		EvaluateFitnessContestSimulation.foodBlobSideLength = foodBlobSideLength;
		EvaluateFitnessContestSimulation.foodBlobCellFoodCount = foodBlobCellFoodCount;
		EvaluateFitnessContestSimulation.antInitialDirection = antInitialDirection;
		EvaluateFitnessContestSimulation.rounds = rounds;
	}
	
//	@Override
//	public Object call() {
//		//Using a seed to construct a random means the worlds generated will be more
//		//uniform than using cloning, which seems to be slightly slower for some reason
//		World world = new World(seed, rows, cols, rocks, anthills,
//			anthillSideLength, foodBlobCount, foodBlobSideLength,
//			foodBlobCellFoodCount, antInitialDirection);
//		//World now has better brain at 0, GA brain at 1
//		world.setBrain(this.blackBrain, 0);
//		world.setBrain(this.redBrain, 1);
//		
//		Ant[] ants = world.getAnts();
//		
//		//Run ants for all steps
//		for(int i = 0; i < rounds; i++){
//			for(Ant ant : ants){
//				ant.step();
//			}
//		}
//		
//		int[] anthillFood = world.getFoodInAnthills();
//		this.redBrain.setFitness(anthillFood[1] - anthillFood[0]);
//		
//		return null;
//	}
	
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
		
		//Run ants for all steps
		for(int i = 0; i < rounds; i++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		int[] anthillFood = world.getFoodInAnthills();
		this.redBrain.setFitness(anthillFood[1] - anthillFood[0]);
		
		this.sem.release();
		
		/* OLD CODE
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		World world = new World(seed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength,
			foodBlobCellFoodCount, antInitialDirection);
		//World now has better brain at 0, GA brain at 1
		world.setBrain(this.blackBrain, 0);
		world.setBrain(this.redBrain, 1);
		
		Ant[] ants = world.getAnts();
		
		//Setup barriers and run ants for all steps
		for(int i = 0; i < rounds; i++){
			for(Ant ant : ants){
//				ant.setBarriers(this.stepBarrier, this.endBarrier);
//				ant.start();
				ant.step();
			}
		}
		
		//Wait for the ants to finish
//		try{
//			this.endBarrier.await();
//		}catch(InterruptedException e){
//			e.printStackTrace();
//		}catch(BrokenBarrierException e){
//			//All ants have completed all of their steps
//		}
		
		//Fitness of the GA brain = its food - opponent's food
		int[] anthillFood = world.getFoodInAnthills();
		this.result = anthillFood[1] - anthillFood[0];
		
		//Wait for the rest of the sims to finish
		try{
			System.out.println("awaits: " + (this.contestEndBarrier.getNumberWaiting() + 1)
				+ ", out of: " + this.contestEndBarrier.getParties());
			this.contestEndBarrier.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}catch(BrokenBarrierException e){
			//All sims have completed their sim
		}
		*/
	}
	
	public int getResult() {
		return this.result;
	}
}
