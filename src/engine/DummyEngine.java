package engine;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.BrainParser;
import antBrain.GeneticAlgorithm;
import antBrain.Simulation;
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
	//World arguments
	private int seed;
	private int rows;
	private int cols;
	private int rocks;
	private int anthills;
	private int anthillSideLength;
	private int foodBlobCount;
	private int foodBlobSideLength;
	private int foodBlobCellFoodCount;
	private int antInitialDirection;
	
	//Simulation arguments
	private int rounds;
	
	//GA variables
	private static final int cpus = Runtime.getRuntime().availableProcessors();
	private Brain trainingBrain;
	
	public DummyEngine(int seed, int rows, int cols, int rocks, int anthills,
		int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
		int foodBlobCellFoodCount, int antInitialDirection, int rounds) {
		this.seed = seed;
		this.rows = rows;
		this.cols = cols;
		this.rocks = rocks;
		this.anthills = anthills;
		this.anthillSideLength = anthillSideLength;
		this.foodBlobCount = foodBlobCount;
		this.foodBlobSideLength = foodBlobSideLength;
		this.foodBlobCellFoodCount = foodBlobCellFoodCount;
		this.antInitialDirection = antInitialDirection;
		this.rounds = rounds;
		
		Logger.log(new InformationLowEvent("New Engine object constructed"));
	}
	
	//How many times GA-related methods are called in each run,
	//ignoring elite (popLen -= elite)
	//Variables in descending order and approximate values
	//rounds,  epochs, ants, popLen, stateNum, k
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
	
//Method name						  Number of calls							Number of calls				   Number of calls		Duration of	  Duration of all calls		Duration	After first		After						
//									  per run, by variable						per run, using defaults		   per run				one call / ns (calls * duration) / ns	per run / %	improvements	multithreading
//DummyEngine.main()				  == 1										== 1						   ==                 1 == 			  == >1,387,516,889,200,000 == 100
//GeneticAlgorithm.createPopulation() == 1										== 1						   ==                 1 == 			  == 						== 
//GeneticAlgorithm.evolve()			  == 1										== 1						   ==                 1 == 			  == 						== 
//GeneticAlgorithm.evolve().loop	  == epochs									== 1,000					   ==             1,000 == 			  == 					  	== 
//DummyEngine.contest()				  == epochs									== 1,000					   ==             1,000 == 			  == 					  	== 
//DummyEngine.contestSimulation()	  == epochs * popLen						== 1,000 * 100				   ==           100,000 == 			  == 	 				  	== 
//GeneticAlgorithm.breed()			  == epochs									== 1,000					   ==             1,000 == 1,500	  ==              1,500,000 == 0
//population.sort()					  == epochs									== 1,000					   ==             1,000 == 1,700	  ==              1,700,000 == 0
//BrainParser.writeBrainTo()		  == epochs									== 1,000					   ==             1,000 == 6,000	  ==              6,000,000 == 0
//Ant.isKill()						  == epochs * ants     * popLen				== 1,000 * 250 * 100		   ==        25,000,000 == 1		  ==             25,000,000 == 0
//Ant()								  == epochs * ants     * popLen				== 1,000 * 250 * 100		   ==        25,000,000 == 1		  ==             25,000,000 == 0
//World.setBrain()					  == epochs * popLen  * 2					== 1,000 * 100 * 2			   ==           200,000 == 250		  ==             50,000,000 == 0
//Brain.setState()					  == epochs * popLen  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 25		  ==            125,000,000 == 0
//World()							  == epochs * popLen						== 1,000 * 100				   ==           100,000 == 1,750	  ==            175,000,000 == 0
//GeneticAlgorithm.ranGenes()		  == epochs * stateNum * k					== 1,000 * 100 * 10			   ==         1,000,000 == 400		  ==            400,000,000 == 0
//State.getValues()					  == epochs * popLen  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 80		  ==            400,000,000 == 0
//World.getFoodInAnthills()			  == epochs * popLen						== 1,000 * 100				   ==           100,000 == 6,800	  ==            680,000,000 == 0
//GeneticAlgorithm.combineStates()	  == epochs * popLen  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 700		  ==          3,500,000,000 == 0
//GeneticAlgorithm.mutateGenes()	  == epochs * popLen  * stateNum / 2		== 1,000 * 100 * 100 / 2	   ==         5,000,000 == 300		  ==          1,500,000,000 == 0
//Ant.setBrain()					  == epochs * ants     * popLen  * 2		== 1,000 * 250 * 100 * 2	   ==        50,000,000 == 200		  ==         10,000,000,000 == 0
//Ant.isAlive()						  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 30		  ==    225,000,000,000,000 == 15		== N/A		
//Ant.step()						  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 75		  ==    562,500,000,000,000 == 39 (100)	== 40		None
//Ant.isSurrounded()				  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 80		  ==    600,000,000,000,000 == 46		== N/A		
	
	public Brain getBestGABrain(Brain startBrain, Brain trainingBrain, int epochs,
		int rounds, int popLen, int elite, int mutationRate) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(cpus, cpus, 1,
			TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(popLen * 2));
		Semaphore semaphore = new Semaphore(popLen * 2, true);
		
		this.trainingBrain = trainingBrain;
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		geneticAlgorithm.createPopulation(startBrain, popLen);
		geneticAlgorithm.evolve(this, threadPoolExecutor, semaphore,
			epochs, rounds, elite, mutationRate);
		return geneticAlgorithm.getBestBrain();
	}
	
	public void sortByFitness(ThreadPoolExecutor threadPoolExecutor,
		Semaphore semaphore, Brain[] population) {
		//Ensure all Brains have a fitness
		evaluateFitnessContest(threadPoolExecutor, semaphore, population);
		
		//Sort by fitnesses calculated
		Arrays.sort(population);
	}
	
	private void evaluateFitnessContest(ThreadPoolExecutor threadPoolExecutor,
		Semaphore semaphore, Brain[] population) {
		//Ants let each other know they've finished a step with the stepBarrier
		//Ants let their sim know they've finished all steps with the endBarrier
		//Sims let the engine know they've finished their sim with the contestEndBarrier
		
		//Set fitness for each brain against the best brain in the population
		//Assumes population has been sorted
		//Get the brain in the population with the highest fitness, if any have one
		int i = population.length;
		Brain trainingBrain;
		do{
			if(i <= 0){
				//If there is no elite, or this is the first epoch,
				//no brains will have a fitness,
				//In these cases, the static Brain test must be used instead
				trainingBrain = this.trainingBrain;
				break;
			}
			i--;
			trainingBrain = population[i];
		}while(trainingBrain.getFitness() == 0);
		
		//Multi-Threaded
		//Get popLen permits, restore as runs complete
		semaphore.acquireUninterruptibly(population.length * 2);
		
		//Set fitness for every brain in population
		for(Brain brain : population){
			brain.setFitness(0);
			//Absolute fitness test
			threadPoolExecutor.execute(
				new Simulation(this.trainingBrain, brain, semaphore));
			//Relative fitness test
			threadPoolExecutor.execute(
				new Simulation(trainingBrain, brain, semaphore));
		}
		
		//Await completion of all calls
		semaphore.acquireUninterruptibly(population.length * 2);
		semaphore.release(population.length * 2);
		
//		//Single-Threaded
//		Brain brain;
//		int i;
//		for(i = 0; i < population.length; i++){
//			brain = population[i];
//			//Brains from previous contests may remain in the elite
//			//their fitness does not need to be calculated again
//			//Only if the fitness test is the same every time,
//			//i.e. tested against the same brain
//			if(brain.getFitness() == 0){
//				brain.setFitness(evaluateFitnessContestSimulation(
//					trainingBrain, brain, rounds));
//			}
//		}
	}
	
	@SuppressWarnings("unused")
	private int evaluateFitnessContestSimulation(Brain bestBrain, Brain brain, int rounds) {
		//Using a seed to construct a random means the worlds generated will be more
		//uniform than using cloning, which seems to be slightly slower for some reason
		World world = new World(this.seed, this.rows, this.cols, this.rocks, this.anthills,
			this.anthillSideLength, this.foodBlobCount, this.foodBlobSideLength,
			this.foodBlobCellFoodCount, this.antInitialDirection);
		//World now has better brain at 0, GA brain at 1
		world.setBrain(bestBrain, 0);
		world.setBrain(brain, 1);
		
		Ant[] ants = world.getAnts();
		
		//Run the simulation
		Logger.log(new InformationLowEvent("Begun simulation"));
		
		for(int r = 0; r < rounds ; r++){
			for(Ant ant : ants){
				//For efficiency, alive check is in step(),
				//And surrounded checks and kill are called after move()
				ant.step();
			}
		}
		
		//Fitness of the GA brain = its food - opponent's food
		int[] anthillFood = world.getFoodInAnthills();
		return anthillFood[1] - anthillFood[0];
	}
	
	public static void main(String args[]) {
		//TODO combine GA and regular sim methods, bit of a pain
		//TODO make sure 2 evolve()s can be run using 1 GeneticAlgorithm and DummyEngine
		//TODO Brain number of states in GeneticAlgorithm.breed(), allow removal of states
			//or at least allow a numOfStates parameter
		//TODO remove polling in Ant.step()
		//TODO add more information logging
		//TODO test effects of changing targetStates in GeneticAlgorithm.breed() //QA
		//TODO javadoc
		//TODO reusing Worlds would increase efficiency
		
		//Setup variables
		//World arguments
		//Used by the GA to train Brains
		int trainSeed = 1;
		//Used here to test resulting Brain
		int testSeed = 0;
		int rows = 140;
		int cols = 140;
		int rocks = 13;
		int anthills = 2;
		//More means more ants, which is slower
		int anthillSideLength = 7;
		int foodBlobCount = 10;
		int foodBlobSideLength = 5;
		int foodBlobCellFoodCount = 5;
		int antInitialDirection = 0;
		
		//GA arguments
		//More is slower, and more likely to generate an improved brain
		int epochs = 2000;
		//More is slower, and increases the accuracy of the GA
		int rounds = 300000;
		//More is slower, and searches more of the search space for brains
		int popLen = 50;
		//More is faster, but increases the likelihood of getting stuck
		//with lucky starting brain
		int elite = 5;
		//More is less change per epoch
		int mutationRate = 25;
		
		Logger.clearLogs();
//		GeneticAlgorithm.clearSaves();
		Logger.setLogLevel(Logger.LogLevel.NORM_LOGGING);
		Simulation.setValues(trainSeed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength, foodBlobCellFoodCount,
			antInitialDirection, rounds);
		
		//Black is the default brain, read in from file
		//Red is the best one found by the GeneticAlgorithm with parameters specified
		//The better red does relative to black, the better the GA is
		
		//Evolve and get the best brain from the GeneticAlgorithm
		//trainingBrain is a decent place to start from
		//but more likely to get stuck there in the optima,
		//blankBrain is a worse starting point, it would take longer to get to a good brain,
		//but it encourages the brains generated to be more random
		Brain trainingBrain = BrainParser.readBrainFrom("better_example");
		DummyEngine dummyEngine = new DummyEngine(trainSeed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength, foodBlobCellFoodCount,
			antInitialDirection, rounds);
		Brain gaBrain = dummyEngine.getBestGABrain(trainingBrain, trainingBrain, epochs,
			rounds, popLen, elite, mutationRate);
//		Brain gaBrain = BrainController.readBrainFrom("ga_result");
		
		//Setup world
		//Seed is also used to determine ant moves,
		//so exactly the same simulation can be replayed
		//could use a seeded world for every GA game,
		//(possibly) fairer and quicker, but less random, evolution
		//more efficient to test all GA population brains against
		//the trainingBrain with seed == 1
		World world = new World(testSeed, rows, cols, rocks, anthills,
			anthillSideLength, foodBlobCount, foodBlobSideLength,
			foodBlobCellFoodCount, antInitialDirection);
		
		//Setup brains
		world.setBrain(trainingBrain, 0);	//black
		world.setBrain(gaBrain, 1);		//red
		
		Ant[] ants = world.getAnts();
		
		//Run the simulation, test the Brain result from the GA against bestBrain
		int r = 0;
		Logger.log(new InformationLowEvent("Begun simulation"));
		
		for(r = 0; r < rounds; r++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		Ant[][] antPlayers = world.getAntsBySpecies();
		int[] survivors = world.survivingAntsBySpecies();
		if(survivors.length > 0){
			int blackAnts = antPlayers[0].length;
			Logger.log(new InformationHighEvent("Surviving black ants: "
				+ survivors[0] + "/" + blackAnts));
		}
		if(survivors.length > 1){
			int redAnts = antPlayers[1].length;
			Logger.log(new InformationHighEvent("Surviving red   ants: "
				+ survivors[1] + "/" + redAnts  ));
		}

		int[] anthillFood = world.getFoodInAnthills();
		if(anthillFood.length > 0){
			Logger.log(new InformationHighEvent("Food in black anthill: "
				+ anthillFood[0]));
		}
		if(anthillFood.length > 1){
			Logger.log(new InformationHighEvent("Food in red   anthill: "
				+ anthillFood[1]));
		}
		
		System.out.println(world);
		System.out.println("---better_example.brain---\n" + trainingBrain);
		System.out.println("---ga_result.brain---\n" + gaBrain);
		System.out.print("GA Brain ");
		
//		if(gaBrain.equals(trainingBrain)){
//			System.out.print("=");
//		}else{
//			System.out.print("!");
//		}
//		System.out.println("= Better Brain");
		
		Logger.log(new InformationHighEvent("Virtual Machine terminated normally"));
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
		
		for(int r = 0; r < this.rounds ; r++){
			for(Ant ant : ants){
				ant.step();
			}
		}
		
		int[] anthillFood = world.getFoodInAnthills();
		//Black wins
		if(anthillFood[0] > anthillFood[1]){
			return blackBrain;
		}
		//Red wins
		if(anthillFood[1] > anthillFood[0]){
			return redBrain;
		}
		return null;
	}
}
