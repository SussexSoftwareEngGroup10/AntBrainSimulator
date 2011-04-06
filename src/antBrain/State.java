package antBrain;

import utilities.InvalidInputWarningEvent;
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
public class State {
	//Remember to change random state generation in GA and other methods if any enums are altered
	enum Command { SENSE, MARK, UNMARK, PICKUP, DROP, TURN, MOVE, FLIP };
	enum SenseDir { HERE, AHEAD, LEFTAHEAD, RIGHTAHEAD };
	enum TurnDir { LEFT, RIGHT };
	enum Condition { FRIEND, FOE, FRIENDWITHFOOD, FOEWITHFOOD,
		FOOD, ROCK, MARKER, FOEMARKER, HOME, FOEHOME }
	
	//All  final, as the first value given should never be overridden
	//Default  values are null and -1
	private final Command command;
	private final SenseDir senseDir;
	private final TurnDir turnDir;
	private final int marker;
	private final int p;
	private final int st1;
	private final int st2;
	private final Condition condition;
	private final int senseMarker;
	
	private int stateNum = -1;
	
	public State(int stateNum, int[] genes) throws InvalidInputWarningEvent {
		this.stateNum = stateNum;
		
		command = toCommand(genes[0]);
		switch(command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			senseDir = toSenseDir(genes[1]);
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = genes[5];
			st2 = genes[6];
			condition = toCondition(genes[7]);
			if(condition == Condition.MARKER){
				senseMarker = genes[8];
			}else{
				senseMarker = -1;
			}
			break;
		//Mark marker st1
		case MARK:
			senseDir = null;
			turnDir = null;
			marker = genes[3];
			p = -1;
			st1 = genes[5];
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Unmark marker st1
		case UNMARK:
			senseDir = null;
			turnDir = null;
			marker = genes[3];
			p = -1;
			st1 = genes[5];
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//PickUp st1 st2
		case PICKUP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = genes[5];
			st2 = genes[6];
			condition = null;
			senseMarker = -1;
			break;
		//Drop st1
		case DROP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = genes[5];
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Turn turnDir st1
		case TURN:
			senseDir = null;
			turnDir = toTurnDir(genes[2]);
			marker = -1;
			p = -1;
			st1 = genes[5];
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Move st1 st2
		case MOVE:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = genes[5];
			st2 = genes[6];
			condition = null;
			senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = genes[4];
			st1 = genes[5];
			st2 = genes[6];
			condition = null;
			senseMarker = -1;
			break;
		//This should never be reached
		default:
			Logger.log(new InvalidInputWarningEvent("Illegal Command ordinal Argument in State constructer"));
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = -1;
			st2 = -1;
			condition = null;
			senseMarker = -1;
		}
	}
	
	public State(int stateNum, String stateString) {
		this.stateNum = stateNum;
		
		//Given: "Sense Ahead 1 3 Food"
		String[] terms = stateString.split(" ");
		//Gives: [Sense,Ahead,1,3,Food]
		
		command = Command.valueOf(terms[0].trim().toUpperCase());
		
		switch(command){
		//Sense senseDir st1 st2 condition (senseMarker)
		case SENSE:
			senseDir = SenseDir.valueOf(terms[1].trim().toUpperCase());
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = Integer.parseInt(terms[2]);
			st2 = Integer.parseInt(terms[3]);
			condition = Condition.valueOf(terms[4].trim().toUpperCase());
			if(condition == Condition.MARKER){
				senseMarker = Integer.parseInt(terms[5]);
			}else{
				senseMarker = -1;
			}
			break;
		//Mark marker st1
		case MARK:
			senseDir = null;
			turnDir = null;
			marker = Integer.parseInt(terms[1]);
			p = -1;
			st1 = Integer.parseInt(terms[2]);
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Unmark marker st1
		case UNMARK:
			senseDir = null;
			turnDir = null;
			marker = Integer.parseInt(terms[1]);
			p = -1;
			st1 = Integer.parseInt(terms[2]);
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//PickUp st1 st2
		case PICKUP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = Integer.parseInt(terms[1]);
			st2 = Integer.parseInt(terms[2]);
			condition = null;
			senseMarker = -1;
			break;
		//Drop st1
		case DROP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = Integer.parseInt(terms[1]);
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Turn turnDir st1
		case TURN:
			senseDir = null;
			turnDir = TurnDir.valueOf(terms[1].trim().toUpperCase());
			marker = -1;
			p = -1;
			st1 = Integer.parseInt(terms[2]);
			st2 = -1;
			condition = null;
			senseMarker = -1;
			break;
		//Move st1 st2
		case MOVE:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = Integer.parseInt(terms[1]);
			st2 = Integer.parseInt(terms[2]);
			condition = null;
			senseMarker = -1;
			break;
		//Flip p st1 st2
		case FLIP:
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = Integer.parseInt(terms[1]);
			st1 = Integer.parseInt(terms[2]);
			st2 = Integer.parseInt(terms[3]);
			condition = null;
			senseMarker = -1;
			break;
		//This should never be reached
		default:
			Logger.log(new InvalidInputWarningEvent("Illegal Command Argument in State constructer"));
			senseDir = null;
			turnDir = null;
			marker = -1;
			p = -1;
			st1 = -1;
			st2 = -1;
			condition = null;
			senseMarker = -1;
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
			Logger.log(new InvalidInputWarningEvent("Illegal field Argument in State constructer"));
			return -1;
		}
	}
	
	private Command toCommand(int i) {
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
			Logger.log(new InvalidInputWarningEvent("Illegal Command ordinal Argument in State toCommand"));
		}
		return null;
	}
	
	private SenseDir toSenseDir(int i) {
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
			Logger.log(new InvalidInputWarningEvent("Illegal senseDir ordinal Argument in State toSenseDir"));
		}
		return null;
	}
	
	private TurnDir toTurnDir(int i) {
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
			Logger.log(new InvalidInputWarningEvent("Illegal turnDir ordinal Argument in State toSenseDir"));
		}
		return null;
	}
	
	private Condition toCondition(int i) {
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
			Logger.log(new InvalidInputWarningEvent("Illegal Condition ordinal Argument in State toSenseDir"));
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
		genes[0] = command.ordinal();
		if(senseDir == null){
			genes[1] = -1;
		}else{
			genes[1] = senseDir.ordinal();
		}
		if(turnDir == null){
			genes[2] = -1;
		}else{
			genes[2] = turnDir.ordinal();
		}
		genes[3] = marker;
		genes[4] = p;
		genes[5] = st1;
		genes[6] = st2;
		if(condition == null){
			genes[7] = -1;
		}else{
			genes[7] = condition.ordinal();
		}
		genes[8] = senseMarker;
		return genes;
	}
	
	public boolean equals(Object o) {
		State b = (State) o;
		int[] genesA = this.getGenes();
		int[] genesB = b.getGenes();
		int i = 0;
		for(i = 0; i < 9; i++){
			if(genesA[i] != genesB[i]){
				return false;
			}
		}
		return true;
	}
	
	public int getCommand() {
		if(command == null){
			return -1;
		}
		return command.ordinal();
	}
	
	public int getSenseDir() {
		if(senseDir == null){
			return -1;
		}
		return senseDir.ordinal();
	}
	
	public int getTurnDir() {
		if(turnDir == null){
			return -1;
		}
		return turnDir.ordinal();
	}
	
	public int getMarker() {
		return marker;
	}
	
	public int getP() {
		return p;
	}
	
	public int getSt1() {
		return st1;
	}
	
	public int getSt2() {
		return st2;
	}
	
	public int getCondition() {
		if(condition == null){
			return -1;
		}
		return condition.ordinal();
	}
	
	public int getSenseMarker() {
		return senseMarker;
	}
	
	public int getStateNum() {
		return stateNum;
	}

	public String toString() {
		String s = "";
		s += command + " ";
		
		switch(command){
		//Sense senseDir st1 st2 condition
		case SENSE:
			s += senseDir + " ";
			s += st1 + " ";
			s += st2 + " ";
			s += condition + " ";
			if(condition == Condition.MARKER){
				s += senseMarker + " ";
			}
			break;
		//Mark marker st1
		case MARK:
			s += marker + " ";
			s += st1 + " ";
			break;
		//Unmark marker st1
		case UNMARK:
			s += marker + " ";
			s += st1 + " ";
			break;
		//PickUp st1 st2
		case PICKUP:
			s += st1 + " ";
			s += st2 + " ";
			break;
		//Drop st1
		case DROP:
			s += st1 + " ";
			break;
		//Turn turnDir st1
		case TURN:
			s += turnDir + " ";
			s += st1 + " ";
			break;
		//Move st1 st2
		case MOVE:
			s += st1 + " ";
			s += st2 + " ";
			break;
		//Flip p st1 st2
		case FLIP:
			s += p + " ";
			s += st1 + " ";
			s += st2 + " ";
			break;
		//This should never be reached
		default:
			Logger.log(new InvalidInputWarningEvent("Illegal Command Argument in State toString"));
		}
		//So far s == "SENSE AHEAD 1 3 FOOD "
		
		while(s.length() < 45){
			s += " ";
		}
		
		s += "; STATE " + stateNum + ": ";
		
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
}
