package antWorld;

import engine.Random;
import engine.SoundPlayer;
import utilities.ErrorEvent;
import utilities.IllegalArgumentEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.State;

/**
 * Ant
 * to execute instructions in an order specified in the Brain it is
 * given, to move between Cell objects, performing a variety of actions
 * including moving food and setting markers. 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public final class Ant implements Comparable<Ant> {
	protected enum Colour { BLACK, RED }
	
	//Random is passed from world, all ants in world, and world itself use the same Random,
	//with the same seed, so exactly the same game can be replicated
	//If no Random is passed, generate a new Random with random seed
	private final Random ran;
	private final int uid;
	private final Colour colour;
	private Brain brain;
	private Cell cell;
	private boolean alive = true;
	private int direction;
	private boolean hasFood = false;
	private int rest = 0;
	
	//Step local variables as fields to enable inline code
	//all methods except constructor are final to allow inline code
	private State state;
	private Cell senseCell;
	private Cell newCell;
	private Ant[] neighbourAnts = new Ant[6];
	private Ant neighbourAnt;
	
	//Sound player for the possible sound effects ants can produce
	//This can be null (in the case of contests, so checks for null need to be
	//made before sounds are played)
	private SoundPlayer soundPlayer;
	
	/**
	 *  Ant
	 *  to allow the construction of Ant objects
	 * @param uid the unique identifier of this Ant object
	 * @param ran the Random to use to flip
	 * @param direction the direction this Ant should face
	 * @param colour 0 or 1, the colour of the Ant
	 * @param cell the starting location of the Ant
	 * @param soundPlayer the SoundPlayer, if any, to use to record events
	 * @throws IllegalArgumentEvent if the direction is invalid
	 */
	public Ant(int uid, Random ran, int direction, int colour, Cell cell, SoundPlayer soundPlayer) throws IllegalArgumentEvent {
		this.uid = uid;
		
		if(ran == null){
			this.ran = new Random(0);
		}else{
			this.ran = ran;
		}
		
		switch(colour){
		case 0:
			this.colour = Colour.BLACK;
			break;
		case 1:
			this.colour = Colour.RED;
			break;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal Colour " +
				"argument in Ant Constructor"));
			this.colour = null;
		}
		if(direction < 0 || direction > 5){
			throw new IllegalArgumentEvent("invalid initial direction");
		}
		this.direction = direction;
		this.cell = cell;
		
		this.soundPlayer = soundPlayer;
	}
	
	/**
	 *  step
	 *  to execute the current state, then move to next state
	 */
	protected final void step() {
		//Removed local variables and parameters in step() and methods it calls
		//to enable compiler to write an inline version,
		//although it has no obligation to do this,
		//only will if execution time will be reduced
		//the current state number is not stored,
		//the state is changed at the end of any method called by step()
		//(move(), sense()...etc...) 
		//Only sense() and move() are synchronised, as they are the only ones where the ant
		//is interacting with a cell outside its own
		if(!this.alive){
			return;
		}
		
		if(this.rest > 0){
			this.rest--;
			return;
		}
		
		switch(this.state.getCommand()){
		//Sense senseDir st1 st2 condition
		case 0:
			sense();
			break;
		//Mark marker st1
		case 1:
			mark();
			break;
		//Unmark marker st1
		case 2:
			unmark();
			break;
		//PickUp st1 st2
		case 3:
			pickUp();
			break;
		//Drop st1
		case 4:
			drop();
			break;
		//Turn turnDir st1
		case 5:
			turn();
			break;
		//Move st1 st2
		case 6:
			move();
			break;
		//Flip p st1 st2
		case 7:
			flip();
			break;
		default:
			//This should not be reached
			if(this.state.getCommand() == -1){
				//null command
				Logger.log(new ErrorEvent("Null Command in state"));
			}else{
				//command < -1 || > 7
				Logger.log(new IllegalArgumentEvent("Illegal Command " +
					"argument in Ant step"));
			}
		}
	}
	
	/**
	 * e.g.
	 * Sense senseDir st1 st2 condition
	 */
	private final void sense() {
		switch(this.state.getSenseDir()){
		case 0:
			this.senseCell = this.cell;
			break;
		case 1:
			try{
				this.senseCell = this.cell.getNeighbour(this.direction);
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			break;
		case 2:
			try {
				this.senseCell = this.cell.getNeighbour(this.direction - 1);
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			break;
		case 3:
			try{
				this.senseCell = this.cell.getNeighbour(this.direction + 1);
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			break;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal senseDir " +
				"argument in Ant sense"));
			this.senseCell = this.cell;
		}
		
		//Break after state is altered, state is always altered once per call
		switch(this.state.getCondition()){
		//FRIEND
		case 0:
			if(this.senseCell.hasAnt()
				&& this.senseCell.getAnt().getColour() == this.colour.ordinal()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FOE
		case 1:
			if(this.senseCell.hasAnt()
				&& this.senseCell.getAnt().getColour() != getColour()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FRIENDWITHFOOD
		case 2:
			if(this.senseCell.hasAnt()
				&& this.senseCell.getAnt().getColour() == this.colour.ordinal()
				&& this.senseCell.getAnt().hasFood()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FOEWITHFOOD
		case 3:
			if(this.senseCell.hasAnt()
				&& this.senseCell.getAnt().getColour() != this.colour.ordinal()
				&& this.senseCell.getAnt().hasFood()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FOOD
		case 4:
			if(this.senseCell.hasFood()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//ROCK
		case 5:
			if(this.senseCell.isRocky()){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//MARKER
		case 6:
			if(this.senseCell.getMarker(this.colour.ordinal(), this.state.getSenseMarker())){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FOEMARKER
		case 7:
			if(this.senseCell.getAnyMarker(this.colour.ordinal())){
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//HOME
		case 8:
			if(this.senseCell.getAnthill() - 1 == this.colour.ordinal()) {
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		//FOEHOME
		case 9:
			if(this.senseCell.getAnthill() != 0
				&& this.senseCell.getAnthill() - 1 != this.colour.ordinal()) {
				this.state = this.brain.get(this.state.getSt1());
				break;
			}
			this.state = this.brain.get(this.state.getSt2());
			break;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal Condition Argument in Ant sense"));
			this.state = this.brain.get(this.state.getSt2());
		}
	}
	
	/**
	 * e.g.
	 * Mark marker st1
	 */
	private final void mark() {
		this.cell.mark(this.colour.ordinal(), this.state.getMarker());
		this.state = this.brain.get(this.state.getSt1());
	}
	
	/**
	 * e.g.
	 * Unmark marker st1
	 */
	private final void unmark() {
		this.cell.unmark(this.colour.ordinal(), this.state.getMarker());
		this.state = this.brain.get(this.state.getSt1());
	}
	
	/**
	 * e.g.
	 * PickUp st1 st2
	 */
	private final void pickUp() {
		//If can not carrying hasFood, and hasFood in cell,
		//pick up hasFood and go to st1, else st2
		if(!this.hasFood && this.cell.hasFood()){
			this.cell.pickupFood();
			this.hasFood = true;
			this.state = this.brain.get(this.state.getSt1());
			//Play sound effect
			if (this.soundPlayer != null) {
				this.soundPlayer.playSound("food_collection");
			}
		}else{
			this.state = this.brain.get(this.state.getSt2());
		}
	}
	
	/**
	 * e.g.
	 * Drop st1
	 */
	private final void drop() {
		//Assumes food contained in a cell cannot be > 9
		//If can carrying hasFood, and hasFood in cell < max, drop up hasFood and go to st1
		if(this.hasFood){// && this.cell.foodCount() < 9){
			this.cell.dropFood();
			this.hasFood = false;
		}
		this.state = this.brain.get(this.state.getSt1());
		//Play sound effect
		if (this.soundPlayer != null) {
			this.soundPlayer.playSound("food_deposition");
		}
	}
	
	/**
	 * e.g.
	 * Turn turnDir st1
	 */
	private final void turn() {
		switch(this.state.getTurnDir()){
		case 0:
			//Turn anticlockwise
			this.direction--;
			if(this.direction < 0){
				this.direction = 5;
			}
			break;
		case 1:
			//Turn clockwise
			this.direction++;
			if(this.direction > 5){
				this.direction = 0;
			}
			break;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal TurnDir Argument in Ant turn"));
		}
		this.state = this.brain.get(this.state.getSt1());
	}
	
	/**
	 * e.g.
	 * Move st1 st2
	 */
	private final void move() {
		//If new cell is not rocky and does not contain an ant,
		//move there and go to st1, else st2
		try{
			this.newCell = this.cell.getNeighbour(this.direction);
		} catch (ErrorEvent e) {
			Logger.log(e);
		}
		if(!this.newCell.isRocky() && !this.newCell.hasAnt()){
			//Move to cell
			this.newCell.setAnt(this);
			this.cell.setAnt(null);
			this.cell = this.newCell;
			this.state = this.brain.get(this.state.getSt1());
			
			//Check this and 3 neighbour ants for becoming surrounded
			//removed use of an array, more efficient
			try{
				this.neighbourAnt = this.cell.getNeighbour(this.direction - 1).getAnt();
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			if(this.neighbourAnt != null){
				if(this.neighbourAnt.isSurrounded()) this.neighbourAnt.kill();
			}
			try{
				this.neighbourAnt = this.cell.getNeighbour(this.direction).getAnt();
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			if(this.neighbourAnt != null){
				if(this.neighbourAnt.isSurrounded()) this.neighbourAnt.kill();
			}
			try{
				this.neighbourAnt = this.cell.getNeighbour(this.direction + 1).getAnt();
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
			if(this.neighbourAnt != null){
				if(this.neighbourAnt.isSurrounded()) this.neighbourAnt.kill();
			}
			
			this.rest = 14;
			
			if(this.isSurrounded())	this.kill();
		}else{
			this.state = this.brain.get(this.state.getSt2());
		}
	}
	
	/**
	 * e.g.
	 * Flip p st1 st2
	 */
	private final void flip() {
		if(this.ran.randomInt(this.state.getP()) == 0){
			this.state = this.brain.get(this.state.getSt1());
		}else{
			this.state = this.brain.get(this.state.getSt2());
		}
	}
	
	/**
	 * @return
	 */
	private final boolean isSurrounded() {
		if(neighbourFoes() >= 5){
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	private final int neighbourFoes() {
		//Assumes none of the neighbouring cells are null
		int i;
		for(i = 0; i < 6; i++){
			try{
				this.neighbourAnts[i] = this.cell.getNeighbour(i).getAnt();
			} catch (ErrorEvent e) {
				Logger.log(e);
			}
		}
		
		int foes = 0;
		//For each neighbouring cell
		for(Ant ant : this.neighbourAnts){
			//If the cell contains a foe,
			//increment number of foes found
			if(ant != null
				&& ant.getColour() != this.colour.ordinal()){
				foes++;
			}
		}
		//All cells must have contained foes
		return foes;
	}
	
	/**
	 *  setBrain
	 *  to set the Brain of this Ant, so the Ant uses the Brain's states
	 * @param brain the Brain to set
	 */
	protected final void setBrain(Brain brain) {
		this.brain = brain;
		this.state = brain.get(0);
	}
	
	/**
	 * 
	 */
	private final void kill() {
		this.alive = false;
		
		//Drop hasFood carried + 3
		if(this.hasFood){
			this.cell.dropFood();
		}
		for(int i = 0; i < 3; i++) this.cell.dropFood();
		
		//Remove from world
		this.cell.setAnt(null);
		this.cell = null;
		//Play sound effect
		if (this.soundPlayer != null) {
			this.soundPlayer.playSound("die");
		}
	}
	
	/**
	 *  isAlive
	 *  to check whether the Ant is alive, and has a location Cell
	 * @return true if the Ant is alive
	 */
	protected final boolean isAlive() {
		return this.alive;
	}
	
	/**
	 *  getCell
	 *  to get the Cell location of this Ant
	 * @return the Cell that this Ant occupies
	 */
	protected final Cell getCell() {
		return this.cell;
	}
	
	/**
	 *  getUID
	 *  to return the UID of this Ant
	 * @return the UID of this Ant
	 */
	public final int getUID() {
		return this.uid;
	}
	
	/**
	 *  getColour
	 *  to return the colour of this Ant
	 * @return the colour of this Ant
	 */
	public final int getColour() {
		return this.colour.ordinal();
	}
	
	/**
	 *  getDirection
	 *  to return the direction this Ant is facing
	 * @return the direction this Ant is facing
	 */
	public int getDirection() {
		return this.direction;
	}
	
	/**
	 *  hasFood
	 *  to return whether or not the Ant is currently carrying food
	 * @return true if the Ant is carrying food
	 */
	public final boolean hasFood() {
		return this.hasFood;
	}
	
	/**
	 *  setCell
	 *  to set the Cell location of this Ant
	 * @param cell the Cell to be set as this Ant's location
	 */
	protected final void setCell(Cell cell) {
		this.cell = cell;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * 
	 *  hashCode
	 *  to return the Ant's UID
	 * @return the Ant's UID
	 */
	@Override
	public int hashCode() {
		return this.uid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *  equals
	 *  tests whether two Ant objects are equal by comparing their
	 * UIDs
	 * @param o the object to test against this Brain
	 * @return true if the Object passed is equal to this Brain
	 */
	@Override
	public final boolean equals(Object o) {
		//This is consistent with the natural ordering of Ant objects,
		//as given by compareTo
		//Should never return true, as each engine creates a maximum of 1
		//Ant for any UID number
		if(((Ant) o).getUID() == this.uid){
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 *  compareTo
	 *  compares two Ant objects by their UIDs.
	 * @param ant the Ant to compare to this Brain
	 * @return 1 if ant's UID is greater than this Brain, 0 if they
	 * are equal, -1 if it is lesser
	 */
	@Override
	public final int compareTo(Ant ant) {
		//Sorts by UID, lowest first
		//Returns negative if this instance is less than the argument
		if(ant.getUID() < this.uid){
			return -1;
		}else if(ant.getUID() == this.uid){
			return 0;
		}else{//if(ant.getUID() > uid){
			return 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 *  toString
	 *  to generate and return a String representation of this Ant,
	 * which includes many of its attributes.
	 * @return a String representation of this Ant, including many of its fields
	 */
	@Override
	public final String toString() {
		String s = "";
		s += "UID: " + this.uid;
		s += "\ncell: " + this.cell;
		s += "\nis ";
		if(!this.alive) s += "not ";
		s += "alive";
		s += "\nDirection: " + this.direction;
		if(!this.hasFood) s += "\ndoes not have food"; else s += "\nhas food";
		s += "\nrest: " + this.rest;
		s += "\nstate: " + this.brain.get(this.state);
		
		return s;
	}
}
