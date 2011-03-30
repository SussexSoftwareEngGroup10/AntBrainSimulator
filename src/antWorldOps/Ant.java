package antWorldOps;

import antBrainOps.Brain;

public class Ant {
	private static int uIDCount = 0;
	private final int uID;
	@SuppressWarnings("unused")
	private final Brain brain;
	@SuppressWarnings("unused")
	private int state;
	private Cell cell;
	private int direction;
	
	private static synchronized void incrementUIDCount() {
		uIDCount++;
	}
	
	private static synchronized int getUIDCount() {
		return uIDCount;
	}
	
	public Ant(int direction, Brain brain, Cell cell) {
		uID = getUIDCount();
		incrementUIDCount();
		
		this.brain = brain;
		this.state = 0;
		this.cell = cell;
		this.direction = direction;
	}
	
	public void act() {
		//TODO something to do with the brain and state
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public int getUID() {
		return uID;
	}
	
	public int getDirection() {
		return direction;
	}
}
