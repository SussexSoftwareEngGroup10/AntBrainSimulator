package antBrain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import utilities.Logger;
import utilities.WarningEvent;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain implements Cloneable, Comparable<Brain> {
	private static final int minNumOfStates = 3;
	private static final int maxNumOfStates = 10000;
	private final HashMap<Integer, State> states;
	private int fitness;
	
	public Brain() {
		states = new HashMap<Integer, State>();
	}
	
	/**
	 * Alternative constructor used for more efficient cloning of Brain objects
	 * 
	 * @param states
	 */
	protected Brain(HashMap<Integer, State> states) {
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
	
	public Set<Integer> getKeys() {
		return states.keySet();
	}
	
	public Collection<State> getValues() {
		return states.values();
	}
	
	public int getFitness() {
		return fitness;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
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
		//Order by fitness, lowest to highest
		//so if this < b, return -1
		int sa = this.getFitness();
		int sb = b.getFitness();
		
		if(sa < sb)		return -1;
		if(sa == sb)	return 0;
		/*if(sa > sb)*/	return 1;
	}
	
	public String toString() {
		String s = "";
		//Prints in state order
		Object[] keys = (Object[]) states.keySet().toArray();
		Arrays.sort(keys);
		int i = 0;
		
		for(i = 0; i < keys.length; i++){
			s += states.get(keys[i]) + "\r\n";
		}
		return s;
	}
}
