package gUI;

/**
 * Enumeration of the different states the game display can be in.
 * 
 * DISPLAYING_GRID means that the game is just displaying the background of the
 * world.  E.g. just grass cells, anthills and rocks.
 * 
 * RUNNING means that the game display is displaying the game currently in
 * progress.
 * 
 * PROCESSING means that the display is not displaying the game in progress
 * and instead should display some screen indicating it is running the game.
 * 
 * @author wjs25
 */
public enum DisplayStates { DISPLAYING_GRID, RUNNING, PROCESSING }