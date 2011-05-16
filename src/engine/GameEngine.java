package engine;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.Logger;
import utilities.WarningEvent;

import antBrain.Brain;
import antBrain.BrainParser;
import antBrain.GeneticAlgorithm;
import antBrain.Simulation;
import antWorld.Ant;
import antWorld.Cell;
import antWorld.World;

public class GameEngine
{
    private World world;
    private Brain redBrain;
    private Brain blackBrain;
//    private SoundEffect winningSoundEffect;
    private int waitTime;
    
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
    private int round;
    
    //GA variables
	private static final int cpus = Runtime.getRuntime().availableProcessors();
	private Brain absoluteTrainingBrain;
	
    /**
     * general constructor
     */
    public GameEngine(String blackBrainLocation, String redBrainLocation)
    {
        seed = 5;
        rows = 5;
        cols = 5;
        rocks = 5;
        waitTime = 50;
        
//        BrainParser brainParser = new BrainParser();
        Brain blackBrain = BrainParser.readBrainFrom(blackBrainLocation);
        Brain redBrain = BrainParser.readBrainFrom(redBrainLocation);
//        world = new World(seed,rows,cols,rocks);
        world = World.getContestWorld(0);
        
        world.setBrain(blackBrain,0);
        world.setBrain(redBrain,1);

        run(300000);
    }
    
    /**
     * 
     * Constructor for contests
     * 
     */
    public GameEngine(Brain blackBrain, Brain redBrain, World world)
    {
    	//Phil: if this is the contest constructor, why are you only passing it 2 brains?
        this.world = world;
        this.redBrain = redBrain;
        this.blackBrain = blackBrain;
        
        this.world.setBrain(blackBrain,0);
        this.world.setBrain(redBrain,1);

        run(300000);
    }

    public void run(int epochs)
    {
        Ant[] ants = (world).getAnts();
        
        for(int epoch = 0 ; epoch < epochs; epoch++)
        {
            for(int ant = 0; ant < ants.length; ant++)
            {
            	//Phil: isSurrounded and kill are built in to the step method in Ant
//                if(ants[ant].isSurrounded())
//                {
//                    ants[ant].kill();
//                }else{
                    ants[ant].step();
//                }
            }
            try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				Logger.log(new WarningEvent(e.getMessage(), e));
			}
        }
        calculateWinner();
    }

    /**  
     * set max and min as 100 and 0 but have no idea what a good speed would be until the GUI is set up
     */
    public void slowDown()
    {
        if(waitTime != 100) { waitTime = waitTime + 10; }
    }
    
    public void speedUp()
    {
        if(waitTime != 0) { waitTime = waitTime - 10; }
    }
    
    /**
     * In the dummy engine already on here this method returned a brain - not sure if the Genetic Alg
     * still works but not sure how easy it is to determine from a brain which team has won?
     * 
     * At present it returns 0 for black, 1 for red, and 2 for the draw
     * 
     */
    public int calculateWinner()
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
    
    public String[] showStatistics()
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
     * 
     * At present just have this method so it returns a 2d array of ints. I think I have the logic right
     * but correct me if I am wrong - each time plays it other team - once on each world?
     * 
     * At present, it stores in the cellreference [black][red] the index of the winning team or "-1"
     * for a draw.
     * 
     */
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
    			
    			world = World.getContestWorld(1);
    			GameEngine ge = new GameEngine(teams[i], teams[j], world);
    			if(ge.calculateWinner() == 0)
    			{
    				//black wins
    				results[i][j] = i;
    				results[j][i] = i;
    			} else if(ge.calculateWinner() == 1 ) {
    				//red wins
    				results[i][j] = j;
    				results[j][i] = j;
    			} else {
    				results[i][j] = -1;
    				results[j][i] = i;
    			}
    		}
    	}

//    	for(int i=teams.length; i>=0; i--)
//    	{
//    		for(int j=i - 1; j>=0; j--)
//    		{
//    			World newWorld = World.getContestWorld(0);
//    			GameEngine ge = new GameEngine(teams[i], teams[j], newWorld);
//    			if(ge.calculateWinner(newWorld) == 0)
//    			{
//    				//black wins
//    				results[i][j] = i;
//    			} else if(ge.calculateWinner(newWorld) == 1 ) {
//    				//red wins
//    				results[i][j] = j;
//    			} else {
//    				results[i][j] = -1;
//    			}
//    		}
//    	}

        return results;
    }
    
    /**
     * Enables the GUI to retrieve a copy of the cells in the world.
     * 
     * @return The cells making up the world.
     */
    public Cell[][] getCells() {
    	return world.getWorld();
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
	
//    /**
//	 * @param startBrain
//	 * @param trainingBrain
//	 * @param seed
//	 * @return
//	 */
//	public Brain getBestGABrain(Brain startBrain, Brain trainingBrain, int seed) {
//		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(cpus, cpus, 1,
//			TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(this.popLen * 4));
//		Semaphore semaphore = new Semaphore(this.popLen * 4, true);
//		
//		this.absoluteTrainingBrain = trainingBrain;
//		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
//		geneticAlgorithm.createPopulation(startBrain, this.popLen);
//		geneticAlgorithm.evolve(this, seed, threadPoolExecutor, semaphore,
//			this.epochs, this.rounds, this.elite, this.mutationRate);
//		return geneticAlgorithm.getBestBrain();
//	}
//	
//	/**
//	 * @param seed
//	 * @param threadPoolExecutor
//	 * @param semaphore
//	 * @param population
//	 */
//	public void sortByFitness(int seed, ThreadPoolExecutor threadPoolExecutor,
//		Semaphore semaphore, Brain[] population) {
//		//Ensure all Brains have a fitness
//		evaluateFitnessContest(seed, threadPoolExecutor, semaphore, population);
//		
//		//Sort by fitnesses calculated
//		Arrays.sort(population);
//	}
//	
//	/**
//	 * @param seed
//	 * @param threadPoolExecutor
//	 * @param semaphore
//	 * @param population
//	 */
//	private void evaluateFitnessContest(int seed, ThreadPoolExecutor threadPoolExecutor,
//		Semaphore semaphore, Brain[] population) {
//		//Ants let each other know they've finished a step with the stepBarrier
//		//Ants let their sim know they've finished all steps with the endBarrier
//		//Sims let the engine know they've finished their sim with the contestEndBarrier
//		
//		//Set fitness for each brain against the best brain in the population
//		//Assumes population has been sorted
//		//Get the brain in the population with the highest fitness, if any have one
//		int i = population.length;
//		Brain relativeTrainingBrain;
//		do{
//			if(i <= 0){
//				//If there is no elite, or this is the first epoch,
//				//no brains will have a fitness,
//				//In these cases, the static Brain test must be used instead
//				relativeTrainingBrain = this.absoluteTrainingBrain;
//				break;
//			}
//			i--;
//			relativeTrainingBrain = population[i];
//		}while(relativeTrainingBrain.getFitness() == 0);
//		
//		//Multi-Threaded
//		//Get popLen permits, restore as runs complete
//		semaphore.acquireUninterruptibly(population.length * 4);
//		
//		//Set fitness for every brain in population
//		for(Brain brain : population){
//			if(brain.getFitness() == 0){
//				//Brain is not in elite
//				//Absolute fitness tests
//				threadPoolExecutor.execute(
//					new Simulation(this, this.absoluteTrainingBrain, brain,
//						semaphore, 0, 0, this.rounds, seed));
//				threadPoolExecutor.execute(
//					new Simulation(this, brain, this.absoluteTrainingBrain,
//						semaphore, 0, 1, this.rounds, seed));
//			}else{
//				semaphore.release(2);
//			}
//			//Relative fitness tests
//			threadPoolExecutor.execute(
//				new Simulation(this, relativeTrainingBrain, brain,
//					semaphore, 0, 2, this.rounds, seed));
//			threadPoolExecutor.execute(
//				new Simulation(this, brain, relativeTrainingBrain,
//					semaphore, 0, 3, this.rounds, seed));
//		}
//		//Await completion of all Simulations
//		semaphore.acquireUninterruptibly(population.length * 4);
//		semaphore.release(population.length * 4);
//	}
}
