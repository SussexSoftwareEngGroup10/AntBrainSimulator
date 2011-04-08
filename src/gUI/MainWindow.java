package gUI;

import javax.swing.*;
import java.awt.*;

/*
 * CURRENTLY AN EXPERIMENTAL CLASS - TESTING ADDING PAPPLET TO A SWING GUI
 */
public class MainWindow {
	/**
	 * Constructs a new GUI and draws it to the screen.
	 */
	public MainWindow()
	{
		drawGUI();
	}
		
	/**
	 * Main method to run the program - simply calls the constructor
	 * to display the GUI.
	 */
	public static void main(String args[]) 
	{
		new MainWindow();
	}
		
	/*
	 * This method is what adds all the swing components to the main
	 * frame and adds listeners to the buttons.
	 */
	private void drawGUI()
	{
		//Set up the main frame with a border layout
		JFrame window = new JFrame("Ant Simulator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800,700);
		Container pane = window.getContentPane();
		pane.setLayout(new FlowLayout());
			
		GameDisplay gridDisplay = new GameDisplay();
		pane.add(gridDisplay);
		gridDisplay.init();
			
		//Centre frame on screen
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
}
