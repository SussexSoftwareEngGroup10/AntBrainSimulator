package antBrain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import utilities.IllegalArgumentEvent;
import utilities.Logger;

/*
Instruction set:
Sense sensedir st1 st2 cond	    	Go to state st1 if cond holds in sensedir;
										and to state st2 otherwise.
Mark i st	    						Set mark i in current cell and go to st.
Unmark i st	    					Clear mark i in current cell and go to st.
PickUp st1 st2	    				Pick up food from current cell and go to st1;
										go to st2 if there is no food in the current cell.
Drop st	    						Drop food in current cell and go to st.
Turn lr st	    					Turn left or right and go to st.
Move st1 st2	    					Move forward and go to st1;
										go to st2 if the cell ahead is blocked.
Flip p st1 st2	    				Choose a random number x from 0 to p-1;
										go to st1 if x=0 and st2 otherwise.
 */

/*
Example code, as it is found in a file:
Sense Ahead 1 3 Food  ; state 0:  [SEARCH] is there food in front of me?
Move 2 0              ; state 1:  YES: move onto food (return to state 0 on failure)
PickUp 8 0            ; state 2:       pick up food and jump to state 8 
                           			(or 0 on failure)
Flip 3 4 5            ; state 3:  NO: choose whether to...
Turn Left 0           ; state 4:      turn left and return to state 0
Flip 2 6 7            ; state 5:      ... or ...
Turn Right 0          ; state 6:      turn right and return to state 0
Move 0 3              ; state 7:      ... or move forward and return to state 0 
                          			(or 3 on failure)
Sense Ahead 9 11 Home ; state 8:  [GO HOME] is the cell in front of me my 
										anthill?
Move 10 8             ; state 9:  YES: move onto anthill
Drop 0                ; state 10:     drop food and return to searching
Flip 3 12 13          ; state 11: NO: choose whether to...
Turn Left 8           ; state 12:     turn left and return to state 8
Flip 2 14 15          ; state 13:     ...or...
Turn Right 8          ; state 14:     turn right and return to state 8
Move 8 11             ; state 15:     ...or move forward and return to 
										state 8
 */

/**
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
	 * Constructer used by the Genetic Algorithm, gives increased efficiency
	 * 
	 * @param stateNum
	 * @param genes
	 */
	public State(int stateNum, int[] genes) {
		this.stateNum = stateNum;
		
		this.command = toCommand(genes[0]);
		switch(this.command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			if(genes[1] < 0 || genes[1] > 3){
				Logger.log(new IllegalArgumentEvent("Illegal SenseDir ordinal " +
					"argument in State constructor"));
				this.senseDir = toSenseDir(0);
			}else{
				this.senseDir = toSenseDir(genes[1]);
			}
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 argument " +
					"in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			if(genes[6] < 0 || genes[6] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 argument " +
					"in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = genes[6];
			}
			if(genes[7] < 0 || genes[7] > 9){
				Logger.log(new IllegalArgumentEvent("Illegal Condition ordinal " +
					"argument in State constructor"));
				this.condition = toCondition(0);
			}else{
				this.condition = toCondition(genes[7]);
			}
			if(this.condition == Condition.MARKER){
				if(genes[8] < 0 || genes[8] > 5){
					Logger.log(new IllegalArgumentEvent("Illegal SenseMarker " +
						"argument in State constructor"));
					this.senseMarker = 0;
				}else{
					this.senseMarker = genes[8];
				}
			}else{
				this.senseMarker = -1;
			}
			break;
		//Mark marker st1
		case MARK:
			this.senseDir = null;
			this.turnDir = null;
			if(genes[3] < 0 || genes[3] > 5){
				Logger.log(new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor"));
				this.marker = 0;
			}else{
				this.marker = genes[3];
			}
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Unmark marker st1
		case UNMARK:
			this.senseDir = null;
			this.turnDir = null;
			if(genes[3] < 0 || genes[3] > 5){
				Logger.log(new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor"));
				this.marker = 0;
			}else{
				this.marker = genes[3];
			}
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			if(genes[6] < 0 || genes[6] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = genes[6];
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Turn turnDir st1
		case TURN:
			this.senseDir = null;
			if(genes[2] < 0 || genes[2] > 1){
				Logger.log(new IllegalArgumentEvent("Illegal TurnDir ordinal " +
					"argument in State constructor"));
				this.turnDir = toTurnDir(0);
			}else{
				this.turnDir = toTurnDir(genes[2]);
			}
			this.marker = -1;
			this.p = -1;
			if(genes[5] < 0 || genes[5] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			if(genes[6] < 0 || genes[6] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = genes[6];
			}
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			if(genes[4] < 2 || genes[4] > 10){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.p = 2;
			}else{
				this.p = genes[4];
			}
			if(genes[5] < 0 || genes[5] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = genes[5];
			}
			if(genes[6] < 0 || genes[6] > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = genes[6];
			}
			this.condition = null;
			this.senseMarker = -1;
			break;
		//This should never be reached
		default:
			Logger.log(new IllegalArgumentEvent("Illegal Command ordinal " +
				"argument in State constructor"));
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			this.st1 = -1;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
		}
	}
	
	/**
	 * General constructer used when reading in States from file
	 * 
	 * @param stateNum
	 * @param stateString
	 */
	public State(int stateNum, String stateString) {
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
			Logger.log(new IllegalArgumentEvent("Illegal Command " +
				"argument in State constructor"));
		}finally{
			this.command = (Command) eVal;
		}
		
		switch(this.command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			try{
				eVal = SenseDir.valueOf(terms[1].trim().toUpperCase());
			}catch(IllegalArgumentException e){
				Logger.log(new IllegalArgumentEvent("Illegal SenseDir ordinal " +
					"argument in State constructor", e));
			}finally{
				this.senseDir = (SenseDir) eVal;
			}
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
			iVal = Integer.parseInt(terms[3]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = iVal;
			}
			try{
				eVal = Condition.valueOf(terms[4].trim().toUpperCase());
			}catch(IllegalArgumentException e){
				Logger.log(new IllegalArgumentEvent("Illegal Condition ordinal " +
					"argument in State constructor", e));
			}finally{
				this.condition = (Condition) eVal;
			}
			if(this.condition == Condition.MARKER){
				iVal = Integer.parseInt(terms[5]);
				if(iVal < 0 || iVal > 5){
					Logger.log(new IllegalArgumentEvent("Illegal senseMarker " +
						"argument in State constructor"));
					this.senseMarker = 0;
				}else{
					this.senseMarker = iVal;
				}
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
				Logger.log(new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor"));
				this.marker = 0;
			}else{
				this.marker = iVal;
			}
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal marker " +
					"argument in State constructor"));
				this.marker = 0;
			}else{
				this.marker = iVal;
			}
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = iVal;
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal TurnDir ordinal " +
					"argument in State constructor", e));
			}finally{
				this.turnDir = (TurnDir) eVal;
			}
			this.marker = -1;
			this.p = -1;
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
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
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = iVal;
			}
			this.condition = null;
			this.senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			iVal = Integer.parseInt(terms[1]);
			if(iVal < 2 || iVal > 10){
				Logger.log(new IllegalArgumentEvent("Illegal p " +
					"argument in State constructor"));
				this.p = 0;
			}else{
				this.p = iVal;
			}
			iVal = Integer.parseInt(terms[2]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st1 " +
					"argument in State constructor"));
				this.st1 = 0;
			}else{
				this.st1 = iVal;
			}
			iVal = Integer.parseInt(terms[3]);
			if(iVal < 0 || iVal > max){
				Logger.log(new IllegalArgumentEvent("Illegal st2 " +
					"argument in State constructor"));
				this.st2 = 0;
			}else{
				this.st2 = iVal;
			}
			this.condition = null;
			this.senseMarker = -1;
			break;
		//This should never be reached
		default:
			Logger.log(new IllegalArgumentEvent("Illegal Command " +
				"argument in State constructor"));
			this.senseDir = null;
			this.turnDir = null;
			this.marker = -1;
			this.p = -1;
			this.st1 = -1;
			this.st2 = -1;
			this.condition = null;
			this.senseMarker = -1;
		}
	}
	
	public static int[] getValues(int states) {
		int[] values = new int[9];
		int i = 0;
		for(i = 0; i < 9; i++){
			values[i] = getValue(states, i);
		}
		return values;
	}
	
	public static int getValue(int states, int field) {
		switch(field){
		case 0:
			return 8;
		case 1:
			return 4;
		case 2:
			return 2;
		case 3:
			return 6;
		case 4:
			return 10;
		case 5:
			return states;
		case 6:
			return states;
		case 7:
			return 10;
		case 8:
			return 6;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal field " +
				"argument in State constructor"));
			return -1;
		}
	}
	
	private static Command toCommand(int i) {
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
			Logger.log(new IllegalArgumentEvent("Illegal Command ordinal " +
				"argument in State toCommand"));
		}
		return null;
	}
	
	private static SenseDir toSenseDir(int i) {
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
			Logger.log(new IllegalArgumentEvent("Illegal senseDir ordinal " +
				"argument in State toSenseDir"));
		}
		return null;
	}
	
	private static TurnDir toTurnDir(int i) {
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
			Logger.log(new IllegalArgumentEvent("Illegal turnDir ordinal " +
				"argument in State toSenseDir"));
		}
		return null;
	}
	
	private static Condition toCondition(int i) {
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
			Logger.log(new IllegalArgumentEvent("Illegal Condition ordinal " +
				"argument in State toSenseDir"));
		}
		return null;
	}
	
	/**
	 * @return the values of all int fields, and the ordinal of all enum values
	 * returns -1 if int or enum is not in use by state
	 * e.g. when command == SENSE, turnDir returns -1, as sense does not have a turnDir
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
	
	public int getCommand() {
		if(this.command == null){
			return -1;
		}
		return this.command.ordinal();
	}
	
	public int getSenseDir() {
		if(this.senseDir == null){
			return -1;
		}
		return this.senseDir.ordinal();
	}
	
	public int getTurnDir() {
		if(this.turnDir == null){
			return -1;
		}
		return this.turnDir.ordinal();
	}
	
	public int getMarker() {
		return this.marker;
	}
	
	public int getP() {
		return this.p;
	}
	
	public int getSt1() {
		return this.st1;
	}
	
	public int getSt2() {
		return this.st2;
	}
	
	public int getCondition() {
		if(this.condition == null){
			return -1;
		}
		return this.condition.ordinal();
	}
	
	public int getSenseMarker() {
		return this.senseMarker;
	}
	
	public int getStateNum() {
		return this.stateNum;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(this.toString().equals(o.toString())){
			return true;
		}
		return false;
	}

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
	
	//The getters and setters for the enums allow for null values
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(getCommand());
		out.writeInt(getSenseDir());
		out.writeInt(getTurnDir());
		out.writeInt(this.marker);
		out.writeInt(this.p);
		out.writeInt(this.st1);
		out.writeInt(this.st2);
		out.writeInt(getCondition());
		out.writeInt(this.senseMarker);
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		this.command = toCommand(in.readInt());
		this.senseDir = toSenseDir(in.readInt());
		this.turnDir = toTurnDir(in.readInt());
		this.marker = in.readInt();
		this.p = in.readInt();
		this.st1 = in.readInt();
		this.st2 = in.readInt();
		this.condition = toCondition(in.readInt());
		this.senseMarker = in.readInt();
	}
}
