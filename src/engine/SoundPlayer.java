package engine;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import utilities.IllegalArgumentEvent;
import utilities.Logger;
/**
 * NOT CURRENTLY INTEGRATED INTO THE GAME - WILL CONTINUE TOMORROW ON IT
 * @author Will
 *
 */
public class SoundPlayer implements LineListener {
	private Line dieLine;
	private Clip dieSound;
	private Line finishLine;
	private Clip finishSound;
	private Line foodCollectionLine;
	private Clip foodCollectionSound;
	private Line foodDepositionLine;
	private Clip foodDepositionSound;
	
	private boolean mute = false;

	public SoundPlayer() {
		try {
			Line.Info linfo = new Line.Info(Clip.class);
			AudioInputStream ais;
			
			dieLine = AudioSystem.getLine(linfo);
			dieSound = (Clip) dieLine;
			dieSound.addLineListener(this);
			ais = AudioSystem.getAudioInputStream(new File("resources/sounds/die.wav"));
			dieSound.open(ais);
			
			finishLine = AudioSystem.getLine(linfo);
			finishSound = (Clip) finishLine;
			finishSound.addLineListener(this);
			ais = AudioSystem.getAudioInputStream(new File("resources/sounds/finish.wav"));
			finishSound.open(ais);

			foodCollectionLine = AudioSystem.getLine(linfo);
			foodCollectionSound = (Clip) foodCollectionLine;
			foodCollectionSound.addLineListener(this);
			ais = AudioSystem.getAudioInputStream(new File("resources/sounds/food_collection.wav"));
			foodCollectionSound.open(ais);
			
			foodDepositionLine = AudioSystem.getLine(linfo);
			foodDepositionSound = (Clip) foodDepositionLine;
			foodDepositionSound.addLineListener(this);
			ais = AudioSystem.getAudioInputStream(new File("resources/sounds/food_deposition.wav"));
			foodDepositionSound.open(ais);
			
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playSound(String sound) {
		if (!mute) {
			if (sound.equals("die")) {
				dieSound.start();
			} else if (sound.equals("finish")) {
				finishSound.start();
			} else if (sound.equals("food_collection")) {
				foodCollectionSound.start();
			} else if (sound.equals("food_deposition")) {
				foodDepositionSound.start();
			} else {
				Logger.log(new IllegalArgumentEvent("Non existant sound requested."));
			}
		}
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
	}

	@Override
	public void update(LineEvent le) {
		LineEvent.Type type = le.getType();
	    if (type == LineEvent.Type.STOP) {
	    	dieSound.setFramePosition(0);
	    	finishSound.setFramePosition(0);
	    	foodCollectionSound.setFramePosition(0);
	    	foodDepositionSound.setFramePosition(0);
	    }
	}
}
