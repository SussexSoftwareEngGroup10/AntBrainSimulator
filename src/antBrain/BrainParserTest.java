package antBrain;

import static org.junit.Assert.*;

import org.junit.Test;

import utilities.IOEvent;

public class BrainParserTest {

	
	@Test
	public void testReadBrainFrom() {
		Brain testBrain;
		try {
			testBrain = BrainParser.readBrainFrom("worlds\\example");
			assertTrue(testBrain != null);
			//validity checking in brain
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
		
	}


}
