package gUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import engine.GameStats;

/**
 * Represents a window that displays the statistics from a regular game once
 * it is finished.
 * 
 * @author wjs25
 */
public class StatisticsWindow {
	
	/**
	 * Constructor for StatisticsWindow, draws the window to the screen.
	 * 
	 * @param stats The statistics from the run game.
	 */
	public StatisticsWindow(GameStats stats) {
		drawGUI(stats);
	}
	
	/*
	 * Method for drawing the GUI to the screen.
	 */
	private void drawGUI(GameStats stats) {
		//Set up the window frame and get the content pane
		JFrame window = new JFrame("Statistics");
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Add a panel displaying the title at the top of the pain
		JLabel heading = new JLabel("Simulation Statistics");
		heading.setFont(new Font("Dialog", 1, 14));
		heading.setBorder(new EmptyBorder(10, 10, 0, 0) );
		pane.add(heading, BorderLayout.NORTH);
		
		//Panel holding the area the statistics are written to
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new GridLayout(3, 3));
		statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		//These labels display the titles for the columns
		JLabel brainLbl = new JLabel("Brain");
		JLabel foodLbl = new JLabel("Food Collected");
		JLabel survivingLbl = new JLabel("Ants Surviving");
		
		//Text fields to display the stats in
		JTextField winnerBrain;
		JTextField winnerFood;
		JTextField winnerSurvivors;
		JTextField loserBrain;
		JTextField loserFood;
		JTextField loserSurvivors;
		
		//If black ant wins, draw the stats as it being the winner
		if (stats.getWinner() == 0) {
			//Get the stats from the stats class, and draw to text fields
			winnerBrain = new JTextField("Black Brain (Winner)");
			winnerFood = 
				new JTextField(Integer.toString(stats.getFoodInBlackAnthill()));
			winnerSurvivors = 
				new JTextField(Integer.toString(stats.getBlackAntsSurviving()));
			loserBrain = new JTextField("Red Brain (Loser)");
			loserFood = 
				new JTextField(Integer.toString(stats.getFoodInRedAnthill()));
			loserSurvivors = 
				new JTextField(Integer.toString(stats.getRedAntsSurviving()));
		} else if (stats.getWinner() == 1) { 
			//Else draw stats as red brain as the winner
			winnerBrain = new JTextField("Red Brain (Winner)");
			winnerFood = 
				new JTextField(Integer.toString(stats.getFoodInRedAnthill()));
			winnerSurvivors = 
				new JTextField(Integer.toString(stats.getRedAntsSurviving()));
			loserBrain = new JTextField("Black Brain (Loser)");
			loserFood = 
				new JTextField(Integer.toString(stats.getFoodInBlackAnthill()));
			loserSurvivors = 
				new JTextField(Integer.toString(stats.getBlackAntsSurviving()));
		} else { //It was a draw
			winnerBrain = new JTextField("Black Brain (Draw)");
			winnerFood = 
				new JTextField(Integer.toString(stats.getFoodInBlackAnthill()));
			winnerSurvivors = 
				new JTextField(Integer.toString(stats.getBlackAntsSurviving()));
			loserBrain = new JTextField("Red Brain (Draw)");
			loserFood = 
				new JTextField(Integer.toString(stats.getFoodInRedAnthill()));
			loserSurvivors = 
				new JTextField(Integer.toString(stats.getRedAntsSurviving()));
		}
		
		//Made the field un editable
		winnerBrain.setEditable(false);
		winnerFood.setEditable(false);
		winnerSurvivors.setEditable(false);
		loserBrain.setEditable(false);
		loserFood.setEditable(false);
		loserSurvivors.setEditable(false);
		
		//Add headings
		statsPanel.add(brainLbl);
		statsPanel.add(foodLbl);
		statsPanel.add(survivingLbl);
		
		//Add data
		statsPanel.add(winnerBrain);
		statsPanel.add(winnerFood);
		statsPanel.add(winnerSurvivors);
		statsPanel.add(loserBrain);
		statsPanel.add(loserFood);
		statsPanel.add(loserSurvivors);
		
		pane.add(statsPanel, BorderLayout.CENTER);
		
		//Add a close button at the bottom of the window
		JPanel closePanel = new JPanel();
		closePanel.setLayout(new FlowLayout());
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new CloseListener());
		closePanel.add(closeButton);
		pane.add(closePanel, BorderLayout.SOUTH);
		
		//Set final window properties
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
