package antWorld;

import utilities.IllegalArgumentEvent;
import utilities.Logger;
import utilities.WarningEvent;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Cell {
	private final int row;
	private final int col;
	
	//I would prefer to make 'rocky' and 'anthill' final, but it would be impossible,
	//given how I construct the world object before altering cell types
	private boolean rocky;
	private int food;
	private int anthill;
	private boolean[][] markers;
	
	private Cell[] neighbours;
	private Ant ant;
	
	public Cell(int row, int col, char c) {
		this.row = row;
		this.col = col;
		
		setCell(c);
	}
	
	public void setCell(char c) {
		switch(c) {
		case '#':
			this.rocky = true;
			this.food = 0;
			this.anthill = 0;
			break;
		case '.':
			this.rocky = false;
			this.food = 0;
			this.anthill = 0;
			break;
		case '+':
			this.rocky = false;
			this.food = 0;
			this.anthill = 1;
			break;
		case '-':
			this.rocky = false;
			this.food = 0;
			this.anthill = 2;
			break;
		default: //'0 to 9'
			//Only case left is int/food (or inappropriate char value)
			try{
				this.rocky = false;
				//48 is the ascii code for '0', 58 for 9
				//Need to convert from (48 to 58) to (0 to 9)
				this.food = c - 48;
				this.anthill = 0;
			}catch(NumberFormatException e){
				//Cell must contain food, otherwise switch would have broken at '.'
				Logger.log(new IllegalArgumentEvent("Illegal food " +
					"argument in Cell setCell", e));
			}
		}
	}
	
	public void setNeighbours(Cell[] neighbours) {
		this.neighbours = neighbours;
	}
	
	public Cell[] getNeighbours() {
		return this.neighbours;
	}
	
	public Cell getNeighbour(int direction) {
		if(direction < 0){
			return this.neighbours[direction + 6];
		}
		if(direction > 5){
			return this.neighbours[direction - 6];
		}
		return this.neighbours[direction];
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public void setupMarkers(int specieses) {
		this.markers = new boolean[specieses][6];
	}
	
	public void mark(int species, int i) {
		this.markers[species][i] = true;
	}
	
	public void unmark(int species, int i) {
		this.markers[species][i] = false;
	}
	
	public boolean getMarker(int species, int i) {
		return this.markers[species][i];
	}
	
	public boolean getAnyMarker(int notSpecies) {
		//returns true if any marker not of species notSpecies is true
		int i = 0;
		int j = 0;
		for(i = 0; i < this.markers.length; i++){
			if(i != notSpecies){
				for(j = 0; j < 6; j++){
					if(this.markers[i][j] == true){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void setAnt(Ant ant) {
		this.ant = ant;
	}
	
	public Ant getAnt() {
		return this.ant;
	}
	
	public boolean isRocky() {
		return this.rocky;
	}
	
	public int foodCount() {
		return this.food;
	}
	
	public boolean hasFood() {
		return this.food != 0;
	}
	
	public void giveFood(int i) {
//		if(this.food + i <= 9){
		this.food += i;
//		}
	}
	
	public void takeFood() {
		if(this.food > 0){
			this.food--;
		}
	}
	
	public int getAnthill() {
		return this.anthill;
	}
	
	public boolean hasAnt() {
		return this.ant != null;
	}
	
	public char toChar() {
		return toString().charAt(0);
	}
	
	@Override
	public String toString() {
		if(hasAnt()){
			if(this.ant.getColour() == 0){
				return "=";
			}
			if(this.ant.getColour() == 1){
				return "|";
			}
		}
		
		if(this.rocky){
			return "#";
			
		}
		if(this.food > 0){
			//Prints the food value,
			//if it is > 9, prints 9 instead
			if(this.anthill == 0){ //0 to 9
				if(this.food > 9){
					return Integer.toString(9);
				}
				return Integer.toString(this.food);
			}
			
			//Otherwise, food must be in an anthill
			//so give unique char value that acknowledges this
			//Greek isn't recognised by Notepad or the console (prints '?' instead)
			//Minimum food value is 1, so -1 from ascii codes
			if(this.anthill == 1){ //Upper case, 65 for Latin, 913 for Greek
				if(this.food > 9){
					return Character.toString((char) (64 + 9));
				}
				return Character.toString((char) (64 + this.food));
			}
			if(this.anthill == 2){ //Lower case, 97 for Latin, 945 for Greek
				if(this.food > 9){
					return Character.toString((char) (96 + 9));
				}
				return Character.toString((char) (96 + this.food));
			}
			//Else error, cannot be less than 0 or more than 2 anthills
			Logger.log(new WarningEvent("Cell anthill value not 0, 1 or 2"));
			return null; 
			
		}
		if(this.anthill > 0){
			if(this.anthill == 1){
				return "+";
			}
			if(this.anthill == 2){
				return "-";
			}
		}
		return ".";
	}
}
