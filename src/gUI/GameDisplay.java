package gUI;

import processing.core.*;

import org.gicentre.utils.move.*; 

import antWorld.World;

/* 
 * CURRENTLY AN EXPERIMENTAL CLASS - TESTING DRAWING HEXAGONS CORRECTLY TO THE SCREEN
 * IMAGES ARE ALSO NOT FINAL VERSIONS
 * TODO:
 * NOTES FOR MYSELF (WILL):
 * HOW SHOULD THE GRID COMMUNICATE WITH ENGINE, WILL ENGINE CALL AN UPDATE METHOD IN GUI?
 * HOW TO INTERFACE - FIRST HOW TO SET UP?  HOW TO PARSE IN INITIAL CONFIG DATA??? 
 * SECOND - WHAT METHODS WILL BE NEEDED TO INTERACTIVELY UPDATE??
 * HAVE DIFFERENT IMAGES FOR DIFFERENT SCALES
 * USE THE VALUE RETURN BY THE SCALE OF THE ZOOMER TO SIZE ELEMENTS
 */
public class GameDisplay extends PApplet {
	private static final long serialVersionUID = 1L;
	
	private static final int PIXEL_WIDTH = 700; //Used to store size of display in pixels
	private static final int PIXEL_HEIGHT = 700;
	
	/*
	 * Image below shows the variable which denote hexagon dimensions.
	 * Imagine image below rotated 90�
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
	
	//Enum represents possible image draw scales for use by the variable below it
	private enum ImageDrawScales { SMALL, MEDIUM, LARGE }
	private ImageDrawScales currentImageScale = ImageDrawScales.MEDIUM;
	
	//Enum represents the two possible directions for use by the variable below it
	private enum Dimensions { HORIZONTAL, VERTICAL }
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
		
		public float direction()  {
			return direction;
		}
	}
	private ZoomPan zoomer; //Class for zooming and panning
	
	private int numHexCol; //Number of columns (in hexagons) wide
	private int numHexRow; //Number of rows (in hexagons) high
	
	//The different forms of the same image (e.g. different scales) are held in arrays;
	//these constants allow for readable indexing into those arrays.
	private static final int SMALL_IMAGE = 0;
	private static final int MEDIUM_IMAGE = 1;
	private static final int LARGE_IMAGE = 2;
	
	private static final int LIGHT_ROCK = 0;
	private static final int NEUTRAL_ROCK = 0;
	private static final int DARK_ROCK = 0;
	
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
	private PImage[] grassTile;
	private PImage[] blackAnthillTile;
	private PImage[] redAnthillTile;
	private PImage[][] rockTile;
	
	private PImage[] blackAnt;
	private PImage blackAntFood;
	private PImage[] redAnt;
	private PImage redAntFood;
	
	private PImage foodSmall;
	private PImage foodMedium;
	private PImage[] foodLarge;
	
	private PImage blackMarker;
	private PImage redMarker;

	public void setup() {
		//Number of hexagons in columns and rows - change to modify quantity of hexagons
		numHexCol = 140;
		numHexRow = 140;
		
		if ((numHexCol * HEX_WIDTH) + (HEX_WIDTH / 2)> (numHexRow * HEX_HEIGHT) + HEX_ANGLE_HEIGHT) {
			largestDimension = Dimensions.HORIZONTAL;
		}
		else {
			largestDimension = Dimensions.VERTICAL;
		}
		
		size(PIXEL_WIDTH, PIXEL_HEIGHT);
		
		smooth(); //Turn on anti aliasing
		zoomer = new ZoomPan(this);  // Initialise the zoomer
		zoomer.allowZoomButton(false); 
		setInitialPanAndZoom();
		
		//Initialise image variables and load image files (files loaded here rather than dynamically when needed
		//because it would require a large amount of loading/unloading image files which would slow the game down
		//when running
		grassTile = new PImage[3];
		grassTile[SMALL_IMAGE] = loadImage("resources/images/tiles/grass_tile_small.png");
		grassTile[MEDIUM_IMAGE] = loadImage("resources/images/tiles/grass_tile_medium.png");
		grassTile[LARGE_IMAGE] = loadImage("resources/images/tiles/grass_tile_large.png");
		
		blackAnthillTile = new PImage[3];
		blackAnthillTile[SMALL_IMAGE] = loadImage("resources/images/tiles/black_anthill_small.png");
		blackAnthillTile[MEDIUM_IMAGE] = loadImage("resources/images/tiles/black_anthill_small.png");
		blackAnthillTile[LARGE_IMAGE] = loadImage("resources/images/tiles/black_anthill_small.png");
		
		redAnthillTile = new PImage[3];
		redAnthillTile[SMALL_IMAGE] = loadImage("resources/images/tiles/red_anthill_small.png");
		redAnthillTile[MEDIUM_IMAGE] = loadImage("resources/images/tiles/red_anthill_small.png");
		redAnthillTile[LARGE_IMAGE] = loadImage("resources/images/tiles/red_anthill_small.png");
		
		rockTile = new PImage[3][3];
		rockTile[SMALL_IMAGE][LIGHT_ROCK] = loadImage("resources/images/tiles/rock_light_tile_small.png");
		rockTile[SMALL_IMAGE][NEUTRAL_ROCK] = loadImage("resources/images/tiles/rock_neutral_tile_small.png");
		rockTile[SMALL_IMAGE][DARK_ROCK] = loadImage("resources/images/tiles/rock_dark_tile_small.png");
		rockTile[MEDIUM_IMAGE][LIGHT_ROCK] = loadImage("resources/images/tiles/rock_light_tile_medium.png");
		rockTile[MEDIUM_IMAGE][NEUTRAL_ROCK] = loadImage("resources/images/tiles/rock_neutral_tile_medium.png");
		rockTile[MEDIUM_IMAGE][DARK_ROCK] = loadImage("resources/images/tiles/rock_dark_tile_medium.png");
		rockTile[LARGE_IMAGE][LIGHT_ROCK] = loadImage("resources/images/tiles/rock_light_tile_large.png");
		rockTile[LARGE_IMAGE][NEUTRAL_ROCK] = loadImage("resources/images/tiles/rock_neutral_tile_large.png");
		rockTile[LARGE_IMAGE][DARK_ROCK] = loadImage("resources/images/tiles/rock_dark_tile_large.png");
		
		blackAnt = new PImage[3];
		blackAnt[SMALL_IMAGE] = loadImage("resources/images/ants/black_ant_small.png");
		blackAnt[MEDIUM_IMAGE] = loadImage("resources/images/ants/black_ant_medium.png");
		blackAnt[LARGE_IMAGE] = loadImage("resources/images/ants/black_ant_large.png");
		blackAntFood = loadImage("resources/images/ants/black_ant_food_large.png");
		
		redAnt = new PImage[3];
		redAnt[SMALL_IMAGE] = loadImage("resources/images/ants/red_ant_small.png");
		redAnt[MEDIUM_IMAGE] = loadImage("resources/images/ants/red_ant_medium.png");
		redAnt[LARGE_IMAGE] = loadImage("resources/images/ants/red_ant_large.png");
		redAntFood = loadImage("resources/images/ants/red_ant_food_large.png");
		
		foodSmall = loadImage("resources/images/food/food_small.png");
		foodMedium = loadImage("resources/images/food/food_medium.png");
		foodLarge = new PImage[9];
		foodLarge[ONE_FOOD] = loadImage("resources/images/food/food_one_large.png");
		foodLarge[TWO_FOOD] = loadImage("resources/images/food/food_two_large.png");
		foodLarge[THREE_FOOD] = loadImage("resources/images/food/food_three_large.png");
		foodLarge[FOUR_FOOD] = loadImage("resources/images/food/food_four_large.png");
		foodLarge[FIVE_FOOD] = loadImage("resources/images/food/food_five_large.png");
		foodLarge[SIX_FOOD] = loadImage("resources/images/food/food_six_large.png");
		foodLarge[SEVEN_FOOD] = loadImage("resources/images/food/food_seven_large.png");
		foodLarge[EIGHT_FOOD] = loadImage("resources/images/food/food_eight_large.png");
		foodLarge[NINE_FOOD] = loadImage("resources/images/food/food_nine_large.png");
		
		blackMarker = loadImage("resources/images/markers/chemical_black.png");
		redMarker = loadImage("resources/images/markers/chemical_red.png");
	}
	
	/*
	 * Method with hard coded values for the initial zoom and pan
	 * of the zoomer based on number of hexagons.
	 */
	private void setInitialPanAndZoom() {
		if (largestDimension.equals("width")) {
			if (numHexCol <= 20) {
				zoomer.setZoomScale(0.94);
			}
			else if (numHexCol <= 40) {
				zoomer.setZoomScale(0.48);
				zoomer.setPanOffset(-160, -160);
			}
			else if (numHexCol <= 60) {
				zoomer.setZoomScale(0.32);
				zoomer.setPanOffset(-220, -220);
			}
			else if (numHexCol <= 80) {
				zoomer.setZoomScale(0.24);
				zoomer.setPanOffset(-250, -250);
			}
			else if (numHexCol <= 100) {
				zoomer.setZoomScale(0.19);
				zoomer.setPanOffset(-270, -270);
			}
			else if (numHexCol <= 120) {
				zoomer.setZoomScale(0.16);
				zoomer.setPanOffset(-280, -280);
			}
			else if (numHexCol <= 140) {
				zoomer.setZoomScale(0.14);
				zoomer.setPanOffset(-290, -290);
			}
		}
		else {
			if (numHexRow <= 20) {
				zoomer.setZoomScale(0.94);
			}
			else if (numHexRow <= 40) {
				zoomer.setZoomScale(0.48);
				zoomer.setPanOffset(-160, -160);
			}
			else if (numHexRow <= 60) {
				zoomer.setZoomScale(0.32);
				zoomer.setPanOffset(-220, -220);
			}
			else if (numHexRow <= 80) {
				zoomer.setZoomScale(0.24);
				zoomer.setPanOffset(-250, -250);
			}
			else if (numHexRow <= 100) {
				zoomer.setZoomScale(0.19);
				zoomer.setPanOffset(-270, -270);
			}
			else if (numHexRow <= 120) {
				zoomer.setZoomScale(0.16);
				zoomer.setPanOffset(-280, -280);
			}
			else if (numHexRow <= 140) {
				zoomer.setZoomScale(0.14);
				zoomer.setPanOffset(-290, -290);
			}
		}
	}
	
	public void draw() {
		zoomer.transform();

		//Work out which size images to use.
		updateImageScale();
		PImage tile; //TODO: refactor into helper method
		if (currentImageScale == ImageDrawScales.LARGE) {
			tile = grassTile[LARGE_IMAGE];
		}
		else if (currentImageScale == ImageDrawScales.MEDIUM) {
			tile = grassTile[MEDIUM_IMAGE];
		}
		else {
			tile = grassTile[SMALL_IMAGE];
		}
		
		background(99, 99, 99);
		
		//Draw hexagons
		for (int row = 0; row < numHexRow; row++) {
			for (int col = 0; col < numHexCol; col++) {
			    if (row % 2 == 0) { //On odd numbered rows the row needs to be shifted to the right
			    	image(tile, col * HEX_WIDTH, row * (HEX_VERT_HEIGHT + HEX_ANGLE_HEIGHT), HEX_WIDTH, HEX_HEIGHT);
			    }
			    else {
			    	image(tile, col * HEX_WIDTH + ((HEX_WIDTH / 2) + 0), row  * (HEX_VERT_HEIGHT + HEX_ANGLE_HEIGHT), HEX_WIDTH, HEX_HEIGHT);
			    }
			}
		}

		drawAnt(25, 15, 0);
	}
	
	/*
	 * Updates the scale at which images should be drawn for.
	 */
	private void updateImageScale() {
		if (numHexCol < 35) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 16.5) {
				currentImageScale = ImageDrawScales.LARGE;
			}
			else if (zoomer.getZoomScale() * (numHexCol / 2) > 4.3){
				currentImageScale = ImageDrawScales.MEDIUM;
			}
			else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexCol < 70) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 22.2) {
				currentImageScale = ImageDrawScales.LARGE;
			}
			else if (zoomer.getZoomScale() * (numHexCol / 2) > 7.6){
				currentImageScale = ImageDrawScales.MEDIUM;
			}
			else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else if (numHexCol < 105) {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 32.6) {
				currentImageScale = ImageDrawScales.LARGE;
			}
			else if (zoomer.getZoomScale() * (numHexCol / 2) > 13){
				currentImageScale = ImageDrawScales.MEDIUM;
			}
			else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
		else {
			if (zoomer.getZoomScale() * (numHexCol / 2) > 44.4) {
				currentImageScale = ImageDrawScales.LARGE;
			}
			else if (zoomer.getZoomScale() * (numHexCol / 2) > 22.4){
				currentImageScale = ImageDrawScales.MEDIUM;
			}
			else {
				currentImageScale = ImageDrawScales.SMALL;
			}
		}
	}

	//Methods converts grid coords to pixel coords (gives the centre of the hexagon specified)
	private int getRowPixelCoords(int row) {
		return row * (HEX_HEIGHT - HEX_ANGLE_HEIGHT);
	}

	//Equivalent method for finding the column in pixels
	private int getColPixelCoords(int col, int row) {
		int pixelCol;
		//If it's an odd numbered row it needs to be shifted along
		if (row % 2 == 0) {
			pixelCol = (col * HEX_WIDTH);// - hexWidth / 2;
		}
		else {
			pixelCol = ((col * HEX_WIDTH) - HEX_WIDTH / 2) + HEX_WIDTH;
		}
		return pixelCol;
	}
	
	public void drawAnt(int row, int col, int colour) {
		if (colour == 0) {
			//push and pop matrices so no further draws are affected by transforms below
			pushMatrix();
			//Translate the coords system so 0,0 is the centre of the tile where the ant should be drawn
			translate((getColPixelCoords(col, row) + HEX_WIDTH / 2), (getRowPixelCoords(row) + HEX_VERT_HEIGHT));
			//Rotate the coords system so that the and is drawn in the correct direction relative to the hexagon grid
			rotate(AntDirection.SOUTH_EAST.direction());
			//Draw the image at an offset so that the origin is back to the top left of the tile.
			image(blackAnt[LARGE_IMAGE], -(HEX_WIDTH / 2), -HEX_VERT_HEIGHT, HEX_WIDTH, HEX_HEIGHT);
			popMatrix();
		}
	}
	
	public void displayNewWorld(World world) {
		//TODO: Implement (draw new world to screen)
	}
}
