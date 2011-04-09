package gUI;

import processing.core.*;
import org.gicentre.utils.move.*; 

/*
 * CURRENTLY AN EXPERIMENTAL CLASS - TESTING DRAWING HEXAGONS CORRECTLY TO THE SCREEN
 * IMAGES ARE ALSO NOT FINAL VERSIONS
 * 
 * NOTES FOR MYSELF (WILL):
 * HOW SHOULD THE GRID COMMUNICATE WITH ENGINE, WILL ENGINE CALL AN UPDATE METHOD IN GUI?
 * SOME SORT OF UNIFORM SCALE FACTOR TO SIZE ALL OBJECTS BY? MAYBE JUST SCALE TO FIT HEXAGON WIDTH.
 * HOW TO INTERFACE - FIRST HOW TO SET UP?  HOW TO PARSE IN INITIAL CONFIG DATA??? 
 * SECOND - WHAT METHODS WILL BE NEEDED TO INTERACTIVELY UPDATE??
 */
@SuppressWarnings("serial")
public class GameDisplay extends PApplet {
	
	private PImage tile; 
	private PImage blackAnt;
		
	ZoomPan zoomer; //Class for zooming and panning
	
	/*
	 * Image below shows the variable which denote hexagon dimensions.
	 * Imagine image below rotated 90º
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
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "gUI.GameDisplay" });
	}

	public void setup() {
		//Dimensions of display in pixels - change to modify size
		int pixelWidth = 750;
		int pixelHeight = 600;
		size(pixelWidth, pixelHeight);
		
		background(0); //Set background to black
		smooth(); //Turn on anti aliasing
		zoomer = new ZoomPan(this);  // Initialise the zoomer
		zoomer.allowZoomButton(false); 
			
		tile = loadImage("resources/GrassTileBorder.png");
		blackAnt = loadImage("resources/Ant.png");
		
		//Number of hexagons in columns and rows - change to modify quantity of hexagons
		numHexCol = 140;
		numHexRow = 140;
		
		//Work out the sizes of the hexagons given size of display and number of hexagons needed
		//Check which is the larger amount between the values above and use that to determine the size of the hexagons
		//This is in accordance with the ratio between the width and height of the hexagons
		float widthProportion = (float) numHexCol / (float) pixelWidth;
		float heightProportion =  (float) numHexRow / (float)pixelHeight;
		//^THIS COULD BE IMPROVED ON - IT ASSUMES THE POINTY PART OF THE HEXAGONS AT THE BOTTOM WILL TAKE UP EXACTLY THE SAME AMOUNT OF SPACE AS THE
		//ROWS OF HEXAGONS THAT JUT OUT ON THE RIGHT HAND SIDE I.E. HEXANGLEHEIGHT != HEXWIDTH / 2.  PROBLEM IS NEED TO KNOW HEX SIZE TO KNOW THESE VALUES.
		if (widthProportion >= heightProportion) { // If columns will be wider
			hexWidth = pixelWidth / (numHexCol + 1);
			//Trigonometry to work out the height of the angled part of the hexagon
			hexAngleHeight = (int) (hexWidth / 2 * tan(radians(30)));
			hexVertHeight = hexAngleHeight * 2;
			hexHeight = (hexAngleHeight * 4);
			}//4
		else { // If rows will be taller
			hexAngleHeight = ((pixelHeight / (numHexRow + 1)) * 1 / 3);
			hexVertHeight = hexAngleHeight * 2;
			hexHeight = hexAngleHeight * 4;
			hexWidth = (int) ((hexAngleHeight * tan(radians(60))) * 2);
		}
		

	}
		
	public void draw() {
		//Sets a lower bound on the zoom scale - it would be nicer if this was done more smoothly
		if (zoomer.getZoomScale() >= 22.7) {
			zoomer.setZoomScale(22.71);
		}
		zoomer.transform();

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
}
