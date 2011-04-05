package antBrain;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain implements Cloneable, Comparable<Brain> {
	private static final int numOfStates = 10000;
	private final Hashtable<Integer, State> states = new Hashtable<Integer, State>();
	
	public Brain() {
		
	}
	
	public int getNumOfStates() {
		return states.size();
	}
	
	public static int getMaxNumOfStates() {
		return numOfStates;
	}
	
	public void setState(State state) {
		states.put(state.getStateNum(), state);
	}
	
	public State getState(int i) {
		try{
			return states.get(i);
		}catch(NullPointerException npe){
			return null;
		}
	}
	
	public Collection<State> getValues() {
		return states.values();
	}
	
	public Brain clone() {
		Brain brain = new Brain();
		int i = 0;
		
		for(i = 0; i < numOfStates; i++){
			if(states.get(i) != null){
				brain.setState(states.get(i));
			}
		}
		
		return brain;
	}
	
	public boolean equals(Object o) {
		Brain b = (Brain) o;
		//do state by state, not contains
		int i = 0;
		for(i = 0; i < numOfStates; i++){
			if(!this.getState(i).equals(b.getState(i))){
				return false;
			}
		}
		return true;
	}
	
	public int compareTo(Brain b) {
		//Sorts by putting -1s to the left, 1s to the right
		//More states = -1
		int sa = this.getValues().size();
		int sb = b.getValues().size();
		if(sa > sb){
			return -1;
		}
		if(sa == sb){
			return 0;
		}
		//if(sa < sb){
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
