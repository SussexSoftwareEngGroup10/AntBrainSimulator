package antWorld;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class WorldController {
	public static World getTournamentWorld(int seed) {
		return World.getTournamentWorld(seed);
	}
	
	public static World getWorld(int rows, int cols, int rocks, int seed,
			int anthills, int anthillSideLength, int foodBlobCount, int foodBlobSideLength,
			int foodBlobCellFoodCount, int antInitialDirection) {
		return new World(rows, cols, rocks, seed, anthills, anthillSideLength,
			foodBlobCount, foodBlobSideLength, foodBlobCellFoodCount, antInitialDirection);
	}
	
	public static World readWorldFrom(String path) {
		return WorldParser.readWorldFrom(path);
	}
	
	public static void writeWorldTo(World world, String path) {
		WorldParser.writeWorldTo(world, path);
	}
}
