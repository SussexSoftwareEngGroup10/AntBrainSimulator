package antBrainOps;

public class BrainController {
	private static final String inPath = "example.brain";
	private static final String outPath = "my.brain"; 
	
	
	public BrainController() {
		
	}
	
	public Brain readBrainFrom(String path) {
		return new BrainParser().readBrainFrom(path);
	}
	
	public Brain readBrainGA() {
		return new BrainParser().readBrainFrom(GA.getBestBrainPath());
	}
	
	public Brain getBestGABrain(int epochs, int popSize) {
		GA gA = new GA();
		gA.createPopulation(popSize);
		gA.evolve(epochs); //Writes best resulting Brain to file
		return readBrainGA();
	}
	
	public static void main(String args[]) {
		BrainController bc = new BrainController();
		
		Brain brain = bc.readBrainFrom(inPath);
		new BrainParser().writeBrainTo(brain, outPath);
	}
}
