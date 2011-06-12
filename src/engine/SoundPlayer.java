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

import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;
import utilities.Logger;

/**
 * A class which allows the playing of four different sound effects.
 * 
 * @author wjs25
 */
public class SoundPlayer implements LineListener {
	//Variables to hold the different sound clips, and the lines they use
	private Line dieLine;
	private Clip dieSound;
	private Line finishLine;
	private Clip finishSound;
	private Line foodCollectionLine;
	private Clip foodCollectionSound;
	private Line foodDepositionLine;
	private Clip foodDepositionSound;
	
	//This is set to true when the sound should be muted
	private boolean mute = false;

	/**
	 * Constructor for the sound player.  This loads in all the audio files.
	 */
	public SoundPlayer() {
		try {
			//Get an info object about the line using the clip class
			Line.Info lineInfo = new Line.Info(Clip.class);
			//The input stream of the audio files
			AudioInputStream audioInputStream;
			
			//Tese blocks of code get an audio line to use, create the clip,
			//and open the relevent audio file with it
			dieLine = AudioSystem.getLine(lineInfo);
			dieSound = (Clip) dieLine;
			dieSound.addLineListener(this);
			audioInputStream = AudioSystem.getAudioInputStream(
					new File("resources/sounds/die.wav"));
			dieSound.open(audioInputStream);
			
			finishLine = AudioSystem.getLine(lineInfo);
			finishSound = (Clip) finishLine;
			finishSound.addLineListener(this);
			audioInputStream = AudioSystem.getAudioInputStream(
					new File("resources/sounds/finish.wav"));
			finishSound.open(audioInputStream);

			foodCollectionLine = AudioSystem.getLine(lineInfo);
			foodCollectionSound = (Clip) foodCollectionLine;
			foodCollectionSound.addLineListener(this);
			audioInputStream = AudioSystem.getAudioInputStream(
					new File("resources/sounds/food_collection.wav"));
			foodCollectionSound.open(audioInputStream);
			
			foodDepositionLine = AudioSystem.getLine(lineInfo);
			foodDepositionSound = (Clip) foodDepositionLine;
			foodDepositionSound.addLineListener(this);
			audioInputStream = AudioSystem.getAudioInputStream(
					new File("resources/sounds/food_deposition.wav"));
			foodDepositionSound.open(audioInputStream);
			
		} catch (LineUnavailableException lUE) {
			Logger.log(new ErrorEvent("Requested audio line is unavailable."));
		} catch (UnsupportedAudioFileException e) {
			Logger.log(new ErrorEvent("Requested audio file is unsuppoted."));
		} catch (IOException e) {
			Logger.log(new IOEvent("IO error when loading an audio file"));
		}
	}

	/**
	 * Plays the sound specified.
	 * 
	 * @param sound Supported sounds: "die", "finish", "food_collection",
	 * 				"food deposition".
	 */
	public void playSound(String sound) {
		//Do not play if muted
		if (!mute) {
			if (sound.equals("die")) {
				//Call the start methods of the clips
				dieSound.start();
			} else if (sound.equals("finish")) {
				finishSound.start();
			} else if (sound.equals("food_collection")) {
				foodCollectionSound.start();
			} else if (sound.equals("food_deposition")) {
				foodDepositionSound.start();
			} else {
				Logger.log(new IllegalArgumentEvent(
						"Non existant sound requested."));
			}
		}
	}
	
	/**
	 * Set whether the sounds should be muted.
	 * 
	 * @param mute True if it should be muted.
	 */
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	/**
	 * Check whether the sounds are muted.
	 * 
	 * @return True if it is muted.
	 */
	public boolean isMute() {
		return mute;
	}

	/**
	 * This is called automatically when the state of the clip changes.  In
	 * this case we only check for when a clip stops, so we can reset the
	 * read position for the clip to the beginning of the file.
	 */
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
