package antWorld;
import static org.junit.Assert.*;
import org.junit.Test;

public class CellTest {


	/**
	 * ACCEPTANCE TESTS
	 * FUNCTIONALITY
	 */
	
	@Test
	public void testStoreRock(){
		Cell testCell = new Cell(1,1,'#');
		//setting the new cell to rocky and then detecting if it is or not
		assertTrue(testCell.isRocky());
	}
	
	@Test
	public void testNotRocky(){
		Cell testCell = new Cell(1,1,'.');
		//sets the cell to not rocky and tests to see if rocky
		assertFalse(testCell.isRocky());
	}
	
	@Test
	public void testAnthillType1(){
		Cell testCell = new Cell(1,1,'+');
		//anthill is set to type 1, detecting if cell registers correctly
		assertEquals(1,testCell.getAnthill());
	}
	
	@Test
	public void testAnthillType2(){
		Cell testCell = new Cell(1,1,'-');
		//anthill is set to type 2, detecting if cell registers correctly
		assertEquals(2,testCell.getAnthill());
	}
	
	@Test
	public void testSetMarker(){
		Cell testCell = new Cell(1,1,'.');
		testCell.setupMarkers(2); //sets two species of markers
		testCell.mark(1, 1); //marks the cell with a species 1 mark
		assertTrue(testCell.getAnyMarker(2)); //this looks for any mark that is not species 2
	}

	@Test
	public void testRemoveMarker(){
		Cell testCell = new Cell(1,1,'.');
		testCell.setupMarkers(2);
		testCell.mark(1,1);
		if (testCell.getAnyMarker(2)){
			testCell.unmark(1, 1);
			assertFalse(testCell.getAnyMarker(2));
		} else {
			fail("Marker was not initially set");
		}
	}
}
