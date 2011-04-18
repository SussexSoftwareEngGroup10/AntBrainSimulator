package antBrain;

import antWorld.Ant;

public class StepThread extends Thread {

	public StepThread() {
		//No code needed
	}
	
	public void stepAnt(Ant ant, int steps) {
		for(int s = 0; s < steps; s++){
			ant.step();
		}
	}
}
