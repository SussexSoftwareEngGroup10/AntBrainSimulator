package gUI;

import java.awt.*;
import javax.swing.*;

/**
 * This class displays the window that allows the user to set up a contest.
 * Once the contest has been run, results are displayed.
 * 
 * @author will
 */
public class ContestWindow {
	
	/**
	 * Constructs a new ContestWindow and draws it to the screen.
	 * 
	 * @param numOfPlayers The number of contest participants.
	 */
	public ContestWindow(int numOfPlayers) {
		drawGUI(numOfPlayers);
	}
	
	/*
	 * This method draws the elements of the GUI to the screen
	 */
	private void drawGUI(int numOfPlayers) {
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Contest Mode");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Panel to display the grid where each row allows a contestant to
		//upload a brain, and show the wins and losses for that brain
		JPanel brainUploadPanel = new JPanel();
		brainUploadPanel.setLayout(new GridLayout(numOfPlayers + 1, 4, 10, 10));
		
		//Labels that are the column headings
		JLabel blankLbl = new JLabel("");
		JLabel brainLocLbl = new JLabel("Brain Location");
		JLabel winsLbl = new JLabel("Wins");
		JLabel lossesLbl = new JLabel("Losses");
		
		brainUploadPanel.add(blankLbl);
		brainUploadPanel.add(brainLocLbl);
		brainUploadPanel.add(winsLbl);
		brainUploadPanel.add(lossesLbl);
		
		//Arrays of the components to be added on each row
		JLabel[] nameLbls = new JLabel[numOfPlayers];
		JTextArea[] brainPathLbls = new JTextArea[numOfPlayers];
		JLabel[] winsLbls = new JLabel[numOfPlayers];
		JLabel[] lossesLbls = new JLabel[numOfPlayers];
		
		//Loop displays each row for uploading a brain for each player
		for (int i = 0; i < numOfPlayers; i++) { 
			nameLbls[i] = new JLabel("Player"  + (i + 1));
			brainPathLbls[i] = new JTextArea();
			winsLbls[i] = new JLabel("");
			lossesLbls[i] = new JLabel("");
			
			brainUploadPanel.add(nameLbls[i]);
			brainUploadPanel.add(brainPathLbls[i]);
			brainUploadPanel.add(winsLbls[i]);
			brainUploadPanel.add(lossesLbls[i]);
		}
		
		pane.add(brainUploadPanel, BorderLayout.NORTH);
		
		//Panel to add button to display statistics and cancel at the bottom
		JPanel showStatsPanel = new JPanel();
		showStatsPanel.setLayout(new FlowLayout());
		JButton showStats = new JButton("Display Statistics");
		showStats.setEnabled(false);
		showStatsPanel.add(showStats);
		pane.add(showStatsPanel, BorderLayout.CENTER);
		
		JPanel goPanel = new JPanel();
		goPanel.setLayout(new FlowLayout());
		JButton cancelBtn = new JButton("Cancel");
		JButton goBtn = new JButton("Go");
		goPanel.add(cancelBtn);
		goPanel.add(goBtn);
		pane.add(goPanel, BorderLayout.SOUTH);
		
		//Pack the window so that the size varies based on the number of
		//contestants
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
