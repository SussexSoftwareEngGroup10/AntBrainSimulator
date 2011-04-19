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
public class CopyOfGameDisplay extends PApplet {
	
	private static final long serialVersionUID = 1L;
	private PImage grassTileLarge;
	private PImage grassTileMedium;
	private PImage grassTileSmall;
	private PImage blackAnt;
		
	ZoomPan zoomer; //Class for zooming and panning
	
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
	
	//TODO: add constants that relate to the zoom scale for small medium and large
	private int hexWidth; //Corresponds to C
	private int hexHeight; //Corresponds to D
	private int hexAngleHeight; //Corresponds to A
	private int hexVertHeight; //Corresponds to B

		
	private int numHexCol; //Number of columns (in hexagons) wide
	private int numHexRow; //Number of rows (in hexagons) high
	private String largestDimension;
	private int pixelWidth; //Used to store size of display in pixels
	private int pixelHeight;
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "gUI.GameDisplay" });
	}

	public void setup() {
		//Dimensions of display in pixels - change to modify size
		pixelWidth = 750;
		pixelHeight = 600;
		
		//Number of hexagons in columns and rows - change to modify quantity of hexagons
		numHexCol = 40;
		numHexRow = 30;
		
		hexWidth = 35;
		hexHeight = 40;
		hexAngleHeight = 10;
		hexVertHeight = 20;
		
		if (hexWidth * numHexCol / pixelWidth > (hexAngleHeight + hexVertHeight) * numHexRow / pixelHeight) {
			largestDimension = "width";
		}
		else {
			largestDimension = "height";
		}
		
		size(pixelWidth, pixelHeight);
		
		background(0); //Set background to black
		smooth(); //Turn on anti aliasing
		zoomer = new ZoomPan(this);  // Initialise the zoomer
		zoomer.allowZoomButton(false); 
		
		//TODO: Refactor into get zoom value method - maybe take into account it's logarithmic?
		//ADD PANNING?
		//also need to sort out these values
		if (largestDimension == "width") {
			if (pixelWidth <= 20) {
				zoomer.setZoomScale(0.94);
			}
			else if (pixelWidth <= 40) {
				zoomer.setZoomScale(0.48);
			}
			else if (pixelWidth <= 60) {
				zoomer.setZoomScale(0.32);
			}
			else if (pixelWidth <= 80) {
				zoomer.setZoomScale(0.24);
			}
			else if (pixelWidth <= 100) {
				zoomer.setZoomScale(0.19);
			}
			else if (pixelWidth <= 120) {
				zoomer.setZoomScale(0.16);
			}
			else if (pixelWidth <= 140) {
				zoomer.setZoomScale(0.14);
			}
		}
		else {
			if (pixelHeight <= 20) {
				zoomer.setZoomScale(0.94);
			}
			else if (pixelHeight <= 40) {
				zoomer.setZoomScale(0.48);
			}
			else if (pixelHeight <= 60) {
				zoomer.setZoomScale(0.32);
			}
			else if (pixelHeight <= 80) {
				zoomer.setZoomScale(0.24);
			}
			else if (pixelHeight <= 100) {
				zoomer.setZoomScale(0.19);
			}
			else if (pixelHeight <= 120) {
				zoomer.setZoomScale(0.16);
			}
			else if (pixelHeight <= 140) {
				zoomer.setZoomScale(0.14);
			}
		}
		zoomer.setZoomScale(-0.4 * log((float) (0.005 * numHexRow))); //TODO: Just have if statements to set it to pre defined sizes at pre defined amount of hex
			
		grassTileLarge = loadImage("resources/grass_tile_large.png");
		grassTileMedium = loadImage("resources/grass_tile_medium.png");
		grassTileSmall = loadImage("resources/grass_tile_small.png");
		blackAnt = loadImage("resources/ant.png");
	}
	
	public void draw() {
		//Sets an upper and lower bound on the zoom scale - it would be nicer if this was done more smoothly
		zoomer.transform();
		//TODO - work out how to set limits to the pan offset (will probably need to know size of grid for the right hand side)
		System.out.println(zoomer.getZoomScale());
		System.out.println(zoomer.getPanOffset());
		PImage tile;
		//if (zoomer.getZoomScale() > 15) {
			tile = grassTileLarge;
		//}
		//else if (zoomer.getZoomScale() > 5) {
		//	tile = grassTileMedium;
		//}
		//else {
		//	tile = grassTileSmall;
		//}
		//Draw hexagons
		background(0);
		for (int row = 0; row < numHexRow; row++) {
			for (int col = 0; col < numHexCol; col++) {
			    if (row % 2 != 0) { //On even numbered rows the row needs to be shifted to the right
			    	image(tile, col * hexWidth, row * (hexVertHeight + hexAngleHeight), hexWidth, hexHeight);
			    }
			    else {
			    	image(tile, col * hexWidth + (hexWidth / 2), row  * (hexVertHeight + hexAngleHeight), hexWidth, hexHeight);
			    }
			}
		}
		//Test code
		/*
		imageMode(CENTER);
		createAnt(1, 1, 0);
		for (int row = 1; row <= numHexRow; row++) {
			for (int col = 1; col <= numHexCol; col++) {
				createAnt(row, col, 0);
			}
		}
		*/
	}
	
	//Methods for finding out the dimensions of the hexagons; one uses the height, the other uses the width
	private int calculateHexDimensionsUsingHeight() {
		float third = (float) (1) / (float) (3);
		hexAngleHeight = (int) ((pixelHeight / (float) (numHexRow + third)) * third);
		hexVertHeight = hexAngleHeight * 2;
		hexHeight = hexAngleHeight * 4;
		//Trigonometry to work out the width
		hexWidth = (int) ((hexAngleHeight * tan(radians(60))) * 2);
		
		return hexWidth; //Returned for use in the if statement when this is called
	}
	
	//Inverse of the above method
	private int calculateHexDimensionsUsingWidth() {
		hexWidth = (int) (pixelWidth / (float) (numHexCol + 0.5));
		hexAngleHeight = (int) (hexWidth / 2 * tan(radians(30)));
		hexVertHeight = hexAngleHeight * 2;
		hexHeight = (hexAngleHeight * 4);
		
		return hexHeight;
	}

	//Methods converts grid coords to pixel coords (gives the centre of the hexagon specified)
	private int getRowPixelCoords(int row) {
		//Work out the values, need to do divide by two at the end to give the centre of the hexagon
		return row * (hexHeight - hexAngleHeight) - hexVertHeight / 2;
	}
	
	//Equivalent method for finding the column in pixels
	private int getColPixelCoords(int col, int row) {
		int pixelCol;
		//If it's an even numbered row it needs to be shifted along
		if (row % 2 == 0) {
			pixelCol = ((col * hexWidth) - hexWidth / 2) + (hexWidth / 2);
		}
		else {
			pixelCol = (col * hexWidth) - hexWidth / 2;
		}
		return pixelCol;
	}
	
	//Test method
	public void createAnt(int row, int col, int colour) {
		if (colour == 0) {
			image(blackAnt, getColPixelCoords(col, row), getRowPixelCoords(row), hexWidth, hexWidth);
		}
	}
	
	public void displayNewWorld(World world) {
		//TODO: Implement (draw new world to screen)
	}
}
