package antWorld;

import java.util.ArrayList;
import java.util.Random;

import antBrain.Brain;

/*
Terrain types:
#        rocky cell
.        clear cell (containing nothing interesting)
+        red anthill cell
-        black anthill cell
1 to 9   clear cell containing the given number of food particles
*/

/*
Example World:
	10
	10
	# # # # # # # # # #
	 # 9 9 . . . . 3 3 #
	# 9 # . - - - - - #
	 # . # - - - - - - #
	# . . 5 - - - - - #
	 # + + + + + 5 . . #
	# + + + + + + # . #
	 # + + + + + . # 9 #
	# 3 3 . . . . 9 9 #
	 # # # # # # # # # #
*/

/**
 * @author pkew20 / 57116
 * @version 1.0
 */
public class World {
	//Using a seed means that the same world can be reproduced
	//Seed is generated randomly, but is recorded, so the same seed can be used again
	private int seed;
	private final Random ran;
	private final int rows;
	private final int cols;
	
	private final int rocks;
	private final int anthills;
	private final int anthillSideLength;
	
	private final int foodBlobCount;
	private final int foodBlobSideLength;
	private final int foodBlobCellFoodCount;
	
	private final int antInitialDirection;
	
	private static final int gap = 1; //gap between objects in world
	
	private Cell[][] cells; //indent every second line, starting at cells[1]
	private final Brain[] brains;
	private final ArrayList<Ant> ants = new ArrayList<Ant>();
	//I would use a ArrayList<Ant>[], but you can't do that in Java
	private final ArrayList<ArrayList<Ant>> antsBySpecies = new ArrayList<ArrayList<Ant>>();
	
	//Ant colours:
	//'+' == black
	//'-' == red
	
	/**
	 * @param seed 0 to use random seed
	 * 
	 * @return a world which is fit to be used in a tournament
	 */
	public static World getTournamentWorld(Brain[] brains, int seed) {
		return new World(140, 140, 13, brains, seed);
	}
	
	/**
	 * Generates a new world from given parameters
	 * Used by the getTournamentWorld method and user
	 * 
	 * @param rows
	 * @param cols
	 * @param rocks
	 */
	public World(int rows, int cols, int rocks, Brain[] brains, int seed) {
		//Can either use a random or predefined seed
		if(seed == 0){
			this.seed = new Random().nextInt(Integer.MAX_VALUE);
		}else{
			this.seed = seed;
		}
		ran = new Random(this.seed);
		
		this.rows = rows;
		this.cols = cols;
		this.rocks = rocks;
		this.brains = brains;
		
		anthills = 2;
		anthillSideLength = 7;
		foodBlobCount = 10;
		foodBlobSideLength = 5;
		foodBlobCellFoodCount = 5;
		antInitialDirection = 0;
		
		constructCells();
		createWorld();
	}
	
	/**
	 * Creates a new world with parameters from given arrays
	 * Used by the WorldParser class
	 * 
	 * @param cellChars
	 */
	protected World(char[][] cellChars, Brain[] brains) {
		//Random is not needed as world will not be generated
		seed = -1;
		ran = null;
		
		this.rows = cellChars.length;
		this.cols = cellChars[0].length;
		this.brains = brains;
		
		cells = new Cell[rows][cols];
		int r = 0;
		int c = 0;
		
		//Setup cells
		for(r = 0; r < rows; r++){
			cells[r] = new Cell[cols];
			for(c = 0; c < cols; c++){
				cells[r][c] = new Cell(r, c, cellChars[r][c]);
			}
		}
		
		//Setup neighbours for each cell
		Cell current;
		for(r = 0; r < rows; r++){
			for(c = 0; c < cols; c++){
				current = cells[r][c];
				current.setNeighbours(getNeighbours(current));
			}
		}
		
		//Assumptions:
		//There is a rocky border around the world which should not 
		//count towards the total number of rocks in the world
		//There is a gap of at least 1 hex between each object
		//All anthills are hexagonal and have the same side length
		//There are no more than 2 types of anthill in the world ('+' and '-')
		//(however, there may be more than 1 anthill for each species)
		//Food blobs are square, and all have the same side length
		//All cells which contain food contain the same amount of food
		
		//Confirmed works for tournament worlds,
		//getAttributes on generated world,
		//then compared results with those given after writing
		//and reading world in through a WorldParser
		int rocks = 0;
		boolean plusAnthill = false;
		boolean minusAnthill = false;
		Cell current2;
		boolean firstAnthillFound = false;
		int anthillSideLength = 0;
		int foodBlobCellCount = 0;
		boolean firstFoodFound = false;
		int foodBlobSideLength = 0;
		int foodBlobCellFoodCount = 0;
		
		//Calculate field values from cell information
		for(r = 0; r < rows; r++){
			for(c = 0; c < cols; c++){
				current = cells[r][c];
				
				//Calculate number of rocks
				if(current.isRocky()){
					rocks++;
				
				//Calculate number of anthills
				}else if(current.getAnthill() == 1){
					plusAnthill = true;
				}else if(current.getAnthill() == 2){
					minusAnthill = true;
				}
				
				//Calculate anthill side length
				if(current.getAnthill() != 0){
					if(!firstAnthillFound){
						current2 = current;
						do{
							anthillSideLength++;
							current2 = current2.getNeighbour(0);
						}while(current2.getAnthill() != 0);
						firstAnthillFound = true;
					}
				}
				
				if(current.hasFood()){
					//Calculate number of cells containing food
					foodBlobCellCount++;
					
					//Calculate food blob side length
					if(!firstFoodFound){
						foodBlobCellFoodCount = current.foodCount();
						current2 = current;
						do{
							foodBlobSideLength++;
							current2 = current2.getNeighbour(0);
						}while(current2.hasFood());
						firstFoodFound = true;
					}
				}
			}
		}
		
		rocks -= (rows - 1) * 2 + (cols - 1) * 2; //subtract border rocks
		this.rocks = rocks;
		
		int anthills = 0;
		if(plusAnthill){
			anthills++;
		}
		if(minusAnthill){
			anthills++;
		}
		this.anthills = anthills;
		this.anthillSideLength = anthillSideLength;
		this.foodBlobCount = foodBlobCellCount / (foodBlobSideLength * foodBlobSideLength);
		this.foodBlobSideLength = foodBlobSideLength;
		this.foodBlobCellFoodCount = foodBlobCellFoodCount;
		antInitialDirection = 0;
		
		//Setup markers in each cell
		//Use this. for fields, local variables created above
		for(r = 0; r < rows; r++){
			for(c = 0; c < cols; c++){
				current = cells[r][c];
				current.setNeighbours(getNeighbours(current));
				current.setupMarkers(this.anthills);
			}
		}
		
		createAnts();
	}
	
	/**
	 * Initialises the arrays of cells to clear
	 */
	private void constructCells() {
		int r = 0;
		int c = 0;
		
		cells = new Cell[rows][cols];
		for(r = 0; r < rows; r++){
			cells[r] = new Cell[cols];
			for(c = 0; c < cols; c++){
				cells[r][c] = new Cell(r, c, '.');
			}
		}
	}
	
	/**
	 * Places all objects specified into world, with required gap between objects
	 * Generates and checks random locations for each object
	 * Skips first 2 and last 2 rows and columns, as will never be able to place
	 * objects in these locations
	 * Inefficient, still tries more locations than required, may need to reduce
	 * randomness, add more constraints, or store locations previously tried
	 * 
	 * Method will hang if, by chance, objects are placed in a way that does not
	 * leave room for all objects which are required to be placed.
	 * Or the map is small, and all objects will not fit into the specified space
	 */
	private void createWorld() {
		int failCount = 0;
		int ranRow;
		int ranCol;
		int i = 0;
		
		//Border
		failCount--;
		if(!setBorderRocks()){
			failCount++;
		}
		
		if(anthills >= 1){
			//Anthill1
			failCount--;
			do{
				failCount++;
				ranRow = ran.nextInt(rows - 2) + 1;
				ranCol = ran.nextInt(cols - 2) + 1;
			}while(!setHex(ranRow, ranCol, anthillSideLength, '+'));
		}
		
		if(anthills >= 2){
			//Anthill2
			failCount--;
			do{
				failCount++;
				ranRow = ran.nextInt(rows - 2) + 1;
				ranCol = ran.nextInt(cols - 2) + 1;
			}while(!setHex(ranRow, ranCol, anthillSideLength, '-'));
		}
		
		//No more than 2 anthills can be constructed as any more than this they cannot be 
		//represented uniquely by the 2 char values specified in Cell
		createAnts();
		
		//Foods
		for(i = 0; i < foodBlobCount; i++){
			failCount--;
			do{
				failCount++;
				ranRow = ran.nextInt(rows - 2) + 1;
				ranCol = ran.nextInt(cols - 2) + 1;
			}while(!setRect(ranRow, ranCol,	foodBlobSideLength,
				foodBlobSideLength, (char) (foodBlobCellFoodCount + 48)));
		}
		
		//Rocks
		for(i = 0; i < rocks; i++){
			failCount--;
			do{
				failCount++;
				ranRow = ran.nextInt(rows - 2) + 1;
				ranCol = ran.nextInt(cols - 2) + 1;
			}while(!setCell(ranRow, ranCol, '#'));
		}
	}
	
	private boolean setBorderRocks() {
		if(!checkBorderClear()){
			return false;
		}
		
		int r = 0;
		int c = 0;
		
		//First column
		c = 0;
		for(r = 0; r < rows; r++){
			cells[r][c].setCell('#');
		}
		
		//Last column
		c = cols - 1;
		for(r = 0; r < rows; r++){
			cells[r][c].setCell('#');
		}
		
		//First row
		r = 0;
		for(c = 0; c < rows; c++){
			cells[r][c].setCell('#');
		}
		
		//Last row
		r = rows - 1;
		for(c = 0; c < rows; c++){
			cells[r][c].setCell('#');
		}
		
		return true;
	}
	
	/**
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @param c new value for every cell in the area
	 */
	private boolean setHex(int row, int col, int sideLength, char ch) {
		if(!checkHexClear(row, col, sideLength)){
			return false;
		}
		
		Cell centre = cells[row][col];
		setHexRecurse(centre, 0, sideLength, ch);
		return true;
	}
	
	private void setHexRecurse(Cell cell, int recurseNum, int recurseDepth, char ch) {
		//Need both checks to allow for hexes
		//containing elements on first or last rows or columns
		//and hexes with side length 0
		
		//Don't set if over required length
		if(recurseNum > recurseDepth - 1){
			return;
		}
		
		cell.setCell(ch);
		
		//Don't recurse if next recurse will take side length over required length
		if(recurseNum > recurseDepth - 2){
			return;
		}
		
		//Sets hexes multiple times, inefficient
		Cell[] neighbours = getNeighbours(cell);
		int i = 0;
		for(i = 0; i < neighbours.length; i++){
			setHexRecurse(neighbours[i], recurseNum + 1, recurseDepth, ch);
		}
	}
	
	private boolean setRect(int row, int col, int height, int width, char ch) {
		if(!checkRectClear(row, col, height, width)){
			return false;
		}
		
		int r = 0;
		int c = 0;
		
		for(r = row; r < row + height; r++){
			for(c = col; c < col + width; c++){
				cells[r][c].setCell(ch);
			}
		}
		return true;
	}
	
	private boolean setCell(int row, int col, char ch) {
		if(!checkCellClear(row, col)){
			return false;
		}
		
		cells[row][col].setCell(ch);
		return true;
	}
	
	private boolean checkBorderClear() {
		int r = 0;
		int c = 0;
		
		//First column + gap
		for(c = 0; c < gap + 1; c++){
			for(r = 0; r < rows; r++){
				if(cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		
		//Last column + gap
		for(c = cols - 1 - gap; c < cols; c++){
			for(r = 0; r < rows; r++){
				if(cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		
		//First row + gap
		for(r = 0; r < gap + 1; r++){
			for(c = 0; c < rows; c++){
				if(cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		
		//Last row + gap
		for(r = rows - 1 - gap; r < rows; r++){
			for(c = 0; c < rows; c++){
				if(cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @return
	 */
	private boolean checkHexClear(int row, int col, int sideLength) {
			Cell centre = cells[row][col];
			
			try{
				return checkHexClearRecurse(centre, 0, sideLength + gap);
			}catch(ArrayIndexOutOfBoundsException aiob){
				return false;
			}
		}
		
	private boolean checkHexClearRecurse(Cell cell, int recurseNum, int recurseDepth) {
		if(cell.toChar() != '.'){
			return false;
		}
		
		//Don't recurse if next recurse will take side length over required length
		if(recurseNum > recurseDepth - 2){
			return true;
		}
		
		//Sets hexes multiple times, inefficient
		Cell[] neighbours = getNeighbours(cell);
		int i = 0;
		for(i = 0; i < neighbours.length; i++){
			if(!checkHexClearRecurse(neighbours[i], recurseNum + 1, recurseDepth)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param row of top left hex
	 * @param col of top left hex
	 * @param height distance from top left to bottom left hex
	 * @param width distance from top left to top right hex
	 * @return
	 */
	private boolean checkRectClear(int row, int col, int height, int width) {
		int r = 0;
		int c = 0;
		
		try{
			for(r = row - gap; r < row + height + gap; r++){
				for(c = col - gap; c < col + width + gap; c++){
					if(cells[r][c].toChar() != '.'){
						return false;
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException aiob){
			return false;
		}
		
		return true;
	}
	
	private boolean checkCellClear(int row, int col) {
		int r = 0;
		int c = 0;
		
		for(r = row - gap; r <= row + gap; r++){
			for(c = col - gap; c <= col + gap; c++){
				if(cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Call after anthills generated, puts an ant in each anthill hex
	 * 
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @param c new value for every cell in the area
	 */
	private void createAnts() {
		ArrayList<Ant> species;
		Brain brain = null;
		Cell cell;
		Ant ant;
		int colour = -1;
		int r = 0;
		int c = 0;
		int uid = 0;
		
		for(r = 0; r < rows; r++){
			for(c = 0; c < cols; c++){
				colour = -1;
				cell = cells[r][c];
				
				if(cell.toChar() == '+'){
					colour = 0;
				}else if(cell.toChar() == '-'){
					colour = 1;
				}else{
					continue;
				}
				
				while(antsBySpecies.size() <= colour){
					antsBySpecies.add(new ArrayList<Ant>());
				}
				species = antsBySpecies.get(colour);
				
				//If more than 1 brain is given, use most possible
				//Otherwise use brain at 0
				if(brains != null){
					if(brains[colour] != null){
						brain = brains[colour];
					}else{
						brain = brains[0];
					}
				}
				
				ant = new Ant(uid, ran, antInitialDirection, colour, brain, cell);
				cell.setAnt(ant);
				ants.add(ant);
				species.add(ant);
				
				uid++;
			}
		}
	}
	
	public Cell[][] getWorld() {
		return cells;
	}
	
	public ArrayList<Ant> getAnts() {
		return ants;
	}
	
	/**
	 * @param c
	 * @return 6 neighbours, if the cell is on the edge of the map some neighbours WILL BE NULL
	 */
	private Cell[] getNeighbours(Cell cell) {
		Cell[] neighbours = new Cell[6];
		int i = 0;

		//Clockwise from east
		for(i = 0; i < 6; i++){
			neighbours[i] = getNeighbour(cell, i);
		}

		return neighbours;
	}
	
	private Cell getNeighbour(Cell cell, int i) {
		Cell neighbour = null;
		int r = cell.getRow();
		int c = cell.getCol();
		//Subtract indent from calculations
		//0 if row is unindented, 1 if row is indented
		int k = r % 2;
		
		switch(i){
		//Clockwise from east
		case 0:
			try{
				neighbour = cells[r    ][c + 1    ]; //east
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		case 1:
			try{
				neighbour = cells[r + 1][c + k    ]; //south-east
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		case 2:
			try{
				neighbour = cells[r + 1][c - 1 + k]; //south-west
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		case 3:
			try{
				neighbour = cells[r    ][c - 1    ]; //west
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		case 4:
			try{
				neighbour = cells[r - 1][c - 1 + k]; //north-west
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		case 5:
			try{
				neighbour = cells[r - 1][c + k    ]; //north-east
			}catch(ArrayIndexOutOfBoundsException aiob){
			}
			break;
		default:
			System.out.println("Illegal i Argument in World getNeighbour");
		}
		return neighbour;
	}
	
	public int getSeed() {
		return seed;
	}
	
	public int[] getFoodInAnthills() {
		int[] totals = new int[antsBySpecies.size()];
		
		int r = 0;
		int c  = 0;
		for(r = 0; r < rows; r++){
			for(c = 0; c < cols; c++){
				if(cells[r][c].getAnthill() != 0){
					totals[cells[r][c].getAnthill() - 1]++;
				}
			}
		}
		
		return totals;
	}
	
	public String getAttributes() {
		String s = "";
		int i = 0;
		
		s += "\nseed: " + seed;
		s += "\nrows: " + rows;
		s += "\ncols: " + cols;
		s += "\nrocks: " + rocks;
		s += "\nanthills: " + anthills;
		s += "\nanthill side length: " + anthillSideLength;
		s += "\nfood blob count: " + foodBlobCount;
		s += "\nfood blob side length: " + foodBlobSideLength;
		s += "\nfood blob cell food count: " + foodBlobCellFoodCount;
		s += "\nant initial direction: " + antInitialDirection;
		s += "\ngap: " + gap;
		if(brains != null){
			for(i = 0; i < brains.length; i++){
				s += "\nbrains" + i + ": " + brains[i];
			}
		}else{
			s += "\nbrains: " + null;
		}
		s += "\nants: ";
		for(i = 0; i < antsBySpecies.size(); i++){
			s += antsBySpecies.get(i).size();
			if(i < antsBySpecies.size() - 1){
				s += ", ";
			}
		}
		
		return s;
	}
	
	public String toString() {
		String s = "";
		int r = 0;
		int c = 0;
		
		s += rows + "\r\n";
		s += cols + "\r\n";
		for(r = 0; r < rows; r++){
			//Check remainder when divided by 2 is not 0
			//i.e., current row is odd
			//Will be printed as even row, as index starts at 0
			if(r % 2 != 0){
				s += " ";
			}
			for(c = 0; c < cols; c++){
				s += cells[r][c].toString() + " ";
			}
			s += "\r\n";
		}
		return s;
	}
}
