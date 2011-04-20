package engine;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import utilities.InformationEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.BrainController;
import antWorld.Ant;
import antWorld.World;

/**
 * Dummy DummyEngine class
 * 
 * sortByFitness is needed by the GeneticAlgorithm,
 * it must order the population by how good they are at winning games, best first
 * 
 * the main method is a dummy version of what the engine might do,
 * used to test the functionality of antBrain and antWorld classes
 * 
 * To work out get the winner in a game, call world.getFoodInAnthills()
 * or world.survivingAntsBySpecies()
 * both return an int[], where index 0 == black and 1 == red
 * high numbers are better for both
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class DummyEngine {
//	private static final Thread main = Thread.currentThread();
	private static final Brain betterBrain = BrainController.readBrainFrom("better_example");
	
	//World arguments
	//easier than passing around values,
	//obviously need to make dynamic in final version
	private static final int seed = 1;
	private static final int rows = 140;
	private static final int cols = 140;
	private static final int rocks = 13;
	private static final int anthills = 2;
	private static final int anthillSideLength = 7;	//Less means less ants, which is quicker
	private static final int foodBlobCount = 10;
	private static final int foodBlobSideLength = 5;
	private static final int foodBlobCellFoodCount = 5;
	private static final int antInitialDirection = 0;
	
	//GA arguments
	private static final int epochs = 1000;					//Less is quicker, but less likely to generate an improved brain
	private static final int rounds = 1000;					//Less is quicker, but reduces the accuracy of the GA
	private static final int popSize = 100;					//Less is quicker, but searches less of the search space for brains
	private static final int elite = 5;						//Less is slower, but avoids getting stuck with lucky starting brain
	private static final int mutationRate = 10;				//Less is more, inverse
	private static final int stepsPerSync = 1;				//Less is slower
	

	private final CyclicBarrier stepBarrier =
		new CyclicBarrier(anthills * (World.hexArea(anthillSideLength)));
	private final CyclicBarrier endBarrier =
		new CyclicBarrier(anthills * (World.hexArea(anthillSideLength)));
	
	public DummyEngine() {
		if(Logger.getLogLevel() >= 3){
			Logger.log(new InformationEvent("New Engine object constructed"));
		}
	}
	
	//How many times GA-related methods are called in each run,
	//ignoring elite (popSize -= elite)
	//Variables in descending order and approximate values
	//rounds,  epochs, ants, popSize, stateNum, k
	//300000, ~1000,  ~250, <100,    <100,     10
	//The more a method is called, the more efficient it should be
	//Duration of a time log = 1,130, subtracted this value from every duration
	//Based on the isAlive, step and isSurrounded method durations,
	//I predict that one run would take 1.4 quadrillion nanoseconds,
	//which equals 1.4 million seconds,
	//which equals >385 hours,
	//which equals >16 days
	//Running a multithreaded version on a server would help
	//Or changing the compiler settings to optimise for speed of execution
	//After first improvements, time reduced to >6 days
	//Start: 100ns
	//combine ant methods: 80ns
	//remove excessive isSurrounded() checks: 30ns
	//removed arrays and a number of variables from the ant code: 30-40ns
	
//Method name						  Number of calls							Number of calls				   Number of calls		Duration of	  Duration of all calls		Duration	After first								
//									  per run, by variable						per run, using defaults		   per run				one call / ns (calls * duration) / ns	per run / %	improvements
//DummyEngine.main()				  == 1										== 1						   ==                 1 == 			  == >1,387,516,889,200,000 == 100
//GeneticAlgorithm.createPopulation() == 1										== 1						   ==                 1 == 			  == 						== 
//GeneticAlgorithm.evolve()			  == 1										== 1						   ==                 1 == 			  == 						== 
//GeneticAlgorithm.evolve().loop	  == epochs									== 1,000					   ==             1,000 == 			  == 					  	== 
//DummyEngine.contest()				  == epochs									== 1,000					   ==             1,000 == 			  == 					  	== 
//DummyEngine.contestSimulation()	  == epochs * popSize						== 1,000 * 100				   ==           100,000 == 			  == 	 				  	== 
//GeneticAlgorithm.breed()			  == epochs									== 1,000					   ==             1,000 == 1,500	  ==              1,500,000 == 0
//population.sort()					  == epochs									== 1,000					   ==             1,000 == 1,700	  ==              1,700,000 == 0
//BrainParser.writeBrainTo()		  == epochs									== 1,000					   ==             1,000 == 6,000	  ==              6,000,000 == 0
//Ant.isKill()						  == epochs * ants     * popSize			== 1,000 * 250 * 100		   ==        25,000,000 == 1		  ==             25,000,000 == 0
//Ant()								  == epochs * ants     * popSize			== 1,000 * 250 * 100		   ==        25,000,000 == 1		  ==             25,000,000 == 0
//World.setBrain()					  == epochs * popSize  * 2					== 1,000 * 100 * 2			   ==           200,000 == 250		  ==             50,000,000 == 0
//Brain.setState()					  == epochs * popSize  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 25		  ==            125,000,000 == 0
//World()							  == epochs * popSize						== 1,000 * 100				   ==           100,000 == 1,750	  ==            175,000,000 == 0
//GeneticAlgorithm.ranGenes()		  == epochs * stateNum * k					== 1,000 * 100 * 10			   ==         1,000,000 == 400		  ==            400,000,000 == 0
//State.getValues()					  == epochs * popSize  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 80		  ==            400,000,000 == 0
//World.getFoodInAnthills()			  == epochs * popSize						== 1,000 * 100				   ==           100,000 == 6,800	  ==            680,000,000 == 0
//GeneticAlgorithm.combineStates()	  == epochs * popSize  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 700		  ==          3,500,000,000 == 0
//GeneticAlgorithm.mutateGenes()	  == epochs * popSize  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 300		  ==          1,500,000,000 == 0
//Ant.setBrain()					  == epochs * ants     * popSize  * 2		== 1,000 * 250 * 100 * 2	   ==        50,000,000 == 200		  ==         10,000,000,000 == 0
//Ant.isAlive()						  == rounds * epochs   * ants     * popSize	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 30		  ==    225,000,000,000,000 == 15		== N/A
//Ant.step()						  == rounds * epochs   * ants     * popSize	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 75		  ==    562,500,000,000,000 == 39 (100)	== 40
//Ant.isSurrounded()				  == rounds * epochs   * ants     * popSize	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 80		  ==    600,000,000,000,000 == 46		== N/A
	
	public void sortByFitness(Brain[] population, int rounds) {
		evaluateFitnessContest(population, rounds);
	}
	
	private void evaluateFitnessContest(Brain[] population, int rounds) {
		Brain brain;
		int i = 0;
		
		//Set fitness for each brain against the best brain in the population
		//Assumes population has been sorted
		//Dynamic fitness test:
//		Brain bestBrain = population[population.length - 1];
		//Else use static fitness test (bestBrain field)
		for(i = 0; i < population.length; i++){
			brain = population[i];
			//Brains from previous contests may remain in the elite
			//their fitness does not need to be calculated again
			//Only if the fitness test is the same every time,
			//i.e. tested against the same brain
			if(brain.getFitness() == 0){
				brain.setFitness(evaluateFitnessContestSimulation(betterBrain, brain, rounds));
			}
		}
		Arrays.sort(population);
	}
	
	private int evaluateFitnessContestSimulation(Brain bestBrain, Brain brain, int rounds) {
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		World world = new World(seed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength,
			foodBlobCellFoodCount, antInitialDirection);
		//World now has better brain at 0, GA brain at 1
		world.setBrain(bestBrain, 0);
		world.setBrain(brain, 1);
		
		Ant[] ants = world.getAnts();
		
		//Run the simulation
		if(Logger.getLogLevel() >= 5){
			Logger.log(new InformationEvent("Begun simulation"));
		}
		
//		// TIMING
		System.gc();
//		ArrayList<Long> times;
//		long mean;
//		times = new ArrayList<Long>();
//		mean = 0;
		Logger.restartTimer();
//		// /TIMING
		
//		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
//		int interrupts = rounds / stepsPerSync;
//		for(int r = 0; r < rounds ; r++){
//		CountDownLatch[] latches = new CountDownLatch[interrupts];
//		for(int c = 0; c < latches.length; c++){
//			latches[c] = new CountDownLatch(ants.length);
//		}//When all the ants countDown, ends
			for(Ant ant : ants){
//				// TIMING
//				System.gc();
//				Logger.restartTimer();
//				// /TIMING
				
				//alive check is in step(),
				//surrounded checks and kill are called after move()
				//The threading, in particular, getting the locks on synchronised methods,
				//may slow it down so much that the change is insignificant
				//Other problems may arise, such as ants being killed half way through a call,
				//testing is needed
				//No guarantee about the order ant.step() is executed
//				ant.step();
				ant.setBarriers(this.stepBarrier, this.endBarrier);	//TODO improve
				ant.start();
//				ant.interrupt();	//steps (rounds / interrupts) step()s
//				ant.start();
				
				//TODO
				//no polling, more object reuse (inc. ants, ant, maybe world), factorise,
				//think about algorithm more, serialize for resuming
				//JIT, inline(javac -O MyClass), arrays more, no enumerations
				
//				times.add(Logger.getCurrentTime());	// TIMING
			}
			try{
				this.endBarrier.await();
			}catch(InterruptedException e){
				e.printStackTrace();
			}catch(BrokenBarrierException e){
				//All ants have completed all of their steps
			}
//			try{
//				latches[latches.length - 1].await();
//			}catch(InterruptedException e){
//				e.printStackTrace();
//			}
//			Thread.yield();	//Ensure all ants have completed their step
//		}
		
//		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);	//might not work
		
//		// TIMING
		long mean = (Logger.getCurrentTime() / rounds) / ants.length;	//for 1 step()
		System.out.println(mean + "ns");
//		for(Long t : times){
//			mean += t;
//		}
//		mean = mean / times.size();
//		System.out.println("MEAN: " + mean + "ns");
//		// /TIMING
		
		//Fitness of the GA brain = its food - opponent's food
		int[] anthillFood = world.getFoodInAnthills();
		return anthillFood[1] - anthillFood[0];
	}
	
	public static void main(String args[]) {
		Logger.clearLogs();
		Logger.setLogLevel(1.5);
		//Synchronise number of step() calls in each ant in a world after n calls
		World.setSteps(stepsPerSync);
		
//		//Calculate duration of the timing methods
//		ArrayList<Long> times;
//		long mean = 0;
//		int i = 0;
//		int j = 0;
//		for(i = 0; i < 10; i++){
//			times = new ArrayList<Long>();
//			mean = 0;
//			for(j = 0; j < 10000000; j++){
//				Logger.restartTimer();
//				times.add(Logger.getCurrentTime());
//			}
//			for(Long t : times){
//				mean += t;
//			}
//			mean = mean / times.size();
//			System.out.println("MEAN: " + mean);
//		}
		
		//Setup world
		//Seed is also used to determine ant moves,
		//so exactly the same simulation can be replayed
		//could use a seeded world for every GA game,
		//(possibly) fairer and quicker, but less random, evolution
		//more efficient to test all GA population brains against
		//the betterBrain with seed == 1
		World world = new World(seed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength,
			foodBlobCellFoodCount, antInitialDirection);
		
		//Setup brains
		//Black is the default brain, read in from file
		//Red is the best one found by the GeneticAlgorithm with parameters specified
		//The better red does relative to black, the better the GA is
//		Brain blankBrain = BrainController.readBrainFrom("blank");
		
		//Evolve and get the best brain from the GeneticAlgorithm
		//betterBrain is a decent place to start from
		//but more likely to get stuck there in the optima,
		//blankBrain is a worse starting point, it would take longer to get to a good brain,
		//but it encourages the brains generated to be more random
		Brain gaBrain = BrainController.getBestGABrain(betterBrain, new DummyEngine(), epochs, rounds, popSize, elite, mutationRate);
//		Brain gaBrain = BrainController.readBrainFrom("ga_result");
		world.setBrain(betterBrain, 0);	//black
		world.setBrain(gaBrain, 1);		//red
		
		Ant[] ants = world.getAnts();
		
		//Run the simulation, test the Brain result from the GA against bestBrain
		int r = 0;
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Begun simulation"));
		}
		for(r = 0; r < rounds; r++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		
		if(Logger.getLogLevel() >= 3){
			Ant[][] antPlayers = world.getAntsBySpecies();
			int[] survivors = world.survivingAntsBySpecies();
			if(survivors.length > 0){
				int blackAnts = antPlayers[0].length;
				Logger.log(new InformationEvent("Surviving black ants: " + survivors[0] + "/" + blackAnts));
			}
			if(survivors.length > 1){
				int redAnts = antPlayers[1].length;
				Logger.log(new InformationEvent("Surviving red   ants: " + survivors[1] + "/" + redAnts  ));
			}

			int[] anthillFood = world.getFoodInAnthills();
			if(anthillFood.length > 0){
				Logger.log(new InformationEvent("Food in black anthill: " + anthillFood[0]));
			}
			if(anthillFood.length > 1){
				Logger.log(new InformationEvent("Food in red   anthill: " + anthillFood[1]));
			}
		}
		
		//TODO remove console prints, eventually, from here and Logger
		System.out.println(world);
		System.out.println("---Better Brain---\n" + betterBrain);
		System.out.println("---GA Brain---\n" + gaBrain);
		System.out.print("GA Brain ");
		if(gaBrain.equals(betterBrain)){
			System.out.print("=");
		}else{
			System.out.print("!");
		}
		System.out.println("= Better Brain");
		
		if(Logger.getLogLevel() >= 1){
			Logger.log(new InformationEvent("Virtual Machine terminated"));
		}
	}
	
	//Phil: I have implemented the below methods, I hope that's what you meant me to do
	public World generateWorld() {
		return World.getContestWorld(0);	//random seeded contest world
	}
	
	//Phil: the way I've coded the colours, black comes first, but I can change this if needed
	//Also, what do you want to happen if there's a draw? At the moment, null is returned
	public Brain run(Brain blackBrain, Brain redBrain, World world) {
		//World now has better brain at 0, GA brain at 1
		world.setBrain(blackBrain, 0);
		world.setBrain(redBrain, 1);

		Ant[] ants = world.getAnts();

		for(int r = 0; r < rounds ; r++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		int[] anthillFood = world.getFoodInAnthills();
		if(anthillFood[0] > anthillFood[1]){
			return blackBrain;
		}
		if(anthillFood[1] < anthillFood[0]){
			return redBrain;
		}
		return null;
	}
}
