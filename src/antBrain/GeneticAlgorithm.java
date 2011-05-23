package antBrain;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import antWorld.World;

import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
import utilities.InformationHighEvent;
import utilities.InformationLowEvent;
import utilities.InformationNormEvent;
import utilities.Logger;
import utilities.TimeEvent;
import utilities.WarningEvent;

import engine.GameEngine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class GeneticAlgorithm implements Serializable {
	private transient static final long serialVersionUID = 1L;
	private transient static final String superFolderPath = "brain_populations";
	private transient static final File superFolder = new File(superFolderPath);
	private transient static int instances = 0;
	private transient static final String subFolderPathPrefix =
		superFolderPath + "\\" + "genetic_algorithm_";
	private transient static final Random ran = new Random();
	
	//Transient object variables
	private transient final int instance;
	private transient int popLen;
	
	//Persistent object variables which are read and written when the object is serialised
	private int epoch;
	private Brain[] population;
	
	/**
	 * 
	 */
	public GeneticAlgorithm() {
		this.instance = instances;
		instances++;
		this.epoch = 0;
	}
	
	/**
	 * Creates a new population of brain objects ready to be evolved by evolve()
	 * 
	 * @param startBrain the default starting brain for new brains in the population
	 * @param popLen the number of brains in the population
	 */
	private void createPopulation(Brain startBrain, int popLen) {
		//Try to resume last epochs()
		try{
			if(loadLast()){
				this.popLen = popLen;
				Brain[] newPopulation = new Brain[popLen];
				
				//Copy serialised population to new population so that the Brain at 0 is
				//lowest fitness, and the Brain at length - 1 is highest fitness,
				//Any new Brains needing addition or removal as the population length is altered
				//are added or removed from 0, maintaining the natural ordering of the population
				for(int i = popLen - 1; i >= 0; i--){
					if(popLen - 1 - i >= popLen - 1 - this.population.length){
						newPopulation[popLen - 1 - i] = this.population[this.population.length - 1 - i];
					}else{
						//If there are more brains required than there are in the
						//population read in from loadLast(), create new brains
						newPopulation[i] = startBrain.clone();
					}
				}
				this.population = newPopulation;
			}else{
				//Otherwise create an entirely new population
				this.epoch = 1;
				this.popLen = popLen;
				this.population = new Brain[popLen];
				//Fill with number of startBrains
				for(int i = 0; i < popLen; i++){
					this.population[i] = startBrain.clone();
				}
				Logger.log(new InformationNormEvent("New GeneticAlgorithm Brain population of size "
					+ popLen + " created"));
			}
		}catch(IOEvent e){
			Logger.log(e);
		}
	}
	
	/**
	 * @param dummyEngine the engine object to be used to determine the fitness of brains
	 * @param threadPoolExecutor used to determine brain fitnesses concurrently
	 * @param semaphore used to determine brain fitnesses concurrently
	 * @param epochs number of times to evolve the population
	 * @param rounds number of rounds in one simulation
	 * @param elite number of brains from the old population to retain in the new population
	 * @param mutationRate the chance of altering any part of any command in any brain
	 */
	private void evolve(GameEngine gameEngine, Brain absoluteTrainingBrain, int epochs, int elite,
		int mutationRate) {
		//Log information on epoch and evolution
		if(this.epoch == 1){
			//Starting evolution from a newly created population
			Logger.log(new InformationHighEvent("Began GeneticAlgorithm evolution for "
				+ epochs + " epochs"
				+ ", " + this.population.length + " brains in the population"
				+ ", an elite of " + elite
				+ ", and a 1/" + mutationRate + " chance of mutation"));
		}else{
			//Resuming evolution from either a serialised object,
			//or after an evolve() call has been completed on this population
			Logger.log(new InformationHighEvent("Resumed GeneticAlgorithm evolution for "
				+ epochs + " epochs at epoch: " + this.epoch
				+ ", " + this.population.length + " brains in the population"
				+ ", an elite of " + elite
				+ ", and a 1/" + mutationRate + " chance of mutation"));
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
		
		//Elitism is not necessary, but may give better results
		
		//Each iteration retains the elite,
		//removes the less fit half of the population and
		//breeds random members of the remaining population until
		//the population is the same size as when it began the iteration
		rank(gameEngine, absoluteTrainingBrain);
		Logger.log(new InformationLowEvent("Initial sort completed"));
		
		//Timing
		Logger.log(new TimeEvent("from start to beginning of first epoch", TimeUnit.SECONDS));
		Logger.restartTimer();
		
		//After constructor, epoch == 1,
		//after deserialisation, epoch == epoch to be run next
		for(; this.epoch <= epochs; this.epoch++){
			//Save at the start of every epoch,
			//so evolve() can be terminated and resumed
			save();
			
			Logger.log(new InformationLowEvent("Beginning epoch " + this.epoch));
			
			newPop = new Brain[this.popLen];
			
			//Copy over elite to the end
			for(j = 0; j < elite; j++){
				newPop[this.popLen - 1 - j] = this.population[this.popLen - 1 - j];
			}
			
			//Breed good (most fit half of the population, includes the elite)
			//Fill newPop from beginning to where elite starts
			for(j = 0; j < this.popLen - elite; j++){
				// Spawn child from 2 random parents
				//(popLen / 2) 
				ran1 = ran.nextInt(this.popLen / 2) + this.popLen / 2;
				if(this.popLen < 3){
					ran2 = 0;
				}else{
					try{
						ran2 = ran.nextInt((this.popLen / 2) - 1) + this.popLen / 2;
					}catch(IllegalArgumentException ex){
						Logger.log(new WarningEvent("Ran arguments in GeneticAlgorithm: " +
							"ran.nextInt((" + this.popLen + "/ 2) - 1) + "
							+ this.popLen + " / 2", ex));
						ran2 = 0;
					}
				}
				// Avoid identical parents
				if(ran2 >= ran1){
					ran2++;
				}
				try {
					newPop[j] = breed(this.population[ran1], this.population[ran2], mutationRate);
				} catch (IllegalArgumentEvent e) {
					Logger.log(e);
				}
			}
			this.population = newPop;
			//Order, ready for next epoch
			rank(gameEngine, absoluteTrainingBrain);
			
			//Timing
			Logger.log(new TimeEvent("for epoch " + (this.epoch - 1), TimeUnit.SECONDS));
			Logger.restartTimer();
			
			//Logging
			Logger.log(new InformationHighEvent("Completed "
				+ (this.epoch) / (double) epochs * 100
				+ "% of GeneticAlgorithm evolution epochs"));
		}
		Logger.log(new InformationHighEvent("Completed GeneticAlgorithm evolution"));
	}
	
	public void rank(GameEngine gameEngine, Brain absoluteTrainingBrain){
		gameEngine.fitnessContestSetup(this.population, absoluteTrainingBrain);
		
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
		//Set fitness for every brain in population
		for(int i = 0; i < this.population.length; i++){
			while(worlds.size() < 4){
				worlds.push((World) world.clone());
			}
			
			gameEngine.fitnessContestStep(worlds);
		}
		
		Arrays.sort(this.population);
		
		//Log fitness stats
		int i;
		int index = this.population.length - 1;
		for(i = this.population.length - 2; i >= 0; i--){
			if(this.population[i].getFitness() > this.population[index].getFitness()){
				index = i;
			}
		}
		int maxFitness = this.population[index].getFitness();
		
		int total = 0;
		for(i = this.population.length - 1; i >= 0; i--){
			total += this.population[i].getFitness();
		}
		int avgFitness =  total / this.population.length;

		index = this.population.length - 1;
		for(i = this.population.length - 2; i >= 0; i--){
			if(this.population[i].getFitness() < this.population[index].getFitness()){
				index = i;
			}
		}
		int minFitness = this.population[index].getFitness();
		
		Logger.log(new InformationNormEvent("Fitnesses: max: " + maxFitness
			+ ";  avg: " + avgFitness + ";  min: " + minFitness));
	}
	
	/**
	 * @param brainA
	 * @param brainB
	 * @param mutationConstant
	 * @return a new brain containing possibly altered states from A, B and new entirely states
	 * @throws IllegalArgumentEvent 
	 */
	private static Brain breed(Brain brainA, Brain brainB, int mutationConstant) throws IllegalArgumentEvent {
		//The target Brain should contain a sensible number of states,
		//it is not necessary for its size to change in the same direction on every breed
		//The size of the brain resulting from evolution will reflect on the
		//size which provides the best fitness
		//i.e. size, in itself, is not inherently good
		//Can't grow or shrink brains, which is bad
		//Numbering of states, and their pointers are fixed,
		//so reducing the size by removing a state would be a real pain,
		//as changing every pointer would be inefficient,
		//and leaving gaps might break some methods
		int targetSize = 50;//Math.max(brainA.size(), brainB.size()) + ran.nextInt(2);
		//Keep targetSize within limits
		if(targetSize < Brain.getMinNumOfStates()){
			Logger.log(new InformationLowEvent("Brain bred containing the " +
				"minimum number of states (" + Brain.getMinNumOfStates() + ")"));
			targetSize = Brain.getMinNumOfStates();
		}else if(targetSize > Brain.getMaxNumOfStates()){
			Logger.log(new InformationLowEvent("Brain bred containing the " +
				"maximum number of states (" + Brain.getMaxNumOfStates() + ")"));
			targetSize = Brain.getMaxNumOfStates();
		}
		
		Brain brainC = new Brain(targetSize);
		ArrayList<State> statesC = new ArrayList<State>(brainC.values());
		Set<Integer> keysA = brainA.keySet();
		Set<Integer> keysB = brainB.keySet();
		int i = 0;
		
		//Consider each state in statesA and statesB for inclusion in statesC
		for(i = 0; i < targetSize; i++){
			State stateA = null;
			State stateB = null;
			State stateC = null;
			//Get state a, if any
			if(keysA.contains(i)){
				stateA = brainA.get(i);
			}
			//Get state b, if any
			if(keysB.contains(i)){
				stateB = brainB.get(i);
			}
			//Breed
			if(stateA != null && stateB != null){
				stateC = combineStates(i, stateA, stateB, targetSize, mutationConstant);
			//Mutate A
			}else if(stateA != null){
				stateC = new State(i, mutateGenes(stateA.getGenes(), targetSize, mutationConstant));
			//Mutate B
			}else if(stateB != null){
				stateC = new State(i, mutateGenes(stateB.getGenes(), targetSize, mutationConstant));
			//New random state
			}else{
				stateC = new State(statesC.size(), ranGenes(targetSize));
			}
			statesC.add(stateC);
		}
		
		for(i = 0; i < statesC.size(); i++){
			//Put new states into brain to be returned
			State state = statesC.get(i);
			brainC.put(state.getStateNum(), state);
		}
		return brainC;
	}
	
	/**
	 * @param index
	 * @param sa
	 * @param sb
	 * @param states
	 * @param mutationConstant
	 * @return a state containing parts of A, B and new parts
	 * @throws IllegalArgumentEvent 
	 */
	private static State combineStates(int index, State sa, State sb,
		int states, int mutationConstant) throws IllegalArgumentEvent {
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
	
	/**
	 * @param gc
	 * @param states
	 * @param mutationConstant
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private static int[] mutateGenes(int[] gc, int states, int mutationConstant) throws IllegalArgumentEvent {
		//All data is discrete, not continuous,
		//so adding or subtracting a small amount is meaningless
		//Rather, select a completely new value, independent from the old value
		//for each gene: (1 / mutationConstant) of the time, a random value is chosen
		//mutationConstant is proportional to the likelihood that a gene will not be altered

		int[] values = State.getValues(states);
		
		//Changing the entire command is 10 times less likely than changing a parameter
		//The value 10 is arbitrary
		//This is not evolution as it does not use any of the genes
		//present in either of the parents
		int i = 0;
		if(ran.nextInt(mutationConstant * 10) == 0){
			return ranGenes(states);
		}
		for(i = 1; i < 9; i++){
			if(ran.nextInt(mutationConstant) == 0){
				//Sometimes replace value with a new random value
				if(i == 4){
					//P < 2 makes no sense (ran.nextInt(<= 1))
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
	
	/**
	 * @param states
	 * @return genes as used by a state, but with logical random values
	 * @throws IllegalArgumentEvent 
	 */
	private static int[] ranGenes(int states) throws IllegalArgumentEvent {
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
	
	/**
	 * @param startBrain
	 * @param trainingBrain
	 * @return
	 */
	public Brain getBestBrain(GameEngine gameEngine, Brain startBrain, Brain absoluteTrainingBrain, 
		int epochs, int popLen, int elite, int mutationRate) {
		this.popLen = popLen;
		createPopulation(startBrain, this.popLen);
		evolve(gameEngine, absoluteTrainingBrain, epochs, elite, mutationRate);
		return this.population[this.population.length - 1];
	}
	
	/**
	 * Delete all except latest toRetain save files
	 */
	public void clearSaves(int toRetain) {
		File folder = new File(subFolderPathPrefix + this.instance);
		//Get all .ser files in this GA's save folder
		File[] files = folder.listFiles(new SerFilter());
		if(files == null) return;
		
		Arrays.sort(files);
		//.ser files are now in alphabetical order
		for(int i = 0; i < files.length - toRetain; i++){
			files[i].delete();
		}
	}
	
	/**
	 * Serialise this object to a file
	 */
	private void save() {
		//Only retain the 4 most recent saves (and this save)
		clearSaves(4);
		
		//Setup save superFolder
		if(!superFolder.exists()) superFolder.mkdir();
		
		//Setup save subFolder
		String subFolderPath = subFolderPathPrefix + this.instance;
		File subFolder = new File(subFolderPath); 
		subFolder.mkdir();
		
		String epochString = Integer.toString(this.epoch);
		while(epochString.length() < 5){
			epochString = "0" + epochString;
		}
		String filePath = subFolderPath + "\\epoch_" + epochString + ".ser";
		
		//Write this object to path
		try{
			writeObject(new ObjectOutputStream(new FileOutputStream(filePath)));
			Logger.log(new InformationNormEvent("Completed serializing GeneticAlgorithm " +
				"object to " + filePath));
		}catch(FileNotFoundException e){
			Logger.log(new IOEvent("Save file: \""
				+ filePath + "\" not found", e));
		}catch(IOException e){
			Logger.log(new IOEvent(e.getMessage(), e));
		}
		
		//Write best brain so far to file
		Brain b = this.population[this.popLen - 1].clone();
		try{
			BrainParser.writeBrainTo(b, "ga_result_full");
		}catch(IOEvent e){
			Logger.log(e);
		}
		try{
			b.trim();
			try{
				BrainParser.writeBrainTo(b, "ga_result_trimmed");
			}catch(IOEvent e){
				Logger.log(e);
			}
		}catch(IllegalArgumentEvent e){
			Logger.log(e);
		}
	}
	
	/**
	 * @return deduce the last written file, and read it in
	 * @throws IOEvent 
	 */
	private boolean loadLast() throws IOEvent {
		//Get superFolder ending in highest number
		File[] files = superFolder.listFiles();
		if(files == null) return false;	//No superfolder
		int max = Integer.MIN_VALUE;
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
			return false;				//No subfolders
		}
		String subFolderPath = subFolderPathPrefix + max;
		File folder = new File(subFolderPath);
		
		//Get file ending in highest number
		String filePathPrefix = subFolderPath + "\\epoch_";
		String filePathSuffix = ".ser";
		files = folder.listFiles();
		String maxFilePath = null;
		max = Integer.MIN_VALUE;
		for(File f2 : files){
			filePath = f2.getPath();
			if(filePath.startsWith(filePathPrefix)
				&& filePath.endsWith(filePathSuffix)){
				filePath = filePath.replace(filePathPrefix, "");
				filePath = filePath.replace(filePathSuffix, "");
				num = Integer.parseInt(filePath);
				if(num > max){
					max = num;
					maxFilePath = filePath;
				}
			}
		}
		if(max == -1){
			//No files,
			//could try next best subfolder
			return false;
		}
		String fileName = filePathPrefix + maxFilePath + filePathSuffix;
		File loadFile = new File(fileName);
		load(loadFile);
		
		return true;
	}
	
	/**
	 * @param loadFile read the file specified and alter object variables accordingly
	 * @throws IOEvent 
	 */
	private void load(File filePath) throws IOEvent {
		try{
			readObject(new ObjectInputStream(new FileInputStream(filePath)));
			Logger.log(new InformationNormEvent("Completed deserializing GeneticAlgorithm " +
				"object from " + filePath.getPath()));
		}catch(FileNotFoundException e){
			throw new IOEvent("Save file: \""
				+ filePath.getPath() + " not found", e);
		}catch(ClassNotFoundException e){
			throw new IOEvent(e.getMessage(), e);
		}catch(IOException e){
			throw new IOEvent(e.getMessage(), e);
		}
	}
	
	/**
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.epoch);
		out.writeObject(this.population);
	}
	
	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		//Persistent variables
		this.epoch = in.readInt();
		this.population = (Brain[]) in.readObject();
		
		//Verification
		if(this.population == null){
			Logger.log(new IOEvent("population == null"));
		}
		if(this.population.length <= 1){
			Logger.log(new IOEvent("population.length <= 1"));
		}
		if(this.epoch < 0){
			Logger.log(new IOEvent("epoch < 0"));
		}
		
		//Transient variables
		this.popLen = this.population.length;
	}
	
	/**
	 * A FileFilter which only accepts files in the appropriate folder with the .ser extension
	 * 
	 * @author pkew20 / 57116
	 * @version 1.0
	 */
	private class SerFilter implements FileFilter {
		/**
		 * 
		 */
		protected SerFilter() {
			//No code needed
		}
		
		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 * 
		 * True if startsWith subFolderPathPrefix and endsWith ".ser"
		 */
		@Override
		public boolean accept(File pathname) {
			return pathname.getPath().startsWith(subFolderPathPrefix)
				&& pathname.getPath().endsWith(".ser");
		}
	}
}
