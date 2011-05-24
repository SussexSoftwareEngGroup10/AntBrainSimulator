package gUI;

import processing.core.*;

public class MyProcessingSketch extends PApplet {
	PImage greyTile;

  public void setup() {
    size(200,200);
    background(150);
    greyTile = loadImage("resources/images/tiles/rock_tile.png");
  }

  public void draw() {
    image(greyTile, 0, 0);
    tint(100);
    image(greyTile, 100, 100);
    tint(255);
   
  }
  
  public static void main(String args[]) {
	    PApplet.main(new String[] { "--present", "gUI.MyProcessingSketch" });
	  }
}