package engine;

import static org.junit.Assert.*;
import org.junit.Test;
import antBrain.Brain;
import antBrain.BrainParser;
import antWorld.World;
import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;

public class GameEngineTest {
	public GameStats stats;
	
	public GameEngineTest(){
		try{
			GameEngine testEngine = new GameEngine();
			Brain[] brainArray = new Brain[2];
			brainArray[0] = BrainParser.readBrainFrom("blank");
			brainArray[1] = BrainParser.readBrainFrom("better_example");
			World testWorld = World.getContestWorld(0, null);
			stats = testEngine.simulate(brainArray[0], brainArray[1], testWorld);
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		} catch (ErrorEvent e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetWinner(){ //this also tests the simulate competition test
		assertEquals(1,stats.getWinner());
	}
	
	@Test
	public void testGetRemainingFood(){
		assertEquals(0,stats.getFoodInBlackAnthill()); //there should be no food left in the dummy ants hill
		assertTrue(stats.getFoodInRedAnthill() > 0); //there should be more than 0 food in this ant hill
	}
}
