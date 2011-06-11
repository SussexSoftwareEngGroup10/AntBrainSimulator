package antWorld;
import static org.junit.Assert.*;

import org.junit.Test;

import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
import utilities.Logger;


public class WorldTest {

	
	//world.getContestWorld(0) MAKES A NICE SHINY WORLD
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
		
			assertTrue("world not created/stored",testWorld != null);
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnSize()
	{
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
		
			Cell[][] worldSize = testWorld.getCells();
			assertEquals("retrned wrong size",10, worldSize.length);
			//the example world is size 10, that is what should be returned
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnAntsInvalidWorld(){
		try {
			testWorld = WorldParser.readWorldFromCustom("example");
			Ant[] testAnts = testWorld.getAnts();
			assertEquals("FAILURE: FAILED BECAUSE INVALID WORLD", 32, testAnts.length);
			//Works out length of hexagon with length
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnAntsValidWorld(){
		try {
				testWorld = World.getContestWorld(93745);
				Ant[] testAnts = testWorld.getAnts();
				assertEquals(254, testAnts.length);
				} catch (ErrorEvent e) {
					fail(e.getMessage());
				}
	}
	
	@Test
	public void testReturnFood(){
		try {
			testWorld = WorldParser.readWorldFromCustom("testWorlds/testFood");
			// this world has a total of 50 foods in
			Cell[][] worldCells = testWorld.getCells();
			int foodCount = 0;
			for(int x = 0; x < 9; x++){
				for(int y = 0; y < 9; y++){
					foodCount += worldCells[x][y].foodCount();
				}
			}
			assertEquals(50, foodCount);
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGenerateRandomWorld(){
		try{
			testWorld = World.getContestWorld(38457);
			assertTrue(testWorld.isContest());
		}  catch (ErrorEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testInvalidWorld(){
		try {
			testWorld = World.getRegularWorld(12, 100, 100, 15);
			assertFalse(testWorld.isContest());
		} catch (ErrorEvent e) {
			fail(e.getMessage());
		}
	}
	
	//TODO: MORE TESTS TO WRITE UP
	
	@Test
	public void testTooMuchFood(){
		try{
			testWorld = WorldParser.readWorldFromCustom("testWorlds/tooMuchFood");
			if(testWorld.getCells()[1][1].foodCount() == 10){
				fail("world with 10 food loaded");
			}else{
				assertTrue(true);
			}
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadInvalidWorld(){
		try{
			testWorld = WorldParser.readWorldFromCustom("testWorlds/invalid");
			assertTrue("Invalid world loaded",testWorld == null);
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testLoadNonExistantWorld(){
		try{
			testWorld = WorldParser.readWorldFromCustom("abcdefg");
			assertTrue("Invalid world loaded",testWorld == null);
		}catch (IOEvent e) {
			assertTrue(true);
		} catch (IllegalArgumentEvent e) {
			assertTrue(true);
		}
	}

	
	
}
