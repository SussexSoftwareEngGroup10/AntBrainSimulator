package gUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import utilities.*;
import engine.DummyEngine;
import antWorld.World;

/**
 * This class displays the main window GUI.  It display a window with the main
 * game display as well as buttons along the bottom.
 * 
 * @author will
 */
public class MainWindow {
	DummyEngine gameEngine;
	
	//The other GUI components used
	GameDisplay gameDisplay;
	ContestWindow contestWindow;
	StatisticsWindow statisticsWindow;
	
	//The paths to the ant and world files
	String blackBrainPath;
	String redBrainPath;
	String worldPath;
	
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
		gameEngine = new DummyEngine();
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
		window.setSize(800,700);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Set up JPanel at the top to hold the game display area
		JPanel gridDisplayPanel = new JPanel();
		gridDisplayPanel.setLayout(new FlowLayout());
		
		gameDisplay = new GameDisplay();
		gridDisplayPanel.add(gameDisplay);
		gameDisplay.init();
		pane.add(gridDisplayPanel, BorderLayout.NORTH);
		
		//Set up JPanel at the bottom to display the control buttons
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 3));
		
		startGameBtn = new JButton("Start Game");
		startGameBtn.setEnabled(false);
		contestBtn = new JButton("Contest Mode");
		contestBtn.addActionListener(new contestListener());
		uploadRedBtn = new JButton("Upload Red Brain");
		uploadRedBtn.addActionListener(new fileBrowseListener());
		uploadBlackBtn = new JButton("Upload Black Brain");
		uploadBlackBtn.addActionListener(new fileBrowseListener());
		uploadWorldBtn = new JButton("Upload World");
		uploadWorldBtn.addActionListener(new fileBrowseListener());
		genWorldBtn = new JButton("Generate World");
		
		controlPanel.add(startGameBtn);
		controlPanel.add(uploadRedBtn);
		controlPanel.add(uploadWorldBtn);
		controlPanel.add(contestBtn);
		controlPanel.add(uploadBlackBtn);
		controlPanel.add(genWorldBtn);
		
		pane.add(controlPanel, BorderLayout.CENTER);
		
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
	
	/**
	 * Attached to the buttons which need to bring up a file browser window.
	 * 
	 * @author will
	 */
	public class fileBrowseListener implements ActionListener 
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
							redBrainPath = path;
						}
						else if (clickedBtn == uploadBlackBtn) {
							if (!clickedBtn.getText().contains("✔")) {
								clickedBtn.setText(clickedBtn.getText() + " ✔");
							}
							blackBrainPath = path;
						}
						else {
							if (!clickedBtn.getText().contains("✔")) {
								clickedBtn.setText(clickedBtn.getText() + " ✔");
							}
							worldPath = path;
						}
					}
				}
				
			}
			//If the user does not have permission to access the file
			catch (SecurityException sE) {
				if (Logger.getLogLevel() >= 1) {
					Logger.log(new IOEvent(
							"Security violation with file!", sE));
				}
			}
		}
	}
	
	/**
	 * Attached to the button for initiating contest mode.
	 * 
	 * @author will
	 */
	public class contestListener implements ActionListener {
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
				if (Logger.getLogLevel() >= 1) {
					Logger.log(new InvalidInputEvent(
							"Input number of players not an integer!", nFE));
				}
			}			
		}
	}
	
	/**
	 * Attached to the button for generating a world.
	 * 
	 * @author will
	 */
	public class worldGenListener implements ActionListener {
		/**
		 * Generates a new random world and displays it.
		 * 
		 * @param e The triggering event.
		 */
		public void actionPerformed(ActionEvent e) {
			World generatedWorld = gameEngine.generateWorld();
			gameDisplay.displayNewWorld(generatedWorld);
		}
	}
}
