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
	
	/*
	 * ACCEPTANCE TESTS
	 * FUNCTIONALITY
	 */
	
	@Test
	public void testCreation(){
		try {
			this.testWorld = WorldParser.readWorldFrom("example", null);
		
			assertTrue("world not created/stored",this.testWorld != null);
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnSize()
	{
		try {
			this.testWorld = WorldParser.readWorldFrom("example", null);
		
			Cell[][] worldSize = this.testWorld.getCells();
			assertEquals("retrned wrong size",10, worldSize.length);
			//the example world is size 10, that is what should be returned
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnAntsInvalidWorld(){
		try {
			this.testWorld = WorldParser.readWorldFrom("example.world", null);
			Ant[] testAnts = this.testWorld.getAnts();
			assertEquals("FAILURE: FAILED BECAUSE INVALID WORLD", 32, testAnts.length);
			//Works out length of hexagon with length
		} catch (IOEvent e) {
			
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnAntsValidWorld(){
		try {
				this.testWorld = World.getContestWorld(93745, null);
				Ant[] testAnts = this.testWorld.getAnts();
				assertEquals(254, testAnts.length);
				} catch (ErrorEvent e) {
					fail(e.getMessage());
				}
	}
	
	@Test
	public void testReturnFood(){
		try {
			this.testWorld = WorldParser.readWorldFrom("testWorlds/testFood", null);
			// this world has a total of 50 foods in
			Cell[][] worldCells = this.testWorld.getCells();
			int foodCount = 0;
			for(int x = 0; x < 9; x++){
				for(int y = 0; y < 9; y++){
					foodCount += worldCells[x][y].foodCount();
				}
			}
			assertEquals(50, foodCount);
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGenerateRandomWorld(){
		try{
			this.testWorld = World.getContestWorld(38457, null);
			assertTrue(this.testWorld.isContest());
		}  catch (ErrorEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testInvalidWorld(){
		try {
			this.testWorld = World.getRegularWorld(12, 100, 100, 15, null);
			assertFalse(this.testWorld.isContest());
		} catch (ErrorEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTooMuchFood(){
		try{
			this.testWorld = WorldParser.readWorldFrom("testWorlds/tooMuchFood", null);
			if(this.testWorld.getCells()[1][1].foodCount() == 10){
				fail("world with 10 food loaded");
			}else{
				assertTrue(true);
			}
		}catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadInvalidWorld(){
		try{
			this.testWorld = WorldParser.readWorldFrom("testWorlds/invalid", null);
			assertTrue("Invalid world loaded",this.testWorld == null);
		}catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadNonExistantWorld(){
		try{
			this.testWorld = WorldParser.readWorldFrom("abcdefg", null);
			assertTrue("Invalid world loaded",this.testWorld == null);
		}catch (IOEvent e) {
			assertTrue(true);
		}
	}
}
