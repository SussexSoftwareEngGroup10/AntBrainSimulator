package antWorld;

import antBrain.Brain;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WorldController {
	private static final WorldParser parser = new WorldParser();
	
	public WorldController() {
		
	}
	
	public World getTournamentWorld(Brain[] brains, int seed) {
		return World.getTournamentWorld(brains, seed);
	}
	
	public World readWorldFrom(Brain[] brains, String path) {
		return parser.readWorldFrom(brains, path);
	}
	
	public void writeWorldTo(World world, String path) {
		parser.writeWorldTo(world, path);
	}
}
