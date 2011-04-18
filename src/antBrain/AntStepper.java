package antBrain;

import antWorld.Ant;

public class AntStepper extends Thread {
	private Ant ant;
	private int steps;
	
	public AntStepper(Ant ant, int steps) {
		this.ant = ant;
		this.steps = steps;
	}
	
	@Override
	public void run() {
		for(int s = 0; s < this.steps; s++){
			this.ant.step();
		}
	}
	
//	public AntStepper() {
//		//No code needed
//	}
//	
//	public void stepAnt(Ant ant, int steps) {
//		for(int s = 0; s < steps; s++){
//			ant.step();
//		}
//	}
}
