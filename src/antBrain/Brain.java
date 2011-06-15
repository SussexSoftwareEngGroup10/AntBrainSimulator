package antBrain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utilities.IllegalArgumentEvent;
import utilities.Logger;
import utilities.WarningEvent;

/**
 * @title Brain
 * @purpose holds a list of mappings from state number to state,
 * it also behaves as a participant in a simulation, with alterable numbers
 * such as the number of wins the Brain has attained.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain extends HashMap<Integer, State> implements Comparable<Brain> {
	private static final long serialVersionUID = 1L;
	private static final int minNumOfStates = 1;
	private static final int maxNumOfStates = 10000;
	private static final int fitnessLength = 4;
	private int[] fitnesses = new int[fitnessLength];
	private int wins = 0;
	private int losses = 0;
	private int draws = 0;
	
	/**
	 * @title Brain
	 * @purpose to allow the construction of Brain objects
	 * @param initialCapacity the starting maximum number of entries the HashMap
	 * may contain
	 */
	public Brain(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * @title Brain
	 * @purpose to allow the clone() method access to an alternative, more
	 * efficient constructor, which takes different parameters.
	 * @param states a HashMap of States that will be moves into the initial
	 * Brain object's HashMap of States. 
	 */
	protected Brain(HashMap<Integer, State> states) {
		super(states);
	}
	
	/**
	 * @title getMinNumOfStates
	 * @purpose to return the minimum number of states
	 * @return the minimum number of states a brain can contain
	 */
	public static int getMinNumOfStates() {
		return minNumOfStates;
	}

	/**
	 * @title getMaxNumOfStates
	 * @purpose to return the maximum number of states
	 * @return the maximum number of states a brain can contain
	 */
	public static int getMaxNumOfStates() {
		return maxNumOfStates;
	}
	
	/**
	 * @title getFitness
	 * @purpose to return the sum of the fitnesses
	 * @return an arbitrary value used to compare brain objects
	 */
	public int getFitness() {
		int t = 0;
		for(int i : this.fitnesses){
			t += i;
		}
		return t;
	}
	
	/**
	 * @title setFitness
	 * @purpose to set the fitness of this Brain to one value, and to clear
	 * all other fitness values
	 * @param fitness the value that the fitness of this Brain will be set to
	 */
	public void setFitness(int fitness) {
		this.fitnesses[0] = fitness;
		for(int i = 1; i < this.fitnesses.length; i++){
			this.fitnesses[i] = 0;
		}
	}
	
	/**
	 * @title setFitness
	 * @purpose to set one of the fitness values of this Brain to the value given
	 * @param i the location in the fitnesses array to set
	 * @param fitness the value to set
	 */
	public void setFitness(int i, int fitness) {
		this.fitnesses[i] = fitness;
	}
	
	/**
	 * @title resetFitnesses
	 * @purpose to reset all statistics about wins, losses and fitness
	 */
	public void resetFitnesses() {
		for(int i = 0; i < this.fitnesses.length; i++){
			this.fitnesses[i] = 0;
		}
		this.wins = 0;
		this.losses = 0;
		this.draws = 0;
	}
	
	/**
	 * @title incrementWins
	 * @purpose to add one to the current number of wins.
	 */
	public void incrementWins() {
		this.wins++;
	}
	
	/**
	 * @title incrementLosses
	 * @purpose to add one to the current number of losses.
	 */
	public void incrementLosses() {
		this.losses++;
	}
	
	/**
	 * @title incrementDraws
	 * @purpose to add one to the current number of draws.
	 */
	public void incrementDraws() {
		this.draws++;
	}
	
	/**
	 * @title getWins
	 * @purpose to get the current number of wins.
	 * @return the number of wins this Brain has attained
	 */
	public int getWins() {
		return this.wins;
	}
	
	/**
	 * @title getLosses
	 * @purpose to get the current number of losses.
	 * @return the number of losses this Brain has attained
	 */
	public int getLosses() {
		return this.losses;
	}
	
	/**
	 * @title getDraws
	 * @purpose to get the current number of draws.
	 * @return the number of draws this Brain has attained
	 */
	public int getDraws() {
		return this.draws;
	}
	
	/**
	 * @title trim
	 * @purpose to remove unreachable states and compacts the brain by lowering
	 * state numbers to the minimum possible so that there are no gaps and the
	 * state numbers start at 0 and end at (the number of states - 1).
	 * @throws IllegalArgumentEvent if any of the States in the Brain are invalid
	 */
	public void trim() throws IllegalArgumentEvent {
		//True if reached from state 0
		boolean[] states = new boolean[maxNumOfStates];
		checkBranch(states, 0);
		
		ArrayList<Integer> keys = new ArrayList<Integer>(keySet());
		
		//Descending order
		Collections.sort(keys);
		Collections.reverse(keys);
		
		for(Integer key : keys){
			State state = get(key);
			//Remove null states
			if(state == null){
				removeState(key);
			}
			//Remove unreachable states
			if(!states[key]){
				removeState(key);
			}
		}
	}
	
	private void checkBranch(boolean[] states, int stateNum) {
		//set the boolean value of every state reachable from stateNum to true.
		if(get(stateNum) == null || states[stateNum]){
			return;
		}
		states[stateNum] = true;
		checkBranch(states, get(stateNum).getSt1());
		
		int command = get(stateNum).getCommand();
		if(command == 0 || command == 3 || command == 6 || command == 7){
			checkBranch(states, get(stateNum).getSt2());
		}
	}
	
	private void removeState(int removeStateNum) throws IllegalArgumentEvent {
		//Removes the state specified and compacts the states by decrementing
		//the state numbers of all following states and altering references
		//to them to reflect this.
		//Change state references in preceding states
		for(int s = 0; s < removeStateNum; s++){
			State state = get(s);
			if(state == null){
				remove(s);
			}else{
				int[] genes = state.getGenes();
				if(genes[5] > removeStateNum){
					genes[5]--;
				}
				if(genes[6] > removeStateNum){
					genes[6]--;
				}
				put(s, new State(s, genes));
			}
		}
		
		//Remove specified state
		remove(removeStateNum);
		
		//Change state references and state numbers in proceding states
		for(int s = removeStateNum; s < maxNumOfStates - 1; s++){
			State state = get(s + 1);
			if(state == null){
				remove(s);
			}else{
				int[] genes = state.getGenes();
				if(genes[5] > removeStateNum){
					genes[5]--;
				}
				if(genes[6] > removeStateNum){
					genes[6]--;
				}
				put(s, new State(s, genes));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 * 
	 * @title put
	 * @purpose adds an entry consisting of the given key-value pair to the
	 * mapping list
	 * @param key the Integer to be set as the key
	 * @param value the State to be set as the value
	 */
	@Override
	public State put(Integer key, State value) {
		if(key > maxNumOfStates){
			Logger.log(new WarningEvent("Brain key: " + key
				+ " for State: " + value + " > " + maxNumOfStates));
			return value;
		}
		return super.put(key, value);
	}
	
	/* (non-Javadoc)
	 * @see java.util.HashMap#clone()
	 * 
	 * @title clone
	 * @purpose creates and returns a Brain containing pointers to the same
	 * key-value pairs as this Brain, does NOT clone keys or values.
	 * @return a Brain containing pointers to the same key-value pairs as this
	 * Brain
	 */
	@Override
	public Brain clone() {
		return (Brain) super.clone();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#hashCode()
	 * 
	 * @title hashCode
	 * @purpose returns a value unique to the key-value pairs in this HashMap.
	 * This is used to compare HashMaps.
	 * @return a number unique to this entry configuration
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 * 
	 * @title equals
	 * @purpose tests whether two Brain objects are equal. This method is 
	 * inconsistent with natural ordering, as compareTo uses fitness,
	 * which is arbitrary and relies on randomness, where equals assesses fields
	 * @param o the object to test against this Brain
	 * @return true if the Object passed is equal to this Brain
	 */
	@Override
	public boolean equals(Object o) {
		Brain b = (Brain) o;
		//do state by state, not contains
		int i = 0;
		for(i = 0; i < maxNumOfStates; i++){
			if(this.get(i) == null || b.get(i) == null ||
				!this.get(i).equals(b.get(i))){
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * @title compareTo
	 * @purpose compares two Brain objects by their fitnesses. This method is 
	 * inconsistent with equals, as compareTo uses fitness,
	 * which is arbitrary and relies on randomness, where equals assesses fields
	 * @param b the Brain to compare to this Brain
	 * @return 1 if the Brain's fitness is greater than this Brain, 0 if they
	 * are equal, -1 if it is lesser
	 */
	@Override
	public int compareTo(Brain b) {
		//Order by fitness, lowest to highest
		int sa = this.getFitness();
		int sb = b.getFitness();
		
		if(sa < sb) return -1;
		if(sa == sb) return 0;
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 * 
	 * @title toString
	 * @purpose gets all of the States in this Brain, in order.
	 * @return a string containing all of the States in this Brain, ordered
	 * by key, one line at a time.
	 */
	@Override
	public String toString() {
		String s = "";
		//Prints in state order
		Object[] keys = super.keySet().toArray();
		Arrays.sort(keys);
		int i = 0;
		
		for(i = 0; i < keys.length; i++){
			s += get(keys[i]) + "\r\n";
		}
		return s;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		//print this object to the stream specified
		Set<Map.Entry<Integer, State>> entrySet = entrySet();
		out.writeInt(entrySet.size());
		
		for(Map.Entry<Integer, State> entry : entrySet){
			out.writeInt(entry.getKey());
			out.writeObject(entry.getValue());
		}
		
		for(int i = 0; i < fitnessLength; i++){
			out.writeInt(0);//this.fitnesses[i]);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		//attempt to read a Brain object from the stream specified
		int size = in.readInt();
		
		for(int i = 0; i < size; i++){
			put(in.readInt(), (State) in.readObject());
		}
		
		this.fitnesses = new int[fitnessLength];
		for(int i = 0; i < fitnessLength; i++){
			this.fitnesses[i] = 0;
			in.readInt();
		}
	}
}
