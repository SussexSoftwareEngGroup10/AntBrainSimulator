package gUI;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	
	//The game engine to use for running the back end code
	protected GameEngine gameEngine;
	//The world currently displaying in the display
	protected World world;
	//Used to reset the world back to the state before the game is run
	protected World clonedWorld;
	
	//The two ant brains to vs each other
	protected Brain blackBrain;
	protected Brain redBrain;
	
	//The other GUI windows used, and generated from here
	protected GameDisplay gameDisplay;
	
	//The control buttons to be display at the bottom of the window
	protected JButton startGameBtn;
	protected JButton contestBtn;
	protected JButton uploadRedBtn;
	protected JButton uploadBlackBtn;
	protected JButton uploadWorldBtn;
	protected JButton genWorldBtn;
	protected JButton finishBtn;
	protected JSlider speedAdjustmentSlider;
	protected JButton muteBtn;
	protected JButton toggleMarkersBtn;
	protected JLabel roundsLbl;
	protected JLabel blackAnthillFoodLbl;
	protected JLabel redAnthillFoodLbl;

	//Sound player used to play all the sounds in the game
	protected SoundPlayer soundPlayer = new SoundPlayer();
	//Used to fetch stats from the game and update them to this window
	private LiveStatGrabber liveStatGrabber;
	//Specifies whether the game was muted before the finish button was pressed
	protected boolean isMuteBeforeFinish = false;
	
	//Private, so cannot be externally accessed.
	private MainWindow() {/**/}
	
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
			this.world = World.getContestWorld(0, this.soundPlayer);
			this.gameEngine = new GameEngine();
			this.liveStatGrabber = new LiveStatGrabber(this, this.world);
			this.liveStatGrabber.start();
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
		
		this.gameDisplay = new GameDisplay(this.world);
		gridDisplayPanel.add(this.gameDisplay);
		this.gameDisplay.init();
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
		this.startGameBtn = new JButton("Start Game");
		this.startGameBtn.addActionListener(new StartGameListener(this));
		this.startGameBtn.setEnabled(false);
		this.contestBtn = new JButton("Contest Mode");
		this.contestBtn.addActionListener(new ContestListener());
		this.uploadRedBtn = new JButton("Upload Red Brain");
		this.uploadRedBtn.addActionListener(new FileBrowseListener());
		this.uploadBlackBtn = new JButton("Upload Black Brain");
		this.uploadBlackBtn.addActionListener(new FileBrowseListener());
		this.uploadWorldBtn = new JButton("Upload World");
		this.uploadWorldBtn.addActionListener(new FileBrowseListener());
		this.genWorldBtn = new JButton("Generate World");
		this.genWorldBtn.addActionListener(new WorldGenListener(this));
		
		setupPanel.add(this.startGameBtn);
		setupPanel.add(this.contestBtn);
		setupPanel.add(this.uploadBlackBtn);
		setupPanel.add(this.uploadRedBtn);
		setupPanel.add(this.uploadWorldBtn);
		setupPanel.add(this.genWorldBtn);
		
		buttonsPanel.add(setupPanel, BorderLayout.NORTH);
		
		//Panel to hold the game controls and live stats
		JPanel gameControlsAndStatsPanel = new JPanel();
		gameControlsAndStatsPanel.setLayout(new BorderLayout());
		
		//Set up JPanel to hold the speed adjustment sliders and game control
		//buttons
		JPanel gameControlsPanel = new JPanel();
		gameControlsPanel.setLayout(new BorderLayout());
		
		//Set up button to finish the current game
		JPanel controlButtonsPanel = new JPanel();
		controlButtonsPanel.setLayout(new FlowLayout());
		this.finishBtn = new JButton("Finish");
		this.finishBtn.addActionListener(new FinishListener());
		this.finishBtn.setEnabled(false);
		controlButtonsPanel.add(this.finishBtn);
		
		//Set up JPanel to display the speed adjustment slider
		JPanel speedAdjustmentPanel = new JPanel();
		this.speedAdjustmentSlider = new JSlider(1, 1000, 500);
		this.speedAdjustmentSlider.addChangeListener(
				new SpeedSliderChangeListener());
		//Add a border, and tick marks to slider
		this.speedAdjustmentSlider.setBorder(
				BorderFactory.createTitledBorder("Speed Adjustment Slider"));
		this.speedAdjustmentSlider.setMajorTickSpacing(20);
		this.speedAdjustmentSlider.setEnabled(false);
		speedAdjustmentPanel.add(this.speedAdjustmentSlider);
		
		//Panel to hold the mute and toggle markers panel as well as the live
		//game stats
		JPanel muteMarkersAndStatsPanel = new JPanel();
		muteMarkersAndStatsPanel.setLayout(new BorderLayout());
		muteMarkersAndStatsPanel.setBorder(new EmptyBorder(0, 0, 140, 0) );
		
		//Set a panel to contain the mute & toggle markers buttons
		JPanel muteAndHideMarkersPanel = new JPanel();
		muteAndHideMarkersPanel.setLayout(new FlowLayout());
		
		this.muteBtn = new JButton("Unmute");
		this.muteBtn.addActionListener(new MuteListener());
		this.muteBtn.setPreferredSize(new Dimension(90, 26));
		this.muteBtn.setEnabled(true);
		this.toggleMarkersBtn = new JButton("Markers Off");
		this.toggleMarkersBtn.addActionListener(new MarkersListener());
		this.toggleMarkersBtn.setPreferredSize(new Dimension(105, 26));
		this.toggleMarkersBtn.setEnabled(true);
		muteAndHideMarkersPanel.add(this.muteBtn);
		muteAndHideMarkersPanel.add(this.toggleMarkersBtn);
		
		//Panel to display the live stats
		JPanel liveStatsPanel = new JPanel();
		liveStatsPanel.setLayout(new GridLayout(3, 1));
		
		//Label to display the current round
		this.roundsLbl = new JLabel("Round:");
		this.roundsLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
		this.roundsLbl.setEnabled(false);
		liveStatsPanel.add(this.roundsLbl);
		//Label to display food in black ant hill
		this.blackAnthillFoodLbl 
				= new JLabel("Food in black ant hill:");
		this.blackAnthillFoodLbl.setBorder(
				BorderFactory.createEmptyBorder(5, 10, 5, 0));
		this.blackAnthillFoodLbl.setEnabled(false);
		liveStatsPanel.add(this.blackAnthillFoodLbl);
		//Label to display food in red ant hill
		this.redAnthillFoodLbl 
				= new JLabel("Food in red ant hill:");
		this.redAnthillFoodLbl.setBorder(
				BorderFactory.createEmptyBorder(5, 10, 5, 0));
		this.redAnthillFoodLbl.setEnabled(false);
		liveStatsPanel.add(this.redAnthillFoodLbl);
		
		//Add the panels for muting and markers and stats to the outer panel
		muteMarkersAndStatsPanel.add(
				muteAndHideMarkersPanel, BorderLayout.NORTH);
		muteMarkersAndStatsPanel.add(liveStatsPanel, BorderLayout.CENTER);
		
		//Add the three sub panels to the main outer panel
		gameControlsPanel.add(controlButtonsPanel, BorderLayout.NORTH);
		gameControlsPanel.add(speedAdjustmentPanel, BorderLayout.CENTER);
		gameControlsPanel.add(muteMarkersAndStatsPanel, BorderLayout.SOUTH);
		pane.add(gameControlsPanel, BorderLayout.CENTER);
		
		buttonsPanel.add(gameControlsPanel, BorderLayout.CENTER);
		pane.add(buttonsPanel, BorderLayout.EAST);
		
		//Centre frame on screen and set visible
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/**
	 * Sets up and displays a new standard world in the main window.
	 * 
	 * @param rows The height in hexagons.
	 * @param cols The width in hexagons.
	 * @param rocks The number of rocks.
	 * @throws ErrorEvent When the world generation fails.
	 */
	protected void setupNewWorldStandardWorld(int rows, int cols, int rocks) 
			throws ErrorEvent {
		this.world 
				= World.getRegularWorld(0, rows, cols, rocks, this.soundPlayer);
		//Update game display with the world
		this.gameDisplay.updateWorld(this.world);
		this.liveStatGrabber.updateWorld(this.world);
	}

	/**
	 * Sets up and displays a new contest world in the main window.
	 * 
	 * @throws ErrorEvent When the world generation fails.
	 */
	protected void setupNewContestWorld() throws ErrorEvent {
		this.world = World.getContestWorld(0, this.soundPlayer);
		this.gameDisplay.updateWorld(this.world);
		this.liveStatGrabber.updateWorld(this.world);
	}

	/**
	 * To be called when the game is complete.  Modifies the window to initial
	 * configuration as well as providing the user with the option of viewing
	 * statistics.
	 * 
	 * @param gameStats The stats of the game.
	 */
	@SuppressWarnings("unused")
	protected void notifyGameComplete(GameStats gameStats) {
		//Restore whether the game was muted or not
		this.soundPlayer.setMute(this.isMuteBeforeFinish);
		//Play the end of game sound
		this.soundPlayer.playSound("finish");
		
		//Re-enable buttons
		this.startGameBtn.setEnabled(true);
		this.contestBtn.setEnabled(true);
		this.uploadBlackBtn.setEnabled(true);
		this.uploadRedBtn.setEnabled(true);
		this.uploadWorldBtn.setEnabled(true);
		this.genWorldBtn.setEnabled(true);
		
		//And disable others
		this.finishBtn.setEnabled(false);
		this.speedAdjustmentSlider.setEnabled(false);
		this.roundsLbl.setEnabled(false);
		this.blackAnthillFoodLbl.setEnabled(false);
		this.redAnthillFoodLbl.setEnabled(false);
		
		
		//Set speed adjustment slider back to default
		this.speedAdjustmentSlider.setValue(500);
		
		//Swap back the state of the world before the game was run
		this.world = this.clonedWorld;
		this.gameDisplay.updateWorld(this.world);
		this.liveStatGrabber.updateWorld(this.world);
		this.gameDisplay.switchState(DisplayStates.DISPLAYING_GRID);
		
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
	 * Updates the main window with current stats from the game.
	 * 
	 * @param round The current round.
	 * @param blackAnthillFood Amount of food in the black ant hill.
	 * @param redAnthillFood Amount of food in the red ant hill.
	 */
	protected void updateLiveStats(
			int round, int blackAnthillFood, int redAnthillFood) {
		//Update labal's text
		this.roundsLbl.setText("Round: " + round);
		this.blackAnthillFoodLbl.setText(
				"Food in black ant hill: " + blackAnthillFood);
		this.redAnthillFoodLbl.setText(
				"Food in red ant hill: " + redAnthillFood);
	}
	
	/*
	 * Attached to the buttons which need to bring up a file browser window.
	 */
	private class FileBrowseListener implements ActionListener 
	{
		public FileBrowseListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Displays the file chooser box when the browse button is 
		 * clicked, a tick is display on the button to confirm the file has 
		 * been selected.
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
					if ((clickedBtn == MainWindow.this.uploadRedBtn || 
							clickedBtn == MainWindow.this.uploadBlackBtn) && 
							!path.contains(".ant")) {
							GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .ant file expected.");
					} else if (clickedBtn == MainWindow.this.uploadWorldBtn &&
							!path.contains(".world")) {
							GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .ant file expected.");
					} else {
						//Depending on which button triggered the event, a tick 
						//symbol is displayed on that button, and the 
						//associated file path is updated with the file chosen
						try {
							if (clickedBtn == MainWindow.this.uploadRedBtn) {
								if (!clickedBtn.getText().contains("✔")) {
									clickedBtn.setText(
												clickedBtn.getText() + " ✔");
								}
								try{
									MainWindow.this.redBrain 
											= BrainParser.readBrainFrom(path);
								} catch (IllegalArgumentEvent iae) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"Brain syntactically incorrect!");
								}
							} else if (clickedBtn 
									== MainWindow.this.uploadBlackBtn) {
								if (!clickedBtn.getText().contains("✔")) {
									clickedBtn.setText(
												clickedBtn.getText() + " ✔");
								}
								try{
									MainWindow.this.blackBrain = 
										BrainParser.readBrainFrom(path);
								} catch (IllegalArgumentEvent iae) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"Brain syntactically incorrect!");
								}
							} else { //Else world button was clicked
								try{
									MainWindow.this.world 
											= WorldParser.readWorldFrom(
											path, MainWindow.this.soundPlayer);
								} catch (IOEvent iOE) {
									GUIErrorMsg.displayErrorMsg(
											"Unable to parse file. " +
											"World syntactically incorrect!");
								}
								//Updated the game display with the parsed 
								//world
								MainWindow.this.gameDisplay.updateWorld(
										MainWindow.this.world);
							}
						} catch (IOEvent iOE) { /**/ }
					}
				}
			} catch (SecurityException sE) {
				//If the user does not have permission to access the file
				GUIErrorMsg.displayErrorMsg("Security violation with file!");
			}
			//If all files have been selected, allow game to be played.
			if (!(MainWindow.this.blackBrain == null) 
					&& !(MainWindow.this.redBrain == null)) {
				MainWindow.this.startGameBtn.setEnabled(true);
			}
		}
	}
	
	/*
	 * Attached to the button for initiating contest mode.
	 */
	private class ContestListener implements ActionListener {
		public ContestListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Brings up a dialog box to select the amount of contest participants
		 * and then builds the contest window based on the amount of 
		 * participants entered.
		 * 
		 * @param e The triggering event.
		 */
		@SuppressWarnings("unused")
		@Override
		public void actionPerformed(ActionEvent e) {
			//Display a dialog box where the user inputs the number of players
			String stringNumberOfPlayers = JOptionPane.showInputDialog(
					null, "Select number of players:", "Player Selector", 
					JOptionPane.QUESTION_MESSAGE);
			//Validate it's an int
			int numberOfPlayers;
			if (stringNumberOfPlayers != null) {
				try {
					numberOfPlayers = Integer.parseInt(stringNumberOfPlayers);
					if (numberOfPlayers < 2) {
						GUIErrorMsg.displayErrorMsg(
								"Can't run a contest with less that two " +
								"brains!");
					} else if (numberOfPlayers > 100) {
						GUIErrorMsg.displayErrorMsg(
								"Upper limit of 100 players for contests!");
					} else {
						//Display contest window
						new ContestWindow(
								numberOfPlayers, MainWindow.this.gameEngine);
					}
				} catch (NumberFormatException nFE){
					GUIErrorMsg.displayErrorMsg(
							"Input number of players not an integer!");
				}
			}
		}
	}
	
	/*
	 * Attached to the button for generating a world.
	 */
	private class WorldGenListener implements ActionListener {
		private MainWindow mainWindow;
		
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
		@SuppressWarnings("unused")
		@Override
		public void actionPerformed(ActionEvent e) {
			new WorldGenerateWindow(this.mainWindow);
		}
	}
	
	/*
	 * Attached to the start game listener for starting the game.  This is only
	 * possible when two ant brains have been selected.
	 */
	private class StartGameListener implements ActionListener {
		private MainWindow mainWindow;
		
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
		public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
			//First clone the state of the world so it can be restored later
			MainWindow.this.clonedWorld = (World) MainWindow.this.world.clone();
			//Set the speed of the simulation to default
			MainWindow.this.gameEngine.setSleepDur(500);
			//Set the mute preference to the state the button is in
			if (MainWindow.this.muteBtn.getText().equals("Mute")) {
				MainWindow.this.soundPlayer.setMute(false);
			} else {
				MainWindow.this.soundPlayer.setMute(true);
			}
			//State a new simulation runner to run the simulation in a new
			//thread
			new SimulationRunner(MainWindow.this.gameEngine, 
								 MainWindow.this.blackBrain, 
								 MainWindow.this.redBrain, 
								 MainWindow.this.world, this.mainWindow)
								 .start();
			MainWindow.this.gameDisplay.switchState(DisplayStates.RUNNING);
			
			//Enable the finish button and the speed adjustment slider
			MainWindow.this.finishBtn.setEnabled(true);
			MainWindow.this.speedAdjustmentSlider.setEnabled(true);
			MainWindow.this.roundsLbl.setEnabled(true);
			MainWindow.this.blackAnthillFoodLbl.setEnabled(true);
			MainWindow.this.redAnthillFoodLbl.setEnabled(true);
			
			//Disable other buttons
			MainWindow.this.startGameBtn.setEnabled(false);
			MainWindow.this.contestBtn.setEnabled(false);
			MainWindow.this.uploadBlackBtn.setEnabled(false);
			MainWindow.this.uploadRedBtn.setEnabled(false);
			MainWindow.this.uploadWorldBtn.setEnabled(false);
			MainWindow.this.genWorldBtn.setEnabled(false);
		}
	}
	
	/*
	 * Attached to the speed adjustment slider to listen for changes of the
	 * slider on it.
	 */
	private class SpeedSliderChangeListener implements ChangeListener {
		
		public SpeedSliderChangeListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Sets the speed of the game to the new speed of the slider.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void stateChanged(ChangeEvent e) {
		    JSlider source = (JSlider)e.getSource();
		    //Subtract from 1000, so that now, the lower the value, the faster
		    MainWindow.this.gameEngine.setSleepDur(
		    		GameEngine.expScale(1001 - source.getValue()));
		    //Mute the sound if it runs faster than 700 after the change
		    if (source.getValue() > 501) {
		    	MainWindow.this.soundPlayer.setMute(true);
		    	MainWindow.this.muteBtn.setEnabled(false);
		    } else if (MainWindow.this.muteBtn.getText().equals("Mute")) {
		    	//If it's slower than 700, unmute if the mute button is toggled
		    	//to not be muted
		    	MainWindow.this.soundPlayer.setMute(false);
		    	MainWindow.this.muteBtn.setEnabled(true);
		    } else {
		    	MainWindow.this.muteBtn.setEnabled(true);
		    }
		}
	}
	
	/*
	 * Attached to the mute button to allow the sounds of the game to be 
	 * toggled on and off.
	 */
	private class MuteListener implements ActionListener {

		public MuteListener() {
			// TODO Auto-generated constructor stub
		}

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
				MainWindow.this.soundPlayer.setMute(true);
				source.setText("Unmute");
			} else {
				MainWindow.this.soundPlayer.setMute(false);
				source.setText("Mute");
			}
		}
	}
	
	/*
	 * Attached to the show markers button to allow for the chemical markers
	 * to be toggled on and off.
	 */
	private class MarkersListener implements ActionListener {
		
		public MarkersListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Depending on what state the button was currently in, flip the state
		 * of the button, and turn on or off the chemical markers.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			//Works in a very similar way to the mute button listener
			if (source.getText().equals("Markers Off")) {
				MainWindow.this.gameDisplay.setMarkers(false);
				source.setText("Markers On");
			} else {
				MainWindow.this.gameDisplay.setMarkers(true);
				source.setText("Markers Off");
			}
		}
	}
	
	/*
	 * Attached to the finish button to allow the game to be rapidly completed
	 * mid way through.
	 */
	private class FinishListener implements ActionListener {
		
		public FinishListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Sets the game to unlimited speed and sets the game  display to the
		 * state where it will display the processing screen.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
			//Sets the game to the fastest speed (as fast as the CPU can handle)
			MainWindow.this.gameEngine.setSleepDur(0);
			MainWindow.this.speedAdjustmentSlider.setEnabled(false);
			MainWindow.this.muteBtn.setEnabled(false);
			MainWindow.this.gameDisplay.switchState(DisplayStates.PROCESSING);
			//Store whether the game was muted before the game is skipped to
			//the end. Then mute
			if (MainWindow.this.muteBtn.getText().equals("Mute")) {
				MainWindow.this.isMuteBeforeFinish = false;
			} else {
				MainWindow.this.isMuteBeforeFinish = true;
			}
			MainWindow.this.soundPlayer.setMute(true);
		}
	}
}
