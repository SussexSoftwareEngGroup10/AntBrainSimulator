package antWorld;

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
			rocky = true;
			food = 0;
			anthill = 0;
			break;
		case '.':
			rocky = false;
			food = 0;
			anthill = 0;
			break;
		case '+':
			rocky = false;
			food = 0;
			anthill = 1;
			break;
		case '-':
			rocky = false;
			food = 0;
			anthill = 2;
			break;
		default: //'0 to 9'
			//Only case left is int/food (or inappropriate char value)
			try{
				rocky = false;
				//48 is the ascii code for '0', 58 for 9
				//Need to convert from (48 to 58) to (0 to 9)
				food = ((int) c) - 48;
				anthill = 0;
			}catch(NumberFormatException nfe){
				//Cell must contain food, otherwise switch would have broken at '.'
				nfe.printStackTrace();
			}
		}
	}
	
	public void setNeighbours(Cell[] neighbours) {
		this.neighbours = neighbours;
	}
	
	public Cell[] getNeighbours() {
		return neighbours;
	}
	
	public Cell getNeighbour(int direction) {
		if(direction < 0){
			direction += 6;
		}else if(direction > 5){
			direction -= 6;
		}
		return neighbours[direction];
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	private String getFoodChar() {
		if(anthill == 0){ //0 to 9
			return Integer.toString(food);
			
			//Otherwise, food must be in an anthill
			//so give unique char value that acknowledges this
			//Greek isn't recognised by Notepad or the console (prints '?' instead)
		}else if(anthill == 1){ //Upper case, 65 for Latin, 913 for Greek
			return Character.toString((char) (65 + food));
		}else if(anthill == 2){ //Lower case, 97 for Latin, 945 for Greek
			return Character.toString((char) (97 + food));
		}
		return null; //Else error, cannot be less than 0 or more than 2 anthills
	}
	
	public void setupMarkers(int specieses) {
		markers = new boolean[specieses][6];
	}
	
	public void mark(int species, int i) {
		markers[species][i] = true;
	}
	
	public void unmark(int species, int i) {
		markers[species][i] = false;
	}
	
	public boolean getMarker(int species, int i) {
		return markers[species][i];
	}
	
	public boolean getAnyMarker(int notSpecies) {
		//returns true if any marker not of species notSpecies is true
		int i = 0;
		int j = 0;
		for(i = 0; i < markers.length; i++){
			if(i != notSpecies){
				for(j = 0; j < 6; j++){
					if(markers[i][j] == true){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public int getSpecieses() {
		//I know the plural of specieses is wrong,
		//but I needed a way to make the singular and plural distinct
		return markers.length;
	}
	
	public void setAnt(Ant ant) {
		this.ant = ant;
	}
	
	public Ant getAnt() {
		return ant;
	}
	
	public boolean isRocky() {
		return rocky;
	}
	
	public int foodCount() {
		return food;
	}
	
	public boolean hasFood() {
		return food != 0;
	}
	
	public void giveFood() {
		if(food < 9){
			food++;
		}
	}
	
	public void takeFood() {
		if(food > 0){
			food--;
		}
	}
	
	public int getAnthill() {
		return anthill;
	}
	
	public boolean hasAnt() {
		return ant != null;
	}
	
	public char toChar() {
		return toString().charAt(0);
	}
	
	public String toString() {
		if(rocky){
			return "#";
			
		}else if(food > 0){
			return getFoodChar();
			
		}else if(anthill > 0){
			if(anthill == 1){
				return "+";
			}else if(anthill == 2){
				return "-";
			}
		}
		return ".";
	}
}
