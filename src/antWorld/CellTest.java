package antWorld;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.IllegalArgumentEvent;

public class CellTest {


	/**
	 * ACCEPTANCE TESTS
	 * FUNCTIONALITY
	 */
	
	@Test
	public void testStoreRock(){
		Cell testCell;
		try {
			testCell = new Cell(1,1,'#');
			//setting the new cell to rocky and then detecting if it is or not
			assertTrue(testCell.isRocky());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testNotRocky(){
		try{
			Cell testCell = new Cell(1,1,'.');
			//sets the cell to not rocky and tests to see if rocky
			assertFalse(testCell.isRocky());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAnthillType1(){
		try{
			Cell testCell = new Cell(1,1,'+');
			//anthill is set to type 1, detecting if cell registers correctly
			assertEquals(1,testCell.getAnthill());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAnthillType2(){
		try{
			Cell testCell = new Cell(1,1,'-');
			//anthill is set to type 2, detecting if cell registers correctly
			assertEquals(2,testCell.getAnthill());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetMarker(){
		try{
			Cell testCell = new Cell(1,1,'.');
			testCell.setupMarkers(2); //sets two species of markers
			testCell.mark(1, 1); //marks the cell with a species 1 mark
			assertTrue(testCell.getAnyMarker(2)); //this looks for any mark that is not species 2
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveMarker(){
		try{
			Cell testCell = new Cell(1,1,'.');
			testCell.setupMarkers(2);
			testCell.mark(1,1);
			if (testCell.getAnyMarker(2)){
				testCell.unmark(1, 1);
				assertFalse(testCell.getAnyMarker(2));
			} else {
				fail("Marker was not initially set");
			}
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnIndividualNeighbours(){
		try{
			Cell testCell = new Cell(1,1,'.');
			Cell[] neighbors = new Cell[2];
			neighbors[0] = new Cell(0,1,'#');
			neighbors[1] = new Cell(2,1,'+');
			testCell.setNeighbours(neighbors);
			assertEquals("Neighbor 1 invalid", '#', testCell.getNeighbour(0).toChar());
			assertEquals("Neighbor 2 invalid", '+', testCell.getNeighbour(1).toChar());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnNeighboursArray(){
		try{
			Cell testCell = new Cell(1,1,'.');
			Cell[] neighbors = new Cell[2];
			neighbors[0] = new Cell(0,1,'#');
			neighbors[1] = new Cell(2,1,'+');
			testCell.setNeighbours(neighbors);
			assertArrayEquals(neighbors, testCell.getNeighbours());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
}
