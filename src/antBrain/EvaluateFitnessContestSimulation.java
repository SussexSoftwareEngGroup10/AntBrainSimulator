package antBrain;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import antWorld.Ant;
import antWorld.World;

public class EvaluateFitnessContestSimulation extends Thread {
//	private final CyclicBarrier stepBarrier =
//		new CyclicBarrier(anthills * (World.hexArea(anthillSideLength)));
//	private final CyclicBarrier endBarrier =
//		new CyclicBarrier(anthills * (World.hexArea(anthillSideLength)) + 1);
	
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
	private CyclicBarrier contestEndBarrier;
	private int result;
	
	public EvaluateFitnessContestSimulation(Brain blackBrain, Brain redBrain,
		CyclicBarrier contestEndBarrier) {
		this.blackBrain = blackBrain;
		this.redBrain = redBrain;
		this.contestEndBarrier = contestEndBarrier;
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
		
		//Setup barriers and run ants for all steps
		for(int i = 0; i < rounds; i++){
			for(Ant ant : ants){
//				ant.setBarriers(this.stepBarrier, this.endBarrier);
//				ant.start();
				ant.step();
			}
		}
		
//		System.out.println("21");
		
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
			System.out.println("awaits: " + (this.contestEndBarrier.getNumberWaiting() + 1));
			this.contestEndBarrier.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}catch(BrokenBarrierException e){
			//All sims have completed their sim
		}
		
//		System.out.println("POS awaits: " + this.contestEndBarrier.getNumberWaiting());
		
//		System.out.println("23");
	}
	
	public int getResult() {
		return this.result;
	}
}
