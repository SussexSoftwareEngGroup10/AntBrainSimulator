package antWorldOps;

import java.util.Random;

import antBrainOps.Brain;
import antBrainOps.State;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Ant {
	enum Colour { BLACK, RED };
	
	//Random is passed from world, all ants in world, and world itself use the same Random,
	//with the same seed, so exactly the same game can be replicated
	//If no Random is passed, generate a new Random with random seed
	private final Random ran;
	private final int uid;
	private final Colour colour;
	private final Brain brain;
	private int state;
	private Cell cell;
	private int direction;
	private boolean food = false;
	
	public Ant(int uid, Random ran, int direction, int colour, Brain brain, Cell cell) {
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
			System.out.println("Illegal Colour Argument in Ant Constructer");
			this.colour = null;
		}
		this.brain = brain;
		this.state = 0;
		this.cell = cell;
		this.direction = direction;
	}
	
	public void step() {
		State s = brain.getState(state);
		int command = s.getCommand();
		
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
			System.out.println("Illegal Command Argument in Ant step");
		}
	}

	//Sense senseDir st1 st2 condition
	private void sense(State s) {
		Cell c = null;
		switch(s.getSenseDir()){
		case 0:
			c = cell;
			break;
		case 1:
			c = cell.getNeighbour(direction);
			break;
		case 2:
			c = cell.getNeighbour(direction - 1);
			break;
		case 3:
			c = cell.getNeighbour(direction + 1);
			break;
		default:
			System.out.println("Illegal senseDir Argument in Ant sense");
		}
		
		boolean condition = false;
		switch(s.getCondition()){
		//FRIEND
		case 0:
			if(c.hasAnt()){
				if(c.getAnt().getColour() == colour.ordinal()){
					condition = true;
				}
			}
			break;
		//FOE
		case 1:
			if(c.hasAnt()){
				if(c.getAnt().getColour() != colour.ordinal()){
					condition = true;
				}
			}
			break;
		//FRIENDWITHFOOD
		case 2:
			if(c.hasAnt()){
				if(c.getAnt().getColour() == colour.ordinal()){
					if(c.getAnt().hasFood()){
						condition = true;
					}
				}
			}
			break;
		//FOEWITHFOOD
		case 3:
			if(c.hasAnt()){
				if(c.getAnt().getColour() != colour.ordinal()){
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
			if(c.getMarker(colour.ordinal(), s.getSenseMarker())){
				condition = true;
			}
			break;
		//FOEMARKER
		case 7:
			if(c.getAnyMarker(colour.ordinal())){
				condition = true;
			}
			break;
		//HOME
		case 8:
			if(c.getAnthill() == colour.ordinal()) {
				condition = true;
			}
			break;
		//FOEHOME
		case 9:
			if(c.getAnthill() != 0){
				if(c.getAnthill() != colour.ordinal()) {
					condition = true;
				}
			}
			break;
		default:
			System.out.println("Illegal Condition Argument in Ant sense");
		}
		
		if(condition){
			state = s.getSt1();
		}else{
			state = s.getSt2();
		}
	}

	//Mark marker st1
	private void mark(State s) {
		cell.mark(colour.ordinal(), s.getMarker());
		state = s.getSt1();
	}

	//Unmark marker st1
	private void unmark(State s) {
		cell.unmark(colour.ordinal(), s.getMarker());
		state = s.getSt1();
	}

	//PickUp st1 st2
	private void pickUp(State s) {
		//If can not carrying food, and food in cell, pick up food and go to st1, else st2
		if(!food && cell.hasFood()){
			cell.takeFood();
			food = true;
			state = s.getSt1();
		}else{
			state = s.getSt2();
		}
	}

	//Drop st1
	private void drop(State s) {
		//If can carrying food, and food in cell < max, drop up food and go to st1
		if(food && cell.foodCount() < 9){
			cell.giveFood();
			food = false;
			state = s.getSt1();
		}
	}
	
	//Turn turnDir st1
	private void turn(State s) {
		if(s.getTurnDir() == 0){
			direction--;
			if(direction < 0){
				direction = 5;
			}
		}else if(s.getTurnDir() == 1){
			direction++;
			if(direction > 5){
				direction = 0;
			}
		}else{
			System.out.println("Illegal TurnDir Argument in Ant turn");
		}
	}
	
	//Move st1 st2
	private void move(State s) {
		//If new cell is not rocky and does not contain an ant, move there and go to st1, else st2
		Cell c = cell.getNeighbour(direction);
		if(!c.isRocky() && !c.hasAnt()){
			c.setAnt(this);
			cell.setAnt(null);
			state = s.getSt1();
		}else{
			state = s.getSt2();
		}
	}

	//Flip p st1 st2
	private void flip(State s) {
		if(ran.nextInt(s.getP()) == 0){
			state = s.getSt1();
		}else{
			state = s.getSt2();
		}
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public int getUID() {
		return uid;
	}
	
	public int getColour() {
		return colour.ordinal();
	}
	
	public boolean hasFood() {
		return food;
	}
}
