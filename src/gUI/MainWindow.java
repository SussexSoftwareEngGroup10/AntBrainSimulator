package gUI;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import engine.GameEngine;
import engine.GameStats;
import engine.SoundPlayer;
import antBrain.Brain;
import antBrain.BrainParser;
import antWorld.World;
import antWorld.WorldParser;
import utilities.ErrorEvent;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;

/**
 * This class displays the main window GUI.  It displays a window with the main
 * game display as well as buttons along the side which allows the simulation
 * to be set up.  There is also a button to adjust the speed of the game, as
 * well as a button that allows the user to run contests.
 * 
 * @author wjs25
 */
public class MainWindow {
	//Holds the singleton object of this class
	public final static MainWindow INSTANCE = new MainWindow();
	//Holds the dimensions of the main display.  These dimensions allow the
	//program to run in a standard 1024 * 786 XGA display at least.
	private static final int WINDOW_WIDTH = 921;
	private static final int WINDOW_HEIGHT = 738;
	//TODO Known issue, heap space error when more than about 4 or 5 worlds
	//	   are changed.
	//TODO contest mode shows error when you press cancel
	
	//The game engine to use for running the back end code
	private GameEngine gameEngine;
	//The world currently displaying in the display
	private World world;
	//Used to reset the world back to the state before the game is run
	private World clonedWorld;
	
	//The two ant brains to vs each other
	private Brain blackBrain;
	private Brain redBrain;
	
	//The other GUI windows used, and generated from here
	private GameDisplay gameDisplay;
	
	//The control buttons to be display at the bottom of the window
	private JButton startGameBtn;
	private JButton finishButton;
	private JButton contestBtn;
	private JButton uploadRedBtn;
	private JButton uploadBlackBtn;
	private JButton uploadWorldBtn;
	private JButton genWorldBtn;
	private JSlider speedAdjustmentSlider;
	private JButton muteBtn;

	//Sound player used to play all the sounds in the game
	private SoundPlayer soundPlayer = new SoundPlayer();
	//Specifies whether the game was muted before the finish button was pressed
	private boolean isMuteBeforeFinish = false;
	
	//Private, so cannot be externally accessed.
	private MainWindow() {}
	
	/**
	 * Get the singleton object of this class.
	 * 
	 * @return The main window.
	 */
	public static MainWindow getInstance() {
        return INSTANCE;
    }
	
	/**
	 * Main method to run the program - simply calls the constructor
	 * to display the GUI.
	 * 
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		MainWindow.getInstance().init();
	}
	
	/**
	 * This initialises the window and starts the running of the game.
	 */
	public void init() {
		try {
			world = World.getContestWorld(0, soundPlayer);
			gameEngine = new GameEngine();
			drawGUI();
		} catch (ErrorEvent e) {
			GUIErrorMsg.displayErrorMsg("Error in generating a world!");
		}
	}
		
	/*
	 * This method is what adds all the swing components to the main frame and 
	 * adds listeners to the buttons.  It is then drawn to the screen.
	 */
	private void drawGUI() {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Ant Brain Simulator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Set up JPanel to hold the game display area, displayed on the left
		JPanel gridDisplayPanel = new JPanel();
		gridDisplayPanel.setLayout(new FlowLayout());
		
		gameDisplay = new GameDisplay(world);
		gridDisplayPanel.add(gameDisplay);
		gameDisplay.init();
		pane.add(gridDisplayPanel, BorderLayout.WEST);
		
		//Set up JPanel at the bottom to display the control buttons, displayed
		//on the right
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BorderLayout());
		
		//Panel to hold the buttons that set up the game
		JPanel setupPanel = new JPanel();
		setupPanel.setLayout(new GridLayout(6, 1, 5, 5));
		//Empty border adds padding - pushing it halfway down the window
		setupPanel.setBorder(new EmptyBorder(150, 20, 30, 20) );
		
		//Create the buttons and assign listeners
		startGameBtn = new JButton("Start Game");
		startGameBtn.addActionListener(new StartGameListener(this));
		startGameBtn.setEnabled(false);
		contestBtn = new JButton("Contest Mode");
		contestBtn.addActionListener(new ContestListener());
		uploadRedBtn = new JButton("Upload Red Brain");
		uploadRedBtn.addActionListener(new FileBrowseListener());
		uploadBlackBtn = new JButton("Upload Black Brain");
		uploadBlackBtn.addActionListener(new FileBrowseListener());
		uploadWorldBtn = new JButton("Upload World");
		uploadWorldBtn.addActionListener(new FileBrowseListener());
		genWorldBtn = new JButton("Generate World");
		genWorldBtn.addActionListener(new WorldGenListener(this));
		
		setupPanel.add(startGameBtn);
		setupPanel.add(contestBtn);
		setupPanel.add(uploadBlackBtn);
		setupPanel.add(uploadRedBtn);
		setupPanel.add(uploadWorldBtn);
		setupPanel.add(genWorldBtn);
		
		buttonsPanel.add(setupPanel, BorderLayout.NORTH);
		
		//Set up JPanel to hold the speed adjustment sliders and game control
		//buttons
		JPanel gameControlsPanel = new JPanel();
		gameControlsPanel.setLayout(new BorderLayout());
		
		//Set up button to finish the current game
		JPanel controlButtonsPanel = new JPanel();
		controlButtonsPanel.setLayout(new FlowLayout());
		finishButton = new JButton("Finish");
		finishButton.addActionListener(new finishListener());
		finishButton.setEnabled(false);
		controlButtonsPanel.add(finishButton);
		
		//Set up JPanel to display the speed adjustment slider
		JPanel speedAdjustmentPanel = new JPanel();
		speedAdjustmentSlider = new JSlider(1, 1000, 500);
		speedAdjustmentSlider.addChangeListener(
				new SpeedSliderChangeListener());
		//Add a border, and tick marks to slider
		speedAdjustmentSlider.setBorder(
				BorderFactory.createTitledBorder("Speed Adjustment Slider"));
		speedAdjustmentSlider.setMajorTickSpacing(20);
		speedAdjustmentSlider.setEnabled(false);
		speedAdjustmentPanel.add(speedAdjustmentSlider);
		
		//Set a panel to contain the mute button
		JPanel mutePanel = new JPanel();
		mutePanel.setLayout(new FlowLayout());
		mutePanel.setBorder(new EmptyBorder(0, 0, 200, 0) );
		muteBtn = new JButton("Mute");
		muteBtn.addActionListener(new MuteListener());
		mutePanel.add(muteBtn);
		
		gameControlsPanel.add(controlButtonsPanel, BorderLayout.NORTH);
		gameControlsPanel.add(speedAdjustmentPanel, BorderLayout.CENTER);
		gameControlsPanel.add(mutePanel, BorderLayout.SOUTH);
		pane.add(gameControlsPanel, BorderLayout.CENTER);
		
		buttonsPanel.add(gameControlsPanel, BorderLayout.CENTER);
		pane.add(buttonsPanel, BorderLayout.EAST);
		
		//Centre frame on screen and set visible
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/**
	 * To be called when the game is complete.  Modifies the window to initial
	 * configuration as well as providing the user with the option of viewing
	 * statistics.
	 * 
	 * @param winner The winner of the game.
	 */
	public void notifyGameComplete(GameStats gameStats) {
		//Restore whether the game was muted or not
		soundPlayer.setMute(isMuteBeforeFinish);
		//Play the end of game sound
		soundPlayer.playSound("finish");
		
		//re-enable buttons
		startGameBtn.setEnabled(true);
		contestBtn.setEnabled(true);
		uploadBlackBtn.setEnabled(true);
		uploadRedBtn.setEnabled(true);
		uploadWorldBtn.setEnabled(true);
		genWorldBtn.setEnabled(true);
		speedAdjustmentSlider.setEnabled(true);
		muteBtn.setEnabled(true);
		
		//Set speed adjustment slider back to default
		speedAdjustmentSlider.setValue(500);
		
		//Swap back the state of the world before the game was run
		world = clonedWorld;
		gameDisplay.updateWorld(world);
		gameDisplay.switchState(DisplayStates.DISPLAYING_GRID);
		
		//Display dialog box asking if statistics should be displayed
		String[] options = {"Statistics", "OK"}; //Possible options
		String winningColour;
		if (gameStats.getWinner() == 0) {
			winningColour = "Black brain";
		} else {
			winningColour = "Red brain";
		}
		int choice = JOptionPane.showOptionDialog(null, winningColour 
				+ " wins!", "And The Winner Is...", JOptionPane.YES_NO_OPTION, 
				JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
		if (choice == 0) {
			//If the user chooses to view statistics, display the window
			new StatisticsWindow(gameStats);
		}
	}
	
	/**
	 * Sets up and displays a new standard world on the main window.
	 * 
	 * @param rows The height in hexagons.
	 * @param cols The width in hexagons.
	 * @param rocks The number of rocks.
	 * @throws ErrorEvent When the world generation fails.
	 */
	public void setupNewWorldStandardWorld(int rows, int cols, int rocks) 
			throws ErrorEvent {
		world = World.getRegularWorld(0, rows, cols, rocks, soundPlayer);
		gameDisplay.updateWorld(world); //Update game display with the world
	}
	
	/**
	 * Sets up and displays a new contest world on the main window.
	 * 
	 * @throws ErrorEvent When the world generation fails.
	 */
	public void setupNewContestWorld() throws ErrorEvent {
			world = World.getContestWorld(0, soundPlayer);
			gameDisplay.updateWorld(world);
	}
	
	/**
	 * Attached to the buttons which need to bring up a file browser window.
	 * 
	 * @author wjs25
	 */
	public class FileBrowseListener implements ActionListener 
	{
		/**
		 * Displays the file chooser box when the browse button is 
		 * clicked, a tick is display on the button to confirm the file has been
		 * selected.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//Set the initial directory to the current project dir
			JFileChooser fileChooser = new JFileChooser(
					System.getProperty("user.dir"));
			try {
				//Show the dialog
				fileChooser.showOpenDialog(null);
				String path = "";
				//Get the path from the selected file if one was selected
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					path = file.getAbsolutePath();
					JButton clickedBtn = (JButton) e.getSource();
					//Validate the file is of the correct format
					if ((clickedBtn == uploadRedBtn || 
							clickedBtn == uploadBlackBtn) && 
							!path.contains(".brain")) {
							GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .brain file expected.");
					} else if (clickedBtn == uploadWorldBtn &&
							!path.contains(".world")) {
							GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .world file expected.");
					} else {
						//Depending on which button triggered the event, a tick 
						//symbol is displayed on that button, and the 
						//associated file path is updated with the file chosen
						try {
							if (clickedBtn == uploadRedBtn) {
								if (!clickedBtn.getText().contains("✔")) {
									clickedBtn.setText(
												clickedBtn.getText() + " ✔");
								}
								try{
									redBrain = BrainParser.readBrainFrom(path);
								} catch (IllegalArgumentEvent iae) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"Brain syntactically incorrect!");
								}
							} else if (clickedBtn == uploadBlackBtn) {
								if (!clickedBtn.getText().contains("✔")) {
									clickedBtn.setText(
												clickedBtn.getText() + " ✔");
								}
								try{
									blackBrain = 
										BrainParser.readBrainFrom(path);
								} catch (IllegalArgumentEvent iae) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"Brain syntactically incorrect!");
								}
							} else { //Else world button was clicked
								try{
									world = 
										WorldParser.readWorldFromCustom(path);
								} catch (IOEvent iOE) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"World syntactically incorrect!");
								} catch (IllegalArgumentEvent iAE) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"World syntactically incorrect!");
								}
								//Updated the game display with the parsed 
								//world
								gameDisplay.updateWorld(world);
							}
						} catch (IOEvent iOE) { }
					}
				}	
			} catch (SecurityException sE) {
				//If the user does not have permission to access the file
				GUIErrorMsg.displayErrorMsg("Security violation with file!");
			}
			//If all files have been selected, allow game to be played.
			if (!(blackBrain == null) && !(redBrain == null)) {
				startGameBtn.setEnabled(true);
			}
		}
	}
	
	/**
	 * Attached to the button for initiating contest mode.
	 * 
	 * @author wjs25
	 */
	public class ContestListener implements ActionListener {
		/**
		 * Brings up a dialog box to select the amount of contest participants
		 * and then builds the contest window based on the amount of 
		 * participants entered.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			//Display a dialog box where the user inputs the number of players
			String stringNumberOfPlayers = (String) JOptionPane.showInputDialog(
					null, "Select number of players:", "Player Selector", 
					JOptionPane.QUESTION_MESSAGE);
			//Validate it's an int
			int numberOfPlayers;
			if (stringNumberOfPlayers != null) {
				try {
					System.out.println(stringNumberOfPlayers);
					numberOfPlayers = Integer.parseInt(stringNumberOfPlayers);
					if (numberOfPlayers > 1) {
						//Display contest window
						new ContestWindow(numberOfPlayers, gameEngine);
					} else {
						GUIErrorMsg.displayErrorMsg(
								"Can't run a contest with less that two " +
								"brains!");
					}
				} catch (NumberFormatException nFE){
					GUIErrorMsg.displayErrorMsg(
							"Input number of players not an integer!");
				}
			}
		}
	}
	
	/**
	 * Attached to the button for generating a world.
	 * 
	 * @author wjs25
	 */
	public class WorldGenListener implements ActionListener {
		MainWindow mainWindow;
		
		/**
		 * Constructor for the WorldGenListener listener class.
		 * 
		 * @param mainWindow The main window that will create the world
		 * 					 generation window.
		 */
		public WorldGenListener(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		/**
		 * Generates a new random world and displays it.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			new WorldGenerateWindow(mainWindow);
		}
	}
	
	/**
	 * Attached to the start game listener for starting the game.  This is only
	 * possible when two ant brains have been selected.
	 * 
	 * @author wjs25
	 */
	public class StartGameListener implements ActionListener {
		MainWindow mainWindow;
		
		/**
		 * Constructor for the StartGameListener listener class.
		 * 
		 * @param mainWindow The main window that will create the world
		 * 					 generation window.
		 */
		public StartGameListener(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		/**
		 * Starts the game running, disables buttons that should not be used
		 * while the game is in play.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			//First clone the state of the world so it can be restored later
			clonedWorld = (World) world.clone();
			//Set the speed of the simulation to default
			gameEngine.setSpeed(500);
			//Set the mute preference to the state the button is in
			if (muteBtn.getText().equals("Mute")) {
				soundPlayer.setMute(false);
			} else {
				soundPlayer.setMute(true);
			}
			//State a new simulation runner to run the simulation in a new
			//thread
			new SimulationRunner(
					gameEngine, blackBrain, redBrain, world, mainWindow)
					.start();
			gameDisplay.switchState(DisplayStates.RUNNING);
			
			//Enable the finish button and the speed adjustment slider
			finishButton.setEnabled(true);
			speedAdjustmentSlider.setEnabled(true);
			
			//Disable other buttons
			startGameBtn.setEnabled(false);
			contestBtn.setEnabled(false);
			uploadBlackBtn.setEnabled(false);
			uploadRedBtn.setEnabled(false);
			uploadWorldBtn.setEnabled(false);
			genWorldBtn.setEnabled(false);
		}
	}
	
	/**
	 * Attached to the speed adjustment slider to listen for changes of the
	 * slider on it.
	 * 
	 * @author wjs25
	 */
	public class SpeedSliderChangeListener implements ChangeListener {
		
		/**
		 * Sets the speed of the game to the new speed of the slider.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
		    JSlider source = (JSlider)e.getSource();
		    //Subtract from 1000, so that now, the lower the value, the faster
		    gameEngine.setSpeed(
		    		GameEngine.expScale(1001 - (int)source.getValue()));
		    //Mute the sound if it runs faster than 700 after the change
		    if (source.getValue() > 700) {
		    	soundPlayer.setMute(true);
		    	muteBtn.setEnabled(false);
		    } else if (muteBtn.getText().equals("Mute")) {
		    	//If it's slower than 700, unmute if the mute button is toggled
		    	//to not be muted
		    	soundPlayer.setMute(false);
		    	muteBtn.setEnabled(true);
		    } else {
		    	muteBtn.setEnabled(true);
		    }
		}
	}
	
	/**
	 * Attched to the mute button to allow the sounds of the game to be toggled
	 * on and off.
	 * 
	 * @author wjs25
	 */
	public class MuteListener implements ActionListener {

		/**
		 * Depending on what state the button was currently in, flip the state
		 * of the button, and mute or unmute the sound.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			//If it was set to being unmuted, set it to being muted and change
			//the text of the button, otherwise do the opposite
			if (source.getText().equals("Mute")) {
				soundPlayer.setMute(true);
				source.setText("Unmute");
			} else {
				soundPlayer.setMute(false);
				source.setText("Mute");
			}
			
		}
	}
	
	/**
	 * Attached to the finish button to allow the game to be rapidly completed
	 * mid way through.
	 * 
	 * @author wjs25
	 */
	public class finishListener implements ActionListener {
		
		/**
		 * Sets the game to unlimited speed and sets the game  display to the
		 * state where it will display the processing screen.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			//Sets the game to the fastest speed (as fast as the CPU can handle)
			gameEngine.setSpeed(0);
			speedAdjustmentSlider.setEnabled(false);
			muteBtn.setEnabled(false);
			gameDisplay.switchState(DisplayStates.PROCESSING);
			//Store whether the game was muted before the game is skipped to
			//the end. Then mute
			isMuteBeforeFinish = soundPlayer.isMute();
			soundPlayer.setMute(true);
		}
	}
}
