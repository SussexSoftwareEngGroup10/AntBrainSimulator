package antWorld;

import utilities.IllegalArgumentEvent;
import utilities.Logger;
import utilities.WarningEvent;

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Cell implements Cloneable {
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
	
	/**
	 * @param row
	 * @param col
	 * @param c
	 * @throws IllegalArgumentEvent 
	 */
	public Cell(int row, int col, char c) throws IllegalArgumentEvent {
		this.row = row;
		this.col = col;
		
		setCell(c);
	}
	
	private Cell(int row, int col, boolean rocky, int food, int anthill){
		this.row = row;
		this.col = col;
		this.rocky = rocky;
		this.food = food;
		this.anthill = anthill;
	}
	
	/**
	 * @param c
	 * @throws IllegalArgumentEvent 
	 */
	public void setCell(char c) throws IllegalArgumentEvent {
		if(c - 48 > 0 && c - 48 < 10){ //'0 to 9'
			this.rocky = false;
			this.food = c - 48;
			this.anthill = 0;
			return;
		}
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
		default:
			throw new IllegalArgumentEvent("Illegal argument in Cell setCell");
		}
	}
	
	/**
	 * @param neighbours
	 */
	public void setNeighbours(Cell[] neighbours) {
		this.neighbours = neighbours;
	}
	
	/**
	 * @return
	 */
	public Cell[] getNeighbours() {
		return this.neighbours;
	}
	
	/**
	 * @param direction
	 * @return
	 */
	public Cell getNeighbour(int direction) {
		if(direction < 0){
			return this.neighbours[direction + 6];
		}
		if(direction > 5){
			return this.neighbours[direction - 6];
		}
		return this.neighbours[direction];
	}
	
	/**
	 * @return
	 */
	public int getRow() {
		return this.row;
	}
	
	/**
	 * @return
	 */
	public int getCol() {
		return this.col;
	}
	
	/**
	 * @param specieses
	 */
	public void setupMarkers(int specieses) {
		this.markers = new boolean[specieses][6];
	}
	
	/**
	 * @param species
	 * @param i
	 */
	public void mark(int species, int i) {
		this.markers[species][i] = true;
	}
	
	/**
	 * @param species
	 * @param i
	 */
	public void unmark(int species, int i) {
		this.markers[species][i] = false;
	}
	
	/**
	 * @param species
	 * @param i
	 * @return
	 */
	public boolean getMarker(int species, int i) {
		return this.markers[species][i];
	}
	
	/**
	 * @param notSpecies
	 * @return
	 */
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
	
	/**
	 * @param ant
	 */
	public void setAnt(Ant ant) {
		this.ant = ant;
	}
	
	/**
	 * @return
	 */
	public Ant getAnt() {
		return this.ant;
	}
	
	/**
	 * @return
	 */
	public boolean isRocky() {
		return this.rocky;
	}
	
	/**
	 * @return
	 */
	public int foodCount() {
		return this.food;
	}
	
	/**
	 * @return
	 */
	public boolean hasFood() {
		return this.food != 0;
	}
	
	/**
	 * @param i
	 */
	public void dropFood(int i) {
		//Removed food limit of 9 per cell
//		if(this.food + i <= 9){
		this.food += i;
//		}
	}
	
	/**
	 * 
	 */
	public void pickupFood() {
		if(this.food > 0){
			this.food--;
		}
	}
	
	/**
	 * @return
	 */
	public int getAnthill() {
		return this.anthill;
	}
	
	/**
	 * @return
	 */
	public boolean hasAnt() {
		return this.ant != null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		return new Cell(this.row, this.col, this.rocky, this.food, this.anthill);
	}
	
	/**
	 * @return
	 */
	public char toChar() {
//		//Ant
//		if(hasAnt()){
//			if(this.ant.getColour() == 0){
//				return '=';
//			}
//			if(this.ant.getColour() == 1){
//				return '|';
//			}
//		}
		
		//Rock
		if(this.rocky){
			return '#';
			
		}
		
		//Food
		if(this.food > 0){
			//Prints the food value,
			//if it is > 9, prints 9 instead
			if(this.anthill == 0){ //0 to 9
				if(this.food > 9){
					return 48 + 9;
				}
				return (char) (this.food + 48);
			}
			
			//Otherwise, food must be in an anthill
			//so give unique char value that acknowledges this
			//Greek isn't recognised by Notepad or the console (prints '?' instead)
			//Minimum food value is 1, so -1 from ascii codes
			if(this.anthill == 1){ //Upper case, 65 for Latin, 913 for Greek
				if(this.food > 9){
					return (char) (64 + 9);
				}
				return (char) (64 + this.food);
			}
			if(this.anthill == 2){ //Lower case, 97 for Latin, 945 for Greek
				if(this.food > 9){
					return (char) (96 + 9);
				}
				return (char) (96 + this.food);
			}
			//Else error, cannot be less than 0 or more than 2 anthills
			Logger.log(new WarningEvent("Cell anthill value not 0, 1 or 2"));
			return 0;	//Null char value
		}
		
		//Anthill
		if(this.anthill > 0){
			if(this.anthill == 1){
				return '+';
			}
			if(this.anthill == 2){
				return '-';
			}
		}
		
//		//Markers
//		for(int i = 0; i < this.markers.length; i++){
//			for(boolean marker : this.markers[i]){
//				if(marker){
//					if(i == 0){
//						return '[';
//					}else if(i == 1){
//						return ']';
//					}else{
//						return '?';
//					}
//				}
//			}		
//		}
		
		return '.';
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Character.toString(toChar());
	}
}
