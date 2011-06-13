package antBrain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import utilities.IllegalArgumentEvent;
import utilities.Logger;

/**
 * @title State
 * @purpose holds an ant brain instruction and a list of its parameters.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class State implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int max = Brain.getMaxNumOfStates();
	
	//Remember to change random state generation in GeneticAlgorithm
	//and other methods if any enums are altered
	protected enum Command { SENSE, MARK, UNMARK, PICKUP, DROP, TURN, MOVE, FLIP }
	protected enum SenseDir { HERE, AHEAD, LEFTAHEAD, RIGHTAHEAD }
	protected enum TurnDir { LEFT, RIGHT }
	protected enum Condition { FRIEND, FOE, FRIENDWITHFOOD, FOEWITHFOOD,
		FOOD, ROCK, MARKER, FOEMARKER, HOME, FOEHOME }
	
	//All final, as the first value given should never be overridden
	//Default values are null and -1
	private Command command;
	private SenseDir senseDir;
	private TurnDir turnDir;
	private int marker;
	private int p;
	private int st1;
	private int st2;
	private Condition condition;
	private int senseMarker;
	
	private int stateNum;
	
	/**
	 * @title State
	 * @purpose constructor used by the Genetic Algorithm, gives increased
	 * efficiency.
	 * @param stateNum the number of this state
	 * @param genes the values of the command and its parameters for this State
	 * @throws IllegalArgumentEvent if any of the genes are outside their range
	 */
	public State(int stateNum, int[] genes) throws IllegalArgumentEvent {
		this.stateNum = stateNum;
		
		this.command = toCommand(genes[0]);
		switch(this.command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			if(genes[1] < 0 || genes[1] > 3){
				throw new IllegalArgumentEvent("Illegal SenseDir ordinal " +
					"argument in State constructor");
			}
			this.senseDir = toSenseDir(genes[1]);
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 argument " +
					"in State constructor");
			}
			this.st1 = genes[5];
			if(genes[6] < 0 || genes[6] > max){
				throw new IllegalArgumentEvent("Illegal st2 argument " +
					"in State constructor");
			}
			this.st2 = genes[6];
			if(genes[7] < 0 || genes[7] > 9){
				throw new IllegalArgumentEvent("Illegal Condition ordinal " +
					"argument in State constructor");
			}
			this.condition = toCondition(genes[7]);
			if(this.condition == Condition.MARKER){
				if(genes[8] < 0 || genes[8] > 5){
					throw new IllegalArgumentEvent("Illegal SenseMarker " +
						"argument in State constructor");
				}
				this.senseMarker = genes[8];
			}else{
				this.senseMarker = -1;
			}
			break;
		//Mark marker st1
		case MARK:
			this.senseDir = null;
			this.turnDir = null;
			if(genes[3] < 0 || genes[3] > 5){
				throw new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor");
			}
			this.marker = genes[3];
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Unmark marker st1
		case UNMARK:
			this.senseDir = null;
			this.turnDir = null;
			if(genes[3] < 0 || genes[3] > 5){
				throw new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor");
			}
			this.marker = genes[3];
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//PickUp st1 st2
		case PICKUP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			if(genes[6] < 0 || genes[6] > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = genes[6];
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Drop st1
		case DROP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Turn turnDir st1
		case TURN:
			this.senseDir = null;
			if(genes[2] < 0 || genes[2] > 1){
				throw new IllegalArgumentEvent("Illegal TurnDir ordinal " +
					"argument in State constructor");
			}
			this.turnDir = toTurnDir(genes[2]);
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Move st1 st2
		case MOVE:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			if(genes[6] < 0 || genes[6] > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = genes[6];
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			if(genes[4] < 1){
				throw new IllegalArgumentEvent("Illegal p " +
					"argument in State constructor");
			}
			this.p = genes[4];
			if(genes[5] < 0 || genes[5] > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = genes[5];
			if(genes[6] < 0 || genes[6] > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = genes[6];
			this.condition = null;
			this.senseMarker = -1;
			break;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal Command ordinal " +
				"argument in State constructor");
		}
	}
	
	/**
	 * @title
	 * @purpose constructor used when reading in States from file.
	 * @param stateNum the number of this State
	 * @param stateString the String to be converted into a State object
	 * @throws IllegalArgumentEvent if the stateString is invalid
	 */
	public State(int stateNum, String stateString) throws IllegalArgumentEvent {
		@SuppressWarnings("rawtypes")
		Enum eVal = null;
		int iVal = 0;
		
		this.stateNum = stateNum;
		
		//Given: "Sense Ahead 1 3 Food"
		String[] terms = stateString.split(" ");
		//Gives: [Sense,Ahead,1,3,Food]
		
		try{
			eVal = Command.valueOf(terms[0].trim().toUpperCase());
		}catch(IllegalArgumentException e){
			throw new IllegalArgumentEvent("Illegal Command " +
				"argument in State constructor: " + e.getMessage(), e);
		}finally{
			this.command = (Command) eVal;
		}
		
		switch(this.command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			try{
				eVal = SenseDir.valueOf(terms[1].trim().toUpperCase());
			}catch(IllegalArgumentException e){
				throw new IllegalArgumentEvent("Illegal SenseDir ordinal " +
					"argument in State constructor: " + e.getMessage(), e);
			}finally{
				this.senseDir = (SenseDir) eVal;
			}
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			iVal = Integer.parseInt(terms[3]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = iVal;
			try{
				eVal = Condition.valueOf(terms[4].trim().toUpperCase());
			}catch(IllegalArgumentException e){
				throw new IllegalArgumentEvent("Illegal Condition ordinal " +
					"argument in State constructor: " + e.getMessage(), e);
			}finally{
				this.condition = (Condition) eVal;
			}
			if(this.condition == Condition.MARKER){
				iVal = Integer.parseInt(terms[5]);
				if(iVal < 0 || iVal > 5){
					throw new IllegalArgumentEvent("Illegal senseMarker " +
						"argument in State constructor");
				}
				this.senseMarker = iVal;
			}else{
				this.senseMarker = -1;
			}
			break;
		//Mark marker st1
		case MARK:
			this.senseDir = null;
			this.turnDir = null;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 0 || iVal > 5){
				throw new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor");
			}
			this.marker = iVal;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Unmark marker st1
		case UNMARK:
			this.senseDir = null;
			this.turnDir = null;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 0 || iVal > 5){
				throw new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor");
			}
			this.marker = iVal;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//PickUp st1 st2
		case PICKUP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = iVal;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Drop st1
		case DROP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Turn turnDir st1
		case TURN:
			this.senseDir = null;
			try{
				eVal = TurnDir.valueOf(terms[1].trim().toUpperCase());
			}catch(IllegalArgumentException e){
				throw new IllegalArgumentEvent("Illegal TurnDir ordinal " +
					"argument in State constructor: " + e.getMessage(), e);
			}finally{
				this.turnDir = (TurnDir) eVal;
			}
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Move st1 st2
		case MOVE:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = iVal;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 1){
				throw new IllegalArgumentEvent("Illegal p " +
					"argument in State constructor");
			}
			this.p = iVal;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor");
			}
			this.st1 = iVal;
			iVal = Integer.parseInt(terms[3]);
			if(iVal < 0 || iVal > max){
				throw new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor");
			}
			this.st2 = iVal;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal Command " +
				"argument in State constructor");
			}
	}
	
	/**
	 * @title getValues
	 * @purpose returns an array of maximum values for each gene
	 * @param states maximum stateNum for st1 and st2
	 * @return number of values for each part of a state
	 * @throws IllegalArgumentEvent if getValue fails
	 */
	public static int[] getValues(int states) throws IllegalArgumentEvent {
		int[] values = new int[9];
		int i = 0;
		for(i = 0; i < 9; i++){
			values[i] = getValue(states, i);
		}
		return values;
	}
	
	/**
	 * @title getValue
	 * @purpose to get the limit for one of the fields of the State
	 * @param states number of states in the Brain
	 * @param field index of the field to find the limit of
	 * @return the limit for the field specified
	 * @throws IllegalArgumentEvent if field is less than 0 or greater than 9
	 */
	public static int getValue(int states, int field) throws IllegalArgumentEvent {
		switch(field){
		//command
		case 0:
			return 8;
		//senseDir
		case 1:
			return 4;
		//turnDir
		case 2:
			return 2;
		//marker
		case 3:
			return 6;
		//p
		case 4:
			return 10;
		//st1
		case 5:
			return states;
		//st2
		case 6:
			return states;
		//condition
		case 7:
			return 10;
		//senseMarker
		case 8:
			return 6;
		default:
			throw new IllegalArgumentEvent("Illegal field " +
				"argument in State constructor");
		}
	}
	
	/**
	 * @param i
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private static Command toCommand(int i) throws IllegalArgumentEvent {
		//SENSE, MARK, UNMARK, PICKUP, DROP, TURN, MOVE, FLIP
		switch(i){
		case -1:
			return null;
		case 0:
			return Command.SENSE;
		case 1:
			return Command.MARK;
		case 2:
			return Command.UNMARK;
		case 3:
			return Command.PICKUP;
		case 4:
			return Command.DROP;
		case 5:
			return Command.TURN;
		case 6:
			return Command.MOVE;
		case 7:
			return Command.FLIP;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal Command ordinal " +
				"argument in State toCommand");
		}
	}
	
	/**
	 * @param i
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private static SenseDir toSenseDir(int i) throws IllegalArgumentEvent {
		//HERE, AHEAD, LEFTAHEAD, RIGHTAHEAD
		switch(i){
		case -1:
			return null;
		case 0:
			return SenseDir.HERE;
		case 1:
			return SenseDir.AHEAD;
		case 2:
			return SenseDir.LEFTAHEAD;
		case 3:
			return SenseDir.RIGHTAHEAD;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal senseDir ordinal " +
				"argument in State toSenseDir");
		}
	}
	
	/**
	 * @param i
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private static TurnDir toTurnDir(int i) throws IllegalArgumentEvent {
		//LEFT, RIGHT
		switch(i){
		case -1:
			return null;
		case 0:
			return TurnDir.LEFT;
		case 1:
			return TurnDir.RIGHT;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal turnDir ordinal " +
				"argument in State toSenseDir");
		}
	}
	
	/**
	 * @param i
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private static Condition toCondition(int i) throws IllegalArgumentEvent {
		//FRIEND, FOE, FRIENDWITHFOOD, FOEWITHFOOD,	FOOD, ROCK, MARKER, FOEMARKER, HOME, FOEHOME
		switch(i){
		case -1:
			return null;
		case 0:
			return Condition.FRIEND;
		case 1:
			return Condition.FOE;
		case 2:
			return Condition.FRIENDWITHFOOD;
		case 3:
			return Condition.FOEWITHFOOD;
		case 4:
			return Condition.FOOD;
		case 5:
			return Condition.ROCK;
		case 6:
			return Condition.MARKER;
		case 7:
			return Condition.FOEMARKER;
		case 8:
			return Condition.HOME;
		case 9:
			return Condition.FOEHOME;
		//This should never be reached
		default:
			throw new IllegalArgumentEvent("Illegal Condition ordinal " +
				"argument in State toSenseDir");
		}
	}
	
	/**
	 * @title getGenes
	 * @purpose to get an encoding of the instructions in this State
	 * @return the values of all int fields, and the ordinal of all enum values
	 * returns -1 if int or enum is not in use by state
	 * e.g. when command == SENSE, turnDir returns -1, as sense does not have a
	 * turnDir
	 */
	public int[] getGenes() {
		int[] genes = new int[9];
		genes[0] = this.command.ordinal();
		if(this.senseDir == null){
			genes[1] = -1;
		}else{
			genes[1] = this.senseDir.ordinal();
		}
		if(this.turnDir == null){
			genes[2] = -1;
		}else{
			genes[2] = this.turnDir.ordinal();
		}
		genes[3] = this.marker;
		genes[4] = this.p;
		genes[5] = this.st1;
		genes[6] = this.st2;
		if(this.condition == null){
			genes[7] = -1;
		}else{
			genes[7] = this.condition.ordinal();
		}
		genes[8] = this.senseMarker;
		return genes;
	}
	
	/**
	 * @title getCommand
	 * @purpose to get the int representing the command part of the instruction
	 * @return the ordinal of the command part of the instruction
	 */
	public int getCommand() {
		if(this.command == null){
			return -1;
		}
		return this.command.ordinal();
	}
	
	/**
	 * @title getSenseDir
	 * @purpose to get the int representing the senseDir part of the instruction
	 * @return the ordinal of the senseDir part of the instruction
	 */
	public int getSenseDir() {
		if(this.senseDir == null){
			return -1;
		}
		return this.senseDir.ordinal();
	}
	
	/**
	 * @title getTurnDir
	 * @purpose to get the int representing the turnDir part of the instruction
	 * @return the ordinal of the turnDir part of the instruction
	 */
	public int getTurnDir() {
		if(this.turnDir == null){
			return -1;
		}
		return this.turnDir.ordinal();
	}
	
	/**
	 * @title getMarker
	 * @purpose to get the int representing the marker part of the instruction
	 * @return the ordinal of the marker part of the instruction
	 */
	public int getMarker() {
		return this.marker;
	}
	
	/**
	 * @title getP
	 * @purpose to get the int representing the p part of the instruction
	 * @return the ordinal of the p part of the instruction
	 */
	public int getP() {
		return this.p;
	}
	
	/**
	 * @title getSt1
	 * @purpose to get the int representing the st1 part of the instruction
	 * @return the ordinal of the st1 part of the instruction
	 */
	public int getSt1() {
		return this.st1;
	}
	
	/**
	 * @title getSt2
	 * @purpose to get the int representing the st2 part of the instruction
	 * @return the ordinal of the st2 part of the instruction
	 */
	public int getSt2() {
		return this.st2;
	}
	
	/**
	 * @title getCondition
	 * @purpose to get the int representing the condition part of the instruction
	 * @return the ordinal of the condition part of the instruction
	 */
	public int getCondition() {
		if(this.condition == null){
			return -1;
		}
		return this.condition.ordinal();
	}
	
	/**
	 * @title getSenseMarker
	 * @purpose to get the int representing the senseMarker part of the instruction
	 * @return the ordinal of the senseMarker part of the instruction
	 */
	public int getSenseMarker() {
		return this.senseMarker;
	}
	
	/**
	 * @title setStateNum
	 * @purpose to set the stateNum of this State
	 * @param stateNum the number to set the stateNum of this State to
	 */
	protected void setStateNum(int stateNum) {
		this.stateNum = stateNum;
	}
	
	/**
	 * @title getStateNum
	 * @purpose get the stateNum of this State
	 * @return an int representing the stateNum of this State
	 */
	public int getStateNum() {
		return this.stateNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * 
	 * @title hashCode
	 * @purpose returns a value unique to the instruction set in this State.
	 * This is used to compare States
	 * @return a number unique to this instruction
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @title equals
	 * @purpose tests whether two State objects are equal.
	 * @param o the object to test against this State
	 * @return true if the Object passed is equal to this State
	 */
	@Override
	public boolean equals(Object o) {
		if(this.toString().equals(o.toString())){
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * @title toString
	 * @purpose gets all of the instructions in this State, in order.
	 * @return a string containing all of the valid instructions in this State,
	 */
	@Override
	public String toString() {
		String s = "";
		s += this.command + " ";
		
		switch(this.command){
		//Sense senseDir st1 st2 condition
		case SENSE:
			s += this.senseDir + " ";
			s += this.st1 + " ";
			s += this.st2 + " ";
			s += this.condition + " ";
			if(this.condition == Condition.MARKER){
				s += this.senseMarker + " ";
			}
			break;
		//Mark marker st1
		case MARK:
			s += this.marker + " ";
			s += this.st1 + " ";
			break;
		//Unmark marker st1
		case UNMARK:
			s += this.marker + " ";
			s += this.st1 + " ";
			break;
		//PickUp st1 st2
		case PICKUP:
			s += this.st1 + " ";
			s += this.st2 + " ";
			break;
		//Drop st1
		case DROP:
			s += this.st1 + " ";
			break;
		//Turn turnDir st1
		case TURN:
			s += this.turnDir + " ";
			s += this.st1 + " ";
			break;
		//Move st1 st2
		case MOVE:
			s += this.st1 + " ";
			s += this.st2 + " ";
			break;
		//Flip p st1 st2
		case FLIP:
			s += this.p + " ";
			s += this.st1 + " ";
			s += this.st2 + " ";
			break;
		//This should never be reached
		default:
			Logger.log(new IllegalArgumentEvent("Illegal Command Argument in State toString"));
		}
		//So far s == "SENSE AHEAD 1 3 FOOD "
		
		while(s.length() < 45){
			s += " ";
		}
		
		s += "; STATE " + this.stateNum + ": ";
		
		while(s.length() < 60){
			s += " ";
		}
		
		//Would need to change order of states to fit descriptions
		//May be impossible for some states
		//One state may not have enough information,
		//description may have to be written by Brain
		//Too much like hard work, not needed
		s += "[description of state]";
		
		return s;
	}
	
	/**
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		//The getters and setters for the enums allow for null values
		out.writeInt(getCommand());
		out.writeInt(getSenseDir());
		out.writeInt(getTurnDir());
		out.writeInt(this.marker);
		out.writeInt(this.p);
		out.writeInt(this.st1);
		out.writeInt(this.st2);
		out.writeInt(getCondition());
		out.writeInt(this.senseMarker);
		out.writeInt(this.stateNum);
	}
	
	/**
	 * @param in
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws IOException {
		try{
		this.command = toCommand(in.readInt());
		this.senseDir = toSenseDir(in.readInt());
		this.turnDir = toTurnDir(in.readInt());
		this.marker = in.readInt();
		this.p = in.readInt();
		this.st1 = in.readInt();
		this.st2 = in.readInt();
		this.condition = toCondition(in.readInt());
		this.senseMarker = in.readInt();
		this.stateNum = in.readInt();
		}catch(IllegalArgumentEvent e){
			Logger.log(e);
		}
	}
}
