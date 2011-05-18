package antBrain;

import static org.junit.Assert.*;
import org.junit.Test;

public class BrainParserTest {

	
	@Test
	public void testReadBrainFrom() {
		Brain testBrain = BrainParser.readBrainFrom("worlds\\example");
		assertTrue(testBrain != null);
	}


}
