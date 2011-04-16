package antWorld;

import java.util.Random;

import utilities.ErrorEvent;
import utilities.InvalidInputEvent;
import utilities.Logger;

import antBrain.Brain;
import antBrain.State;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Ant implements Comparable<Ant> {
	enum Colour { BLACK, RED }
	
	//Random is passed from world, all ants in world, and world itself use the same Random,
	//with the same seed, so exactly the same game can be replicated
	//If no Random is passed, generate a new Random with random seed
	private final Random ran;
	private final int uid;
	private final Colour colour;
	private Brain brain;
	private int stateNum = 0;
	private Cell cell;
	private boolean alive = true;
	private int direction;
	private boolean food = false;
	private int rest = 0;
	
	public Ant(int uid, Random ran, int direction, int colour, Cell cell) {
		this.uid = uid;
		
		if(ran == null){
			this.ran = new Random();
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
			if(Logger.getLogLevel() >= 1){
				Logger.log(new InvalidInputEvent("Illegal Colour Argument in Ant Constructor"));
			}
			this.colour = null;
		}
		this.cell = cell;
		this.direction = direction;
	}
	
	public void step() {
		if(this.rest > 0){
			this.rest--;
			return;
		}
		
		State s = this.brain.getState(this.stateNum);
		
		int command = 0;
		try{
			command = s.getCommand();
		}catch(NullPointerException e){
			if(Logger.getLogLevel() >= 1){
				Logger.log(new ErrorEvent("Null Command in state: " + e.getMessage(), e));
			}
			System.exit(1);
		}

		switch(command){
		//Sense senseDir st1 st2 condition
		case 0:
			sense(s);
			break;
		//Mark marker st1
		case 1:
			mark(s);
			break;
		//Unmark marker st1
		case 2:
			unmark(s);
			break;
		//PickUp st1 st2
		case 3:
			pickUp(s);
			break;
		//Drop st1
		case 4:
			drop(s);
			break;
		//Turn turnDir st1
		case 5:
			turn(s);
			break;
		//Move st1 st2
		case 6:
			move(s);
			break;
		//Flip p st1 st2
		case 7:
			flip(s);
			break;
		//This should never be reached
		default:
			if(Logger.getLogLevel() >= 1){
				Logger.log(new InvalidInputEvent("Illegal Command Argument in Ant step"));
			}
		}
	}

	//Sense senseDir st1 st2 condition
	private void sense(State s) {
		Cell c = null;
		switch(s.getSenseDir()){
		case 0:
			c = this.cell;
			break;
		case 1:
			c = this.cell.getNeighbour(this.direction);
			break;
		case 2:
			c = this.cell.getNeighbour(this.direction - 1);
			break;
		case 3:
			c = this.cell.getNeighbour(this.direction + 1);
			break;
		default:
			if(Logger.getLogLevel() >= 1){
				Logger.log(new InvalidInputEvent("Illegal senseDir Argument in Ant sense"));
			}
			c = this.cell;
		}
		
		boolean condition = false;
		switch(s.getCondition()){
		//FRIEND
		case 0:
			if(c.hasAnt()){
				if(c.getAnt().getColour() == this.colour.ordinal()){
					condition = true;
				}
			}
			break;
		//FOE
		case 1:
			if(c.hasAnt()){
				if(c.getAnt().getColour() != this.colour.ordinal()){
					condition = true;
				}
			}
			break;
		//FRIENDWITHFOOD
		case 2:
			if(c.hasAnt()){
				if(c.getAnt().getColour() == this.colour.ordinal()){
					if(c.getAnt().hasFood()){
						condition = true;
					}
				}
			}
			break;
		//FOEWITHFOOD
		case 3:
			if(c.hasAnt()){
				if(c.getAnt().getColour() != this.colour.ordinal()){
					if(c.getAnt().hasFood()){
						condition = true;
					}
				}
			}
			break;
		//FOOD
		case 4:
			if(c.hasFood()){
				condition = true;
			}
			break;
		//ROCK
		case 5:
			if(c.isRocky()){
				condition = true;
			}
			break;
		//MARKER
		case 6:
			if(c.getMarker(this.colour.ordinal(), s.getSenseMarker())){
				condition = true;
			}
			break;
		//FOEMARKER
		case 7:
			if(c.getAnyMarker(this.colour.ordinal())){
				condition = true;
			}
			break;
		//HOME
		case 8:
			if(c.getAnthill() - 1 == this.colour.ordinal()) {
				condition = true;
			}
			break;
		//FOEHOME
		case 9:
			if(c.getAnthill() != 0){
				if(c.getAnthill() - 1 != this.colour.ordinal()) {
					condition = true;
				}
			}
			break;
		default:
			if(Logger.getLogLevel() >= 1){
				Logger.log(new InvalidInputEvent("Illegal Condition Argument in Ant sense"));
			}
		}
		
		if(condition){
			this.stateNum = s.getSt1();
		}else{
			this.stateNum = s.getSt2();
		}
	}

	//Mark marker st1
	private void mark(State s) {
		this.cell.mark(this.colour.ordinal(), s.getMarker());
		this.stateNum = s.getSt1();
	}

	//Unmark marker st1
	private void unmark(State s) {
		this.cell.unmark(this.colour.ordinal(), s.getMarker());
		this.stateNum = s.getSt1();
	}

	//PickUp st1 st2
	private void pickUp(State s) {
		//If can not carrying food, and food in cell, pick up food and go to st1, else st2
		if(!this.food && this.cell.hasFood()){
			this.cell.takeFood();
			this.food = true;
			this.stateNum = s.getSt1();
		}else{
			this.stateNum = s.getSt2();
		}
	}

	//Drop st1
	private void drop(State s) {
		//If can carrying food, and food in cell < max, drop up food and go to st1
		if(this.food && this.cell.foodCount() < 9){
			this.cell.giveFood();
			this.food = false;
			this.stateNum = s.getSt1();
		}
	}
	
	//Turn turnDir st1
	private void turn(State s) {
		switch(s.getTurnDir()){
		case 0:
			this.direction--;
			if(this.direction < 0){
				this.direction = 5;
			}
			break;
		case 1:
			this.direction++;
			if(this.direction > 5){
				this.direction = 0;
			}
			break;
		default:
			if(Logger.getLogLevel() >= 1){
				Logger.log(new InvalidInputEvent("Illegal TurnDir Argument in Ant turn"));
			}
		}
		this.stateNum = s.getSt1();
	}
	
	//Move st1 st2
	private void move(State s) {
		//If new cell is not rocky and does not contain an ant, move there and go to st1, else st2
		Cell newCell = this.cell.getNeighbour(this.direction);
		if(!newCell.isRocky() && !newCell.hasAnt()){
			newCell.setAnt(this);
			this.cell.setAnt(null);
			this.cell = newCell;
			this.stateNum = s.getSt1();
		}else{
			this.stateNum = s.getSt2();
		}
		this.rest = 14;
	}

	//Flip p st1 st2
	private void flip(State s) {
		if(this.ran.nextInt(s.getP()) == 0){
			this.stateNum = s.getSt1();
		}else{
			this.stateNum = s.getSt2();
		}
	}
	
	public boolean isSurrounded() {
		if(neighbourFoes() >= 5){
			return true;
		}
		return false;
	}
	
	public void setBrain(Brain brain) {
		this.brain = brain;
	}
	
	private int neighbourFoes() {
		Cell[] neighbours = this.cell.getNeighbours();
		Ant ant = null;
		int foes = 0;
		int i = 0;
		//For each neighbouring cell
		for(i = 0; i < 6; i++){
			//If the cell contains a foe,
			//increment number of foes found
			ant = neighbours[i].getAnt();
			if(ant != null){
				if(ant.getColour() != this.colour.ordinal()){
					foes++;
				}
			}
		}
		//All cells must have contained foes
		return foes;
	}
	
	public void kill() {
		this.alive = false;
		
		//Drop food carried + 3
		if(this.food){
			this.cell.giveFood();
		}
		int i = 0;
		for(i = 0; i < 3; i++){
			this.cell.giveFood();
		}
		
		//Remove from world
		this.cell.setAnt(null);
		this.cell = null;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public Cell getCell() {
		return this.cell;
	}
	
	public int getUID() {
		return this.uid;
	}
	
	public int getColour() {
		return this.colour.ordinal();
	}
	
	public boolean hasFood() {
		return this.food;
	}
	
	public boolean equals(Ant ant) {
		//This is consistent with the natural ordering of Ant objects,
		//as given by compareTo
		//Should never return true, as each engine creates a maximum of 1
		//Ant for any UID number
		if(ant.getUID() == this.uid){
			return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(Ant ant) {
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

	public void setCell(Cell cell) {
		this.cell = cell;
	}
}
