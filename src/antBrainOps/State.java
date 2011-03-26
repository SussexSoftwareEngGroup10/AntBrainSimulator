package antBrainOps;

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

public class State {
	enum Command { SENSE, MARK, UNMARK, PICKUP, DROP, TURN, MOVE, FLIP};
	enum SenseDir { AHEAD, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, BEHIND};
	enum TurnDir { LEFT, RIGHT};
	enum Condition { FOOD, HOME }; //More values needed, but I don't want to add too many

	private Command command;
	private SenseDir senseDir;
	private TurnDir turnDir;
	private int marker;
	private int p;
	private int st1;
	private int st2;
	private Condition condition;
	
	private int stateNum;

	public State(int stateNum, String stateString) throws ArrayIndexOutOfBoundsException {
		this.stateNum = stateNum;
		
		//Given: "Sense Ahead 1 3 Food"
		String[] terms = stateString.split(" ");
		//Gives: [Sense,Ahead,1,3,Food]
		
		command = Command.valueOf(terms[0].trim().toUpperCase());
		
		switch(command){
		//Sense sensedir st1 st2 cond
		case SENSE:
			senseDir = SenseDir.valueOf(terms[1].trim().toUpperCase());
			st1 = Integer.parseInt(terms[2]);
			st2 = Integer.parseInt(terms[3]);
			condition = Condition.valueOf(terms[4].trim().toUpperCase());
			break;
			//Mark i st
		case MARK:
			marker = Integer.parseInt(terms[1]);
			st1 = Integer.parseInt(terms[2]);
			break;
			//Unmark i st
		case UNMARK:
			marker = Integer.parseInt(terms[1]);
			st1 = Integer.parseInt(terms[2]);
			break;
			//PickUp st1 st2
		case PICKUP:
			st1 = Integer.parseInt(terms[1]);
			st2 = Integer.parseInt(terms[2]);
			break;
			//Drop st
		case DROP:
			st1 = Integer.parseInt(terms[1]);
			break;
			//Turn lr st
		case TURN:
			turnDir = TurnDir.valueOf(terms[1].trim().toUpperCase());
			st1 = Integer.parseInt(terms[2]);
			break;
			//Move st1 st2
		case MOVE:
			st1 = Integer.parseInt(terms[1]);
			st2 = Integer.parseInt(terms[2]);
			break;
			//Flip p st1 st2
		case FLIP:
			p = Integer.parseInt(terms[1]);
			st1 = Integer.parseInt(terms[2]);
			st2 = Integer.parseInt(terms[3]);
			break;
		default:
			//This should never be reached
			System.out.println("Illegal Command Argument in State constructer");
		}
	}

	public String toString() {
		String s = "";
		s += command + " ";
		
		switch(command){
		//Sense sensedir st1 st2 cond
		case SENSE:
			s += senseDir + " ";
			s += st1 + " ";
			s += st2 + " ";
			s += condition + " ";
			break;
			//Mark i st
		case MARK:
			s += marker + " ";
			s += st1 + " ";
			break;
			//Unmark i st
		case UNMARK:
			s += marker + " ";
			s += st1 + " ";
			break;
			//PickUp st1 st2
		case PICKUP:
			s += st1 + " ";
			s += st2 + " ";
			break;
			//Drop st
		case DROP:
			s += st1 + " ";
			break;
			//Turn lr st
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
		default:
			//This should never be reached
			System.out.println("Illegal Command Argument in State toString");
		}
		//So far s == "SENSE AHEAD 1 3 FOOD "
		
		while(s.length() < 30){
			s += " ";
		}
		
		s += "; STATE " + stateNum + ": ";
		
		while(s.length() < 45){
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
