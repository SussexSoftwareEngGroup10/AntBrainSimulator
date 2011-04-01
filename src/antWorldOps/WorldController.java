package antWorldOps;

import antBrainOps.Brain;

public class WorldController {
	private static final WorldParser parser = new WorldParser();
	
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
//		String inPath = "example.world";
		String outPath = "my.world"; 
		WorldController wc = new WorldController(); 
		World world = null;
		World world2 = null;
		
		world = wc.getTournamentWorld(null, 0);
		
		wc.writeWorldTo(world, outPath);
		world2 = wc.readWorldFrom(outPath, null);
		
		System.out.println(world);
		System.out.println(world2);
		System.out.println(world.getAttributes());
		System.out.println(world2.getAttributes());
	}
}
