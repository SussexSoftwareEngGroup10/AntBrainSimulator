package antWorld;
import antBrain.Brain;
import antBrain.BrainParser;
import static org.junit.Assert.*;
import org.junit.Test;

import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
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
			testWorld = WorldParser.readWorldFrom("testWorlds\\testSenseFood", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains\\senseFoodTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(5, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSenseFoodB() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseFoodTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(0, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSenseEnemyAntA(){
		//Phil: I reversed the loop that created the Ants for efficiency,
		//so it numbered them backwards
		//I've changed it back though
		//Turns out it was my code:(
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseEnemyAnt", null);
			Ant[] testAnts = testWorld.getAnts();
			try{
			brain = BrainParser.readBrainFrom("testBrains/senseEnemyAntTestBrain"); //the ant will turn left if it senses an enemy ant
			}catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testWorld.setBrain(brain,0);
			testWorld.setBrain(brain,1);
			Ant ant = testAnts[0];
			
			for(int i = 0; i < 2; i++){
				ant.step();
			}
			
			if (testAnts[0].getDirection() == 1)
			{
			fail("No enemy ant detected");
			}else{
			assertEquals(5, testAnts[0].getDirection());//pass if turned left
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSenseEnemyAntB(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			try{
				brain = BrainParser.readBrainFrom("testBrains/senseEnemyAntTestBrain"); //the ant will turn left if it senses an enemy ant
			} catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testWorld.setBrain(brain,0); //turns right if no ant present
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(1, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testSenseFriendlyAntA(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFriendlyAnt", null);
			Ant[] testAnts = testWorld.getAnts();
			try{
			brain = BrainParser.readBrainFrom("testBrains/senseFriendlyAntTestBrain"); //the ant will turn left if it senses an enemy ant
			}catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			Ant ant = testAnts[0];
			ant.setBrain(brain);
			
			for(int i = 0; i < 2; i++){
				ant.step();
			}
			
			if (testAnts[0].getDirection() == 1)
			{
			fail("No Friendly ant detected");
			}else{
			assertEquals(5, testAnts[0].getDirection());//pass if turned left
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSenseFriendlyAntB(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			try{
				brain = BrainParser.readBrainFrom("testBrains/senseFriendlyAntTestBrain"); //the ant will turn left if it senses an enemy ant
			} catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testWorld.setBrain(brain,0); //turns right if no ant present
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(1, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSenseRocksA() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds\\testSenseRock", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains\\senseRockTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(5, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSenseRocksB() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/senseRockTestBrain"); //the ant will turn left if it senses food
			testWorld.setBrain(brain,0);
			testAnts[0].step();
			testAnts[0].step();
			assertEquals(0, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAttackandKill(){
		try{
			testWorld = WorldParser.readWorldFrom("testWorlds/testKill", null);
			
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("move_ahead");
			testWorld.setBrain(brain, 0);
			testWorld.setBrain(brain, 1);
			for(int i = 0; i < 15; i++){
				for(int r = 0; r < testAnts.length; r++){
					testAnts[r].step();
				}
			}
			assertFalse("Ant not dead", testAnts[3].isAlive());
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	

	@Test
	public void testLeftTurn(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/turnLeftOnly");

			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(5, testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRightTurn(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood", null);
		
			Ant[] testAnts = testWorld.getAnts();
			try{
				brain = BrainParser.readBrainFrom("testBrains/turnRightOnly");
			} catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(1,testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMove(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();

			brain = BrainParser.readBrainFrom("move_ahead");

			testAnts[0].setBrain(brain);
			testAnts[0].step();
			assertEquals(4, testAnts[0].getCell().getRow());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCollectFood(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood", null);
		
			Ant[] testAnts = testWorld.getAnts();

			brain = BrainParser.readBrainFrom("testBrains/testCollectFood"); //load working brain with the ability to pick up food

			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 17; i++){ //after 17 steps, the ant should have JUST picked up the food acording to the brain
				testAnts[0].step();
			}
			assertTrue(testAnts[0].hasFood());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDropFood(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/testSenseFood", null);
		
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
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetMark(){
		//Phil: I changed your brain here to make the test pass, I think it's fine now
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("testBrains/markerPlaceAndSense"); 

			//this brain places a marker, moves, and turns around to sense, if detected, the ant turns left
			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 3; i++){
				testAnts[0].step();
			}
			assertEquals(5, testAnts[0].getDirection());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRemoveMark(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
		
			brain = BrainParser.readBrainFrom("testBrains/markerPlaceAndRemove"); 
			//this brain places a marker, moves, and turns around to sense, if detected, the ant turns left
			testWorld.setBrain(brain, 0);
			for (int i = 0; i < 3; i++){
				testAnts[0].step();
			}
			if(testAnts[0].getDirection() == 5){
				for(int i = 0; i < 4; i++){
					testAnts[0].step();
					
				}
				assertEquals("mark not removed",1, testAnts[0].getDirection());
			}else{
				fail("mark not set");
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnColourA(){
		try{
			testWorld = WorldParser.readWorldFrom("testWorlds/twoAnts", null);
			
			Ant[] testAnts = testWorld.getAnts();
			
			assertEquals(0, testAnts[0].getColour());
			
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturnColourB(){
		try{
			
			testWorld = WorldParser.readWorldFrom("testWorlds/twoAnts", null);
			
			Ant[] testAnts = testWorld.getAnts();
			
			assertEquals(1, testAnts[1].getColour());
			
			
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetCell(){
		try{
			testWorld = WorldParser.readWorldFrom("testWorlds/testCell", null);
			
			Ant[] testAnts = testWorld.getAnts();
			assertEquals("wrong cell returned","11", testAnts[0].getCell().getRow() + "" + testAnts[0].getCell().getCol());
			assertEquals("wrong cell returned","88", testAnts[1].getCell().getRow() + "" + testAnts[1].getCell().getCol());

		}catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetCell(){
		try{
			testWorld = WorldParser.readWorldFrom("testWorlds/testCell", null);
			
			Ant[] testAnts = testWorld.getAnts();
			
			Cell testCell = new Cell(5,5,'+');
			testAnts[0].setCell(testCell);
			assertEquals("cell not set/read properly", testCell, testAnts[0].getCell());
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * BOUNDARY TESTS
	 * Design testing
	 */
	
	@Test
	public void testContinualLeftTurning(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			try{
				brain = BrainParser.readBrainFrom("testBrains/turnLeftOnly");
			} catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testAnts[0].setBrain(brain);
			for (int i = 0; i < 10000000; i++){
				testAnts[0].step();
			}  /**
			 * the ant takes 10,000,000 left turn steps, after this many
			 *	the direction should be 4 as 10,000,000 / 6 
			 * (steps divided by possible directions) = remainder of 4
			 * so 10,000,00 left turns should result in a endind directions
			 * of 2
			 *
			 */
			assertEquals(2, testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testContinualRightTurning(){
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
		
			Ant[] testAnts = testWorld.getAnts();
			try{
				brain = BrainParser.readBrainFrom("testBrains/turnRightOnly");
			} catch (IllegalArgumentEvent e) {
				fail(e.getMessage());
			}
			testAnts[0].setBrain(brain);
			for (int i = 0; i < 10000000; i++){
				testAnts[0].step();
			} /**
			 * the ant takes 10,000,000 right turn steps, after this many
			 *	the direction should be 4 as 10,000,000 / 6 
			 * (steps divided by possible directions) = remainder of 4
			 * so 10,000,00 right turns should result in a inevitable direction of 4
			 */
			assertEquals(4, testAnts[0].getDirection()); //testing for 5, since 5 is after one left turn
		} catch (IOEvent e) {
			fail(e.getMessage());
		}
	}
	
	//TODO: MORE TESTS AFTER THIS
	
	@Test
	public void testMoveBeyondEdgeOfMap() {
		try {
			testWorld = WorldParser.readWorldFrom("testWorlds/blank", null);
			
			Ant[] testAnts = testWorld.getAnts();
			brain = BrainParser.readBrainFrom("move_ahead");
			testAnts[0].setBrain(brain);
			for(int i = 0; i < 100; i++){
				testAnts[0].step();
			}		
			if(testAnts[0].getCell().getCol() > 8){
				fail("ant moved beyond playing field");
			}else{
				assertTrue(true);
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testsomething() {
			fail("ADD MORE");
	}
	
}
