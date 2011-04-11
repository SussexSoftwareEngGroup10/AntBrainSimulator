package gUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import utilities.*;

public class MainWindow {
	ContestWindow contestWindow;
	StatisticsWindow statisticsWindow;
	
	String blackBrainPath;
	String redBrainPath;
	String worldPath;
	
	JButton startGameBtn;
	JButton uploadRedBtn;
	JButton uploadBlackBtn;
	JButton uploadWorldBtn;
	
	/**
	 * Constructs a new GUI and draws it to the screen.
	 */
	public MainWindow() {
		drawGUI();
	}
		
	/**
	 * Main method to run the program - simply calls the constructor
	 * to display the GUI.
	 */
	public static void main(String args[]) {
		new MainWindow();
	}
		
	/*
	 * This method is what adds all the swing components to the main
	 * frame and adds listeners to the buttons.
	 */
	private void drawGUI() {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Ant Brain Simulator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800,700);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		JPanel gridDisplayPanel = new JPanel();
		gridDisplayPanel.setLayout(new FlowLayout());
		
		GameDisplay gridDisplay = new GameDisplay();
		gridDisplayPanel.add(gridDisplay);
		gridDisplay.init();
		
		pane.add(gridDisplayPanel, BorderLayout.NORTH);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 3));
		
		startGameBtn = new JButton("Start Game");
		startGameBtn.setEnabled(false);
		JButton contestBtn = new JButton("Contest Mode");
		contestBtn.addActionListener(new contestListener());
		uploadRedBtn = new JButton("Upload Red Brain");
		uploadRedBtn.addActionListener(new fileBrowseListener());
		uploadBlackBtn = new JButton("Upload Black Brain");
		uploadBlackBtn.addActionListener(new fileBrowseListener());
		uploadWorldBtn = new JButton("Upload World");
		uploadWorldBtn.addActionListener(new fileBrowseListener());
		JButton genWorldBtn = new JButton("Generate World");
		
		controlPanel.add(startGameBtn);
		controlPanel.add(uploadRedBtn);
		controlPanel.add(uploadWorldBtn);
		controlPanel.add(contestBtn);
		controlPanel.add(uploadBlackBtn);
		controlPanel.add(genWorldBtn);
		
		pane.add(controlPanel, BorderLayout.CENTER);
		
		//Centre frame on screen
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/**
	 * Called when game is complete.
	 */
	public void notifyGameComplete(String winner) {
		//TODO: Untested
		//Reset upload buttons to original text without ticks
		uploadRedBtn.setText("Upload Red Brain");
		uploadBlackBtn.setText("Upload Black Brain");
		uploadWorldBtn.setText("Upload World");
		
		//Flush stored file paths
		redBrainPath = "";
		blackBrainPath = "";
		worldPath = "";
		
		startGameBtn.setEnabled(true);
		
		//Custom button text
		String[] options = {"Statistics", "OK"};
		int choice = JOptionPane.showOptionDialog(null, winner + " wins!", "And The Winner Is...", 
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
		if (choice == 0) {
			//Show StatisticsWindow
		}
	}
	
	public class fileBrowseListener implements ActionListener 
	{
		/**
		 * Displays the file chooser box when the browse button is 
		 * clicked, and displays the selected path in the text box.
		 */
		public void actionPerformed(ActionEvent e)
		{
			//set the initial directory to the current project dir
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			try {
				//show the dialog
				fc.showOpenDialog(null);
				String path = "";
				//get the path from the selected file if one was selected
				File f = fc.getSelectedFile();
				if (f != null) {
					path = f.getAbsolutePath();
				}
				//TODO: Validate what is return by the file chooser.  Maybe it was closed?  Or nothing selected?
				JButton clickedBtn = (JButton) e.getSource();
				
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
			//if the user does not have permission to access the file
			catch (SecurityException sE) {
				if (Logger.getLogLevel() >= 1) {
					Logger.log(new IOWarningEvent("Security violation with file!", sE));
				}
			}
		}
	}
	
	class contestListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String stringNumberOfPlayers = (String)JOptionPane.showInputDialog(null, "Select number of players:", "Player Selector", JOptionPane.QUESTION_MESSAGE);
			//Validate it's an int | Show ContestWindow
			
			int numberOfPlayers;
			try {
				numberOfPlayers = Integer.parseInt(stringNumberOfPlayers);
				contestWindow = new ContestWindow(numberOfPlayers);
			}
			catch (NumberFormatException nFE){
				if (Logger.getLogLevel() >= 1) {
					Logger.log(new InvalidInputWarningEvent("Input number of players not an integer!", nFE));
				}
			}			
		}
	}
}
