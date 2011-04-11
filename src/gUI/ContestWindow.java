package gUI;

import java.awt.*;
import javax.swing.*;

public class ContestWindow {
	
	public ContestWindow(int numOfPlayers) {
		drawGUI(numOfPlayers);
	}
	
	private void drawGUI(int numOfPlayers) {
		JFrame window = new JFrame("Contest Mode");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		JPanel brainUploadPanel = new JPanel();
		brainUploadPanel.setLayout(new GridLayout(numOfPlayers + 1, 4, 10, 10));
		
		JLabel blankLbl = new JLabel("");
		JLabel brainLocLbl = new JLabel("Brain Location");
		JLabel winsLbl = new JLabel("Wins");
		JLabel lossesLbl = new JLabel("Losses");
		
		brainUploadPanel.add(blankLbl);
		brainUploadPanel.add(brainLocLbl);
		brainUploadPanel.add(winsLbl);
		brainUploadPanel.add(lossesLbl);
		
		JLabel[] nameLbls = new JLabel[numOfPlayers];
		JTextArea[] brainPathLbls = new JTextArea[numOfPlayers];
		JLabel[] winsLbls = new JLabel[numOfPlayers];
		JLabel[] lossesLbls = new JLabel[numOfPlayers];
		
		for (int i = 0; i < numOfPlayers; i++) { // Display a row for uploading a brain for each player
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
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
