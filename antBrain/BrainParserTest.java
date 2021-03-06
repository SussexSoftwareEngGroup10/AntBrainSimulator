package antBrain;

import static org.junit.Assert.*;
import org.junit.Test;
import utilities.Event;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;

public class BrainParserTest {

	@Test
	public void testReadBrainFrom() {
		Brain testBrain;
		try {
			testBrain = BrainParser.readBrainFrom("example");
			assertTrue(testBrain != null);
			//validity checking in brain
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testReadInvalidBrain() {
		try{
			Brain testBrain = BrainParser.readBrainFrom("testBrains/invalid");
			fail("Invalid Brain accepted");
		}catch (Exception e){
			assertTrue(true);
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testReadNonExistantBrain(){
		try{
			BrainParser.readBrainFrom("abcde");
			fail("Non-existant brain loaded");
		}catch (Event e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testWriteBrain(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			BrainParser.writeBrainTo(testBrain, "testBrains/testWriteBrain");
			Brain testReadNewBrain = BrainParser.readBrainFrom("testBrains/testWriteBrain");
			assertTrue(testReadNewBrain != null);
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
}
