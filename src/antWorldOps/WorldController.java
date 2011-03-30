package antWorldOps;

import antBrainOps.Brain;

public class WorldController {
	private static final WorldParser parser = new WorldParser();
	private static final String inPath = "example.world";
	private static final String outPath = "my.world"; 
	
	public WorldController() {
		
	}
	
	public World getTournamentWorld(Brain[] brains, int seed) {
		return World.getTournamentWorld(brains, seed);
	}
	
	public World readWorldFrom(String path, Brain[] brains) {
		return parser.readWorldFrom(path, brains);
	}
	
	public void writeWorldTo(World world, String path) {
		parser.writeWorldTo(world, path);
	}
	
	public static void main(String args[]) {
		WorldController wc = new WorldController(); 
		World world = null;
		
		world = wc.readWorldFrom(inPath, null);
		world = wc.getTournamentWorld(null, 0);
		
		System.out.println(world);
		wc.writeWorldTo(world, outPath);
	}
}
