package antWorldOps;

public class Cell {
	private final int row;
	private final int col;
	
	//Defaults are for "clear cell" ('.')
	private boolean rocky; //TODO final
	private int food;
	private int anthill; //TODO final
	
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
			//We aren't using Latin at the moment
			//Greek isn't recognised by Notepad or the console (prints '?' instead)
		}else if(anthill == 1){ //Upper case, 65 for Latin, 913 for Greek
			return Character.toString((char) (65 + food));
		}else if(anthill == 2){ //Lower case, 97 for Latin, 945 for Greek
			return Character.toString((char) (97 + food));
		}
		return "X"; //Else error, cannot be less than 0 or more than 2 anthills
	}
	
	public void setAnt(Ant ant) {
		this.ant = ant;
	}
	
	public Ant getAnt() {
		return ant;
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
