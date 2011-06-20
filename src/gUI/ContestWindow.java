package gUI;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import antBrain.Brain;
import antBrain.BrainParser;
import engine.GameEngine;
import utilities.IOEvent;
import utilities.IllegalArgumentEvent;

/**
 * This class displays the window that allows the user to set up a contest.  
 * Once the contest has been run, results are displayed.
 * 
 * @author wjs25
 */
public class ContestWindow {
	protected GameEngine gameEngine;
	protected ContestRunner contestRunner;
	protected int numOfPlayers;
	//Holds the file paths of the ant brains
	protected String[] brainPaths;
	
	//Stores arrays of GUI components
	protected JButton[] browseBtns;
	protected JTextField[] brainPathLbls;
	protected JTextField[] winsFields;
	protected JTextField[] lossesFields;
	protected JButton goBtn;
	protected JProgressBar progressBar;
	
	/**
	 * ContestWindow
	 * Constructs a new ContestWindow and draws it to the screen.
	 * 
	 * @param numOfPlayers The number of contest participants.
	 * @param gameEngine The game engine to run the contest with.
	 */
	public ContestWindow(int numOfPlayers, GameEngine gameEngine) {
		this.numOfPlayers = numOfPlayers;
		this.gameEngine = gameEngine;
		this.brainPaths = new String[numOfPlayers];
		drawGUI();
	}
	
	/*
	 * This method draws the elements of the GUI to the screen
	 */
	private void drawGUI() {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Contest Mode");
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Panel to display the grid where each row allows a contestant to
		//upload a brain, and show the wins and losses for that brain
		JPanel brainUploadPanel = new JPanel();
		brainUploadPanel.setLayout(
				new GridLayout(this.numOfPlayers + 1, 5, 10, 10));
		brainUploadPanel.setBorder(new EmptyBorder(0, 10, 0, 10) );
		
		//Labels that are the column headings
		JLabel blankLbl1 = new JLabel("");
		JLabel blankLbl2 = new JLabel("");
		JLabel brainLocLbl = new JLabel("Brain Location");
		JLabel winsLbl = new JLabel("Wins");
		JLabel lossesLbl = new JLabel("Losses");
		
		brainUploadPanel.add(blankLbl1);
		brainUploadPanel.add(brainLocLbl);
		brainUploadPanel.add(blankLbl2);
		brainUploadPanel.add(winsLbl);
		brainUploadPanel.add(lossesLbl);
		
		//Arrays of the components to be added on each row
		JLabel[] nameLbls = new JLabel[this.numOfPlayers];
		this.browseBtns = new JButton[this.numOfPlayers];
		this.brainPathLbls = new JTextField[this.numOfPlayers];
		this.winsFields = new JTextField[this.numOfPlayers];
		this.lossesFields = new JTextField[this.numOfPlayers];
		
		//Loop displays each row for uploading a brain for each player
		for (int i = 0; i < this.numOfPlayers; i++) { 
			nameLbls[i] = new JLabel("Player"  + (i + 1));
			this.browseBtns[i] = new JButton("Browse");
			this.browseBtns[i].addActionListener(new brainBrowseListener());
			this.brainPathLbls[i] = new JTextField();
			this.winsFields[i] = new JTextField("");
			this.winsFields[i].setEnabled(false);
			this.lossesFields[i] = new JTextField("");
			this.lossesFields[i].setEnabled(false);
			
			brainUploadPanel.add(nameLbls[i]);
			brainUploadPanel.add(this.browseBtns[i]);
			brainUploadPanel.add(this.brainPathLbls[i]);
			brainUploadPanel.add(this.winsFields[i]);
			brainUploadPanel.add(this.lossesFields[i]);
		}
		
		JScrollPane scrollPanel = new JScrollPane(brainUploadPanel);
		//Only add scroll bars by limiting the preferred size of the scroll 
		//panel if the number of players is above 7 (the amount that fits into 
		//300 pixels).
		if (this.numOfPlayers > 7) {
			scrollPanel.setPreferredSize(new Dimension(500, 300));
		} else {
			//If there won't be scrolling, remove the border of the scroll panel
			scrollPanel.setBorder(null);
		}
		scrollPanel.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.add(scrollPanel, BorderLayout.NORTH);
		
		this.progressBar = new JProgressBar(0, this.numOfPlayers);
		this.progressBar.setBorder(new EmptyBorder(10, 10, 10, 10) );
		pane.add(this.progressBar, BorderLayout.CENTER);
		
		JPanel goPanel = new JPanel();
		goPanel.setLayout(new FlowLayout());
		this.goBtn = new JButton("Go");
		this.goBtn.addActionListener(new StartContestListener(this, pane));
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new CloseListener());
		goPanel.add(this.goBtn);
		goPanel.add(cancelBtn);
		pane.add(goPanel, BorderLayout.SOUTH);
		
		//Pack the window so that the size varies based on the number of
		//contestants
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	protected void setProgressBarVal(int val) {
		this.progressBar.setValue(val);
	}
	
	protected void notifyContestComplete(Brain[] brains) {
		for (int i = 0; i < brains.length; i++) {
			String wins = Integer.toString(brains[i].getWins());
			String losses = Integer.toString(brains[i].getLosses());
			this.winsFields[i].setText(wins);
			this.lossesFields[i].setText(losses);
			}
		this.goBtn.setEnabled(true);
		for (JButton browseBtn : this.browseBtns) {
			browseBtn.setEnabled(true);
		}
	}
	
	/**
	 * Attached to the buttons which need to bring up a file browser window.
	 * 
	 * @author will
	 */
	public class brainBrowseListener implements ActionListener 
	{
		/**
		 * Displays the file chooser box when the browse button is 
		 * clicked, the file path is displayed in the corresponding text box.
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
					//Validate the file is of the correct format
					if (!path.contains(".ant")) {
						GUIErrorMsg.displayErrorMsg(
								"Invalid file format, .ant file expected.");
					} else {
						//Search for the index of the JButton clicked
						JButton clickedBtn = (JButton) e.getSource();
						int index = 0;
						int i = 0;
						while (i < ContestWindow.this.numOfPlayers 
								&& index == 0) {
							if (ContestWindow.this.browseBtns[i]
							        == clickedBtn) {
								index = i;
							}
							i++;
						}
						//Update to selected path
						ContestWindow.this.brainPathLbls[index].setText(path);
						ContestWindow.this.brainPaths[index] = path;
					}
				}
				
			}
			//If the user does not have permission to access the file
			catch (SecurityException sE) {
				GUIErrorMsg.displayErrorMsg(
						"Security violation with file!");
			}
		}
	}
	
	public class StartContestListener implements ActionListener {
		private ContestWindow contestWindow;
		private Container pane;
		private Brain[] brains;
		
		public StartContestListener(
				ContestWindow contestWindow, Container pane) {
			this.contestWindow = contestWindow;
			this.pane = pane;
		}
		
		@Override
		public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
			//If a contest has already been run on this window, reset the wins
			//and losses text fields
			for (int i = 0; i < ContestWindow.this.numOfPlayers; i++) { 
				ContestWindow.this.winsFields[i].setText("");
				ContestWindow.this.lossesFields[i].setText("");
			}
			
			this.brains = new Brain[ContestWindow.this.numOfPlayers];
			try {
				for (int i = 0; i < ContestWindow.this.numOfPlayers; i++) {
					this.brains[i] = BrainParser.readBrainFrom(
							ContestWindow.this.brainPaths[i]);
				}
				ContestWindow.this.contestRunner = 
						new ContestRunner(ContestWindow.this.gameEngine, 
										  this.brains, this.contestWindow);
				ContestWindow.this.contestRunner.start();
				//Need to disable run button on main window
				ContestWindow.this.goBtn.setEnabled(false);
				
				for (JButton browseBtn : ContestWindow.this.browseBtns) {
					browseBtn.setEnabled(false);
				}
				
				//Show a part of the progress bar so it's clear what it is for
				ContestWindow.this.progressBar.setValue(1);
				
				JOptionPane.showMessageDialog(
						this.pane, "Running contest, please wait.");
			} catch (IOEvent ioE) {
				GUIErrorMsg.displayErrorMsg(
						"An error occured while parsing an ant brain " +
						"file!");
			} catch (IllegalArgumentEvent iAE) {
				GUIErrorMsg.displayErrorMsg(
						"An error occured while parsing an ant brain " +
						"file!");
			}
		}
	}
	
	public class CloseContestListener extends CloseListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Get the button
			JButton triggeringBtn = (JButton) e.getSource();
			//Get the frame the button was on
			JFrame window = (JFrame) SwingUtilities.getRoot(triggeringBtn);
			ContestWindow.this.contestRunner.interrupt();
			window.setVisible(false);
		}
	}
}
