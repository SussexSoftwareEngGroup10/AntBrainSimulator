package engine;

import java.util.ArrayList;
import java.util.Collections;

import antBrainOps.BrainController;
import antBrainOps.Brain;
import antWorldOps.Ant;
import antWorldOps.WorldController;
import antWorldOps.World;

/**
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
		
		//TODO
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
		BrainController bc = new BrainController();
		WorldController wc = new WorldController();
		
		//Evolve and get the best brain from the GA
		Brain[] brains = new Brain[2];
		int epochs = 100;
		int popSize = 50;
		int mutationRate = 20;
		
		//Setup brains
		//Black is the best one found by the GA with parameters specified
		//Red is default brain, read in from file
		//Black should win when sortByFitness is done
		brains[0] = bc.getBestGABrain(engine, epochs, popSize, mutationRate);
		brains[1] = bc.readBrainFrom("example.brain");
		
		
		World world = null;
		//This creates a random seed, rather than a fixed seed world
		int seed = 0;
		
		//Setup world
		world = wc.getTournamentWorld(brains, seed);
		world = wc.readWorldFrom(brains, "example.world");
		
		//Setup ants
		ArrayList<Ant> ants = world.getAnts();
		
		//Run the engine for each ant
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
