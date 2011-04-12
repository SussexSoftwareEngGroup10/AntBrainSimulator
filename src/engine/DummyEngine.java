package engine;

import java.util.ArrayList;
import java.util.Arrays;

import utilities.InformationEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.BrainController;
import antBrain.BrainParser;
import antWorld.Ant;
import antWorld.World;
import antWorld.WorldController;

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
	public static final long startTime = System.currentTimeMillis();
	private static final int tourneySeed = 1;
	private static final World tourneyWorld = WorldController.getTournamentWorld(tourneySeed);
	
	public DummyEngine() {
		if(Logger.getLogLevel() >= 1){
			Logger.log(new InformationEvent("New Engine object constructed"));
		}
	}
	
	public void sortByFitness(Brain[] population) {
		tournament(population);
	}
	
	private void tournament(Brain[] population) {
		Brain brain;
		int i = 0;
		
		//Set fitness for each brain against the best brain in the population
		//Assumes population has been sorted
		Brain bestBrain = population[population.length - 1];
		for(i = 0; i < population.length; i++){
			brain = population[i];
			//Brains from previous tournaments may remain in the elite
			//their fitness does not need to be calculated again
			if(brain.getFitness() == 0){
				brain.setFitness(tourneySimulation(bestBrain, brain));
			}
		}
		
		Arrays.sort(population);
	}
	
	private int tourneySimulation(Brain bestBrain, Brain brain) {
		World world = tourneyWorld.clone();
		world.setBrain(bestBrain, 0);
		world.setBrain(brain, 1);
		//World now has better brain at 0, GA brain at 1
		
		ArrayList<Ant> ants = world.getAnts();
		//Run the simulation
		int r = 0;
		int rounds = 300000;
		if(Logger.getLogLevel() >= 5){
			Logger.log(new InformationEvent("Begun simulation"));
		}
		for(r = 0; r < rounds; r++){
			for(Ant ant : ants){
				if(ant.isAlive()){
					if(ant.isSurrounded()){
						ant.kill();
					}else{
						ant.step();
					}
				}
			}
		}
		//Fitness of the GA brain = its food - opponent's food
		int[] anthillFood = world.getFoodInAnthills();
		return anthillFood[1] - anthillFood[0];
	}
	
	public static void main(String args[]) {
		Logger.clearLogs();
		Logger.setLogLevel(3);
		
		//Setup world
		World world;
		//This creates a random seed, rather than a fixed seed world
		//seed is also used to determine ant moves,
		//so exactly the same simulation can be replayed
		//could use a seeded world for every GA game,
		//(possibly) fairer and quicker, but less random, evolution
		//more efficient to test all GA population brains against
		//the betterBrain with seed == 1
		int seed = 0;
		
		//World variables
//		int rows = 140;
//		int cols = 140;
//		int rocks = 30;
//		int anthills = 2;
//		int anthillSideLength = 7;
//		int foodBlobCount = 15;
//		int foodBlobSideLength = 3;
//		int foodBlobCellFoodCount = 9;
//		int antInitialDirection = 0;
//		world = WorldController.getWorld(rows, cols, rocks, brains, seed,
//			anthills, anthillSideLength, foodBlobCount, foodBlobSideLength,
//			foodBlobCellFoodCount, antInitialDirection);
		world = WorldController.getTournamentWorld(seed);
//		world = WorldController.readWorldFrom(brains, "example.world");
		
		//Setup brains
		//Evolve and get the best brain from the GeneticAlgorithm
		DummyEngine dummyEngine = new DummyEngine();
		int epochs = 1000;
		int popSize = 100;
		int mutationRate = 10;
		
		//Black is the best one found by the GeneticAlgorithm with parameters specified
		//Red is default brain, read in from file
		//Black should win when sortByFitness is done
//		Brain exampleBrain = BrainController.readBrainFrom("example.brain");
		Brain betterBrain = BrainController.readBrainFrom("better_example.brain");
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Time to GA start: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
		Brain gaBrain = BrainController.getBestGABrain(betterBrain.clone(), dummyEngine, epochs, popSize, mutationRate);
//		Brain gaBrain = BrainController.readBrainFrom("ga.brain");
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Time to GA end: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
		world.setBrain(betterBrain, 0);	//black
		world.setBrain(gaBrain, 1);		//red
		
		ArrayList<Ant> ants = world.getAnts();
		
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Time to simulation start: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
		//Run the simulation
		int r = 0;
		int rounds = 300000;
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Begun simulation"));
		}
		for(r = 0; r < rounds; r++){
			for(Ant ant : ants){
				if(ant.isAlive()){
					if(ant.isSurrounded()){
						ant.kill();
					}else{
						ant.step();
					}
				}
			}
		}
		
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Time to simulation end: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
		
		if(Logger.getLogLevel() >= 3){
			ArrayList<ArrayList<Ant>> antPlayers = world.getAntsBySpecies();
			int[] survivors = world.survivingAntsBySpecies();
			if(survivors.length > 0){
				int blackAnts = antPlayers.get(0).size();
				Logger.log(new InformationEvent("Surviving black ants: " + survivors[0] + "/" + blackAnts));
			}
			if(survivors.length > 1){
				int redAnts = antPlayers.get(1).size();
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
		
		//TODO remove console prints, eventually
		System.out.println(world);
		BrainParser.writeBrainTo(gaBrain, "my.brain");
		System.out.println("---Better Brain---\n" + betterBrain);
		System.out.println("---GA Brain---\n" + gaBrain);
		
		if(Logger.getLogLevel() >= 1){
			Logger.log(new InformationEvent("Virtual Machine terminated, " +
				"total execution time: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
	}
	
	public World generateWorld() {
		//TODO: Implement (in another class).
		return null;
	}
}
