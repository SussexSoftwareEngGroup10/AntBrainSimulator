package antBrain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Brain extends HashMap<Integer, State> implements Comparable<Brain> {
	private static final long serialVersionUID = 1L;
	private static final int minNumOfStates = 1;
	private static final int maxNumOfStates = 10000;
	private int absoluteFitness = 0;
	private int relativeFitness = 0;
	
	public Brain(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Alternative constructor used for more efficient cloning of Brain objects
	 * 
	 * @param states
	 */
	protected Brain(HashMap<Integer, State> states) {
		super(states);
	}
	
	public static int getMinNumOfStates() {
		return minNumOfStates;
	}
	
	public static int getMaxNumOfStates() {
		return maxNumOfStates;
	}
	
	public int getFitness() {
		return this.absoluteFitness + this.relativeFitness;
	}
	
	public void setAbsoluteFitness(int absoluteFitness) {
		this.absoluteFitness = absoluteFitness;
	}
	
	public void setRelativeFitness(int relativeFitness) {
		this.relativeFitness = relativeFitness;
	}
	
	public void trim() {
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
	
	//Recursive method that checks for unreachable states
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
	
	private void removeState(int removeStateNum) {
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
	public Brain clone() {
		return (Brain) super.clone();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	//Inconsistent with natural ordering, as compareTo uses fitness,
	//which is arbitrary and relies on randomness, where equals assesses fields
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
	
	@Override
	public int compareTo(Brain b) {
		//Order by fitness, lowest to highest
		//so if this < b, return -1
		int sa = this.getFitness();
		int sb = b.getFitness();
		
		if(sa < sb) return -1;
		if(sa == sb) return 0;
		return 1;
	}
	
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
		out.writeInt(size());
		for(Integer key : keySet()){
			out.writeInt(key);
			out.writeObject(get(key));
		}
		out.writeInt(this.absoluteFitness);
		out.writeInt(this.relativeFitness);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		for(int i = 0; i < size; i++){
			put(in.readInt(), (State) in.readObject());
		}
		setAbsoluteFitness(in.readInt());
		setRelativeFitness(in.readInt());
	}
}
