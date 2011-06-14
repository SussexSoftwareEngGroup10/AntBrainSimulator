package antWorld;
import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import engine.SoundPlayer;

import utilities.ErrorEvent;
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
		} catch (ErrorEvent e) {
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
	
	@Test
	public void testSetGetCoordinates(){
		try{
			Random rand = new Random();
			int x = rand.nextInt(100);
			int y = rand.nextInt(100);
			Cell testCell = new Cell(x,y,'.');
			assertEquals("X-COOrdinate incorrect",x,testCell.getRow());
			assertEquals("Y-COOrdinate incorrect",y,testCell.getCol());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHasAnt(){
		try{
			Cell testCell = new Cell(8,8,'.');
			engine.Random rand = new engine.Random(500);
			Ant testAnt = new Ant(177,rand,0,0,testCell, null);
			testCell.setAnt(testAnt);
			assertTrue(testCell.hasAnt());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetGetAnt(){
		try{
			Cell testCell = new Cell(8,8,'.');
			engine.Random rand = new engine.Random(500);
			Ant testAnt = new Ant(177,rand,0,0,testCell, null);
			testCell.setAnt(testAnt);
			assertEquals(testAnt,testCell.getAnt());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHasFood(){
		try{
			for(int i = 49; i < 58; i++){
				Cell testCell = new Cell(4,4,(char)(i));
				assertTrue(testCell.hasFood());
			}
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetGetFood(){
		try{
			Random rand = new Random();
			char food = (char) (rand.nextInt(8) + 49);
			//random from 8 and +49 because removing 0 from the equation
			Cell testCell = new Cell(4,4,food);
			assertEquals(food - 48,testCell.foodCount());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPickupFood(){
		try{
			Random rand = new Random();
			char food = (char) (rand.nextInt(8) + 49);
			//random from 8 and +49 because removing 0 from the equation
			Cell testCell = new Cell(4,4,food);
			testCell.pickupFood();
			assertEquals(food - 49,testCell.foodCount());
			// - 49 because translating from char and removing one!
			
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDropFood(){
		try{
			Cell testCell = new Cell(4,4,'.');
			for(int droppedFood = 1; droppedFood <= 9; droppedFood++){
				testCell.dropFood(droppedFood);
				assertEquals(droppedFood,testCell.foodCount());
				for(int i = 0; i < droppedFood; i++){
					testCell.pickupFood();
				}
			}
		
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	//TODO: MORE TESTS FROM HERE MORGAN
	
	/**
	 * BOUNDARY AND FAULT TESTS
	 */
	
	@Test
	public void testInitInvalidCharacter(){
		try{
			Cell testCell = new Cell(4,4,'%');
			if(testCell.toChar() == '%'){
				fail("invalid cell set");
			}else{
				assertTrue(true);
			}
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetInvalidChar(){
		try{
			Cell testCell = new Cell(10,10,'.');
			testCell.setCell('%');
			if(testCell.toChar() == '%'){
				fail("invalid cell set");
			}else{
				assertTrue(true);
			}
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testSetSignedPosition(){
		try{
			Cell testCell = new Cell(-10,-10,'.');
			fail("invalid Co-ordinates set");
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetSignedFood(){
		try{
			Cell testCell = new Cell(10,10,'.');
			testCell.dropFood(-2);
			if(testCell.foodCount() == -2){
				fail("signed food set");
			}else{
				assertTrue(true);
			}
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testPickupOverMaxFood(){
		try{
			Cell testCell = new Cell(10,10,'.');
			testCell.dropFood(4);
			for(int i = 0; i < 10; i++){
				testCell.pickupFood();
			}
			if(testCell.foodCount() > 0){
				fail("Food count is below 0");
			}else{
				assertTrue(true);
			}
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
}
