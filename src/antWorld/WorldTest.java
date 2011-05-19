package antWorld;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.ErrorEvent;
import utilities.IOEvent;
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
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
		
			assertTrue("ADD MORE TESTS",testWorld != null);
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReturnSize()
	{
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
		
			Cell[][] worldSize = testWorld.getCells();
			assertEquals("ADD MORE TESTS",10, worldSize.length);
			//the example world is size 10, that is what should be returned
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReturnAnts(){
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
			Ant[] testAnts = testWorld.getAnts();
			assertEquals(32, testAnts.length);
			//TODO: Works out length of hexagon with length 5
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReturnFood(){
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
		
		fail("FINISH ME");
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateRandomWorld(){
		try {
			testWorld = World.getContestWorld(38457);
		//TODO:TAKE OUT PRINnt
		System.out.println(testWorld);
		} catch (ErrorEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
