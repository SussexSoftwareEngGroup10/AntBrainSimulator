package antWorld;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.Logger;


public class WorldTest {

	public World testWorld;
	
	public WorldTest(){
		Logger.setLogLevel(Logger.LogLevel.WARNING_LOGGING);
	}
	
	/**
	 * ACCEPTANCE TESTS
	 * FUNCTIONALITY
	 */
	
	@Test
	public void testCreation(){
		testWorld = WorldParser.readWorldFrom("example");
		assertTrue("ADD MORE TESTS",testWorld != null);
	}
	
	@Test
	public void testReturnSize()
	{
		testWorld = WorldParser.readWorldFrom("example");
		Cell[][] worldSize = testWorld.getCells();
		assertEquals("ADD MORE TESTS",10, worldSize.length);
		//the example world is size 10, that is what should be returned
	}
	
	@Test
	public void testReturnAnts(){
		testWorld = WorldParser.readWorldFrom("example");
		Ant[] testAnts = testWorld.getAnts();
		assertEquals(32, testAnts.length);
	}
	
	@Test
	public void testReturnFood(){
		testWorld = WorldParser.readWorldFrom("example");
		fail("FINISH ME");
	}
	
}
