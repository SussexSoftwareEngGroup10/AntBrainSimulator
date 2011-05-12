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
	private PImage grassTileLarge;
	private PImage grassTileMedium;
	private PImage grassTileSmall;
	private PImage blackAnt;
	
	public enum ImageDrawScales { SMALL, MEDIUM, LARGE }
	private ImageDrawScales currentImageScale = ImageDrawScales.MEDIUM;
	
	public enum AntDirection {
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
	
	private int hexWidth; //Corresponds to C
	private int hexHeight; //Corresponds to D
	private int hexAngleHeight; //Corresponds to A
	private int hexVertHeight; //Corresponds to B

		
	private int numHexCol; //Number of columns (in hexagons) wide
	private int numHexRow; //Number of rows (in hexagons) high
	private String largestDimension;
	private int pixelWidth; //Used to store size of display in pixels
	private int pixelHeight;

	public void setup() {
		//Dimensions of display in pixels - change to modify size
		pixelWidth = 700;
		pixelHeight = 700;
		
		//Number of hexagons in columns and rows - change to modify quantity of hexagons
		numHexCol = 30;
		numHexRow = 30;

		hexWidth = 35;
		hexHeight = 40;
		hexAngleHeight = 10;
		hexVertHeight = 20;
		
		if ((numHexCol * hexWidth) + (hexWidth / 2)> (numHexRow * hexHeight) + hexAngleHeight) {
			largestDimension = "width";
		}
		else {
			largestDimension = "height";
		}
		
		size(pixelWidth, pixelHeight);
		
		smooth(); //Turn on anti aliasing
		zoomer = new ZoomPan(this);  // Initialise the zoomer
		zoomer.allowZoomButton(false); 
		
		setInitialPanAndZoom();
		
		grassTileLarge = loadImage("resources/images/tiles/grass_tile_large.png");
		grassTileMedium = loadImage("resources/images/tiles/grass_tile_medium.png");
		grassTileSmall = loadImage("resources/images/tiles/grass_tile_small.png");
		blackAnt = loadImage("resources/images/ants/black_ant_large.png");
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
		//TODO: this formula doesn't always apply when there are larger numbers of hex
		//TODO: add upper and lower bounds to if statement where camera won't soom
		
		//System.out.println(zoomer.getZoomScale() * (numHexCol / 2));
		//System.out.println(zoomer.getZoomScale());
		//System.out.println(zoomer.getPanOffset());
		
		//Work out which size images to use.
		updateImageScale();
		PImage tile; //TODO: refactor into helper method
		if (currentImageScale == ImageDrawScales.LARGE) {
			tile = grassTileLarge;
		}
		else if (currentImageScale == ImageDrawScales.MEDIUM) {
			tile = grassTileMedium;
		}
		else {
			tile = grassTileSmall;
		}
		
		
		background(255, 204, 0);
		
		//Draw hexagons
		for (int row = 0; row < numHexRow; row++) {
			for (int col = 0; col < numHexCol; col++) {
			    if (row % 2 == 0) { //On odd numbered rows the row needs to be shifted to the right
			    	image(tile, col * hexWidth, row * (hexVertHeight + hexAngleHeight), hexWidth, hexHeight);
			    }
			    else {
			    	image(tile, col * hexWidth + ((hexWidth / 2) + 0), row  * (hexVertHeight + hexAngleHeight), hexWidth, hexHeight);
			    }
			}
		}

		drawAnt(25, 15, 0);
		
		//for (int row = 1; row <= numHexRow; row++) {
		//	for (int col = 1; col <= numHexCol; col++) {
		//		createAnt(row, col, 0);
		//	}
		//}
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
		return row * (hexHeight - hexAngleHeight);
	}

	//Equivalent method for finding the column in pixels
	private int getColPixelCoords(int col, int row) {
		int pixelCol;
		//If it's an odd numbered row it needs to be shifted along
		if (row % 2 == 0) {
			pixelCol = (col * hexWidth);// - hexWidth / 2;
		}
		else {
			pixelCol = ((col * hexWidth) - hexWidth / 2) + hexWidth;
		}
		return pixelCol;
	}
	
	public void drawAnt(int row, int col, int colour) {
		if (colour == 0) {
			//push and pop matrices so no further draws are affected by transforms below
			pushMatrix();
			//Translate the coords system so 0,0 is the centre of the tile where the ant should be drawn
			translate((getColPixelCoords(col, row) + hexWidth / 2), (getRowPixelCoords(row) + hexVertHeight));
			//Rotate the coords system so that the and is drawn in the correct direction relative to the hexagon grid
			rotate(AntDirection.SOUTH_EAST.direction());
			//Draw the image at an offset so that the origin is back to the top left of the tile.
			image(blackAnt, -(hexWidth / 2), -hexVertHeight, hexWidth, hexHeight);
			popMatrix();
		}
	}
	
	public void displayNewWorld(World world) {
		//TODO: Implement (draw new world to screen)
	}
}
