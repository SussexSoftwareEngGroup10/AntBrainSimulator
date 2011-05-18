package antWorld;
import antBrain.Brain;
import antBrain.State;
import antBrain.BrainParser;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.Logger;

public class AntTest {

	public World testWorld;
	public Ant testAnt1;
	public Brain brain;
	//world.getContestWorld(0) MAKES A NICE SHINY WORLD
	
	/**
	 * Acceptance tests
	 */
	
	@Test
	public void testSenseFoodA() {
		Logger.setLogLevel(Logger.LogLevel.WARNING_LOGGING);
		testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/senseFoodTestBrain"); //the ant will turn left if it senses food
		testWorld.setBrain(brain,0);
		testAnts[0].step();
		testAnts[0].step();
		assertEquals(5, testAnts[0].getDirection());
	}
	
	@Test
	public void testSenseFoodB() {
		testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/senseFoodTestBrain"); //the ant will turn left if it senses food
		testWorld.setBrain(brain,0);
		testAnts[0].step();
		testAnts[0].step();
		assertEquals(0, testAnts[0].getDirection());
	}

	@Test
	public void testSenseAntA(){
		
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
	}
	
	@Test
	public void testSenseAntB(){
		testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/senseAntTestBrain"); //the ant will turn left if it senses an enemy ant
		testWorld.setBrain(brain,0); //turns right if no ant present
		testAnts[0].step();
		testAnts[0].step();
		assertEquals(1, testAnts[0].getDirection());
	}
	
	@Test
	public void testLeftTurn(){
		testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/turnLeftOnly");
		testAnts[0].setBrain(brain);
		testAnts[0].step();
		assertEquals(testAnts[0].getDirection(), 5); //testing for 5, since 5 is after one left turn
	}
	
	@Test
	public void testRightTurn(){
		testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/turnRightOnly");
		testAnts[0].setBrain(brain);
		testAnts[0].step();
		assertEquals(1,testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
	}
	
	@Test
	public void testMove(){
		testWorld = WorldParser.readWorldFrom("testWorlds/blank");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("move_ahead");
		testAnts[0].setBrain(brain);
		testAnts[0].step();
		assertEquals(4, testAnts[0].getCell().getRow());
		}
	
	@Test
	public void testCollectFood(){
		testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood");
		Ant[] testAnts = testWorld.getAnts();
		brain = BrainParser.readBrainFrom("testBrains/testCollectFood"); //load working brain with the ability to pick up food
		testWorld.setBrain(brain, 0);
		for (int i = 0; i < 17; i++){ //after 17 steps, the ant should have JUST picked up the food acording to the brain
			testAnts[0].step();
		}
		assertTrue(testAnts[0].hasFood());
	}
	
	@Test
	public void testDropFood(){
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
	}

	@Test
	public void testSetMark(){
		
	}
	
	
	
}
