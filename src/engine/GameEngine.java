package engine;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.InformationNormEvent;
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
	private static final int rounds = 300000;
	private static final int processors = Runtime.getRuntime().availableProcessors();
	private World world;
	private int sleepDur;
	
	/**
	 * @param world
	 */
	public GameEngine(World world) {
		setWorld(world);
		this.sleepDur = 500;
		Logger.log(new InformationLowEvent("New Engine object constructed"));
	}
	
	/**
	 * @param world
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * 
	 */
	public void slowDown() {
		this.sleepDur = Math.min(this.sleepDur + 50, 1000);
	}

	/**
	 * 
	 */
	public void speedUp() {
		this.sleepDur = Math.max(this.sleepDur - 50, 0);
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
	 * Simulates each Brain against each other Brain in population,
	 * sets their fitness to the number of wins they get,
	 * then orders the population by fitness, so the most wins is at population[length - 1]
	 * @param population
	 */
	public void contest(Brain[] population) {
		evaluateFitnessContest(false, population, null);
	}
	
	/**
	 * @param threadPoolExecutor
	 * @param semaphore
	 * @param useFitness
	 * @param population
	 */
	public void evaluateFitnessContest(boolean useFitness, Brain[] population,
		Brain absoluteTrainingBrain) {
		int sims = 0;
		if(useFitness) {
			sims = population.length * 4;
		}else{
			sims = population.length * population.length * 2;
		}
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			GameEngine.processors, GameEngine.processors, 1, TimeUnit.NANOSECONDS,
			new ArrayBlockingQueue<Runnable>(sims));
		Semaphore semaphore = new Semaphore(sims, true);
		
		//Find Brain in elite with highest fitness
		int index = population.length - 1;
		int i;
		for(i = population.length - 2; i >= 0; i--){
			if(population[i].getFitness() > population[index].getFitness()){
				index = i;
			}
		}
		Brain relativeTrainingBrain;
		if(population[index].getFitness() == 0){
			//Either no elite or first epoch, so use absoluteTrainingBrain
			relativeTrainingBrain = absoluteTrainingBrain;
		}else{
			relativeTrainingBrain = population[index];
		}
		
		//Give priority to the Simulation threads, so, in theory, 2 exist at any one time,
		//when one finished, another one is started by the main thread,
		//this is far more efficient in terms of memory
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		//Multi-Threaded
		//Get popLen permits, restore as runs complete
		if(useFitness){
			semaphore.acquireUninterruptibly(population.length * 4);
			//Set fitness for every brain in population
			for(Brain brain : population){
				if(brain.getFitness() == 0){
					//Brain is not in elite
					//Absolute fitness tests
					threadPoolExecutor.execute(
						new Simulation(absoluteTrainingBrain, brain,
							semaphore, 0, 0, true, GameEngine.rounds, (World) this.world.clone()));
					threadPoolExecutor.execute(
						new Simulation(brain, absoluteTrainingBrain,
							semaphore, 0, 1, true, GameEngine.rounds, (World) this.world.clone()));
				}else{
					semaphore.release(2);
				}
				//Relative fitness tests
				threadPoolExecutor.execute(
					new Simulation(relativeTrainingBrain, brain,
						semaphore, 0, 2, true, GameEngine.rounds, (World) this.world.clone()));
				threadPoolExecutor.execute(
					new Simulation(brain, relativeTrainingBrain,
						semaphore, 0, 3, true, GameEngine.rounds, (World) this.world.clone()));
			}
			//Await completion of all Simulations
			semaphore.acquireUninterruptibly(population.length * 4);
			semaphore.release(population.length * 4);
		}else{
			semaphore.acquireUninterruptibly(population.length * population.length * 2);
			for(int j = population.length - 1; j >= 0; j--){
				for(int k = population.length - 1; k >= 0; k--){
					if(j == k){
						semaphore.release(2);
						continue;
					}
					threadPoolExecutor.execute(
						new Simulation(population[j], population[k],
							semaphore, 0, 0, false, GameEngine.rounds, (World) this.world.clone()));
					threadPoolExecutor.execute(
						new Simulation(population[k], population[j],
							semaphore, 0, 0, false, GameEngine.rounds, (World) this.world.clone()));
				}
			}
			//Await completion of all Simulations
			semaphore.acquireUninterruptibly(population.length * population.length * 2);
			semaphore.release(population.length * population.length * 2);
		}
		
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		Arrays.sort(population);
		
		//Log fitness stats
		index = population.length - 1;
		for(i = population.length - 2; i >= 0; i--){
			if(population[i].getFitness() > population[index].getFitness()){
				index = i;
			}
		}
		int maxFitness = population[index].getFitness();
		
		int total = 0;
		for(i = population.length - 1; i >= 0; i--){
			total += population[i].getFitness();
		}
		int avgFitness =  total / population.length;

		index = population.length - 1;
		for(i = population.length - 2; i >= 0; i--){
			if(population[i].getFitness() < population[index].getFitness()){
				index = i;
			}
		}
		int minFitness = population[index].getFitness();
		
		Logger.log(new InformationNormEvent("Fitnesses: max: " + maxFitness
			+ ";  avg: " + avgFitness + ";  min: " + minFitness));
	}
	
	/**
	 * @param blackBrain
	 * @param redBrain
	 * @param world
	 * @return
	 */
	public Brain simulate(Brain blackBrain, Brain redBrain) {
		World world = (World) this.world.clone();
		//Setup brains
		world.setBrain(blackBrain, 0);
		world.setBrain(redBrain, 1);
		
		//Run the simulation, test the Brain result from the GA against bestBrain
		Logger.log(new InformationLowEvent("Begun simulation"));
		
		//Runs in serial
		new Simulation(blackBrain, redBrain, null,
			this.sleepDur, 0, false, GameEngine.rounds, world).run();
		
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO combine GA and regular sim methods
		//TODO make sure 2 evolve()s can be run using 1 GeneticAlgorithm and DummyEngine
		//TODO number of states in GeneticAlgorithm.breed(), allow removal of states
			//or at least allow a numOfStates parameter
		//TODO remove polling in Ant.step()
		//TODO use jar on linux server
		//TODO javac -O, java -prof, JIT
		
		Logger.clearLogs();
//		GeneticAlgorithm.clearSaves();
		Logger.setLogLevel(Logger.LogLevel.NORM_LOGGING);
		
		//Evolve and get the best brain from the GeneticAlgorithm
		//trainingBrain is a decent place to start from
		//but more likely to get stuck there in the optima,
		//blankBrain is a worse starting point, it would take longer to get to a good brain,
		//but it encourages the brains generated to be more random
		Brain trainingBrain = BrainParser.readBrainFrom("better_example");
		GameEngine gameEngine = new GameEngine(World.getContestWorld(1));
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		
//		//World(char[][]) test:
//		World world = World.getContestWorld(0);
//		WorldParser.writeWorldTo(world, "test");
//		world = WorldParser.readWorldFrom("test");
//		System.out.println(world);
//		System.out.println(world.getAttributes());
		
		Brain gaBrain = geneticAlgorithm.getBestBrain(gameEngine, trainingBrain, trainingBrain, 
			Integer.MAX_VALUE, 50, 50/10, 50);
//		Brain gaBrain = BrainParser.readBrainFrom("ga_result_full");
		
		//Compact and remove null and unreachable states
		trainingBrain.trim();
		gaBrain.trim();
		
		gameEngine.simulate(trainingBrain, gaBrain);
		
		Logger.log(new InformationHighEvent("Virtual Machine terminated normally"));
	}
}
