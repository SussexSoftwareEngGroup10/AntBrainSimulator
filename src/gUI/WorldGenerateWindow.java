package gUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import utilities.ErrorEvent;

/**
 * This class displays the window which allows the user to specify how a world 
 * is generated. The user can either pick a contest style mode, or select their 
 * own parameters for world generation.
 * 
 * @author wjs25
 */
public class WorldGenerateWindow {
	MainWindow mainWindow; //The parent window
	
	//Swing components which need to be accessed by the button listeners
	JFrame window;
	JRadioButton contestBtn;
	JRadioButton standardBtn;
	JLabel rowsSelectLbl;
	JLabel colsSelectLbl;
	JLabel rocksSelectLbl;
	JTextField rowsSelectText;
	JTextField colsSelectText;
	JTextField rocksSelectText;
	
	/**
	 * Constructs a new instance of this window.  This draws the window to the 
	 * screen.
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
		window = new JFrame("World Generator");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container pane = window.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//Description panel to display advice to the user
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new FlowLayout());
		
		JLabel descriptionLbl = 
				new JLabel("Choose whether you want to play on a standard " +
						   "tournament world, or if you would like to " +
						   "specify your own world parameters.");
		descriptionPanel.add(descriptionLbl);
		pane.add(descriptionPanel, BorderLayout.NORTH);
		
		//Panel to give radio buttons used for selecting either a contest style
		//world, or a custom world
		JPanel radioBtnPanel = new JPanel();
		radioBtnPanel.setLayout(new FlowLayout());
		
		//Create the radio buttons, and group them
		ButtonGroup radioBtnGroup = new ButtonGroup();
		contestBtn = new JRadioButton("Contest Style World", true);
		contestBtn.addItemListener(new ContestBtnListener());
		standardBtn = new JRadioButton("Custom World", false);
		radioBtnGroup.add(contestBtn);
		radioBtnGroup.add(standardBtn);
		//Add the radio buttons to the JPanel, and then the window
		radioBtnPanel.add(contestBtn);
		radioBtnPanel.add(standardBtn);
		pane.add(radioBtnPanel, BorderLayout.CENTER);
		
		//Panel to hold the input areas for the custom worl parameter, as well 
		//as the generate world button
		JPanel worldParamsAndGeneratePanel = new JPanel();
		worldParamsAndGeneratePanel.setLayout(new BorderLayout());
		
		//The panel holding the parameter inputs
		JPanel worldParametersPanel = new JPanel();
		worldParametersPanel.setLayout(new FlowLayout());
		
		//Panel for selecting number of rows, made up of a descriptive label,
		//and a text field for the user to enter the desired value
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
		worldParamsAndGeneratePanel.add(
					worldParametersPanel, BorderLayout.NORTH);
		//Panel to display the two buttons, to generate the world or cancel, at
		//the bottom of the window
		JPanel generatePanel = new JPanel();
		generatePanel.setLayout(new FlowLayout());
		JButton generateBtn = new JButton("Generate");
		generateBtn.addActionListener(new GenerateListener());
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new CloseListener());
		generatePanel.add(generateBtn);
		generatePanel.add(cancelBtn);
		worldParamsAndGeneratePanel.add(generatePanel, BorderLayout.CENTER);
		
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
	  * @author wjs25
	  */
	public class ContestBtnListener implements ItemListener {
		
		/**
		 * Turns on or off the parameter selection text fields and labels
		 * based on the state of the contest button.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			//If contest button is selected, deselect parameter selection 
			//options otherwise, enable them
			if (e.getStateChange() == ItemEvent.SELECTED) {
				rowsSelectLbl.setEnabled(false);
				colsSelectLbl.setEnabled(false);
				rocksSelectLbl.setEnabled(false);
				
				rowsSelectText.setEnabled(false);
				colsSelectText.setEnabled(false);
				rocksSelectText.setEnabled(false);
			} else {
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
	 * @author wjs25
	 */
	public class GenerateListener implements ActionListener {
		
		/**
		 * Will either generate a contest style world or a custom world 
		 * depending on what is selected.
		 * 
		 * @param e The triggering event.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (contestBtn.isSelected()) {
				try {
					mainWindow.setupNewContestWorld();
					window.setVisible(false);
				} catch (ErrorEvent eE) {
					GUIErrorMsg.displayErrorMsg(
							"Error in generating contest world!");
				}
			} else {
				try {
					//Try converting parameters to integers
					int rows = Integer.parseInt(rowsSelectText.getText());
					int cols = Integer.parseInt(colsSelectText.getText());
					int rocks = Integer.parseInt(rocksSelectText.getText());
					
					//Validate user input complies to this games rules
					if (rows > 140 || cols > 140) {
						GUIErrorMsg.displayErrorMsg("World dimensions must " +
													"not exceed 140!");
					} else if (rows < 1 || cols < 1 || rocks < 1) {
						GUIErrorMsg.displayErrorMsg("Values cannot be " +
													"negative!");
					} else {
						try {
							mainWindow.setupNewWorldStandardWorld(
									rows, cols, rocks);
							window.setVisible(false);
						} catch (ErrorEvent eE) {
							GUIErrorMsg.displayErrorMsg(
									"World is not a legal standard world!");
						}
					}
				} catch (NumberFormatException nFE) {
					GUIErrorMsg.displayErrorMsg(
							"Parameters must be an integer!");
				}
			}
		}
	}
}
