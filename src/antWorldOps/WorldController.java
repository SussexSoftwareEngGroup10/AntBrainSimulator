package antWorldOps;

public class WorldController {
	private static final String inPath = "example.world";
	private static final String outPath = "my.world"; 
	
	public WorldController() {
		
	}
	
	public World getTournamentWorld() {
		World world = new World();
		world.tournamentWorld();
		return world;
	}
	
	public World ReadWorldFrom(String path) {
		return new WorldParser().readWorldFrom(path);
	}
	
	public static void main(String args[]) {
		WorldParser p = new WorldParser();
		World world = p.readWorldFrom(inPath);
		p.writeWorldTo(world, outPath);
	}
}
