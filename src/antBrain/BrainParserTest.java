package antBrain;

import static org.junit.Assert.*;

import org.junit.Test;

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


}
