package antBrain;

import static org.junit.Assert.*;
import java.util.Random;

import org.junit.Test;

import utilities.IOEvent;
import utilities.IllegalArgumentEvent;


public class BrainTest {

	@Test
	public void testIncrementWins(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			Random rand = new Random();
			int setWins = rand.nextInt(500);
			for(int i = 0; i < setWins; i++){
				testBrain.incrementWins();
			}
			assertEquals(setWins, testBrain.getWins());
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testIncrementLosses(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			Random rand = new Random();
			int setLosses = rand.nextInt(500);
			for(int i = 0; i< setLosses; i++){
				testBrain.incrementLosses();
			}
			assertEquals(setLosses,testBrain.getLosses());
			
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testIncrementDraws(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			Random rand = new Random();
			int setDraws = rand.nextInt(500);
			for(int i = 0; i< setDraws; i++){
				testBrain.incrementDraws();
			}
			assertEquals(setDraws,testBrain.getDraws());
			
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testSetAndGetFitness(){
		try{
			Random rand = new Random();
			Brain testBrain = BrainParser.readBrainFrom("example");
			int nextValue = 0;
			for(int i = 0; i < 100; i++){
				nextValue = rand.nextInt(100);
				testBrain.setFitness(nextValue);
				assertEquals(nextValue,testBrain.getFitness());
			}
		}catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testResetFitness(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			testBrain.setFitness(100);
			if (testBrain.getFitness() == 100){
				testBrain.resetFitnesses();
				assertEquals(0,testBrain.getFitness());
			}else{
				fail("Fitness not set");
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testResetWinsLossesAndDraws(){
		try{
			Brain testBrain = BrainParser.readBrainFrom("example");
			Random rand = new Random();
			int setWins = rand.nextInt(500);
			int setLoss = rand.nextInt(500);
			int setDraws = rand.nextInt(500);
			for(int i = 0; i < setWins; i++){
				testBrain.incrementWins();
			}
			for(int r = 0; r < setLoss; r++){
				testBrain.incrementLosses();
			}
			for(int d = 0; d < setDraws; d++){
				testBrain.incrementDraws();
			}
			if((testBrain.getWins() == setWins) && (testBrain.getLosses() == setLoss) && (testBrain.getDraws() == setDraws)){
				testBrain.resetFitnesses();
				assertEquals(0,testBrain.getWins());
				assertEquals(0,testBrain.getLosses());
				assertEquals(0,testBrain.getDraws());
			}else{
				fail("values not set");
			}
		} catch (IOEvent e) {
			fail(e.getMessage());
		} catch (IllegalArgumentEvent e) {
			fail(e.getMessage());
		}
		
	}
	
}
