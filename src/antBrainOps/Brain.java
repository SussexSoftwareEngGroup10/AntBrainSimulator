package antBrainOps;

public class Brain implements Cloneable {
	private static final int numOfStates = 10000;
	private final State[] states = new State[numOfStates];
	
	public Brain() {
		
	}
	
	public static int getNumOfStates() {
		return numOfStates;
	}
	
	public void setState(int stateNum, State state) {
		states[stateNum] = state;
	}
	
	public State getState(int i) {
		return states[i];
	}
	
	public Brain clone() {
		Brain brain = new Brain();
		int i = 0;
		
		for(i = 0; i < numOfStates; i++){
			brain.setState(i, states[i]);
		}
		
		return brain;
	}
	
	public String toString() {
		String s = "";
		int i = 0;
		
		for(i = 0; i < numOfStates; i++){
			if(states[i] != null){
				s += states[i].toString();
				s += "\r\n";
			}
		}
		
		return s;
	}
}
