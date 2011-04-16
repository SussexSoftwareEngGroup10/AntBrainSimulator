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
		this.states = new HashMap<Integer, State>();
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
		return this.states.size();
	}
	
	public static int getMinNumOfStates() {
		return minNumOfStates;
	}
	
	public static int getMaxNumOfStates() {
		return maxNumOfStates;
	}
	
	public void setState(State state) {
		this.states.put(state.getStateNum(), state);
	}
	
	public State getState(int i) {
		try{
			return this.states.get(i);
		}catch(NullPointerException npe){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new WarningEvent("Null state " + i + " returned in Brain"));
			}
			return null;
		}
	}
	
	public Set<Integer> getKeys() {
		return this.states.keySet();
	}
	
	public Collection<State> getValues() {
		return this.states.values();
	}
	
	public int getFitness() {
		return this.fitness;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public Brain clone() {
		return new Brain(this.states);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
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
	
	@Override
	public int compareTo(Brain b) {
		//Order by fitness, lowest to highest
		//so if this < b, return -1
		int sa = this.getFitness();
		int sb = b.getFitness();
		
		if(sa < sb)		return -1;
		if(sa == sb)	return 0;
		/*if(sa > sb)*/	return 1;
	}
	
	@Override
	public String toString() {
		String s = "";
		//Prints in state order
		Object[] keys = this.states.keySet().toArray();
		Arrays.sort(keys);
		int i = 0;
		
		for(i = 0; i < keys.length; i++){
			s += this.states.get(keys[i]) + "\r\n";
		}
		return s;
	}
}
