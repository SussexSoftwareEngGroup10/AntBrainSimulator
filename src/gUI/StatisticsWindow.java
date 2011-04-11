package gUI;

import java.awt.*;
import javax.swing.*;

public class StatisticsWindow {
	
	public StatisticsWindow(int[][] stats) {
		drawGUI(stats);
	}
	
	public static void main(String args[]) {
		//new StatisticsWindow(5);
	}
	
	private void drawGUI(int[][] stats) {
		JFrame window = new JFrame("Contest Mode");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800,700);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		JPanel brainUploadPanel = new JPanel();
		brainUploadPanel.setLayout(new GridLayout(stats.length + 1, 4));
		
		JLabel blankLbl = new JLabel("");
		JLabel killsLbl = new JLabel("Kills");
		JLabel deathsLbl = new JLabel("Deaths");
		JLabel foodLbl = new JLabel("Total Food Collected");
		
		brainUploadPanel.add(blankLbl);
		brainUploadPanel.add(killsLbl);
		brainUploadPanel.add(deathsLbl);
		brainUploadPanel.add(foodLbl);
		
		pane.add(brainUploadPanel, BorderLayout.NORTH);
		
		for (int i = 0; i < stats.length; i++) { // Display a row for uploading a brain for each player
			
		}
		
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
