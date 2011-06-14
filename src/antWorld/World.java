package antWorld;

import engine.Random;
import engine.SoundPlayer;
import utilities.ErrorEvent;
import utilities.IllegalArgumentEvent;
import utilities.Logger;

import antBrain.Brain;

/**
 * @title World
 * @purpose to hold a 2D collection of Cell objects and Ants, and to allow these
 * Ants to execute instructions as they wish, within the rules of the game.
 * This class also stores statistics and informations about the game's progress
 * so far. 
 * 
 * @author pkew20 / 57116
 * @version 1.0
 */
public class World implements Cloneable {
	//Using a seed means that the same world can be reproduced
	//Seed is generated randomly, but is recorded, so the same seed can be used again
	private final int seed;
	private final Random ran;
	
	private final int rows;
	private final int cols;
	
	private final int rocks;
	private final boolean rockAreaConsistency;
	private final boolean borderRocks;
	private final int anthills;
	private final int anthillSideLength;
	private final boolean anthillAreaConsistency;
	private final int foodBlobCount;
	private final int foodBlobSideLength;
	private final int foodBlobCellFoodCount;
	private final boolean foodBlobAreaConsistency;
	
	private final int antInitialDirection;
	
	private final int gap; //gap between objects in world
	
	private Cell[][] cells; //indent every second line, starting at cells[1]
	
	//I use 2 different ways of storing the pointers to the ants in the world
	//These should be kept in sync as they only use pointers,
	//ants are added to both, and never removed
	//Ants are added in UID order,
	//Arrays.sort() will restore the list to UID order, as it was created
	private Ant[] ants;
	private Ant[][] antsBySpecies;
	
	//Sound player for the possible sound effects ants can produce
	//This can be null (in the case of contests, so checks for null need to be
	//made before sounds are played)
	private SoundPlayer soundPlayer = null;
	
	//Ant colours:
	//'+' == black
	//'-' == red
	
	/**
	 * @title getContestWorld
	 * @purpose to get a new World with many of the default settings for
	 * parameters
	 * @param seed 0 to use random seed, else create a deterministic World where
	 * all the Ants will move in a fixed seeded pattern
	 * @param soundPlayer the object with which to play sounds, if any
	 * @return a world which is fit to be used in a contest
	 * @throws ErrorEvent if objects specified don't fit in area specified
	 * @throws IllegalArgumentEvent if any arguments are illegal
	 */
	public static World getContestWorld(int seed, SoundPlayer soundPlayer) throws ErrorEvent {
		return getRegularWorld(seed, 140, 140, 13, soundPlayer);
	}
	
	/**
	 * @title getContestWorld
	 * @purpose to get a new World with some of the default settings for
	 * parameters
	 * @param seed 0 to use random seed, else create a deterministic World where
	 * all the Ants will move in a fixed seeded pattern
	 * @param rows the number of rows in the World to construct
	 * @param cols the number of columns in the World to construct
	 * @param rocks the number of rocks, not including rocks around the border
	 * of the World, to add to the World
	 * @param soundPlayer the object with which to play sounds, if any
	 * @return a world which is fit to be used in a contest
	 * @throws ErrorEvent if objects specified don't fit in area specified
	 * @throws IllegalArgumentEvent if any arguments are illegal 
	 */
	public static World getRegularWorld(int seed, int rows, int cols, int rocks,
		SoundPlayer soundPlayer) throws ErrorEvent {
		return new World(seed, rows, cols, rocks, 2, 7, 10, 5, 5, 0, 1, soundPlayer);
	}
	
	/**
	 * @title getContestWorld
	 * @purpose the highest level of specification of parameters, as all
	 * parameters can be set by the user, unlike the static methods
	 * @param seed 0 to use random seed, else create a deterministic World where
	 * all the Ants will move in a fixed seeded pattern
	 * @param rows the number of rows in the World to construct
	 * @param cols the number of columns in the World to construct
	 * @param rocks the number of rocks, not including rocks around the border
	 * of the World, to add to the World
	 * @param anthills the number of anthills to create, one per Ant species
	 * @param anthillSideLength the length of the sides of the anthills
	 * @param foodBlobCount the number of food blobs to create in the World
	 * @param foodBlobSideLength the length of all sides of the food blobs
	 * @param foodBlobCellFoodCount the amount of food in each food blob Cell
	 * @param antInitialDirection the starting direction of 
	 * @param gap minimum the number of hexes between non-food objects
	 * @param soundPlayer the object with which to play sounds, if any
	 * @return a world which is fit to be used in a contest
	 * @throws ErrorEvent if objects specified don't fit in area specified
	 * @throws IllegalArgumentEvent if any arguments are illegal 
	 */
	public World(int seed, int rows, int cols, int rocks,
		int anthills, int anthillSideLength, int foodBlobCount,
		int foodBlobSideLength, int foodBlobCellFoodCount,
		int antInitialDirection, int gap, SoundPlayer soundPlayer) throws ErrorEvent {
		try{
			//Can either use a random or predefined seed
			this.seed = seed;
			this.ran = new Random(this.seed);

			this.rows = rows;
			this.cols = cols;
			this.rocks = rocks;
			this.rockAreaConsistency = true;
			this.borderRocks = true;
			this.anthills = anthills;
			this.anthillSideLength = anthillSideLength;
			this.anthillAreaConsistency = true;
			this.foodBlobCount = foodBlobCount;
			this.foodBlobSideLength = foodBlobSideLength;
			this.foodBlobCellFoodCount = foodBlobCellFoodCount;
			this.foodBlobAreaConsistency = true;
			this.antInitialDirection = antInitialDirection;
			this.gap = gap;
			this.soundPlayer = soundPlayer;

			//Initialise every cell to be clear
			int r = 0;
			int c = 0;
			this.cells = new Cell[rows][cols];
			for(r = 0; r < rows; r++){
				this.cells[r] = new Cell[cols];
				for(c = 0; c < cols; c++){
					this.cells[r][c] = new Cell(r, c, '.');
				}
			}

			Cell current;
			//Setup markers in each cell
			//Use this. for fields, local variables created above
			for(r = 0; r < rows; r++){
				for(c = 0; c < cols; c++){
					current = this.cells[r][c];
					current.setNeighbours(getNeighbours(current));
					current.setupMarkers(this.anthills);
				}
			}

			createWorld();
		} catch (IllegalArgumentEvent e) {
			throw new ErrorEvent(e.getMessage(), e);
		}
	}
	
	/**
	 * @title World
	 * @purpose to create a new world with attributes inferred from given
	 * array, this is used by the WorldParser class and clone.
	 * @param cellChars the 2D character array to use as the new World's
	 * @throws ErrorEvent if any Cells in the array are invalid
	 */
	protected World(char[][] cellChars, SoundPlayer soundPlayer) throws ErrorEvent {
		this.soundPlayer = soundPlayer;
		//Random is not needed for world generation, but is for Ant.step()
		this.seed = 1;
		this.ran = new Random(this.seed);
		
		this.rows = cellChars.length;
		this.cols = cellChars[0].length;
		
		this.cells = new Cell[this.rows][this.cols];
		int r = 0;
		int c = 0;
		
		//Setup cells
		for(r = 0; r < this.rows; r++){
			this.cells[r] = new Cell[this.cols];
			for(c = 0; c < this.cols; c++){
				try {
					this.cells[r][c] = new Cell(r, c, cellChars[r][c]);
				} catch (IllegalArgumentEvent e) {
					throw new ErrorEvent(e.getMessage(), e);
				}
			}
		}
		
		//Setup neighbours for each cell
		Cell current;
		for(r = 0; r < this.rows; r++){
			for(c = 0; c < this.cols; c++){
				current = this.cells[r][c];
				try {
					current.setNeighbours(getNeighbours(current));
				} catch (IllegalArgumentEvent e) {
					Logger.log(e);
				}
			}
		}
		
		//Assumptions:
		//There are no more than 2 types of anthill in the world ('+' and '-')
		//Food blobs are square, and all have the same side length
		//All cells which contain food contain the same amount of food
		
		//Confirmed works for tournament worlds,
		//getAttributes on generated world,
		//then compared results with those given after writing
		//and reading world in through a WorldParser
		
		//rocks (not including border, if any)
		int rocks = 0;
		for(r = 1; r < this.rows - 1; r++){
			for(c = 1; c < this.cols - 1; c++){
				if(this.cells[r][c].isRocky()) rocks++;
			}
		}
		this.rocks = rocks;
		
		//rockAreaConsistency
		boolean rockAreaConsistency = true;
		rockAreaLoop:
			for(r = 1; r < this.rows - 1; r++){
				for(c = 1; c < this.cols - 1; c++){
					current = this.cells[r][c];
					if(current.isRocky()){
						//Cells must be surrounded by clear cells
						for(Cell neighbour : current.getNeighbours()){
							if(neighbour.toChar() != '.'){
								rockAreaConsistency = false;
								break rockAreaLoop;
							}
						}
					}
				}
			}
		this.rockAreaConsistency = rockAreaConsistency;
		
		//borderRocks
		this.borderRocks = checkBorder(0, '#');
		if(!this.borderRocks){
			throw new ErrorEvent("border not completely rocky");
		}
		
		//anthills
		boolean[] existingAnthills = {false, false};
		int[][] anthillLocs = new int[2][2];
		for(r = 0; r < this.rows && (!existingAnthills[0] || !existingAnthills[1]); r++){
			for(c = 0; c < this.cols && (!existingAnthills[0] || !existingAnthills[1]); c++){
				current = this.cells[r][c];
				if(!existingAnthills[0]){
					if(current.getAnthill() == 1){
						existingAnthills[0] = true;
						anthillLocs[0][0] = r;
						anthillLocs[0][1] = c;
					}
				}
				if(!existingAnthills[1]){
					if(current.getAnthill() == 2){
						existingAnthills[1] = true;
						anthillLocs[1][0] = r;
						anthillLocs[1][1] = c;
					}
				}
			}
		}
		int anthills = 0;
		if(existingAnthills[0]) anthills++;
		if(existingAnthills[1]) anthills++;
		this.anthills = anthills;
		
		//anthillSideLength
		int anthillType;
		int lenC = 0;
		anthillSideLengthLoop:
			for(r = 0; r < this.rows; r++){
				for(c = 0; c < this.cols; c++){
					current = this.cells[r][c];
					
					if(current.getAnthill() != 0){
						anthillType = current.getAnthill();
						for(lenC = c + 1; lenC < this.cols; lenC++){
							if(this.cells[r][lenC].getAnthill() != anthillType){
								break anthillSideLengthLoop;
							}
						}
						break anthillSideLengthLoop;
					}
				}
			}
		this.anthillSideLength = lenC - c;
		
		//find anthill centres
		for(int anthill = 0; anthill < this.anthills; anthill++){
			if(anthillLocs[anthill][0] % 2 == 0
				&& (anthillLocs[anthill][0] % 2) + this.anthillSideLength - 1 == 1){
				anthillLocs[anthill][1] += (this.anthillSideLength / 2) - 1;
			}else{
				anthillLocs[anthill][1] += this.anthillSideLength / 2;
			}
			anthillLocs[anthill][0] += this.anthillSideLength - 1;
		}
		
		//anthillAreaConsistency
		int[][] anthillAreas = new int[this.rows][this.cols];
		if(existingAnthills[0]){
			setHexBool(anthillLocs[0][0], anthillLocs[0][1], 
				this.anthillSideLength, '+', anthillAreas, 1);
		}
		if(existingAnthills[1]){
			setHexBool(anthillLocs[1][0], anthillLocs[1][1], 
				this.anthillSideLength, '-', anthillAreas, 2);
		}
		
		boolean anthillAreaConsistency = true;
		anthillAreaLoop:
			for(r = 0; r < this.rows; r++){
				for(c = 0; c < this.cols; c++){
					if(anthillAreas[r][c] != 0){
						if(this.cells[r][c].getAnthill() != anthillAreas[r][c]){
							anthillAreaConsistency = false;
							break anthillAreaLoop;
						}
					}else{
						if(this.cells[r][c].getAnthill() != 0){
							anthillAreaConsistency = false;
							break anthillAreaLoop;
						}
					}
				}
			}
		this.anthillAreaConsistency = anthillAreaConsistency;
		
		//foodBlobCount, foodBlobSideLength, foodBlobCellFoodCount
		int foodBlobCount = 0;
		int foodBlobSideLength = -1;
		int foodBlobCellFoodCount = -1;
		boolean[][] foodBlobAreas = new boolean[this.rows][this.cols];
		int currentFood;
		r = 0;
		c = 0;
		while(r < this.rows || c < this.cols){
			foodBlobSideLengthLoop:
				for(; r < this.rows; r++){
					for(c = 0; c < this.cols; c++){
						if(!foodBlobAreas[r][c]){
							current = this.cells[r][c];
							currentFood = current.foodCount();
							if(currentFood > 0){
								foodBlobCount++;
								if(foodBlobCellFoodCount == -1){
									foodBlobCellFoodCount = currentFood;
								}
								for(lenC = c + 1; lenC < this.cols; lenC++){
									if(!this.cells[r][lenC].hasFood()){
										break;
									}
								}
								if(foodBlobSideLength == -1){
									foodBlobSideLength = lenC - c;
								}
								setRectBool(r, c, foodBlobSideLength, foodBlobSideLength,
									foodBlobAreas, true);
								break foodBlobSideLengthLoop;
							}
						}
					}
				}
		}
		this.foodBlobCount = foodBlobCount;
		this.foodBlobSideLength = foodBlobSideLength;
		this.foodBlobCellFoodCount = foodBlobCellFoodCount;
		
		//foodBlobAreaConsistency
		boolean foodBlobAreaConsistency = true;
		foodBlobAreaConsistencyLoop:
			for(r = 0; r < this.rows; r++){
				for(c = 0; c < this.cols; c++){
					if(foodBlobAreas[r][c]){
						if(this.cells[r][c].foodCount() != this.foodBlobCellFoodCount){
							foodBlobAreaConsistency = false;
							break foodBlobAreaConsistencyLoop;
						}
					}else{
						if(this.cells[r][c].hasFood()){
							foodBlobAreaConsistency = false;
							break foodBlobAreaConsistencyLoop;
						}
					}
				}
			}
		this.foodBlobAreaConsistency = foodBlobAreaConsistency;
		
		//check minimum gap
		Cell neighbour;
		int gap = 1;
		gapCheck:
			for(r = 1; r < this.rows - 1; r++){
				for(c = 1; c < this.cols - 1; c++){
					current = this.cells[r][c];
					if(current.getAnthill() != 0){
						anthillType = current.getAnthill();
						for(int i = 0; i < 6; i++){
							neighbour = current.getNeighbour(i);
							if(neighbour.isRocky() ||
								(neighbour.getAnthill() != 0
								&& neighbour.getAnthill() != anthillType)){
								gap = 0;
								break gapCheck;
							}
						}
					}else if(current.hasFood()){
						for(int i = 0; i < 6; i++){
							neighbour = current.getNeighbour(i);
							if(neighbour.isRocky() || neighbour.getAnthill() != 0){
								gap = 0;
								break gapCheck;
							}
						}
					}else if(current.isRocky()){
						for(int i = 0; i < 6; i++){
							neighbour = current.getNeighbour(i);
							if(neighbour.isRocky() || neighbour.hasFood() || neighbour.getAnthill() != 0){
								gap = 0;
								break gapCheck;
							}
						}
					}
				}
			}
		this.gap = gap;
		
		this.antInitialDirection = 0;
		
		for(r = 0; r < this.rows; r++){
			for(c = 0; c < this.cols; c++){
				this.cells[r][c].setupMarkers(this.anthills);
			}
		}
		
		createAnts();
	}
	
	private World(int seed, int rows, int cols, int rocks, boolean rockAreaConsistency,
		boolean borderRocks, int anthills, int anthillSideLength, boolean anthillAreaConsistency,
		int foodBlobCount, int foodBlobSideLength, int foodBlobCellFoodCount,
		boolean foodBlobAreaConsistency, int antInitialDirection, int gap, Cell[][] cells, SoundPlayer soundPlayer) {
		this.seed = seed;
		this.ran = new Random(seed);
		this.rows = rows;
		this.cols = cols;
		this.rocks = rocks;
		this.rockAreaConsistency = rockAreaConsistency;
		this.borderRocks = borderRocks;
		this.anthills = anthills;
		this.anthillSideLength = anthillSideLength;
		this.anthillAreaConsistency = anthillAreaConsistency;
		this.foodBlobCount = foodBlobCount;
		this.foodBlobSideLength = foodBlobSideLength;
		this.foodBlobCellFoodCount = foodBlobCellFoodCount;
		this.foodBlobAreaConsistency = foodBlobAreaConsistency;
		this.antInitialDirection = antInitialDirection;
		this.gap = gap;
		this.cells = cells;
		this.soundPlayer = soundPlayer;

		for(int r = this.rows - 1; r >= 0; r--){
			for(int c = this.cols - 1; c >= 0; c--){
				Cell current = cells[r][c];
				try {
					current.setNeighbours(getNeighbours(current));
				} catch (IllegalArgumentEvent e) {
					Logger.log(e);
				}
				current.setupMarkers(this.anthills);
			}
		}
		
		createAnts();
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
	 * @throws ErrorEvent 
	 * @throws IllegalArgumentEvent 
	 */
	private void createWorld() throws ErrorEvent, IllegalArgumentEvent {
		int maxFails = 100;
		int failCount = 0;
		int ranRow;
		int ranCol;
		int i = 0;
		
		//Border
		if(!setBorderRocks()){
			if(failCount > maxFails){
				throw new ErrorEvent("failed to set border rocks 1 time(s)");
			}
		}
		
		if(this.anthills >= 1){
			//Anthill1
			failCount = -1;
			do{
				if(failCount > maxFails){
					throw new ErrorEvent("failed to set black anthill " + failCount + " time(s)");
				}
				failCount++;
				ranRow = this.ran.randomInt(this.rows - 2) + 1;
				ranCol = this.ran.randomInt(this.cols - 2) + 1;
			}while(!setHex(ranRow, ranCol, this.anthillSideLength, '+'));
		}
		
		if(this.anthills >= 2){
			//Anthill2
			failCount = -1;
			do{
				if(failCount > maxFails){
					throw new ErrorEvent("failed to set black anthill " + failCount + " time(s)");
				}
				failCount++;
				ranRow = this.ran.randomInt(this.rows - 2) + 1;
				ranCol = this.ran.randomInt(this.cols - 2) + 1;
			}while(!setHex(ranRow, ranCol, this.anthillSideLength, '-'));
		}
		
		//No more than 2 anthills can be constructed as any more than this they cannot be 
		//represented uniquely by the 2 char values specified in Cell
		createAnts();
		
		//Foods
		for(i = 0; i < this.foodBlobCount; i++){
			failCount = -1;
			do{
				if(failCount > maxFails){
					throw new ErrorEvent("failed to set food blob " + i + " of " + this.foodBlobCount
						+ " " + failCount + " time(s)");
				}
				failCount++;
				ranRow = this.ran.randomInt(this.rows - 2) + 1;
				ranCol = this.ran.randomInt(this.cols - 2) + 1;
			}while(!setRect(ranRow, ranCol,	this.foodBlobSideLength,
				this.foodBlobSideLength, (char) (this.foodBlobCellFoodCount + 48)));
		}
		
		//Rocks
		for(i = 0; i < this.rocks; i++){
			failCount = -1;
			do{
				if(failCount > maxFails){
					throw new ErrorEvent("failed to set rock " + i + " of " + this.rocks
						+ " " + failCount + " time(s)");
				}
				failCount++;
				ranRow = this.ran.randomInt(this.rows - 2) + 1;
				ranCol = this.ran.randomInt(this.cols - 2) + 1;
			}while(!setCell(ranRow, ranCol, '#'));
		}
	}
	
	/**
	 * @return set the first and last rows and columns as rocks
	 * @throws IllegalArgumentEvent 
	 */
	private boolean setBorderRocks() throws IllegalArgumentEvent {
		if(!checkBorder(this.gap, '.')){
			return false;
		}
		
		int r = 0;
		int c = 0;
		
		//First column
		c = 0;
		for(r = 0; r < this.rows; r++){
			this.cells[r][c].setCell('#');
		}
		
		//Last column
		c = this.cols - 1;
		for(r = 0; r < this.rows; r++){
			this.cells[r][c].setCell('#');
		}
		
		//First row
		r = 0;
		for(c = 0; c < this.cols; c++){
			this.cells[r][c].setCell('#');
		}
		
		//Last row
		r = this.rows - 1;
		for(c = 0; c < this.cols; c++){
			this.cells[r][c].setCell('#');
		}
		
		return true;
	}
	
	/**
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @param c new value for every cell in the area
	 * @throws IllegalArgumentEvent 
	 */
	private boolean setHex(int row, int col, int sideLength, char ch) throws IllegalArgumentEvent {
		if(!checkHex(row, col, sideLength, this.gap, '.')){
			return false;
		}
		
		Cell centre = this.cells[row][col];
		setHexRecurse(centre, 0, sideLength, ch);
		return true;
	}
	
	/**
	 * @param cell
	 * @param recurseNum
	 * @param recurseDepth
	 * @param ch
	 * @throws IllegalArgumentEvent 
	 */
	private void setHexRecurse(Cell cell, int recurseNum, int recurseDepth, char ch) throws IllegalArgumentEvent {
		//Need both checks to allow for hexes
		//containing elements on first or last rows or columns
		//and hexes with side length 0
		
		//Don't set if over required length
		if(recurseNum > recurseDepth - 1){
			return;
		}
		
		//The check is probably slightly more efficient
		//than overwriting identical values
		if(cell.toChar() != ch){
			cell.setCell(ch);
		}
		
		//Don't recurse if next recurse will take side length over required length
		if(recurseNum > recurseDepth - 2){
			return;
		}
		
		//Sets hexes multiple times, inefficient
		Cell[] neighbours = getNeighbours(cell);
		for(Cell neighbour : neighbours){
			setHexRecurse(neighbour, recurseNum + 1, recurseDepth, ch);
		}
	}
	
	/**
	 * @param row
	 * @param col
	 * @param height
	 * @param width
	 * @param ch
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private boolean setRect(int row, int col, int height, int width, char ch) throws IllegalArgumentEvent {
		if(!checkRect(row, col, height, width, '.')){
			return false;
		}
		
		int r = 0;
		int c = 0;
		
		for(r = row; r < row + height; r++){
			for(c = col; c < col + width; c++){
				this.cells[r][c].setCell(ch);
			}
		}
		return true;
	}
	
	/**
	 * @param row
	 * @param col
	 * @param ch
	 * @return
	 * @throws IllegalArgumentEvent 
	 */
	private boolean setCell(int row, int col, char ch) throws IllegalArgumentEvent {
		if(!checkCellClear(row, col)){
			return false;
		}
		
		this.cells[row][col].setCell(ch);
		return true;
	}
	
	/**
	 * @return
	 */
	private boolean checkBorder(int gap, char ch) {
		int r = 0;
		int c = 0;
		
		//First column + gap
		for(c = 0; c < gap + 1; c++){
			for(r = 0; r < this.rows; r++){
				if(this.cells[r][c].toChar() != ch){
					return false;
				}
			}
		}
		
		//Last column + gap
		for(c = this.cols - 1 - gap; c < this.cols; c++){
			for(r = 0; r < this.rows; r++){
				if(this.cells[r][c].toChar() != ch){
					return false;
				}
			}
		}
		//First row + gap
		for(r = 0; r < gap + 1; r++){
			for(c = 0; c < this.cols; c++){
				if(this.cells[r][c].toChar() != ch){
					return false;
				}
			}
		}
		
		//Last row + gap
		for(r = this.rows - 1 - gap; r < this.rows; r++){
			for(c = 0; c < this.cols; c++){
				if(this.cells[r][c].toChar() != ch){
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param row
	 * @param col
	 * @param anthillSideLength
	 * @param ch
	 * @param anthillAreas
	 * @param set
	 * @return
	 */
	private void setHexBool(int r, int c, int anthillSideLength, char ch,
		int[][] anthillAreas, int set){
		
		setHexBoolRecurse(r, c, 0, anthillSideLength, ch, anthillAreas, set);
	}
	
	/**
	 * @param row
	 * @param col
	 * @param recurseNum
	 * @param recurseDepth
	 * @param ch
	 * @param set
	 */
	private void setHexBoolRecurse(int r, int c, int recurseNum, int recurseDepth, char ch,
		int[][] anthillAreas, int set) {
		//Don't set if over required length
		if(recurseNum > recurseDepth - 1){
			return;
		}
		
		//The check is probably slightly more efficient
		//than overwriting identical values
		if(anthillAreas[r][c] != set){
			anthillAreas[r][c] = set;
		}
		
		//Don't recurse if next recurse will take side length over required length
		if(recurseNum > recurseDepth - 2){
			return;
		}
		
		//Set neighbours
		int k = r % 2;
		//Clockwise from east
		try{
			setHexBoolRecurse(r, c + 1, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //east
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
		try{
			setHexBoolRecurse(r + 1, c + k, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //south-east
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
		try{
			setHexBoolRecurse(r + 1, c - 1 + k, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //south-west
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
		try{
			setHexBoolRecurse(r, c - 1, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //west
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
		try{
			setHexBoolRecurse(r - 1, c - 1 + k, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //north-west
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
		try{
			setHexBoolRecurse(r - 1, c + k, recurseNum + 1, recurseDepth,
				ch, anthillAreas, set); //north-east
		}catch(ArrayIndexOutOfBoundsException e){
			//Neighbour[i] is off the edge of the world
		}
	}
	
	private void setRectBool(int row, int col, int height, int width,
		boolean[][] foodBlobAreas, boolean set) {
		
		for(int r = row; r < row + height; r++){
			for(int c = col; c < col + width; c++){
				foodBlobAreas[r][c] = set;
			}
		}
	}
	
	/**
	 * @param row of centre hex
	 * @param col of centre hex
	 * @param sideLength
	 * @return
	 */
	private boolean checkHex(int row, int col, int sideLength, int gap, char ch) {
			Cell centre = this.cells[row][col];
			
			try{
				return checkHexRecurse(centre, 0, sideLength + gap, ch);
			}catch(ArrayIndexOutOfBoundsException e){
				return false;
			}
		}
		
	/**
	 * @param cell
	 * @param recurseNum
	 * @param recurseDepth
	 * @return
	 */
	private boolean checkHexRecurse(Cell cell, int recurseNum, int recurseDepth, char ch) {
		if(cell == null || cell.toChar() != '.'){
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
			if(!checkHexRecurse(neighbours[i], recurseNum + 1, recurseDepth, ch)){
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
	private boolean checkRect(int row, int col, int height, int width, char ch) {
		int r = 0;
		int c = 0;
		
		try{
			for(r = row - this.gap; r < row + height + this.gap; r++){
				for(c = col - this.gap; c < col + width + this.gap; c++){
					if(this.cells[r][c].toChar() != ch){
						return false;
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean checkCellClear(int row, int col) {
		int r = 0;
		int c = 0;
		
		for(r = row - this.gap; r <= row + this.gap; r++){
			for(c = col - this.gap; c <= col + this.gap; c++){
				if(this.cells[r][c].toChar() != '.'){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Call after anthills generated, puts an ant in each anthill hex
	 */
	private void createAnts() {
		Cell cell;
		Ant ant = null;
		int colour;
		int black = 0;
		int red = 0;
		int uid = 0;
		
		for(int r = 0; r < this.rows; r++){
			for(int c = 0; c < this.cols; c++){
				cell = this.cells[r][c];
				
				if(cell.toChar() == '+'){
					black++;
				}else if(cell.toChar() == '-'){
					red++;
				}
			}
		}
		
		this.ants = new Ant[black + red];
		this.antsBySpecies = new Ant[this.anthills][];
		this.antsBySpecies[0] = new Ant[black];
		this.antsBySpecies[1] = new Ant[red];
		int[] nextAntIndex = {0, 0};
		
		//Put new ants onto each anthill cell, and into the right arrays
		for(int r = 1; r < this.rows - 1; r++){
			for(int c = 1; c < this.cols - 1; c++){
				colour = -1;
				cell = this.cells[r][c];
				
				if(cell.toChar() == '+'){
					colour = 0;
				}else if(cell.toChar() == '-'){
					colour = 1;
				}else{
					continue;
				}
				
				//Create and store ant
				try {
					ant = new Ant(uid, this.ran, this.antInitialDirection, colour, cell, this.soundPlayer);
				} catch (IllegalArgumentEvent e) {
					Logger.log(e);
				}
				cell.setAnt(ant);
				this.ants[nextAntIndex[0] + nextAntIndex[1]] = ant;
				//Use nextAntIndex[colour] value BEFORE increment (opposite to ++i)
				this.antsBySpecies[colour][nextAntIndex[colour]++] = ant;
				
				uid++;
			}
		}
		
//		//ArrayList version
//		//More efficient to store pointers to each ArrayList than get every time
//		ArrayList<Ant> species0 = new ArrayList<Ant>();
//		ArrayList<Ant> species1 = new ArrayList<Ant>();
//		this.antsBySpecies.add(species0);
//		this.antsBySpecies.add(species1);
//		
//		//Put new ants onto each anthill cell, and into the right arrays
//		for(r = 0; r < this.rows; r++){
//			for(c = 0; c < this.cols; c++){
//				colour = -1;
//				cell = this.cells[r][c];
//				
//				if(cell.toChar() == '+'){
//					colour = 0;
//				}else if(cell.toChar() == '-'){
//					colour = 1;
//				}else{
//					continue;
//				}
//				
//				//Create and store ant
//				ant = new Ant(uid, this.ran, this.antInitialDirection, colour, cell);
//				cell.setAnt(ant);
//				this.ants.add(ant);
//				if(colour == 0){
//					species0.add(ant);
//				}else if(colour == 1){
//					species1.add(ant);
//				}
//				
//				uid++;
//			}
//		}
	}
	
	/**
	 * @title hexArea
	 * @purpose to calculate the area of a hexagon with the side length given
	 * @param len the side length of the hypothetical Cell hex
	 * @return the area of a hypothetical hexagon of Cells with the length
	 * specified in len
	 */
	public static int hexArea(int len) {
		//Calculates the number of cells in a hex (e.g. anthill) given side length n
		if(len < 1){
			return 0;
		}
		if(len == 1){
			return 1;
		}
		return hexArea(len - 1) + ((len - 1) * 6);
	}
	
	/**
	 * @title setBrain
	 * @purpose to set the Brain of the Ants of the species specified to the
	 * given Brain
	 * @param brain the Brain to set
	 * @param i the species of Ant to set
	 */
	public void setBrain(Brain brain, int i) {
		for(Ant ant : this.antsBySpecies[i]){
			try{
				ant.setBrain(brain);
			}catch(NullPointerException e){
				System.out.println(ant);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @title getCells
	 * @purpose to get the Cell array of this World.
	 * @return the Cell array of this World
	 */
	public Cell[][] getCells() {
		return this.cells;
	}
	
	/**
	 * @title getChars
	 * @purpose to get the char array version of this World's Cells
	 * @return the World's Cell array converted to chars
	 */
	public char[][] getChars() {
		char[][] chars = new char[this.rows][this.cols];
		for(int r = 0; r < this.rows; r++){
			for(int c = 0; c < this.cols; c++){
				chars[r][c] = this.cells[r][c].toChar();
			}
		}
		return chars;
	}
	
	/**
	 * @title getAnts
	 * @purpose to get this World's ants
	 * @return this World's Ants, which were initially ordered from top left
	 * to bottom right, left first, then down, but may have moved since then
	 */
	public Ant[] getAnts() {
		return this.ants;
	}
	
	/**
	 * @title getAntsBySpecies
	 * @purpose to get this World's ants
	 * @return this World's Ants, which were initially ordered from top left
	 * to bottom right, left first, then down, but may have moved since then
	 */
	public Ant[][] getAntsBySpecies() {
		return this.antsBySpecies;
	}
	
	/**
	 * @title step
	 * @purpose permits each Ant in the World to perform one step, in order of
	 * UID, so from top left to bottom right, horizontally first
	 */
	public void step() {
		for(Ant ant : this.ants){
			ant.step();
		}
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
	
	/**
	 * @param cell
	 * @param i
	 * @return
	 */
	private Cell getNeighbour(Cell cell, int i) {
		Cell neighbour = null;
		int r = cell.getRow();
		int c = cell.getCol();
		//Subtract indent from calculations
		//0 if row is unindented, 1 if row is indented
		//Neighbours of border cells off the edge of the World are null
		int k = r % 2;
		
		switch(i){
		//Clockwise from east
		case 0:
			try{
				neighbour = this.cells[r    ][c + 1    ]; //east
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		case 1:
			try{
				neighbour = this.cells[r + 1][c + k    ]; //south-east
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		case 2:
			try{
				neighbour = this.cells[r + 1][c - 1 + k]; //south-west
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		case 3:
			try{
				neighbour = this.cells[r    ][c - 1    ]; //west
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		case 4:
			try{
				neighbour = this.cells[r - 1][c - 1 + k]; //north-west
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		case 5:
			try{
				neighbour = this.cells[r - 1][c + k    ]; //north-east
			}catch(ArrayIndexOutOfBoundsException e){
				//Neighbour[i] is off the edge of the world, set to null
			}
			break;
		default:
			Logger.log(new IllegalArgumentEvent("Illegal i " +
				"argument in World getNeighbour"));
		}
		return neighbour;
	}
	
	/**
	 * @title getSeed
	 * @purpose to get the seed of this World
	 * @return the seed of this World
	 */
	public int getSeed() {
		return this.seed;
	}
	
	/**
	 * @title getFoodInAnthills
	 * @purpose to get the total food in each Cell, for each anthill
	 * @return the total amount of food in each anthill
	 */
	public int[] getFoodInAnthills() {
		int[] totals = new int[this.antsBySpecies.length];
		
		int r = 0;
		int c  = 0;
		Cell current;
		for(r = 0; r < this.rows; r++){
			for(c = 0; c < this.cols; c++){
				current = this.cells[r][c];
				if(current.getAnthill() != 0){
					if(current.hasFood()){
						totals[this.cells[r][c].getAnthill() - 1] += current.foodCount();
					}
				}
			}
		}
		
		return totals;
	}
	
	/**
	 * @title survivingAntsBySpecies
	 * @purpose to get a list of the remaining ants for each species
	 * @return the number of surviving ants in each species
	 */
	public int[] survivingAntsBySpecies() {
		int[] survivors = new int[this.antsBySpecies.length];
		int i = 0;
		
		for(i = 0; i < this.antsBySpecies.length; i++){
			for(Ant ant : this.antsBySpecies[i]){
				if(ant.isAlive()){
					survivors[i]++;
				}
			}
		}
		return survivors;
	}
	
	/**
	 * @title isContest
	 * @purpose to get whether this World is suitable for running a contest
	 * @return true if all of this World's attributes are appropriate for a
	 * contest
	 */
	public boolean isContest() {
		if(this.rows == 140
		&& this.cols == 140
		&& this.rocks == 13
		&& this.rockAreaConsistency
		&& this.borderRocks
		&& this.anthills == 2
		&& this.anthillSideLength == 7
		&& this.anthillAreaConsistency
		&& this.foodBlobCount == 10
		&& this.foodBlobSideLength == 5
		&& this.foodBlobCellFoodCount == 5
		&& this.foodBlobAreaConsistency
		&& this.antInitialDirection == 0
		&& this.gap == 1){
			return true;
		}
		return false;
	}
	
	/**
	 * @title getAttributes
	 * @purpose to determine and return the attributes of the World
	 * @return a list of this World's attributes
	 */
	public String getAttributes() {
		String s = "";
		
		s += "\nseed: " + this.seed;
		s += "\nrows: " + this.rows;
		s += "\ncols: " + this.cols;
		s += "\nrocks: " + this.rocks;
		s += "\nrockAreaConsistency: " + this.rockAreaConsistency;
		s += "\nborderRocks: " + this.borderRocks;
		s += "\nanthills: " + this.anthills;
		s += "\nanthill side length: " + this.anthillSideLength;
		s += "\nanthillAreaConsistency: " + this.anthillAreaConsistency;
		s += "\nfood blob count: " + this.foodBlobCount;
		s += "\nfood blob side length: " + this.foodBlobSideLength;
		s += "\nfood blob cell food count: " + this.foodBlobCellFoodCount;
		s += "\nfoodBlobAreaConsistency: " + this.foodBlobAreaConsistency;
		s += "\nant initial direction: " + this.antInitialDirection;
		s += "\ngap: " + this.gap;
		s += "\nants: ";
		for(int i = 0; i < this.antsBySpecies.length; i++){
			s += this.antsBySpecies[i].length;
			if(i < this.antsBySpecies.length - 1){
				s += ", ";
			}
		}
		s += "\n";
		if(!isContest()){
			s += "not ";
		}
		s += "suitable for contest";
		
		return s;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 * 
	 * @title clone
	 * @purpose to return a copy of this World
	 * @return a World with a cloned Cell[][], does not copy Ants, generates
	 * new Ants on anthills, equivilent to calling World(char[][]) with one
	 * parsed from a file, but without the slow checks
	 */
	@Override
	public Object clone() {
		Cell[][] newCells = new Cell[this.rows][this.cols];
		for(int r = this.rows - 1; r >= 0; r--){
			for(int c = this.cols - 1; c >= 0; c--){
				newCells[r][c] = (Cell) this.cells[r][c].clone();
			}
		}
		World world = new World(this.seed, this.rows, this.cols, this.rocks,
			this.rockAreaConsistency, this.borderRocks, this.anthills, this.anthillSideLength,
			this.anthillAreaConsistency, this.foodBlobCount, this.foodBlobSideLength,
			this.foodBlobCellFoodCount,	this.foodBlobAreaConsistency, this.antInitialDirection,
			this.gap, newCells, this.soundPlayer);
		return world;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * @title toString
	 * @purpose to obtain a graphic representation of the World
	 * @return a String representation of all of the Cells in the World in a 
	 * 2D grid
	 */
	@Override
	public String toString() {
		//Returns the world in a format identical to that found in a readable text file,
		//such that if the toString of a world were written to a file and read in through
		//a parser, the world would be identical
		//(except for ants and food in anthills, that'd break the parser)
		String s = "";
		int r = 0;
		int c = 0;
		
		s += this.rows + "\r\n";
		s += this.cols + "\r\n";
		for(r = 0; r < this.rows; r++){
			//Check remainder when divided by 2 is not 0
			//i.e., current row is odd
			//Will be printed as even row, as index starts at 0
			if(r % 2 != 0){
				s += " ";
			}
			for(c = 0; c < this.cols; c++){
				s += this.cells[r][c].toString() + " ";
			}
			s += "\r\n";
		}
		return s;
	}
}
