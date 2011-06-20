package gUI;

import java.util.HashMap;
import java.util.Random;
import org.gicentre.utils.move.ZoomPan;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import antWorld.Ant;
import antWorld.Cell;
import antWorld.World;

/**
 * This class is the Processing Sketch which displays the ant world to the
 * screen in the main window.
 * 
 * @author wjs25
 */
public class GameDisplay extends PApplet {
	private static final long serialVersionUID = 1L;
	
	//The current world the display is displaying
	private World world;
	//The cells the current world is made up of
	private Cell[][] gridCells;
	
	//Used to store size of display in pixels
	private static final int PIXEL_WIDTH = 700; 
	private static final int PIXEL_HEIGHT = 700;
	
	/*
	 * Image below shows the variable which denote hexagon dimensions
	 * Imagine image below rotated 90 degrees
	 * 
	 *   		A   B
	 *         |--|----|
	 *   		   _____     _
	 *            /     \    |
	 * 			 /       \   |
	 * 			(         )  | C
	 * 			 \       /   |
	 *			  \_____/    |
	 *						 -
	 *			|---------|
	 *				D
	 *
	 * Some useful rules:
	 * 
	 * B = 2 * A
	 * D = 2 * B
	 * C = D / sqrt(3)
	 * 
	 */
	
	private static final int HEX_WIDTH = 35; //Corresponds to C
	private static final int HEX_HEIGHT = 40; //Corresponds to D
	private static final int HEX_ANGLE_HEIGHT = 10; //Corresponds to A
	private static final int HEX_VERT_HEIGHT = 20; //Corresponds to B
	
	//The total width and height in hexagons
	private int totalHexWidth;
	private int totalHexHeight;
	
	private int numHexCol; //Number of columns (in hexagons) wide
	private int numHexRow; //Number of rows (in hexagons) high
	
	//Enum represents possible image draw scales for use by the variable below 
	//it
	private enum ImageDrawScales { SMALL, MEDIUM, LARGE }
	private ImageDrawScales currentImageScale = ImageDrawScales.MEDIUM;
	
	//Enum represents the two possible directions for use by the variable below 
	//it
	private enum Dimensions { HORIZONTAL, VERTICAL }
	//The largest dimension needs to be recorded because it is important to
	//know what this is when the initial zoom scale is being calculated
	private Dimensions largestDimension;
	
	//Enum represents possible ant directions as the value in radians
	private enum AntDirection {
		EAST(0), 
		SOUTH_EAST(PI / 3), 
		SOUTH_WEST(2 * PI / 3), 
		WEST(PI), 
		NORTH_WEST(4 * PI / 3), 
		NORTH_EAST(5 * PI / 3);

		private final float direction;
		
		AntDirection(float direction) {
			this.direction = direction;
		}
		
		/**
		 * @return Gives the value of the direction specified.
		 */
		public float direction()  {
			return this.direction;
		}
	}
	
	//For each cell a random integer (1 or 0) will dictate which species
	//chemical marker should be drawn last
	private HashMap<Cell, Integer> markerDrawOrders;
	
	//Holds the current state of the display.  The states the display can be in
	//are enumerated in DisplayStates.  The display while draw differently
	//depending on the state it's in
	private DisplayStates currentGameState = DisplayStates.DISPLAYING_GRID;
	
	//Holds whether the chemical markers should be displayed
	private boolean isMarkers = true;
	
	private Random random = new Random(); //Used for generating random nums
	private ZoomPan zoomer; //Class for zooming and panning
	//Used for drawing the background tiles to an off screen buffer, which can
	//then be drawn to the screen much more efficiently than if each tile was
	//drawn separately on each call of draw()
	private PGraphics backgroundBuffer;
	//Variable to hold the font file
	private PFont courierFont;
	
	//The different forms of the same image (e.g. different scales) are held in 
	//arrays; these constants allow for readable indexing into those arrays.
	private static final int SMALL_IMAGE = 0;
	private static final int MEDIUM_IMAGE = 1;
	private static final int LARGE_IMAGE = 2;
	
	private static final int LIGHT_ROCK_TINT = 255;
	private static final int NEUTRAL_ROCK_TINT = 175;
	private static final int DARK_ROCK_TINT = 100;
	
	private static final int ONE_FOOD = 0;
	private static final int TWO_FOOD = 1;
	private static final int THREE_FOOD = 2;
	private static final int FOUR_FOOD = 3;
	private static final int FIVE_FOOD = 4;
	private static final int SIX_FOOD = 5;
	private static final int SEVEN_FOOD = 6;
	private static final int EIGHT_FOOD = 7;
	private static final int NINE_FOOD = 8;
	
	//Variables to hold images
	private PImage grassTile;
	private PImage blackAnthillTile;
	private PImage redAnthillTile;
	private PImage rockTile;
	
	private PImage[] blackAnt;
	private PImage blackAntFood;
	private PImage[] redAnt;
	private PImage redAntFood;
	
	private PImage foodSmall;
	private PImage foodMedium;
	private PImage[] foodLarge;
	
	private PImage blackMarker;
	private PImage redMarker;
	
	/**
	 * Constructor for the game display.  This loads all the resource files (
	 * images and font) into the variables.
	 * 
	 * @param world The world to display.
	 */
	public GameDisplay(World world) {
		this.world = world;
		//Initialise image variables and load image files (files loaded here 
		//rather than dynamically when needed because it would require a large 
		//amount of loading/unloading image files which would slow the game 
		//down when running
		this.grassTile = loadImage("resources/images/tiles/grass_tile.png");
		this.blackAnthillTile 
				= loadImage("resources/images/tiles/black_anthill.png");
		this.redAnthillTile 
				= loadImage("resources/images/tiles/red_anthill.png");
		this.rockTile = loadImage("resources/images/tiles/rock_tile.png");
		
		//Some entities have different images depending on how zoomed in the
		//display is, i.e. ants and food.  In these cases the different scaled
		//images are held in array, accessed via constants
		this.blackAnt = new PImage[3];
		this.blackAnt[SMALL_IMAGE] 
		        = loadImage("resources/images/ants/black_ant_small.png");
		this.blackAnt[LARGE_IMAGE] 
		        = loadImage("resources/images/ants/black_ant_large.png");
		this.blackAntFood = 
				loadImage("resources/images/ants/black_ant_food_large.png");
		
		this.redAnt = new PImage[3];
		this.redAnt[SMALL_IMAGE] 
		        = loadImage("resources/images/ants/red_ant_small.png");
		this.redAnt[LARGE_IMAGE] 
		        = loadImage("resources/images/ants/red_ant_large.png");
		this.redAntFood =
				loadImage("resources/images/ants/red_ant_food_large.png");
		
		this.foodSmall = loadImage("resources/images/food/food_small.png");
		this.foodMedium = loadImage("resources/images/food/food_medium.png");
		this.foodLarge = new PImage[9];
		this.foodLarge[ONE_FOOD] 
		        = loadImage("resources/images/food/food_one_large.png");
		this.foodLarge[TWO_FOOD] 
		        = loadImage("resources/images/food/food_two_large.png");
		this.foodLarge[THREE_FOOD] 
		        = loadImage("resources/images/food/food_three_large.png");
		this.foodLarge[FOUR_FOOD] 
		        = loadImage("resources/images/food/food_four_large.png");
		this.foodLarge[FIVE_FOOD] 
		        = loadImage("resources/images/food/food_five_large.png");
		this.foodLarge[SIX_FOOD] 
		        = loadImage("resources/images/food/food_six_large.png");
		this.foodLarge[SEVEN_FOOD] 
		        = loadImage("resources/images/food/food_seven_large.png");
		this.foodLarge[EIGHT_FOOD] 
		        = loadImage("resources/images/food/food_eight_large.png");
		this.foodLarge[NINE_FOOD] = 
				loadImage("resources/images/food/food_nine_large.png");
		
		this.blackMarker 
				= loadImage("resources/images/markers/chemical_black.png");
		this.redMarker 
				= loadImage("resources/images/markers/chemical_red.png");
		
		//Load font
		this.courierFont = loadFont("resources/fonts/courier_new_font.vlw");
	}
	
	/**
	 * Sets up the drawing area.  This mainly involves setting the correct size,
	 * as well as buffering the background.  Calls to processing methods are
	 * also made e.g. to set the frame rate and enable smoothing.
	 */
	@Override
	public void setup() {
		this.gridCells = this.world.getCells();
		this.markerDrawOrders = new HashMap<Cell, Integer>();
		setMarkerDrawOrders(this.gridCells);
		//Number of hexagons in columns and rows
		this.numHexRow = this.gridCells.length;
		this.numHexCol = this.gridCells[0].length;
		//Calculate the total width and height in pixels the hexagons take up
		this.totalHexWidth = (this.numHexCol * HEX_WIDTH) + HEX_WIDTH / 2;
		this.totalHexHeight = 
				(HEX_ANGLE_HEIGHT + HEX_VERT_HEIGHT) 
				* this.numHexRow + HEX_ANGLE_HEIGHT;
		
		//Work out which dimension take up the most space
		if (this.totalHexWidth > this.totalHexHeight) {
			this.largestDimension = Dimensions.HORIZONTAL;
		} else {
			this.largestDimension = Dimensions.VERTICAL;
		}
		size(PIXEL_WIDTH, PIXEL_HEIGHT);
		
		//Gives the garbage clearer time to free up memory before the 
		//background buffer is recalculated
		this.backgroundBuffer = null; 
		//Of screen buffer to where the background tiles of the ant world a are
		//drawn.  This means that each time the buffer is drawn to the screen,
		//rather than all the tiles which makes it much faster!
		this.backgroundBuffer = createGraphics(this.totalHexWidth,
				this.totalHexHeight, P2D);
		
		smooth(); //Turn on anti aliasing
		frameRate(10); //Turn down the frame rate for less processing power
		//Text variables
		textFont(this.courierFont, 30);
		fill(255); //Fill colour (white) for the text
		this.zoomer = new ZoomPan(this);  // Initialise the zoomer
		this.zoomer.allowZoomButton(false); 
		setInitialPanAndZoom(); //Set the initial zoom and pan
		updateImageScale(); //Update the image scales because zoom scale has
							//been changed
		bufferWorld(); //Buffer the background tiles
	}
	
	/*
	 * Used to randomly decide which marker is drawn first (so if there
	 * is more than one on the tile, it will randomly pick which one
	 * is drawn last and actually displayed)
	 */
	private void setMarkerDrawOrders(Cell[][] cells) {
		for (Cell[] cellCol : cells) {
			for (Cell cell : cellCol) {
				int order = this.random.nextInt(2);
				this.markerDrawOrders.put(cell, order);
			}
		}
	}
	
	/*
	 * Method with hard coded values for the initial zoom and pan
	 * of the zoomer based on number of hexagons.  This is hard coded because
	 * no good function could be found to map world dimensions in hexagons
	 * to an appropriate zoom and pan offset.
	 */
	private void setInitialPanAndZoom() {
		if (this.largestDimension == Dimensions.HORIZONTAL) {
			if (this.numHexCol <= 20) {
				this.zoomer.setZoomScale(0.94);
			} else if (this.numHexCol <= 40) {
				this.zoomer.setZoomScale(0.48);
				this.zoomer.setPanOffset(-160, -160);
			} else if (this.numHexCol <= 60) {
				this.zoomer.setZoomScale(0.32);
				this.zoomer.setPanOffset(-220, -220);
			} else if (this.numHexCol <= 80) {
				this.zoomer.setZoomScale(0.24);
				this.zoomer.setPanOffset(-250, -250);
			} else if (this.numHexCol <= 100) {
				this.zoomer.setZoomScale(0.19);
				this.zoomer.setPanOffset(-270, -270);
			} else if (this.numHexCol <= 120) {
				this.zoomer.setZoomScale(0.16);
				this.zoomer.setPanOffset(-280, -280);
			} else if (this.numHexCol <= 140) {
				this.zoomer.setZoomScale(0.14);
				this.zoomer.setPanOffset(-290, -290);
			}
		} else {
			if (this.numHexRow <= 20) {
				this.zoomer.setZoomScale(0.94);
			} else if (this.numHexRow <= 40) {
				this.zoomer.setZoomScale(0.48);
				this.zoomer.setPanOffset(-160, -160);
			} else if (this.numHexRow <= 60) {
				this.zoomer.setZoomScale(0.32);
				this.zoomer.setPanOffset(-220, -220);
			} else if (this.numHexRow <= 80) {
				this.zoomer.setZoomScale(0.24);
				this.zoomer.setPanOffset(-250, -250);
			} else if (this.numHexRow <= 100) {
				this.zoomer.setZoomScale(0.19);
				this.zoomer.setPanOffset(-270, -270);
			} else if (this.numHexRow <= 120) {
				this.zoomer.setZoomScale(0.16);
				this.zoomer.setPanOffset(-280, -280);
			} else if (this.numHexRow <= 140) {
				this.zoomer.setZoomScale(0.14);
				this.zoomer.setPanOffset(-290, -290);
			}
		}
	}
	
	/**
	 * Allows the current world to be updated.
	 * 
	 * @param world The world to update the display width.
	 */
	protected void updateWorld(World world) {
		this.world = world;
		setup(); //Call setup again to re assign certain variables that depend
				 // on the world
		bufferWorld(); //Buffer the new world
	}
	
	/**
	 * Switch the state of the display.
	 * 
	 * @param gameState The state to switch the display to.
	 */
	protected void switchState(DisplayStates gameState) {
		this.currentGameState = gameState;
	}
	
	/**
	 * Set whether markers are displayed.
	 * 
	 * @param isMarkers True means they are displayed.
	 */
	protected void setMarkers(boolean isMarkers) {
		this.isMarkers = isMarkers;
	}

	/*
	 * Draws the background tiles of the world to an off screen buffer which
	 * is then drawn in the draw method, rather than drawing each tile
	 * separately whenever draw is called.
	 */
	private void bufferWorld() {
		//Draws to a PGraphics object must be surrounded with this...
		this.backgroundBuffer.beginDraw();
		//Set a dark grey background
		this.backgroundBuffer.background(50, 50, 50);
		//Iterate for each hexgon in the grid
		for (int row = 0; row < this.numHexRow; row++) {
			for (int col = 0; col < this.numHexCol; col++) {
			if (this.gridCells[row][col].getAnthill() == 1) {
				//If it is black anthill
				drawImage(this.blackAnthillTile, row, col, 1);
			} else if (this.gridCells[row][col].getAnthill() == 2) { 
					//If the cell is a red ant hill
					drawImage(this.redAnthillTile, row, col, 1);
				} else if (this.gridCells[row][col].isRocky()) {
					//If it's rocky, randomly pick shade of grey
					int shade = this.random.nextInt(2);
					switch (shade) {
						case 0: this.backgroundBuffer.tint(LIGHT_ROCK_TINT);
						break;
						case 1: this.backgroundBuffer.tint(NEUTRAL_ROCK_TINT);
						break;
						case 2: this.backgroundBuffer.tint (DARK_ROCK_TINT);
					}
					drawImage(this.rockTile, row, col, 1);
					this.backgroundBuffer.tint(255); //Restore default no tint
				} else {
					//Otherwise it is a grass tile
					drawImage(this.grassTile, row, col, 1); 
				}
			}
		}
		this.backgroundBuffer.endDraw();
	}
	
	/**
	 * Void which draws all the information to the screen which represents the
	 * current state of the game engine.  This is called as called over and
	 * over again at the speed specified by the frame rate.
	 */
	@Override
	public void draw() {
		//Set the background to dark grey
		background(50, 50, 50);	
		//If the current state is processing, display text explaining this
		if (this.currentGameState == DisplayStates.PROCESSING) {
			text("Processing, please wait...", 30, 340); 
		} else { //Otherwise, the grid should be drawn in it's current state
			this.zoomer.transform(); //Transform the zoom based on user input
			//Get the cells in their current state
			this.gridCells = this.world.getCells(); 
			//Work out which size images to use.
			updateImageScale();
			//Draw the images to the sketch based on the current scale
			if (this.currentImageScale == ImageDrawScales.LARGE) {
				drawImages(LARGE_IMAGE, this.gridCells);
			} else if (this.currentImageScale == ImageDrawScales.MEDIUM) {
				drawImages(MEDIUM_IMAGE, this.gridCells);
			} else {
				drawImages(SMALL_IMAGE, this.gridCells);
			}
		}
	}
	
	/*
	 * This helper method draws all the images to the sketch area.
	 */
	private void drawImages(int imageScale, Cell[][] gridCells) {
		//If the background buffer isn't null, draw it to the screen
		if (this.backgroundBuffer != null) {
			image(this.backgroundBuffer, 0, 0);
		}
		if (this.currentGameState == DisplayStates.RUNNING) {
			//Loop through each hexagon, and draw the markers, then food, then
			//ants in that order
			for (int row = 0; row < this.numHexRow; row++) {
				for (int col = 0; col < this.numHexCol; col++) {
					if (this.isMarkers) {
						drawMarker(row, col, gridCells);
					}
					drawFood(imageScale, row, col);
					drawAnt(imageScale, row, col);

				}
			}
		}
	}
	
	/*
	 * Helper method for drawing a marker on a specific hexagon if this is one.
	 */
	private void drawMarker(int row, int col, Cell[][] gridCells) {
		for (int i = 0; i < 6; i++) { //Check in each type of the 6 markers
			//Get the draw order and draw in that order
			int drawOrder = this.markerDrawOrders.get(gridCells[row][col]);
			if (drawOrder == 0) {
				//Draw a marker if one exists for either of the species
				if (gridCells[row][col].getMarker(0, i)) {
					drawImage(this.blackMarker, row, col, 0);
				}
				if (gridCells[row][col].getMarker(1, i)) {
					drawImage(this.redMarker, row, col, 0);
				}
			} else {
				if (gridCells[row][col].getMarker(1, i)) {
					drawImage(this.redMarker, row, col, 0);
				}
				if (gridCells[row][col].getMarker(0, i)) {
					drawImage(this.blackMarker, row, col, 0);
				}
			}
		}
	}
	
	/*
	 * Helper method for drawing food on a specific hexagon if this is any.
	 */
	private void drawFood(int imageScale, int row, int col) {
		//If scale is small, need to check quantity to show the correct image
		if (imageScale == LARGE_IMAGE) {
			int foodCount = this.gridCells[row][col].foodCount();
			if(foodCount > 9) foodCount = 9;
			switch (foodCount) {
				case 1: drawImage(this.foodLarge[ONE_FOOD], row, col, 0); 
				break;
				case 2: drawImage(this.foodLarge[TWO_FOOD], row, col, 0); 
				break;
				case 3: drawImage(this.foodLarge[THREE_FOOD], row, col, 0); 
				break;
				case 4: drawImage(this.foodLarge[FOUR_FOOD], row, col, 0); 
				break;
				case 5: drawImage(this.foodLarge[FIVE_FOOD], row, col, 0); 
				break;
				case 6: drawImage(this.foodLarge[SIX_FOOD], row, col, 0); 
				break;
				case 7: drawImage(this.foodLarge[SEVEN_FOOD], row, col, 0); 
				break;
				case 8: drawImage(this.foodLarge[EIGHT_FOOD], row, col, 0); 
				break;
				case 9: drawImage(this.foodLarge[NINE_FOOD], row, col, 0); 
				break;
			}
		} else if (imageScale == MEDIUM_IMAGE) { 
			//Otherwise, just check if any is there
			if (this.gridCells[row][col].hasFood()) {
				drawImage(this.foodMedium, row, col, 0); 
			}
		} else {
			if (this.gridCells[row][col].hasFood()) {
				drawImage(this.foodSmall, row, col, 0); 
			}
		}
	}
	
	/*
	 * Helper method for drawing ants on a specific hexagon if this is any.
	 * Also displays the ant carrying food if it is (at the smallest scale).
	 * Furthermore it rotates the ants at the smallest scale.
	 */
	private void drawAnt(int imageScale, int row, int col) {
		Ant currentAnt;
		try { //Try this if the current ant is null e.g. if there is an ant
			  //on the current hexagon
			currentAnt = this.gridCells[row][col].getAnt();
			//On both small and medium scales, display the smallest image
			if (imageScale == LARGE_IMAGE || imageScale == MEDIUM_IMAGE) {	
				//Note: all cases of the if statement work in the same way,
				//just different images are draw.  The reason for the code
				//duplication is because the push and pop matrix calls would
				//not work if they were outside the if statements
				if (currentAnt.getColour() == 0) { //If it's a black ant
					if (currentAnt.hasFood()) {
						//Ant needs to be rotated.  In reality the whole world
						//needs to be translated and rotated, the and drawn,
						//and then the whole world is translated back
						pushMatrix();		
						//Translate the coords system so 0,0 is the centre of 
						//the tile where the ant should be drawn
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								  (getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						//Rotate the coords system so that the and is drawn in 
						//the correct direction relative to the hexagon grid
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						//Draw the image at an offset so that the origin is 
						//back to the top left of the tile.
						image(this.blackAntFood, -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					} else {
						pushMatrix();
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								(getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						image(this.blackAnt[LARGE_IMAGE], -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					}
				} else {
					if (currentAnt.hasFood()) {
						pushMatrix();
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								(getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						image(this.redAntFood, -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					} else {
						pushMatrix();
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								(getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						image(this.redAnt[LARGE_IMAGE], -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					}
				}
			} else {
				//Else just draw the highly zoomed out image
				if (currentAnt.getColour() == 0) { //If it's a black ant
					drawImage(this.blackAnt[SMALL_IMAGE], row, col, 0);
				} else {
					drawImage(this.redAnt[SMALL_IMAGE], row, col, 0);
				}
			}
		} catch (NullPointerException nPE) { /**/ } //Do nothing if no ant
	}
	
	/*
	 * Helper method to get the ant's direction from the numeric representation
	 * used in the game engine
	 */
	private AntDirection getAntDirection(int directionVal) {
		AntDirection direction = AntDirection.EAST;
		switch (directionVal) {
			case 0: direction = AntDirection.EAST; 
			break;
			case 1: direction = AntDirection.SOUTH_EAST;
			break;
			case 2: direction = AntDirection.SOUTH_WEST; 
			break;
			case 3: direction = AntDirection.WEST;
			break;
			case 4: direction = AntDirection.NORTH_WEST; 
			break;
			case 5: direction = AntDirection.NORTH_EAST;
			break;
		}
		return direction;
	}
	
	/*
	 * The helper method which simplifies the actual drawing of the images so
	 * that the width and height do not need to be constantly passed in.
	 */
	private void drawImage(PImage image, int row, int col, int type) {
		//The type parameter decides whether the image should be drawn to the
		//screen (if it's 0), or to the background buffer (if it's 1)
		if (type == 0) {
			 image(image, getColPixelCoords(col, row), getRowPixelCoords(row),
				   HEX_WIDTH, HEX_HEIGHT);
		} else if (type == 1) {
			this.backgroundBuffer.image(image, getColPixelCoords(col, row), 
								   		getRowPixelCoords(row), 
								   		HEX_WIDTH, HEX_HEIGHT);
		}
	}
	
	//Methods converts grid coords in hexagons to pixel coords (gives the 
	//centre of the hexagon specified)
	private int getRowPixelCoords(int row) {
		return row * (HEX_HEIGHT - HEX_ANGLE_HEIGHT);
	}

	//Equivalent method for finding the column in pixels
		private int getColPixelCoords(int col, int row) {
			int pixelCol;
			//If it's an odd numbered row it needs to be shifted along
			if (row % 2 == 0) {
				pixelCol = (col * HEX_WIDTH);// - hexWidth / 2;
			} else {
				pixelCol = ((col * HEX_WIDTH) - HEX_WIDTH / 2) + HEX_WIDTH;
			}
			return pixelCol;
		}

	/*
	 * Updates the scale at which images should be drawn for.  Like the method
	 * which calculates the initial zoom and pan offset, this uses hard coded
	 * values because no effect function could be found to map from the current
	 * zoom scale to the correct image draw scale because the scales will be
	 * different depending on the size of the grid.
	 * 
	 * However it was found that multiplying the zoom scale by the largest
	 * dimension / 2, gave much more uniform results, which meant there didn't
	 * need to be as many cases of the if statement.
	 */
	private void updateImageScale() {
		//Check which dimension is larger and store it in this variable.
		int numHexLargestDimension = this.numHexRow;
		if (this.totalHexWidth > this.totalHexHeight) {
			numHexLargestDimension = this.numHexCol;
		}
		
		if (numHexLargestDimension < 35) {
			if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 16.5) {
				this.currentImageScale = ImageDrawScales.LARGE;
			} else if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 7){
				this.currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				this.currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexLargestDimension < 70) {
			if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 22.2) {
				this.currentImageScale = ImageDrawScales.LARGE;
			} else if (this.zoomer.getZoomScale() 
					* (this.numHexCol / 2) > 14.5){
				this.currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				this.currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexLargestDimension < 105) {
			if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 32.6) {
				this.currentImageScale = ImageDrawScales.LARGE;
			} else if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 21){
				this.currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				this.currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else {
			if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 44.4) {
				this.currentImageScale = ImageDrawScales.LARGE;
			} else if (this.zoomer.getZoomScale() * (this.numHexCol / 2) > 28){
				this.currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				this.currentImageScale = ImageDrawScales.SMALL;
			}
		}
	}
}
