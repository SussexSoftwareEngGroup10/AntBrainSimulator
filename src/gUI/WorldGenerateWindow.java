package gUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class displays the window which allows the user to specify how a world is generated.
 * The user can either pick a contest style mode, or select their own parameters for world
 * generation.
 * 
 * @author Will
 */
public class WorldGenerateWindow {
	MainWindow mainWindow;
	
	JRadioButton contestBtn;
	JRadioButton standardBtn;
	
	JLabel rowsSelectLbl;
	JLabel colsSelectLbl;
	JLabel rocksSelectLbl;
	
	JTextField rowsSelectText;
	JTextField colsSelectText;
	JTextField rocksSelectText;
	
	/**
	 * Constructs a new instance of this window.  This draws the window to the screen.
	 * 
	 * @param mainWindow The main window which is the parent of this.
	 */
	public WorldGenerateWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		
		drawGUI();
	}
	
	/*
	 * Method which draws the GUI elements.
	 */
	private void drawGUI() {
		//Set up the JFrame
		JFrame window = new JFrame("World Generator");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Description panel to display advice to the user
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new FlowLayout());
		
		JLabel descriptionLbl = new JLabel("Choose whether you want to play on a standard tournament world, " +
											 "or if you would like to specify your own world parameters.");
		descriptionPanel.add(descriptionLbl);
		pane.add(descriptionPanel, BorderLayout.NORTH);
		
		//Panel to give radio buttons used for selecting either a contest style world, or a custom world
		JPanel radioBtnPanel = new JPanel();
		radioBtnPanel.setLayout(new FlowLayout());
		
		ButtonGroup radioBtnGroup = new ButtonGroup();
		contestBtn = new JRadioButton("Contest Style World", true);
		contestBtn.addItemListener(new ContestBtnListener());
		standardBtn = new JRadioButton("Custom World", false);
		radioBtnGroup.add(contestBtn);
		radioBtnGroup.add(standardBtn);
		
		radioBtnPanel.add(contestBtn);
		radioBtnPanel.add(standardBtn);
		pane.add(radioBtnPanel, BorderLayout.CENTER);
		
		//Panel to hold the input areas for the custom worl parameter, as well as the generate world button
		JPanel worldParamsAndGeneratePanel = new JPanel();
		worldParamsAndGeneratePanel.setLayout(new BorderLayout());
		
		//The panel holding the parameter inputs
		JPanel worldParametersPanel = new JPanel();
		worldParametersPanel.setLayout(new FlowLayout());
		
		//Panel for selecting number of rows
		JPanel rowsSelectPanel = new JPanel();
		rowsSelectPanel.setLayout(new FlowLayout());
		
		rowsSelectLbl = new JLabel("Number of rows: ");
		rowsSelectLbl.setEnabled(false);
		rowsSelectText = new JTextField(5);
		rowsSelectText.setEnabled(false);
		rowsSelectPanel.add(rowsSelectLbl);
		rowsSelectPanel.add(rowsSelectText);
		
		worldParametersPanel.add(rowsSelectPanel);
		
		//Panel for selecting number of columns
		JPanel colsSelectPanel = new JPanel();
		colsSelectPanel.setLayout(new FlowLayout());
		
		colsSelectLbl = new JLabel("Number of columns: ");
		colsSelectLbl.setEnabled(false);
		colsSelectText = new JTextField(5);
		colsSelectText.setEnabled(false);
		rowsSelectPanel.add(colsSelectLbl);
		rowsSelectPanel.add(colsSelectText);
		
		worldParametersPanel.add(colsSelectPanel);
		
		//Panel for selecting number of rocks
		JPanel rocksSelectPanel = new JPanel();
		rocksSelectPanel.setLayout(new FlowLayout());
		
		rocksSelectLbl = new JLabel("Number of rocks: ");
		rocksSelectLbl.setEnabled(false);
		rocksSelectText = new JTextField(5);
		rocksSelectText.setEnabled(false);
		rocksSelectPanel.add(rocksSelectLbl);
		rocksSelectPanel.add(rocksSelectText);
		
		worldParametersPanel.add(rocksSelectPanel);
		worldParamsAndGeneratePanel.add(worldParametersPanel, BorderLayout.NORTH);
		
		JButton generateBtn = new JButton("Generate!");
		worldParamsAndGeneratePanel.add(generateBtn, BorderLayout.CENTER);
		
		pane.add(worldParamsAndGeneratePanel, BorderLayout.SOUTH);
		
		//set window properties, and draw to screen
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	 
	 /**
	  * Attached to the contest radio button to turn on and off the
	  * parameter selection depending on if it is selected or not.
	  * 
	  * @author Will
	  */
	public class ContestBtnListener implements ItemListener {
		
		/**
		 * Turns on or off the parameter selection text fields and labels
		 * based on the state of the contest button.
		 * 
		 * @param e The triggering event.
		 */
		public void itemStateChanged(ItemEvent e) {
			//If contest button is selected, deselect parameter selection options
			//otherwise, enable them
			if (e.getStateChange() == ItemEvent.SELECTED) {
				rowsSelectLbl.setEnabled(false);
				colsSelectLbl.setEnabled(false);
				rocksSelectLbl.setEnabled(false);
				
				rowsSelectText.setEnabled(false);
				colsSelectText.setEnabled(false);
				rocksSelectText.setEnabled(false);
			}
			else {
				rowsSelectLbl.setEnabled(true);
				colsSelectLbl.setEnabled(true);
				rocksSelectLbl.setEnabled(true);
				
				rowsSelectText.setEnabled(true);
				colsSelectText.setEnabled(true);
				rocksSelectText.setEnabled(true);
			}
		}
	}
	
	/**
	 * Listener to attach to the generate button.
	 * 
	 * @author Will
	 */
	public class GenerateListener implements ActionListener {
		
		/**
		 * Will either generate a contest style world or a custom world 
		 * depending on what is selected.
		 * 
		 * @param e The triggering event.
		 */
		public void actionPerformed(ActionEvent e) {
			if (contestBtn.isSelected()) {
				mainWindow.setupNewContestWorld();
			}
			else {
				try {
					//Try converting parameters to integers
					int rows = Integer.parseInt(rowsSelectText.getText());
					int cols = Integer.parseInt(colsSelectText.getText());
					int rocks = Integer.parseInt(rocksSelectText.getText());
					
					//TODO: Decide on a good maximum and minimum number of rows, cols, rocks.
					mainWindow.setupNewWorldStandardWorld(rows, cols, rocks);
				}
				catch (NumberFormatException nFE) {
					GUIErrorMsg.displayErrorMsg("Parameters must be an integer!");
				}
			}
		}
	}
}
