package antBrain;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import utilities.Logger;
import utilities.WarningEvent;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain implements Cloneable, Comparable<Brain> {
	private static final int minNumOfStates = 3;
	private static final int maxNumOfStates = 10000;
	private final Hashtable<Integer, State> states;
	private int score;
	
	public Brain() {
		states = new Hashtable<Integer, State>();
	}
	
	/**
	 * Alternative constructor used for more efficient cloning of Brain objects
	 * 
	 * @param states
	 */
	protected Brain(Hashtable<Integer, State> states) {
		this.states = states;
	}
	
	public int getNumOfStates() {
		return states.size();
	}
	
	public static int getMinNumOfStates() {
		return minNumOfStates;
	}
	
	public static int getMaxNumOfStates() {
		return maxNumOfStates;
	}
	
	public void setState(State state) {
		states.put(state.getStateNum(), state);
	}
	
	public State getState(int i) {
		try{
			return states.get(i);
		}catch(NullPointerException npe){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new WarningEvent("Null state " + i + " returned in Brain"));
			}
			return null;
		}
	}
	
	public Collection<State> getValues() {
		return states.values();
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public Brain clone() {
		return new Brain(states);
	}
	
	public boolean equals(Object o) {
		Brain b = (Brain) o;
		//do state by state, not contains
		int i = 0;
		for(i = 0; i < maxNumOfStates; i++){
			if(!this.getState(i).equals(b.getState(i))){
				return false;
			}
		}
		return true;
	}
	
	public int compareTo(Brain b) {
//		//Test used by GA, sorts Brains in order of number of states
//		//Sorts by putting -1s to the left, 1s to the right
//		//More states = -1
//		int sa = this.getValues().size();
//		int sb = b.getValues().size();
//		if(sa > sb){
//			return -1;
//		}
//		if(sa == sb){
//			return 0;
//		}
//		//if(sa < sb){
//		return 1;
		
		int sa = this.getScore();
		int sb = b.getScore();
		
		if(sa > sb){
			return -1;
		}
		if(sa == sb){
			return 0;
		}
//		if(sa < sb){
		return 1;
	}
	
	public String toString() {
		String s = "";
		Enumeration<State> elements = states.elements();
		
		while(elements.hasMoreElements()){
			s += elements.nextElement();
			s += "\r\n";
		}
		return s;
	}
}
