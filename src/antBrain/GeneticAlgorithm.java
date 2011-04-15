package antBrain;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import utilities.InformationEvent;
import utilities.Logger;
import utilities.WarningEvent;

import engine.DummyEngine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class GeneticAlgorithm {
	private static final Random ran = new Random();
	private static final int min = Brain.getMinNumOfStates();
	private static final int max = Brain.getMaxNumOfStates();
	private static String bestBrainPath = "best.brain";
	private Brain[] population;
	private int popSize;
	
	public GeneticAlgorithm() {
		
	}
	
	public static void setBestBrainPath(String bestBrainPath) {
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Path for the writing of Brain objects " +
				"resulting from GeneticAlgorithm.writeBrain() changed to " + bestBrainPath));
		}
		GeneticAlgorithm.bestBrainPath = bestBrainPath;
	}
	
	public static String getBestBrainPath() {
		return bestBrainPath;
	}
	
	public void createPopulation(Brain exampleBrain, DummyEngine dummyEngine, int popSize) {
		this.popSize = popSize;
		population = new Brain[popSize];
		
		//Fill with number of example brains
		int i = 0;
		for(i = 0; i < popSize; i++){
			population[i] = (Brain) exampleBrain.clone();
		}
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("New GeneticAlgorithm Brain population of size " + popSize + " created"));
		}
	}
	
	public void evolve(DummyEngine dummyEngine, int epochs, int rounds, int elite, int mutationRate) {
		if(Logger.getLogLevel() >= 1.5){
			Logger.log(new InformationEvent("Begun GeneticAlgorithm evolution for "
				+ epochs + " epochs, with " + rounds + " rounds per simulation, an elite of " + elite +
				", and a 1/" + mutationRate + " chance of mutation"));
		}
		Brain[] newPop;
		int e = 0;
		int i = 0;
		int j = 0;
		int ran1;
		int ran2;
		
		//Elitism is where, when each epoch is run and a new population is
		//formed from the offspring of the old population,
		//a number of the individuals with the highest fitnesses from
		//the old population are moved into the new population
		//This means the best brain found so far, at the end of each epoch
		//is never worse than the best brain at the last epoch
		//However, this can mean becoming stuck in local optima,
		//and not searching the search space enough, resulting in
		
		//Elitism has been tested and works,
		//It is not necessary, but may give better results
		//when tested on the final DummyEngine
		
		//Each iteration retains the elite,
		//removes the less fit half of the population and
		//breeds random members of the remaining population until
		//the population is the same size as when it began the iteration
		orderByFitness(dummyEngine, rounds);

		for(e = 0; e < epochs; e++){
			//Log progress
			int frequency = epochs / 100;
			if(frequency == 0) frequency = 1;
			for(i = 0; i <= epochs / frequency; i++){
				if(e == i * frequency){
					if(Logger.getLogLevel() >= 1.5){
						Logger.log(new InformationEvent("Completed " + i + " percent of " +
							"GeneticAlgorithm evolution epochs"));
					}
				}
			}
			newPop = new Brain[popSize];
			
			//Copy over elite to the end
			for(j = 0; j < elite; j++){
				newPop[popSize - 1 - j] = population[popSize -  1 - j];
			}
			
			//Breed good (most fit half of the population, includes the elite)
			//Fill newPop from beginning to where elite starts
			for(j = 0; j < popSize - elite; j++){
				// Spawn child from 2 random parents
				//(popSize / 2) 
				ran1 = ran.nextInt(popSize / 2) + popSize / 2;
				if(popSize < 3){
					ran2 = 0;
				}else{
					try{
						ran2 = ran.nextInt((popSize / 2) - 1) + popSize / 2;
					}catch(IllegalArgumentException ex){
						if(Logger.getLogLevel() >= 1){
							Logger.log(new WarningEvent("Ran arguments in GeneticAlgorithm: " +
								"ran.nextInt((" + popSize + "/ 2) - 1) + " + popSize + " / 2", ex));
						}
						ran2 = 0;
					}
				}
				// Avoid identical parents
				if(ran2 >= ran1){
					ran2++;
				}
				newPop[j] = breed(population[ran1], population[ran2], mutationRate);
			}
			population = newPop;
			//Order, ready for next epoch
			orderByFitness(dummyEngine, rounds);
			
			//Write best brain so far to file
			BrainParser.writeBrainTo(population[popSize - 1], "ga_result");
		}
		if(Logger.getLogLevel() >= 1.5){
			Logger.log(new InformationEvent("Completed GeneticAlgorithm evolution"));
		}
		for(i = 0; i < population.length; i++){//TODO
			System.out.println(population[i]);
		}
	}
	
	private void orderByFitness(DummyEngine dummyEngine, int rounds) {
		dummyEngine.sortByFitness(population, rounds);
	}
	
	private Brain breed(Brain brainA, Brain brainB, int mutationConstant) {
		Brain brainC = new Brain();
		ArrayList<State> statesC = new ArrayList<State>(brainC.getValues());
		Set<Integer> keysA = brainA.getKeys();
		Set<Integer> keysB = brainB.getKeys();
		State stateA;
		State stateB;
		State stateC;
		int i = 0;
		//The target Brain should contain a sensible number of states,
		//it is not necessary for its size to change in the same direction on every breed
		//The size of the brain resulting from evolution will reflect on the
		//size which provides the best fitness
		//i.e. size, in itself, is not inherently good
//		int targetSize = max;
		int targetSize = 20;
		//Math.max(brainA.getNumOfStates(), brainB.getNumOfStates()) + ran.nextInt(3);
		//TODO
		//removes random states, which is bad
		//Numbering of states, and their pointers are fixed,
		//so reducing the size by removing a state would be a real pain,
		//as changing every pointer would be inefficient,
		//and leaving gaps might break some methods
		
		//Keep targetSize within limits
		if(targetSize < min){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new WarningEvent("Brain bred containing the" +
					"minimum number of states (" + min + ")"));
			}
			targetSize = min;
		}else if(targetSize > max){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new WarningEvent("Brain bred containing the" +
					"maximum number of states (" + max + ")"));
			}
			targetSize = max;
		}
		
		//Consider each state in statesA and statesB for inclusion in statesC
		for(i = 0; i < targetSize; i++){
			stateA = null;
			stateB = null;
			stateC = null;
			//Get state a, if any
			if(keysA.contains(i)){
				stateA = brainA.getState(i);
			}
			//Get state b, if any
			if(keysB.contains(i)){
				stateB = brainB.getState(i);
			}
			//Breed
			if(stateA != null && stateB != null){
				stateC = combineStates(i, stateA, stateB, targetSize, mutationConstant);
			//Mutate A
			}else if(stateA != null){
				stateC = mutateState(i, stateA, targetSize, mutationConstant);
			//Mutate B
			}else if(stateB != null){
				stateC = mutateState(i, stateB, targetSize, mutationConstant);
			//New random state
			}else{
				stateC = ranState(statesC.size(), targetSize);
			}
			statesC.add(stateC);
		}
		
		for(i = 0; i < statesC.size(); i++){
			//Put new states into brain to be returned
			brainC.setState(statesC.get(i));
		}
		return brainC;
	}
	
	private State combineStates(int index, State sa, State sb, int states, int mutationConstant) {
		int[] ga = sa.getGenes();
		int[] gb = sb.getGenes();
		int[] gc = new int[9];
		
		//Set command
		if(ran.nextInt(2) == 0){
			gc[0] = ga[0];
		}else{
			gc[0] = gb[0];
		}
		
		//One of the 2 input states must have a value for field i,
		//unless neither parent state has the MARKER condition, and the resulting state does
		//Randomly choose a or b and try to get its value,
		//if it has none, get the value of the other,
		//this may still result in -1 for senseMarker if a state has SENSE and MARKER
		int i = 0;
		for(i = 1; i < 9; i++){
			if(ran.nextInt(2) == 0){
				if(ga[i] == -1){
					gc[i] = gb[i];
				}else{
					gc[i] = ga[i];
				}
			}else{
				if(gb[i] == -1){
					gc[i] = ga[i];
				}else{
					gc[i] = gb[i];
				}
			}
		}
		
		//If neither of the parent states has SENSE......MARKER...[senseMarker]
		//the senseMarker value for both will be -1, and the child will be given this value
		//this is not a legal value and must be replaced with a new random value
		//This is performed by the mutateGenes method
		
		gc = mutateGenes(gc, states, mutationConstant);
		
		return new State(index, gc);
	}
	
	private State mutateState(int index, State c, int states, int mutationConstant) {
		return new State(index, mutateGenes(c.getGenes(), states, mutationConstant));
	}
	
	private int[] mutateGenes(int[] gc, int states, int mutationConstant) {
		//All data is discrete, not continuous,
		//so adding or subtracting a small amount is meaningless
		//Rather, select a completely new value, independent from the old value
		//for each gene: (1 / mutationConstant) of the time, a random value is chosen
		//mutationConstant is proportional to the likelihood that a gene will not be altered

		int[] values = State.getValues(states);
		
		//Changing the entire command is 10 times less likely than changing a parameter
		//The value 10 is arbitrary
		//This is not evolution as it does not use any of the genes present in either of the parents
		int i = 0;
		if(ran.nextInt(mutationConstant * 10) == 0){
			return ranGenes(states);
		}
		for(i = 1; i < 9; i++){
			if(ran.nextInt(mutationConstant) == 0){
				//Sometimes replace value with a new random value
				if(i == 4){
					//P < 2 makes no sense
					gc[i] = ran.nextInt(values[i] - 2) + 2;
				}else{
					gc[i] = ran.nextInt(values[i]);
				}
			}
		}
		
		//Get new value for senseMarker
		//if SENSE and MARKER and senseMarker is -1
		if(gc[0] == 0 && gc[7] == 6 && gc[8] == -1){
			gc[8] = ran.nextInt(State.getValue(states, 8));
		}
		
		return gc;
	}
	
	private State ranState(int index, int states) {
		return new State(index, ranGenes(states));
	}
	
	private int[] ranGenes(int states) {
		int[] values = State.getValues(states);
		int[] gc = new int[9];
		int i = 0;
		
		for(i = 0; i < 9; i++){
			//Generate a new random value
			if(i == 4){
				//P < 2 makes no sense
				gc[i] = ran.nextInt(values[i] - 2) + 2;
			}else{
				gc[i] = ran.nextInt(values[i]);
			}
		}
		
		//Every index in gc now has a possible value,
		//however, this does not matter, as values which do not apply to the
		//generated command value (g[0]) will not be used in the State constructor
		//This is inefficient as some values are generated needlessly,
		//however, increasing efficiency would require drastically more code,
		//and switch/case statements, which would be more prone to errors
		
		return gc;
	}
	
	public Brain getBestBrain() {
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("Returned the Brain with the highest fitness generated by GeneticAlgorithm"));
		}
		//Assumes population is sorted by fitness in ascending order
		return population[population.length - 1];
	}
}
