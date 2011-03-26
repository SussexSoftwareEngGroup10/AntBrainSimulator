package antWorldOps;

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
	private int rows;
	private int cols;
	
	private int rocks;
	private int anthills;
	private int anthillSideLength;
	private int foodBlobSideLength;
	private int cellFoodCount;
	private int antInitialDirection;
	
	private static final int gap = 1; //gap between objects in world
	
	private Cell[][] cells; //indent every other line, starting at cells[1]
	
	public World() {
		
	}
	
	public World(int rows, int cols, char[][] cellChars) {
		this.rows = rows;
		this.cols = cols;
		
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
	
	public void setWorld(int rows, int cols, char[][] cellChars) {
		this.rows = rows;
		this.cols = cols;
		int r = 0;
		int c = 0;
		
		cells = new Cell[rows][cols];
		for(r = 0; r < rows; r++){
			cells[r] = new Cell[rows];
			for(c = 0; c < cols; c++){
				cells[r][c] = new Cell(r, c, cellChars[r][c]);
			}
		}
	}
	
	public void tournamentWorld() {
		this.rows = 140;
		this.cols = 140;
		this.rocks = 13;
		
		createWorld();
	}
	
	public void randomWorld(int rows, int cols, int rocks) {
		this.rows = rows;
		this.cols = cols;
		this.rocks = rocks;
		
		createWorld();
	}
	
	private void createWorld() {
		anthills = 2;
		anthillSideLength = 7;
		foodBlobSideLength = 5;
		cellFoodCount = 5;
		antInitialDirection = 0;
		
		cells = new Cell[rows][cols];
		
		//TODO set centre locations for every anthill, rock and food
		//ensure are required distance apart
		//Think up a good way of doing this randomly so the gap is right too,
		//possible "do-while" with some really clever method used
		
		int[][] anthillLocs = new int[anthills][2];
		int i = 0;
		
		for(i = 0; i < anthillLocs.length; i++){
			//TODO
		}
	}
	
	public Cell[][] getWorld() {
		return cells;
	}
	
	private int distanceBetween(int[] a, int[] b) {
		int d = 0;
		//TODO find min distance between cells
		return d;
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
		neighbours[1] = cells[r + 1][c - k    ]; //south-east
		neighbours[2] = cells[r + 1][c - 1 - k]; //south-west
		neighbours[3] = cells[r    ][c - 1    ]; //west
		neighbours[4] = cells[r - 1][c - 1 - k]; //north-west
		neighbours[5] = cells[r - 1][c - k    ]; //north-east
		
		return neighbours;
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
