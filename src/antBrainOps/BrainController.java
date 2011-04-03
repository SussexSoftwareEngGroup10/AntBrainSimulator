package antBrainOps;

import engine.Engine;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class BrainController {
	public BrainController() {
		
	}
	
	public Brain readBrainFrom(String path) {
		return new BrainParser().readBrainFrom(path);
	}
	
	public Brain readBrainGA() {
		return new BrainParser().readBrainFrom(GA.getBestBrainPath());
	}
	
	public Brain getBestGABrain(Engine engine, int epochs, int popSize, int mutationRate) {
		GA ga = new GA();
		ga.createPopulation(engine, popSize);
		ga.evolve(engine, epochs, mutationRate);
		return ga.getBestBrain();
	}
}
