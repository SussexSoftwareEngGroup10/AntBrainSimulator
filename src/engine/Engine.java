package engine;

import java.util.ArrayList;
import java.util.Collections;

import antBrain.Brain;
import antBrain.BrainController;
import antWorld.Ant;
import antWorld.World;
import antWorld.WorldController;

/**
 * Dummy Engine class
 * 
 * sortByFitness is needed by the GA,
 * it must order the population by how good they are at winning games, best first
 * 
 * the main method is just a dummy version of what the engine might do,
 * used to test antBrain and antWorld classes
 * 
 * To work out get the winner in a game, call world.getFoodInAnthills()
 * or world.survivingAntsBySpecies()
 * both return an int[], where index 0 == black and 1 == red
 * high numbers are better for both
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Engine {
	public Engine() {
		
	}
	
	public void sortByFitness(ArrayList<Brain> population) {
		//Dummy method that shows the GA works,
		//populations is sorted by number of states in each brain, most first
		Collections.sort(population);
		
		//either
		//run a tournament and fight every brain against every other brain
		//or
		//fight each brain against the default brain a number of times
		//
		//then rank according to wins, most first
		//don't use the Collections.sort method at all, this is a dummy
	}
	
	public static void main(String args[]) {
		//Dummy engine methods show that the
		//World-, Brain- and Ant-related methods work
		Engine engine = new Engine();
		
		//Setup brains
		//Evolve and get the best brain from the GA
		Brain[] brains = new Brain[2];
		int epochs = 100;
		int popSize = 50;
		int mutationRate = 20;
		
		//Black is the best one found by the GA with parameters specified
		//Red is default brain, read in from file
		//Black should win when sortByFitness is done
		brains[0] = BrainController.getBestGABrain(engine, epochs, popSize, mutationRate);
		brains[1] = BrainController.readBrainFrom("example.brain");
		
		
		//Setup world
		World world = null;
		//This creates a random seed, rather than a fixed seed world
		int seed = 0;
		
		world = WorldController.getTournamentWorld(brains, seed);
		world = WorldController.readWorldFrom(brains, "example.world");
		
		//Setup ants
		ArrayList<Ant> ants = world.getAnts();
		
		//Run the simulation
		int r = 0;
		int rounds = 300000;
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
	}
}
