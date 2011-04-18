package antBrain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import utilities.IOEvent;
import utilities.InformationEvent;
import utilities.Logger;
import utilities.WarningEvent;

import engine.DummyEngine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class GeneticAlgorithm implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String superFolderPath = "Brain_Populations";
	private static final File superFolder = new File(superFolderPath);
	private static int gasConstructed = 0;
	private static final String subFolderPathPrefix = superFolderPath + "\\" + "Genetic_Algorithm_";
	private static final Random ran = new Random();
	private static final int min = Brain.getMinNumOfStates();
	private static final int max = Brain.getMaxNumOfStates();
	private static String bestBrainPath = "best.brain";
	
	private int saveDir;
	private int epoch;
	private int popSize;
	private Brain[] population;
	
	public GeneticAlgorithm() {
		this.saveDir = gasConstructed;
		gasConstructed++;
		this.epoch = 0;
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
	
	public void createPopulation(Brain exampleBrain, int popSize) {
		//Try to resume last epochs()
		if(loadLast()){
			return;
		}
		
		//Otherwise create a new population
		this.popSize = popSize;
		this.population = new Brain[popSize];
		
		//Fill with number of example brains
		for(int i = 0; i < popSize; i++){
			this.population[i] = exampleBrain.clone();
		}
		if(Logger.getLogLevel() >= 2){
			Logger.log(new InformationEvent("New GeneticAlgorithm Brain population of size " + popSize + " created"));
		}
	}
	
	public void evolve(DummyEngine dummyEngine, int epochs, int rounds, int elite, int mutationRate) {
		//Log information on epoch and evolution
		if(Logger.getLogLevel() >= 1.5){
			if(this.epoch == 0){
				//Starting evolution from a newly created population
				Logger.log(new InformationEvent("Began GeneticAlgorithm evolution for "
					+ epochs + " epochs,"
					+ ", with " + rounds + " rounds per simulation,"
					+ "an elite of " + elite
					+ ", and a 1/" + mutationRate + " chance of mutation"));
			}else{
				//Resuming evolution from either a serialised object,
				//or after an evolve() call has been completed on this population
				Logger.log(new InformationEvent("Resumed GeneticAlgorithm evolution for "
					+ epochs + " epochs at epoch: " + this.epoch
					+ ", with " + rounds + " rounds per simulation,"
					+ "an elite of " + elite
					+ ", and a 1/" + mutationRate + " chance of mutation"));
			}
		}
		Brain[] newPop;
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
		
		//Each iteration retains the elite,
		//removes the less fit half of the population and
		//breeds random members of the remaining population until
		//the population is the same size as when it began the iteration
		orderByFitness(dummyEngine, rounds);
		
		//After constructor, epoch == 0,
		//after deserialization, epoch == epoch to be run next
		//Round values up, otherwise may get a divide by zero
		int tenth = epochs / 10;
		if(tenth == 0) tenth = 1;
		int hundredth = epochs / 100;
		if(hundredth == 0) hundredth = 1;
		int thousandth = epochs / 1000;
		if(thousandth == 0) thousandth = 1;
		for(; this.epoch < epochs; this.epoch++){
			//TODO these are polling, remove
			for(double d = 0; d <= epochs / thousandth; d += thousandth){
				//Log progress
				if(this.epoch == d){
					if(Logger.getLogLevel() >= 1.5){
						Logger.log(new InformationEvent("Completed " + d / 10 + "% of " +
						"GeneticAlgorithm evolution epochs"));
					}
				}
				//Save every epoch,
				//so JVM can be terminated and resumed
				if(this.epoch == d){
					save();
				}
			}
			
			newPop = new Brain[this.popSize];
			
			//Copy over elite to the end
			for(j = 0; j < elite; j++){
				newPop[this.popSize - 1 - j] = this.population[this.popSize -  1 - j];
			}
			
			//Breed good (most fit half of the population, includes the elite)
			//Fill newPop from beginning to where elite starts
			for(j = 0; j < this.popSize - elite; j++){
				// Spawn child from 2 random parents
				//(popSize / 2) 
				ran1 = ran.nextInt(this.popSize / 2) + this.popSize / 2;
				if(this.popSize < 3){
					ran2 = 0;
				}else{
					try{
						ran2 = ran.nextInt((this.popSize / 2) - 1) + this.popSize / 2;
					}catch(IllegalArgumentException ex){
						if(Logger.getLogLevel() >= 1){
							Logger.log(new WarningEvent("Ran arguments in GeneticAlgorithm: " +
								"ran.nextInt((" + this.popSize + "/ 2) - 1) + " + this.popSize + " / 2", ex));
						}
						ran2 = 0;
					}
				}
				// Avoid identical parents
				if(ran2 >= ran1){
					ran2++;
				}
				newPop[j] = breed(this.population[ran1], this.population[ran2], mutationRate);
			}
			this.population = newPop;
			//Order, ready for next epoch
			orderByFitness(dummyEngine, rounds);
			
			//Write best brain so far to file
			BrainParser.writeBrainTo(this.population[this.popSize - 1], "ga_result");
		}
		if(Logger.getLogLevel() >= 1.5){
			Logger.log(new InformationEvent("Completed GeneticAlgorithm evolution"));
		}
	}
	
	private void orderByFitness(DummyEngine dummyEngine, int rounds) {
		dummyEngine.sortByFitness(this.population, rounds);
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
		int targetSize = 50; //TODO do it properly, nextInt(5) - 2
		//Can't grow or shrink brains, which is bad
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
		return this.population[this.population.length - 1];
	}
	
	public void resetEpoch() {
		this.epoch = 0;
	}
	
	public static void clearSaves() {
		//Deletes every file beginning with the above prefix and ending with the above suffix
		File folder = new File(superFolderPath + "\\");
		File[] files = folder.listFiles();
		
		if(files == null) return;
		for(File f : files){
			if(f.getPath().startsWith(subFolderPathPrefix)){
				//Assume f does not contain any directories
				for(File sf : f.listFiles()){
					sf.delete();
				}
				f.delete();
			}
		}
	}
	
	public void save() {
		//Only retain the most recent save
		clearSaves();
		
		//Setup save superFolder
		if(!superFolder.exists()) superFolder.mkdir();
		
		//Setup save subFolder
		String subFolderPath = subFolderPathPrefix + this.saveDir;
		File subFolder = new File(subFolderPath); 
		subFolder.mkdir();
		
		String filePath = subFolderPath + "\\epoch_" + this.epoch + ".ser";
		
		//Write this object to path
		try{
			writeObject(new ObjectOutputStream(new FileOutputStream(filePath)));
		}catch(FileNotFoundException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent("Save file: \""
					+ filePath + " not found", e));
			}
		}catch(IOException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent(e.getMessage(), e));
			}
		}
	}
	
	public boolean loadLast() {
		//Get superFolder ending in highest number
		File[] files = superFolder.listFiles();
		if(files == null) return false;	//No superfolder
		int max = -1;
		int num;
		String filePath;
		for(File f1 : files){
			//Genetic_Algorithms\Genetic_Algorithm_x
			filePath = f1.getPath();
			if(filePath.startsWith(subFolderPathPrefix)){
				//Add number that the path ends with
				filePath = filePath.replace(subFolderPathPrefix, "");
				num = Integer.parseInt(filePath);
				if(num > max){
					max = num;
				}
			}
		}
		if(max == -1){
			return false;	//No subfolders
		}
		String subFolderPath = subFolderPathPrefix + max;
		File folder = new File(subFolderPath);
		
		//Get file ending in highest number
		String filePathPrefix = subFolderPath + "\\epoch_";
		String filePathSuffix = ".ser";
		files = folder.listFiles();
		max = -1;
		for(File f2 : files){
			filePath = f2.getPath();
			if(filePath.startsWith(filePathPrefix)
				&& filePath.endsWith(filePathSuffix)){
				filePath = filePath.replace(filePathPrefix, "");
				filePath = filePath.replace(filePathSuffix, "");
				num = Integer.parseInt(filePath);
				if(num > max){
					max = num;
				}
			}
		}
		if(max == -1){
			//No files,
			//could try next best subfolder
			return false;
		}
		String fileName = filePathPrefix + max + filePathSuffix;
		File loadFile = new File(fileName);
		load(loadFile);
		return true;
	}
	
	public void load(File loadFile) {
		try{
			readObject(new ObjectInputStream(new FileInputStream(loadFile)));
		}catch(FileNotFoundException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent("Save file: \""
					+ loadFile.getPath() + " not found", e));
			}
		}catch(ClassNotFoundException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent(e.getMessage(), e));
			}
		}catch(IOException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new IOEvent(e.getMessage(), e));
			}
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.saveDir);
		out.writeInt(this.epoch);
		out.writeInt(this.popSize);
		out.writeObject(this.population);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		this.saveDir = in.readInt();
		this.epoch = in.readInt();
		this.popSize = in.readInt();
		this.population = (Brain[]) in.readObject();
	}

//	private void readObjectNoData() throws ObjectStreamException{
//		
//	}
}
