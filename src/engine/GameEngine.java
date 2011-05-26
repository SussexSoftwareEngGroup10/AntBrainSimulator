package engine;

import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utilities.ErrorEvent;
import utilities.Event;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.BrainParser;
import antBrain.GeneticAlgorithm;
import antWorld.Ant;
import antWorld.Simulation;
import antWorld.World;
import antWorld.WorldParser;

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
	private static final int rounds = 300000;
	private static final int processors = Runtime.getRuntime().availableProcessors();
	private int sleepDur = 500;
	private Brain absoluteTrainingBrain;
	private Brain relativeTrainingBrain;
	private ThreadPoolExecutor threadPoolExecutor;
	private Semaphore semaphore;
	private Brain[] population;
	private int count1 = 0;
	private int count2 = 0;
	
	/**
	 * 
	 */
	public GameEngine() {
		Logger.log(new InformationLowEvent("New GameEngine object constructed"));
	}
	
	/**
	 * @return
	 */
	public long getSleepDur() {
		return this.sleepDur;
	}
	
	/**
	 * @param sleepDur
	 */
	public void setSpeed(int sleepDur) {
		this.sleepDur = sleepDur;
	}
	
	/**
	 * @param x should be between 0 and 1000, but is not checked
	 * @return a value between 1 and 1000 proportional to 2^x
	 */
	public static int expScale(int x) {
		return ((int) Math.pow(2, (x * log2(1000) / 1000)));
	}
	
	/**
	 * @param x
	 * @return log base 2 of x
	 */
	public static double log2(int x) {
		return Math.log(x)/Math.log(2);
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
	
//	/*
//	 * Simulates each Brain against each other Brain in population,
//	 * sets their fitness to the number of wins they get,
//	 * then orders the population by fitness, so the most wins is at population[length - 1]
//	 * @param absoluteTrainingBrain
//	 */
//	public void contestSetup(Brain[] population, Brain absoluteTrainingBrain) {
//		this.threadPoolExecutor = new ThreadPoolExecutor(
//=======
//	private void runContest() {
//		evaluateFitnessContest(false, contestBrains, null);
//	}
	
	public void contestSetup(Brain[] population) {
		for(Brain brain : population){
			brain.resetFitnesses();
		}
		fitnessContestSetup(population, null);
	}
	
	/**
	 * call once at the start of each contest
	 * @param threadPoolExecutor
	 * @param semaphore
	 * @param useFitness
	 * @param population
	 */
	public void fitnessContestSetup(Brain[] population, Brain absoluteTrainingBrain) {
		this.population = population;
		this.absoluteTrainingBrain = absoluteTrainingBrain;
		
		this.threadPoolExecutor = new ThreadPoolExecutor(
			GameEngine.processors, GameEngine.processors, 1, TimeUnit.NANOSECONDS,
			new ArrayBlockingQueue<Runnable>(2));
		this.semaphore = new Semaphore(2, true);
		
		//Find Brain in elite with highest fitness
		int index = population.length - 1;
		int i;
		for(i = population.length - 2; i >= 0; i--){
			if(population[i].getFitness() > population[index].getFitness()){
				index = i;
			}
		}
		if(population[index].getFitness() == 0){
			//Either no elite or first epoch, so use absoluteTrainingBrain
			this.relativeTrainingBrain = absoluteTrainingBrain;
		}else{
			this.relativeTrainingBrain = population[index];
		}
		this.count1 = 0;
	}
	
	/**
	 * worlds.size() == 4 (call pop.len times)
	 * @param worlds
	 */
	public void fitnessContestStep(Stack<World> worlds) {
		//Multi-Threaded
		//Set fitness for every brain in population
		Brain brain = this.population[this.count1];
		
		if(brain.getFitness() == 0){
			//Brain is not in elite
			//Absolute fitness tests
			this.semaphore.acquireUninterruptibly(2);
			this.threadPoolExecutor.execute(
				new Simulation(this, this.absoluteTrainingBrain, brain,
					this.semaphore, 0, true, GameEngine.rounds, worlds.pop()));
			this.threadPoolExecutor.execute(
				new Simulation(this, brain, this.absoluteTrainingBrain,
					this.semaphore, 1, true, GameEngine.rounds, worlds.pop()));
			//Await completion of Simulations
			this.semaphore.acquireUninterruptibly(2);
			this.semaphore.release(2);
		}
		
		//Relative fitness tests
		this.semaphore.acquireUninterruptibly(2);
		this.threadPoolExecutor.execute(
			new Simulation(this, this.relativeTrainingBrain, brain,
				this.semaphore, 2, true, GameEngine.rounds, worlds.pop()));
		this.threadPoolExecutor.execute(
			new Simulation(this, brain, this.relativeTrainingBrain,
				this.semaphore, 3, true, GameEngine.rounds, worlds.pop()));
		//Await completion of Simulations
		this.semaphore.acquireUninterruptibly(2);
		this.semaphore.release(2);
		
		//increment count
		this.count1++;
	}
	
	/**
	 * automatically runs entire contest, cannot pass worlds
	 */
	public void contestStepAll() {
		//Multi-Threaded
		//Get popLen permits, restore as runs complete
		Stack<World> worlds = new Stack<World>();
		World world;
		try {
			world = World.getContestWorld(1);
		} catch (ErrorEvent e) {
			Logger.log(e);
			return;
		}
		for(int j = this.population.length - 1; j >= 0; j--){
			for(int k = this.population.length - 1; k >= 0; k--){
				while(worlds.size() < 2){
					worlds.push((World) world.clone());
				}
				
				contestStep(worlds);
			}
		}
	}
	
	/**
	 * worlds.size() == 2 (call method pop.len^2 times)
	 * @param worlds
	 */
	public void contestStep(Stack<World> worlds) {
		//Multi-Threaded
		//Get popLen permits, restore as runs complete
		if(this.count1 == this.count2){
			//increment count
			this.count2++;
			if(this.count2 >= this.population.length){
				this.count1++;
				this.count2 = 0;
			}
			return;
		}
		
		this.semaphore.acquireUninterruptibly(2);
		this.threadPoolExecutor.execute(
			new Simulation(this, this.population[this.count1], this.population[this.count2],
				this.semaphore, 0, false, GameEngine.rounds, worlds.pop()));
		this.threadPoolExecutor.execute(
			new Simulation(this, this.population[this.count2], this.population[this.count1],
				this.semaphore, 0, false, GameEngine.rounds, worlds.pop()));
		//Await completion of Simulations
		this.semaphore.acquireUninterruptibly(2);
		this.semaphore.release(2);

		//increment count
		this.count2++;
		if(this.count2 >= this.population.length){
			this.count1++;
			this.count2 = 0;
		}
	}
	
	/**
	 * Runs a standard simulation.
	 * @param blackBrain
	 * @param redBrain
	 * @param world
	 * @return
	 */
	public GameStats simulate(Brain blackBrain, Brain redBrain, World world) {
		//Setup brains
		world.setBrain(blackBrain, 0);
		world.setBrain(redBrain, 1);
		
		//Run the simulation, test the Brain result from the GA against bestBrain
		Logger.log(new InformationLowEvent("Begun simulation"));
		
		//Runs in serial
		new Simulation(this, blackBrain, redBrain, null,
			this.sleepDur, false, GameEngine.rounds, world).run();
		
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
		
		//Create and return statistics based on winner
		if(blackBrain.getFitness() > redBrain.getFitness()) {
			return new GameStats(0, anthillFood[0], anthillFood[1],
				survivors[0], survivors[1]);
		}
		return new GameStats(1, anthillFood[0], anthillFood[1],
			survivors[0], survivors[1]);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			World world = WorldParser.readWorldFromContest("seed_1 - Copy");
			System.out.println(world + "\n" + world.getAttributes());
		} catch (Event e) {
			System.out.println(e);
		}
//		//TODO combine GA and regular sim methods
//		//TODO make sure 2 evolve()s can be run using 1 GeneticAlgorithm and DummyEngine
//		//TODO number of states in GeneticAlgorithm.breed(), allow removal of states
//			//or at least allow a numOfStates parameter
//		//TODO remove polling in Ant.step()
//		//TODO use jar on linux server
//		//TODO javac -O, java -prof, JIT
//		//TODO separate parts of contest method so no memory error
//		
//		Logger.clearLogs();
////		GeneticAlgorithm.clearSaves();
//		Logger.setLogLevel(Logger.LogLevel.NORM_LOGGING);
//		
//		//Evolve and get the best brain from the GeneticAlgorithm
//		//trainingBrain is a decent place to start from
//		//but more likely to get stuck there in the optima,
//		//blankBrain is a worse starting point, it would take longer to get to a good brain,
//		//but it encourages the brains generated to be more random
//		Brain trainingBrain = null;
//		try{
//			trainingBrain = BrainParser.readBrainFrom("better_example");
//		}catch(IOEvent e){
//			Logger.log(e);
//			return;
//		} catch (IllegalArgumentEvent e) {
//			Logger.log(e);
//			return;
//		}
//		GameEngine gameEngine = new GameEngine();
//		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
//		
////		//World(char[][]) test:
////		World world = World.getContestWorld(0);
////		WorldParser.writeWorldTo(world, "test");
////		world = WorldParser.readWorldFrom("test");
////		System.out.println(world);
////		System.out.println(world.getAttributes());
//		
//		Brain gaBrain = geneticAlgorithm.getBestBrain(gameEngine, trainingBrain, trainingBrain, 
//			Integer.MAX_VALUE, 50, 50/10, 100);
////		Brain gaBrain = BrainParser.readBrainFrom("ga_result_full");
//		
//		//Compact and remove null and unreachable states
//		try {
//			trainingBrain.trim();
//		} catch (IllegalArgumentEvent e) {
//			Logger.log(e);
//		}
//		try {
//			gaBrain.trim();
//		} catch (IllegalArgumentEvent e) {
//			Logger.log(e);
//		}
//		
//		try {
//			gameEngine.simulate(trainingBrain, gaBrain, World.getContestWorld(1));
//		} catch (ErrorEvent e) {
//			Logger.log(e);
//		}
//		
		Logger.log(new InformationHighEvent("Virtual Machine terminated normally"));
	}
}
