package antWorld;

import utilities.ErrorEvent;
import utilities.IllegalArgumentEvent;
import utilities.Logger;
import utilities.WarningEvent;

/**
 * @title Cell
 * @purpose to hold a set of values including the states of the markers, the
 * amount of food present, and the anthill and rockyness of this "hex". The Ant
 * present in this Cell has limited control over these values, and can do with
 * them as its Brain, and the rules specify.
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class Cell implements Cloneable {
	private final int row;
	private final int col;
	
	private boolean rocky;
	private int food;
	private int anthill;
	private boolean[][] markers;
	
	private Cell[] neighbours;
	private Ant ant;
	
	/**
	 * @title Cell
	 * @purpose constructor for objects of type Cell 
	 * @param row the row of the Cell
	 * @param col the column of the Cell
	 * @param c the character that will specify the starting attributes of the 
	 * Cell 
	 * @throws IllegalArgumentEvent if row or col is below 0, or c is invalid
	 */
	public Cell(int row, int col, char c) throws IllegalArgumentEvent {
		if(row < 0 || col < 0){
			throw new IllegalArgumentEvent("row or column value below 0");
		}
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
	 * @title setCell
	 * @purpose to change the attributes of the Cell
	 * @param c the new format of the Cell
	 * @throws IllegalArgumentEvent if c is not a valid character
	 */
	protected void setCell(char c) throws IllegalArgumentEvent {
		if(c > 48 && c < 58){ //'0 to 9' in ascii
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
	 * @title setNeighbours
	 * @purpose to set the neighbours in all 6 directions of this Cell
	 * @param neighbours the Cell[] of new neighbours of the Cell
	 * @throws IllegalArgumentEvent if there are not 6 neighbours
	 */
	protected void setNeighbours(Cell[] neighbours) throws IllegalArgumentEvent {
		if(neighbours.length != 6){
			throw new IllegalArgumentEvent("incorrect number of neighbours");
		}
		this.neighbours = neighbours;
	}
	
	/**
	 * @title getNeighbours
	 * @purpose to return the Cell's neighbours
	 * @return the 6 Cells neighbouring this Cell
	 */
	protected Cell[] getNeighbours() {
		return this.neighbours;
	}
	
	/**
	 * @title getNeighbour
	 * @purpose to get one of the Cells neighbouring this Cell
	 * @param direction alters the direction by 6 until it is within the range
	 * 0 to 6
	 * @return the neighbour of the Cell in the direction specified
	 * @throws ErrorEvent if the Cell has no neighbours
	 */
	protected Cell getNeighbour(int direction) throws ErrorEvent {
		if(this.neighbours == null){
			throw new ErrorEvent("This Cell has no neighbours specified");
		}
		int dir = direction;
		while(dir < 0){
			dir += 6;
		}
		while(dir > 5){
			dir -= 6;
		}
		return this.neighbours[dir];
	}
	
	/**
	 * @title getRow
	 * @purpose to return the row of this Cell
	 * @return the row that this Cell is in
	 */
	protected int getRow() {
		return this.row;
	}
	
	/**
	 * @title getCol
	 * @purpose to return the column of this Cell
	 * @return the column that this Cell is in
	 */
	protected int getCol() {
		return this.col;
	}
	
	/**
	 * @title setupMarkers
	 * @purpose to create a set of 6 boolean markers for each Ant species
	 * @param specieses the number of species or colours of Ant
	 */
	protected void setupMarkers(int specieses) {
		this.markers = new boolean[specieses][6];
	}
	
	/**
	 * @title mark
	 * @purpose to set the marker of the given species at the given location to
	 * true
	 * @param species the species Ants to set to true
	 * @param i the location of the marker to set to true
	 */
	protected void mark(int species, int i) {
		this.markers[species][i] = true;
	}
	
	/**
	 * @title unmark
	 * @purpose to set the marker of the given species at the given location to
	 * false
	 * @param species the species Ants to set to false
	 * @param i the location of the marker to set to false
	 */
	protected void unmark(int species, int i) {
		this.markers[species][i] = false;
	}
	
	/**
	 * @title getMarker 
	 * @purpose to get the value of the marker specified
	 * @param species the species of Ant of the marker to get
	 * @param i the location in the marker array to get the value from
	 * @return true if the marker at the given position is true
	 */
	public boolean getMarker(int species, int i) {
		return this.markers[species][i];
	}
	
	/**
	 * @title getAnyMarker
	 * @purpose to return true if any of the markers of any of the other species
	 * are true
	 * @param notSpecies the species to not check
	 * @return true if any marker of any other species is true
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
	 * @title setAnt
	 * @purpose to allow an Ant's location to be set to this Cell
	 * @param ant the Ant to set as this Cell's Ant
	 */
	protected void setAnt(Ant ant) {
		this.ant = ant;
	}
	
	/**
	 * @title getAnt
	 * @purpose to get the Ant positioned on this Cell
	 * @return this Cell's Ant
	 */
	public Ant getAnt() {
		return this.ant;
	}
	
	/**
	 * @title isRocky
	 * @purpose to get whether or not this Cell is rocky
	 * @return true if this Cell is rocky
	 */
	public boolean isRocky() {
		return this.rocky;
	}
	
	/**
	 * @title foodCount
	 * @purpose to get the amount of food on this Cell
	 * @return the amount of food on the Cell
	 */
	public int foodCount() {
		return this.food;
	}
	
	/**
	 * @title hasFood
	 * @purpose to get whether there is any food on this Cell
	 * @return true if there is any food in this Cell
	 */
	public boolean hasFood() {
		return this.food > 0;
	}
	
	/**
	 * @title dropFood
	 * @purpose to drop one food on the Cell
	 */
	protected void dropFood() {
		this.food++;
	}
	
	/**
	 * @title pickupFood
	 * @purpose to allow the removal of 1 food, if there is any food in the Cell
	 */
	protected void pickupFood() {
		if(this.food > 0){
			this.food--;
		}
	}
	
	/**
	 * @title getAnthill
	 * @purpose to return the anthill of this Cell
	 * @return 0 if no anthill, otherwise the value of the anthill of this Cell
	 */
	public int getAnthill() {
		return this.anthill;
	}
	
	/**
	 * @title hasAnt
	 * @purpose to get whether the Cell is the locaiton of an Ant
	 * @return true if there is an Ant on this Cell
	 */
	public boolean hasAnt() {
		return this.ant != null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 * 
	 * @title clone 
	 * @purpose to get a copy of this Cell
	 * @return an deep copy of this Cell with identical attributes
	 */
	@Override
	public Object clone() {
		return new Cell(this.row, this.col, this.rocky, this.food, this.anthill);
	}
	
	/**
	 * @title toChar
	 * @purpose to get the character representation of this Cell
	 * @return a char that depends on the attributes of the Cell
	 */
	public char toChar() {
		//Ant
		if(hasAnt()){
			if(this.ant.getColour() == 0){
				return '=';
			}
			if(this.ant.getColour() == 1){
				return '|';
			}
		}
		
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
	 * 
	 * @title toString
	 * @purpose to get a String that represents this Cell
	 * @return a String representation of the attributes of this Cell
	 */
	@Override
	public String toString() {
		return Character.toString(toChar());
	}
}
