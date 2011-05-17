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
import antWorld.Ant;
import antWorld.Simulation;
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
public class GameEngine {
	private int rows;
	private int cols;
	private int rocks;
	private int anthills;
	private int anthillSideLength;
	private int foodBlobCount;
	private int foodBlobSideLength;
	private int foodBlobCellFoodCount;
	private int antInitialDirection;
	private int gap;
	private int sleepDur;
	
	//GA variables
	private static final int cpus = Runtime.getRuntime().availableProcessors();
	private Brain absoluteTrainingBrain;
	
	private int epochs;
	private int rounds;
	private int popLen;
	private int elite;
	private int mutationRate;
	
	/**
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
	 * @param epochs
	 * @param rounds
	 * @param popLen
	 * @param elite
	 * @param mutationRate
	 */
	public GameEngine(int rows, int cols, int rocks,
		int anthills, int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
		int foodBlobCellFoodCount, int antInitialDirection, int gap, int epochs, int rounds,
		int popLen, int elite, int mutationRate, int sleepDur) {
		
		setVariables(rows, cols, rocks,
		anthills, anthillSideLength, foodBlobCount, foodBlobSideLength,
		foodBlobCellFoodCount, antInitialDirection, gap, epochs, rounds,
		popLen, elite, mutationRate, sleepDur);
		
		Logger.log(new InformationLowEvent("New Engine object constructed"));
	}
	
	/**
	 * 
	 */
	public void slowDown() {
		this.sleepDur += 10;
		if(this.sleepDur > 100) this.sleepDur = 100;
	}

	/**
	 * 
	 */
	public void speedUp() {
		this.sleepDur -= 10;
		if(this.sleepDur < 0) this.sleepDur = 0;
	}
	
	/*
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
//Ant.isKill()						  == epochs * ants     * popLen				== 1,000   * 250   * 100	   ==        25,000,000 == 1		  ==             25,000,000 == 0
//Ant()								  == epochs * ants     * popLen				== 1,000   * 250   * 100	   ==        25,000,000 == 1		  ==             25,000,000 == 0
//World.setBrain()					  == epochs * popLen   * 2					== 1,000   * 100   * 2		   ==           200,000 == 250		  ==             50,000,000 == 0
//Brain.setState()					  == epochs * popLen   * stateNum / 2		== 1,000   * 100   * 100 / 2   ==         5,000,000 == 25		  ==            125,000,000 == 0
//World()							  == epochs * popLen						== 1,000   * 100			   ==           100,000 == 1,750	  ==            175,000,000 == 0
//GeneticAlgorithm.ranGenes()		  == epochs * stateNum * k					== 1,000   * 100   * 10		   ==         1,000,000 == 400		  ==            400,000,000 == 0
//State.getValues()					  == epochs * popLen   * stateNum / 2		== 1,000   * 100   * 100 / 2   ==         5,000,000 == 80		  ==            400,000,000 == 0
//World.getFoodInAnthills()			  == epochs * popLen						== 1,000   * 100			   ==           100,000 == 6,800	  ==            680,000,000 == 0
//GeneticAlgorithm.combineStates()	  == epochs * popLen   * stateNum / 2		== 1,000   * 100   * 100 / 2   ==         5,000,000 == 700		  ==          3,500,000,000 == 0
//GeneticAlgorithm.mutateGenes()	  == epochs * popLen   * stateNum / 2		== 1,000   * 100   * 100 / 2   ==         5,000,000 == 300		  ==          1,500,000,000 == 0
//Ant.setBrain()					  == epochs * ants     * popLen   * 2		== 1,000   * 250   * 100 * 2   ==        50,000,000 == 200		  ==         10,000,000,000 == 0
//Ant.isAlive()						  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 30		  ==    225,000,000,000,000 == 15		== N/A		
//Ant.step()						  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 75		  ==    562,500,000,000,000 == 39 (100)	== 40		None
//Ant.isSurrounded()				  == rounds * epochs   * ants     * popLen	== 300,000 * 1,000 * 250 * 100 == 7,500,000,000,000 == 80		  ==    600,000,000,000,000 == 46		== N/A		
	*/
	
	/**
	 * @param startBrain
	 * @param trainingBrain
	 * @param seed
	 * @return
	 */
	public Brain getBestGABrain(Brain startBrain, Brain trainingBrain, int seed) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(cpus, cpus, 1,
			TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(this.popLen * 4));
		Semaphore semaphore = new Semaphore(this.popLen * 4, true);
		
		this.absoluteTrainingBrain = trainingBrain;
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		geneticAlgorithm.createPopulation(startBrain, this.popLen);
		geneticAlgorithm.evolve(this, seed, threadPoolExecutor, semaphore,
			this.epochs, this.rounds, this.elite, this.mutationRate);
		return geneticAlgorithm.getBestBrain();
	}
	
	/**
	 * @param seed
	 * @param threadPoolExecutor
	 * @param semaphore
	 * @param population
	 */
	public void sortByFitness(int seed, ThreadPoolExecutor threadPoolExecutor,
		Semaphore semaphore, Brain[] population) {
		//Ensure all Brains have a fitness
		evaluateFitnessContest(seed, threadPoolExecutor, semaphore, population);
		
		//Sort by fitnesses calculated
		Arrays.sort(population);
	}
	
	/**
	 * @param seed
	 * @param threadPoolExecutor
	 * @param semaphore
	 * @param population
	 */
	private void evaluateFitnessContest(int seed, ThreadPoolExecutor threadPoolExecutor,
		Semaphore semaphore, Brain[] population) {
		//Ants let each other know they've finished a step with the stepBarrier
		//Ants let their sim know they've finished all steps with the endBarrier
		//Sims let the engine know they've finished their sim with the contestEndBarrier
		
		//Set fitness for each brain against the best brain in the population
		//Assumes population has been sorted
		//Get the brain in the population with the highest fitness, if any have one
		int i = population.length;
		Brain relativeTrainingBrain;
		do{
			if(i <= 0){
				//If there is no elite, or this is the first epoch,
				//no brains will have a fitness,
				//In these cases, the static Brain test must be used instead
				relativeTrainingBrain = this.absoluteTrainingBrain;
				break;
			}
			i--;
			relativeTrainingBrain = population[i];
		}while(relativeTrainingBrain.getFitness() == 0);
		
		//Multi-Threaded
		//Get popLen permits, restore as runs complete
		semaphore.acquireUninterruptibly(population.length * 4);
		
		//Set fitness for every brain in population
		for(Brain brain : population){
			if(brain.getFitness() == 0){
				//Brain is not in elite
				//Absolute fitness tests
				threadPoolExecutor.execute(
					new Simulation(this, this.absoluteTrainingBrain, brain,
						semaphore, 0, 0, this.rounds, seed));
				threadPoolExecutor.execute(
					new Simulation(this, brain, this.absoluteTrainingBrain,
						semaphore, 0, 1, this.rounds, seed));
			}else{
				semaphore.release(2);
			}
			//Relative fitness tests
			threadPoolExecutor.execute(
				new Simulation(this, relativeTrainingBrain, brain,
					semaphore, 0, 2, this.rounds, seed));
			threadPoolExecutor.execute(
				new Simulation(this, brain, relativeTrainingBrain,
					semaphore, 0, 3, this.rounds, seed));
		}
		//Await completion of all Simulations
		semaphore.acquireUninterruptibly(population.length * 4);
		semaphore.release(population.length * 4);
	}
	
	/**
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
	 * @param epochs
	 * @param rounds
	 * @param popLen
	 * @param elite
	 * @param mutationRate
	 */
	public void setVariables(int rows, int cols, int rocks,
		int anthills, int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
		int foodBlobCellFoodCount, int antInitialDirection, int gap, int epochs, int rounds,
		int popLen, int elite, int mutationRate, int sleepDur) {
		this.rows = rows;
		this.cols = cols;
		this.rocks = rocks;
		this.anthills = anthills;
		this.anthillSideLength = anthillSideLength;
		this.foodBlobCount = foodBlobCount;
		this.foodBlobSideLength = foodBlobSideLength;
		this.foodBlobCellFoodCount = foodBlobCellFoodCount;
		this.antInitialDirection = antInitialDirection;
		this.gap = gap;
		this.epochs = epochs;
		this.rounds = rounds;
		this.popLen = popLen;
		this.elite = elite;
		this.mutationRate = mutationRate;
		this.sleepDur = sleepDur;
	}
	
	/**
	 * @param seed
	 * @return
	 */
	public World generateWorld(int seed) {
		return new World(seed, this.rows, this.cols,
			this.rocks, this.anthills, this.anthillSideLength, this.foodBlobCount,
			this.foodBlobSideLength, this.foodBlobCellFoodCount,
			this.antInitialDirection, this.gap);
	}
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param world
	 * @return
	 */
	public Brain simulate(Brain blackBrain, Brain redBrain, World world) {
		//Setup brains
		world.setBrain(blackBrain, 0);
		world.setBrain(redBrain, 1);
		
		//Run the simulation, test the Brain result from the GA against bestBrain
		Logger.log(new InformationLowEvent("Begun simulation"));
		
		new Simulation(this, blackBrain, redBrain, null,
			this.sleepDur, 0, this.rounds, world).run();
		
		//Ant results
		Ant[][] antsBySpecies = world.getAntsBySpecies();
		int[] survivors = world.survivingAntsBySpecies();
		if(survivors.length > 0){
			int blackAnts = antsBySpecies[0].length;
			Logger.log(new InformationHighEvent("Surviving black ants: "
				+ survivors[0] + "/" + blackAnts));
		}
		if(survivors.length > 1){
			int redAnts = antsBySpecies[1].length;
			Logger.log(new InformationHighEvent("Surviving red   ants: "
				+ survivors[1] + "/" + redAnts  ));
		}
		
		//Food results
		int[] anthillFood = world.getFoodInAnthills();
		if(anthillFood.length > 0){
			Logger.log(new InformationHighEvent("Food in black anthill: "
				+ anthillFood[0]));
		}
		if(anthillFood.length > 1){
			Logger.log(new InformationHighEvent("Food in red   anthill: "
				+ anthillFood[1]));
		}
		
		if(blackBrain.getFitness() > redBrain.getFitness()) return blackBrain;
		if(redBrain.getFitness() > blackBrain.getFitness()) return redBrain;
		return null;
	}
	
	public synchronized int[][] runContest(Brain[] teams)
    {
    	//Phil: okay, so you want to play every brain against every other brain and store all the winning indexes.
    	//You can't reuse Worlds, need a new one for every simulation
    	//Why do you need 2 loops?
    	//You were using the field world in calculateWinner, and using a local variable here, so that would have failed
    	
    	int[][] results = new int[teams.length][teams.length];

    	for(int i=0; i<teams.length; i++)
    	{
    		for(int j=0; j<teams.length; j++)
    		{
    			if(j == i) j++;
    			if(j >= teams.length) break;
    			
    			World world = World.getContestWorld(1);
    			simulate(teams[i], teams[j], world);

    			if(calculateWinner(world) == 0)
    			{
    				//black wins
    				results[i][j] = i;
    				results[j][i] = i;
    			} else if(calculateWinner(world) == 1 ) {
    				//red wins
    				results[i][j] = j;
    				results[j][i] = j;
    			} else {
    				results[i][j] = -1;
    				results[j][i] = i;
    			}
    		}
    	}
    	
    	return results;
    }
    	
    /**
     * In the dummy engine already on here this method returned a brain - not sure if the Genetic Alg
     * still works but not sure how easy it is to determine from a brain which team has won?
     * 
     * At present it returns 0 for black, 1 for red, and 2 for the draw
     */
	public int calculateWinner(World world)
   	{
	  	int[] anthillFood = world.getFoodInAnthills();
	            
	  	if(anthillFood[0] > anthillFood[1])
		{
	  		return 0;
	 	}
	  	else if(anthillFood[1] > anthillFood[0])
	 	{
	  		return 1;
	  	}
	  	return 2;
	    //play sound effect
    }
    	
    /**
     * just a simple string array... what other stats can we add?
     */  
	public String[] showStatistics(World world)
	{
		int[] survivors = (world).survivingAntsBySpecies();
    	int[] food = (world).getFoodInAnthills();
    	String[] stats = new String[4];
    	stats[0] = "The black team finished with " + survivors[0] + " living ants";
    	stats[1] = "The red team finished with " + survivors[1] + " living ants";
    	stats[2] = "The black team collected " + food[0] + "units of food";
    	stats[3] = "The red team collected " + food[1] + "units of food";
    	        
    	return stats;  
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.clearLogs();
//		GeneticAlgorithm.clearSaves();
		Logger.setLogLevel(Logger.LogLevel.NORM_LOGGING);
		
		//Evolve and get the best brain from the GeneticAlgorithm
		//trainingBrain is a decent place to start from
		//but more likely to get stuck there in the optima,
		//blankBrain is a worse starting point, it would take longer to get to a good brain,
		//but it encourages the brains generated to be more random
		Brain trainingBrain = BrainParser.readBrainFrom("better_example");
		GameEngine dummyEngine = new GameEngine(140, 140, 13, 2, 7, 10, 5, 5, 0, 1,
			Integer.MAX_VALUE, 300000, 50, 50 / 10, 100, 50);
		
//		//World(char[][]) test:
//		World world = World.getContestWorld(0);
//		WorldParser.writeWorldTo(world, "test");
//		world = WorldParser.readWorldFrom("test");
//		System.out.println(world);
//		System.out.println(world.getAttributes());
		
		Brain gaBrain = dummyEngine.getBestGABrain(trainingBrain, trainingBrain, 1);
//		Brain gaBrain = BrainParser.readBrainFrom("ga_result_full");
		
		//Compact and remove null and unreachable states
		trainingBrain.trim();
		gaBrain.trim();
		
		dummyEngine.simulate(trainingBrain, gaBrain, dummyEngine.generateWorld(0));
		
		Logger.log(new InformationHighEvent("Virtual Machine terminated normally"));
	}
}
