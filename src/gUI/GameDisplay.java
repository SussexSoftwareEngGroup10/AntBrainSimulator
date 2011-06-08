package gUI;

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
			return direction;
		}
	}
	
	//Holds the current state of the display.  The states the display can be in
	//are enumerated in DisplayStates.  The display while draw differently
	//depending on the state it's in
	private DisplayStates currentGameState = DisplayStates.DISPLAYING_GRID;
	
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
		grassTile = loadImage("resources/images/tiles/grass_tile.png");
		blackAnthillTile = 
				loadImage("resources/images/tiles/black_anthill.png");
		redAnthillTile = loadImage("resources/images/tiles/red_anthill.png");
		rockTile = loadImage("resources/images/tiles/rock_tile.png");
		
		//Some entities have different images depending on how zoomed in the
		//display is, i.e. ants and food.  In these cases the different scaled
		//images are held in array, accessed via constants
		blackAnt = new PImage[3];
		blackAnt[SMALL_IMAGE] = 
				loadImage("resources/images/ants/black_ant_small.png");
		blackAnt[LARGE_IMAGE] = 
				loadImage("resources/images/ants/black_ant_large.png");
		blackAntFood = 
				loadImage("resources/images/ants/black_ant_food_large.png");
		
		redAnt = new PImage[3];
		redAnt[SMALL_IMAGE] = 
				loadImage("resources/images/ants/red_ant_small.png");
		redAnt[LARGE_IMAGE] = 
				loadImage("resources/images/ants/red_ant_large.png");
		redAntFood =
				loadImage("resources/images/ants/red_ant_food_large.png");
		
		foodSmall = loadImage("resources/images/food/food_small.png");
		foodMedium = loadImage("resources/images/food/food_medium.png");
		foodLarge = new PImage[9];
		foodLarge[ONE_FOOD] = 
				loadImage("resources/images/food/food_one_large.png");
		foodLarge[TWO_FOOD] = 
				loadImage("resources/images/food/food_two_large.png");
		foodLarge[THREE_FOOD] = 
				loadImage("resources/images/food/food_three_large.png");
		foodLarge[FOUR_FOOD] = 
				loadImage("resources/images/food/food_four_large.png");
		foodLarge[FIVE_FOOD] = 
				loadImage("resources/images/food/food_five_large.png");
		foodLarge[SIX_FOOD] = 
				loadImage("resources/images/food/food_six_large.png");
		foodLarge[SEVEN_FOOD] = 
				loadImage("resources/images/food/food_seven_large.png");
		foodLarge[EIGHT_FOOD] = 
				loadImage("resources/images/food/food_eight_large.png");
		foodLarge[NINE_FOOD] = 
				loadImage("resources/images/food/food_nine_large.png");
		
		blackMarker = loadImage("resources/images/markers/chemical_black.png");
		redMarker = loadImage("resources/images/markers/chemical_red.png");
		
		//Load font
		courierFont = loadFont("resources/fonts/courier_new_font.vlw");
	}
	
	/**
	 * Sets up the drawing area.  This mainly involves setting the correct size,
	 * as well as buffering the background.  Calls to processing methods are
	 * also made e.g. to set the frame rate and enable smoothing.
	 */
	@Override
	public void setup() {
		gridCells = this.world.getCells();
		//Number of hexagons in columns and rows
		numHexRow = gridCells.length;
		numHexCol = gridCells[0].length;
		//Calculate the total width and height in pixels the hexagons take up
		totalHexWidth = (numHexCol * HEX_WIDTH) + HEX_WIDTH / 2;
		totalHexHeight = 
			(HEX_ANGLE_HEIGHT + HEX_VERT_HEIGHT) * numHexRow + HEX_ANGLE_HEIGHT;
		
		//Work out which dimension take up the most space
		if (totalHexWidth > totalHexHeight) {
			largestDimension = Dimensions.HORIZONTAL;
		} else {
			largestDimension = Dimensions.VERTICAL;
		}
		size(PIXEL_WIDTH, PIXEL_HEIGHT);
		
		//Gives the garbage clearer time to free up memory before the 
		//background buffer is recalculated
		backgroundBuffer = null; 
		//Of screen buffer to where the background tiles of the ant world a are
		//drawn.  This means that each time the buffer is drawn to the screen,
		//rather than all the tiles which makes it much faster!
		backgroundBuffer = createGraphics(totalHexWidth,
				totalHexHeight, P2D);
		
		smooth(); //Turn on anti aliasing
		frameRate(10); //Turn down the frame rate for less processing power
		//Text variables
		textFont(courierFont, 30);
		fill(255); //Fill colour (white) for the text
		zoomer = new ZoomPan(this);  // Initialise the zoomer
		zoomer.allowZoomButton(false); 
		setInitialPanAndZoom(); //Set the initial zoom and pan
		updateImageScale(); //Update the image scales because zoom scale has
							//been changed
		bufferWorld(); //Buffer the background tiles
	}
	
	/*
	 * Method with hard coded values for the initial zoom and pan
	 * of the zoomer based on number of hexagons.  This is hard coded because
	 * no good function could be found to map world dimensions in hexagons
	 * to an appropriate zoom and pan offset.
	 */
	private void setInitialPanAndZoom() {
		if (largestDimension == Dimensions.HORIZONTAL) {
			if (numHexCol <= 20) {
				zoomer.setZoomScale(0.94);
			} else if (numHexCol <= 40) {
				zoomer.setZoomScale(0.48);
				zoomer.setPanOffset(-160, -160);
			} else if (numHexCol <= 60) {
				zoomer.setZoomScale(0.32);
				zoomer.setPanOffset(-220, -220);
			} else if (numHexCol <= 80) {
				zoomer.setZoomScale(0.24);
				zoomer.setPanOffset(-250, -250);
			} else if (numHexCol <= 100) {
				zoomer.setZoomScale(0.19);
				zoomer.setPanOffset(-270, -270);
			} else if (numHexCol <= 120) {
				zoomer.setZoomScale(0.16);
				zoomer.setPanOffset(-280, -280);
			} else if (numHexCol <= 140) {
				zoomer.setZoomScale(0.14);
				zoomer.setPanOffset(-290, -290);
			}
		} else {
			if (numHexRow <= 20) {
				zoomer.setZoomScale(0.94);
			} else if (numHexRow <= 40) {
				zoomer.setZoomScale(0.48);
				zoomer.setPanOffset(-160, -160);
			} else if (numHexRow <= 60) {
				zoomer.setZoomScale(0.32);
				zoomer.setPanOffset(-220, -220);
			} else if (numHexRow <= 80) {
				zoomer.setZoomScale(0.24);
				zoomer.setPanOffset(-250, -250);
			} else if (numHexRow <= 100) {
				zoomer.setZoomScale(0.19);
				zoomer.setPanOffset(-270, -270);
			} else if (numHexRow <= 120) {
				zoomer.setZoomScale(0.16);
				zoomer.setPanOffset(-280, -280);
			} else if (numHexRow <= 140) {
				zoomer.setZoomScale(0.14);
				zoomer.setPanOffset(-290, -290);
			}
		}
	}
	
	/**
	 * Allows the current world to be updated.
	 * 
	 * @param world The world to update the displayw ith.
	 */
	public void updateWorld(World world) {
		this.world = world;
		setup(); //Call setup again to re assign certain varibles that depend
				 // on the world
		bufferWorld(); //Buffer the new world
	}
	
	/**
	 * Switch the state of the display.
	 * 
	 * @param gameState The state to switch the display to.
	 */
	public void switchState(DisplayStates gameState) {
		currentGameState = gameState;
	}

	/*
	 * Draws the background tiles of the world to an off screen buffer which
	 * is then drawn in the draw method, rather than drawing each tile
	 * separately whenever draw is called.
	 */
	private void bufferWorld() {
		//Draws to a PGraphics object must be surrounded with this...
		backgroundBuffer.beginDraw();
		//Set a dark grey background
		backgroundBuffer.background(50, 50, 50);
		//Iterate for each hexgon in the grid
		for (int row = 0; row < numHexRow; row++) {
			for (int col = 0; col < numHexCol; col++) {
				if (gridCells[row][col].getAnthill() == 1) { 
					//If the cell is a red anthill
					drawImage(redAnthillTile, row, col, 1);
				} else if (gridCells[row][col].getAnthill() == 2) {
					//If it is black anthill
					drawImage(blackAnthillTile, row, col, 1);
				} else if (gridCells[row][col].isRocky()) { //If it's rocky
					//Randomly pick shade of grey
					int shade = random.nextInt(2);
					switch (shade) {
						case 0: backgroundBuffer.tint(LIGHT_ROCK_TINT);
						break;
						case 1: backgroundBuffer.tint(NEUTRAL_ROCK_TINT);
						break;
						case 2: backgroundBuffer.tint (DARK_ROCK_TINT);
					}
					drawImage(rockTile, row, col, 1);
					backgroundBuffer.tint(255); //Restore default no tint
				} else {
					//Otherwise it is a grass tile
					drawImage(grassTile, row, col, 1); 
				}
			}
		}
		backgroundBuffer.endDraw();
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
		if (currentGameState == DisplayStates.PROCESSING) {
			text("Processing, please wait...", 30, 340); 
		} else { //Otherwise, the grid should be drawn in it's current state
			zoomer.transform(); //Transform the zoom based on user input
			gridCells = world.getCells(); //Get the cells in their current state
			//Work out which size images to use.
			updateImageScale();
			//Draw the images to the sketch based on the current scale
			if (currentImageScale == ImageDrawScales.LARGE) {
				drawImages(LARGE_IMAGE);
			} else if (currentImageScale == ImageDrawScales.MEDIUM) {
				drawImages(MEDIUM_IMAGE);
			} else {
				drawImages(SMALL_IMAGE);
			}
		}
	}
	
	/*
	 * This helper method draws all the images to the sketch area.
	 */
	private void drawImages(int imageScale) {
		//If the background buffer isn't null, draw it to the screen
		if (backgroundBuffer != null) {
			image(backgroundBuffer, 0, 0);
		}
		if (currentGameState == DisplayStates.RUNNING) {
			//Loop through each hexagon, and draw the markers, then food, then
			//ants in that order
			for (int row = 0; row < numHexRow; row++) {
				for (int col = 0; col < numHexCol; col++) {
					drawMarker(row, col);
					drawFood(imageScale, row, col);
					drawAnt(imageScale, row, col);

				}
			}
		}
	}
	
	/*
	 * Helper method for drawing a marker on a specific hexagon if this is one.
	 */
	private void drawMarker(int row, int col) {
		for (int i = 0; i < 6; i++) { //Check in each type of the 6 markers
			//Draw a marker if one exists for either of the species
			if (gridCells[row][col].getMarker(0, i)) {
				drawImage(blackMarker, row, col, 0);
			}
			if (gridCells[row][col].getMarker(1, i)) {
				drawImage(redMarker, row, col, 0);
			}
		}
	}
	
	/*
	 * Helper method for drawing food on a specific hexagon if this is any.
	 */
	private void drawFood(int imageScale, int row, int col) {
		 //If scale is small, need to check quantity to show the correct image
		if (imageScale == LARGE_IMAGE) {
			int foodCount = gridCells[row][col].foodCount();
			if(foodCount > 9) foodCount = 9;
			switch (foodCount) {
				case 1: drawImage(foodLarge[ONE_FOOD], row, col, 0); 
				break;
				case 2: drawImage(foodLarge[TWO_FOOD], row, col, 0); 
				break;
				case 3: drawImage(foodLarge[THREE_FOOD], row, col, 0); 
				break;
				case 4: drawImage(foodLarge[FOUR_FOOD], row, col, 0); 
				break;
				case 5: drawImage(foodLarge[FIVE_FOOD], row, col, 0); 
				break;
				case 6: drawImage(foodLarge[SIX_FOOD], row, col, 0); 
				break;
				case 7: drawImage(foodLarge[SEVEN_FOOD], row, col, 0); 
				break;
				case 8: drawImage(foodLarge[EIGHT_FOOD], row, col, 0); 
				break;
				case 9: drawImage(foodLarge[NINE_FOOD], row, col, 0); 
				break;
			}
		} else if (imageScale == MEDIUM_IMAGE) { 
			//Otherwise, just check if any is there
			if (gridCells[row][col].hasFood()) {
				drawImage(foodMedium, row, col, 0); 
			}
		} else {
			if (gridCells[row][col].hasFood()) {
				drawImage(foodSmall, row, col, 0); 
			}
		}
	}
	
	/*
	 * Helper method for drawing ants on a specific hexagon if this is any.
	 * Also displays the ant carrying food if it is (at the smallest scale).
	 * Furthermore it rotates the ants at the smallest scale.
	 */
	public void drawAnt(int imageScale, int row, int col) {
		Ant currentAnt;
		try { //Try this if the current ant is null e.g. if there is an ant
			  //on the current hexagon
			currentAnt = gridCells[row][col].getAnt();
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
						image(blackAntFood, -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					} else {
						pushMatrix();
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								(getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						image(blackAnt[LARGE_IMAGE], -(HEX_WIDTH / 2),
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
						image(redAntFood, -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					} else {
						pushMatrix();
						translate((getColPixelCoords(col, row) + HEX_WIDTH / 2),
								(getRowPixelCoords(row) + HEX_VERT_HEIGHT));
						rotate(getAntDirection(
								currentAnt.getDirection()).direction());
						image(redAnt[LARGE_IMAGE], -(HEX_WIDTH / 2),
								-HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
						popMatrix();
					}
				}
			} else {
				//Else just draw the highly zoomed out image
				if (currentAnt.getColour() == 0) { //If it's a black ant
					drawImage(blackAnt[SMALL_IMAGE], row, col, 0);
				} else {
					drawImage(redAnt[SMALL_IMAGE], row, col, 0);
				}
			}
		} catch (NullPointerException nPE) { } //Do nothing if the was no ant
	}
	
	/*
	 * Helper method to get the ant's direction from the numeric representation
	 * used in the game engine
	 */
	private AntDirection getAntDirection(int directionVal) {
		AntDirection direction = AntDirection.EAST; ;
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
			backgroundBuffer.image(image, getColPixelCoords(col, row), 
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
		int numHexLargestDimension = numHexRow;
		if (totalHexWidth > totalHexHeight) {
			numHexLargestDimension = numHexCol;
		}
		
		if (numHexLargestDimension < 35) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 16.5) {
				currentImageScale = ImageDrawScales.LARGE;
			} else if (zoomer.getZoomScale() * (numHexCol / 2) > 7){
				currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexLargestDimension < 70) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 22.2) {
				currentImageScale = ImageDrawScales.LARGE;
			} else if (zoomer.getZoomScale() * (numHexCol / 2) > 14.5){
				currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexLargestDimension < 105) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 32.6) {
				currentImageScale = ImageDrawScales.LARGE;
			} else if (zoomer.getZoomScale() * (numHexCol / 2) > 21){
				currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 44.4) {
				currentImageScale = ImageDrawScales.LARGE;
			} else if (zoomer.getZoomScale() * (numHexCol / 2) > 28){
				currentImageScale = ImageDrawScales.MEDIUM;
			} else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
	}
}
