package antBrain;

import engine.Engine;

/**
 * Used by the Engine to get Brain objects from file, or generated by the GA
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class BrainController {
	public static Brain readBrainFrom(String path) {
		return BrainParser.readBrainFrom(path);
	}
	
	public static Brain getBestGABrain(Engine engine, int epochs, int popSize, int mutationRate) {
		GA ga = new GA();
		ga.createPopulation(engine, popSize);
		ga.evolve(engine, epochs, mutationRate);
		return ga.getBestBrain();
	}
}
