package antBrainOps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import engine.Engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class GA {
	private static final Random ran = new Random();
	private static final String bestBrainPath = "best.brain";
	private static final String exampleBrainPath = "example.brain";
	private static final Brain exampleBrain = new BrainParser().readBrainFrom(exampleBrainPath);
	private final ArrayList<Brain> population = new ArrayList<Brain>();
	
	private int elite = 0;
	
	public GA() {
		
	}
	
	public static String getBestBrainPath() {
		return bestBrainPath;
	}
	
	public void createPopulation(Engine engine, int popSize) {
		elite = 0;
		
		//Remove population ready for next
		try{
			while(true){
				population.remove(0);
			}
		}catch(IndexOutOfBoundsException iob){
			//population.size() == 0
		}
		
		//Fill with number of example brains
		int i = 0;
		for(i = 0; i < popSize; i++){
			population.add((Brain) exampleBrain.clone());
		}
		orderByFitness(engine);
	}
	
	public void evolve(Engine engine, int epochs, int mutationRate) {
		int popSize = population.size();
		int e = 0;
		int i = 0;
		int ran1;
		int ran2;
		
		//Each iteration retains the elite,
		//removes the less fit half of the population and
		//breeds random members of the remaining population until
		//the population is the same size as when it began the iteration
		
		for(e = 0; e < epochs; e++){
			//Remove the least fit half of the population
			for(i = 0; i < popSize / 2; i++){
				population.remove(population.size() - 1);
			}
			
			//Breed good (most fit half of the population, includes the elite)
			//until size = startSize + good (which will be removed below)
			while(population.size() < popSize + ((popSize - elite) / 2)){
				// Spawn child from 2 random parents
				ran1 = ran.nextInt(popSize / 2);
				
				ran2 = ran.nextInt((popSize / 2) - 1);
				// Avoid identical parents
				if(ran2 >= ran1){
					ran2++;
				}
				
				// Sexual reproduction
				population.add(breed(population.get(ran1), population.get(ran2), mutationRate));
			}
			
			while(population.size() > popSize + ((popSize - elite) / 2)){
				population.add(breed(population.get(0), population.get(0), mutationRate));
			}
			
			//Remove the last of the old population, not including the elite
			for(i = elite; i < ((popSize - elite) / 2) + elite; i++){
				population.remove(elite);
			}
			
			//Remove states which are equal from each of the Brains in the population,
			//Redirect pointers to the state which was not removed
			//Gaps are fine, don't need to change every state index and pointer
			//Gaps will be removed on next iteration (except for elites)
			//This would make the code more efficient on running and reading in for the Brains,
			//but would seriously slow down evolve()
			
			orderByFitness(engine);
		}
		
//		writeBrain(population.get(0));
	}
	
	private void orderByFitness(Engine engine) {
		engine.sortByFitness(population);
	}
	
	private Brain breed(Brain brainA, Brain brainB, int mutationConstant) {
		Brain brainC = new Brain();
		Collection<State> statesA = brainA.getStates();
		Collection<State> statesB = brainB.getStates();
		ArrayList<State> statesC = new ArrayList<State>(brainC.getStates());
		Iterator<State> iterA = statesA.iterator();
		Iterator<State> iterB = statesB.iterator();
		int i = 0;
		//The target Brain should contain a sensible number of states,
		//it is not necessary for its size to change in the same direction on every breed
		//The size of the brain resulting from evolution will reflect on the
		//size which provides the best fitness
		//i.e. size, in itself, is not inherently good
		int targetSize = Math.max(statesA.size(), statesB.size()) + (ran.nextInt(5) - 2);
		//Keep targetSize within limits
		if(targetSize < 3){
			targetSize = 3;
		}else if(targetSize > Brain.getMaxNumOfStates()){
			targetSize = Brain.getMaxNumOfStates();
		}
		
		//Consider each state in statesA and statesB for inclusion in statesC
		
		//Add state resulting from the combination of next a and b states
		while(iterA.hasNext() && iterB.hasNext()){
			statesC.add(combineStates(statesC.size(), (State) iterA.next(),
				(State) iterB.next(), targetSize, mutationConstant));
		}

		//Mutate sa and add to c
		while(iterA.hasNext()){
			statesC.add(mutateState(statesC.size(), (State) iterA.next(),
				targetSize, mutationConstant));
		}

		//Mutate sb and add to c
		while(iterB.hasNext()){
			statesC.add(mutateState(statesC.size(), (State) iterB.next(),
				targetSize, mutationConstant));
		}
		
		//All states in statesA and statesB have now been considered for inclusion in statesC
		//Any more states added will be purely random
		
		//Grow to size required
		while(statesC.size() < targetSize){
			statesC.add(ranState(statesC.size(), targetSize));
		}
		
		//Trim to size required
		while(statesC.size() > targetSize){
			//This should remove a random state each time
			statesC.remove(ran.nextInt(statesC.size()));
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
			if(ran.nextInt(2) == -1){
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
		
		//If neither of the parent states has SENSE......MARKER...senseMarker
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
		int i = 0;
		
		//Changing the entire command if 10 times less likely than changing a parameter
		//The value 10 is arbitrary
		if(ran.nextInt(mutationConstant * 10) == 0){
			return ranGenes(states);
		}
		for(i = 1; i < 9; i++){
			if(ran.nextInt(mutationConstant) == 0){
				//Sometimes replace value with a new random value
				gc[i] = ran.nextInt(values[i]);
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
			gc[i] = ran.nextInt(values[i]);
		}
		
		//Every index in gc now has a possible value,
		//however, this does not matter, as values which do not apply to the
		//generated command value (g[0]) will not be used in the State constructer
		
		return gc;
	}
	
	public Brain getBestBrain() {
		return population.get(0);
	}
	
	@SuppressWarnings("unused")
	private void writeBrain(Brain brain) {
		new BrainParser().writeBrainTo(brain, bestBrainPath);
	}
}
