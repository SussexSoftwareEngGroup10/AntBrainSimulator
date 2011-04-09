package engine;

import java.util.ArrayList;
import java.util.Collections;

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
	private int tourneySeed = 1;
	private World tourneyWorld = WorldController.getTournamentWorld(tourneySeed);
	private Brain tourneyBrain = BrainController.readBrainFrom("better_example.brain");
	
	public DummyEngine() {
		if(Logger.getLogLevel() >= 1){
			Logger.log(new InformationEvent("New Engine object constructed"));
		}
	}
	
	public void sortByFitness(ArrayList<Brain> population) {
		//Dummy method that shows the GeneticAlgorithm works,
		//populations is sorted by number of states in each brain, most first
//		Collections.sort(population);
		
		//either
		//run a tournament and fight every brain against every other brain
		//or
		//fight each brain against the default brain a number of times,
		//possibly on a seeded world
		//
		//then rank according to wins, most first
		//don't use the Collections.sort method at all, this is a dummy
		
		tournament(population);
	}
	
	private void tournament(ArrayList<Brain> population) {
		int popSize = population.size();
		Brain brain;
		int i = 0;
		
		for(i = 0; i < popSize; i++){
			brain = population.get(i);
			brain.setScore(tourneySimulation(brain));
		}
		
		Collections.sort(population);
	}
	
	private int tourneySimulation(Brain brain) {
		World world = tourneyWorld.clone();
		world.setBrain(tourneyBrain, 0);
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
	
	@SuppressWarnings("unused")
	private void randomTournament(ArrayList<Brain> population) {
		int popSize = population.size();
		int[] scores = new int[popSize];
		int i = 0;
		int j = 0;
		
		//Sum the wins for each brain against every other brain
		for(i = 0; i < popSize; i++){
			for(j = 0; j < popSize; j++){
				if(i != j){
					Brain[] brains = {population.get(i), population.get(j)};
					try{
						scores[simulation(brains)]++;
					}catch(ArrayIndexOutOfBoundsException e){
						//simulation was a draw
					}
				}
			}
		}
		//Rank brains according to wins, descending
		for(i = 0; i < popSize; i++){
			population.get(i).setScore(scores[i]);
		}
		Collections.sort(population);
	}
	
	private int simulation(Brain[] brains) {
		World world = WorldController.getTournamentWorld(0);
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
		
//		ArrayList<ArrayList<Ant>> antPlayers = world.getAntsBySpecies();
//		int[] survivors = world.survivingAntsBySpecies();

		int[] anthillFood = world.getFoodInAnthills();
		if(anthillFood[0] > anthillFood[1]){
			return 0;
		}
		if(anthillFood[1] > anthillFood[0]){
			return 1;
		}
		return -1;
	}
	
	public static void main(String args[]) {
		long startTime = System.currentTimeMillis();
		Logger.clearLogs();
		Logger.setLogLevel(3);
		
		//Setup brains
		//Evolve and get the best brain from the GeneticAlgorithm
		Brain[] brains = new Brain[2];
		DummyEngine dummyEngine = new DummyEngine();
		int epochs = 1;
		int popSize = 5;
		int mutationRate = 20;
		
		//Black is the best one found by the GeneticAlgorithm with parameters specified
		//Red is default brain, read in from file
		//Black should win when sortByFitness is done
		Brain exampleBrain = BrainController.readBrainFrom("example.brain");
		Brain betterBrain = BrainController.readBrainFrom("better_example.brain");
		Brain gaBrain = BrainController.getBestGABrain(exampleBrain.clone(), dummyEngine, epochs, popSize, mutationRate);
		brains[0] = gaBrain; //black
		brains[1] = betterBrain;  //red
		
		
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
		world.setBrains(brains);
		ArrayList<Ant> ants = world.getAnts();
		
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
		
		System.out.println(world);
		BrainParser.writeBrainTo(gaBrain, "my.brain");
		System.out.println(gaBrain);
		
		if(Logger.getLogLevel() >= 1){
			Logger.log(new InformationEvent("Virtual Machine terminated, " +
				"total execution time: " + (System.currentTimeMillis() - startTime) + "ms"));
		}
	}
}
