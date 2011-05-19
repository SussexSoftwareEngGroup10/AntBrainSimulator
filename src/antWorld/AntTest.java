package antWorld;
import antBrain.Brain;
import antBrain.State;
import antBrain.BrainParser;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.IOEvent;
import utilities.Logger;

public class AntTest {

	public World testWorld;
	public Ant testAnt1;
	public Brain brain;
	//world.getContestWorld(0) MAKES A NICE SHINY WORLD
	
	public AntTest(){
		Logger.setLogLevel(Logger.LogLevel.WARNING_LOGGING);
	}
	/**
	 * ACCEPTANCE TESTS
	 * FUNCTIONALITY
	 */
	
	@Test
	public void testSenseFoodA() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseFoodTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(5, testAnts[0].getDirection());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSenseFoodB() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseFoodTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(0, testAnts[0].getDirection());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSenseAntA(){
		
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseAnt");
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseAntTestBrain"); //the ant will turn left if it senses an enemy ant
			testWorld.setBrain(brain,0);
			testWorld.setBrain(brain,1);
			testAnts[0].step();
			testAnts[0].step();
			if (testAnts[0].getDirection() == 1)
			{
			fail("No enemy ant detected");
			}else{
			assertEquals(5, testAnts[0].getDirection());//pass if turned left
			}
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSenseAntB(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseAntTestBrain"); //the ant will turn left if it senses an enemy ant
			testWorld.setBrain(brain,0); //turns right if no ant present
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(1, testAnts[0].getDirection());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLeftTurn(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/turnLeftOnly");
			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(testAnts[0].getDirection(), 5); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRightTurn(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/turnRightOnly");
			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(1,testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMove(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("move_ahead");
			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(4, testAnts[0].getCell().getRow());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	@Test
	public void testCollectFood(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/testCollectFood"); //load working brain with the ability to pick up food
			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 17; i++){ //after 17 steps, the ant should have JUST picked up the food acording to the brain
				testAnts[0].step();
			}
			assertTrue(testAnts[0].hasFood());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDropFood(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/testDropFood"); //load working brain with the ability to pick up food
			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 17; i++){
				testAnts[0].step();
			}
			if (!testAnts[0].hasFood()){
				fail("The ant did not pick up any food");
			}else{
				testAnts[0].step();
				assertTrue("The ant did not drop the food it had", !testAnts[0].hasFood());
			}
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSetMark(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/markerPlaceAndSense"); 
			//this brain places a marker, moves, and turns around to sense, if detected, the ant turns left
			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 3; i++){
				testAnts[0].step();
			}
			assertEquals(4, testAnts[0].getDirection());
		} catch (IOEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRemoveMark(){
		fail("FINISH ME");
	}
	
	
	
	
}
