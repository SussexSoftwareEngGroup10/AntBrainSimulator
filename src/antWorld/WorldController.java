package antWorld;

import antBrain.Brain;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WorldController {
	public static World getTournamentWorld(Brain[] brains, int seed) {
		return World.getTournamentWorld(brains, seed);
	}
	
	public static World readWorldFrom(Brain[] brains, String path) {
		return WorldParser.readWorldFrom(brains, path);
	}
	
	public static void writeWorldTo(World world, String path) {
		WorldParser.writeWorldTo(world, path);
	}
}
