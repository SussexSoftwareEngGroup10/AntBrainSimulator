package gUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import engine.GameStats;

public class StatisticsWindow {
	
	public StatisticsWindow(GameStats stats) {
		drawGUI(stats);
	}
	
	private void drawGUI(GameStats stats) {
		JFrame window = new JFrame("Statistics");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		JLabel heading = new JLabel("Simulation Statistics");
		heading.setFont(new Font("Dialog", 1, 14));
		heading.setBorder(new EmptyBorder(10, 10, 0, 0) );
		pane.add(heading, BorderLayout.NORTH);
		
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new GridLayout(3, 3));
		statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		JLabel brainLbl = new JLabel("Brain");
		JLabel foodLbl = new JLabel("Food Collected");
		JLabel survivingLbl = new JLabel("Ants Surviving");
		
		JTextField winnerBrain;
		JTextField winnerFood;
		JTextField winnerSurvivors;
		JTextField loserBrain;
		JTextField loserFood;
		JTextField loserSurvivors;
		
		//If black ant wins, draw the stats as it being the winner
		if (stats.getWinner() == 0) {
			winnerBrain = new JTextField("Black Brain (Winner)");
			winnerFood = 
				new JTextField(Integer.toString(stats.getFoodInBlackAnthil()));
			winnerSurvivors = 
				new JTextField(Integer.toString(stats.getBlackAntsSurviving()));
			loserBrain = new JTextField("Red Brain (Loser)");
			loserFood = 
				new JTextField(Integer.toString(stats.getFoodInRedAnthill()));
			loserSurvivors = 
				new JTextField(Integer.toString(stats.getRedAntsSurvivig()));
		} else { //Else draw stats as red brain as the winner
			winnerBrain = new JTextField("Red Brain (Winner)");
			winnerFood = 
				new JTextField(Integer.toString(stats.getFoodInRedAnthill()));
			winnerSurvivors = 
				new JTextField(Integer.toString(stats.getRedAntsSurvivig()));
			loserBrain = new JTextField("Black Brain (Loser)");
			loserFood = 
				new JTextField(Integer.toString(stats.getFoodInBlackAnthil()));
			loserSurvivors = 
				new JTextField(Integer.toString(stats.getBlackAntsSurviving()));
		}
		
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
		
		//TODO: Add close ("OK") button
		JPanel closePanel = new JPanel();
		closePanel.setLayout(new FlowLayout());
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new CloseListener());
		closePanel.add(closeButton);
		pane.add(closePanel, BorderLayout.SOUTH);
		
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
