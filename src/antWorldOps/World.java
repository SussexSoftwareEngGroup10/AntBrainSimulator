package antWorldOps;

import java.util.ArrayList;
import java.util.Random;

import antBrainOps.Brain;

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

public class World {
	//Using a seed means that the same world can be reproduced
	//Seed is generated randomly, but is recorded, so the same seed can be used again
	private int seed = new Random().nextInt(Integer.MAX_VALUE);
	private final Random ran = new Random(seed);
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
	//I would use a ArrayList<Ant>[], but you can't do that in Java
	private final ArrayList<ArrayList<Ant>> ants = new ArrayList<ArrayList<Ant>>();
	
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
		if(seed != 0){
			this.seed = seed;
			ran.setSeed(this.seed);
		}
		
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
		this.rows = cellChars.length;
		this.cols = cellChars[0].length;
		this.brains = brains;
		
		//TODO work out these values from cellChars (effort + boring)
		rocks = 0;
		anthills = 0;
		anthillSideLength = 0;
		foodBlobCount = 0;
		foodBlobSideLength = 0;
		foodBlobCellFoodCount = 0;
		antInitialDirection = 0;
		
		cells = new Cell[rows][cols];
		int r = 0;
		int c = 0;
		
		for(r = 0; r < rows; r++){
			cells[r] = new Cell[cols];
			for(c = 0; c < cols; c++){
				cells[r][c] = new Cell(r, c, cellChars[r][c]);
			}
		}
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
			createAnts(ranRow, ranCol, anthillSideLength, '+');
		}
		
		if(anthills >= 2){
			//Anthill2
			failCount--;
			do{
				failCount++;
				ranRow = ran.nextInt(rows - 2) + 1;
				ranCol = ran.nextInt(cols - 2) + 1;
			}while(!setHex(ranRow, ranCol, anthillSideLength, '-'));
			createAnts(ranRow, ranCol, anthillSideLength, '-');
		}
		
		//No more than 2 anthills can be constructed as any more than this they cannot be 
		//represented uniquely by the 2 char values specified in Cell
		
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
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @param c new value for every cell in the area
	 */
	private boolean createAnts(int row, int col, int sideLength, char ch) {
		//Doesn't check cells, assumes are all locations where ants are desired
		
		int num = -1;
		if(ch == '+'){
			num = 0;
		}else if(ch == '-'){
			num = 1;
		}else{
			return false;
		}
		
		while(ants.size() <= num){
			ants.add(new ArrayList<Ant>());
		}
		ArrayList<Ant> species = ants.get(num);
		Brain brain = brains[num];
		
		Cell centre = cells[row][col];
		
		createAntsRecurse(centre, 0, sideLength, species, brain);
		return true;
	}
	
	private void createAntsRecurse(Cell cell, int recurseNum, int recurseDepth,
		ArrayList<Ant> species, Brain brain) {
		//Need both checks to allow for hexes
		//containing elements on first or last rows or columns
		//and hexes with side length 0
		
		//Don't set if over required length
		if(recurseNum > recurseDepth - 1){
			return;
		}
		
		Ant ant = new Ant(antInitialDirection, brain, cell);
		cell.setAnt(ant);
		species.add(ant);
		
		//Don't recurse if next recurse will take side length over required length
		if(recurseNum > recurseDepth - 2){
			return;
		}
		
		//Sets hexes multiple times, inefficient
		Cell[] neighbours = getNeighbours(cell);
		int i = 0;
		for(i = 0; i < neighbours.length; i++){
			createAntsRecurse(neighbours[i], recurseNum + 1, recurseDepth, species, brain);
		}
	}
	
	public Cell[][] getWorld() {
		return cells;
	}
	
	public ArrayList<ArrayList<Ant>> getAnts() {
		return ants;
	}
	
	/**
	 * @param c
	 * @return 6 neighbours, if the cell is on the edge of the map some neighbours WILL BE NULL
	 */
	private Cell[] getNeighbours(Cell cell) {
		Cell[] neighbours = new Cell[6];
		int r = cell.getRow();
		int c = cell.getCol();
		
		//Subtract indent from calculations
		//0 if row is unindented, 1 if row is indented
		int k = r % 2;
		
		//Clockwise from east
		neighbours[0] = cells[r    ][c + 1    ]; //east
		neighbours[1] = cells[r + 1][c + k    ]; //south-east
		neighbours[2] = cells[r + 1][c - 1 + k]; //south-west
		neighbours[3] = cells[r    ][c - 1    ]; //west
		neighbours[4] = cells[r - 1][c - 1 + k]; //north-west
		neighbours[5] = cells[r - 1][c + k    ]; //north-east
		
		return neighbours;
	}
	
	public int getSeed() {
		return seed;
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
