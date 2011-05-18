package gUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import utilities.*;
import engine.GameEngine;
import antBrain.Brain;
import antBrain.BrainParser;
import antWorld.World;
import antWorld.WorldParser;

/**
 * This class displays the main window GUI.  It display a window with the main
 * game display as well as buttons along the bottom.
 * 
 * @author will
 */
public class MainWindow {
	private static final int WINDOW_WIDTH = 750;
	private static final int WINDOW_HEIGHT = 850;
	
	GameEngine gameEngine;
	World world;
	
	Brain blackBrain;
	Brain redBrain;
	
	//The other GUI components used
	GameDisplay gameDisplay;
	ContestWindow contestWindow;
	StatisticsWindow statisticsWindow;
	
	//The paths to the ant and world files
	String blackBrainPath = "";
	String redBrainPath = "";
	String worldPath = "";
	
	//The control buttons to be display at the bottom of the window
	JButton startGameBtn;
	JButton contestBtn;
	JButton uploadRedBtn;
	JButton uploadBlackBtn;
	JButton uploadWorldBtn;
	JButton genWorldBtn;
	
	/**
	 * Constructs a new MainWindow and draws it to the screen.
	 */
	public MainWindow() {
		world = World.getContestWorld(0);
		gameEngine = new GameEngine(world);
		drawGUI();
	}
		
	/**
	 * Main method to run the program - simply calls the constructor
	 * to display the GUI.
	 * 
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		new MainWindow();
	}
		
	/*
	 * This method is what adds all the swing components to the main frame and 
	 * adds listeners to the buttons.
	 */
	private void drawGUI() {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Ant Brain Simulator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Set up JPanel at the top to hold the game display area
		JPanel gridDisplayPanel = new JPanel();
		gridDisplayPanel.setLayout(new FlowLayout());
		
		gameDisplay = new GameDisplay(world);
		gridDisplayPanel.add(gameDisplay);
		gameDisplay.init();
		pane.add(gridDisplayPanel, BorderLayout.NORTH);
		
		//Set up JPanel to display the speed adjustment slider
		JPanel speedAdjustmentPanel = new JPanel();
		JSlider speedAdjustmentSlider = new JSlider();
		//Add a border, and tick marks to slider
		speedAdjustmentSlider.setBorder(BorderFactory.createTitledBorder("Speed Adjustment Slider"));
		speedAdjustmentSlider.setMajorTickSpacing(20);
		speedAdjustmentSlider.setEnabled(false);
		speedAdjustmentPanel.add(speedAdjustmentSlider);
		pane.add(speedAdjustmentPanel);
		
		//Set up JPanel at the bottom to display the control buttons
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 3));
		
		startGameBtn = new JButton("Start Game");
		startGameBtn.addActionListener(new StartGameListener());
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
		
		controlPanel.add(startGameBtn);
		controlPanel.add(uploadRedBtn);
		controlPanel.add(uploadWorldBtn);
		controlPanel.add(contestBtn);
		controlPanel.add(uploadBlackBtn);
		controlPanel.add(genWorldBtn);
		
		pane.add(controlPanel, BorderLayout.SOUTH);
		
		//Centre frame on screen and set visable
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/**
	 * To be called when the game is complete.  Modifies window to initial
	 * configuration as well as providing the user with the option of viewing
	 * statistics.
	 * 
	 * @param winner The winner of the game.
	 */
	public void notifyGameComplete(String winner) {
		//TODO: Untested, Should it really be parsed a string?
		//Reset upload buttons to original text without ticks
		uploadRedBtn.setText("Upload Red Brain");
		uploadBlackBtn.setText("Upload Black Brain");
		uploadWorldBtn.setText("Upload World");
		
		//Flush stored file paths
		redBrainPath = "";
		blackBrainPath = "";
		worldPath = "";
		
		startGameBtn.setEnabled(true);
		
		//Display dialog box asking if statistics should be displayed
		String[] options = {"Statistics", "OK"};
		int choice = JOptionPane.showOptionDialog(null, winner + " wins!",
				"And The Winner Is...", JOptionPane.YES_NO_OPTION, 
				JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
		if (choice == 0) {
			//TODO: Show StatisticsWindow
		}
	}
	
	public void setupNewWorldStandardWorld(int rows, int cols, int rocks) {
		world = World.getRegularWorld(0, rows, cols, rocks);
		gameDisplay.updateWorld(world);
	}
	
	public void setupNewContestWorld() {
		world = World.getContestWorld(0);
		gameDisplay.updateWorld(world);
	}
	
	/**
	 * Attached to the buttons which need to bring up a file browser window.
	 * 
	 * @author will
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
					}
					else if (clickedBtn == uploadWorldBtn &&
							!path.contains(".world")) {
							GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .world file expected.");
					}
					else {
						//Depending on which button triggered the event, a tick 
						//symbol is displayed on that button, and the associated 
						//file path is updated with the file chosen
						if (clickedBtn == uploadRedBtn) {
							if (!clickedBtn.getText().contains("✔")) {
								clickedBtn.setText(clickedBtn.getText() + " ✔");
							}
							redBrain = BrainParser.readBrainFrom(path);
						}
						else if (clickedBtn == uploadBlackBtn) {
							if (!clickedBtn.getText().contains("✔")) {
								clickedBtn.setText(clickedBtn.getText() + " ✔");
							}
							blackBrain = BrainParser.readBrainFrom(path);
						}
						else {
							world = WorldParser.readWorldFrom(path);
							gameDisplay.updateWorld(world);
						}
					}
				}
				
			}
			//If the user does not have permission to access the file
			catch (SecurityException sE) {
				Logger.log(new IOEvent(
						"Security violation with file!", sE));
			}
			//If all files have been selected, allow game to be played.
			if (!(blackBrain == null) && !(redBrainPath == null)) {
				startGameBtn.setEnabled(true);
			}
		}
	}
	
	/**
	 * Attached to the button for initiating contest mode.
	 * 
	 * @author will
	 */
	public class ContestListener implements ActionListener {
		/**
		 * Brings up a dialog box to select the amount of contest participants
		 * and then builds the contest window based on the amount of 
		 * participants entered.
		 * 
		 * @param e The triggering event.
		 */
		public void actionPerformed(ActionEvent e) {
			//Display a dialog box where the user inputs the number of players
			String stringNumberOfPlayers = (String) JOptionPane.showInputDialog(
					null, "Select number of players:", "Player Selector", 
					JOptionPane.QUESTION_MESSAGE);
			//Validate it's an int
			int numberOfPlayers;
			try {
				numberOfPlayers = Integer.parseInt(stringNumberOfPlayers);
				//Display contest window
				contestWindow = new ContestWindow(numberOfPlayers);
			}
			catch (NumberFormatException nFE){
				Logger.log(new IllegalArgumentEvent(
					"Input number of players not an integer!", nFE));
			}			
		}
	}
	
	/**
	 * Attached to the button for generating a world.
	 * 
	 * @author will
	 */
	public class WorldGenListener implements ActionListener {
		MainWindow mainWindow;
		/**
		 * Generates a new random world and displays it.
		 * 
		 * @param e The triggering event.
		 */
		public WorldGenListener(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		public void actionPerformed(ActionEvent e) {
			new WorldGenerateWindow(mainWindow);
		}
	}
	
	public class StartGameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gameEngine.setWorld(world);
			gameEngine.simulate(blackBrain, redBrain);
		}
	}
}
