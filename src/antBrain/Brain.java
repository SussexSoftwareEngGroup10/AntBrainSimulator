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
 * @change_log 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain extends HashMap<Integer, State> implements Comparable<Brain> {
	private static final long serialVersionUID = 1L;
	private static final int minNumOfStates = 1;
	private static final int maxNumOfStates = 10000;
	private static final int fitnessLength = 4;
	private int[] fitnesses;
	private int wins = 0;
	private int losses = 0;
	private int draws = 0;
	
	/**
	 * @param initialCapacity
	 */
	public Brain(int initialCapacity) {
		super(initialCapacity);
		this.fitnesses = new int[fitnessLength];
	}
	
	/**
	 * Alternative constructor used for more efficient cloning of Brain objects
	 * 
	 * @param states
	 */
	protected Brain(HashMap<Integer, State> states) {
		super(states);
		this.fitnesses = new int[fitnessLength];
	}
	
	/**
	 * @return the minimum number of states a brain can contain
	 */
	public static int getMinNumOfStates() {
		return minNumOfStates;
	}

	/**
	 * @return the maximum number of states a brain can contain
	 */
	public static int getMaxNumOfStates() {
		return maxNumOfStates;
	}
	
	/**
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
	 * @param fitness
	 */
	public void setFitness(int fitness) {
		this.fitnesses[0] = fitness;
	}
	
	/**
	 * @param i
	 * @param fitness
	 */
	public void setFitness(int i, int fitness) {
		this.fitnesses[i] = fitness;
	}
	
	/**
	 * 
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
	 * 
	 */
	public void incrementWins() {
		this.wins++;
	}
	
	/**
	 * 
	 */
	public void incrementLosses() {
		this.losses++;
	}
	
	/**
	 * 
	 */
	public void incrementDraws() {
		this.draws++;
	}
	
	/**
	 * @return
	 */
	public int getWins() {
		return this.wins;
	}
	
	/**
	 * @return
	 */
	public int getLosses() {
		return this.losses;
	}
	
	/**
	 * @return
	 */
	public int getDraws() {
		return this.draws;
	}
	
	/**
	 * Removes unreachable states and compacts the brain by lowering state numbers
	 * to the minimum possible so that there are no gaps and the state numbers
	 * start at 0 and end at (the number of states - 1)
	 * @throws IllegalArgumentEvent 
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
	
	/**
	 * Sets the boolean value of every state reachable from stateNum to true
	 * @param states
	 * @param stateNum
	 */
	private void checkBranch(boolean[] states, int stateNum) {
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
	
	/**
	 * Removes the state specified and compacts the states by decrementing the state
	 * numbers of all following states and altering references to them to reflect this
	 * @param removeStateNum
	 * @throws IllegalArgumentEvent 
	 */
	private void removeState(int removeStateNum) throws IllegalArgumentEvent {
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
	 */
	@Override
	public Brain clone() {
		return (Brain) super.clone();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 * 
	 * Inconsistent with natural ordering, as compareTo uses fitness,
	 * which is arbitrary and relies on randomness, where equals assesses fields
	 */
	@Override
	public boolean equals(Object o) {
		Brain b = (Brain) o;
		//do state by state, not contains
		int i = 0;
		for(i = 0; i < maxNumOfStates; i++){
			try{
				if(!this.get(i).equals(b.get(i))){
					return false;
				}
			}catch(NullPointerException e){
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
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
	
	/**
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		Set<Map.Entry<Integer, State>> entrySet = entrySet();
		out.writeInt(entrySet.size());
		
		for(Map.Entry<Integer, State> entry : entrySet){
			out.writeInt(entry.getKey());
			out.writeObject(entry.getValue());
		}
		
		for(int i = 0; i < fitnessLength; i++){
			out.writeInt(this.fitnesses[i]);
		}
	}
	
	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		
		for(int i = 0; i < size; i++){
			put(in.readInt(), (State) in.readObject());
		}
		
		this.fitnesses = new int[fitnessLength];
		for(int i = 0; i < fitnessLength; i++){
			this.fitnesses[i] = in.readInt();
		}
	}
}
