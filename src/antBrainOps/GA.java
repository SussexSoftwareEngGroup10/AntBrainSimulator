package antBrainOps;

import java.util.ArrayList;

public class GA {
	private static final String bestBrainPath = "best.brain";
	private final ArrayList<Brain> population = new ArrayList<Brain>();
	private final Brain exampleBrain = new BrainParser().readBrainFrom(bestBrainPath);
	
	public GA() {
		
	}
	
	public static String getBestBrainPath() {
		return bestBrainPath;
	}
	
	public void createPopulation(int popSize) {
		//Remove current population
		try{
			while(true){
				population.remove(0);
			}
		}catch(IndexOutOfBoundsException iob){
			
		}
		
		//Fill with number of default brains
		int i = 0;
		for(i = 0; i < popSize; i++){
			population.add((Brain) exampleBrain.clone());
		}
	}
	
	public void evolve(int epochs) {
		//TODO
		//breed brains, get fitness using an Engine
		//Copy other stuff from GAUsingNN (or don't)
		//change values of random bits of states (could use enum codes)
		
		writeBrain(new Brain());
	}
	
	private void writeBrain(Brain brain) {
		new BrainParser().writeBrainTo(brain, bestBrainPath);
	}
}
